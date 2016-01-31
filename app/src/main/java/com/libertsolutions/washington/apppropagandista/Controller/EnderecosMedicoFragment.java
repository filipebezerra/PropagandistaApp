package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.libertsolutions.washington.apppropagandista.Dao.EnderecoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Endereco;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import com.libertsolutions.washington.apppropagandista.adapter.NothingSelectedSpinnerAdapter;
import java.util.ArrayList;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 13/01/2016
 * @since #
 */
public class EnderecosMedicoFragment extends Fragment {
    private static final String EXTRA_MEDICO = "IdMedico";

    @Bind(R.id.hintEndereco) protected TextInputLayout mEnderecoHint;
    @Bind(R.id.txtEndereco) protected EditText mEnderecoView;

    @Bind(R.id.txtCep) protected EditText mCepView;

    @Bind(R.id.txtNumero) protected EditText mNumeroView;

    @Bind(R.id.hintBairro) protected TextInputLayout mBairroHint;
    @Bind(R.id.txtBairro) protected EditText mBairroView;

    @Bind(R.id.txtComplemento) protected EditText mComplementoView;
    @Bind(R.id.txtObservacao) protected EditText mObservacaoView;

    @Bind(R.id.spinnerEnderecos) protected Spinner mEnderecosAdicionadosView;

    private ArrayAdapter<Endereco> mEnderecosAdapter;

    private Medico mMedico;
    private EnderecoDAO mPersistenciaEndereco;

    private Endereco mEnderecoSelecionadoEdicao;

    public EnderecosMedicoFragment() {
    }

    public static EnderecosMedicoFragment newInstance(@NonNull Medico medico) {
        final EnderecosMedicoFragment fragment = new EnderecosMedicoFragment();

        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDICO, medico);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() == null
                || !getArguments().containsKey(EXTRA_MEDICO)
                || ! (getArguments().get(EXTRA_MEDICO) instanceof Parcelable)) {
            throw new IllegalArgumentException(
                    "O médico deve ser passado como argumento via extra EXTRA_MEDICO");
        }

        mPersistenciaEndereco = new EnderecoDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_enderecos_medico,
                container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMedico = getArguments().getParcelable(EXTRA_MEDICO);
        mPersistenciaEndereco.openDatabase();

        mEnderecosAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, new ArrayList<Endereco>());

        mEnderecosAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        mEnderecosAdicionadosView.setAdapter(
                new NothingSelectedSpinnerAdapter(mEnderecosAdapter,
                        R.layout.spinner_row_nothing_selected, getActivity()));

        mEnderecosAdicionadosView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Object item = mEnderecosAdicionadosView.getAdapter().getItem(position);

                if (item != null) {
                    final Endereco endereco = (Endereco)item;
                    mEnderecoView.setText(endereco.getEndereco());
                    mCepView.setText(endereco.getCep());
                    mNumeroView.setText(endereco.getNumero());
                    mBairroView.setText(endereco.getBairro());
                    mComplementoView.setText(endereco.getComplemento());
                    mObservacaoView.setText(endereco.getObservacao());
                    mEnderecoSelecionadoEdicao = endereco;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearFields();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cadastro_enderecos_medico, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_salvar) {
            saveFormData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveFormData() {
        boolean isFormValid = true;

        if (TextUtils.isEmpty(mEnderecoView.getText())) {
            isFormValid = false;
            mEnderecoHint.setError("O endereço deve ser informado");
            mEnderecoHint.setErrorEnabled(true);
        } else {
            mEnderecoHint.setError(null);
            mEnderecoHint.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mBairroView.getText())) {
            isFormValid = false;
            mBairroHint.setError("O endereço deve ser informado");
            mBairroHint.setErrorEnabled(true);
        } else {
            mBairroHint.setError(null);
            mBairroHint.setErrorEnabled(false);
        }

        if (isFormValid) {
            final Endereco novoEndereco = new Endereco()
                    .setIdMedico(mMedico.getIdMedico())
                    .setEndereco(mEnderecoView.getText().toString())
                    .setBairro(mBairroView.getText().toString());

            if (!TextUtils.isEmpty(mCepView.getText())) {
                novoEndereco.setCep(mCepView.getText().toString());
            }

            if (!TextUtils.isEmpty(mNumeroView.getText())) {
                novoEndereco.setNumero(mNumeroView.getText().toString());
            }

            if (!TextUtils.isEmpty(mComplementoView.getText())) {
                novoEndereco.setComplemento(mComplementoView.getText().toString());
            }

            if (!TextUtils.isEmpty(mObservacaoView.getText())) {
                novoEndereco.setObservacao(mObservacaoView.getText().toString());
            }

            novoEndereco.setStatus(Status.Pendente);

            final long idNovoEndereco = mPersistenciaEndereco.incluir(novoEndereco);

            String mensagemConfirmacao;

            if (idNovoEndereco == -1) {
                mensagemConfirmacao = "Houve um erro ao incluir o endereço. Tente novamente!";
            } else {
                novoEndereco.setId(idNovoEndereco);
                mensagemConfirmacao = "Endereço cadastrado com sucesso!";
            }

            mEnderecosAdapter.add(novoEndereco);
            clearFields();
            mEnderecoView.requestFocus();
            Dialogos.mostrarMensagemFlutuante(getView(), mensagemConfirmacao, false);
        } else {
            Dialogos.mostrarMensagemFlutuante(getView(),
                    "Preencha os campos requeridos para salvar!", false);
        }
    }

    private void clearFields() {
        mEnderecoSelecionadoEdicao = null;
        mEnderecoView.setText("");
        mCepView.setText("");
        mNumeroView.setText("");
        mBairroView.setText("");
        mComplementoView.setText("");
        mObservacaoView.setText("");
    }

    @Override
    public void onDestroyView() {
        mPersistenciaEndereco.closeDatabase();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
