package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;

public class Medico_details extends AppCompatActivity {
    //Atributos
    private MedicoDAO medicoDb;
    private EditText txtNome;
    private EditText txtDtAniversario;
    private EditText txtSecretaria;
    private EditText txtTelefone;
    private EditText txtEmail;
    private EditText txtCrm;
    private EditText txtEspecialidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_details);

        //Carrega Campos
        getCampos();

        //Recupera parâmetros
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //Edição
        if(bundle!=null)
        {
            carregaDados(Integer.parseInt(bundle.getString("id")));
        }
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
    }

    public void carregaDados(Integer id)
    {
        try {
            medicoDb = new MedicoDAO(this);
            Medico medico = medicoDb.Consultar(id);
            this.txtNome.setText(medico.getNome());
            if(medico.getDtAniversario() != null)
                this.txtDtAniversario.setText(medico.getDtAniversario().toString());

            this.txtSecretaria.setText(medico.getSecretaria());
            this.txtTelefone.setText(medico.getTelefone());
            this.txtEmail.setText(medico.getEmail());
            this.txtCrm.setText(medico.getCrm());
            this.txtEspecialidade.setText(medico.getEspecialidade());
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta(this,erro.getMessage());
        }
    }
}