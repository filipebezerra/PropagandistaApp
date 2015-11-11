package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.Tela;

public class MedicoActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_medico);
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
}
