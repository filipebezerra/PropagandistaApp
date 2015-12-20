package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.libertsolutions.washington.apppropagandista.Enum.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import java.util.ArrayList;

/**
 * Created by washington on 14/11/2015.
 */
public class AgendaDAO {
    private static final String TABLE_NAME = "Agenda";
    private static final String[] COLUMNS = {"id_agenda","data","hora","id_medico","obs","status","statusagenda","id_unico"};
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
            valores.put("data",agenda.getData());
            valores.put("hora",agenda.getHora());
            valores.put("obs",agenda.getObs());
            valores.put("id_medico",agenda.getId_medico().getId_medico());
            valores.put("statusagenda",StatusAgenda.Pendente.codigo);
            valores.put("status",agenda.getStatus());
            valores.put("id_unico",StatusAgenda.Pendente.codigo);
            resultado = cnn.db().insert(TABLE_NAME,null,valores);
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
            valores.put("data",agenda.getData());
            valores.put("hora",agenda.getHora());
            valores.put("obs",agenda.getObs());
            valores.put("id_medico", agenda.getId_medico().getId_medico());
            valores.put("status",agenda.getStatus());
            valores.put("statusagenda",agenda.getStatus());
            cnn.db().update(TABLE_NAME,valores,where,null);
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
            Cursor cursor = cnn.db().query(TABLE_NAME,COLUMNS,"id_agenda = "+id_agenda,null,null,null,null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    agenda.setId_agenda(cursor.getInt(0));
                    agenda.setData(cursor.getString(1));
                    agenda.setHora(cursor.getString(2));
                    agenda.setId_medico(new MedicoDAO(context).Consultar(cursor.getInt(3)));
                    agenda.setObs(cursor.getString(4));
                    agenda.setStatus(cursor.getInt(5));
                    agenda.setStatusAgenda(cursor.getInt(6));
                    agenda.setId_unico(cursor.getInt(7));
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
        ArrayList<Agenda> list = new ArrayList<Agenda>();
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
                    "data, hora",
                    start+","+limit);

            while(cursor.moveToNext()){
                agenda = new Agenda();
                agenda.setId_agenda(cursor.getInt(0));
                agenda.setData(cursor.getString(1));
                agenda.setHora(cursor.getString(2));
                agenda.setId_medico(new MedicoDAO(context).Consultar(cursor.getInt(3)));
                agenda.setObs(cursor.getString(4));
                agenda.setStatus(cursor.getInt(5));
                agenda.setStatusAgenda(cursor.getInt(6));
                agenda.setId_unico(cursor.getInt(7));
                list.add(agenda);
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

    public ArrayList<Agenda> Listar(String start,String limit, String filter, String...args)
    {
        Agenda agenda;
        ArrayList<Agenda> list = new ArrayList<>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query(
                    TABLE_NAME,
                    COLUMNS,
                    filter,
                    args,
                    null,
                    null,
                    "data, hora",
                    start+","+limit);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    agenda = new Agenda();
                    agenda.setId_agenda(cursor.getInt(0));
                    agenda.setData(cursor.getString(1));
                    agenda.setHora(cursor.getString(2));
                    agenda.setId_medico(new MedicoDAO(context).Consultar(cursor.getInt(3)));
                    agenda.setObs(cursor.getString(4));
                    agenda.setStatus(cursor.getInt(5));
                    agenda.setStatusAgenda(cursor.getInt(6));
                    agenda.setId_unico(cursor.getInt(7));
                    list.add(agenda);
                }
                while (cursor.moveToNext());

                cursor.close();
            }

        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context, error.getMessage());
        }
        return list;
    }
}
