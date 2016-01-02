package com.libertsolutions.washington.apppropagandista.Controller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.libertsolutions.washington.apppropagandista.Util.DateUtil.FormatType.DATE_AND_TIME;

public class CancelarVisita extends AppCompatActivity {
    private int mIdAgenda;
    private Agenda mAgenda;
    @NonNull
    private AgendaDAO mAgendaDAO;
    private MedicoDAO mMedicoDAO;

    @Bind(R.id.status) TextView mStatus;
    @Bind(R.id.data_hora_view) TextView mDataHoraView;
    @Bind(R.id.medico_view) TextView mMedicoView;
    @Bind(R.id.obs_view) TextView mObservacaoView;
    @Bind(R.id.btnIniciar) Button btnIniciarVisita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_visita);
        ButterKnife.bind(this);

        if (!getIntent().hasExtra("id") ||
                getIntent().getExtras().getString("id") == null) {
            throw new IllegalStateException("O ID da agenda deve ser passado via putExtras()");
        } else {
            mIdAgenda = Integer.parseInt(getIntent().getStringExtra("id"));
        }

        mAgendaDAO = new AgendaDAO(this);
        mMedicoDAO = new MedicoDAO(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAgendaDAO.openDatabase();
        mMedicoDAO.openDatabase();
        mAgenda = mAgendaDAO.consultar(mIdAgenda);
        PreencheTela();
        btnIniciarVisita.setText("Cancelar");
        btnIniciarVisita.setBackgroundResource(R.color.visita_cancelada);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAgendaDAO.closeDatabase();
        mMedicoDAO.closeDatabase();
    }

    //Metódo para preencher a tela com os dados da Agenda
    public void PreencheTela()
    {
        mStatus.setText(mAgenda.getStatusAgenda().name());
        mDataHoraView.setText(DateUtil.format(mAgenda.getDataCompromisso(), DATE_AND_TIME));
        mMedicoView.setText(mMedicoDAO.consultar(MedicoDAO.COLUNA_ID_MEDICO + " = ?", mAgenda.getIdMedico().toString()).getNome());
        mObservacaoView.setText(mAgenda.getObservacao());
    }

    @OnClick(R.id.btnIniciar)
    public void onClickBtnIniciar() {
        mAgenda.setStatusAgenda(StatusAgenda.Cancelado);
        //Salva Alterações tabela Agenda
        mAgendaDAO.alterar(mAgenda);
        onBackPressed();
    }
}