package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Classe utilitária para mostrar diversos tipos de diálogos.
 *
 * @author Filipe Bezerra
 * @version 1.0, 24/12/2015
 * @since 1.0
 */
public class Dialogos {
    public static void mostrarMensagem(@NonNull Context context, @NonNull String titulo,
            @NonNull String conteudo) {
        new MaterialDialog.Builder(context)
                .title(titulo)
                .content(conteudo)
                .positiveText("OK")
                .show();
    }

    public static MaterialDialog mostrarProgresso(@NonNull Context context,
            @NonNull String conteudo, boolean cancelavel) {
        return new MaterialDialog.Builder(context)
                .content(conteudo)
                .progress(true, 0)
                .cancelable(cancelavel)
                .show();
    }
}
