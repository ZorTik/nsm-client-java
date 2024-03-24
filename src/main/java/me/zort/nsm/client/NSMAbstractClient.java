package me.zort.nsm.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zort.nsm.client.exception.NSMResponseException;
import me.zort.nsm.client.request.CreateServiceRequest;
import me.zort.nsm.client.request.ServiceListRequest;
import me.zort.nsm.client.response.CreateServiceResponse;
import me.zort.nsm.client.response.InspectServiceResponse;
import me.zort.nsm.client.response.ServiceListResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public abstract class NSMAbstractClient {

    public abstract NSMNodeRepository getRepository(String serviceId);
    public abstract ServiceListResponse serviceList(ServiceListRequest request) throws NSMResponseException;

    public final CreateServiceResponse createService(CreateServiceRequest request) throws NSMResponseException {
        return handleRequest(getRepository(null).createService(request));
    }

    public final InspectServiceResponse inspectService(String serviceId) throws NSMResponseException {
        return handleRequest(getRepository(serviceId).inspectService(serviceId));
    }

    protected <T> T handleRequest(Call<T> call) throws NSMResponseException {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            try (ResponseBody rawBody = response.errorBody()) {
                if (rawBody == null) {
                    throw new RuntimeException("Unknown error received from status code " + response.code() + " with empty body.");
                }
                String rawBodyString = rawBody.string();
                try {
                    JsonObject errorObject = JsonParser.parseString(rawBodyString).getAsJsonObject();
                    throw new NSMResponseException(
                            errorObject.get("status").getAsInt(),
                            errorObject.get("message").getAsString()
                    );
                } catch (Exception e) {
                    if (e instanceof NSMResponseException) {
                        throw (NSMResponseException) e;
                    }
                    throw new RuntimeException("Unknown error received from status code " + response.code() + " with body: " + rawBodyString);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while executing the request.", e);
        }
    }

}
