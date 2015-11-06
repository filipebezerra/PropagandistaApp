package com.libertsolutions.washington.apppropagandista.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by washington on 02/09/13.
 */
public class Mensagem {
    //Função para Exibir mensagem
    public static void MensagemAlerta(String Titulo, String Mensagem, Activity act)
    {
        AlertDialog.Builder menssagem = new AlertDialog.Builder(act);
        menssagem.setTitle(Titulo);
        menssagem.setMessage(Mensagem);
        menssagem.setNeutralButton("Ok",null);
        menssagem.show();
    }

    public static void MensagemAlerta(Context context,String mensagem)
    {
        Toast toast = Toast.makeText(context, mensagem, Toast.LENGTH_SHORT);
        toast.show();
    }
}
