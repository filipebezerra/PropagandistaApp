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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import java.util.ArrayList;
import java.util.List;

public class CadastroMedicoActivity extends AppCompatActivity
    implements DetalhesMedicoFragment.CadastroMedicoListener {

    @Bind(R.id.tabs) protected TabLayout mTabLayout;

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

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    @Override
    public void onSaveFormData(Medico novoMedico) {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_fragment_enderecos_medico));
        mFragmentAdapter
                .addFragment(EnderecosMedicoFragment.newInstance(novoMedico),
                        getString(R.string.title_fragment_enderecos_medico));
    }

    public class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
            for (Fragment fragmentAdded : mFragments) {
                if (fragmentAdded.getClass().getSimpleName()
                        .equals(fragment.getClass().getSimpleName())) {
                    return;
                }
            }

            mFragments.add(fragment);
            mFragmentTitles.add(title);
            notifyDataSetChanged();
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
