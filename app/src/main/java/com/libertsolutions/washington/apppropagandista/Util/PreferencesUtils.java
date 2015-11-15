package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;

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

    public static boolean isUserLogged(@NonNull Context context) {
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.contains(USER_LOGGED_KEY) &&
                preferences.getBoolean(USER_LOGGED_KEY, false);
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
}
