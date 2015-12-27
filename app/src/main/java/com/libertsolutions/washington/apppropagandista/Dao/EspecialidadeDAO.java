package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Model.Status;

/**
 * Classe de acesso aos dados de {@link Especialidade}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class EspecialidadeDAO extends DAOGenerico<Especialidade> {
    static final String TABELA_ESPECIALIDADE = "Especialidade";
    public static final String COLUNA_ID_ESPECIALIDADE = "id_especialidade";
    private static final String COLUNA_NOME = "nome";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_ESPECIALIDADE,
            COLUNA_NOME,
            COLUNA_STATUS,
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + TABELA_ESPECIALIDADE + "("  +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_ID_ESPECIALIDADE + " INTEGER, " +
                    COLUNA_NOME + " TEXT, " +
                    COLUNA_STATUS + " INTEGER, " +
            " UNIQUE (" + COLUNA_ID_ESPECIALIDADE + ") ON CONFLICT IGNORE);";

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public EspecialidadeDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_ESPECIALIDADE;
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
    protected Especialidade fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) return null;

        final Especialidade especialidade = new Especialidade();
        especialidade.setId(
                cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        especialidade.setIdEspecialidade(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID_ESPECIALIDADE)));
        especialidade.setNome(
                cursor.getString(cursor.getColumnIndex(COLUNA_NOME)));
        especialidade.setStatus(
                Status.fromOrdinal(cursor.getInt(cursor.getColumnIndex(COLUNA_STATUS))));
        return especialidade;
    }

    @Override
    public long incluir(@NonNull Especialidade especialidade) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(especialidade, "especialidade não pode ser nula");
        Preconditions.checkNotNull(especialidade.getNome(),
                "especialidade.getNome() não pode ser nulo");

        if (especialidade.getStatus() == Status.Importado ) {
            Preconditions.checkNotNull(especialidade.getIdEspecialidade(),
                    "especialidade.getIdEspecialidade() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (especialidade.getIdEspecialidade() != null) {
            valores.put(COLUNA_ID_ESPECIALIDADE, especialidade.getIdEspecialidade());
        }

        valores.put(COLUNA_NOME, especialidade.getNome());

        valores.put(COLUNA_STATUS, especialidade.getStatus() == null ?
                Status.Pendente.ordinal() : especialidade.getStatus().ordinal());

        return mDatabase.insert(TABELA_ESPECIALIDADE, null, valores);
    }

    @Override
    public int alterar(@NonNull Especialidade especialidade) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(especialidade, "especialidade não pode ser nula");
        Preconditions.checkNotNull(especialidade.getNome(),
                "especialidade.getNome() não pode ser nulo");

        Preconditions.checkNotNull(especialidade.getStatus(),
                "especialidade.getStatus() não pode ser nula");

        if (especialidade.getStatus() == Status.Enviado ||
                especialidade.getStatus() == Status.Importado) {
            Preconditions.checkNotNull(especialidade.getIdEspecialidade(),
                    "especialidade.getIdEspecialidade() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (especialidade.getStatus() == Status.Enviado
                || especialidade.getStatus() == Status.Importado) {
            valores.put(COLUNA_ID_ESPECIALIDADE, especialidade.getIdEspecialidade());
        }

        valores.put(COLUNA_NOME, especialidade.getNome());

        valores.put(COLUNA_STATUS,
                especialidade.getStatus() == Status.Enviado
                        || especialidade.getStatus() == Status.Importado ?
                        especialidade.getStatus().ordinal() : Status.Pendente.ordinal());

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(especialidade.getId()) };

        return mDatabase.update(TABELA_ESPECIALIDADE, valores, where, whereById);
    }
}
