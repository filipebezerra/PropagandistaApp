package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Visita;

/**
 * Classe de acesso aos dados de {@link Visita}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 24/12/2015
 * @since 1.0
 */
public class VisitaDAO extends DAOGenerico<Visita> {
    static final String TABELA_VISITA = "Visita";
    public static final String COLUNA_ID_VISITA = "id_visita";
    public static final String COLUNA_DT_INICIO = "dt_inicio";
    public static final String COLUNA_LATITUDE_INICIAL = "lat_inicial";
    public static final String COLUNA_LONGITUDE_INICIAL = "long_inicial";
    public static final String COLUNA_DT_FIM = "dt_fim";
    public static final String COLUNA_LATITUDE_FINAL = "lat_final";
    public static final String COLUNA_LONGITUDE_FINAL = "long_final";
    public static final String COLUNA_DETALHES = "detalhes";
    public static final String COLUNA_RELACAO_AGENDA = "id_agenda";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_VISITA,
            COLUNA_DT_INICIO,
            COLUNA_LATITUDE_INICIAL,
            COLUNA_LONGITUDE_INICIAL,
            COLUNA_DT_FIM,
            COLUNA_LATITUDE_FINAL,
            COLUNA_LONGITUDE_FINAL,
            COLUNA_DETALHES,
            COLUNA_STATUS,
            COLUNA_RELACAO_AGENDA
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + TABELA_VISITA + " (" +
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

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public VisitaDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_VISITA;
    }

    @NonNull
    @Override
    protected String[] projecaoTodasColunas() {
        return PROJECAO_TODAS_COLUNAS;
    }

    @Nullable
    @Override
    protected String colunasOrdenacao() {
        return null;
    }

    @Nullable
    @Override
    protected Visita fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) return null;

        Visita visita = new Visita();

        visita.setId(cursor.getInt(
                cursor.getColumnIndex(COLUNA_ID)));
        visita.setIdVisita(cursor.getInt(
                cursor.getColumnIndex(COLUNA_ID_VISITA)));
        visita.setDataInicio(cursor.getLong(
                cursor.getColumnIndex(COLUNA_DT_INICIO)));
        visita.setLatInicial(cursor.getDouble(
                cursor.getColumnIndex(COLUNA_LATITUDE_INICIAL)));
        visita.setLongInicial(cursor.getDouble(
                cursor.getColumnIndex(COLUNA_LONGITUDE_INICIAL)));
        visita.setDataFim(cursor.getLong(
                cursor.getColumnIndex(COLUNA_DT_FIM)));
        visita.setLatFinal(cursor.getDouble(
                cursor.getColumnIndex(COLUNA_LATITUDE_FINAL)));
        visita.setLongFinal(cursor.getDouble(
                cursor.getColumnIndex(COLUNA_LONGITUDE_FINAL)));
        visita.setDetalhes(cursor.getString(
                cursor.getColumnIndex(COLUNA_DETALHES)));
        visita.setStatus(Status.fromOrdinal(cursor.getInt(
                cursor.getColumnIndex(COLUNA_STATUS))));
        visita.setIdAgenda(cursor.getInt(
                cursor.getColumnIndex(COLUNA_RELACAO_AGENDA)));

        return visita;
    }

    /**
     * Inclui uma {@link Visita} indicando que esta foi iniciada pelo propagandista.
     * Ao realizar a inclusão o campo <code>statusagenda</code> da {@link AgendaDAO}
     * deve alterado para {@link StatusAgenda#EmAtendimento}.
     *
     * @param visita a entidade com campos requeridos para inserção preenchidos.
     * @return o ID do registro inserido.
     */
    @Override
    public long incluir(@NonNull Visita visita) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(visita, "visita não pode ser nula");
        Preconditions.checkNotNull(visita.getDataInicio(),
                "visita.getDataInicio() não pode ser nulo");
        Preconditions.checkNotNull(visita.getLatInicial(),
                "visita.getLatInicial() não pode ser nula");
        Preconditions.checkNotNull(visita.getLongInicial(),
                "visita.getLongInicial() não pode ser nula");
        Preconditions.checkNotNull(visita.getIdAgenda(),
                "visita.getIdAgenda() não pode ser nula");
        Preconditions.checkState((
                        visita.getStatus() != null
                                && visita.getStatus() == Status.Importado
                                && visita.getIdVisita() == null),
                "agenda.getIdVisita() não pode ser nulo");

        ContentValues valores = new ContentValues();

        if (visita.getIdVisita() != null) {
            valores.put(COLUNA_ID_VISITA, visita.getIdVisita());
        }

        valores.put(COLUNA_DT_INICIO, visita.getDataInicio());
        valores.put(COLUNA_LATITUDE_INICIAL, visita.getLatInicial());
        valores.put(COLUNA_LONGITUDE_INICIAL, visita.getLongInicial());
        valores.put(COLUNA_RELACAO_AGENDA, visita.getIdAgenda());
        valores.put(COLUNA_STATUS, visita.getStatus() == null ?
                Status.Pendente.ordinal() : visita.getStatus().ordinal());

        if (visita.getDetalhes() != null && !visita.getDetalhes().isEmpty()) {
            valores.put(COLUNA_DETALHES, visita.getDetalhes());
        }

        return mDatabase.insert(TABELA_VISITA, null, valores);
    }

    /**
     * Altera a {@link Visita} indicando a conclusão dela. A conclusão poderá significar
     * uma "Não visita" ou "Encerramento da visita".
     *
     * @param visita a entidade com campos requeridos para alteração preenchidos.
     * @return a quantidade de registros alterados.
     */
    @Override
    public int alterar(@NonNull Visita visita) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkNotNull(visita, "visita não pode ser nula");
        Preconditions.checkNotNull(visita.getId(), "visita.getId() não pode ser nulo");
        Preconditions.checkNotNull(visita.getDataInicio(), "visita.getDataInicio() não pode ser nulo");
        Preconditions.checkNotNull(visita.getLatInicial(), "visita.getLatInicial() não pode ser nula");
        Preconditions.checkNotNull(visita.getLongInicial(), "visita.getLongInicial() não pode ser nula");

        Preconditions.checkNotNull(visita.getIdAgenda(), "visita.getIdAgenda() não pode ser nulo");

        Preconditions.checkNotNull(visita.getDataFim(), "visita.getDataFim() não pode ser nula");
        Preconditions.checkNotNull(visita.getLatFinal(), "visita.getLatFinal() não pode ser nula");
        Preconditions.checkNotNull(visita.getLongFinal(), "visita.getLongFinal() não pode ser nula");

        Preconditions.checkNotNull(visita.getStatus(),
                "visita.getStatus() não pode ser nula");
        Preconditions.checkState(
                ((visita.getStatus() == Status.Enviado
                        || visita.getStatus() == Status.Importado)
                        && visita.getIdVisita() == null),
                "visita.getIdVisita() não pode ser nulo");

        ContentValues valores = new ContentValues();

        if (visita.getStatus() == Status.Enviado || visita.getStatus() == Status.Importado) {
            valores.put(COLUNA_ID_VISITA, visita.getIdVisita());

            if (visita.getStatus() == Status.Importado) {
                valores.put(COLUNA_RELACAO_AGENDA, visita.getIdAgenda());
            }
        }

        valores.put(COLUNA_DT_FIM, visita.getDataFim());
        valores.put(COLUNA_LATITUDE_FINAL, visita.getLatFinal());
        valores.put(COLUNA_LONGITUDE_FINAL, visita.getLongFinal());
        valores.put(COLUNA_STATUS,
                visita.getStatus() == Status.Enviado
                        || visita.getStatus() == Status.Importado ?
                        visita.getStatus().ordinal() : Status.Pendente.ordinal());

        if (visita.getDetalhes() != null && visita.getDetalhes().length() > 0) {
            valores.put(COLUNA_DETALHES, visita.getDetalhes());
        }

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(visita.getId()) };

        return mDatabase.update(TABELA_VISITA, valores, where, whereById);
    }
}