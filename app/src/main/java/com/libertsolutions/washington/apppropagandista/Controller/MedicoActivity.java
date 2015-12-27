package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

public class MedicoActivity extends ActionBarActivity {
    ArrayList<HashMap<String, String>> lstMedicos = new ArrayList<HashMap<String, String>>();
    PersonalAdapter arrayAdapter;
    ListView grdMedicos;
    private boolean isLoadMore = false;
    ProgressDialog pDialog;
    private MedicoDAO mMedicoDAO;
    int start = 0;
    int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_novo:
                Tela.AbrirTela(MedicoActivity.this, CadastroMedicoActivity.class);
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

        try {
            CarregaGrid();
        }
        catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Erro Start Produtos", erro.getMessage(), MedicoActivity.this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMedicoDAO.closeDatabase();
    }

    //Carrega Grid
    public void CarregaGrid()
    {
        start = 0;
        arrayAdapter = null;
        grdMedicos = (ListView)findViewById(R.id.lstMedicos);
        lstMedicos = new ArrayList<HashMap<String, String>>();

        //Preenche Grid com dados iniciais
        PreencheGrid(start,limit);
        start += 10;

        //Evento Click na Grid
        grdMedicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>)grdMedicos.getAdapter().getItem(position);
                Bundle param = new Bundle();
                param.putString("id",obj.get("id").toString());
                Tela.AbrirTela(MedicoActivity.this,DetalhesMedicoActivity.class,param);
            }
        });

        //Evento Scrool aparelho
        grdMedicos.setOnScrollListener(new EndlessScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if((lastInScreen == totalItemCount)){
                    if(isLoadMore == false)
                    {
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
    private void PreencheGrid(int start,int limit)
    {
        try
        {
            List<Medico> lista = new ArrayList<Medico>();
            lista = mMedicoDAO.listar(String.valueOf(start), String.valueOf(limit));
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] {"id","col1", "col2","col3"};

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] {R.id.id,R.id.column1, R.id.column2,R.id.column3};
            for (int i = 0; i < lista.size();i++)
            {
                Medico medico = lista.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(columnTags[0],String.valueOf(medico.getIdMedico()));  //Id
                map.put(columnTags[1],medico.getNome());  //Nome
                map.put(columnTags[2], "Telefone: " + medico.getTelefone());  //Telefone
                map.put(columnTags[3], "Secretária: " + medico.getSecretaria());  //Secretária
                //Adiciona dados no Arraylist
                lstMedicos.add(map);
            }

            int currentPosition = grdMedicos.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            arrayAdapter = new PersonalAdapter(this, lstMedicos, R.layout.cols_3,columnTags , columnIds);

            //Adiciona Array no ListView
            grdMedicos.setAdapter(arrayAdapter);
            if(start > 1)
                grdMedicos.setSelectionFromTop(currentPosition + 1, 0);
        }catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(), MedicoActivity.this);

        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    MedicoActivity.this);
            pDialog.setMessage("Carregando...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(start > 1)
                    {
                        start += 10;
                        // increment current page
                        PreencheGrid(start,limit);
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