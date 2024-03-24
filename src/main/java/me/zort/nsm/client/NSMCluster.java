package me.zort.nsm.client;

import com.google.gson.Gson;
import me.zort.sqllib.pool.SQLConnectionPool;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;

public final class NSMCluster {

    private final Gson gson = new Gson();
    private final Map<String, NSMNodeRepository> repos = new HashMap<>();
    private final SQLConnectionPool sql; // Nullable

    NSMCluster(SQLConnectionPool pool) {
        this.sql = pool;
    }

    public void addNode(String name, String baseUrl) {
        repos.put(name, createRepository(baseUrl));
    }

    public NSMNodeRepository repoByName(String name) {
        return repos.get(name);
    }

    public NSMNodeRepository repoByService(String serviceId) {
        requireOnlyBaseOrSql();
        if (repos.size() > 1) {
            // TODO
        } else {
            return getOnly();
        }
    }

    public NSMNodeRepository repoBalanced() {
        requireOnlyBaseOrSql();
        if (repos.size() > 1) {
            // TODO
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

    private void requireOnlyBaseOrSql() {
        if (repos.size() > 1 && sql == null) {
            throw new RuntimeException("Multiple base urls are supported only if a cluster database is provided!");
        }
    }
}
