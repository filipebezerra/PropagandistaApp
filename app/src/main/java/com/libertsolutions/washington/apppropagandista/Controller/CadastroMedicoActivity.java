package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DrawableUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CadastroMedicoActivity extends AppCompatActivity {
    Button btnAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medico);
        ButterKnife.bind(this);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        btnAdicionar = (Button)findViewById(R.id.btnAdicionar);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PrincipalMedicoFragment.newInstance(position + 1);
                case 1:
                    return EnderecoMedicoFragment.newInstance(position+1);
                default:
                    return PrincipalMedicoFragment.newInstance(position+1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Principal";
                case 1:
                    return "Endereço";
                default:
                    return "Principal";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhes_medico, menu);
        DrawableUtil.tint(this, menu.findItem(R.id.action_salvar).getIcon(), R.color.white);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_salvar) {
            //save();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}