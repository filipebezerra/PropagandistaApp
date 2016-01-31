package com.libertsolutions.washington.apppropagandista.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.libertsolutions.washington.apppropagandista.Model.Especialidade;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 31/01/2016
 * @since 0.1.0
 */
public class ConsultaEspecialidadeAdapter extends ArrayAdapter<Especialidade> {
    private static final int LAYOUT = android.R.layout.simple_list_item_1;

    public ConsultaEspecialidadeAdapter(Context context, List<Especialidade> medicos) {
        super(context, LAYOUT, medicos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(LAYOUT, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.especialidade = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Especialidade item = getItem(position);
        viewHolder.especialidade.setText(item.getNome());

        return convertView;
    }

    private class ViewHolder {
        TextView especialidade;
    }
}
