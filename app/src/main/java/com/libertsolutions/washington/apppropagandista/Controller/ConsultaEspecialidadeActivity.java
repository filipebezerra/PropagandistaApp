package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Dao.EspecialidadeDAO;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.adapter.ConsultaEspecialidadeAdapter;
import java.util.List;

/**
 * Tela de consulta de {@link com.libertsolutions.washington.apppropagandista.Model.Especialidade}s.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 31/01/2016
 * @since 0.1.0
 */
public class ConsultaEspecialidadeActivity extends AppCompatActivity {
    private ConsultaEspecialidadeAdapter mAdapter;
    private EspecialidadeDAO mEspecialidadeDAO;
    private int mStartLoading = 0;
    private static final int LIMIT_LOADING = 20;
    private boolean mLoadingMore = false;

    @BindView(android.R.id.list) protected ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_especialidade);
        ButterKnife.bind(this);

        mEspecialidadeDAO = new EspecialidadeDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(
                menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getString(R.string.pesquisar));
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

        menu.findItem(R.id.action_novo).setEnabled(false).setVisible(false);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEspecialidadeDAO.openDatabase();

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
        mEspecialidadeDAO.closeDatabase();
        super.onStop();
    }

    @OnItemClick(android.R.id.list)
    public void onListViewItemClick(int position) {
        final Especialidade especialidade = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("id_especialidade", especialidade.getIdEspecialidade());
        intent.putExtra("nome", especialidade.getNome());
        setResult(RESULT_OK, intent);
        finish();
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, List<Especialidade>> {
        private MaterialDialog mDialogoProgresso;

        @Override
        protected void onPreExecute() {
            mLoadingMore = true;
            mDialogoProgresso = Dialogos.mostrarProgresso(
                    ConsultaEspecialidadeActivity.this,
                    "Carregando... Por favor espere...", false);
        }

        @Override
        protected List<Especialidade> doInBackground(Void... params) {
            return mEspecialidadeDAO.listar(String.valueOf(mStartLoading),
                    String.valueOf(LIMIT_LOADING), null);
        }

        @Override
        protected void onPostExecute(List<Especialidade> especialidadeList) {
            if (especialidadeList != null && !especialidadeList.isEmpty()) {
                if (mAdapter == null) {
                    mAdapter = new ConsultaEspecialidadeAdapter(
                            ConsultaEspecialidadeActivity.this,
                            especialidadeList);
                    mListView.setAdapter(mAdapter);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mAdapter.addAll(especialidadeList);
                    } else {
                        for(Especialidade especialidade : especialidadeList) {
                            mAdapter.add(especialidade);
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