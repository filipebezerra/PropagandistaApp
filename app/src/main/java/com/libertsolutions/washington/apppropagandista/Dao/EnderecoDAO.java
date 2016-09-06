package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.libertsolutions.washington.apppropagandista.Model.Endereco;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import java.util.List;

/**
 * Classe de acesso aos dados de {@link Endereco}. Esta classe contém todas operações
 * que necessitam de comunicação e transação com banco de dados local(SQLite).
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class EnderecoDAO extends DAOGenerico<Endereco> {
    static final String TABELA_ENDERECO = "Endereco";
    static final String COLUNA_ID_ENDERECO = "id_endereco";
    private static final String COLUNA_ENDERECO = "endereco";
    private static final String COLUNA_CEP = "cep";
    private static final String COLUNA_NUMERO = "numero";
    private static final String COLUNA_BAIRRO = "bairro";
    private static final String COLUNA_COMPLEMENTO = "complemento";
    private static final String COLUNA_LONGITUDE = "longitude";
    private static final String COLUNA_LATITUDE = "latitude";
    private static final String COLUNA_OBSERVACAO = "observacao";
    private static final String COLUNA_RELACAO_MEDICO = "id_medico";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_ENDERECO,
            COLUNA_ENDERECO,
            COLUNA_CEP,
            COLUNA_NUMERO,
            COLUNA_BAIRRO,
            COLUNA_COMPLEMENTO,
            COLUNA_LONGITUDE,
            COLUNA_LATITUDE,
            COLUNA_OBSERVACAO,
            COLUNA_STATUS,
            COLUNA_RELACAO_MEDICO
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + TABELA_ENDERECO + " (" +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_ID_ENDERECO + " INTEGER, " +
                    COLUNA_ENDERECO + " TEXT not null, " +
                    COLUNA_CEP + " TEXT, " +
                    COLUNA_NUMERO + " TEXT, " +
                    COLUNA_BAIRRO + " TEXT not null, "+
                    COLUNA_COMPLEMENTO + " TEXT, " +
                    COLUNA_LONGITUDE + " TEXT, " +
                    COLUNA_LATITUDE + " TEXT, " +
                    COLUNA_OBSERVACAO + " TEXT, " +
                    COLUNA_STATUS + " INTEGER, " +
                    COLUNA_RELACAO_MEDICO + " INTEGER not null, " +
            " FOREIGN KEY (" + COLUNA_RELACAO_MEDICO +
                ") REFERENCES " + MedicoDAO.TABELA_MEDICO +
                " (" + MedicoDAO.COLUNA_ID_MEDICO + "), " +
            " UNIQUE (" + COLUNA_ID_ENDERECO + ") ON CONFLICT IGNORE);";

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public EnderecoDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_ENDERECO;
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
    protected Endereco fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) return null;

        final Endereco endereco = new Endereco();
        endereco.setId(
                cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        endereco.setIdEndereco(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID_ENDERECO)));
        endereco.setEndereco(
                cursor.getString(cursor.getColumnIndex(COLUNA_ENDERECO)));
        endereco.setCep(
                cursor.getString(cursor.getColumnIndex(COLUNA_CEP)));
        endereco.setNumero(
                cursor.getString(cursor.getColumnIndex(COLUNA_NUMERO)));
        endereco.setBairro(
                cursor.getString(cursor.getColumnIndex(COLUNA_BAIRRO)));
        endereco.setComplemento(
                cursor.getString(cursor.getColumnIndex(COLUNA_COMPLEMENTO)));
        endereco.setLongitude(
                cursor.getString(cursor.getColumnIndex(COLUNA_LONGITUDE)));
        endereco.setLatitude(
                cursor.getString(cursor.getColumnIndex(COLUNA_LATITUDE)));
        endereco.setObservacao(
                cursor.getString(cursor.getColumnIndex(COLUNA_OBSERVACAO)));
        endereco.setStatus(
                Status.fromOrdinal(cursor.getInt(cursor.getColumnIndex(COLUNA_STATUS))));
        endereco.setIdMedico(
                cursor.getInt(cursor.getColumnIndex(COLUNA_RELACAO_MEDICO)));
        return endereco;
    }

    @Override
    public long incluir(@NonNull Endereco endereco) {
        // Pré-condições para realizar a transação na tabela destino
        Utils.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Utils.checkNotNull(endereco, "endereco não pode ser nula");
        Utils.checkNotNull(endereco.getEndereco(),
                "endereco.getEndereco() não pode ser nulo");
        Utils.checkNotNull(endereco.getBairro(),
                "endereco.getBairro() não pode ser nula");
        Utils.checkNotNull(endereco.getIdMedico(),
                "endereco.getIdMedico() não pode ser nula");

        if (endereco.getStatus() == Status.Importado ) {
            Utils.checkNotNull(endereco.getIdEndereco(),
                    "endereco.getIdEndereco() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (endereco.getIdEndereco() != null) {
            valores.put(COLUNA_ID_ENDERECO, endereco.getIdMedico());
        }

        valores.put(COLUNA_ENDERECO, endereco.getEndereco());
        valores.put(COLUNA_CEP, endereco.getCep());
        valores.put(COLUNA_NUMERO, endereco.getNumero());
        valores.put(COLUNA_BAIRRO, endereco.getBairro());
        valores.put(COLUNA_COMPLEMENTO, endereco.getComplemento());
        valores.put(COLUNA_LONGITUDE, endereco.getLongitude());
        valores.put(COLUNA_LATITUDE, endereco.getLatitude());
        valores.put(COLUNA_OBSERVACAO, endereco.getObservacao());

        valores.put(COLUNA_STATUS, endereco.getStatus() == null ?
                Status.Pendente.ordinal() : endereco.getStatus().ordinal());

        valores.put(COLUNA_RELACAO_MEDICO, endereco.getIdMedico());

        return mDatabase.insert(TABELA_ENDERECO, null, valores);
    }

    @Override
    public int alterar(@NonNull Endereco endereco) {
        // Pré-condições para realizar a transação na tabela destino
        Utils.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Utils.checkNotNull(endereco, "endereco não pode ser nula");
        Utils.checkNotNull(endereco.getEndereco(),
                "endereco.getEndereco() não pode ser nulo");
        Utils.checkNotNull(endereco.getBairro(),
                "endereco.getBairro() não pode ser nula");
        Utils.checkNotNull(endereco.getIdMedico(),
                "endereco.getIdMedico() não pode ser nula");

        Utils.checkNotNull(endereco.getStatus(),
                "endereco.getStatus() não pode ser nula");

        if (endereco.getStatus() == Status.Enviado ||
                endereco.getStatus() == Status.Importado) {
            Utils.checkNotNull(endereco.getIdEndereco(),
                    "endereco.getIdEndereco() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (endereco.getStatus() == Status.Enviado || endereco.getStatus() == Status.Importado) {
            valores.put(COLUNA_ID_ENDERECO, endereco.getIdMedico());

            if (endereco.getStatus() == Status.Importado) {
                valores.put(COLUNA_RELACAO_MEDICO, endereco.getIdMedico());
            }
        }

        valores.put(COLUNA_ENDERECO, endereco.getEndereco());
        valores.put(COLUNA_CEP, endereco.getCep());
        valores.put(COLUNA_NUMERO, endereco.getNumero());
        valores.put(COLUNA_BAIRRO, endereco.getBairro());
        valores.put(COLUNA_COMPLEMENTO, endereco.getComplemento());
        valores.put(COLUNA_LONGITUDE, endereco.getLongitude());
        valores.put(COLUNA_LATITUDE, endereco.getLatitude());
        valores.put(COLUNA_OBSERVACAO, endereco.getObservacao());

        valores.put(COLUNA_STATUS,
                endereco.getStatus() == Status.Enviado
                        || endereco.getStatus() == Status.Importado ?
                        endereco.getStatus().ordinal() : Status.Pendente.ordinal());

        final String where = COLUNA_ID +" = ?";
        final String [] whereById = new String [] {
                String.valueOf(endereco.getId()) };

        return mDatabase.update(TABELA_ENDERECO, valores, where, whereById);
    }

    /**
     * Consulta por todos endereços no banco de dados que correspondem ao {@code medico}
     * especificado e os retornam.
     *
     * @param medico a ser consultado.
     * @return o conjunto de entidades que correspondem.
     */
    public @Nullable List<Endereco> listar(@NonNull Medico medico) {
        Utils.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Utils.checkNotNull(medico, "medico não deve ser nulo");
        Utils.checkNotNull(medico.getIdMedico(),
                "medico.getIdMedico() não deve ser nulo");
        Utils.checkState(medico.getIdMedico() > 0,
                "medico.getIdMedico() não deve ser menor que zero");

        final String where = COLUNA_RELACAO_MEDICO +" = ?";
        final String [] whereById = new String [] { String.valueOf(medico.getIdMedico()) };

        return toEntityList(query(where, whereById, null, null));
    }
}
