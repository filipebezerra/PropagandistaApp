package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.DrawableUtil;
import com.libertsolutions.washington.apppropagandista.api.models.MedicoModel;
import com.libertsolutions.washington.apppropagandista.api.services.MedicoService;
import java.util.List;
import org.joda.time.DateTime;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO.COLUNA_ID_ESPECIALIDADE;
import static com.libertsolutions.washington.apppropagandista.Util.DateUtil.FormatType.DATE_ONLY;
import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

public class DetalhesMedicoActivity extends AppCompatActivity
        implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Bind(R.id.root_layout) ViewGroup mRootView;
    @Bind(R.id.hintNomeMedico) TextInputLayout mNomeMedicoHint;
    @Bind(R.id.txtNomeMedico) EditText mNomeMedicoView;
    @Bind(R.id.txtDtAniversario) EditText mDataAniversarioView;
    @Bind(R.id.txtSecretaria) EditText mNomeSecretariaView;
    @Bind(R.id.txtTelefone) EditText mTelefoneView;
    @Bind(R.id.txtEmail) EditText mEmailView;
    @Bind(R.id.txtCrm) EditText mCrmView;
    @Bind(R.id.hintEspecialidade) TextInputLayout mEspecialidadeHint;
    @Bind(R.id.txtEspecialidade) AutoCompleteTextView mEspecialidadeView;

    private EspecialidadeDAO mEspecialidadeDAO;
    private ArrayAdapter<Especialidade> mEspecialidadesAdapter;
    private Especialidade mEspecialidadeSelecionada;

    private MedicoDAO mMedicoDAO;
    private Long mIdMedico;
    private Medico mMedicoSelecionado;

    private MaterialDialog mProgressDialog;

    private boolean mHasDialogFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra("id") ||
                getIntent().getStringExtra("id").equals("0")) {
            throw new IllegalArgumentException(
                    "O id do médico deve ser passado via putExtra(String)");
        }

        mIdMedico = Long.valueOf(getIntent().getStringExtra("id"));

        setContentView(R.layout.activity_detalhes_medico);
        ButterKnife.bind(this);

        mMedicoDAO = new MedicoDAO(this);
        mEspecialidadeDAO = new EspecialidadeDAO(this);

        if (savedInstanceState == null) {
            mHasDialogFrame = findViewById(R.id.frame) != null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMedicoDAO.openDatabase();
        mEspecialidadeDAO.openDatabase();

        final List<Especialidade> especialidades = mEspecialidadeDAO.listar();
        if (especialidades != null) {
            mEspecialidadesAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, especialidades);
            mEspecialidadesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mEspecialidadeView.setThreshold(1);
            mEspecialidadeView.setAdapter(mEspecialidadesAdapter);
            mEspecialidadeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                        long id) {
                    mEspecialidadeSelecionada = mEspecialidadesAdapter.getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            });
        }

        mMedicoSelecionado = mMedicoDAO.consultar(mIdMedico);
        preencheTela(mMedicoSelecionado);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final CalendarDatePickerDialogFragment calendarDialogFragment =
                (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDialogFragment != null) {
            calendarDialogFragment.setOnDateSetListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMedicoDAO.closeDatabase();
        mEspecialidadeDAO.closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhes_medico, menu);
        DrawableUtil.tint(this, menu.findItem(R.id.action_salvar).getIcon(), R.color.white);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_salvar) {
            save();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private Especialidade getEspecialidadeByName(@NonNull String nomeEspecialidade) {
        for (int i = 0; i < mEspecialidadesAdapter.getCount() - 1; i++) {
            final Especialidade especialidade = mEspecialidadesAdapter.getItem(i);

            if (especialidade.getNome().equalsIgnoreCase(nomeEspecialidade)) {
                return especialidade;
            }
        }

        return null;
    }

    public void save() {
        if(validaTela()) {
        mMedicoSelecionado = getDados();
            mMedicoSelecionado.setStatus(Status.Alterado);
            final int alteracoes = mMedicoDAO.alterar(mMedicoSelecionado);

            if (alteracoes > 0) {
                Dialogos.mostrarMensagemFlutuante(mRootView, "Dados salvos com sucesso!", false);

                final MedicoService service = createService(MedicoService.class, this);

                if (service != null) {
                    service.post(Medico.toModel(mMedicoSelecionado, null))
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new EnviaMedicoSubscriber());
                }
            }
        }
    }

    public Medico getDados()
    {
        final Medico medico = new Medico();
        medico.setNome(mNomeMedicoView.getText().toString());

        if(!TextUtils.isEmpty(mDataAniversarioView.getText().toString()))
            medico.setDataAniversario(
                    DateUtil.toDateMillis(mDataAniversarioView.getText().toString()));

        if(!TextUtils.isEmpty(mNomeSecretariaView.getText().toString()))
            medico.setSecretaria(this.mNomeSecretariaView.getText().toString());

        if(!TextUtils.isEmpty(mTelefoneView.getText().toString()))
            medico.setTelefone(this.mTelefoneView.getText().toString());

        if(!TextUtils.isEmpty(mEmailView.getText().toString()))
            medico.setEmail(this.mEmailView.getText().toString());

        if(!TextUtils.isEmpty(mCrmView.getText().toString()))
            medico.setCrm(this.mCrmView.getText().toString());

        //if(!TextUtils.isEmpty(mEspecialidadeView.getText().toString()))
            //medico.setEspecialidade(this.mEspecialidadeView.getText().toString());

        return medico;
    }

    private boolean validaTela() {
        boolean isFormValid = true;

        if (TextUtils.isEmpty(mNomeMedicoView.getText())) {
            isFormValid = false;
            mNomeMedicoHint.setError("O nome do médico não foi informado");
            mNomeMedicoHint.setErrorEnabled(true);
        } else {
            mNomeMedicoHint.setError(null);
            mNomeMedicoHint.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mEspecialidadeView.getText())) {
            isFormValid = false;
            mEspecialidadeHint.setError("Nenhuma especialidade foi informado");
            mEspecialidadeHint.setEnabled(true);
        } else {
            if (mEspecialidadeSelecionada == null) {
                mEspecialidadeSelecionada = getEspecialidadeByName(mEspecialidadeView
                        .getText().toString());
            }

            if (mEspecialidadeSelecionada == null) {
                isFormValid = false;
                mEspecialidadeHint.setError("A especialidade informada não está cadastrada");
                mEspecialidadeHint.setEnabled(true);
            } else {
                mEspecialidadeHint.setError(null);
                mEspecialidadeHint.setEnabled(false);
            }
        }

        return isFormValid;
    }

    private void preencheTela(Medico medico) {
        mNomeMedicoView.setText(medico.getNome());

        if (medico.getDataAniversario() != null) {
            mDataAniversarioView.setText(DateUtil.format(medico.getDataAniversario(), DATE_ONLY));
        }

        mNomeSecretariaView.setText(medico.getSecretaria());

        if (!TextUtils.isEmpty(medico.getTelefone())) {
            /*try {
                final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                final Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil
                        .parse(medico.getTelefone(), "BR");
                final String telefone = phoneNumberUtil.format(phoneNumber,
                        PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                mTelefoneView.setText(telefone);
            } catch (NumberParseException e) {
                mTelefoneView.setText(medico.getTelefone());
            }*/
        }

        mEmailView.setText(medico.getEmail());
        mCrmView.setText(medico.getCrm());

        if (medico.getIdEspecialidade() != null) {
            final Especialidade especialidade = mEspecialidadeDAO.consultar(
                    COLUNA_ID_ESPECIALIDADE + " = ?",
                    String.valueOf(medico.getIdEspecialidade()));

            mEspecialidadeView.setText(especialidade != null ? especialidade.getNome() : "");
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @OnTouch(R.id.txtDtAniversario)
    public boolean onTxtDtAniversarioTouch(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showDatePicker();
            return true;
        }
        return false;
    }

    private DateTime obtainDateFromField() {
        if (!TextUtils.isEmpty(mDataAniversarioView.getText())) {
            final String dateText = mDataAniversarioView.getText().toString();
            return DateUtil.toDate(dateText);
        }

        return null;
    }

    private void showDatePicker() {
        int year, monthOfYear, dayOfMonth;
        final DateTime dateSet = obtainDateFromField();

        if (dateSet != null) {
            year = dateSet.getYear();
            monthOfYear = dateSet.getMonthOfYear();
            dayOfMonth = dateSet.getDayOfMonth();
        } else {
            final DateTime now = DateTime.now();
            year = now.getYear();
            monthOfYear = now.getMonthOfYear();
            dayOfMonth = now.getDayOfMonth();
        }

        CalendarDatePickerDialogFragment dialogFragment = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setPreselectedDate(year, monthOfYear - 1, dayOfMonth);

        if (mHasDialogFrame) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.frame, dialogFragment, FRAG_TAG_DATE_PICKER)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            dialogFragment.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
            int dayOfMonth) {
        mDataAniversarioView.setText(DateUtil.format(year, monthOfYear, dayOfMonth));
    }

    private static final String LOG_ENVIA_MEDICOS =
            EnviaMedicoSubscriber.class.getSimpleName();

    private class EnviaMedicoSubscriber extends Subscriber<MedicoModel> {
        @Override
        public void onCompleted() {
            dismissDialog();
            Log.d(LOG_ENVIA_MEDICOS, "Envio dos cadastros de médicos concluído");

            Dialogos.mostrarMensagem(DetalhesMedicoActivity.this, "Sincronização dos dados",
                    "Sincronização dos dados concluída com sucesso!",
                    new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    });
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_ENVIA_MEDICOS, "Falha no envio do cadastro de médico", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIA_MEDICOS, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(DetalhesMedicoActivity.this,
                    "Sincronização do cadastro do médico",
                    String.format("Infelizmente houve um erro e a sincronização não "
                            + "pôde ser completada. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(MedicoModel model) {
            dismissDialog();

            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Preconditions.checkNotNull(model.idCliente, "model.idCliente não pode ser nulo");

                final Medico medicoEnviado = Medico.fromModel(model);
                medicoEnviado.setStatus(Status.Enviado);
                mMedicoDAO.alterar(medicoEnviado);
            }
        }
    }
}