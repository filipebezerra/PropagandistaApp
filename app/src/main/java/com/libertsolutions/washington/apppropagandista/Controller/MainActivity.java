package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Navigator;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdapter;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO.COLUNA_STATUS_AGENDA;
import static com.libertsolutions.washington.apppropagandista.Util.DateUtil.FormatType.DATE_AND_TIME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SETTINGS = 100;
    private static final int RC_LOGIN = 101;

    @Bind(R.id.lstPrincipal) ListView mListView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) NavigationView mNavigationView;

    private ArrayList<HashMap<String, String>> mListaAgenda = new ArrayList<>();
    private PersonalAdapter mPersonalAdapter;
    private boolean mIsLoadMore = false;
    private MaterialDialog mProgressDialog;
    private int mStart = 0;
    private int mLimit = 20;

    private AgendaDAO mAgendaDAO;
    private MedicoDAO mMedicoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        if (PreferencesUtils.getSyncUrlSettings(this) == null ||
                PreferencesUtils.getSyncAuthKeySettings(this) == null) {
            Navigator.navigateToSettings(this, RC_SETTINGS);
        } else if (!PreferencesUtils.isUserLogged(this)) {
            Navigator.navigateToLogin(this, RC_LOGIN);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAgendaDAO = new AgendaDAO(this);
        mAgendaDAO.openDatabase();

        mMedicoDAO = new MedicoDAO(this);
        mMedicoDAO.openDatabase();

        CarregaGrid();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAgendaDAO.closeDatabase();
        mMedicoDAO.closeDatabase();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SETTINGS:
                if (PreferencesUtils.getSyncUrlSettings(this) == null ||
                        PreferencesUtils.getSyncAuthKeySettings(this) == null) {
                    finish();
                } else if (!PreferencesUtils.isUserLogged(this)) {
                    Navigator.navigateToLogin(this, RC_LOGIN);
                }
                break;
            case RC_LOGIN:
                final Propagandista userLogged = PreferencesUtils.getUserLogged(this);
                if (userLogged != null) {
                    ButterKnife.<TextView>
                            findById(mNavigationView.getHeaderView(0), R.id.user_name)
                            .setText(userLogged.getNome());
                    ButterKnife.<TextView>
                            findById(mNavigationView.getHeaderView(0), R.id.user_email)
                            .setText(userLogged.getEmail());
                } else {
                    finish();
                }
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Menu Agenda
        if (id == R.id.nav_agenda) {
            Tela.AbrirTela(MainActivity.this, AgendaActivity.class);
            //Menu Médicos
        } else if (id == R.id.nav_medico) {
            Tela.AbrirTela(MainActivity.this, MedicoActivity.class);
        } else if (id == R.id.nav_config) {
            Tela.AbrirTela(MainActivity.this, ConfiguracaoActivity.class);
        } else if (id == R.id.nav_sincronizar) {
            Tela.AbrirTela(MainActivity.this, SincronizarActivity.class);
        } else if (id == R.id.nav_sair) {
            PreferencesUtils.logoutUser(this);
            this.finish();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void CarregaGrid() {
        mStart = 0;
        mPersonalAdapter = null;
        mListaAgenda = new ArrayList<>();

        //Preenche Grid com dados iniciais
        PreencheGrid(mStart, mLimit);
        mStart += 10;

        //Evento Click na Grid
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) mListView.getAdapter()
                        .getItem(position);
                final Bundle extras = new Bundle();
                extras.putString("id", obj.get("id").toString());
                final Intent launcherIntent = DetalhesVisitaActivity.getLauncherIntent(
                        MainActivity.this);
                launcherIntent.putExtras(extras);
                startActivity(launcherIntent);
            }
        });

        //Evento Scrool aparelho
        mListView.setOnScrollListener(new EndlessScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)) {
                    if (!mIsLoadMore) {
                        mIsLoadMore = true;
                        new loadMoreListView().execute();
                    }
                }
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            }
        });
    }

    private void PreencheGrid(int start, int limit) {
        List<Agenda> lista = mAgendaDAO.listar(String.valueOf(mStart), String.valueOf(mLimit),
                COLUNA_STATUS_AGENDA + " in(?,?)", String.valueOf(StatusAgenda.Pendente.ordinal()),String.valueOf(StatusAgenda.EmAtendimento.ordinal()));

        if (lista != null) {
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] { "status","id", "col1", "col2", "col3" };
            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] {R.id.status, R.id.id, R.id.column1, R.id.column2, R.id.column3 };

            for (Agenda agenda : lista) {
                HashMap<String, String> map = new HashMap<>();
                map.put(columnTags[0], agenda.getStatusAgenda().name());
                map.put(columnTags[1], String.valueOf(agenda.getId()));

                final Medico medico = mMedicoDAO.consultar(MedicoDAO.COLUNA_ID_MEDICO + " = ?",
                        String.valueOf(agenda.getIdMedico()));

                map.put(columnTags[2], medico != null ? medico.getNome() : "");
                map.put(columnTags[3], "Data: " + DateUtil.format(agenda.getDataCompromisso(),
                        DATE_AND_TIME));
                map.put(columnTags[4], "Obs: " + agenda.getObservacao());

                mListaAgenda.add(map);
            }

            int currentPosition = mListView.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            mPersonalAdapter = new PersonalAdapter(this, mListaAgenda, R.layout.cols_3_status, columnTags,
                    columnIds);

            //Adiciona Array no ListView
            mListView.setAdapter(mPersonalAdapter);
            if (mStart > 1) {
                mListView.setSelectionFromTop(currentPosition + 1, 0);
            }
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = Dialogos
                    .mostrarProgresso(MainActivity.this, "Carregando compromissos...", false);
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mStart > 1) {
                        mStart += 10;
                        // increment current page
                        PreencheGrid(mStart, mLimit);
                    }
                }
            });

            return (null);
        }

        protected void onPostExecute(Void unused) {
            mProgressDialog.dismiss();
        }
    }
}
