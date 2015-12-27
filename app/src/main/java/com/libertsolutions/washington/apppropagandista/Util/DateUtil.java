package com.libertsolutions.washington.apppropagandista.Util;

import android.support.annotation.NonNull;
import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import static org.joda.time.format.DateTimeFormat.forPattern;

/**
 * Classe utilitária para obter formação de data.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/12/2015
 * @since 0.1.0
 */
public class DateUtil {
    private static final String FORMATO_DATA_PADRAO = "dd/MM/yyyy";
    private static final String FORMATO_HORA_PADRAO = "HH:mm";
    private static final String FORMATO_DATA_HORA_PADRAO =
            FORMATO_DATA_PADRAO + " - " + FORMATO_HORA_PADRAO;

    public enum FormatType {
        DATE_ONLY, TIME_ONLY, DATE_AND_TIME;
    }

    public static String format(int hourOfDay, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        return LocalTime.fromCalendarFields(calendar).toString(FORMATO_HORA_PADRAO);
    }

    public static String format(int year, int monthOfYear, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return LocalDate.fromCalendarFields(calendar).toString(FORMATO_DATA_PADRAO);
    }

    public static String format(long dateInMillis, @NonNull FormatType formatType) {
        switch (formatType) {
            case DATE_ONLY:
                return format(dateInMillis, FORMATO_DATA_PADRAO);
            case TIME_ONLY:
                return format(dateInMillis, FORMATO_HORA_PADRAO);
            default:
                return format(dateInMillis, FORMATO_DATA_HORA_PADRAO);
        }
    }

    public static String format(long dateInMillis, @NonNull String format) {
        return new DateTime(dateInMillis).toString(format);
    }

    public static long toDateMillis(@NonNull String dateText) {
        return DateTime.parse(dateText).getMillis();
    }

    public static DateTime toDate(@NonNull String dateText) {
        return DateTime.parse(dateText, forPattern(FORMATO_DATA_PADRAO));
    }

    public static DateTime toTime(@NonNull String timeText) {
        return DateTime.parse(timeText, forPattern(FORMATO_HORA_PADRAO));
    }
}
