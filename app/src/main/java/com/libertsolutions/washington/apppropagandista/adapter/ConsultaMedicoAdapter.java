package com.libertsolutions.washington.apppropagandista.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.R;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/01/2016
 * @since #
 */
public class ConsultaMedicoAdapter extends ArrayAdapter<Medico> {
    private static final int LAYOUT = R.layout.item_medico;

    public ConsultaMedicoAdapter(Context context, List<Medico> medicos) {
        super(context, LAYOUT, medicos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(LAYOUT, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nomeMedico = (TextView) convertView.findViewById(R.id.nome_medico);
            viewHolder.telefone = (TextView) convertView.findViewById(R.id.telefone);
            viewHolder.nomeSecretaria = (TextView) convertView.findViewById(R.id.nome_secretaria);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Medico medico = getItem(position);
        viewHolder.nomeMedico.setText(medico.getNome());
        viewHolder.telefone
                .setText(String.format("Telefone: %s", medico.getTelefone()));
        viewHolder.nomeSecretaria
                .setText(String.format("Secretária: %s",
                        TextUtils.isEmpty(medico.getSecretaria()) ? "-" : medico.getSecretaria()));

        return convertView;
    }

    private class ViewHolder {
        TextView nomeMedico;
        TextView telefone;
        TextView nomeSecretaria;
    }
}
