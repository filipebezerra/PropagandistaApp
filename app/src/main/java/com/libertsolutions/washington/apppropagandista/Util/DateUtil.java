package com.libertsolutions.washington.apppropagandista.Util;

import android.support.annotation.NonNull;
import org.joda.time.DateTime;

/**
 * Classe utilitária para obter formação de data.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/12/2015
 * @since 0.1.0
 */
public class DateUtil {
    public static final String FORMATO_DATA_PADRAO = "dd/MM/yyyy - HH:mm";

    public static String format(long  dateIntMillis) {
        return format(dateIntMillis, FORMATO_DATA_PADRAO);
    }

    public static String format(long dateIntMillis, @NonNull String format) {
        return new DateTime(dateIntMillis).toString(format);
    }
}
