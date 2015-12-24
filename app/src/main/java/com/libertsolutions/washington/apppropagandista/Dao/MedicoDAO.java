package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by washington on 11/11/2015.
 */
public class MedicoDAO {
    private static final String TABLE_NAME = "Medico";
    private static final String[] COLUMNS = {"id_medico","nome","dtAniversario","secretaria","telefone","email","crm","id_especialidade","status","id_unico"};
    private Banco cnn;
    private Context context;

    //Construtor
    public MedicoDAO(Context context)
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
    public void Incluir(Medico medico)
    {
        ContentValues valores;
        long resultado;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("nome",medico.getNome());
            if(medico.getDtAniversario() != null)
                valores.put("dtAniversario",medico.getDtAniversario());
            valores.put("secretaria",medico.getSecretaria());
            valores.put("telefone",medico.getTelefone());
            valores.put("email",medico.getEmail());
            valores.put("crm",medico.getCrm());
            valores.put("id_especialidade",medico.getId_especialidade().getId_especialidade());
            valores.put("id_unico",medico.getId_unico());
            valores.put("status",medico.getStatus());
            resultado = cnn.db().insert(TABLE_NAME,null,valores);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
    }

    //Metódo Alterar
    public void Alterar(Medico medico)
    {
        ContentValues valores;
        String where;
        try{
            where = "id_medico = "+medico.getId_medico();
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("nome",medico.getNome());
            if(medico.getDtAniversario() != null)
                valores.put("dtAniversario",medico.getDtAniversario());
            valores.put("secretaria",medico.getSecretaria());
            valores.put("telefone",medico.getTelefone());
            valores.put("email",medico.getEmail());
            valores.put("crm",medico.getCrm());
            valores.put("id_especialidade",medico.getId_especialidade().getId_especialidade());
            valores.put("id_unico", medico.getId_unico());
            valores.put("status",medico.getStatus());
            cnn.db().update(TABLE_NAME,valores,where,null);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }

    }

    //Metódo Existe Id
    public boolean Existe(Integer id_unico)
    {
        boolean existe = false;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"id_unico = "+id_unico,null,null,null,null);
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

    //Metódo Consultar
    public Medico Consultar(Integer id_medico)
    {
        Medico medico = new Medico();
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"id_medico = "+id_medico,null,null,null,null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    medico.setId_medico(cursor.getInt(0));
                    medico.setNome(cursor.getString(1));
                    medico.setDtAniversario(cursor.getString(2).toString());
                    medico.setSecretaria(cursor.getString(3));
                    medico.setTelefone(cursor.getString(4));
                    medico.setEmail(cursor.getString(5));
                    medico.setCrm(cursor.getString(6));
                    medico.setId_espcialidade(new EspecialidadeDAO(context).Consultar(cursor.getInt(7)));
                    medico.setId_unico(cursor.getInt(8));
                    medico.setStatus(cursor.getInt(9));
                    medico.setId_unico(cursor.getInt(10));
                }
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  medico;
    }

    //Metódo Listar paginação
    public ArrayList<Medico> Listar(String start,String limit, String filter, String...args)
    {
        Medico medico;
        ArrayList<Medico> list = new ArrayList<Medico>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query(TABLE_NAME,
                    COLUMNS,
                    filter,
                    args,
                    null,
                    null,
                    "nome",
                    start+","+limit);

            while(cursor.moveToNext()){
                medico = new Medico();
                medico.setId_medico(cursor.getInt(0));
                medico.setNome(cursor.getString(1));
                medico.setDtAniversario(cursor.getString(2).toString());
                medico.setSecretaria(cursor.getString(3));
                medico.setTelefone(cursor.getString(4));
                medico.setEmail(cursor.getString(5));
                medico.setCrm(cursor.getString(6));
                medico.setId_espcialidade(new EspecialidadeDAO(context).Consultar(cursor.getInt(7)));
                medico.setId_unico(cursor.getInt(8));
                medico.setStatus(cursor.getInt(9));
                medico.setId_unico(cursor.getInt(10));
                list.add(medico);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            //Mensagem.MensagemAlerta("Listar Cond.Pgto.", error.getMessage(), activ);
        }
        return list;
    }

    //Metódo Listar paginação
    public ArrayList<Medico> Listar(String start,String limit)
    {
        Medico medico;
        ArrayList<Medico> list = new ArrayList<Medico>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query(TABLE_NAME,
                    COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    "nome",
                    start+","+limit);

            while(cursor.moveToNext()){
                medico = new Medico();
                medico.setId_medico(cursor.getInt(0));
                medico.setNome(cursor.getString(1));
                medico.setDtAniversario(cursor.getString(2).toString());
                medico.setSecretaria(cursor.getString(3));
                medico.setTelefone(cursor.getString(4));
                medico.setEmail(cursor.getString(5));
                medico.setCrm(cursor.getString(6));
                medico.setId_espcialidade(new EspecialidadeDAO(context).Consultar(cursor.getInt(7)));
                medico.setId_unico(cursor.getInt(8));
                medico.setStatus(cursor.getInt(9));
                medico.setId_unico(cursor.getInt(10));
                list.add(medico);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            //Mensagem.MensagemAlerta("Listar Cond.Pgto.", error.getMessage(), activ);
        }
        return list;
    }

    //Listar médicos por status
    public ArrayList<Medico> Listar(int status)
    {
        Medico medico;
        ArrayList<Medico> list = new ArrayList<Medico>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"status = "+status,null,null,null,null);

            while(cursor.moveToNext()){
                medico = new Medico();
                medico.setId_medico(cursor.getInt(0));
                medico.setNome(cursor.getString(1));
                medico.setDtAniversario(cursor.getString(2).toString());
                medico.setSecretaria(cursor.getString(3));
                medico.setTelefone(cursor.getString(4));
                medico.setEmail(cursor.getString(5));
                medico.setCrm(cursor.getString(6));
                medico.setId_espcialidade(new EspecialidadeDAO(context).Consultar(cursor.getInt(7)));
                medico.setId_unico(cursor.getInt(8));
                medico.setStatus(cursor.getInt(9));
                medico.setId_unico(cursor.getInt(10));
                list.add(medico);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            //Mensagem.MensagemAlerta("Listar Cond.Pgto.", error.getMessage(), activ);
        }
        return list;
    }
}