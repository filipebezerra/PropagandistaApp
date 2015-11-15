package com.libertsolutions.washington.apppropagandista.network.call;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.GsonBuilder;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.contants.ServiceUrl;
import com.libertsolutions.washington.apppropagandista.network.listener.AsyncListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/11/2015
 * @since #
 */
public class LoginAsyncTaskCall implements AsyncCall<String,Propagandista> {
    private static final String TAG = LoginAsyncTaskCall.class.getSimpleName();

    @Override
    public void execute(String cpf, final AsyncListener<Propagandista> callback) {
        new AsyncTask<String,Void,Propagandista>() {
            private Throwable mException;

            @Override
            protected void onPreExecute() {
                callback.onBeforeExecute();
            }

            @Override
            protected Propagandista doInBackground(@NonNull String... params) {
                if (params.length == 0) {
                    mException = new IllegalArgumentException("Parâmetro cpf vazio");
                    return null;
                }

                BufferedReader bufferedReader = null;

                try {
                    final Uri uri = Uri.parse(ServiceUrl.PROPAGANDISTA)
                            .buildUpon()
                            .appendQueryParameter("cpf", params[0])
                            .build();

                    final String urlString = uri.toString();
                    Log.d(TAG, String.format("URL final: %s", urlString));

                    final URL url = new URL(urlString);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    bufferedReader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                            .fromJson(bufferedReader, Propagandista.class);
                } catch (IOException e) {
                    mException = e;
                    return null;
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Closing the buffer", e);
                        }
                    }
                }
            }

            @Override
            protected void onPostExecute(Propagandista propagandista) {
                if (propagandista != null) {
                    callback.onSuccess(propagandista);
                } else if (mException != null) {
                    callback.onFailure(mException);
                } else {
                    callback.onResultNothing();
                }
            }
        }.execute(cpf);
    }
}
