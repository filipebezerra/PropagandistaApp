package com.libertsolutions.washington.apppropagandista.Util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import com.libertsolutions.washington.apppropagandista.Controller.ConfiguracaoActivity;
import com.libertsolutions.washington.apppropagandista.Controller.LoginActivity;
import com.libertsolutions.washington.apppropagandista.Controller.MainActivity;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 25/11/2015
 * @since #
 */
public class Navigator {
    private static final int NO_RESULT = -1;
    private Navigator() {
        // no instances
    }

    public static void navigateToMain(@NonNull Context context) {
        final Intent intent = IntentCompat.makeMainActivity(
                new ComponentName(context, MainActivity.class));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchActivity(context, intent, NO_RESULT);
    }

    public static void navigateToSettings(@NonNull Context context, int requestCode) {
        launchActivity(context, ConfiguracaoActivity.getLauncherIntent(context), requestCode);
    }

    public static void navigateToLogin(@NonNull Context context, int requestCode) {
        launchActivity(context, LoginActivity.getLauncherIntent(context), requestCode);
    }

    private static void launchActivity(@NonNull Context context,
            @NonNull Intent launchIntent, int requestCode) {
        if (requestCode == -1) {
            context.startActivity(launchIntent);
        } else if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(launchIntent, requestCode);
        }
    }
}
