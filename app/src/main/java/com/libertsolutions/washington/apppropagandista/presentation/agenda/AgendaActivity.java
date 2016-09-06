package com.libertsolutions.washington.apppropagandista.presentation.agenda;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.DrawableUtil;
import com.libertsolutions.washington.apppropagandista.presentation.util.Navigator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Washington, Filipe Bezerra
 */
public class AgendaActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) protected Toolbar mToolbar;
    @BindView(R.id.recycler_view_agendas) protected RecyclerView mRecyclerViewAgendas;

    private AgendaAdapter mAgendaAdapter;

    private AgendaDAO mAgendaDAO;

    private MedicoDAO mMedicoDAO;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerViewAgendas.setHasFixedSize(true);
        mRecyclerViewAgendas.setLayoutManager(new LinearLayoutManager(this));

        mAgendaDAO = new AgendaDAO(this);
        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAgendaAdapter != null) {
                    mAgendaAdapter.getFilter().filter(newText);
                    return true;
                }
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        DrawableUtil.tint(this, menu.findItem(R.id.action_novo).getIcon(), R.color.white);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(mAgendaAdapter != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_novo:
                Navigator.toNovoCompromisso(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAgendaDAO.openDatabase();
        mMedicoDAO.openDatabase();
        carregarAgendas();
    }

    private void carregarAgendas() {
        new AsyncTask<Void, Void, List<Pair<Agenda, Medico>>>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Dialogos
                        .mostrarProgresso(AgendaActivity.this, "Carregando agendas...", false);
            }

            @Override
            protected List<Pair<Agenda, Medico>> doInBackground(Void... voids) {
                List<Pair<Agenda, Medico>> list = new ArrayList<>();

                List<Agenda> agendas = mAgendaDAO.listar();
                if (agendas != null && !agendas.isEmpty()) {
                    for (Agenda agenda : agendas) {
                        Medico medico = mMedicoDAO.consultar(MedicoDAO.COLUNA_ID_MEDICO + " = ?",
                                agenda.getIdMedico().toString());

                        list.add(Pair.create(agenda, medico));
                    }

                    return list;
                }

                return Collections.emptyList();
            }

            @Override
            protected void onPostExecute(List<Pair<Agenda, Medico>> dadosAgenda) {
                if (!dadosAgenda.isEmpty()) {
                    mAgendaAdapter = new AgendaAdapter(AgendaActivity.this, dadosAgenda);
                    mRecyclerViewAgendas.setAdapter(mAgendaAdapter);
                    invalidateOptionsMenu();
                }
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAgendaDAO.closeDatabase();
        mMedicoDAO.closeDatabase();
    }
}
