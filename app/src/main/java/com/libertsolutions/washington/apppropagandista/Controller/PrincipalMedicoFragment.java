package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libertsolutions.washington.apppropagandista.R;

public class PrincipalMedicoFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";

    public PrincipalMedicoFragment() {
    }

    public static PrincipalMedicoFragment newInstance(int page) {
        PrincipalMedicoFragment fragment = new PrincipalMedicoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_principal_medico, container, false);
        return rootView;
    }
}