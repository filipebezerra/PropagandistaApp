package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mask;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;

/**
 * Created by washington on 17/11/2015.
 */
public class Medico_details extends AppCompatActivity {
    //Atributos
    private MedicoDAO medicoDb;
    @Bind(R.id.txtNome) EditText txtNome;
    @Bind(R.id.txtDtAniversario) EditText txtDtAniversario;
    DatePickerDialog dataAniversario;
    @Bind(R.id.txtSecretaria) EditText txtSecretaria;
    @Bind(R.id.txtTelefone) EditText txtTelefone;
    @Bind(R.id.txtEmail) EditText txtEmail;
    @Bind(R.id.txtCrm) EditText txtCrm;
    @Bind(R.id.txtEspecialidade) EditText txtEspecialidade;
    @Bind(R.id.txtId) EditText txtId;
    @Bind(R.id.btnSalvar) Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_details);

        //Chama Funções para Campos Data
        setDateTimeField();

        //Selecionar Data Aniversário
        txtDtAniversario.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dataAniversario.show();
                return false;
            }
        });

        //Click Botão Salvar
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validaTela()) {
                    Medico medico = getDados();
                    try
                    {
                        //Salva dados no banco
                        medicoDb.Alterar(medico);
                    }catch (Exception error)
                    {
                    }finally {
                        Mensagem.MensagemAlerta(Medico_details.this, "Dados incluidos com sucesso!");
                        onBackPressed();
                    }
                }
            }
        });

        //Mascara
        txtTelefone.addTextChangedListener(Mask.insert("(##)####-#####", txtTelefone));
        txtDtAniversario.addTextChangedListener(Mask.insert("##/##/####", txtDtAniversario));

        //Recupera Parâmetros
        final Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("id") && extras.getString("id") != null) {
            medicoDb = new MedicoDAO(this);
            int idMedico = Integer.valueOf(extras.getString("id"));
            preencheTela(medicoDb.Consultar(idMedico));
        }
    }

    //Metódo para mostrar data
    private void setDateTimeField() {
        //Dialog Data
        Calendar newCalendar = Calendar.getInstance();
        dataAniversario = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                txtDtAniversario.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //Metódo Preencher dados na tela
    public void preencheTela(Medico medico)
    {
        txtId.setText(medico.getId_medico());
        txtNome.setText(medico.getNome());
        txtDtAniversario.setText(medico.getDtAniversario());
        txtSecretaria.setText(medico.getSecretaria());
        txtTelefone.setText(medico.getTelefone());
        txtEmail.setText(medico.getEmail());
        txtCrm.setText(medico.getCrm());
        txtEspecialidade.setText(medico.getEspecialidade());
    }

    //Metódo Preenche Objeto
    public Medico getDados()
    {
        Medico  medico = new Medico();
        medico.setNome(this.txtNome.getText().toString());
        medico.setId_medico(Integer.parseInt(this.txtId.getText().toString()));

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
}