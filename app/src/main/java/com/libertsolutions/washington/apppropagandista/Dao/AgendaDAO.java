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

/**
 * Classe de acesso aos dados de {@link Agenda}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 25/12/2015
 * @since 1.0
 */
public class AgendaDAO extends DAOGenerico<Agenda> {
    static final String TABELA_AGENDA = "Agenda";
    static final String COLUNA_ID_AGENDA = "id_agenda";
    private static final String COLUNA_DT_COMPROMISSO = "dt_compromisso";
    private static final String COLUNA_OBSERVACAO = "observacao";
    private static final String COLUNA_STATUS_AGENDA = "status_agenda";
    private static final String COLUNA_RELACAO_MEDICO = "id_medico";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_AGENDA,
            COLUNA_DT_COMPROMISSO,
            COLUNA_OBSERVACAO,
            COLUNA_STATUS_AGENDA,
            COLUNA_STATUS,
            COLUNA_RELACAO_MEDICO
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + AgendaDAO.TABELA_AGENDA + "("  +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_ID_AGENDA + " INTEGER," +
                    COLUNA_DT_COMPROMISSO + " INTEGER not null, " +
                    COLUNA_OBSERVACAO + " TEXT," +
                    COLUNA_STATUS_AGENDA + " INTEGER, " +
                    COLUNA_STATUS + " INTEGER, " +
                    COLUNA_RELACAO_MEDICO + " INTEGER not null, " +
            " FOREIGN KEY (" + COLUNA_RELACAO_MEDICO +
                ") REFERENCES " + MedicoDAO.TABELA_MEDICO +
                " (" + MedicoDAO.COLUNA_ID_MEDICO + "), " +
            " UNIQUE (" + COLUNA_ID_AGENDA + ") ON CONFLICT REPLACE);";

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public AgendaDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_AGENDA;
    }

    @NonNull
    @Override
    protected String[] projecaoTodasColunas() {
        return PROJECAO_TODAS_COLUNAS;
    }

    @Nullable
    @Override
    protected String colunasOrdenacao() {
        return COLUNA_DT_COMPROMISSO;
    }

    @Nullable
    @Override
    protected Agenda fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) return null;

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

    @Override
    public long incluir(@NonNull Agenda agenda) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(agenda, "agenda não pode ser nula");
        Preconditions.checkNotNull(agenda.getDataCompromisso(),
                "agenda.getDataCompromisso() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getIdMedico(),
                "agenda.getIdMedico() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getStatus(),
                "agenda.getStatus() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getStatusAgenda(),
                "agenda.getStatusAgenda() não pode ser nulo");
        Preconditions.checkState(
                agenda.getStatus() != Status.Importado || agenda.getIdAgenda() == null,
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

        return mDatabase.insert(TABELA_AGENDA, null, valores);
    }

    @Override
    public int alterar(@NonNull Agenda agenda) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

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

            if (agenda.getStatus() == Status.Importado) {
                valores.put(COLUNA_RELACAO_MEDICO, agenda.getIdMedico());
            }
        }

        valores.put(COLUNA_DT_COMPROMISSO, agenda.getDataCompromisso());

        if (agenda.getObservacao() != null && !agenda.getObservacao().isEmpty()) {
            valores.put(COLUNA_OBSERVACAO, agenda.getObservacao());
        }

        valores.put(COLUNA_STATUS_AGENDA, agenda.getStatusAgenda().ordinal());

        valores.put(COLUNA_STATUS,
                agenda.getStatus() == Status.Enviado
                        || agenda.getStatus() == Status.Importado ?
                        agenda.getStatus().ordinal() : Status.Pendente.ordinal());

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(agenda.getId()) };

        return mDatabase.update(TABELA_AGENDA, valores, where, whereById);
    }
}