package com.libertsolutions.washington.apppropagandista.presentation.agenda;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filipe Bezerra
 */
public class AgendaAdapter extends RecyclerView.Adapter<AgendaViewHolder>
        implements Filterable {

    private final Context mContext;

    private List<Pair<Agenda, Medico>> mListaAgendas;

    private List<Pair<Agenda, Medico>> mOriginalValues;

    private AgendaFilter mFilter;

    public AgendaAdapter(Context context, List<Pair<Agenda, Medico>> listaAgendas) {
        mContext = context;
        mListaAgendas = listaAgendas;
    }

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_agenda, parent, false);
        return new AgendaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AgendaViewHolder holder, int position) {
        Pair<Agenda, Medico> dadosAgenda = mListaAgendas.get(position);

        switch (dadosAgenda.first.getStatusAgenda()) {
            case Pendente:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_pendente));
                break;
            case EmAtendimento:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_ematendimento));
                break;
            case Cancelado:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_cancelada));
                break;
            case NaoVisita:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_naovisita));
                break;
            case Finalizado:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_finalizada));
                break;
            default:
                holder.viewStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.visita_pendente));
        }

        holder.textViewMedico.setText(dadosAgenda.second.getNome());
        holder.textViewData.setText(
                mContext.getString(R.string.template_data_agenda,
                        DateUtil.format(dadosAgenda.first.getDataCompromisso(),
                                DateUtil.FormatType.DATE_AND_TIME)));
        holder.textViewObservacao.setText(
                mContext.getString(R.string.template_observacao_agenda,
                        dadosAgenda.first.getObservacao()));
    }

    @Override
    public int getItemCount() {
        return mListaAgendas.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new AgendaFilter();
        }
        return mFilter;
    }

    private class AgendaFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(mListaAgendas);
            }

            if (TextUtils.isEmpty(prefix)) {
                List<Pair<Agenda, Medico>> list = new ArrayList<>(mOriginalValues);
                results.values = list;
                results.count = list.size();
            } else {
                final String strPrefix = prefix.toString().toLowerCase();

                List<Pair<Agenda, Medico>> values = new ArrayList<>(mOriginalValues);

                final List<Pair<Agenda, Medico>> newValues = new ArrayList<>();

                for (Pair<Agenda, Medico> dadosAgenda : values) {
                    final String medico = TextUtils.isEmpty(dadosAgenda.second.getNome()) ?
                            null : dadosAgenda.second.getNome().trim().toLowerCase();

                    final String data = dadosAgenda.first.getDataCompromisso() == 0 ?
                            null : DateUtil.format(dadosAgenda.first.getDataCompromisso(),
                            DateUtil.FormatType.DATE_AND_TIME);

                    final String observacao = TextUtils.isEmpty(dadosAgenda.first.getObservacao()) ?
                            null : dadosAgenda.first.getObservacao().trim().toLowerCase();

                    if ((medico != null && medico.startsWith(strPrefix))
                            || (data != null && data.contains(strPrefix))
                            || (observacao != null && observacao.contains(strPrefix))) {
                        newValues.add(dadosAgenda);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            mListaAgendas = (List<Pair<Agenda, Medico>>) results.values;
            notifyDataSetChanged();
        }
    }
}
