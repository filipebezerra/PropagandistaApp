package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Classe utilitária para mostrar diversos tipos de diálogos.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/12/2015
 * @since 0.1.0
 */
public class Dialogos {
    public static void mostrarMensagemFlutuante(@NonNull View view, @NonNull String texto,
            boolean pequenaDuracao) {
        Snackbar.make(view, texto, pequenaDuracao ? LENGTH_SHORT : LENGTH_LONG)
                .show();
    }

    public static void mostrarMensagem(@NonNull Context context, @NonNull String titulo,
            @NonNull String conteudo) {
        new MaterialDialog.Builder(context)
                .title(titulo)
                .content(conteudo)
                .positiveText("OK")
                .show();
    }

    public static void mostrarMensagem(@NonNull Context context, @NonNull String titulo,
            @NonNull String conteudo, @NonNull SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .title(titulo)
                .content(conteudo)
                .positiveText("OK")
                .onPositive(callback)
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
