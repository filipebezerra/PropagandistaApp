package com.libertsolutions.washington.apppropagandista.Dao;

import android.content.Context;
import android.database.Cursor;

import com.libertsolutions.washington.apppropagandista.Controller.UsuarioActivity;
import com.libertsolutions.washington.apppropagandista.Model.Usuario;
import com.libertsolutions.washington.apppropagandista.Util.Banco;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

/**
 * Created by washington on 04/11/2015.
 */
public class UsuarioDAO {
    private Banco cnn;
    private Context context;

    //Construtor
    public UsuarioDAO(Context context)
    {
        try{
            this.context = context;
            cnn = Banco.getInstance(context);
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }
    }

    //Metódo Incluir
    public void Incluir(Usuario usuario)
    {
        String sql = "";
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            sql = "insert into Usuario (nome,cpf,email,senha) ";
            sql += "values (?,?,?,?)";
            cnn.db().execSQL(sql,new String[]{usuario.getNome(),usuario.getCpf(),usuario.getEmail(),usuario.getSenha()});

        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
    }

    public Usuario Consultar(String email)
    {
        Usuario user = new Usuario();
        try{
            //Abre Conexão
            cnn.AbrirConexao();
            Cursor cursor = cnn.db().rawQuery("select nome,cpf,email,senha from Usuario",null);
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    user.setNome(cursor.getString(0));
                    user.setCpf(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setSenha(cursor.getString(3));
                }
            }
        }catch (Exception error)
        {
            Mensagem.MensagemAlerta(context,error.getMessage());
        }finally {
            cnn.close();
        }
        return  user;
    }

    //Metódo Para validar se existe usuários cadastrados
    public boolean Existe()
    {
        boolean valido = false;
        Cursor cursor = cnn.db().rawQuery("select count(*) as possui from Usuario",null);
        //Verifica se não retornou nulo a consulta
        if(cursor != null)
        {
            //Verifica se existe produto no banco de dados
            if (cursor.moveToFirst()) {
                if(cursor.getInt(0) > 0)
                    valido = true;
            }
        }
        return valido;
    }
}
