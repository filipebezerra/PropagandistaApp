package com.libertsolutions.washington.apppropagandista.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by washington on 01/11/2015.
 */
public class Tela {
    //Metódo para abrir Tela
    public static void AbrirTela(Activity activity, Class tela)
    {
        Intent it = new Intent(activity, tela);
        activity.startActivity(it);
    }

    //Metódo para abrir Tela
    public static void AbrirTela(Activity activity, Class tela,Bundle bundle)
    {
        Intent it = new Intent(activity, tela);
        it.putExtras(bundle);
        activity.startActivity(it);
    }

    //Metódo para abrir Tela
    public static void AbrirTela(Activity activity,Class tela, int opcao)
    {
        Intent it = new Intent(activity, tela);
        activity.startActivityForResult(it,opcao);
    }

    //Metódo para abrir Tela
    public static void AbrirTela(Activity activity,Class tela, int opcao,Bundle bundle)
    {
        Intent it = new Intent(activity, tela);
        it.putExtras(bundle);
        activity.startActivityForResult(it,opcao);
    }
}