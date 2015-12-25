package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Visita;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso aos dados de {@link Visita}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 24/12/2015
 * @since 1.0
 */
public class VisitaDAO {
    public static final String TABELA_VISITA = "Visita";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_ID_VISITA = "id_visita";
    public static final String COLUNA_DT_INICIO = "dt_inicio";
    public static final String COLUNA_LATITUDE_INICIAL = "lat_inicial";
    public static final String COLUNA_LONGITUDE_INICIAL = "long_inicial";
    public static final String COLUNA_DT_FIM = "dt_fim";
    public static final String COLUNA_LATITUDE_FINAL = "lat_final";
    public static final String COLUNA_LONGITUDE_FINAL = "long_final";
    public static final String COLUNA_DETALHES = "detalhes";
    public static final String COLUNA_STATUS = "status";
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

    @NonNull private Banco mBanco;

    /**
     * Construtor.
     *
     * @param context contexto para inicializar o banco.
     */
    public VisitaDAO(@NonNull Context context) {
        mBanco = Banco.getInstance(context);
    }

    /**
     * Inclui uma {@link Visita} indicando que esta foi iniciada pelo propagandista.
     * Ao realizar a inclusão o campo <code>statusagenda</code> da {@link AgendaDAO}
     * deve alterado para {@link StatusAgenda#EmAtendimento}.
     *
     * @param visita a entidade com campos requeridos para inserção preenchidos.
     * @return o ID do registro inserido.
     */
    public long incluir(@NonNull Visita visita) {
        // Pré-condições para realizar a transação na tabela destino
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

        try {
            mBanco.AbrirConexao();
            return mBanco.db()
                    .insert(TABELA_VISITA, null, valores);
        } finally {
            mBanco.close();
        }
    }

    /**
     * Altera a {@link Visita} indicando a conclusão dela. A conclusão poderá significar
     * uma "Não visita" ou "Encerramento da visita".
     *
     * @param visita a entidade com campos requeridos para alteração preenchidos.
     * @return a quantidade de registros alterados.
     */
    public int alterar(@NonNull Visita visita) {
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

        try {
            mBanco.AbrirConexao();
            return mBanco.db()
                    .update(TABELA_VISITA, valores, where, whereById);
        } finally {
            mBanco.close();
        }
    }

    /**
     * Consulta e recupera os dados da visita pelo {@code id} provido. O {@code id}
     * não deve ser {@code null} nem ser {@code id <= 0}.<br><br>
     * Esta consulta não recupera os dados da relação a entidade {@link Agenda}, ou seja não
     * chama faz chamada ao método {@link Visita#setIdAgenda(Integer)}.<br><br>
     * Esta consulta poderá retornar {@code null}.
     *
     * @param id o código da visita.
     * @return a visita consultada no banco de dados
     */
    public @Nullable Visita consultar(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "id não deve ser nulo");
        Preconditions.checkState(id > 0, "id não deve ser menor que zero");

        final String where = COLUNA_ID + " = ?";
        final String [] whereById = new String [] { String.valueOf(id) };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null ? toSingleEntity(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Lista os dados da visita pelo quantidade requerida baseada no {@code start} e no
     * {@code end}
     *
     * @param start
     * @param end
     * @return
     */
    public @Nullable List<Visita> listar(@Nullable String start, @Nullable String end) {
        Cursor cursor = null;
        try {
            cursor = query(null, null, start, end);
            return cursor != null ? toEntityList(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Recupera uma {@link Visita} com todos os dados nas colunas fornecidas no
     * momento da consulta ao banco de dados.
     *
     * @param cursor o {@code cursor} obtido por uma chamada ao método {@link #query(String, String[], String, String)}.
     * @return uma {@code Visita} com todas colunas lidas, poderá ser {@code null}.
     * @see #toSingleEntity(Cursor)
     * @see #toEntityList(Cursor)
     */
    private @Nullable Visita fromCursor(@NonNull Cursor cursor) {
        if (cursor.isClosed()) {
            return null;
        }

        if (cursor.isBeforeFirst() && !cursor.moveToFirst()) {
            return null;
        }

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
     * Obtém todo o conjunto de registros existentes no {@code cursor} se houver.
     * @param cursor o {@code cursor} obtido da chamada ao método {@link #query(String, String[], String, String)}.
     * @return uma {@link List<Visita>} obtidos do cursor {@code cursor}, poderá ser {@code null}.
     */
    private @Nullable List<Visita> toEntityList(@NonNull Cursor cursor) {
        List<Visita> list = new ArrayList<>();

        Visita visita;
        do {
            visita = fromCursor(cursor);

            if (visita != null) {
                list.add(visita);
            }
        } while(cursor.moveToNext());

        return list;
    }

    /**
     * Obtém o primeiro resultado do {@code cursor} se este conter registros.
     *
     * @param cursor o {@code cursor} obtido da chamada ao método {@link #query(String, String[], String, String)}.
     * @return a {@link Visita} com dados obtidos do {@code cursor}, poderá ser {@code null}.
     */
    private @Nullable Visita toSingleEntity(@NonNull Cursor cursor) {
        return fromCursor(cursor);
    }

    //TODO (filipe): mover para a classe Banco.
    /**
     * Realiza uma consulta na tabela {@link #TABELA_VISITA} usando os argumentos
     * de configuração das condições de consulta.
     *
     * @param where quais condições devem ser usadas, poderá ser {@code null} para obter todos registros.
     * @param arguments os argumentos das condições providas, deverá ser {@code null} quando {@code where is null}.
     * @param start determina o início da consulta, é usado em conjunto com {@code end} para formar o limite.
     * @param end determina o fim da consulta, é usado em conjunto com {@code start} para formar o limite.
     * @return o {@code cursor} com o resultado da consulta.
     */
    private Cursor query(@Nullable final String where, @Nullable final String [] arguments,
            @Nullable String start, @Nullable String end) {

        String limit = null;
        if ((start != null && !start.isEmpty()) && (end != null && !end.isEmpty())) {
            limit = start + "," + end;
        }

        try {
            mBanco.AbrirConexao();

            return mBanco.db()
                    .query(
                            TABELA_VISITA,
                            PROJECAO_TODAS_COLUNAS,
                            where,
                            arguments,
                            null,
                            null,
                            limit);
        } finally {
            mBanco.close();
        }
    }

    //TODO (filipe): mover para a classe Banco.
    /**
     * Fecha o cursor liberando os recursos alocados.
     *
     * @param cursor o cursor obtido por uma chamada ao
     * {@link #query(String, String[], String, String)}.
     */
    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}