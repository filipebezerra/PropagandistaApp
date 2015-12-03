package com.libertsolutions.washington.apppropagandista.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.libertsolutions.washington.apppropagandista.api.PropagandistaService;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController.createService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
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
            mProgressDialog = new MaterialDialog.Builder(LoginActivity.this)
                    .content("Validando seu login, por favor aguarde...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            final PropagandistaService service = createService(PropagandistaService.class, this);
            if (service != null) {
                service.getByCpf(cpf)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new LoginSubscriber());
            }
        }
    }

    private class LoginSubscriber extends Subscriber<Propagandista> {
        @Override
        public void onCompleted() {
            // vazio
        }

        @Override
        public void onError(Throwable e) {
            dismissDialog();
            Log.e(TAG, e.getMessage());
            if (e.getCause() != null) {
                Log.e(TAG, e.getCause().getMessage());
            }
            Mensagem.MensagemAlerta(LoginActivity.this, e.getMessage());
        }

        @Override
        public void onNext(Propagandista propagandista) {
            dismissDialog();

            if (propagandista != null) {
                PreferencesUtils.setUserLogged(LoginActivity.this, propagandista);
                finish();
            } else {
                Mensagem.MensagemAlerta("Login", "CPF não cadastrado", LoginActivity.this);
            }
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, LoginActivity.class);
    }
}

