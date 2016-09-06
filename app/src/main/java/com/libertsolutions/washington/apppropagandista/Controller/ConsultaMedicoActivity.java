package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import com.libertsolutions.washington.apppropagandista.adapter.ConsultaMedicoAdapter;
import java.util.List;

public class ConsultaMedicoActivity extends AppCompatActivity {
    private ConsultaMedicoAdapter mAdapter;
    private MedicoDAO mMedicoDAO;
    private int mStartLoading = 0;
    private static final int LIMIT_LOADING = 20;
    private boolean mLoadingMore = false;

    @BindView(android.R.id.list) protected ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_medico);
        ButterKnife.bind(this);

        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(
                menu.findItem(R.id.menu_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_novo) {
            Tela.AbrirTela(ConsultaMedicoActivity.this, CadastroMedicoActivity.class);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMedicoDAO.openDatabase();

        if (mAdapter == null) {
            new LoadMoreDataTask().execute();
        }

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = mListView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (mListView.getLastVisiblePosition() >= count - 1
                            && !(mLoadingMore)) {
                        new LoadMoreDataTask().execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });
    }

    @Override
    protected void onStop() {
        mMedicoDAO.closeDatabase();
        super.onStop();
    }

    @OnItemClick(android.R.id.list)
    public void onListViewItemClick(int position) {
        final Medico medico = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("id_medico", medico.getIdMedico());
        intent.putExtra("nome", medico.getNome());
        intent.putExtra("email", medico.getEmail());
        setResult(RESULT_OK, intent);
        finish();
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, List<Medico>> {
        private MaterialDialog mDialogoProgresso;

        @Override
        protected void onPreExecute() {
            mLoadingMore = true;
            mDialogoProgresso = Dialogos.mostrarProgresso(
                    ConsultaMedicoActivity.this,
                    "Carregando... Por favor espere...", false);
        }

        @Override
        protected List<Medico> doInBackground(Void... params) {
            return mMedicoDAO.listar(String.valueOf(mStartLoading),
                    String.valueOf(LIMIT_LOADING), null);
        }

        @Override
        protected void onPostExecute(List<Medico> medicoList) {
            if (medicoList != null && !medicoList.isEmpty()) {
                if (mAdapter == null) {
                    mAdapter = new ConsultaMedicoAdapter(ConsultaMedicoActivity.this,
                            medicoList);
                    mListView.setAdapter(mAdapter);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mAdapter.addAll(medicoList);
                    } else {
                        for(Medico medico : medicoList) {
                            mAdapter.add(medico);
                        }
                    }
                }

                mStartLoading = mAdapter.getCount();
            }

            mDialogoProgresso.dismiss();
            mLoadingMore = false;
        }
    }
}