package me.zort.nsm.client;

import me.zort.nsm.client.exception.NSMResponseException;
import me.zort.nsm.client.repository.NSMNodeRepository;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.NodeStatusResponse;
import me.zort.nsm.client.response.ServiceListResponse;

public final class NSMNode extends NSMAbstractClient {

    private final NSMNodeRepository repository;

    NSMNode(NSMNodeRepository repository) {
        this.repository = repository;
    }

    public NodeStatusResponse status() throws NSMResponseException {
        return handleRequest(repository.status());
    }

    @Override
    public ServiceListResponse serviceList(ServiceListRequest request) throws NSMResponseException {
        return handleRequest(repository.serviceList(request));
    }

    // TODO

    @Override
    public NSMNodeRepository getRepository(String serviceId) {
        return repository;
    }
}
