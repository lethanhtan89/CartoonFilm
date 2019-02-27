package tan.le.cartoonfilm.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tan.le.cartoonfilm.utils.Constant;

public class BaseApiHandler {
    protected static Retrofit buildAuthorizationRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constant.API_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(Constant.API_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                return chain.proceed(request);
                            }
                        }).build();

        String serverUrlRoot = Constant.URL_SERVER;
        String serverUrl = serverUrlRoot + (serverUrlRoot.substring(serverUrlRoot.length() - 1).equals("/") ? "" : "/");
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(defaultHttpClient)
                .build();
        return builder;
    }
}
