package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Model.ModeloBase;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstração genérico de objeto de acesso à dados. Esta classe deve ser a base para
 * implementações das demais classes que realizam operações de acesso à dados.
 *
 * @author Filipe Bezerra
 * @version 1.0, 26/12/2015
 * @since 1.0
 */
public abstract class DAOGenerico<T extends ModeloBase> {
    /**
     * Classe utilitária para gerenciar o banco de dados
     */
    @NonNull protected SQLiteHelper mHelper;

    /**
     * Banco de dados
     */
    @Nullable protected SQLiteDatabase mDatabase;

    /**
     * Todas entidades devem conter esta coluna como chave primária.
     *
     * Classes que descendem de {@link ModeloBase} já contém o atributo {@link ModeloBase#mId}
     * que será usado como coluna chave primária da tabela.
     */
    static final String COLUNA_ID = "_id";

    /**
     * Todas entidades devem conter esta coluna como controle interno de sincronização.
     *
     * Classes que descendem de {@link ModeloBase} já contém o atributo {@link ModeloBase#mStatus}
     * que será usado como coluna na tabela.
     */
    static final String COLUNA_STATUS = "status";

    /**
     * Construtor padrão.
     *
     * @param context contexto para inicializar o helper do banco de dados.
     */
    public DAOGenerico(@NonNull Context context) {
        mHelper = new SQLiteHelper(context);
    }

    /**
     * Cria e/ou abre um banco de dados que será usado para escrita.
     *
     * @throws IllegalStateException se o banco de dados foi chamado antes de ser inicializado.
     * @throws SQLiteException se o banco de dados não pode ser aberto para escrita.
     */
    public void openDatabase() throws IllegalStateException, SQLiteException {
        mDatabase = mHelper.getWritableDatabase();
    }

    /**
     * Fecha qualquer banco de dados aberto.
     *
     * @throws IllegalStateException se o banco de dados foi chamado antes de ser inicializado.
     */
    public void closeDatabase() throws IllegalStateException {
        mHelper.close();
    }

    /**
     * Fecha o cursor liberando os recursos alocados.
     *
     * @param cursor O {@code cursor}.
     */
    protected void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Verifica se o {@code cursor} está aberto e se contém registros para serem lidos.<br><br>
     * Este método já coloca o {@code cursor} no primeiro registro se possível.
     *
     * @param cursor o {@link Cursor}.
     * @return se está aberto e contém registros.
     */
    protected boolean isCursorOpenedAndPrepared(@NonNull Cursor cursor) {
        return !cursor.isClosed() && !(cursor.isBeforeFirst() && !cursor.moveToFirst()) &&
                !cursor.isAfterLast();
    }

    /**
     * Obtém a entidade com seus dados lidos do {@code cursor}.
     *
     * @param cursor o {@link Cursor}.
     * @return a entidade obtida do {@code cursor}.
     */
    protected @Nullable T toSingleEntity(@NonNull Cursor cursor) {
        return fromCursor(cursor);
    }

    /**
     * Obtém um conjunto da entidade com seus dados lidos do {@code cursor}.
     *
     * @param cursor o {@link Cursor}.
     * @return o conjunto da entidade obtidos do {@code cursor}.
     */
    protected @Nullable List<T> toEntityList(@NonNull Cursor cursor) {
        List<T> list = new ArrayList<>();

        T entidade;
        do {
            entidade = fromCursor(cursor);

            if (entidade != null) {
                list.add(entidade);
            }
        } while(cursor.moveToNext());

        return list;
    }

    /**
     * Método auxiliar para realizar consultas no banco de dados.<br><br>
     * Para usar este método é necessário abrir o banco de dados antes usando o método {@link #openDatabase()}.
     *
     * @param where quais condições devem ser usadas, poderá ser {@code null} para obter todos registros.
     * @param arguments os argumentos das condições providas, deverá ser {@code null} quando {@code where is null}.
     * @param start determina o início da consulta, é usado em conjunto com {@code end} para formar o limite.
     * @param end determina o fim da consulta, é usado em conjunto com {@code start} para formar o limite.
     * @return o {@link Cursor} com o resultado da consulta.
     */
    protected Cursor query(@Nullable final String where, @Nullable final String [] arguments,
            @Nullable String start, @Nullable String end) {
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

        String limit = null;
        if ((start != null && !start.isEmpty()) && (end != null && !end.isEmpty())) {
            limit = start + "," + end;
        }

        return mDatabase.query(
                nomeTabela(),
                projecaoTodasColunas(),
                where,
                arguments,
                null,
                null,
                colunasOrdenacao(),
                limit);
    }

    /**
     * Consulta se existe um registro na tabela com o {@code id} especificado.
     *
     * @param id a ser consultado.
     * @return se existe registro para o {@code id}.
     */
    public boolean existe(Integer id) {
        return consultar(id) != null;
    }

    /**
     * Consulta por um registro no banco de dados que corresponde ao {@code id} especificado e o
     * retorna.
     *
     * @param id a ser consultado.
     * @return a entidade que corresponde.
     */
    public @Nullable T consultar(@NonNull Integer id) {
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

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

    public @Nullable T consultar(@NonNull String filter, @NonNull String... args) {
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");
        Preconditions.checkNotNull(filter, "filter não pode ser nulo");
        Preconditions.checkNotNull(args, "args não pode ser nulo");

        Cursor cursor = null;
        try {
            cursor = query(filter, args, null, null);
            return cursor != null ? toSingleEntity(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Lista todos registros da tabela dentro do limite especificado.
     *
     * @param start o início da consulta.
     * @param end até que ponto da consulta.
     * @return o conjunto de entidades.
     */
    public @Nullable List<T> listar(@Nullable String start, @Nullable String end) {
        return listar(start, end, null, (String[]) null);
    }

    /**
     * Lista todos registros da tabela dentro do limite especificado usando o {@code filter} e
     * os {@code args} para definir a condição de busca.
     *
     * @param start o início da consulta.
     * @param end até que ponto da consulta.
     * @param filter o filtro da busca.
     * @param args os argumentos usados no filtro da busca.
     * @return o conjunto de entidades.
     */
    public @Nullable List<T> listar(@Nullable String start, @Nullable String end,
            @Nullable String filter, @Nullable String... args) {
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

        Cursor cursor = null;
        try {
            cursor = query(filter, args, start, end);
            return cursor != null ? toEntityList(cursor) : null;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Lista todos registros da tabela que correspondem ao {@code status}.
     *
     * @param status a ser consultado.
     * @return o conjunto de entidades.
     */
    public @Nullable List<T> listar(@NonNull Status status) {
        Preconditions.checkState(mDatabase != null, "é preciso chamar o método openDatabase() antes");

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

    /**
     * Lista todos registros da tabela.
     *
     * @return o conjunto de entidades.
     */
    public @Nullable List<T> listar() {
        Preconditions.checkState(mDatabase != null,
                "é preciso chamar o método openDatabase() antes");

        return listar(null, null);
    }

    /**
     * Especifica o nome da tabela usado no método {@link #query(String, String[], String, String)},
     * podendo ser usado também nas implementações dos métodos {@link #incluir(ModeloBase)},
     * {@link #alterar(ModeloBase)}, {@link #consultar(Integer)}, {@link #listar(Status)} e
     * {@link #listar(String, String)}.
     *
     * @return o nome da tabela.
     */
    protected abstract @NonNull String nomeTabela();

    /**
     * Especifica a lista de colunas para seleção na tabela que serão recuperados ao usar métodos como
     * {@link #query(String, String[], String, String)} podendo ser usado nas implementações
     * de {@link #incluir(ModeloBase)}, {@link #alterar(ModeloBase)}, {@link #consultar(Integer)},
     * {@link #listar(Status)} e {@link #listar(String, String)}.
     *
     * @return a lista de colunas da seleção.
     */
    protected abstract @NonNull String[] projecaoTodasColunas();

    /**
     * Especifica a lista de colunas separadas por vírgula que determinam a ordenação
     * usada no método {@link #query(String, String[], String, String)}.
     *
     * @return a lista de colunas separadas por vírgula.
     */
    protected abstract @Nullable String colunasOrdenacao();

    /**
     * Recupera a entidade com todos os dados nas colunas fornecidas no momento da consulta.
     *
     * @param cursor o {@code cursor} obtido por uma chamada ao método {@link #query(String, String[], String, String)}.
     * @return uma entidade com todas colunas lidas, poderá ser {@code null}.
     * @see #toSingleEntity(Cursor)
     * @see #toEntityList(Cursor)
     */
    protected abstract @Nullable T fromCursor(@NonNull Cursor cursor);

    /**
     * Efetua a inclusão da {@code entidade} especificada no banco de dados.
     *
     * @param entidade a entidade com dados novos.
     * @return o id da entidade, geralmente gerado pela tabela destino.
     */
    public abstract long incluir(@NonNull T entidade);

    /**
     * Aplica as alterações no registro do banco que dados que corresponde á {@code entidade}
     * especificada que deve conter o valor da coluna usada como chave primária.
     *
     * @param entidade a entidade com dados alterados.
     * @return a quantidade de registros afetados pela alteração no banco de dados.
     */
    public abstract int alterar(@NonNull T entidade);
}