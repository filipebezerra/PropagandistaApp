package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.ProgressDialog;
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
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Navigator;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdapter;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SETTINGS = 100;
    private static final int RC_LOGIN = 101;

    @Bind(R.id.lstPrincipal) ListView mListView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) NavigationView mNavigationView;

    ArrayList<HashMap<String, String>> lstAgenda = new ArrayList<HashMap<String, String>>();
    PersonalAdapter arrayAdapter;
    private boolean isLoadMore = false;
    ProgressDialog pDialog;
    private AgendaDAO agendaDb;
    int start = 0;
    int limit = 20;

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
        this.agendaDb = new AgendaDAO(this);
        CarregaGrid();
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
        start = 0;
        arrayAdapter = null;
        lstAgenda = new ArrayList<>();

        //Preenche Grid com dados iniciais
        PreencheGrid(start, limit);
        start += 10;

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
                    if (!isLoadMore) {
                        isLoadMore = true;
                        new loadMoreListView().execute();
                    }
                }
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            }
        });
    }

    //Metódo para preencher Grid
    private void PreencheGrid(int start, int limit) {
        //TODO reimplementar
        /*
        try {
            List<Agenda> lista = new ArrayList<>();
            lista = agendaDb.listar(String.valueOf(start), String.valueOf(limit), "statusagenda=?",
                    "1");
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] { "id", "col1", "col2", "col3" };

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] { R.id.id, R.id.column1, R.id.column2, R.id.column3 };
            for (int i = 0; i < lista.size(); i++) {
                Agenda agenda = lista.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(columnTags[0], String.valueOf(agenda.getId()));  //Id
                map.put(columnTags[1], agenda.getIdMedico().getNome());  //Médico
                map.put(columnTags[2],
                        "Data: " + agenda.getData() + " " + agenda.getHora());  //Data e Horário
                map.put(columnTags[3], "Obs: " + agenda.getObservacao());  //Observação
                //Adiciona dados no Arraylist
                lstAgenda.add(map);
            }

            int currentPosition = mListView.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            arrayAdapter = new PersonalAdapter(this, lstAgenda, R.layout.cols_3, columnTags,
                    columnIds);

            //Adiciona Array no ListView
            mListView.setAdapter(arrayAdapter);
            if (start > 1) {
                mListView.setSelectionFromTop(currentPosition + 1, 0);
            }
        } catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(), MainActivity.this);
        }
        */
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Carregando...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (start > 1) {
                        start += 10;
                        // increment current page
                        PreencheGrid(start, limit);
                    }
                }
            });

            return (null);
        }

        protected void onPostExecute(Void unused) {
            // closing progress dialog
            pDialog.dismiss();
        }
    }
}
