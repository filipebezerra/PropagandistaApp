package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.libertsolutions.washington.apppropagandista.Model.Visita;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

import java.util.ArrayList;

/**
 * Created by washington on 17/11/2015.
 */
public class VisitaDAO {
    private Banco cnn;
    private Context context;

    //Construtor
    public VisitaDAO(Context context)
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
    public void Incluir(Visita visita)
    {
        ContentValues valores;
        long resultado;
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("dtInicio",visita.getDtInicio());
            valores.put("horaInicio",visita.getHoraInicio());
            valores.put("longInicial",visita.getLongInicial());
            valores.put("latInicial",visita.getLatInicial());
            valores.put("id_agenda",visita.getAgenda().getId_agenda());
            resultado = cnn.db().insert("Visita",null,valores);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
    }

    //Metódo Alterar
    public void Alterar(Visita visita)
    {
        ContentValues valores;
        String where;
        try{
            where = "id_visita = "+visita.getId_visita();
            //Abre Conexão
            cnn.AbrirConexao();
            valores = new ContentValues();
            valores.put("dtInicio",visita.getDtInicio());
            valores.put("horaInicio", visita.getHoraInicio());
            valores.put("longInicial",visita.getLongInicial());
            valores.put("latInicial",visita.getLatInicial());
            valores.put("dtFim",visita.getAgenda().getId_agenda());
            valores.put("horaFim",visita.getHoraFim());
            valores.put("longFinal", visita.getLongFinal());
            valores.put("latFinal",visita.getLatFinal());
            valores.put("detalhes",visita.getDetalhes());
            cnn.db().update("Visita",valores,where,null);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }

    }

    //Metódo Consultar
    public Visita Consultar(Integer id_visita)
    {
        Visita visita = new Visita();
        String[] campos = {"id_visita","dtInicio","horaInicio","longInicial","latInicial","dtFim","horaFim","longFinal","latFinal","detalhes"};
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().query("Visita",campos,"id_visita = "+id_visita,null,null,null,null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    visita.setId_visita(cursor.getInt(0));
                    visita.setDtInicio(cursor.getString(1));
                    visita.setHoraInicio(cursor.getString(2).toString());
                    visita.setLongInicial(cursor.getDouble(3));
                    visita.setLatInicial(cursor.getDouble(4));
                    visita.setDtFim(cursor.getString(5));
                    visita.setHoraFim(cursor.getString(6));
                    visita.setLongFinal(cursor.getDouble(7));
                    visita.setLatFinal(cursor.getDouble(8));
                    visita.setDetalhes(cursor.getString(9));
                }
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  visita;
    }

    //Metódo Listar
    public ArrayList<Visita> Listar(String start,String limit)
    {
        Visita visita;
        String[] campos = {"id_visita","dtInicio","horaInicio","longInicial","latInicial","dtFim","horaFim","longFinal","latFinal","detalhes"};
        ArrayList<Visita> list = new ArrayList<Visita>();
        try {
            //Abre Conexão
            cnn.AbrirConexao();

            Cursor cursor = cnn.db().query("Visita",campos,null,null,null,null,"dtInicio",start+","+limit);

            while(cursor.moveToNext()){
                visita = new Visita();
                visita.setId_visita(cursor.getInt(0));
                visita.setDtInicio(cursor.getString(1));
                visita.setHoraInicio(cursor.getString(2).toString());
                visita.setLongInicial(cursor.getDouble(3));
                visita.setLatInicial(cursor.getDouble(4));
                visita.setDtFim(cursor.getString(5));
                visita.setHoraFim(cursor.getString(6));
                visita.setLongFinal(cursor.getDouble(7));
                visita.setLatFinal(cursor.getDouble(8));
                visita.setDetalhes(cursor.getString(9));
                list.add(visita);
            }

            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }
        return list;
    }
}
