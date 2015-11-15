package com.libertsolutions.washington.apppropagandista.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mask;

import java.util.Date;

public class Medico_Cadastrar extends AppCompatActivity {
    //Atributos
    private MedicoDAO medicoDb;
    private EditText txtNome;
    private EditText txtDtAniversario;
    private EditText txtSecretaria;
    private EditText txtTelefone;
    private EditText txtEmail;
    private EditText txtCrm;
    private EditText txtEspecialidade;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_cadastrar);
        this.medicoDb = new MedicoDAO(this);

        //Recupera Campos
        getCampos();

        //Click Botão Salvar
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validaTela()) {
                    Medico medico = getDados();
                    try
                    {
                        //Salva dados no banco
                        medicoDb.Incluir(medico);
                    }catch (Exception error)
                    {
                    }finally {
                        onBackPressed();
                    }
                }
            }
        });

        //Mascara
        txtTelefone.addTextChangedListener(Mask.insert("(##)####-#####", txtTelefone));
        txtDtAniversario.addTextChangedListener(Mask.insert("##/##/####", txtDtAniversario));
    }

    //Metódo Recuperar Campos
    public void getCampos()
    {
        this.txtNome = (EditText)findViewById(R.id.txtNome);
        this.txtDtAniversario = (EditText)findViewById(R.id.txtDtAniversario);
        this.txtSecretaria = (EditText)findViewById(R.id.txtSecretaria);
        this.txtTelefone = (EditText)findViewById(R.id.txtTelefone);
        this.txtEmail = (EditText)findViewById(R.id.txtEmail);
        this.txtCrm = (EditText)findViewById(R.id.txtCrm);
        this.txtEspecialidade = (EditText)findViewById(R.id.txtEspecialidade);
        this.btnSalvar = (Button)findViewById(R.id.btnSalvar);
    }

    //Metódo Preenche Objeto
    public Medico getDados()
    {
        Medico  medico = new Medico();
        medico.setNome(this.txtNome.getText().toString());

        if(TextUtils.isEmpty(txtDtAniversario.getText().toString()))
            medico.setDtAniversario("");
        else
            medico.setDtAniversario(this.txtDtAniversario.getText().toString());

        if(TextUtils.isEmpty(txtSecretaria.getText().toString()))
            medico.setSecretaria("");
        else
            medico.setSecretaria(this.txtSecretaria.getText().toString());

        if(TextUtils.isEmpty(txtTelefone.getText().toString()))
            medico.setTelefone("");
        else
            medico.setTelefone(this.txtTelefone.getText().toString());

        if(TextUtils.isEmpty(txtEmail.getText().toString()))
            medico.setEmail("");
        else
            medico.setEmail(this.txtEmail.getText().toString());

        if(TextUtils.isEmpty(txtCrm.getText().toString()))
            medico.setCrm("");
        else
            medico.setCrm(this.txtCrm.getText().toString());

        if(TextUtils.isEmpty(txtEspecialidade.getText().toString()))
            medico.setEspecialidade("");
        else
            medico.setEspecialidade(this.txtEspecialidade.getText().toString());

        return medico;
    }

    //Função Validar Tela
    private boolean validaTela() {
        // Reset errors.
        txtNome.setError(null);

        // Store values at the time of the login attempt.
        String nome = txtNome.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
}
