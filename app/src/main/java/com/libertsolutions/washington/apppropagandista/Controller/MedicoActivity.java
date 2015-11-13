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
import android.widget.ListView;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.EndlessScrollListener;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PersonalAdpater;
import com.libertsolutions.washington.apppropagandista.Util.Tela;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicoActivity extends ActionBarActivity {
    ArrayList<HashMap<String, String>> lstMedicos = new ArrayList<HashMap<String, String>>();
    PersonalAdpater arrayAdapter;
    ListView grdMedicos;
    ProgressDialog pDialog;
    private MedicoDAO medicoDb;
    int start = 0;
    int limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_medico);

            this.medicoDb = new MedicoDAO(this);
            //CarregaGrid();
        }catch (Exception erro)
        {
            Mensagem.MensagemAlerta(this,erro.getMessage());
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
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_novo:
                Tela.AbrirTela(MedicoActivity.this, Medico_Cadastrar.class);
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
            Mensagem.MensagemAlerta("Erro Start Produtos", erro.getMessage(), MedicoActivity.this);
        }
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

        grdMedicos.setOnScrollListener(new EndlessScrollListener() {
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
            lista = medicoDb.Listar(String.valueOf(start),String.valueOf(limit));
            //Cria array com quantidade de colunas da ListView
            String[] columnTags = new String[] {"col1", "col2","col3"};

            //Recupera id das colunas do layout list_itens_ped
            int[] columnIds = new int[] {R.id.column1, R.id.column2,R.id.column3};
            for (int i = 0; i < lista.size();i++)
            {
                Medico medico = lista.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(columnTags[0], "Nome: "+medico.getNome());  //Código
                map.put(columnTags[1], "Telefone: " + medico.getTelefone());  //Nome
                map.put(columnTags[2], "Secretária: " + medico.getSecretaria());  //Preço Venda
                //Adiciona dados no Arraylist
                lstMedicos.add(map);
            }

            int currentPosition = grdMedicos.getFirstVisiblePosition();
            //Função para realizar adptação necessária para inserir dados no ListView
            arrayAdapter = new PersonalAdpater(this, lstMedicos, R.layout.cols_3,columnTags , columnIds);

            //Adiciona Array no ListView
            grdMedicos.setAdapter(arrayAdapter);
            if(start > 1)
                grdMedicos.setSelectionFromTop(currentPosition + 1, 0);
        }catch (Exception error) {
            Mensagem.MensagemAlerta("Preenche Grid", error.getMessage(), MedicoActivity.this);

        }
    }
}