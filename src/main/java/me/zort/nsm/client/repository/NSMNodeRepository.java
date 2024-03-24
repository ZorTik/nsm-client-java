package me.zort.nsm.client.repository;

import me.zort.nsm.client.request.ChServiceOptionsRequest;
import me.zort.nsm.client.request.CreateServiceRequest;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NSMNodeRepository {

    @GET("/v1/status")
    Call<NodeStatusResponse> status();

    @POST("/v1/servicelist")
    Call<ServiceListResponse> serviceList(@Body ServiceListRequest request);

    @POST("/v1/service/create")
    Call<CreateServiceResponse> createService(@Body CreateServiceRequest request);

    @GET("/v1/service/{serviceId}")
    Call<InspectServiceResponse> inspectService(@Path("serviceId") String serviceId);

    @POST("/v1/service/{serviceId}/resume")
    Call<BasicActionResponse> resumeService(@Path("serviceId") String serviceId);

    @POST("/v1/service/{serviceId}/stop")
    Call<BasicActionResponse> stopService(@Path("serviceId") String serviceId);

    @POST("/v1/service/{serviceId}/delete")
    Call<BasicActionResponse> deleteService(@Path("serviceId") String serviceId);

    @POST("/v1/service/{serviceId}/reboot")
    Call<BasicActionResponse> rebootService(@Path("serviceId") String serviceId);

    @GET("/v1/service/{serviceId}/powerstatus")
    Call<PowerStatusResponse> powerStatus(@Path("serviceId") String serviceId);

    @POST("/v1/service/{serviceId}/options")
    Call<BasicActionResponse> changeOptions(@Path("serviceId") String serviceId, @Body ChServiceOptionsRequest request);

}
