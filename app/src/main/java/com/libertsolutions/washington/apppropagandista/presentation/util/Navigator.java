package com.libertsolutions.washington.apppropagandista.presentation.util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import com.libertsolutions.washington.apppropagandista.presentation.configuracao.ConfiguracaoActivity;
import com.libertsolutions.washington.apppropagandista.Controller.MainActivity;
import com.libertsolutions.washington.apppropagandista.presentation.login.LoginActivity;

/**
 * @author Filipe Bezerra
 */
public class Navigator {
    public static final int REQUEST_LOGIN = 0x1;
    public static final int REQUEST_SETTINGS = 0x2;

    private Navigator() {}

    public static void toMain(@NonNull Activity activity) {
        Intent mainIntent = IntentCompat.makeMainActivity(
                new Intent(activity, MainActivity.class)
                        .getComponent())
                .setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(activity, mainIntent, null);
    }

    public static void toLogin(@NonNull Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        ActivityCompat.startActivityForResult(activity, loginIntent, REQUEST_LOGIN, null);
    }

    public static void toConfiguracao(@NonNull Activity activity) {
        Intent loginIntent = new Intent(activity, ConfiguracaoActivity.class);
        ActivityCompat.startActivityForResult(activity, loginIntent, REQUEST_SETTINGS, null);
    }
}
