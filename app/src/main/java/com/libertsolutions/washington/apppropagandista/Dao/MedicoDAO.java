package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import java.util.List;

/**
 * Classe de acesso aos dados de {@link Medico}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */

public class MedicoDAO extends DAOGenerico<Medico> {
    static final String TABELA_MEDICO = "Medico";
    static final String COLUNA_ID_MEDICO = "id_medico";
    private static final String COLUNA_NOME = "nome";
    private static final String COLUNA_DATA_ANIVERSARIO = "dt_aniversario";
    private static final String COLUNA_SECRETARIA = "secretaria";
    private static final String COLUNA_TELEFONE = "telefone";
    private static final String COLUNA_EMAIL = "email";
    private static final String COLUNA_CRM = "crm";
    private static final String COLUNA_RELACAO_ESPECIALIDADE = "id_especialidade";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_MEDICO,
            COLUNA_NOME,
            COLUNA_DATA_ANIVERSARIO,
            COLUNA_SECRETARIA,
            COLUNA_TELEFONE,
            COLUNA_EMAIL,
            COLUNA_CRM,
            COLUNA_STATUS,
            COLUNA_RELACAO_ESPECIALIDADE
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + TABELA_MEDICO + " (" +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_ID_MEDICO + " INTEGER, " +
                    COLUNA_NOME + " TEXT not null, " +
                    COLUNA_DATA_ANIVERSARIO + " LONG, "+
                    COLUNA_SECRETARIA + " TEXT, " +
                    COLUNA_TELEFONE + " TEXT not null, " +
                    COLUNA_EMAIL + " TEXT, " +
                    COLUNA_CRM + " TEXT, " +
                    COLUNA_STATUS + " INTEGER, " +
                    COLUNA_RELACAO_ESPECIALIDADE + " INTEGER not null, " +
            " FOREIGN KEY (" + COLUNA_RELACAO_ESPECIALIDADE +
                ") REFERENCES " + EspecialidadeDAO.TABELA_ESPECIALIDADE +
                " (" + EspecialidadeDAO.COLUNA_ID_ESPECIALIDADE + "), " +
            " UNIQUE (" + COLUNA_ID_MEDICO + ") ON CONFLICT IGNORE);";


    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public MedicoDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_MEDICO;
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
    protected Medico fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) return null;

        final Medico medico = new Medico();

        medico.setId(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID)));
        medico.setIdMedico(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID_MEDICO)));
        medico.setNome(
                cursor.getString(cursor.getColumnIndex(COLUNA_NOME)));
        medico.setDataAniversario(
                cursor.getLong(cursor.getColumnIndex(COLUNA_DATA_ANIVERSARIO)));
        medico.setSecretaria(
                cursor.getString(cursor.getColumnIndex(COLUNA_SECRETARIA)));
        medico.setTelefone(
                cursor.getString(cursor.getColumnIndex(COLUNA_TELEFONE)));
        medico.setEmail(
                cursor.getString(cursor.getColumnIndex(COLUNA_EMAIL)));
        medico.setCrm(
                cursor.getString(cursor.getColumnIndex(COLUNA_CRM)));
        medico.setStatus(
                Status.fromOrdinal(cursor.getInt(cursor.getColumnIndex(COLUNA_STATUS))));
        medico.setIdEspecialidade(
                cursor.getInt(cursor.getColumnIndex(COLUNA_RELACAO_ESPECIALIDADE)));

        return medico;
    }

    @Override
    public long incluir(@NonNull Medico medico) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(medico, "medico não pode ser nula");
        Preconditions.checkNotNull(medico.getNome(),
                "medico.getNome() não pode ser nulo");
        Preconditions.checkNotNull(medico.getTelefone(),
                "medico.getTelefone() não pode ser nula");
        Preconditions.checkNotNull(medico.getIdEspecialidade(),
                "medico.getIdEspecialidade() não pode ser nula");

        if (medico.getStatus() == Status.Importado ) {
            Preconditions.checkNotNull(medico.getIdMedico(),
                    "medico.getIdMedico() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (medico.getIdMedico() != null) {
            valores.put(COLUNA_ID_MEDICO, medico.getIdMedico());
        }

        valores.put(COLUNA_NOME, medico.getNome());
        valores.put(COLUNA_DATA_ANIVERSARIO, medico.getDataAniversario());
        valores.put(COLUNA_SECRETARIA, medico.getSecretaria());
        valores.put(COLUNA_TELEFONE, medico.getTelefone());

        valores.put(COLUNA_EMAIL, medico.getEmail());
        valores.put(COLUNA_CRM, medico.getCrm());

        valores.put(COLUNA_STATUS, medico.getStatus() == null ?
                Status.Pendente.ordinal() : medico.getStatus().ordinal());

        valores.put(COLUNA_RELACAO_ESPECIALIDADE, medico.getIdEspecialidade());

        return mDatabase.insert(TABELA_MEDICO, null, valores);
    }

    @Override
    public int alterar(@NonNull Medico medico) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(medico, "medico não pode ser nula");
        Preconditions.checkNotNull(medico.getNome(),
                "medico.getNome() não pode ser nulo");
        Preconditions.checkNotNull(medico.getTelefone(),
                "medico.getTelefone() não pode ser nula");
        Preconditions.checkNotNull(medico.getIdEspecialidade(),
                "medico.getIdEspecialidade() não pode ser nula");

        Preconditions.checkNotNull(medico.getStatus(),
                "medico.getStatus() não pode ser nula");

        if (medico.getStatus() == Status.Enviado ||
                medico.getStatus() == Status.Importado) {
            Preconditions.checkNotNull(medico.getIdMedico(),
                    "medico.getIdVisita() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (medico.getStatus() == Status.Enviado || medico.getStatus() == Status.Importado) {
            valores.put(COLUNA_ID_MEDICO, medico.getIdMedico());
        }

        valores.put(COLUNA_NOME, medico.getNome());
        valores.put(COLUNA_DATA_ANIVERSARIO, medico.getDataAniversario());
        valores.put(COLUNA_SECRETARIA, medico.getSecretaria());
        valores.put(COLUNA_TELEFONE, medico.getTelefone());

        valores.put(COLUNA_EMAIL, medico.getEmail());
        valores.put(COLUNA_CRM, medico.getCrm());

        valores.put(COLUNA_RELACAO_ESPECIALIDADE, medico.getIdEspecialidade());

        valores.put(COLUNA_STATUS,
                medico.getStatus() == Status.Enviado
                        || medico.getStatus() == Status.Importado ?
                        medico.getStatus().ordinal() : Status.Pendente.ordinal());

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(medico.getId()) };

        return mDatabase.update(TABELA_MEDICO, valores, where, whereById);
    }

    public @Nullable List<Medico> listar() {
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        return listar(null, null);
    }
}