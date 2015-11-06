package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by washington on 04/11/2015.
 */
public class Banco extends SQLiteOpenHelper  {
    //Atributos
    private static final String DATABASE_NAME = "blocskin.db";//nome do banco
    private static final int DATABASE_VERSION = 1;//versão do banco

    private static Context context = null;
    private static Banco instance;
    private static SQLiteDatabase db = null;
    private static int nConect = 0;

    private Banco(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
        Banco.context = context;
    }

    public SQLiteDatabase db() {
        return db;
    }

    public void AbrirConexao() {
        nConect++;
        db = getInstance(context).getWritableDatabase();

    }

    public static synchronized Banco getInstance(Context context) {
        if (instance == null) {
            instance = new Banco(context, DATABASE_NAME, null,DATABASE_VERSION);
        }

        return instance;
    }

    public void FecharConexao() {
        close();
    }

    @Override
    public synchronized void close() {
        nConect--;
        if (instance != null && nConect <= 0)
            db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //executa os scripts de criação do banco
            db.execSQL(Usuario());
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }
    }
    //cria a tabela Usuári
    private String Usuario()   {
        return "CREATE TABLE IF NOT EXISTS Usuario (id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome text not null,"+
                "cpf text not null,"+
                "email text not null," +
                "senha text not null)";
    }

    //este método faz a atualização do banco
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
