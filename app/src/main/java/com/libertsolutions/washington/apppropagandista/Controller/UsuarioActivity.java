package com.libertsolutions.washington.apppropagandista.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.libertsolutions.washington.apppropagandista.Dao.UsuarioDAO;
import com.libertsolutions.washington.apppropagandista.Model.Usuario;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Tela;

public class UsuarioActivity extends AppCompatActivity {
    private UsuarioDAO mUsuarioDAO;
    private EditText txtNome;
    private EditText txtCpf;
    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        this.mUsuarioDAO = new UsuarioDAO(this);

        //Recupera Campos
        getCampos();

        //Click Botão Salvar
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validaTela()) {
                    Usuario user = getDados();
                    try
                    {
                        //Salva dados no banco
                        mUsuarioDAO.incluir(user);
                    }catch (Exception error)
                    {
                    }finally {
                        Bundle param = new Bundle();
                        param.putString("email", user.getEmail());
                        Tela.AbrirTela(UsuarioActivity.this, LoginActivity.class, param);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUsuarioDAO.openDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mUsuarioDAO.closeDatabase();
    }

    //Metódo Recuperar Campos
    public void getCampos()
    {
        this.txtNome = (EditText)findViewById(R.id.txtNomeMedico);
        this.txtCpf = (EditText)findViewById(R.id.txtCpf);
        this.txtEmail = (EditText)findViewById(R.id.txtEmail);
        this.txtSenha = (EditText)findViewById(R.id.txtSenha);
        this.btnSalvar = (Button)findViewById(R.id.btnSalvar);
    }

    //Metódo Preenche Objeto
    public Usuario getDados()
    {
        Usuario user = new Usuario();
        user.setNome(this.txtNome.getText().toString());
        user.setCpf(this.txtCpf.getText().toString());
        user.setEmail(this.txtEmail.getText().toString());
        user.setSenha(this.txtSenha.getText().toString());
        return user;
    }

    //Função Validar Tela
    private boolean validaTela() {
        // Reset errors.
        txtNome.setError(null);
        txtCpf.setError(null);
        txtEmail.setError(null);
        txtSenha.setError(null);

        // Store values at the time of the login attempt.
        String nome = txtNome.getText().toString();
        String cpf = txtCpf.getText().toString();
        String email = txtEmail.getText().toString();
        String senha = txtSenha.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Valida Senha
        if (!TextUtils.isEmpty(senha) && !isPasswordValid(senha)) {
            txtSenha.setError(getString(R.string.error_invalid_password));
            focusView = txtSenha;
            cancel = true;
        }else if(TextUtils.isEmpty(senha))
        {
            txtSenha.setError(getString(R.string.error_field_required));
            focusView = txtSenha;
            cancel = true;
        }

        // Valida E-mail
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_field_required));
            focusView = txtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            focusView = txtEmail;
            cancel = true;
        }

        //Valida CPF
        if(TextUtils.isEmpty(cpf))
        {
            txtCpf.setError(getString(R.string.error_field_required));
            focusView = txtCpf;
            cancel = true;
        }else if(!isCpf(cpf))
        {
            txtCpf.setError(getString(R.string.error_incorrect_cpf));
            focusView = txtCpf;
            cancel = true;
        }

        //Valida Nome
        if(TextUtils.isEmpty(nome))
        {
            txtNome.setError(getString(R.string.error_field_required));
            focusView = txtNome;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isCpf(String password) {
        //Valida CPF
        return password.length() == 11;
    }
}
