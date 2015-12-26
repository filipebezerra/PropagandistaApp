package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTouch;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import com.libertsolutions.washington.apppropagandista.api.services.AgendaService;
import java.util.Calendar;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class CadastroCompromissoActivity extends AppCompatActivity
    implements
        CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener {

    @Bind(R.id.root_layout) protected CoordinatorLayout mRootLayout;
    @Bind(R.id.toolbar) protected Toolbar mToolbar;

    @Bind(R.id.hintMedico) protected TextInputLayout mMedicoHint;
    @Bind(R.id.txtMedico) protected AutoCompleteTextView mMedicoView;
    @Bind(R.id.hintDataCompromisso) protected TextInputLayout mDataCompromissoHint;
    @Bind(R.id.txtDataCompromisso) protected EditText mDataCompromissoView;
    @Bind(R.id.hintHorarioCompromisso) protected TextInputLayout mHorarioCompromissoHint;
    @Bind(R.id.txtHorarioCompromisso) protected EditText mHorarioCompromissoView;
    @Bind(R.id.hintObservacao) protected TextInputLayout mObservacaoHint;
    @Bind(R.id.txtObservacao) protected EditText mObservacaoView;

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    private boolean mHasDialogFrame;

    private AgendaDAO mAgendaDAO;

    private MedicoDAO mMedicoDAO;
    private Medico mMedicoSelecionado;

    private ArrayAdapter<Medico> mMedicosAdapter;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_compromisso);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mHasDialogFrame = findViewById(R.id.frame) != null;
        }

        mMedicoDAO = new MedicoDAO(this);
        mAgendaDAO = new AgendaDAO(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final List<Medico> medicos = mMedicoDAO.Listar();
        mMedicosAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, medicos);
        mMedicosAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mMedicoView.setThreshold(1);
        mMedicoView.setAdapter(mMedicosAdapter);
        mMedicoView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMedicoSelecionado = mMedicosAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
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

        final RadialTimePickerDialogFragment radialDialogFragment = (RadialTimePickerDialogFragment)
                getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER);
        if (radialDialogFragment != null) {
            radialDialogFragment.setOnTimeSetListener(this);
        }
    }

    @OnEditorAction(R.id.txtMedico)
    public boolean onTxtMedicoEditorAction(final int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (TextUtils.isEmpty(mDataCompromissoView.getText())) {
                showDatePicker();
            } else if (TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
                showTimePicker();
            } else if (TextUtils.isEmpty(mObservacaoView.getText())) {
                mObservacaoView.requestFocus();
            }
            return true;
        }
        return false;
    }

    @OnTouch({ R.id.txtDataCompromisso, R.id.txtHorarioCompromisso })
    public boolean onTouchInView(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.txtDataCompromisso:
                    showDatePicker();
                    return true;

                case R.id.txtHorarioCompromisso:
                    showTimePicker();
                    return true;

                default:
                    return false;
            }
        }
        return false;
    }

    private Medico getMedicoByName(@NonNull String nomeMedico) {
        for (int i = 0; i < mMedicosAdapter.getCount() - 1; i++) {
            final Medico medico = mMedicosAdapter.getItem(i);

            if (medico.getNome().equalsIgnoreCase(nomeMedico)) {
                return medico;
            }
        }

        return null;
    }

    @OnClick(R.id.btnSalvar)
    public void onBtnSalvarClick() {
        boolean isFormValid = true;

        if (TextUtils.isEmpty(mMedicoView.getText())) {
            isFormValid = false;
            mMedicoHint.setError("Nenhum médico foi informado");
            mMedicoHint.setEnabled(true);
        } else {
            if (mMedicoSelecionado == null) {
                mMedicoSelecionado = getMedicoByName(mMedicoView.getText().toString());
            }

            if (mMedicoSelecionado == null) {
                isFormValid = false;
                mMedicoHint.setError("O médico informado não está cadastrado");
                mMedicoHint.setEnabled(true);
            } else {
                mMedicoHint.setError(null);
                mMedicoHint.setEnabled(false);
            }
        }

        if (TextUtils.isEmpty(mDataCompromissoView.getText())) {
            isFormValid = false;
            mDataCompromissoHint.setError("Nenhuma data foi informada");
            mDataCompromissoHint.setErrorEnabled(true);
        } else {
            mDataCompromissoHint.setError(null);
            mDataCompromissoHint.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            isFormValid = false;
            mHorarioCompromissoHint.setError("Nenhum horário foi informado");
            mHorarioCompromissoHint.setErrorEnabled(true);
        } else {
            mHorarioCompromissoHint.setError(null);
            mHorarioCompromissoHint.setErrorEnabled(false);
        }

        if (isFormValid) {
            final Agenda novaAgenda = new Agenda();
            novaAgenda.setIdMedico(mMedicoSelecionado.getId_medico());
            novaAgenda.setDataCompromisso(obtainDateInMillesFromFields());
            novaAgenda.setStatusAgenda(StatusAgenda.Pendente);
            novaAgenda.setStatus(Status.Pendente);

            if (!TextUtils.isEmpty(mObservacaoView.getText())) {
                novaAgenda.setObservacao(mObservacaoView.getText().toString());
            }

            final long idNovaAgenda = mAgendaDAO.incluir(novaAgenda);

            if (idNovaAgenda == -1) {
                Snackbar.make(mRootLayout, "Houve um erro ao salvar a agenda. Tente novamente!",
                        LENGTH_LONG).show();
            } else {
                sincronizarNovaAgenda(novaAgenda);
            }
        } else {
            Snackbar.make(mRootLayout, "Preencha os campos requeridos para salvar!", LENGTH_LONG)
                    .show();
        }
    }

    private Long obtainDateInMillesFromFields() {
        final DateTime dateSet = obtainDateFromField();
        final DateTime timeSet = obtainTimeFromField();

        if (dateSet != null && timeSet != null) {
            return dateSet.withTime(timeSet.toLocalTime()).getMillis();
        }

        return null;
    }

    private DateTime obtainDateFromField() {
        if (!TextUtils.isEmpty(mDataCompromissoView.getText())) {
            final String dateText = mDataCompromissoView.getText().toString();
            return DateTime.parse(dateText, forPattern("dd/MM/yyyy"));
        }

        return null;
    }

    private DateTime obtainTimeFromField() {
        if (!TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            final String timeText = mHorarioCompromissoView.getText().toString();
            return DateTime.parse(timeText, forPattern("HH:mm"));
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

        final CalendarDatePickerDialogFragment dialogFragment = CalendarDatePickerDialogFragment
                .newInstance(this, year, monthOfYear - 1, dayOfMonth);

        if (mHasDialogFrame) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.frame, dialogFragment, FRAG_TAG_DATE_PICKER)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            dialogFragment.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        }
    }

    private void showTimePicker() {
        int hourOfDay, minuteOfHour;
        final DateTime timeSet = obtainTimeFromField();

        if (timeSet != null) {
            hourOfDay = timeSet.getHourOfDay();
            minuteOfHour = timeSet.getMinuteOfHour();
        } else {
            final DateTime now = DateTime.now();
            hourOfDay = now.getHourOfDay();
            minuteOfHour = now.getMinuteOfHour();
        }

        final RadialTimePickerDialogFragment dialogFragment = RadialTimePickerDialogFragment
                .newInstance(this, hourOfDay, minuteOfHour, DateFormat.is24HourFormat(this));

        if (mHasDialogFrame) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.frame, dialogFragment, FRAG_TAG_TIME_PICKER)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            dialogFragment.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
            int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        final String date = LocalDate.fromCalendarFields(calendar).toString("dd/MM/yyyy");
        mDataCompromissoView.setText(date);

        if (TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            showTimePicker();
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        final String time = LocalTime.fromCalendarFields(calendar).toString("HH:mm");
        mHorarioCompromissoView.setText(time);

        if (TextUtils.isEmpty(mObservacaoView.getText())) {
            mObservacaoView.requestFocus();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void sincronizarNovaAgenda(Agenda novaAgenda) {
        final AgendaService service = createService(AgendaService.class, this);
        final Propagandista propagandista = PreferencesUtils.getUserLogged(this);

        if (service == null) {
            Dialogos.mostrarMensagem(this, "Sincronização dos dados",
                    "As configurações de sincronização não foram aplicadas corretamente.");
        } else {
            dismissDialog();
            mProgressDialog = Dialogos
                    .mostrarProgresso(this, "Por favor aguarde, sincronizando nova agenda...",
                            false);

            final AgendaModel agendaModel = Agenda.toModel(novaAgenda);

            // TODO extrair cpf para atributo e validar se o cpf esta presente
            service.put(propagandista.getCpf(), agendaModel)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new EnvioNovaAgendaSubscriber());
        }
    }

    private static final String LOG_ENVIO_NOVA_AGENDA =
            EnvioNovaAgendaSubscriber.class.getSimpleName();

    private class EnvioNovaAgendaSubscriber extends Subscriber<AgendaModel> {
        @Override
        public void onCompleted() {
            dismissDialog();
            Log.d(LOG_ENVIO_NOVA_AGENDA, "Sincronização da nova agenda concluído");
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(LOG_ENVIO_NOVA_AGENDA, "Falha na sincronização da nova agenda", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIO_NOVA_AGENDA, "Causa da falha", e.getCause());
            }

            Dialogos.mostrarMensagem(CadastroCompromissoActivity.this,
                    "Sincronização da nova agenda",
                    String.format("Infelizmente houve um erro e a sincronização não "
                            + "pôde ser completada. Erro: %s", e.getMessage()));
        }

        @Override
        public void onNext(AgendaModel model) {
            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Preconditions.checkNotNull(model.idCliente, "model.idCliente não pode ser nulo");
                Preconditions.checkNotNull(model.idAgenda, "model.idAgenda não pode ser nulo");
                Preconditions.checkNotNull(model.statusAgenda,
                        "model.statusAgenda não pode ser nulo");

                mAgendaDAO.alterar(Agenda.fromModel(model));
            }
        }
    }
}
