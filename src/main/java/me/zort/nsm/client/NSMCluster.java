package me.zort.nsm.client;

import com.google.gson.Gson;
import me.zort.nsm.client.repository.NSMNodeRepository;
import me.zort.nsm.client.response.NodeStatusResponse;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.Row;
import me.zort.sqllib.pool.SQLConnectionPool;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public final class NSMCluster {

    private final Gson gson = new Gson();
    private final Map<String, NSMNodeRepository> repos = new HashMap<>();
    private final Set<String> unavailable = new HashSet<>();
    private final SQLConnectionPool sql; // Nullable
    private final Logger logger;

    NSMCluster(SQLConnectionPool pool, Logger logger) {
        this.sql = pool;
        this.logger = logger;
    }

    public void addNode(String baseUrl) {
        NSMNodeRepository repo = createRepository(baseUrl);
        try {
            Response<NodeStatusResponse> res = repo.status().execute();
            try {
                if (!res.isSuccessful() || res.body() == null) {
                    throw new RuntimeException();
                }
                repos.put(res.body().getNodeId(), repo);
            } catch (Exception e) {
                logger.severe("Failed to fetch info about node " + baseUrl + ", will be re-fetched later.");
                logger.severe("Status code: " + res.code());
                unavailable.add(baseUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void revalidateUnavailable() {
        for (String u : unavailable) {
            addNode(u);
        }
    }

    public NSMNodeRepository repoByName(String name) {
        return repos.get(name);
    }

    public NSMNodeRepository repoByService(String serviceId) {
        requireBaseAmounts();
        if (repos.size() > 1) {
            try (SQLDatabaseConnection conn = sql.getResource()) {
                String nodeId = conn.select("nodeId")
                        .from("Service")
                        .where().isEqual("serviceId", serviceId)
                        .obtainOne().orElseThrow(() -> new RuntimeException("Service not found!"))
                        .getString("nodeId");
                if (!repos.containsKey(nodeId)) {
                    throw new RuntimeException("Node not found!");
                }
                return repos.get(nodeId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return getOnly();
        }
    }

    public NSMNodeRepository repoBalanced() {
        requireBaseAmounts();
        if (repos.size() > 1) {
            Map<String, Integer> sessionCounts = new HashMap<>();
            try (SQLDatabaseConnection conn = sql.getResource()) {
                for (Row row : conn.query("SELECT nodeId, COUNT(serviceId) AS serviceCount FROM Session GROUP BY nodeId;")) {
                    sessionCounts.put(row.getString("nodeId"), row.getInt("serviceCount"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String node = null;
            int min = Integer.MAX_VALUE;
            for (String nId : sessionCounts.keySet()) {
                if (sessionCounts.get(nId) < min && repos.containsKey(nId)) {
                    node = nId;
                    min = sessionCounts.get(nId);
                }
            }
            if (node == null) {
                // Random node from repos
                if (repos.isEmpty()) {
                    throw new RuntimeException("No nodes are available!");
                }
                int randIndex = ThreadLocalRandom.current().nextInt(repos.size());
                repos.get(repos.keySet().toArray(new String[0])[randIndex]);
            }
            return repos.get(node);
        } else {
            return getOnly();
        }
    }

    public Map<String, NSMNodeRepository> getRepos() {
        return new HashMap<>(repos);
    }

    private NSMNodeRepository getOnly() {
        return repos.values().iterator().next();
    }

    private NSMNodeRepository createRepository(String baseUrl) {
        Retrofit r = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return r.create(NSMNodeRepository.class);
    }

    private void requireBaseAmounts() {
        revalidateUnavailable();

        if (repos.size() > 1 && sql == null) {
            throw new RuntimeException("Multiple base urls are supported only if a cluster database is provided!");
        } else if (repos.isEmpty()) {
            throw new RuntimeException("No nodes are available!");
        }
    }
}
