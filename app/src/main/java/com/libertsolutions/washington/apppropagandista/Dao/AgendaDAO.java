package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.libertsolutions.washington.apppropagandista.Enum.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by washington on 14/11/2015.
 */
public class AgendaDAO {
    private Banco cnn;
    private Context context;

    //Construtor
    public AgendaDAO(Context context)
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
    public void Incluir(Agenda agenda)
    {
        ContentValues valores;
        long resultado;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("data",agenda.getData().toString());
            valores.put("obs",agenda.getObs());
            valores.put("id_medico",agenda.getId_medico().getId_medico());
            valores.put("status",Integer.valueOf(StatusAgenda.Pendente.toString()));
            resultado = cnn.db().insert("Agenda",null,valores);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
    }

    //Metódo Alterar
    public void Alterar(Agenda agenda)
    {
        ContentValues valores;
        String where;
        try{
            where = "id_agenda = "+agenda.getId_agenda();
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("data",agenda.getData().toString());
            valores.put("obs",agenda.getObs());
            valores.put("id_medico", agenda.getId_medico().getId_medico());
            valores.put("status",agenda.getStatus());
            cnn.db().update("Agenda",valores,where,null);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }

    }

    //Metódo Consultar
    public Agenda Consultar(Integer id_agenda)
    {
        Agenda agenda = new Agenda();
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().rawQuery("select id_agenda,data,id_medico,obs,status " +
                    "from Agenda where id_agenda = '"+id_agenda.toString()+"'",null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    agenda.setId_agenda(cursor.getInt(0));
                    //medico.setDtAniversario(Date.parse(cursor.getString(1).toString()));
                    agenda.setId_medico(new MedicoDAO(context).Consultar(cursor.getInt(2)));
                    agenda.setObs(cursor.getString(3));
                    agenda.setStatus(Integer.parseInt(cursor.getString(4)));
                }
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  agenda;
    }

    //Metódo Listar
    public ArrayList<Agenda> Listar(String start,String limit)
    {
        Agenda agenda;
        String[] campos = {"id_agenda","data","id_medico","obs","status"};
        ArrayList<Agenda> list = new ArrayList<Agenda>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query("Agenda",campos,null,null,null,null,"data,status",start+","+limit);

            while(cursor.moveToNext()){
                agenda = new Agenda();
                agenda.setId_agenda(cursor.getInt(0));
                //medico.setDtAniversario(Date.parse(cursor.getString(1).toString()));
                agenda.setId_medico(new MedicoDAO(context).Consultar(cursor.getInt(2)));
                agenda.setObs(cursor.getString(3));
                agenda.setStatus(Integer.parseInt(cursor.getString(4)));
                list.add(agenda);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            //Mensagem.MensagemAlerta("Listar Cond.Pgto.", error.getMessage());
        }
        return list;
    }
}