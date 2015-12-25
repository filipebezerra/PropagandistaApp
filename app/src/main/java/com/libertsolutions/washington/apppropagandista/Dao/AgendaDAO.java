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
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso aos dados de {@link Agenda}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 25/12/2015
 * @since 1.0
 */
public class AgendaDAO {
    private static final String TABELA_AGENDA = "Agenda";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_ID_AGENDA = "id_agenda";
    public static final String COLUNA_DT_COMPROMISSO = "dt_compromisso";
    public static final String COLUNA_OBSERVACAO = "observacao";
    public static final String COLUNA_STATUS_AGENDA = "status_agenda";
    public static final String COLUNA_STATUS = "status";
    public static final String COLUNA_RELACAO_MEDICO = "id_medico";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_AGENDA,
            COLUNA_DT_COMPROMISSO,
            COLUNA_OBSERVACAO,
            COLUNA_STATUS_AGENDA,
            COLUNA_STATUS,
            COLUNA_RELACAO_MEDICO
    };

    @NonNull private Banco mBanco;

    /**
     * Construtor.
     *
     * @param context contexto para inicializar o banco.
     */
    public AgendaDAO(Context context) {
        mBanco = Banco.getInstance(context);
    }

    /**
     * Inclui uma {@link Agenda} e retorna o código interno atribuído para o registro.
     *
     * @param agenda a entidade com campos requeridos para inserção preenchidos.
     * @return o ID do registro inserido.
     */
    public long incluir(@NonNull Agenda agenda) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkNotNull(agenda, "agenda não pode ser nula");
        Preconditions.checkNotNull(agenda.getDataCompromisso(),
                "agenda.getDataCompromisso() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getIdMedico(),
                "agenda.getIdMedico() não pode ser nula");
        Preconditions.checkNotNull(agenda.getStatusAgenda(),
                "agenda.getStatusAgenda() não pode ser nula");
        Preconditions.checkState((
                        agenda.getStatus() != null
                                && agenda.getStatus() == Status.Importado
                                && agenda.getIdAgenda() == null),
                "agenda.getIdAgenda() não pode ser nulo");

        ContentValues valores = new ContentValues();

        if (agenda.getIdAgenda() != null) {
            valores.put(COLUNA_ID_AGENDA, agenda.getIdMedico());
        }

        valores.put(COLUNA_DT_COMPROMISSO, agenda.getDataCompromisso());

        if (agenda.getObservacao() != null && !agenda.getObservacao().isEmpty()) {
            valores.put(COLUNA_OBSERVACAO, agenda.getObservacao());
        }

        valores.put(COLUNA_RELACAO_MEDICO, agenda.getIdMedico());
        valores.put(COLUNA_STATUS_AGENDA, agenda.getStatusAgenda().ordinal());
        valores.put(COLUNA_STATUS, agenda.getStatus() == null ?
                Status.Pendente.ordinal() : agenda.getStatusAgenda().ordinal());

        try {
            mBanco.AbrirConexao();
            return mBanco.db()
                    .insert(TABELA_AGENDA, null, valores);
        } finally {
            mBanco.close();
        }
    }

    /**
     * Altera a {@link Agenda} retornando a quantidade de registros afetados.
     *
     * @param agenda a entidade com campos requeridos para alteração preenchidos.
     * @return a quantidade de registros alterados.
     */
    public int alterar(@NonNull Agenda agenda) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkNotNull(agenda, "agenda não pode ser nula");
        Preconditions.checkNotNull(agenda.getId(),
                "agenda.getId() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getDataCompromisso(),
                "agenda.getDataCompromisso() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getIdMedico(),
                "agenda.getIdMedico() não pode ser nula");
        Preconditions.checkNotNull(agenda.getStatusAgenda(),
                "agenda.getStatusAgenda() não pode ser nula");
        Preconditions.checkNotNull(agenda.getStatus(),
                "agenda.getStatus() não pode ser nula");
        Preconditions.checkState(
                ((agenda.getStatus() == Status.Enviado
                        || agenda.getStatus() == Status.Importado)
                        && agenda.getIdAgenda() == null),
                "agenda.getIdAgenda() não pode ser nulo");

        ContentValues valores = new ContentValues();

        if (agenda.getStatus() == Status.Enviado || agenda.getStatus() == Status.Importado) {
            valores.put(COLUNA_ID_AGENDA, agenda.getIdMedico());
        }

        valores.put(COLUNA_DT_COMPROMISSO, agenda.getDataCompromisso());

        if (agenda.getObservacao() != null && !agenda.getObservacao().isEmpty()) {
            valores.put(COLUNA_OBSERVACAO, agenda.getObservacao());
        }

        valores.put(COLUNA_RELACAO_MEDICO, agenda.getIdMedico());
        valores.put(COLUNA_STATUS_AGENDA, agenda.getStatusAgenda().ordinal());

        valores.put(COLUNA_STATUS,
                agenda.getStatus() == Status.Enviado
                        || agenda.getStatus() == Status.Importado ?
                        agenda.getStatus().ordinal() : Status.Pendente.ordinal());

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(agenda.getId()) };

        try {
            mBanco.AbrirConexao();
            return mBanco.db()
                    .update(TABELA_AGENDA, valores, where, whereById);
        } finally {
            mBanco.close();
        }
    }

    public @Nullable Agenda consultar(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "id não deve ser nulo");
        Preconditions.checkState(id > 0, "id não deve ser menor que zero");

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] { String.valueOf(id) };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null ? toSingleEntity(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    public @Nullable List<Agenda> listar(@Nullable String start, @Nullable String end) {
        Cursor cursor = null;
        try {
            cursor = query(null, null, start, end);
            return cursor != null ? toEntityList(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    public List<Agenda> listar(String start, String end, String filter, String... args) {
        Cursor cursor = null;
        try {
            cursor = query(filter, args, start, end);
            return cursor != null ? toEntityList(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    public List<Agenda> listar(Status status) {
        final String where = COLUNA_STATUS + " = ?";
        final String [] whereById = new String [] { String.valueOf(status.ordinal()) };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null ? toEntityList(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    public boolean existe(Integer idAgenda) {
        Preconditions.checkNotNull(idAgenda, "idAgenda não deve ser nulo");
        Preconditions.checkState(idAgenda > 0, "idAgenda não deve ser menor que zero");

        final String where = COLUNA_ID_AGENDA +" = ?";
        final String [] whereById = new String [] { String.valueOf(idAgenda) };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null && toSingleEntity(cursor) != null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Recupera uma {@link Agenda} com todos os dados nas colunas fornecidas no
     * momento da consulta ao banco de dados.
     *
     * @param cursor o {@code cursor} obtido por uma chamada ao método {@link #query(String, String[], String, String)}.
     * @return uma {@code Agenda} com todas colunas lidas, poderá ser {@code null}.
     * @see #toSingleEntity(Cursor)
     * @see #toEntityList(Cursor)
     */
    private @Nullable Agenda fromCursor(@NonNull Cursor cursor) {
        if (cursor.isClosed()) {
            return null;
        }

        if (cursor.isBeforeFirst() && !cursor.moveToFirst()) {
            return null;
        }

        Agenda agenda = new Agenda();

        agenda.setId(cursor.getInt(
                cursor.getColumnIndex(COLUNA_ID)));
        agenda.setIdAgenda(cursor.getInt(
                cursor.getColumnIndex(COLUNA_ID_AGENDA)));
        agenda.setDataCompromisso(cursor.getLong(
                cursor.getColumnIndex(COLUNA_DT_COMPROMISSO)));
        agenda.setObservacao(cursor.getString(
                cursor.getColumnIndex(COLUNA_OBSERVACAO)));
        agenda.setStatusAgenda(StatusAgenda.fromOrdinal(cursor.getInt(
                cursor.getColumnIndex(COLUNA_STATUS_AGENDA))));
        agenda.setStatus(Status.fromOrdinal(cursor.getInt(
                cursor.getColumnIndex(COLUNA_STATUS))));
        agenda.setIdMedico(cursor.getInt(
                cursor.getColumnIndex(COLUNA_RELACAO_MEDICO)));
        agenda.setStatus(Status.fromOrdinal(cursor.getInt(
                cursor.getColumnIndex(COLUNA_STATUS))));

        return agenda;
    }

    /**
     * Obtém todo o conjunto de registros existentes no {@code cursor} se houver.
     * @param cursor o {@code cursor} obtido da chamada ao método {@link #query(String, String[], String, String)}.
     * @return uma {@link List <Agenda>} obtidos do cursor {@code cursor}, poderá ser {@code null}.
     */
    private @Nullable List<Agenda> toEntityList(@NonNull Cursor cursor) {
        List<Agenda> list = new ArrayList<>();

        Agenda agenda;
        do {
            agenda = fromCursor(cursor);

            if (agenda != null) {
                list.add(agenda);
            }
        } while(cursor.moveToNext());

        return list;
    }

    /**
     * Obtém o primeiro resultado do {@code cursor} se este conter registros.
     *
     * @param cursor o {@code cursor} obtido da chamada ao método {@link #query(String, String[], String, String)}.
     * @return a {@link Agenda} com dados obtidos do {@code cursor}, poderá ser {@code null}.
     */
    private @Nullable Agenda toSingleEntity(@NonNull Cursor cursor) {
        return fromCursor(cursor);
    }

    //TODO (filipe): mover para a classe Banco.
    /**
     * Realiza uma consulta na tabela {@link #TABELA_AGENDA} usando os argumentos
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
                            TABELA_AGENDA,
                            PROJECAO_TODAS_COLUNAS,
                            where,
                            arguments,
                            null,
                            COLUNA_DT_COMPROMISSO,
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
