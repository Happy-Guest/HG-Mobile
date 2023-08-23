package ipl.estg.happyguest.utils.api;

import com.google.gson.GsonBuilder;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static Retrofit getClient(String token) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                    .build();
            return chain.proceed(request);
        }).build();

        return new Retrofit.Builder()
                .baseUrl("https://happyguest.joaopinto.pt/api/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().setLenient().create()))
                .client(client)
                .build();
    }
}
