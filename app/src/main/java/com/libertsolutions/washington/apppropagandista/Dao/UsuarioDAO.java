package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Model.Usuario;

public class UsuarioDAO extends DAOGenerico<Usuario> {
    private static final String LOG = UsuarioDAO.class.getSimpleName();

    static final String TABELA_USUARIO = "Usuario";
    static final String COLUNA_ID_USUARIO = "id_usuario";
    private static final String COLUNA_NOME = "nome";
    private static final String COLUNA_CPF = "cpf";
    private static final String COLUNA_EMAIL = "email";
    private static final String COLUNA_SENHA = "senha";

    private static final String[] PROJECAO_TODAS_COLUNAS = {
            COLUNA_ID,
            COLUNA_ID_USUARIO,
            COLUNA_NOME,
            COLUNA_CPF,
            COLUNA_EMAIL,
            COLUNA_SENHA,
            COLUNA_STATUS,
    };

    static final String SCRIPT_CRIACAO =
            "CREATE TABLE " + TABELA_USUARIO + "("  +
                    COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUNA_ID_USUARIO + " INTEGER, " +
                    COLUNA_NOME + " TEXT not null, " +
                    COLUNA_CPF + " TEXT not null, " +
                    COLUNA_EMAIL + " TEXT not null, " +
                    COLUNA_SENHA + " TEXT not null, " +
                    COLUNA_STATUS + " INTEGER, " +
                    " UNIQUE (" + COLUNA_ID_USUARIO + ") ON CONFLICT IGNORE);";

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public UsuarioDAO(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected String nomeTabela() {
        return TABELA_USUARIO;
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
    protected Usuario fromCursor(@NonNull Cursor cursor) {
        if (!isCursorOpenedAndPrepared(cursor)) {
            return null;
        }

        final Usuario usuario = new Usuario();
        usuario.setId(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID)));
        usuario.setIdUsuario(
                cursor.getInt(cursor.getColumnIndex(COLUNA_ID_USUARIO)));
        usuario.setNome(
                cursor.getString(cursor.getColumnIndex(COLUNA_NOME)));
        usuario.setCpf(
                cursor.getString(cursor.getColumnIndex(COLUNA_CPF)));
        usuario.setEmail(
                cursor.getString(cursor.getColumnIndex(COLUNA_EMAIL)));
        usuario.setSenha(
                cursor.getString(cursor.getColumnIndex(COLUNA_SENHA)));
        usuario.setStatus(
                Status.fromOrdinal(cursor.getInt(cursor.getColumnIndex(COLUNA_STATUS))));
        return usuario;
    }

    @Override
    public long incluir(@NonNull Usuario usuario) {
        // Pré-condições para realizar a transação na tabela destino
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(usuario, "usuario não pode ser nula");
        Preconditions.checkNotNull(usuario.getNome(),
                "usuario.getNome() não pode ser nulo");
        Preconditions.checkNotNull(usuario.getCpf(),
                "usuario.getCpf() não pode ser nulo");
        Preconditions.checkNotNull(usuario.getEmail(),
                "usuario.getEmail() não pode ser nulo");
        Preconditions.checkNotNull(usuario.getSenha(),
                "usuario.getSenha() não pode ser nulo");

        if (usuario.getStatus() == Status.Importado ) {
            Preconditions.checkNotNull(usuario.getIdUsuario(),
                    "usuario.getIdUsuario() não pode ser nulo");
        }

        ContentValues valores = new ContentValues();

        if (usuario.getIdUsuario() != null) {
            valores.put(COLUNA_ID_USUARIO, usuario.getIdUsuario());
        }

        valores.put(COLUNA_NOME, usuario.getNome());
        valores.put(COLUNA_CPF, usuario.getCpf());
        valores.put(COLUNA_EMAIL, usuario.getEmail());
        valores.put(COLUNA_SENHA, usuario.getSenha());

        valores.put(COLUNA_STATUS, usuario.getStatus() == null ?
                Status.Pendente.ordinal() : usuario.getStatus().ordinal());

        return mDatabase.insert(TABELA_USUARIO, null, valores);
    }

    @Override
    public int alterar(@NonNull Usuario entidade) {
        Log.i(LOG, "Método alterar() não implementado.");
        return 0;
    }

    public @Nullable Usuario consultar(@NonNull String email) {
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        Preconditions.checkNotNull(email, "email não deve ser nulo");

        final String where = COLUNA_EMAIL +" = ?";
        final String [] whereById = new String [] { email };

        Cursor cursor = null;
        try {
            cursor = query(where, whereById, null, null);
            return cursor != null ? toSingleEntity(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }
}
