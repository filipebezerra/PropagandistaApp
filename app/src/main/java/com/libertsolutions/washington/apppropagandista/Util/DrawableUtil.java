package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 01.0, 27/12/2015
 * @since 0.1.0
 */
public class DrawableUtil {
    public static void tint(@NonNull Context context, @NonNull Drawable drawable,
            @ColorRes int colorRes) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorRes));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
    }
}
