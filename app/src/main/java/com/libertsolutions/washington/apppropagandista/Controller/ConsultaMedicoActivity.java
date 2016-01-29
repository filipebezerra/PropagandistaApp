package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdapter;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConsultaMedicoActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> lstMedicos = new ArrayList<>();
    private PersonalAdapter arrayAdapter;
    private ListView grdMedicos;
    private boolean isLoadMore = false;
    private ProgressDialog pDialog;
    private MedicoDAO mMedicoDAO;
    private int start = 0;
    private int limit = 20;
    private String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_medico);

        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(
                menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    filter = "lower(nome||telefone||secretaria) like '%" + newText.toLowerCase()
                            + "%'";
                    start = 0;
                    limit = 20;
                    arrayAdapter = null;
                    grdMedicos = (ListView) findViewById(R.id.lstMedicos);
                    lstMedicos = new ArrayList<>();
                    PreencheGrid(start, limit);
                } else {
                    filter = null;
                    start = 0;
                    limit = 20;
                    arrayAdapter = null;
                    grdMedicos = (ListView) findViewById(R.id.lstMedicos);
                    lstMedicos = new ArrayList<>();
                    PreencheGrid(start, limit);
                }
                return false;
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_novo:
                Tela.AbrirTela(ConsultaMedicoActivity.this, CadastroMedicoActivity.class);
                //open Activity,Fragments or other action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMedicoDAO.openDatabase();
        CarregaGrid();
    }

    @Override
    protected void onStop() {
        mMedicoDAO.closeDatabase();
        super.onStop();
    }

    //Carrega Grid
    public void CarregaGrid() {
        start = 0;
        arrayAdapter = null;
        grdMedicos = (ListView) findViewById(R.id.lstMedicos);
        lstMedicos = new ArrayList<>();

        //Preenche Grid com dados iniciais
        PreencheGrid(start, limit);
        start += 10;

        //Evento Click na Grid
        grdMedicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    HashMap<String, Object> obj = (HashMap<String, Object>) grdMedicos.getAdapter()
                            .getItem(position);
                    Medico medico = mMedicoDAO.consultar(Long.valueOf(obj.get("id").toString()));
                    Intent intent = new Intent();
                    intent.putExtra("id_medico", medico.getIdMedico());
                    intent.putExtra("nome", medico.getNome());
                    intent.putExtra("email", medico.getEmail());
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } catch (Exception erro) {
                    Mensagem.MensagemAlerta(ConsultaMedicoActivity.this, erro.getMessage());
                }
            }
        });

        //Evento Scrool aparelho
        grdMedicos.setOnScrollListener(new EndlessScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)) {
                    if (isLoadMore == false) {
                        isLoadMore = true;
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
    private void PreencheGrid(int start, int limit) {
        try {
            List<Medico> lista = new ArrayList<Medico>();
            lista = mMedicoDAO.listar(String.valueOf(start), String.valueOf(limit), filter);
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] { "id", "col1", "col2", "col3" };

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] { R.id.id, R.id.column1, R.id.column2, R.id.column3 };
            for (int i = 0; i < lista.size(); i++) {
                Medico medico = lista.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(columnTags[0], String.valueOf(medico.getIdMedico()));  //Id
                map.put(columnTags[1], medico.getNome());  //Nome
                map.put(columnTags[2], "Telefone: " + medico.getTelefone());  //Telefone
                map.put(columnTags[3], "Secretária: " + medico.getSecretaria());  //Secretária
                //Adiciona dados no Arraylist
                lstMedicos.add(map);
            }

            int currentPosition = grdMedicos.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            arrayAdapter = new PersonalAdapter(this, lstMedicos, R.layout.cols_3, columnTags,
                    columnIds);

            //Adiciona Array no ListView
            grdMedicos.setAdapter(arrayAdapter);
            if (start > 1) {
                grdMedicos.setSelectionFromTop(currentPosition + 1, 0);
            }
        } catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(),
                    ConsultaMedicoActivity.this);
        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    ConsultaMedicoActivity.this);
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