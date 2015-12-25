package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.VisitaDAO;

import static com.libertsolutions.washington.apppropagandista.Dao.VisitaDAO.TABELA_VISITA;

/**
 * Classe utilitária para operações com banco de dados local (SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0
 * @since 1.0
 */
public class Banco extends SQLiteOpenHelper  {
    /**
     * Variável de classe usada para loggind em modo debug
     */
    private static final String LOG = Banco.class.getSimpleName();

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
            db.execSQL(Usuario());//Script Tabela Usuário
            db.execSQL(Medico());//Script Tabela Médico
            db.execSQL(Agenda());//Script Tabela Agenda
            db.execSQL(Visita());//Script Tabela Visita
        }catch (Exception error) {
            Log.e(LOG, "Falha na criação das tabelas.", error);
            throw error;
        }
    }

    //cria a tabela Usuário
    private String Usuario()   {
        return "CREATE TABLE IF NOT EXISTS Usuario (id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome text not null,"+
                "cpf text not null,"+
                "email text not null," +
                "senha text not null)";
    }

    //cria a tabela Médico
    private String Medico()   {
        return "CREATE TABLE IF NOT EXISTS Medico (id_medico INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome text not null,"+
                "dtAniversario text,"+
                "secretaria text," +
                "telefone text," +
                "email text," +
                "crm text," +
                "especialidade text," +
                "id_unico integer,"+
                "status integer)";
    }

    //cria a tabela Agenda
    private String Agenda()   {
        final String SQL =  "CREATE TABLE IF NOT EXISTS " + AgendaDAO.TABELA_AGENDA + "("  +
                    AgendaDAO.COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AgendaDAO.COLUNA_ID_AGENDA + " INTEGER," +
                    AgendaDAO.COLUNA_DT_COMPROMISSO + " INTEGER not null, " +
                    AgendaDAO.COLUNA_OBSERVACAO + " TEXT," +
                    AgendaDAO.COLUNA_STATUS_AGENDA + " INTEGER, " +
                    AgendaDAO.COLUNA_STATUS + " INTEGER, " +
                    AgendaDAO.COLUNA_RELACAO_MEDICO + " INTEGER not null, " +
                " FOREIGN KEY (id_medico) REFERENCES Medico (id_medico)" +
                " UNIQUE (" + AgendaDAO.COLUNA_ID_AGENDA + ") ON CONFLICT REPLACE);";

        Log.d(LOG, "SQL de criação da tabela Agenda: \n\n" + SQL);
        return SQL;
    }

    /**
     * Gera o script de criação da tabela Visita
     *
     * @return o script da tabela.
     */
    private String Visita()   {
        final String SQL = "CREATE TABLE " + TABELA_VISITA + " (" +
                    VisitaDAO.COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VisitaDAO.COLUNA_ID_VISITA + " INTEGER," +
                    VisitaDAO.COLUNA_DT_INICIO + " INTEGER not null, " +
                    VisitaDAO.COLUNA_LATITUDE_INICIAL + " REAL not null, " +
                    VisitaDAO.COLUNA_LONGITUDE_INICIAL + " REAL not null, " +
                    VisitaDAO.COLUNA_DT_FIM + " INTEGER, " +
                    VisitaDAO.COLUNA_LATITUDE_FINAL + " REAL, " +
                    VisitaDAO.COLUNA_LONGITUDE_FINAL + " REAL, " +
                    VisitaDAO.COLUNA_DETALHES + " TEXT, " +
                    VisitaDAO.COLUNA_STATUS + " INTEGER, " +
                    VisitaDAO.COLUNA_RELACAO_AGENDA + " INTEGER not null, " +
                " FOREIGN KEY (" + VisitaDAO.COLUNA_RELACAO_AGENDA +
                    ") REFERENCES " + AgendaDAO.TABELA_AGENDA +
                        " (" + AgendaDAO.COLUNA_ID_AGENDA + "), " +
                " UNIQUE (" + VisitaDAO.COLUNA_ID_VISITA + ") ON CONFLICT REPLACE);";

        Log.d(LOG, "SQL de criação da tabela Visita: \n\n" + SQL);
        return SQL;
    }

    //este método faz a atualização do banco
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO (filipe bezerra) regra de negócio para atualizar a versão do banco
    }
}
