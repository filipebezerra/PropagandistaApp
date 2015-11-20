package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Enum.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;

public class DetalhesVisitaActivity extends AppCompatActivity {
    private AgendaDAO mAgendaDAO;
    private Agenda mAgenda;

    @Bind(R.id.data_hora_view)TextView mDataHoraView;
    @Bind(R.id.medico_view)TextView mMedicoView;
    @Bind(R.id.obs_view)TextView mObservacaoView;
    @Bind(R.id.acoes_visita) Button mAcoesVisitaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_visita);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("id") && extras.getString("id") != null) {
            mAgendaDAO = new AgendaDAO(this);
            int idAgenda = Integer.valueOf(extras.getString("id"));
            mAgenda = mAgendaDAO.Consultar(idAgenda);

            final MedicoDAO medicoDAO = new MedicoDAO(this);
            final Medico medico = medicoDAO.Consultar(mAgenda.getId_medico().getId_medico());

            mDataHoraView.setText(mAgenda.getData() + "/" + mAgenda.getHora());

            if (medico != null) {
                mMedicoView.setText(medico.getNome());
            }

            mObservacaoView.setText(mAgenda.getObs());

            initiliazeAcoesVisitaButton();
        } else {
            throw new IllegalArgumentException("O ID da agenda deve ser passado via putExtras()");
        }
    }

    @OnClick(R.id.acoes_visita)
    public void onClickAcoesVisita() {
        switch (StatusAgenda.status(mAgenda.getStatus())) {
            case Pendente:
                break;
            case EmAtendimento:
                break;
            case Finalizado:
                break;
        }
    }

    private void initiliazeAcoesVisitaButton() {
        switch (StatusAgenda.status(mAgenda.getStatus())) {
            case Pendente:
                mAcoesVisitaButton.setText("Iniciar Visita");
                break;
            case EmAtendimento:
                mAcoesVisitaButton.setText("Finalizar Visita");
                break;
            case Finalizado:
                mAcoesVisitaButton.setText("Visita Finalizada");
                break;
        }
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, DetalhesVisitaActivity.class);
    }
}
