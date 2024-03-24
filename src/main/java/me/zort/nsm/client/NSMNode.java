package me.zort.nsm.client;

import me.zort.nsm.client.exception.NSMResponseException;
import me.zort.nsm.client.repository.NSMNodeRepository;
import me.zort.nsm.client.response.NodeStatusResponse;

public final class NSMNode extends NSMAbstractClient {

    private final NSMNodeRepository repository;

    NSMNode(NSMNodeRepository repository) {
        this.repository = repository;
    }

    public NodeStatusResponse status() throws NSMResponseException {
        return handleRequest(repository.status());
    }

    @Override
    public NSMNodeRepository getRepository(String serviceId) {
        return repository;
    }
}
