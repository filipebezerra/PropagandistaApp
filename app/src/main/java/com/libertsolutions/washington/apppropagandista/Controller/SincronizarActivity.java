package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.EnderecoDAO;
import com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Dao.VisitaDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Endereco;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Visita;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import com.libertsolutions.washington.apppropagandista.api.models.EspecialidadeModel;
import com.libertsolutions.washington.apppropagandista.api.models.MedicoModel;
import com.libertsolutions.washington.apppropagandista.api.models.VisitaModel;
import com.libertsolutions.washington.apppropagandista.api.services.AgendaService;
import com.libertsolutions.washington.apppropagandista.api.services.EspecialidadeService;
import com.libertsolutions.washington.apppropagandista.api.services.MedicoService;
import com.libertsolutions.washington.apppropagandista.api.services.VisitaService;
import java.util.Collections;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

/**
 * Classe backend da view de sincronização dos dados.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 24/12/2015
 * @since 1.0
 */
public class SincronizarActivity extends AppCompatActivity {
    private MedicoDAO mMedicoDAO;
    private VisitaDAO mVisitaDAO;
    private EnderecoDAO mEnderecoDAO;
    private AgendaDAO mAgendaDAO;
    private EspecialidadeDAO mEspecialidadeDAO;
    private Agenda mAgenda;
    private Visita mVisita;

    private MaterialDialog mProgressDialog;

    @Bind(R.id.chkMedicos) protected CheckBox mChkSincMedicos;
    @Bind(R.id.chkAgendas) protected CheckBox mChkSincAgendas;

    private Propagandista mUsuarioLogado;

    private EspecialidadeService mEspecialidadeService;
    private MedicoService mMedicoService;
    private AgendaService mAgendaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        ButterKnife.bind(this);

        mMedicoDAO = new MedicoDAO(this);
        mVisitaDAO = new VisitaDAO(this);
        mEnderecoDAO = new EnderecoDAO(this);
        mAgendaDAO = new AgendaDAO(this);
        mEspecialidadeDAO = new EspecialidadeDAO(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsuarioLogado = PreferencesUtils.getUserLogged(this);

        if (mUsuarioLogado == null || TextUtils.isEmpty(mUsuarioLogado.getCpf())) {
            Dialogos.mostrarMensagem(this, "Não é possível continuar",
                    "Não há usuário logado ou há erros no cpf configurado!",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    });
        }

        mEspecialidadeService = createService(EspecialidadeService.class, this);
        mMedicoService = createService(MedicoService.class, this);
        mAgendaService = createService(AgendaService.class, this);

        if (mMedicoService == null || mAgendaService == null || mEspecialidadeService == null) {
            Dialogos.mostrarMensagem(this, "Não é possível continuar",
                    "As configurações de sincronização não foram aplicadas corretamente.",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMedicoDAO.openDatabase();
        mVisitaDAO.openDatabase();
        mEnderecoDAO.openDatabase();
        mAgendaDAO.openDatabase();
        mEspecialidadeDAO.openDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMedicoDAO.closeDatabase();
        mVisitaDAO.closeDatabase();
        mEnderecoDAO.closeDatabase();
        mAgendaDAO.closeDatabase();
        mEspecialidadeDAO.closeDatabase();
    }

    @OnCheckedChanged(R.id.chkTodos)
    public void chkTodosCheckedChanged(boolean isChecked) {
        mChkSincMedicos.setChecked(isChecked);
        mChkSincAgendas.setChecked(isChecked);
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @OnClick(R.id.btnSincronizar)
    public void btnSincronizarClick() {
        if (!mChkSincMedicos.isChecked() && !mChkSincAgendas.isChecked()) {
            Dialogos.mostrarMensagem(this, "Sincronização de dados",
                    "É necessário selecionar quais dados devem ser sincronizados!");
            return;
        }

        if(mChkSincMedicos.isChecked()) {
            importarEspecialidades();
        } else {
            importarAgendas();
        }
    }

    private void importarEspecialidades() {
        dismissDialog();
        mProgressDialog = Dialogos
                .mostrarProgresso(this, "Por favor aguarde, importando especialidade médica",
                        false);

        mEspecialidadeService
                .get()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ImportaEspecialidadesSubscriber());
    }

    /**
     * Faz uma requisição GET no servidor solicitando os médicos cadastradas para o
     * propagandista logado.
     */
    public void importarMedicos() {
        dismissDialog();
        mProgressDialog = Dialogos
                .mostrarProgresso(this, "Por favor aguarde, importando cadastros de médicos",
                        false);

        mMedicoService
                .getByCpf(mUsuarioLogado.getCpf())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ImportaMedicosSubscriber());
    }

    private static final String LOG_IMPORTA_ESPECIALIDADES =
            ImportaEspecialidadesSubscriber.class.getSimpleName();

    private static final String LOG_IMPORTA_MEDICOS =
            ImportaMedicosSubscriber.class.getSimpleName();


    /**
     * Observador e receptor dos cadastros de médicos recebidas do webservice.
     */
    private class ImportaEspecialidadesSubscriber extends Subscriber<List<EspecialidadeModel>> {
        @Override
        public void onCompleted() {
            importarMedicos();
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_IMPORTA_ESPECIALIDADES, "Falha na importação dos cadastros de especialidades médicas", e);
            if (e.getCause() != null) {
                Log.e(LOG_IMPORTA_ESPECIALIDADES, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(SincronizarActivity.this,
                    "Importação das especialidades médicas cadastradas",
                    String.format("Infelizmente houve um erro e a importação não "
                            + "pôde ser completada. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(List<EspecialidadeModel> especialidades) {
            if (especialidades == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                if (especialidades.isEmpty()) {
                    Dialogos.mostrarMensagem(SincronizarActivity.this,
                            "Importação das especialidades médicas cadastrados",
                            "A importação concluiu sem nenhum registro de especialidade médica importada!");
                } else {
                    for (EspecialidadeModel especialidadeModel : especialidades) {
                        final Especialidade especialidade = Especialidade.fromModel(especialidadeModel);
                        especialidade.setStatus(Status.Importado);

                        mEspecialidadeDAO.incluir(especialidade);
                    }
                }
            }
        }
    }

    /**
     * Observador e receptor dos cadastros de médicos recebidas do webservice.
     */
    private class ImportaMedicosSubscriber extends Subscriber<List<MedicoModel>> {
        @Override
        public void onCompleted() {
            dismissDialog();
            Log.d(LOG_IMPORTA_MEDICOS, "Importação dos cadastros de médicos concluída");
            enviarMedicos();
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_IMPORTA_MEDICOS, "Falha na importação dos cadastros de médicos", e);
            if (e.getCause() != null) {
                Log.e(LOG_IMPORTA_MEDICOS, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(SincronizarActivity.this,
                    "Importação dos médicos cadastrados",
                    String.format("Infelizmente houve um erro e a importação não "
                            + "pôde ser completada. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(List<MedicoModel> medicos) {
            if (medicos == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                if (medicos.isEmpty()) {
                    Dialogos.mostrarMensagem(SincronizarActivity.this,
                            "Importação dos médicos cadastrados",
                            "A importação concluiu sem nenhum registro de médico importado!");
                } else {
                    for (MedicoModel medicoModel : medicos) {
                        final Medico medico = Medico.fromModel(medicoModel);
                        medico.setStatus(Status.Importado);

                        mMedicoDAO.incluir(medico);
                    }
                }
            }
        }
    }

    public void enviarMedicos() {
        final List<Medico> medicos = mMedicoDAO.listar(Status.Pendente);

        if (medicos != null && !medicos.isEmpty()) {
            dismissDialog();
            mProgressDialog = Dialogos
                    .mostrarProgresso(this, "Por favor aguarde, enviando cadastros de médicos",
                            false);

            for (Medico medico : medicos) {
                List<Endereco> enderecos = mEnderecoDAO.listar(medico);
                if (enderecos == null) {
                    enderecos = Collections.emptyList();
                }

                mMedicoService
                        .put(mUsuarioLogado.getCpf(), Medico.toModel(medico, enderecos))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new EnviaMedicoSubscriber());
            }
        }
    }

    private static final String LOG_ENVIA_MEDICOS =
            EnviaMedicoSubscriber.class.getSimpleName();

    /**
     * Observador e receptor da resposta do webservice após processar o envio dos
     * cadastros de médicos feitos localmente.
     */
    private class EnviaMedicoSubscriber extends Subscriber<MedicoModel> {
        @Override
        public void onCompleted() {
            dismissDialog();
            Log.d(LOG_ENVIA_MEDICOS, "Envio dos cadastros de médicos concluído");

            if (mChkSincAgendas.isChecked()) {
                importarAgendas();
            } else {
                Dialogos.mostrarMensagem(SincronizarActivity.this, "Sincronização dos dados",
                        "Sincronização dos dados concluída com sucesso!");
            }
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_ENVIA_MEDICOS, "Falha no envio dos cadastros de médicos", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIA_MEDICOS, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(SincronizarActivity.this,
                    "Envio dos médicos cadastrados",
                    String.format("Infelizmente houve um erro e o envio não "
                            + "pôde ser completado. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(MedicoModel model) {
            dismissDialog();

            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Utils.checkNotNull(model.idCliente, "model.idCliente não pode ser nulo");

                final Medico medicoEnviado = Medico.fromModel(model);
                medicoEnviado.setStatus(Status.Enviado);
                mMedicoDAO.alterar(medicoEnviado);
            }
        }
    }

    /**
     * Faz requisição uma GET para servidor solicitando as agendas cadastradas para
     * o propagandista logado.
     */
    public void importarAgendas() {
        dismissDialog();
        mProgressDialog = Dialogos
                .mostrarProgresso(this, "Por favor aguarde, importando cadastros de agendas",
                        false);

        mAgendaService
                .getByCpf(mUsuarioLogado.getCpf())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ImportaAgendasSubscriber());
    }

    private static final String LOG_IMPORTA_AGENDAS =
            ImportaAgendasSubscriber.class.getSimpleName();

    /**
     * Observador e receptor dos cadastros de agendas recebidas do webservice.
     */
    private class ImportaAgendasSubscriber extends Subscriber<List<AgendaModel>> {
        @Override
        public void onCompleted() {
            dismissDialog();
            Log.d(LOG_IMPORTA_AGENDAS, "Importação dos cadastros de agendas concluído");

            enviarAgendas();
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_IMPORTA_AGENDAS, "Falha na importação dos cadastros de agendas", e);
            if (e.getCause() != null) {
                Log.e(LOG_IMPORTA_AGENDAS, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(SincronizarActivity.this,
                    "Importação das agendas cadastradas",
                    String.format("Infelizmente houve um erro e a importação não "
                            + "pôde ser completada. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(List<AgendaModel> agendas) {
            if (agendas == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                if (agendas.isEmpty()) {
                    Dialogos.mostrarMensagem(SincronizarActivity.this,
                            "Importação das agendas cadastrados",
                            "A importação concluiu sem nenhum registro de agenda importado!");
                } else {
                    for (AgendaModel agendaModel : agendas) {
                        final Agenda agenda = Agenda.fromModel(agendaModel);
                        agenda.setStatus(Status.Importado);
                        //Valida se Agenda já existe
                        if(mAgendaDAO.consultar(AgendaDAO.COLUNA_ID_AGENDA+" = ?",String.valueOf(agendaModel.idAgenda)) == null);
                        {
                            mAgendaDAO.incluir(agenda);
                        }
                    }
                }
            }
        }
    }

    public void enviarAgendas() {
        dismissDialog();
        Dialogos.mostrarMensagem(this, "Sincronização de dados",
                "Sincronização concluída com sucesso");

        //Envia Inclusão de Agendas
        List<Agenda>  agendaNovo = mAgendaDAO.listar(Status.Pendente);
        final Propagandista propagandista = PreferencesUtils.getUserLogged(this);
        if(agendaNovo != null)
        {
            for (Agenda agenda : agendaNovo) {
                mAgenda = agenda;
                mVisita = mVisitaDAO.consultar(VisitaDAO.COLUNA_RELACAO_AGENDA+" = ?",mAgenda.getId().toString());
                //Se for diferente de null
                if(mVisita != null) {
                    final AgendaModel agendaModel = Agenda.toModel(mAgenda);
                    mAgendaService
                            .put(propagandista.getCpf(), agendaModel)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new AlteraAgendaSubscriber());
                }
            }
        }

        //Envia Alteração das Agendas
        List<Agenda> agendaAlterada = mAgendaDAO.listar(Status.Alterado);
        if(agendaAlterada != null)
        {
            for (Agenda agenda : agendaAlterada) {
                mAgenda = agenda;
                mVisita = mVisitaDAO.consultar(VisitaDAO.COLUNA_RELACAO_AGENDA+" = ?",mAgenda.getId().toString());
                final AgendaModel agendaModel = Agenda.toModel(mAgenda);
                mAgendaService
                        .post(agendaModel)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new AlteraAgendaSubscriber());
            }
        }
    }

    private class AlteraAgendaSubscriber extends Subscriber<AgendaModel> {
        @Override
        public void onCompleted() {
            if(mAgenda.getStatusAgenda() == StatusAgenda.EmAtendimento) {
                EnviaVisita();
            }else if(mAgenda.getStatusAgenda() == StatusAgenda.NaoVisita || mAgenda.getStatusAgenda() == StatusAgenda.Finalizado)
            {
                AlteraVisita();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_IMPORTA_AGENDAS, "Falha na sincronização da alteração da agenda", e);
            if (e.getCause() != null) {
                Log.e(LOG_IMPORTA_AGENDAS, "Causa da falha", e.getCause());
            }
        }

        @Override
        public void onNext(AgendaModel model) {
            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Utils.checkNotNull(model.idCliente, "model.idCliente não pode ser nulo");
                Utils.checkNotNull(model.idAgenda, "model.idAgenda não pode ser nulo");
                Utils.checkNotNull(model.statusAgenda,
                        "model.statusAgenda não pode ser nulo");

                Agenda agendaModel = Agenda.fromModel(model);
                agendaModel.setStatus(com.libertsolutions.washington.apppropagandista.Model.Status.Enviado);
                mAgendaDAO.alterar(agendaModel);
            }
        }
    }

    //Inclui visita no webservice
    public void EnviaVisita() {
        final VisitaService visitaService = createService(VisitaService.class, this);
        if(visitaService != null) {
            final VisitaModel visitaModel = Visita.toModel(mVisita);
            visitaService.put(visitaModel)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new EnviaVisitaSubscriber());
        }
    }

    //Envia Web Service Alteração Visita
    public void AlteraVisita() {
        final VisitaService visitaService = createService(VisitaService.class, this);
        if(visitaService != null) {
            final VisitaModel visitaModel = Visita.toModel(mVisita);
            visitaService.post(visitaModel)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new EnviaVisitaSubscriber());
        }
    }

    private class EnviaVisitaSubscriber extends Subscriber<VisitaModel> {
        @Override
        public void onCompleted() {
            Log.d(LOG_IMPORTA_AGENDAS, "Envio dos cadastros de médicos concluído");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_IMPORTA_AGENDAS, "Falha no envio da Visita", e);
            if (e.getCause() != null) {
                Log.e(LOG_IMPORTA_AGENDAS, "Causa da falha", e.getCause());
            }
        }

        @Override
        public void onNext(VisitaModel model) {
            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Utils.checkNotNull(model.idCliente, "model.idVisita não pode ser nulo");

                final Visita visitaEnviado = Visita.fromModel(model);
                visitaEnviado.setStatus(com.libertsolutions.washington.apppropagandista.Model.Status.Enviado);
                mVisitaDAO.alterar(visitaEnviado);
            }
        }
    }
}