package com.libertsolutions.washington.apppropagandista.api.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.libertsolutions.washington.apppropagandista.BuildConfig;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

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

            final OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(createCache(context))
                    .addInterceptor(createLoggingInterceptor())
                    .addInterceptor(createInterceptorWithAuthKey(authKey))
                    .build();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(
                            RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(httpClient)
                    .build();

            sAuthKey = authKey;
            sBaseUrl = baseUrl;
        }

        return sRetrofit.create(serviceClass);
    }

    private static Cache createCache(@NonNull Context context) {
        return new Cache(new File(context.getCacheDir(), HTTP_CACHE_FILE_NAME), HTTP_CACHE_SIZE);
    }

    private static Interceptor createLoggingInterceptor() {
        HttpLoggingInterceptor.Level level;
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY;
        } else {
            level = HttpLoggingInterceptor.Level.NONE;
        }
        return new HttpLoggingInterceptor().setLevel(level);
    }

    private static Interceptor createInterceptorWithAuthKey(@NonNull final String authKey) {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                final HttpUrl httpUrl = original.url()
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
