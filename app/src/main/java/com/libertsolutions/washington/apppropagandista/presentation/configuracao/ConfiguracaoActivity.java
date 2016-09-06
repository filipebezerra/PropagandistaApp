package com.libertsolutions.washington.apppropagandista.presentation.configuracao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.libertsolutions.washington.apppropagandista.R;

/**
 * Tela de integração com as configurações do aplicativo.
 *
 * @author Filipe Bezerra
 * @version 1.0, 20/11/2015
 * @since 1.0
 */
public class ConfiguracaoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
