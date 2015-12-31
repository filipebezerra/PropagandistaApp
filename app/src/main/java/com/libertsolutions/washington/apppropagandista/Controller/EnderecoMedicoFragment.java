package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libertsolutions.washington.apppropagandista.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnderecoMedicoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnderecoMedicoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnderecoMedicoFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";

    public EnderecoMedicoFragment() {
    }

    public static EnderecoMedicoFragment newInstance(int page) {
        EnderecoMedicoFragment fragment = new EnderecoMedicoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_endereco_medico, container, false);
        /*
        TextView txt = (TextView) rootView.findViewById(R.id.page_number_label);
        int page = getArguments().getInt(ARG_PAGE_NUMBER, -1);
        txt.setText("Endereço");
        */
        return rootView;
    }
}