package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.DrawableUtil;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdapter;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;

/**
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 27/12/2015
 * @since 0.1.0
 */
public class AgendaActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> mListaAgenda = new ArrayList<>();
    private PersonalAdapter mPersonalAdapter;
    private ListView mAgendasListView;
    private boolean mIsLoadMore = false;
    private MaterialDialog mProgressDialog;
    private int mStart = 0;
    private int mLimit = 20;
    private String filter;

    private AgendaDAO mAgendaDAO;
    private MedicoDAO mMedicoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        mAgendaDAO = new AgendaDAO(this);
        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 2)
                {
                    filter = "(id_agenda||dt_compromisso||observacao) like '%"+newText+"%'";
                    mStart = 0;
                    mLimit = 20;
                    mPersonalAdapter = null;
                    mAgendasListView = (ListView)findViewById(R.id.lstMedicos);
                    mListaAgenda = new ArrayList<HashMap<String, String>>();
                    PreencheGrid(mStart,mLimit);
                }else
                {
                    filter = null;
                    mStart = 0;
                    mLimit = 20;
                    mPersonalAdapter = null;
                    mAgendasListView = (ListView)findViewById(R.id.lstAgenda);
                    mListaAgenda = new ArrayList<HashMap<String, String>>();
                    PreencheGrid(mStart,mLimit);
                }
                return false;
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        DrawableUtil.tint(this, menu.findItem(R.id.action_novo).getIcon(), R.color.white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_novo:
                Tela.AbrirTela(AgendaActivity.this, CadastroCompromissoActivity.class);
                //open Activity,Fragments or other action
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

        CarregaGrid();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAgendaDAO.closeDatabase();
        mMedicoDAO.closeDatabase();
    }

    //Carrega Grid
    public void CarregaGrid() {
        mStart = 0;
        mPersonalAdapter = null;
        mAgendasListView = (ListView)findViewById(R.id.lstAgenda);
        mListaAgenda = new ArrayList<>();

        //Preenche Grid com dados iniciais
        PreencheGrid(mStart, mLimit);
        mStart += 10;

        //Evento Click na Grid
        mAgendasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) mAgendasListView.getAdapter()
                        .getItem(position);
                Bundle param = new Bundle();
                param.putString("id", obj.get("id").toString());
                Tela.AbrirTela(AgendaActivity.this, CadastroCompromissoActivity.class, param);
            }
        });

        //Evento Scrool aparelho
        mAgendasListView.setOnScrollListener(new EndlessScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)) {
                    if (mIsLoadMore == false) {
                        mIsLoadMore = true;
                        new loadMoreListView().execute();
                    }
                }
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //new loadMoreListView().execute();
            }
        });
    }

    //Metódo para preencher Grid
    private void PreencheGrid(int start,int limit)
    {
        try
        {
            List<Agenda> lista = mAgendaDAO.listar(String.valueOf(start), String.valueOf(limit),filter);
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] {"id","col1", "col2","col3"};

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] {R.id.id,R.id.column1, R.id.column2,R.id.column3};

            for (Agenda agenda : lista) {
                HashMap<String, String> map = new HashMap<>();
                map.put(columnTags[0], String.valueOf(agenda.getId()));
                map.put(columnTags[1], mMedicoDAO.consultar(agenda.getIdMedico()).getNome());
                map.put(columnTags[2], "Data: " + new DateTime(agenda.getDataCompromisso()).toString("dd/MM/yyyy"));
                map.put(columnTags[3], "Obs: " + agenda.getObservacao());
                mListaAgenda.add(map);
            }

            int currentPosition = mAgendasListView.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            mPersonalAdapter = new PersonalAdapter(this, mListaAgenda, R.layout.cols_3,columnTags , columnIds);

            //Adiciona Array no ListView
            mAgendasListView.setAdapter(mPersonalAdapter);
            if(start > 1)
                mAgendasListView.setSelectionFromTop(currentPosition + 1, 0);
        }catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(), AgendaActivity.this);
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = Dialogos
                    .mostrarProgresso(AgendaActivity.this, "Carregando...", false);
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(mStart > 1)
                    {
                        mStart += 10;
                        // increment current page
                        PreencheGrid(mStart, mLimit);
                    }
                }
            });

            return (null);
        }


        protected void onPostExecute(Void unused) {
            // closing progress dialog
            mProgressDialog.dismiss();
        }
    }
}
