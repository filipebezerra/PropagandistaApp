package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTouch;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Medico;
import com.libertsolutions.washington.apppropagandista.Model.Status;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.DateUtil;
import com.libertsolutions.washington.apppropagandista.Util.Dialogos;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joda.time.DateTime;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 12/01/2016
 * @since #
 */
public class DetalhesMedicoFragment extends Fragment
        implements CalendarDatePickerDialogFragment.OnDateSetListener {
    private static final int RC_SELECIONAR_ESPECIALIDADE = 1001;

    @Bind(R.id.hintNomeMedico) protected TextInputLayout mNomeMedicoHint;
    @Bind(R.id.txtNomeMedico) protected EditText mNomeMedicoView;
    @Bind(R.id.txtDtAniversario) protected EditText mDataAniversarioView;
    @Bind(R.id.txtSecretaria) protected EditText mSecretariaView;
    @Bind(R.id.hintTelefone) protected TextInputLayout mTelefoneHint;
    @Bind(R.id.txtTelefone) protected EditText mTelefoneView;
    @Bind(R.id.txtEmail) protected EditText mEmailView;
    @Bind(R.id.txtCrm) protected EditText mCrmView;
    @Bind(R.id.hintEspecialidade) protected TextInputLayout mEspecialidadeHint;
    @Bind(R.id.txtEspecialidade) protected EditText mEspecialidadeView;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private boolean mHasDialogFrame;

    private Medico mNovoMedico;
    private MedicoDAO mPersistenciaMedico;

    private CadastroMedicoListener mListener;

    private Integer mCodigoEspecialidade;
    private String mNomeEspecialidade = null;

    public DetalhesMedicoFragment() {
    }

    public static DetalhesMedicoFragment newInstance() {
        return new DetalhesMedicoFragment();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            if (mListener == null) {
                mListener = (CadastroMedicoListener) activity;
            }
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            if (mListener == null) {
                mListener = (CadastroMedicoListener) context;
            }
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onCreate(Bundle inState) {
        super.onCreate(inState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cadastro_medico, menu);
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

        if (TextUtils.isEmpty(mNomeMedicoView.getText())) {
            isFormValid = false;
            mNomeMedicoHint.setError("O nome do médico deve ser informado");
            mNomeMedicoHint.setErrorEnabled(true);
        } else {
            mNomeMedicoHint.setError(null);
            mNomeMedicoHint.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mTelefoneView.getText())) {
            isFormValid = false;
            mTelefoneHint.setError("O telefone deve ser informado");
            mTelefoneHint.setErrorEnabled(true);
        } else {
            mTelefoneHint.setError(null);
            mTelefoneHint.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(mEspecialidadeView.getText())) {
            isFormValid = false;
            mEspecialidadeHint.setError("Nenhuma especialidade foi selecionada");
            mEspecialidadeHint.setErrorEnabled(true);
        } else {
            mEspecialidadeHint.setError(null);
            mEspecialidadeHint.setErrorEnabled(false);
        }

        if (isFormValid) {
            if (mNovoMedico == null) {
                mNovoMedico = new Medico();
            }

            mNovoMedico
                    .setNome(mNomeMedicoView.getText().toString())
                    .setTelefone(mTelefoneView.getText().toString())
                    .setIdEspecialidade(mCodigoEspecialidade);

            if (!TextUtils.isEmpty(mDataAniversarioView.getText())) {
                final String dateText = mDataAniversarioView.getText().toString();
                mNovoMedico.setDataAniversario(DateUtil.toDate(dateText).getMillis());
            }

            if (!TextUtils.isEmpty(mSecretariaView.getText())) {
                mNovoMedico.setSecretaria(mSecretariaView.getText().toString());
            }

            if (!TextUtils.isEmpty(mEmailView.getText())) {
                mNovoMedico.setEmail(mEmailView.getText().toString());
            }

            if (!TextUtils.isEmpty(mCrmView.getText())) {
                mNovoMedico.setCrm(mCrmView.getText().toString());
            }

            mNovoMedico.setStatus(Status.Pendente);

            String mensagemConfirmacao;
            final AtomicBoolean isNotificarInclusao = new AtomicBoolean(false);

            if (mNovoMedico.getId() == null || mNovoMedico.getId() == 0) {
                final long idNovoMedico = mPersistenciaMedico.incluir(mNovoMedico);

                if (idNovoMedico == -1) {
                    mensagemConfirmacao = "Houve um erro ao incluir o médico. Tente novamente!";
                } else {
                    mNovoMedico.setId(idNovoMedico);
                    //TODO (Filipe Bezerra): Campo IdMedico deve ser Integer ou Long como o campo Id?
                    mNovoMedico.setIdMedico((int) idNovoMedico);

                    isNotificarInclusao.set(true);

                    mensagemConfirmacao = "Médico cadastrado com sucesso!";
                }
            } else {
                final int alteracoes = mPersistenciaMedico.alterar(mNovoMedico);

                if (alteracoes == 0) {
                    mensagemConfirmacao = "Houve um erro ao alterar o cadastro do médico. "
                            + "Tente novamente!";
                } else {
                    mensagemConfirmacao = "Cadastro de médico alterado com sucesso!";
                }
            }

            Dialogos.mostrarMensagemFlutuante(getView(), mensagemConfirmacao, false,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (isNotificarInclusao.get() && mListener != null) {
                                mListener.onSaveFormData(mNovoMedico);
                            }
                        }
                    });
        } else {
            Dialogos.mostrarMensagemFlutuante(getView(),
                    "Preencha os campos requeridos para salvar!", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_medico,
                container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle inState) {
        super.onViewCreated(view, inState);

        if (inState == null) {
            mHasDialogFrame = view.findViewById(R.id.frame) != null;
        }

        mPersistenciaMedico = new MedicoDAO(getContext());
        mPersistenciaMedico.openDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();

        final CalendarDatePickerDialogFragment calendarDialogFragment =
                (CalendarDatePickerDialogFragment) getChildFragmentManager()
                        .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDialogFragment != null) {
            calendarDialogFragment.setOnDateSetListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mPersistenciaMedico != null) {
            mPersistenciaMedico.closeDatabase();
        }
    }

    @OnEditorAction(R.id.txtNomeMedico)
    public boolean onEditorActionTxtNomeMedico(final int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (!mDataAniversarioView.requestFocusFromTouch()) {
                final long downTime = SystemClock.uptimeMillis();
                final long eventTime = SystemClock.uptimeMillis() + 100;
                mDataAniversarioView.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime,
                        MotionEvent.ACTION_UP, 0.0f, 0.0f, 0));
            }
            return true;
        }
        return false;
    }

    @OnEditorAction(R.id.txtCrm)
    public boolean onEditorActionTxtCrm(final int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (!mEspecialidadeView.requestFocusFromTouch()) {
                final long downTime = SystemClock.uptimeMillis();
                final long eventTime = SystemClock.uptimeMillis() + 100;
                mEspecialidadeView.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime,
                        MotionEvent.ACTION_UP, 0.0f, 0.0f, 0));
            }
            return true;
        }
        return false;
    }

    @OnTouch({ R.id.txtDtAniversario, R.id.txtEspecialidade })
    public boolean onTouchInView(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.txtDtAniversario:
                    showDatePicker();
                    return true;

                case R.id.txtEspecialidade:
                    startActivityForResult(
                            new Intent(getContext(), ConsultaEspecialidadeActivity.class),
                            RC_SELECIONAR_ESPECIALIDADE);
                    return true;
            }
        }
        return false;
    }

    private PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();

    @OnFocusChange(R.id.txtTelefone)
    public void onFocusChangeTxtTelefone(final boolean focused) {
        if (!focused && !TextUtils.isEmpty(mTelefoneView.getText())) {
            mTelefoneHint.setError(null);
            mTelefoneHint.setErrorEnabled(false);

            try {
                final Phonenumber.PhoneNumber number = mPhoneNumberUtil.parse(
                        mTelefoneView.getText().toString(), "BR");

                if (mPhoneNumberUtil.isValidNumber(number)) {
                    mTelefoneView.setText(mPhoneNumberUtil.format(number,
                            PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
                } else {
                    mTelefoneHint.setError(getString(R.string.error_invalid_phone_number));
                    mTelefoneHint.setErrorEnabled(true);
                }
            } catch (NumberParseException e) {
                mTelefoneHint.setError(e.getMessage());
                mTelefoneHint.setErrorEnabled(true);
            }
        }
    }

    private DateTime obtainDateFromField() {
        if (!TextUtils.isEmpty(mDataAniversarioView.getText())) {
            final String dateText = mDataAniversarioView.getText().toString();
            return DateUtil.toDate(dateText);
        }

        return null;
    }

    private void showDatePicker() {
        int year, monthOfYear, dayOfMonth;
        final DateTime dateSet = obtainDateFromField();

        if (dateSet != null) {
            year = dateSet.getYear();
            monthOfYear = dateSet.getMonthOfYear();
            dayOfMonth = dateSet.getDayOfMonth();
        } else {
            final DateTime now = DateTime.now();
            year = now.getYear();
            monthOfYear = now.getMonthOfYear();
            dayOfMonth = now.getDayOfMonth();
        }

        CalendarDatePickerDialogFragment dialogFragment = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setPreselectedDate(year, monthOfYear - 1, dayOfMonth);

        if (mHasDialogFrame) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            ft.add(R.id.frame, dialogFragment, FRAG_TAG_DATE_PICKER)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            dialogFragment.show(getChildFragmentManager(), FRAG_TAG_DATE_PICKER);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year,
            int monthOfYear, int dayOfMonth) {
        mDataAniversarioView.setText(DateUtil.format(year, monthOfYear, dayOfMonth));
        mSecretariaView.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SELECIONAR_ESPECIALIDADE) {
            if (resultCode == Activity.RESULT_OK) {
                final Bundle extras = data.getExtras();
                mCodigoEspecialidade = extras.getInt("id_especialidade");
                mNomeEspecialidade = extras.getString("nome");
                mEspecialidadeView.setText(mNomeEspecialidade);
            } else {
                mCodigoEspecialidade = null;
                mNomeEspecialidade = null;
                mEspecialidadeView.setText("");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface CadastroMedicoListener {
        void onSaveFormData(Medico novoMedico);
    }
}
