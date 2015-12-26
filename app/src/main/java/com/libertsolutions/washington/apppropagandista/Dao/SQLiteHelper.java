package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe utilitária para gerenciar a criação de banco de dados e seu versionamento.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 25/12/2015
 * @since 1.0
 */
public class SQLiteHelper extends SQLiteOpenHelper  {
    /**
     * Variável de classe usada para loggind em modo debug
     */
    private static final String LOG = SQLiteHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "blocskin.db";//nome do banco
    private static final int DATABASE_VERSION = 1;//versão do banco

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteHelper.scriptUsuario());
        db.execSQL(SQLiteHelper.scriptMedico());
        db.execSQL(SQLiteHelper.scriptAgenda());
        db.execSQL(SQLiteHelper.scriptVisita());
        db.execSQL(SQLiteHelper.scriptEspecialidade());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG, "Atualizando banco de dados da versão " + oldVersion + " para "
                + newVersion + ", o que irá destruir todos dados existentes.");

        db.execSQL("DROP TABLE IF EXISTS Usuario");
        db.execSQL("DROP TABLE IF EXISTS " + MedicoDAO.TABELA_MEDICO);
        db.execSQL("DROP TABLE IF EXISTS " + AgendaDAO.TABELA_AGENDA);
        db.execSQL("DROP TABLE IF EXISTS " + VisitaDAO.TABELA_VISITA);
        db.execSQL("DROP TABLE IF EXISTS " + EspecialidadeDAO.TABELA_ESPECIALIDADE);
        onCreate(db);
    }

    /**
     * Gera o script de criação da tabela Usuario
     *
     * @return o script da tabela.
     */
    private static String scriptUsuario()   {
        final String SQL = "CREATE TABLE Usuario (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome text not null,"+
                "cpf text not null,"+
                "email text not null," +
                "senha text not null)";

        Log.i(LOG, "SQL de criação da tabela Usuario: \n\n" + SQL);
        return SQL;
    }

    /**
     * Gera o script de criação da tabela Medico
     *
     * @return o script da tabela.
     */
    private static String scriptMedico()   {
        final String SQL = MedicoDAO.SCRIPT_CRIACAO;

        Log.i(LOG, String.format("SQL de criação da tabela %s: \n\n%s",
                MedicoDAO.TABELA_MEDICO, SQL));
        return SQL;
    }

    /**
     * Gera o script de criação da tabela Agenda
     *
     * @return o script da tabela.
     */
    private static String scriptAgenda()   {
        final String SQL = AgendaDAO.SCRIPT_CRIACAO;

        Log.i(LOG, String.format("SQL de criação da tabela %s: \n\n%s",
                AgendaDAO.TABELA_AGENDA, SQL));
        return SQL;
    }

    /**
     * Gera o script de criação da tabela Visita
     *
     * @return o script da tabela.
     */
    private static String scriptVisita()   {
        final String SQL = VisitaDAO.SCRIPT_CRIACAO;

        Log.i(LOG, String.format("SQL de criação da tabela %s: \n\n%s",
                VisitaDAO.TABELA_VISITA, SQL));
        return SQL;
    }

    /**
     * Gera o script de criação da tabela Especialidade
     *
     * @return o script da tabela.
     */
    private static String scriptEspecialidade()   {
        final String SQL = EspecialidadeDAO.SCRIPT_CRIACAO;

        Log.i(LOG, String.format("SQL de criação da tabela %s: \n\n%s",
                EspecialidadeDAO.TABELA_ESPECIALIDADE, SQL));
        return SQL;
    }
}
