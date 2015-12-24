package com.libertsolutions.washington.apppropagandista.api.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Classe base para construção das classes de serviço que fazem chamada
 * aos recursos HTTP do webservice Rest.
 *
 * @author Filipe Bezerra
 * @version #, 02/12/2015
 * @since #
 */
public class RetrofitController {
    private static final long HTTP_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String HTTP_CACHE_FILE_NAME = "http";

    private static Retrofit sRetrofit;

    private static String sBaseUrl;
    private static String sAuthKey;

    public static <S> S createService(@NonNull Class<S> serviceClass, @NonNull Context context) {
        final String baseUrl = PreferencesUtils.getSyncUrlSettings(context);
        final String authKey = PreferencesUtils.getSyncAuthKeySettings(context);

        if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(authKey)) {
            return null;
        }

        if (sRetrofit == null ||
                (!baseUrl.equals(sBaseUrl) || !authKey.equals(sAuthKey))) {

            final OkHttpClient httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            httpClient.setReadTimeout(30, TimeUnit.SECONDS);
            httpClient.setCache(createCache(context));
            httpClient.interceptors().clear();
            httpClient.interceptors().add(createLoggingInterceptor());
            httpClient.interceptors().add(createCustomInterceptor(authKey));

            sRetrofit = new Retrofit.Builder().
                    baseUrl(baseUrl).
                    addConverterFactory(GsonConverterFactory.create()).
                    addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                    client(httpClient).
                    build();

            sAuthKey = authKey;
            sBaseUrl = baseUrl;
        }

        return sRetrofit.create(serviceClass);
    }

    private static Cache createCache(@NonNull Context context) {
        return new Cache(new File(context.getCacheDir(), HTTP_CACHE_FILE_NAME),
                HTTP_CACHE_SIZE);
    }

    private static Interceptor createLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return interceptor;
    }

    private static Interceptor createCustomInterceptor(@NonNull final String authKey) {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                final HttpUrl httpUrl = original.httpUrl()
                        .newBuilder()
                        .addQueryParameter("key", authKey)
                        .build();

                final Request request = original.newBuilder()
                        .url(httpUrl)
                        .header("Accept", "applicaton/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
    }
}
