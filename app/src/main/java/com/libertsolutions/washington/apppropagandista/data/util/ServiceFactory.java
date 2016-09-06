package com.libertsolutions.washington.apppropagandista.data.util;

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
 */
public class ServiceFactory {
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
            sRetrofit = createRetrofit(context, baseUrl, authKey);
            sAuthKey = authKey;
            sBaseUrl = baseUrl;
        }

        return sRetrofit.create(serviceClass);
    }

    private static Retrofit createRetrofit(
            final Context context, final String baseUrl, final String authKey) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(
                        RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(createOkHttpClient(context, authKey))
                .build();
    }

    private static OkHttpClient createOkHttpClient(final Context context, final String authKey) {
        return new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(createCache(context))
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(createInterceptorWithAuthKey(authKey))
                .build();
    }

    private static Cache createCache(final Context context) {
        return new Cache(new File(context.getCacheDir(), HTTP_CACHE_FILE_NAME), HTTP_CACHE_SIZE);
    }

    private static Interceptor createLoggingInterceptor() {
        return new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ?
                        HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    private static Interceptor createInterceptorWithAuthKey(final String authKey) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
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
