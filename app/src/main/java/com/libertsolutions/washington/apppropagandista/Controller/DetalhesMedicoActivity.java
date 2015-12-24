package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Enum.Status;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mask;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.MedicoService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

public class DetalhesMedicoActivity extends AppCompatActivity {
    //Atributos
    private MedicoDAO medicoDb;
    @Bind(R.id.txtIdUnico)
    EditText txtIdUnico;

    @Bind(R.id.txtId)
    EditText txtId;

    @Bind(R.id.txtNome)
    EditText txtNome;

    @Bind(R.id.txtDtAniversario)
    EditText txtDtAniversario;

    @Bind(R.id.txtSecretaria)
    EditText txtSecretaria;

    @Bind(R.id.txtTelefone)
    EditText txtTelefone;

    @Bind(R.id.txtEmail)
    EditText txtEmail;

    @Bind(R.id.txtCrm)
    EditText txtCrm;

    @Bind(R.id.txtEspecialidade)
    EditText txtEspecialidade;

    @Bind(R.id.btnSalvar)
    Button btnSalvar;
    DatePickerDialog dataAniversario;
    private Medico medico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_medico);
        this.medicoDb = new MedicoDAO(this);

        ButterKnife.bind(this);

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

        //Mascara
        txtTelefone.addTextChangedListener(Mask.insert("(##)####-#####", txtTelefone));
        txtDtAniversario.addTextChangedListener(Mask.insert("##/##/####", txtDtAniversario));

        //Recupera Parâmetros e Preenche Tela
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //Edição
        if(bundle!=null)
        {
            if(bundle.getString("id") != "")
            {
                Medico medico = medicoDb.Consultar(Integer.parseInt(bundle.getString("id")));
                PreencheTela(medico);
            }
        }
    }

    /// Metódo click botão Salvar
    @OnClick(R.id.btnSalvar)
    public void bnSalvarClick() {
        if(!validaTela()) {
            medico = getDados();
            try
            {
                medico.setStatus(Status.Alterado.codigo);
                //Salva dados no banco
                medicoDb.Alterar(medico);
                final MedicoService service = createService(MedicoService.class, this);

                if (service != null) {
                    service.post(medico)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MedicoEnviar());
                }
            }catch (Exception error)
            {
            }finally {
                Mensagem.MensagemAlerta(DetalhesMedicoActivity.this, "Dados incluidos com sucesso!");
                onBackPressed();
            }
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

    //Metódo Preenche Objeto
    public Medico getDados()
    {
        Medico  medico = new Medico();
        medico.setNome(this.txtNome.getText().toString());
        medico.setId_unico(Integer.parseInt(txtIdUnico.getText().toString()));
        medico.setId_medico(Integer.parseInt(txtId.getText().toString()));
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
            medico.setId_espcialidade(new Especialidade());
        else
            medico.setId_espcialidade(new EspecialidadeDAO(this).Consultar(Integer.parseInt(this.txtEspecialidade.getText().toString())));

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

    //Preenche Tela
    private void PreencheTela(Medico medico)
    {
        this.txtId.setText(medico.getId_medico().toString());
        this.txtIdUnico.setText(medico.getId_unico().toString());
        this.txtNome.setText(medico.getNome());
        this.txtDtAniversario.setText(medico.getDtAniversario());
        this.txtSecretaria.setText(medico.getSecretaria());
        this.txtTelefone.setText(medico.getTelefone());
        this.txtEmail.setText(medico.getEmail());
        this.txtCrm.setText(medico.getCrm());
        this.txtEspecialidade.setText(medico.getId_especialidade().getId_especialidade());
    }

    private class  MedicoEnviar extends Subscriber<Integer> {
        @Override
        public void onCompleted() {
            // vazio
        }

        @Override
        public void onError(Throwable e) {
            if (e.getCause() != null) {
                //Log.e(TAG, e.getCause().getMessage());
            }
            Mensagem.MensagemAlerta(DetalhesMedicoActivity.this, e.getMessage());
        }

        @Override
        public void onNext(Integer id_unico) {
            if (id_unico > 0) {
                medico.setId_unico(id_unico);//Seta id unico
                medico.setStatus(Status.Enviado.codigo);
                medicoDb.Alterar(medico);
            } else {
                Mensagem.MensagemAlerta("Enviar médicos", "Ocorreu um erro ao enviar médico.", DetalhesMedicoActivity.this);
            }
        }
    }
}