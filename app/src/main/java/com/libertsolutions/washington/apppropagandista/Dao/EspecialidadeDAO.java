package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

import java.util.ArrayList;

/**
 * Created by washington on 21/12/2015.
 */
public class EspecialidadeDAO {
    private static final String TABLE_NAME = "Especialidade";
    private static final String[] COLUMNS = {"id_especialidade","nome"};
    private Banco cnn;
    private Context context;

    //Construtor
    public EspecialidadeDAO(Context context)
    {
        try{
            this.context = context;
            cnn = Banco.getInstance(context);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context, error.getMessage());
        }
    }

    //Metódo Incluir
    public void Incluir(Especialidade especialidade)
    {
        ContentValues valores;
        long resultado;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("id_especialidade",especialidade.getId_especialidade());
            valores.put("nome",especialidade.getNome());
            resultado = cnn.db().insert(TABLE_NAME,null,valores);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
    }

    //Metódo Alterar
    public void Alterar(Especialidade especialidade)
    {
        ContentValues valores;
        String where;
        try{
            where = "id_especialidade = "+especialidade.getId_especialidade();
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("nome",especialidade.getNome());
            cnn.db().update(TABLE_NAME,valores,where,null);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }

    }

    //Metódo Consultar
    public Especialidade Consultar(Integer id_especialidade)
    {
        Especialidade especialidade = new Especialidade();
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"id_especialidade = "+id_especialidade,null,null,null,null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    especialidade.setId_especialidade(cursor.getInt(0));
                    especialidade.setNome(cursor.getString(1));
                }
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  especialidade;
    }

    //Metódo Listar
    public ArrayList<Especialidade> Listar(String start,String limit)
    {
        Especialidade especialidade;
        ArrayList<Especialidade> list = new ArrayList<Especialidade>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query(
                    TABLE_NAME,
                    COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    "nome",
                    start+","+limit);

            while(cursor.moveToNext()){
                especialidade = new Especialidade();
                especialidade.setId_especialidade(cursor.getInt(0));
                especialidade.setNome(cursor.getString(1));
                list.add(especialidade);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context, error.getMessage());
        }
        return list;
    }

    //Metódo Existe Id
    public boolean Existe(Integer id_especialidade)
    {
        boolean existe = false;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"id_especialidade = "+id_especialidade,null,null,null,null);
            if(cursor.moveToFirst()) {
                existe = true;
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  existe;
    }
}
