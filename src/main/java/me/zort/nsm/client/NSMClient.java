package me.zort.nsm.client;

import me.zort.nsm.client.exception.NSMResponseException;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.ServiceListResponse;
import me.zort.sqllib.SQLConnectionBuilder;
import me.zort.sqllib.pool.SQLConnectionPool;

import java.util.HashMap;
import java.util.Map;

public final class NSMClient extends NSMAbstractClient {

    private final NSMCluster cluster;
    private final Map<String, NSMNode> nodes = new HashMap<>();

    private NSMClient(NSMCluster cluster) {
        this.cluster = cluster;
        populateNodes();
    }

    @Override
    public ServiceListResponse serviceList(ServiceListRequest request) throws NSMResponseException {
        return handleRequest(getRepository(null).serviceList(request.toBuilder().all(true).build()));
    }

    // TODO

    public NSMNode getNode(String name) {
        return nodes.get(name);
    }

    @Override
    public NSMNodeRepository getRepository(String serviceId) {
        if (serviceId == null) {
            return cluster.repoBalanced();
        } else {
            return cluster.repoByService(serviceId);
        }
    }

    private void populateNodes() {
        cluster.getRepos().forEach((key, value) -> nodes.put(key, new NSMNode(value)));
    }

    public static final class Builder {

        private final Map<String, String> baseUrls = new HashMap<>();

        private SQLConnectionBuilder sqlBuilder = null;
        private SQLConnectionPool.Options sqlPoolOpt = new SQLConnectionPool.Options();

        public Builder registerNode(String name, String baseUrl) {
            baseUrls.put(name, baseUrl);
            return this;
        }

        public Builder db(SQLConnectionBuilder builder) {
            this.sqlBuilder = builder;
            return this;
        }

        public Builder poolOptions(SQLConnectionPool.Options opt) {
            this.sqlPoolOpt = opt;
            return this;
        }

        public NSMClient build() {
            if (baseUrls.isEmpty()) {
                throw new RuntimeException("At least one base url must be provided!");
            }

            SQLConnectionPool pool = null;
            if (sqlBuilder != null) {
                pool = sqlBuilder.createPool(sqlPoolOpt);
            }
            NSMCluster cluster = new NSMCluster(pool);
            for (String name : baseUrls.keySet()) {
                cluster.addNode(name, baseUrls.get(name));
            }
            return new NSMClient(cluster);
        }

    }

}
