package me.zort.nsm.client;

import me.zort.nsm.client.exception.NSMResponseException;
import me.zort.nsm.client.repository.NSMNodeRepository;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.ServiceListResponse;
import me.zort.sqllib.SQLConnectionBuilder;
import me.zort.sqllib.pool.SQLConnectionPool;

import java.util.*;
import java.util.logging.Logger;

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

    public NSMNode getNode(String id) {
        return nodes.get(id);
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

        private final Set<String> baseUrls = new HashSet<>();

        private SQLConnectionBuilder sqlBuilder = null;
        private SQLConnectionPool.Options sqlPoolOpt = new SQLConnectionPool.Options();
        private Logger logger = Logger.getGlobal();

        public Builder node(String baseUrl) {
            baseUrls.clear();
            baseUrls.add(baseUrl);
            return this;
        }

        // Provide db where NSM stores its data about services and nodes to
        // be able to balance requests.
        public Builder nodes(Collection<String> baseUrls, SQLConnectionBuilder db) {
            this.baseUrls.clear();
            this.baseUrls.addAll(baseUrls);
            this.sqlBuilder = db;
            return this;
        }

        public Builder poolOptions(SQLConnectionPool.Options opt) {
            this.sqlPoolOpt = opt;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
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
            NSMCluster cluster = new NSMCluster(pool, logger);
            for (String baseUrl : baseUrls) {
                cluster.addNode(baseUrl);
            }
            return new NSMClient(cluster);
        }

    }

}
