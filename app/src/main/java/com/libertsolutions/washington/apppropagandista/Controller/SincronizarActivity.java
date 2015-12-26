package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import com.libertsolutions.washington.apppropagandista.api.services.AgendaService;
import com.libertsolutions.washington.apppropagandista.api.services.MedicoService;
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
    private AgendaDAO mAgendaDAO;

    private MaterialDialog mProgressDialog;

    @Bind(R.id.chkMedicos) protected CheckBox mChkSincMedicos;
    @Bind(R.id.chkAgendas) protected CheckBox mChkSincAgendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        ButterKnife.bind(this);

        mMedicoDAO = new MedicoDAO(this);
        mAgendaDAO = new AgendaDAO(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMedicoDAO.openDatabase();
        mAgendaDAO.openDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMedicoDAO.closeDatabase();
        mAgendaDAO.closeDatabase();
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
            importarMedicos();
        } else {
            importarAgendas();
        }
    }

    /**
     * Faz uma requisição GET no servidor solicitando os médicos cadastradas para o
     * propagandista logado.
     */
    public void importarMedicos() {
        final MedicoService service = createService(MedicoService.class, this);
        final Propagandista propagandista = PreferencesUtils.getUserLogged(this);

        if (service == null) {
            Dialogos.mostrarMensagem(this, "Sincronização dos dados",
                    "As configurações de sincronização não foram aplicadas corretamente.");
        } else {
            dismissDialog();
            mProgressDialog = Dialogos
                    .mostrarProgresso(this, "Por favor aguarde, importando cadastros de médicos",
                            false);

            // TODO extrair cpf para atributo e validar se o cpf esta presente
            service.getByCpf(propagandista.getCpf())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ImportaMedicosSubscriber());
        }
    }

    private static final String LOG_IMPORTA_MEDICOS =
            ImportaMedicosSubscriber.class.getSimpleName();

    /**
     * Observador e receptor dos cadastros de médicos recebidas do webservice.
     */
    private class ImportaMedicosSubscriber extends Subscriber<List<Medico>> {
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
        public void onNext(List<Medico> medicos) {
            if (medicos == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                if (medicos.isEmpty()) {
                    Dialogos.mostrarMensagem(SincronizarActivity.this,
                            "Importação dos médicos cadastrados",
                            "A importação concluiu sem nenhum registro de médico importado!");
                } else {
                    for (Medico medico : medicos) {
                        medico.setStatus(Status.Importado.ordinal());

                        if (mMedicoDAO.existe(medico.getId_unico())) {
                            mMedicoDAO.alterar(medico);
                        } else {
                            mMedicoDAO.incluir(medico);
                        }
                    }
                }
            }
        }
    }

    public void enviarMedicos() {
        final List<Medico> medicos = mMedicoDAO.listar(Status.Pendente);

        if (!medicos.isEmpty()) {
            final MedicoService service = createService(MedicoService.class, this);
            final Propagandista propagandista = PreferencesUtils.getUserLogged(this);

            if (service == null) {
                Dialogos.mostrarMensagem(this, "Sincronização dos dados",
                        "As configurações de sincronização não foram aplicadas corretamente.");
            } else {
                dismissDialog();
                mProgressDialog = Dialogos
                        .mostrarProgresso(this, "Por favor aguarde, enviando cadastros de médicos",
                                false);

                for (Medico medico : medicos) {
                    // TODO extrair cpf para atributo e validar se o cpf esta presente
                    service.put(propagandista.getCpf(), medico)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new EnviaMedicoSubscriber());
                }
            }
        }
    }

    private static final String LOG_ENVIA_MEDICOS =
            EnviaMedicoSubscriber.class.getSimpleName();

    /**
     * Observador e receptor da resposta do webservice após processar o envio dos
     * cadastros de médicos feitos localmente.
     */
    private class EnviaMedicoSubscriber extends Subscriber<Integer> {
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
        public void onNext(Integer idUnico) {
            dismissDialog();

            // TODO totalmente incorreto esta aplicação, o correto é o servidor retornar também
            // o código que o cliente gerou para controle interno
            if (idUnico > 0) {
                mMedico.setId_unico(idUnico);//Seta id unico
                mMedico.setStatus(Status.Enviado.ordinal());
                mMedicoDAO.alterar(mMedico);
            } else {
                Dialogos.mostrarMensagem(SincronizarActivity.this,
                        "Envio dos médicos cadastrados",
                        "Ocorreu um erro ao enviar médico!");
            }
        }
    }

    /**
     * Faz requisição uma GET para servidor solicitando as agendas cadastradas para
     * o propagandista logado.
     */
    public void importarAgendas() {
        final AgendaService service = createService(AgendaService.class, this);
        final Propagandista propagandista = PreferencesUtils.getUserLogged(this);

        if (service == null) {
            Dialogos.mostrarMensagem(this, "Sincronização dos dados",
                    "As configurações de sincronização não foram aplicadas corretamente.");
        } else {
            dismissDialog();
            mProgressDialog = Dialogos
                    .mostrarProgresso(this, "Por favor aguarde, importando cadastros de agendas",
                            false);

            // TODO extrair cpf para atributo e validar se o cpf esta presente
            service.getByCpf(propagandista.getCpf())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ImportaAgendasSubscriber());
        }
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

                        if(mAgendaDAO.existe(agenda.getIdAgenda())) {
                            mAgendaDAO.alterar(agenda);
                        } else {
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
    }
}