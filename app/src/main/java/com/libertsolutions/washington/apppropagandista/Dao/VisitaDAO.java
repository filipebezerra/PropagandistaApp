package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Enum.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Visita;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Washington, Filipe Bezerra
 */
public class VisitaDAO {
    public static final String TABELA_VISITA = "Visita";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_DT_INICIO = "dt_inicio";
    public static final String CAMPO_LATITUDE_INICIAL = "lat_inicial";
    public static final String CAMPO_LONGITUDE_INICIAL = "long_inicial";
    public static final String CAMPO_DT_FIM = "dt_fim";
    public static final String CAMPO_LATITUDE_FINAL = "lat_final";
    public static final String CAMPO_LONGITUDE_FINAL = "long_final";
    public static final String CAMPO_DETALHES = "detalhes";
    public static final String CAMPO_STATUS = "status";
    public static final String CAMPO_RELACAO_AGENDA = "id_agenda";

    private static final String[] PROJECAO_TODOS_CAMPOS = {
            CAMPO_ID,
            CAMPO_DT_INICIO ,
            CAMPO_LATITUDE_INICIAL,
            CAMPO_LONGITUDE_INICIAL,
            CAMPO_DT_FIM,
            CAMPO_LATITUDE_FINAL,
            CAMPO_LONGITUDE_FINAL,
            CAMPO_DETALHES,
            CAMPO_STATUS,
            CAMPO_RELACAO_AGENDA
    };

    /**
     * Cópia dos campos {@link #PROJECAO_TODOS_CAMPOS} mas exclui o campo
     * {@link #CAMPO_RELACAO_AGENDA}.
     *
     * Criado para ser usados nos métodos {@link #consultar(Integer)} e
     * {@link #listar(String, String)}.
     */
    private static final String[] PROJECAO_SEM_CAMPO_RELACAO_AGENDA =
            Arrays.copyOfRange(PROJECAO_TODOS_CAMPOS, 0, PROJECAO_TODOS_CAMPOS.length - 1);

    @NonNull private Banco mBanco;

    /**
     * Construtor.
     *
     * @param context contexto para inicializar o banco.
     */
    public VisitaDAO(@NonNull Context context)
    {
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
        Preconditions.checkNotNull(visita.getDataInicio(), "dataInicio não pode ser nulo");
        Preconditions.checkNotNull(visita.getLatInicial(), "latInicial não pode ser nula");
        Preconditions.checkNotNull(visita.getLongInicial(), "longInicial não pode ser nula");
        Preconditions.checkNotNull(visita.getAgenda(), "agenda não pode ser nula");
        Preconditions.checkNotNull(visita.getAgenda().getId_agenda(), "idAgenda não pode ser nulo");

        ContentValues valores = new ContentValues();
        valores.put(CAMPO_DT_INICIO, visita.getDataInicio());
        valores.put(CAMPO_LATITUDE_INICIAL, visita.getLatInicial());
        valores.put(CAMPO_LONGITUDE_INICIAL, visita.getLongInicial());
        valores.put(CAMPO_RELACAO_AGENDA, visita.getAgenda().getId_agenda());
        valores.put(CAMPO_STATUS, visita.getStatus());

        if (visita.getDetalhes() != null && visita.getDetalhes().length() > 0) {
            valores.put(CAMPO_DETALHES, visita.getDetalhes());
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
        Preconditions.checkNotNull(visita.getId(), "id não pode ser nulo");
        Preconditions.checkNotNull(visita.getDataInicio(), "dataInicio não pode ser nulo");
        Preconditions.checkNotNull(visita.getLatInicial(), "latInicial não pode ser nula");
        Preconditions.checkNotNull(visita.getLongInicial(), "longInicial não pode ser nula");

        Preconditions.checkNotNull(visita.getAgenda(), "agenda não pode ser nula");
        Preconditions.checkNotNull(visita.getAgenda().getId_agenda(), "idAgenda não pode ser nulo");

        Preconditions.checkNotNull(visita.getDataFim(), "dataFim não pode ser nula");
        Preconditions.checkNotNull(visita.getLatFinal(), "latFinal não pode ser nula");
        Preconditions.checkNotNull(visita.getLongFinal(), "longFinal não pode ser nula");

        ContentValues valores = new ContentValues();
        valores.put(CAMPO_DT_FIM, visita.getDataFim());
        valores.put(CAMPO_LATITUDE_FINAL, visita.getLatFinal());
        valores.put(CAMPO_LONGITUDE_FINAL, visita.getLongFinal());
        valores.put(CAMPO_STATUS, visita.getStatus());

        if (visita.getDetalhes() != null && visita.getDetalhes().length() > 0) {
            valores.put(CAMPO_DETALHES, visita.getDetalhes());
        }

        final String where = CAMPO_ID +" = ?";
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
                cursor.getColumnIndex(CAMPO_ID)));
        visita.setDataInicio(cursor.getLong(
                cursor.getColumnIndex(CAMPO_DT_INICIO)));
        visita.setLatInicial(cursor.getDouble(
                cursor.getColumnIndex(CAMPO_LATITUDE_INICIAL)));
        visita.setLongInicial(cursor.getDouble(
                cursor.getColumnIndex(CAMPO_LONGITUDE_INICIAL)));
        visita.setDataFim(cursor.getLong(
                cursor.getColumnIndex(CAMPO_DT_FIM)));
        visita.setLatFinal(cursor.getDouble(
                cursor.getColumnIndex(CAMPO_LATITUDE_FINAL)));
        visita.setLongFinal(cursor.getDouble(
                cursor.getColumnIndex(CAMPO_LONGITUDE_FINAL)));
        visita.setDetalhes(cursor.getString(
                cursor.getColumnIndex(CAMPO_DETALHES)));
        visita.setStatus(cursor.getInt(
                cursor.getColumnIndex(CAMPO_STATUS)));

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
        if ((start != null && start.length() > 0) &&
                (end != null && end.length() > 0)) {
            limit = start + "," + end;
        }

        try {
            mBanco.AbrirConexao();

            return mBanco.db()
                    .query(
                            TABELA_VISITA,
                            PROJECAO_SEM_CAMPO_RELACAO_AGENDA,
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

    /**
     * Consulta e recupera os dados da visita pelo {@code idVisita} provido. O {@code idVisita}
     * não deve ser {@code null} nem ser {@code idVisita <= 0}.<br><br>
     * Esta consulta não recupera os dados da relação a entidade {@link Agenda}, ou seja não
     * chama faz chamada ao método {@link Visita#setAgenda(Agenda)}.<br><br>
     * Esta consulta poderá retornar {@code null}.
     *
     * @param idVisita o código da visita.
     * @return a visita consultada no banco de dados
     */
    public @Nullable Visita consultar(@NonNull Integer idVisita) {
        Preconditions.checkNotNull(idVisita, "idVisita não deve ser nulo");
        Preconditions.checkState(idVisita > 0, "idVisita não deve ser menor que zero");

        final String where = CAMPO_ID +" = ?";
        final String [] whereById = new String [] { String.valueOf(idVisita) };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null ? toSingleEntity(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
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
}