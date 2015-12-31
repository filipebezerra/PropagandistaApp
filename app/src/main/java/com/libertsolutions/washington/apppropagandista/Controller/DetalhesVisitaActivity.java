package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Dao.VisitaDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.R;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private Location mBestLocation;

    @NonNull private AgendaDAO mAgendaDAO;
    @NonNull private MedicoDAO mMedicoDAO;
    @NonNull private Agenda mAgenda;
    private int mIdAgenda;

    @Bind(R.id.data_hora_view) TextView mDataHoraView;
    @Bind(R.id.medico_view) TextView mMedicoView;
    @Bind(R.id.obs_view) TextView mObservacaoView;
    @Bind(R.id.acoes_visita) Button mAcoesVisitaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra("id") ||
                getIntent().getExtras().getString("id") == null) {
            throw new IllegalStateException("O ID da agenda deve ser passado via putExtras()");
        } else {
            mIdAgenda = Integer.parseInt(getIntent().getStringExtra("id"));
        }

        mAgendaDAO = new AgendaDAO(this);
        mMedicoDAO = new MedicoDAO(this);

        setContentView(R.layout.activity_detalhes_visita);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!servicesAvailable()) {
            finish();
        }

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAgendaDAO.openDatabase();
        mMedicoDAO.openDatabase();

        PreencheTela();

        initiliazeAcoesVisitaButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
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

    @OnClick(R.id.acoes_visita)
    public void acoesVisitaClick() {
        //TODO reimplementar
        /*
        switch (StatusAgenda.status(mAgenda.getStatus())) {
            case Pendente:
                break;
            case EmAtendimento:
                break;
            case Finalizado:
                break;
        }
        */
    }

    private void initiliazeAcoesVisitaButton() {
        //TODO reimplementar
        /*
        switch (StatusAgenda.status(mAgenda.getStatus())) {
            case Pendente:
                mAcoesVisitaButton.setText("Iniciar Visita");
                break;
            case EmAtendimento:
                mAcoesVisitaButton.setText("Finalizar Visita");
                break;
            case Finalizado:
                mAcoesVisitaButton.setText("Visita Finalizada");
                break;
        }
        */
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "Connected to GoogleApiClient");

        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            // Get best last location measurement meeting criteria
            mBestLocation = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

            if (mBestLocation == null
                    || mBestLocation.getAccuracy() > MIN_LAST_READ_ACCURACY
                    || mBestLocation.getTime() < System.currentTimeMillis() - TWO_MIN) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
                mRequestingLocationUpdates = true;

                // Schedule a runnable to unregister location listeners
                Executors.newScheduledThreadPool(1)
                        .schedule(new Runnable() {
                            @Override
                            public void run() {
                                LocationServices.FusedLocationApi.removeLocationUpdates(
                                        mGoogleApiClient, DetalhesVisitaActivity.this);
                                mRequestingLocationUpdates = false;
                            }
                        }, ONE_MIN, TimeUnit.MILLISECONDS);
            }
        }
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
        if (mBestLocation == null || location.getAccuracy() < mBestLocation.getAccuracy()) {
            mBestLocation = location;

            if (mBestLocation.getAccuracy() < MIN_ACCURACY) {
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
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, DetalhesVisitaActivity.class);
    }

    //Metódo para preencher a tela com os dados da Agenda
    public void PreencheTela()
    {
        Agenda agenda = mAgendaDAO.consultar(mIdAgenda);
        mDataHoraView.setText(agenda.getDataCompromisso().toString());
        mMedicoView.setText(mMedicoDAO.consultar(MedicoDAO.COLUNA_ID_MEDICO +" = ?",agenda.getIdMedico().toString()).getNome());
        mObservacaoView.setText(agenda.getObservacao());
    }
}
