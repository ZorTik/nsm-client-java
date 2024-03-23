package me.zort.nsm.client;

import com.google.gson.Gson;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NSMClient {

    private final Gson gson = new Gson();
    private final Map<String, NSMRepository> repos = new ConcurrentHashMap<>();

    public NSMClient(List<String> baseUrls) {
        baseUrls.forEach(url -> repos.put(url, createRepository(url)));
    }

    private NSMRepository repoByService(String serviceId) {
        // TODO
        return null;
    }

    private NSMRepository repoBalanced() {
        // TODO
        return null;
    }

    private NSMRepository createRepository(String baseUrl) {
        Retrofit r = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return r.create(NSMRepository.class);
    }

}
