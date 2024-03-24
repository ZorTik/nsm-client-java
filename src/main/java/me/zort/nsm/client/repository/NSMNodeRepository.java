package me.zort.nsm.client.repository;

import me.zort.nsm.client.request.CreateServiceRequest;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.CreateServiceResponse;
import me.zort.nsm.client.response.InspectServiceResponse;
import me.zort.nsm.client.response.NodeStatusResponse;
import me.zort.nsm.client.response.ServiceListResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NSMNodeRepository {

    @GET("/v1/status")
    Call<NodeStatusResponse> status();

    @POST("/v1/servicelist")
    Call<ServiceListResponse> serviceList(ServiceListRequest request);

    @POST("/v1/service/create")
    Call<CreateServiceResponse> createService(@Body CreateServiceRequest request);

    @GET("/v1/service/{serviceId}")
    Call<InspectServiceResponse> inspectService(@Path("serviceId") String serviceId);

}
