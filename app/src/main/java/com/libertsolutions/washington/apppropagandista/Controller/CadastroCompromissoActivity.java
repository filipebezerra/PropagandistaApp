package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnTouch;
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
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.DrawableUtil;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import com.libertsolutions.washington.apppropagandista.api.services.AgendaService;
import java.util.List;
import org.joda.time.DateTime;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import static android.os.Build.*;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

public class CadastroCompromissoActivity extends AppCompatActivity
    implements
        CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener {

    private static final int RC_ADICIONA_AGENDA = 1001;

    @Bind(R.id.root_layout) protected CoordinatorLayout mRootLayout;
    @Bind(R.id.toolbar) protected Toolbar mToolbar;

    @Bind(R.id.errorMedico) protected TextView mMedicoHint;
    @Bind(R.id.spinnerMedico) protected Spinner mMedicoView;
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
    private ArrayAdapter<Medico> mMedicosAdapter;

    private Intent mInsertCalendarIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_compromisso);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upIndicator = ContextCompat.getDrawable(this, R.drawable.ic_close);
        DrawableUtil.tint(this, upIndicator, R.color.white);
        getSupportActionBar().setHomeAsUpIndicator(upIndicator);

        if (savedInstanceState == null) {
            mHasDialogFrame = findViewById(R.id.frame) != null;
        }

        mMedicoDAO = new MedicoDAO(this);
        mAgendaDAO = new AgendaDAO(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMedicoDAO.openDatabase();
        final List<Medico> medicos = mMedicoDAO.listar();
        mMedicoDAO.closeDatabase();

        if (medicos != null) {
            mMedicosAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, medicos);
            mMedicosAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mMedicoView.setAdapter(mMedicosAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAgendaDAO.openDatabase();

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

    @Override
    protected void onPause() {
        super.onPause();

        mAgendaDAO.closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro_compromisso, menu);
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

    @OnItemSelected(R.id.spinnerMedico)
    public void onSpinnerMedicoSelected(final int position) {
        if (TextUtils.isEmpty(mDataCompromissoView.getText())) {
            showDatePicker();
        } else if (TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            showTimePicker();
        } else if (TextUtils.isEmpty(mObservacaoView.getText())) {
            mObservacaoView.requestFocus();
        }
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

    private Medico obterMedicoSelecionado() {
        return mMedicoView.getSelectedItemPosition()  == AdapterView.INVALID_POSITION ?
                null : (Medico) mMedicoView.getSelectedItem();
    }

    public void save() {
        boolean isFormValid = true;

        final Medico medicoSelecionado = obterMedicoSelecionado();

        if (medicoSelecionado == null) {
            isFormValid = false;
            mMedicoHint.setVisibility(View.VISIBLE);
        } else {
            mMedicoHint.setVisibility(View.GONE);
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
            novaAgenda.setIdMedico(medicoSelecionado.getIdMedico());
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
                novaAgenda.setId(idNovaAgenda);

                if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mInsertCalendarIntent = new Intent(Intent.ACTION_INSERT)
                            .setData(Events.CONTENT_URI)
                            .putExtra(EXTRA_EVENT_BEGIN_TIME, novaAgenda.getDataCompromisso())
                            .putExtra(Events.TITLE, String.format("Visita com %s", medicoSelecionado.getNome()))
                            .putExtra(Events.DESCRIPTION, novaAgenda.getObservacao())
                            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                            .putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE)
                            .putExtra(Intent.EXTRA_EMAIL, medicoSelecionado.getEmail());
                }

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
            return DateUtil.toDate(dateText);
        }

        return null;
    }

    private DateTime obtainTimeFromField() {
        if (!TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            final String timeText = mHorarioCompromissoView.getText().toString();
            return DateUtil.toTime(timeText);
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
        mDataCompromissoView.setText(DateUtil.format(year, monthOfYear, dayOfMonth));

        if (TextUtils.isEmpty(mHorarioCompromissoView.getText())) {
            showTimePicker();
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        mHorarioCompromissoView.setText(DateUtil.format(hourOfDay, minute));

        if (TextUtils.isEmpty(mObservacaoView.getText())) {
            mObservacaoView.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADICIONA_AGENDA) {
            if (resultCode == RESULT_OK) {
                Dialogos.mostrarMensagemFlutuante(mRootLayout,
                        "Compromisso incluído com sucesso em sua agenda", true,
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                finish();
                            }
                        });
            } else {
                finish();
            }
        }
    }

    private void adicionarNaAgenda() {
        if (mInsertCalendarIntent != null) {
            startActivityForResult(mInsertCalendarIntent, RC_ADICIONA_AGENDA);
        }
    }

    private void sincronizarNovaAgenda(Agenda novaAgenda) {
        final AgendaService service = createService(AgendaService.class, this);
        final Propagandista propagandista = PreferencesUtils.getUserLogged(this);

        if (service == null) {
            Dialogos.mostrarMensagemFlutuante(mRootLayout, "Sincronização dos dados As configurações de sincronização não foram aplicadas corretamente.",
                    false);
        } else {
            Dialogos.mostrarMensagemFlutuante(mRootLayout, "Por favor aguarde, sincronizando nova agenda...",
            false,new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            finish();
                        }
                    });

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
            adicionarNaAgenda();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_ENVIO_NOVA_AGENDA, "Falha na sincronização da nova agenda", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIO_NOVA_AGENDA, "Causa da falha", e.getCause());
            }
            Dialogos.mostrarMensagemFlutuante(mRootLayout,"Infelizmente não foi possível sincronizar os dados: "+e.getMessage(),
                    false,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            finish();
                        }
                    });
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

                Agenda.fromModel(model);
                mAgendaDAO.alterar(Agenda.fromModel(model));
            }
        }
    }
}
