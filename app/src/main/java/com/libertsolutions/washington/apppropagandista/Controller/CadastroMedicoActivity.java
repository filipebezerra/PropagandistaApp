package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.libertsolutions.washington.apppropagandista.R;
import java.util.ArrayList;
import java.util.List;

public class CadastroMedicoActivity extends AppCompatActivity {

    private FragmentAdapter mFragmentAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medico);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mFragmentAdapter.addFragment(DetalhesMedicoFragment.newInstance(),
                getString(R.string.title_fragment_detalhes_medico));
        mFragmentAdapter.addFragment(EnderecosMedicoFragment.newInstance(),
                getString(R.string.title_fragment_enderecos_medico));

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    public static class DetalhesMedicoFragment extends Fragment {
        public DetalhesMedicoFragment() {}

        public static DetalhesMedicoFragment newInstance() {
            return new DetalhesMedicoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_cadastro_medico, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_salvar) {
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_cadastro_medico,
                    container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }

    public static class EnderecosMedicoFragment extends Fragment {
        public EnderecosMedicoFragment() {}

        public static EnderecosMedicoFragment newInstance() {
            return new EnderecosMedicoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_cadastro_enderecos_medico, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_adicionar) {
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_cadastro_enderecos_medico,
                    container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }

    public class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || position >= mFragments.size()) {
                return null;
            }

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= mFragmentTitles.size()) {
                return null;
            }

            return mFragmentTitles.get(position);
        }
    }
}
