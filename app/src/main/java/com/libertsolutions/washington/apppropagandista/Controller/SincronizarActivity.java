package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.StateSet;
import android.widget.Button;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Enum.Status;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.AgendaService;
import com.libertsolutions.washington.apppropagandista.api.EspecialidadeService;
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
    private AgendaDAO agendaDb;
    private EspecialidadeDAO especialidadeDb;
    private Medico medico;

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
        this.agendaDb = new AgendaDAO(this);
        this.especialidadeDb = new EspecialidadeDAO(this);
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
                .content("Por favor aguarde, sincronizando dados....")
                .progress(true, 0)
                .cancelable(false)
                .show();

        //Integra Médicos
        if(chkMedicos.isChecked())
        {
            ImportaEspecialidade();//Importa médicos do webservice
            ImportaMedicos();//Importa médicos do webservice
            EnviaMedicos();//Envia medicos para o web serviçe
        }

        //Integra Agendas
        if(chkAgendas.isChecked())
        {
            ImportaAgendas();//Importa Agendas Registradas
            EnviaAgendas();//Envia Agendas Pendentes
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
            final MedicoService service = createService(MedicoService.class, this);
            Propagandista propagandista = PreferencesUtils.getUserLogged(this);
            if (service != null) {
                service.getByCpf(propagandista.getCpf())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MedicoSubscriber());
            }
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Sincronizar Dados", erro.getMessage(), SincronizarActivity.this);
        }
    }

    /// Metódo para enviar e receber médicos cadastrados no web-service
    public void ImportaEspecialidade()
    {
        try {
            final EspecialidadeService service = createService(EspecialidadeService.class, this);
            if (service != null) {
                service.get()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new EspecialidadeSubscriber());
            }
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Sincronizar Dados", erro.getMessage(), SincronizarActivity.this);
        }
    }

    public void EnviaMedicos()
    {
        try {
            //Recupera lista de médicos ainda não enviados para o webservice.
            List<Medico> lstMedicos = medicoDb.Listar(1);
            final MedicoService service = createService(MedicoService.class, this);
            Propagandista propagandista = PreferencesUtils.getUserLogged(this);
            if (service != null) {
                for (Medico objMedico : lstMedicos) {
                    this.medico = objMedico;
                    service.put(propagandista.getCpf(), objMedico)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MedicoEnviar());
                }
            }
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Sincronizar Dados", erro.getMessage(), SincronizarActivity.this);
        }
    }

    //Envia Médico para o webservice
    private class  MedicoEnviar extends Subscriber<Integer> {
        @Override
        public void onCompleted() {
            // vazio
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            if (e.getCause() != null) {
                //Log.e(TAG, e.getCause().getMessage());
            }
            Mensagem.MensagemAlerta(SincronizarActivity.this, e.getMessage());
        }

        @Override
        public void onNext(Integer id_unico) {
            dismissDialog();
            if (id_unico > 0) {
                medico.setId_unico(id_unico);//Seta id unico
                medico.setStatus(Status.Enviado.codigo);
                medicoDb.Alterar(medico);
            } else {
                Mensagem.MensagemAlerta("Enviar médicos", "Ocorreu um erro ao enviar médico.", SincronizarActivity.this);
            }
        }
    }

    //Obtem médicos do web service
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
                    medico.setStatus(Status.Enviado.codigo);//Seta status = 2 improtado;
                    //Valida se médico já existe
                    if(medicoDb.Existe(medico.getId_unico())) {
                        medicoDb.Alterar(medico);
                    }
                    else
                        medicoDb.Incluir(medico);
                }
            } else {
                Mensagem.MensagemAlerta("Sincronizar", "Médicos não foram importados...", SincronizarActivity.this);
            }
        }
    }

    //Obtem médicos do web service
    private class  EspecialidadeSubscriber extends Subscriber<List<Especialidade>> {
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
        public void onNext(List<Especialidade> especialidades) {
            if (especialidades != null) {
                for (int i =0; i < especialidades.size();i++)
                {
                    Especialidade especialidade = especialidades.get(i);
                    //Valida se médico já existe
                    if(especialidadeDb.Existe(especialidade.getId_especialidade())) {
                        especialidadeDb.Alterar(especialidade);
                    }
                    else
                        especialidadeDb.Incluir(especialidade);
                }
            } else {
                Mensagem.MensagemAlerta("Sincronizar", "Especialidade Médica não foi importado...", SincronizarActivity.this);
            }
        }
    }

    /// Metódo para enviar e receber agendas cadastradas no web-service
    public void ImportaAgendas()
    {
        try {
            final AgendaService service = createService(AgendaService.class, this);
            Propagandista propagandista = PreferencesUtils.getUserLogged(this);
            if (service != null) {
                service.getByCpf(propagandista.getCpf())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new AgendaSubscriber());
            }
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Sincronizar Dados", erro.getMessage(), SincronizarActivity.this);
        }
    }

    //Obtem médicos do web service
    private class AgendaSubscriber extends Subscriber<List<Agenda>> {
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
        public void onNext(List<Agenda> agendas) {
            if (agendas != null) {
                for (int i =0; i < agendas.size();i++)
                {
                    Agenda agenda = agendas.get(i);
                    medico.setStatus(Status.Enviado.codigo);
                    //Valida se médico já existe
                    if(agendaDb.Existe(agenda.getId_unico())) {
                        agendaDb.Alterar(agenda);
                    }
                    else
                        agendaDb.Incluir(agenda);
                }
            } else {
                Mensagem.MensagemAlerta("Sincronizar", "Médicos não foram importados...", SincronizarActivity.this);
            }
        }
    }

    //Metódo para enviar
    public void EnviaAgendas()
    {
        dismissDialog();
    }
}