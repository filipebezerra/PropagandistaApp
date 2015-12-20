package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdapter;
import com.libertsolutions.washington.apppropagandista.Util.Tela;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;

public class AgendaActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> lstAgenda = new ArrayList<HashMap<String, String>>();
    PersonalAdapter arrayAdapter;
    ListView grdAgenda;
    private boolean isLoadMore = false;
    ProgressDialog pDialog;
    private AgendaDAO agendaDb;
    int start = 0;
    int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_agenda);

            this.agendaDb = new AgendaDAO(this);
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta(this, erro.getMessage());
        }
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
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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
        try {
            CarregaGrid();
        }
        catch (Exception erro)
        {
            Mensagem.MensagemAlerta("Erro Start Produtos", erro.getMessage(), AgendaActivity.this);
        }
    }

    //Carrega Grid
    public void CarregaGrid()
    {
        start = 0;
        arrayAdapter = null;
        grdAgenda = (ListView)findViewById(R.id.lstAgenda);
        lstAgenda = new ArrayList<HashMap<String, String>>();

        //Preenche Grid com dados iniciais
        PreencheGrid(start,limit);
        start += 10;

        //Evento Click na Grid
        grdAgenda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>)grdAgenda.getAdapter().getItem(position);
                Bundle param = new Bundle();
                param.putString("id",obj.get("id").toString());
                Tela.AbrirTela(AgendaActivity.this,CadastroCompromissoActivity.class,param);
            }
        });

        //Evento Scrool aparelho
        grdAgenda.setOnScrollListener(new EndlessScrollListener() {
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
            List<Agenda> lista = new ArrayList<Agenda>();
            lista = agendaDb.Listar(String.valueOf(start),String.valueOf(limit));
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] {"id","col1", "col2","col3"};

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] {R.id.id,R.id.column1, R.id.column2,R.id.column3};
            for (int i = 0; i < lista.size();i++)
            {
                Agenda agenda = lista.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(columnTags[0],String.valueOf(agenda.getId_agenda()));  //Id
                map.put(columnTags[1],agenda.getId_medico().getNome());  //Médico
                map.put(columnTags[2], "Data: " + agenda.getData()+" "+agenda.getHora());  //Data e Horário
                map.put(columnTags[3], "Obs: " + agenda.getObs());  //Observação
                //Adiciona dados no Arraylist
                lstAgenda.add(map);
            }

            int currentPosition = grdAgenda.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            arrayAdapter = new PersonalAdapter(this, lstAgenda, R.layout.cols_3,columnTags , columnIds);

            //Adiciona Array no ListView
            grdAgenda.setAdapter(arrayAdapter);
            if(start > 1)
                grdAgenda.setSelectionFromTop(currentPosition + 1, 0);
        }catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(), AgendaActivity.this);

        }
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    AgendaActivity.this);
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
