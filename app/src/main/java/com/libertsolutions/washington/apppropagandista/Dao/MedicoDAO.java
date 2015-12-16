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
            valores.put("especialidade",medico.getEspecialidade());
            valores.put("id_unico",medico.getId_unico());
            valores.put("status",1);
            resultado = cnn.db().insert("Medico",null,valores);
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
            valores.put("dtAniversario",medico.getDtAniversario());
            valores.put("secretaria",medico.getSecretaria());
            valores.put("telefone",medico.getTelefone());
            valores.put("email",medico.getEmail());
            valores.put("crm",medico.getCrm());
            valores.put("especialidade",medico.getEspecialidade());
            valores.put("id_unico", medico.getId_unico());
            cnn.db().update("Medico",valores,where,null);
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
        String[] campos = {"id_medico","nome","dtAniversario","secretaria","telefone","email","crm","especialidade","status"};
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query("Medico",campos,"id_unico = "+id_unico,null,null,null,null);
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
        String[] campos = {"id_medico","nome","dtAniversario","secretaria","telefone","email","crm","especialidade","id_unico","status"};
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query("Medico",campos,"id_medico = "+id_medico,null,null,null,null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    medico.setId_medico(cursor.getInt(0));
                    medico.setNome(cursor.getString(1));
                    medico.setDtAniversario(cursor.getString(2).toString());
                    medico.setTelefone(cursor.getString(3));
                    medico.setEmail(cursor.getString(4));
                    medico.setCrm(cursor.getString(5));
                    medico.setEmail(cursor.getString(6));
                    medico.setEspecialidade(cursor.getString(7));
                    medico.setId_unico(cursor.getInt(8));
                    medico.setStatus(Integer.parseInt(cursor.getString(9)));
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

    //Metódo Listar
    public ArrayList<Medico> Listar(String start,String limit)
    {
        Medico medico;
        String[] campos = {"id_medico","nome","dtAniversario","secretaria","telefone","email","crm","especialidade","id_unico","status"};
        ArrayList<Medico> list = new ArrayList<Medico>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query("Medico",campos,null,null,null,null,"nome",start+","+limit);

            while(cursor.moveToNext()){
                medico = new Medico();
                medico.setId_medico(Integer.parseInt(cursor.getString(0)));
                medico.setNome(cursor.getString(1));
                medico.setDtAniversario(cursor.getString(2));
                medico.setSecretaria(cursor.getString(3));
                medico.setTelefone(cursor.getString(4));
                medico.setEmail(cursor.getString(5));
                medico.setCrm(cursor.getString(6));
                medico.setEspecialidade(cursor.getString(7));
                medico.setId_unico(cursor.getInt(8));
                medico.setStatus(cursor.getInt(9));
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
