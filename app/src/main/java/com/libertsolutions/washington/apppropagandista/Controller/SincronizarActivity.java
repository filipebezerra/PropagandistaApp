package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.MedicoService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

public class SincronizarActivity extends AppCompatActivity {
    private MaterialDialog mProgressDialog;
    private MedicoDAO medicoDb;

    @Bind(R.id.btnSincronizar)
    Button btnsicronizar;

    @Bind(R.id.chkMedicos)
    CheckBox chkMedicos;

    @Bind(R.id.chkAgendas)
    CheckBox chkAgendas;

    @Bind(R.id.chkTodos)
    CheckBox chkTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);

        this.medicoDb = new MedicoDAO(this);
        ButterKnife.bind(this);
    }

    /// Metódo click selecionar todos
    @OnClick(R.id.chkTodos)
    public void onChkTodosrClick() {
        if(chkTodos.isChecked())
        {
            chkAgendas.setChecked(true);
            chkMedicos.setChecked(true);
        }
        else
        {
            chkAgendas.setChecked(false);
            chkMedicos.setChecked(false);
        }
    }

    /// Metódo click botão sincronizar
    @OnClick(R.id.btnSincronizar)
    public void onSincronizarClick() {
        mProgressDialog = new MaterialDialog.Builder(SincronizarActivity.this)
                .content("Por favor aguarde...importando dados")
                .progress(true, 0)
                .cancelable(false)
                .show();

        //Integra Médicos
        if(chkMedicos.isChecked())
        {
            ImportaMedicos();
        }

        //Integra Agendas
        if(chkAgendas.isChecked())
        {
            ImportaAgendas();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /// Metódo para enviar e receber médicos cadastrados no web-service
    public void ImportaMedicos()
    {
        try {
            mProgressDialog.setContent("Sincronizando Médicos....");
            final MedicoService service = createService(MedicoService.class, this);
            Propagandista propagandista = PreferencesUtils.getUserLogged(this);
            if (service != null) {
                service.getByCpf(propagandista.getCpf())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MedicoSubscriber());
            }

            dismissDialog();
        }catch (Exception erro)
        {
            dismissDialog();
            Mensagem.MensagemAlerta("Sincronizar Dados", erro.getMessage(), SincronizarActivity.this);
        }
    }

    private class  MedicoSubscriber extends Subscriber<List<Medico>> {
        @Override
        public void onCompleted() {
            // vazio
        }

        @Override
        public void onError(Throwable e) {
            if (e.getCause() != null) {
                //Log.e(TAG, e.getCause().getMessage());
            }
            Mensagem.MensagemAlerta(SincronizarActivity.this, e.getMessage());
        }

        @Override
        public void onNext(List<Medico> medicos) {
            if (medicos != null) {
                for (int i =0; i < medicos.size();i++)
                {
                    Medico medico = medicos.get(i);
                    //Valida se médico já existe
                    if(medicoDb.Existe(medico.getId_unico())) {
                        medicoDb.Alterar(medico);
                    }
                    else
                        medicoDb.Incluir(medico);
                }
            } else {
                dismissDialog();
                Mensagem.MensagemAlerta("Sincronizar", "Médicos não foram importados...", SincronizarActivity.this);
            }
        }
    }

    /// Metódo para enviar e receber agendas cadastradas no web-service
    public void ImportaAgendas()
    {
        mProgressDialog.setContent("Sincronizando Agendas....");
    }
}