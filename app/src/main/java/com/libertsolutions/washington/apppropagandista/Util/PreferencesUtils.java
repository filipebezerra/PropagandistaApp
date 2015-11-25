package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.Model.Usuario;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/11/2015
 * @since #
 */
public class PreferencesUtils {
    private static final String USER_LOGGED_KEY = "user-logged";
    private static final String USER_NAME_KEY = "user-name";
    private static final String USER_EMAIL_KEY = "user-email";
    private static final String PREF_SERVER_URL = "pref_url";
    private static final String PREF_AUTH_KEY = "pref_auth_key";

    public static boolean isUserLogged(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return
                preferences.contains(USER_LOGGED_KEY) &&
                        preferences.getBoolean(USER_LOGGED_KEY, false) &&
                preferences.contains(USER_NAME_KEY) &&
                        preferences.getString(USER_NAME_KEY, null) != null &&
                preferences.contains(USER_EMAIL_KEY) &&
                        preferences.getString(USER_EMAIL_KEY, null) != null;
    }

    public static void setUserLogged(@NonNull Context context, Propagandista propagandista) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(USER_LOGGED_KEY, true);
        editor.putString(USER_NAME_KEY, propagandista.getNome());
        editor.putString(USER_EMAIL_KEY, propagandista.getUsuario().getEmail());
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static Propagandista getUserLogged(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (isUserLogged(context)) {
            final Propagandista propagandista = new Propagandista();
            final Usuario usuario = new Usuario();
            usuario.setEmail(preferences.getString(USER_EMAIL_KEY, ""));
            propagandista.setNome(preferences.getString(USER_NAME_KEY, ""));
            propagandista.setUsuario(usuario);

            return propagandista;
        } else {
            return null;
        }
    }

    public static void logoutUser(@NonNull Context context) {
        if (isUserLogged(context)) {
            final SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(USER_LOGGED_KEY, false);
            editor.putString(USER_NAME_KEY, null);
            editor.putString(USER_EMAIL_KEY, null);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }

    public static String getSyncUrlSettings(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (!preferences.contains(PREF_SERVER_URL)) {
            return null;
        }

        return preferences.getString(PREF_SERVER_URL, null);
    }

    public static String getSyncAuthKeySettings(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (!preferences.contains(PREF_AUTH_KEY)) {
            return null;
        }

        return preferences.getString(PREF_AUTH_KEY, null);
    }
}
