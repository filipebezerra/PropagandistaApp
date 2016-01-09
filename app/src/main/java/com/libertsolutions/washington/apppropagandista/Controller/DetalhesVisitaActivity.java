package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Dao.VisitaDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Visita;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import com.libertsolutions.washington.apppropagandista.api.models.VisitaModel;
import com.libertsolutions.washington.apppropagandista.api.services.AgendaService;
import com.libertsolutions.washington.apppropagandista.api.services.VisitaService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.Util.DateUtil.FormatType.DATE_AND_TIME;
import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

public class DetalhesVisitaActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String LOG = DetalhesVisitaActivity.class.getSimpleName();

    /**
     * Constante usada para requisitar o diálogo das configurações de localização.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Fornece a interface de comunicação com Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * O intervalo desejado para atualizações de localização. Inexato. As atualizações podem ser
     * mais ou menos frequentes.
     */
    private static final long POLLING_FREQ = 1000 * 30;

    /**
     * A taxa mais rápida para atualizações de localização ativos. Exato. Atualizações nunca serão
     * mais freqüentes do que esse valor.
     */
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;

    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    /**
     * Armazena os parametetros para requisições ao FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Armazena os tipos de serviços de localização que o cliente está interessado em usar.
     * Utilizado para verificar as configurações para determinar se o dispositivo tem as definições
     * de localização ideais.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates = false;

    private Location mUserLocation;

    private AgendaDAO mAgendaDAO;
    private MedicoDAO mMedicoDAO;
    private VisitaDAO mVisitaDAO;

    private Agenda mAgenda;
    private Visita mVisita;
    private long mIdAgenda;

    @Bind(R.id.root_layout)
    CoordinatorLayout mRootLayout;
    @Bind(R.id.status)
    TextView mStatus;
    @Bind(R.id.data_hora_view)
    TextView mDataHoraView;
    @Bind(R.id.medico_view)
    TextView mMedicoView;
    @Bind(R.id.obs_view)
    TextView mObservacaoView;
    @Bind(R.id.data_hora_ini_view)
    TextView mDataHoraInilView;
    @Bind(R.id.data_hora_final_view)
    TextView mDataHoraFinalView;
    @Bind(R.id.details_view)
    TextView mDetalhesView;
    @Bind(R.id.btnIniciar)
    Button btnIniciarVisita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra("id") ||
                getIntent().getExtras().getString("id") == null) {
            throw new IllegalStateException("O ID da agenda deve ser passado via putExtras()");
        } else {
            mIdAgenda = Long.parseLong(getIntent().getStringExtra("id"));
        }

        setContentView(R.layout.activity_detalhes_visita);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAgendaDAO = new AgendaDAO(this);
        mAgendaDAO.openDatabase();

        mMedicoDAO = new MedicoDAO(this);
        mMedicoDAO.openDatabase();

        mVisitaDAO = new VisitaDAO(this);
        mVisitaDAO.openDatabase();

        mAgenda = mAgendaDAO.consultar(mIdAgenda);
        if(mAgenda.getStatusAgenda() == StatusAgenda.EmAtendimento || mAgenda.getStatusAgenda() == StatusAgenda.NaoVisita
                || mAgenda.getStatusAgenda() == StatusAgenda.Finalizado)
        {
            mVisita = mVisitaDAO.consultar(VisitaDAO.COLUNA_RELACAO_AGENDA+" = ?",String.valueOf(mIdAgenda));
        }

        preencherDadosTela();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

            if (mRequestingLocationUpdates) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (mRequestingLocationUpdates) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }

            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAgendaDAO.closeDatabase();
        mMedicoDAO.closeDatabase();
        mVisitaDAO.closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visita, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle param = new Bundle();
        param.putString("id", String.valueOf(mIdAgenda));

        switch (item.getItemId()) {
            case R.id.nav_naovisita:
                if (mAgenda.getStatusAgenda() == StatusAgenda.EmAtendimento) {
                    Log.d(LOG, "Não Visita");
                    new MaterialDialog.Builder(this)
                            .title("Não Visita...")
                            .inputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE |
                                    InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                            .input("Informe o motivo da não visita.", null, false,
                                    new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog materialDialog,
                                                            CharSequence charSequence) {
                                            naoVisita(charSequence.toString());
                                        }
                                    })
                            .show();
                } else {
                    Dialogos.mostrarMensagemFlutuante(mRootLayout,
                            "Somente agendas Em Atendimento podem ser classificadas como Não Visita!",
                            false);
                }
                return true;

            case R.id.nav_cancelar:
                if (mAgenda.getStatusAgenda() == StatusAgenda.Pendente) {
                    Log.d(LOG, "Cancelar Compromisso");
                    new MaterialDialog.Builder(this)
                            .title("Cancelar Compromisso")
                            .autoDismiss(false)
                            .content("Deseja cancelar o compromisso?")
                            .positiveText("Confirmar")
                            .negativeText("Cancelar")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    mAgenda.setStatusAgenda(StatusAgenda.Cancelado);
                                    mAgendaDAO.alterar(mAgenda);
                                    btnIniciarVisita.setText("Visita Cancelada");
                                    btnIniciarVisita.setBackgroundResource(R.color.visita_cancelada);
                                    btnIniciarVisita.setEnabled(false);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                } else {
                    Dialogos.mostrarMensagemFlutuante(mRootLayout,
                            "Somente agendas Pendentes podem ser Canceladas!",
                            false);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    private synchronized void buildGoogleApiClient() {
        Log.i(LOG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(POLLING_FREQ);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        } else {
            return bestResult;
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(LOG, String.format("Play services availability result code %d", resultCode));

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

    @OnClick(R.id.btnIniciar)
    public void onClickBtnIniciar() {
        switch (mAgenda.getStatusAgenda()) {
            case Pendente:
                iniciarVisita();
                break;
            case EmAtendimento:
                finalizarVisita();
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "Connected to GoogleApiClient");

        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            // Get best last location measurement meeting criteria
            mUserLocation = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

            if (mUserLocation == null
                    || mUserLocation.getAccuracy() > MIN_LAST_READ_ACCURACY
                    || mUserLocation.getTime() < System.currentTimeMillis() - TWO_MIN) {
                requestLocationUpdates();
            }
        }
    }

    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        mRequestingLocationUpdates = true;

        // Schedule a runnable to unregister location listeners
        Executors
                .newScheduledThreadPool(1)
                .schedule(new Runnable() {
                    @Override
                    public void run() {
                        LocationServices
                                .FusedLocationApi
                                .removeLocationUpdates(mGoogleApiClient,
                                        DetalhesVisitaActivity.this);
                        mRequestingLocationUpdates = false;
                    }
                }, ONE_MIN, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG, "Connection with play services was suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG, "Connection with play services failed: ConnectionResult.getErrorCode() = " +
                result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG, String.format("Location changed. New location is: %f, %f",
                location.getLatitude(), location.getLongitude()));

        // Determine whether new location is better than current best
        // estimate
        if (mUserLocation == null || location.getAccuracy() < mUserLocation.getAccuracy()) {
            mUserLocation = location;

            if (mUserLocation.getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mRequestingLocationUpdates = false;
            }
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOG, "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(LOG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG, "User agreed to make required location settings changes.");
                        Dialogos.mostrarMensagemFlutuante(mRootLayout,
                                "Configurações de localização aplicadas com sucesso!", true);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG, "User chose not to make required location settings changes.");
                        Dialogos.mostrarMensagem(this, "Localização desconhecida",
                                "É necessário habilitar suas configurações de localização!");
                        break;
                }
                break;
        }
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, DetalhesVisitaActivity.class);
    }

    //Metódo para preencher a tela com os dados da Agenda
    private void preencherDadosTela() {
        mStatus.setText(mAgenda.getStatusAgenda().descricao());
        mDataHoraView.setText(DateUtil.format(mAgenda.getDataCompromisso(), DATE_AND_TIME));
        mMedicoView.setText(mMedicoDAO.consultar(MedicoDAO.COLUNA_ID_MEDICO + " = ?", mAgenda.getIdMedico().toString()).getNome());
        mObservacaoView.setText(mAgenda.getObservacao());

        switch (mAgenda.getStatusAgenda()) {
            case Pendente:
                btnIniciarVisita.setText("Iniciar Visita");
                btnIniciarVisita.setBackgroundResource(R.color.visita_pendente);
                break;
            case EmAtendimento:
                mDataHoraInilView.setText(DateUtil.format(mVisita.getDataInicio(), DATE_AND_TIME));
                btnIniciarVisita.setText("Finalizar Visita");
                btnIniciarVisita.setBackgroundResource(R.color.visita_ematendimento);
                break;
            case Finalizado:
                mDataHoraInilView.setText(DateUtil.format(mVisita.getDataInicio(), DATE_AND_TIME));
                mDataHoraFinalView.setText(DateUtil.format(mVisita.getDataFim(), DATE_AND_TIME));
                mDetalhesView.setText(mVisita.getDetalhes());
                btnIniciarVisita.setText("Visita Finalizada");
                btnIniciarVisita.setBackgroundResource(R.color.visita_finalizada);
                btnIniciarVisita.setEnabled(false);
                break;
            case Cancelado:
                btnIniciarVisita.setText("Visita Cancelada");
                btnIniciarVisita.setBackgroundResource(R.color.visita_cancelada);
                btnIniciarVisita.setEnabled(false);
                break;
            case NaoVisita:
                mDataHoraInilView.setText(DateUtil.format(mVisita.getDataInicio(), DATE_AND_TIME));
                mDataHoraFinalView.setText(DateUtil.format(mVisita.getDataFim(), DATE_AND_TIME));
                mDetalhesView.setText(mVisita.getDetalhes());
                btnIniciarVisita.setText("Não Visita");
                btnIniciarVisita.setBackgroundResource(R.color.visita_naovisita);
                btnIniciarVisita.setEnabled(false);
                break;
        }
    }

    private boolean localizacaoDisponivel() {
        if (mUserLocation == null) {
            Log.d(LOG, "Localização do usuário desconhecida");

            if (!servicesAvailable()) {
                Log.d(LOG, "Google play services não está disponível");
                Dialogos.mostrarMensagem(this, "Serviço indisponível",
                        "O serviço para obter sua localização não está disponível!");
                return false;
            }

            mUserLocation = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

            if (mUserLocation == null) {
                Dialogos.mostrarMensagem(this, "Localização não disponível",
                        "Não foi possível obter sua localização!");
                return false;
            }
        }

        return true;
    }

    private void iniciarVisita() {
        Log.d(LOG, "Iniciando a visita");
        Double longtitude = 0.0;
        Double latitude = 0.0;

        if (mUserLocation != null) {
            longtitude = mUserLocation.getLongitude();
            latitude = mUserLocation.getLatitude();
        }
        mVisita = new Visita();
        mVisita = Visita.iniciar(System.currentTimeMillis(),
                latitude,
                longtitude, mIdAgenda);
        mVisita.setLatFinal(0.0);
        mVisita.setLongFinal(0.0);
        final long id = mVisitaDAO.incluir(mVisita);

        Log.d(LOG, String.format("Visita incluída com id %d", id));
        if (id != -1) {
            mVisita.setId(id);
            mAgenda.setStatusAgenda(StatusAgenda.EmAtendimento);
            mAgendaDAO.alterar(mAgenda);

            mStatus.setText(mAgenda.getStatusAgenda().descricao());
            btnIniciarVisita.setText("Finalizar Visita");
            btnIniciarVisita.setBackgroundResource(R.color.visita_ematendimento);

            Dialogos.mostrarMensagemFlutuante(mRootLayout, "Visita iniciada...", true);

            // TODO extrair cpf para atributo e validar se o cpf esta presente
            final AgendaService service = createService(AgendaService.class, this);
            if(service != null) {
                final AgendaModel agendaModel = Agenda.toModel(mAgenda);
                service.post(agendaModel)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new AlteraAgendaSubscriber());
            }

        } else {
            Dialogos.mostrarMensagem(this, "Falha ao iniciar a vista",
                    "Não foi possível salvar os dados ao iniciar a visita!");
        }
    }

    private void finalizarVisita() {
        Log.d(LOG, "Finalizando a visita");

        new MaterialDialog.Builder(this)
                .title("Finalizando a visita...")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input("Informe os detalhes da visita realizada.", null, false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog materialDialog,
                                                CharSequence charSequence) {
                                finalizarVisita(charSequence.toString());
                            }
                        })
                .show();
    }

    private void finalizarVisita(@NonNull String detalhes) {
        Visita visitaEmAndamento = mVisitaDAO
                .consultar("id_agenda = ?", String.valueOf(mIdAgenda));

        if (visitaEmAndamento != null) {
            visitaEmAndamento.setDetalhes(detalhes);
            visitaEmAndamento.setDataFim(System.currentTimeMillis());
            if (mUserLocation != null) {
                visitaEmAndamento.setLatFinal(mUserLocation.getLatitude());
                visitaEmAndamento.setLongFinal(mUserLocation.getLongitude());
            }

            final int alteracoes = mVisitaDAO.alterar(visitaEmAndamento);

            if (alteracoes > 0) {
                mAgenda.setStatusAgenda(StatusAgenda.Finalizado);
                mAgendaDAO.alterar(mAgenda);

                Dialogos.mostrarMensagemFlutuante(mRootLayout, "Visita finalizada com sucesso",
                        true, new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                finish();
                            }
                        });
            } else {
                Dialogos.mostrarMensagem(this, "Falha ao finalizar a vista",
                        "Não foi possível salvar os dados ao finalizar a visita!");
            }
        }
    }

    private void naoVisita(@NonNull String detalhes) {
        Visita visitaEmAndamento = mVisitaDAO
                .consultar("id_agenda = ?", String.valueOf(mIdAgenda));

        if (visitaEmAndamento != null) {
            visitaEmAndamento.setDetalhes(detalhes);
            visitaEmAndamento.setDataFim(System.currentTimeMillis());
            if (mUserLocation != null) {
                visitaEmAndamento.setLatFinal(mUserLocation.getLatitude());
                visitaEmAndamento.setLongFinal(mUserLocation.getLongitude());
            }

            final int alteracoes = mVisitaDAO.alterar(visitaEmAndamento);

            if (alteracoes > 0) {
                mAgenda.setStatusAgenda(StatusAgenda.Finalizado);
                mAgendaDAO.alterar(mAgenda);

                Dialogos.mostrarMensagemFlutuante(mRootLayout, "Encerrado por não visita.",
                        true, new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                finish();
                            }
                        });
            } else {
                Dialogos.mostrarMensagem(this, "Falha ao finalizar a vista",
                        "Não foi possível salvar os dados ao finalizar a visita!");
            }
        }
    }

    private static final String LOG_ENVIO_VISITA =
            AlteraAgendaSubscriber.class.getSimpleName();

    private class AlteraAgendaSubscriber extends Subscriber<AgendaModel> {
        @Override
        public void onCompleted() {
            EnviaVisita();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_ENVIO_VISITA, "Falha na sincronização da alteração da agenda", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIO_VISITA, "Causa da falha", e.getCause());
            }
            Dialogos.mostrarMensagemFlutuante(mRootLayout, "Infelizmente não foi possível sincronizar os dados: " + e.getMessage(),
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

    private class EnviaVisitaSubscriber extends Subscriber<VisitaModel> {
        @Override
        public void onCompleted() {
            Log.d(LOG_ENVIO_VISITA, "Envio dos cadastros de médicos concluído");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_ENVIO_VISITA, "Falha no envio da Visita", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIO_VISITA, "Causa da falha", e.getCause());
            }
        }

        @Override
        public void onNext(VisitaModel model) {
            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Preconditions.checkNotNull(model.idCliente, "model.idVisita não pode ser nulo");

                final Visita visitaEnviado = Visita.fromModel(model);
                mVisitaDAO.alterar(visitaEnviado);
            }
        }
    }
}

    /*
    private class EnviaVisitaSubscriber extends Subscriber<VisitaModel> {
        @Override
        public void onCompleted() {
            Log.d(LOG_ENVIA_VISITA, "Envio dos cadastros de médicos concluído");
            EnviaAgenda();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_ENVIA_VISITA, "Falha no envio da Visita", e);
            if (e.getCause() != null) {
                Log.e(LOG_ENVIA_VISITA, "Causa da falha", e.getCause());
            }
        }

        @Override
        public void onNext(VisitaModel model) {
            if (model == null) {
                onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
            } else {
                Preconditions.checkNotNull(model.idCliente, "model.idVisita não pode ser nulo");

                final Visita visitaEnviado = Visita.fromModel(model);
                mVisitaDAO.alterar(visitaEnviado);
            }
        }

        public void EnviaAgenda() {
            final AgendaService service = createService(AgendaService.class, DetalhesVisitaActivity.this);
            if (service == null) {
                Mensagem.MensagemAlerta(DetalhesVisitaActivity.this, "As configurações de sincronização não foram aplicadas corretamente.");

            } else {
                // TODO extrair cpf para atributo e validar se o cpf esta presente
                service.post(Agenda.toModel(mAgenda))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new EnvioAgendaSubscriber());
            }
        }

        private class EnvioAgendaSubscriber extends Subscriber<AgendaModel> {
            @Override
            public void onCompleted() {
                Log.d(LOG_ENVIA_VISITA, "Sincronização alteração da agenda concluída");
                Mensagem.MensagemAlerta(DetalhesVisitaActivity.this,"Dados enviados com sucesso.");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_ENVIA_VISITA, "Falha na sincronização alteração da agenda", e);
                Mensagem.MensagemAlerta(DetalhesVisitaActivity.this, "Falha ao enviar Agenda.");
                if (e.getCause() != null) {
                    Log.e(LOG_ENVIA_VISITA, "Causa da falha", e.getCause());
                    Mensagem.MensagemAlerta(DetalhesVisitaActivity.this,e.getCause().getMessage());
                }
            }

            @Override
            public void onNext(AgendaModel model) {
                if (model == null) {
                    onError(new Exception("O servidor não respondeu corretamente à solicitação!"));
                } else {
                    Preconditions.checkNotNull(model.idAgenda, "model.idAgenda não pode ser nulo");
                    Preconditions.checkNotNull(model.statusAgenda,
                            "model.statusAgenda não pode ser nulo");

                    Agenda.fromModel(model);
                    mAgendaDAO.alterar(Agenda.fromModel(model));
                }
            }
        }
    }
}
*/