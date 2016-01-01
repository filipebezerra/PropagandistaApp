package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.libertsolutions.washington.apppropagandista.Model.StatusAgenda;
import com.libertsolutions.washington.apppropagandista.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by washington on 21/09/14.
 */
public class PersonalAdapter extends SimpleAdapter {
    private int[] colors = new int[] {Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF") };
    private LayoutInflater mInflater;
    private int id;

    public PersonalAdapter(Context context, List<? extends Map<String, ?>> data, int resource,String[] from, int[] to) {
        super(context, data, resource, from, to);

        mInflater = LayoutInflater.from(context);
        this.id=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        //Muda de Cor de Necessário
        if (view != null)
        {
            if(view.findViewById(R.id.status) != null) {
                TextView status = (TextView) view.findViewById(R.id.status);
                TextView cor = (TextView) view.findViewById(R.id.corStatus);
                TextView column1 = (TextView) view.findViewById(R.id.column1);
                switch (status.getText().toString()) {
                    case "Pendente":
                        cor.setBackgroundResource(R.color.visita_pendente);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_pendente));
                        break;
                    case "EmAtendimento":
                        cor.setBackgroundResource(R.color.visita_ematendimento);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_ematendimento));
                        break;
                    case "Cancelado":
                        cor.setBackgroundResource(R.color.visita_cancelada);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_cancelada));
                        break;
                    case "NaoVisita":
                        cor.setBackgroundResource(R.color.visita_naovisita);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_naovisita));
                        break;
                    case "Finalizado":
                        cor.setBackgroundResource(R.color.visita_finalizada);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_finalizada));
                        break;
                    default:
                        cor.setBackgroundResource(R.color.visita_pendente);
                        column1.setTextColor(view.getResources().getColor(R.color.visita_pendente));
                }
            }
        }
        return view;
    }
}
