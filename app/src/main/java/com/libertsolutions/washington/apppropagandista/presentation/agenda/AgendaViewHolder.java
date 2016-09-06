package com.libertsolutions.washington.apppropagandista.presentation.agenda;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.libertsolutions.washington.apppropagandista.R;

/**
 * @author Filipe Bezerra
 */
public class AgendaViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.view_status) View viewStatus;
    @BindView(R.id.text_view_medico) TextView textViewMedico;
    @BindView(R.id.text_view_data) TextView textViewData;
    @BindView(R.id.text_view_observacao) TextView textViewObservacao;

    public AgendaViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
