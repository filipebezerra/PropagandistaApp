package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Mensagem;
import com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils;
import com.libertsolutions.washington.apppropagandista.Util.Tela;
import com.libertsolutions.washington.apppropagandista.network.call.LoginAsyncTaskCall;
import com.libertsolutions.washington.apppropagandista.network.listener.AsyncListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements AsyncListener<Propagandista> {
    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    @Bind(R.id.cpf)
    EditText mCpfView;

    private MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (PreferencesUtils.isUserLogged(this)) {
            Tela.AbrirTela(LoginActivity.this, MainActivity.class);
            finish();
        }
    }

    @OnEditorAction(R.id.cpf)
    public boolean onEditorAction(int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.email_sign_in_button)
    public void onSignInClick() {
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mCpfView.setError(null);

        // Store values at the time of the login attempt.
        final String cpf = mCpfView.getText().toString();

        View focusView = null;

        //Valida Login e Senha
        if(TextUtils.isEmpty(cpf))
        {
            mCpfView.setError("CPF é obrigatório");
            focusView = mCpfView;
        } else if (cpf.length() != 11) {
            mCpfView.setError("CPF inválido");
            focusView = mCpfView;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            new LoginAsyncTaskCall().execute(cpf, this);
        }
    }

    @Override
    public void onBeforeExecute() {
        mProgressDialog = new MaterialDialog.Builder(LoginActivity.this)
                .content("Validando seu login, por favor aguarde...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    @Override
    public void onSuccess(Propagandista result) {
        dismissDialog();
        PreferencesUtils.setUserLogged(this, result);
        Tela.AbrirTela(LoginActivity.this, MainActivity.class);
    }

    @Override
    public void onResultNothing() {
        dismissDialog();
        Mensagem.MensagemAlerta("Login", "CPF não cadastrado", LoginActivity.this);
    }

    @Override
    public void onFailure(Throwable error) {
        dismissDialog();
        Log.e(TAG, error.getMessage());
        if (error.getCause() != null) {
            Log.e(TAG, error.getCause().getMessage());
        }
        Mensagem.MensagemAlerta(this, error.getMessage());
    }

    @Override
    public void onCancel() {}

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

