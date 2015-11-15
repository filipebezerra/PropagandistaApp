package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import com.libertsolutions.washington.apppropagandista.Dao.AgendaDAO;
import com.libertsolutions.washington.apppropagandista.Dao.MedicoDAO;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;
import com.libertsolutions.washington.apppropagandista.R;
import com.libertsolutions.washington.apppropagandista.Util.Tela;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agenda_cadastrar extends AppCompatActivity {
    private AgendaDAO agendaDb;
    EditText txtData;
    EditText txtHora;
    EditText txtMedico;
    EditText txtIdMedico;
    EditText txtObs;
    Button btnSalvar;
    DatePickerDialog dataCompromisso;
    TimePickerDialog horaCompromisso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_cadastrar);

        //Recupera Campos
        getCampos();
        setDateTimeField();

        //Metódo selecionar médico
        txtMedico.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tela.AbrirTela(Agenda_cadastrar.this,ConsultarMedicoActivity.class);
                return false;
            }
        });


        //Seta Data
        txtData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dataCompromisso.show();
                return false;
            }
        });

        //Seta Hora
        txtHora.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                horaCompromisso.show();
                return false;
            }
        });

        //Evento Salvar
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validaTela()) {
                    Agenda agenda = getDados();
                    try
                    {
                        //Salva dados no banco
                        agendaDb.Incluir(agenda);
                    }catch (Exception error)
                    {
                    }finally {
                        onBackPressed();
                    }
                }
            }
        });

        //Recupera parâmetros
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            txtIdMedico.setText(bundle.getInt("id_medico"));
            txtMedico.setText(bundle.getString("nome"));
        }
    }

    //Metódo para mostrar data e hora
    private void setDateTimeField() {
        //Dialog Data
        Calendar newCalendar = Calendar.getInstance();
        dataCompromisso = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                txtData.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //Dialog Hora
        horaCompromisso = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtHora.setText(hourOfDay + ":" + minute);
                    }
                },newCalendar.get(Calendar.HOUR),newCalendar.get(Calendar.MINUTE), true);
    }

    //Metódo para recuperar campos
    public void getCampos()
    {
        this.agendaDb = new AgendaDAO(this);
        txtData = (EditText)findViewById(R.id.txtData);
        txtHora = (EditText)findViewById(R.id.txtHora);
        txtMedico = (EditText)findViewById(R.id.txtMedico);
        txtIdMedico = (EditText)findViewById(R.id.txtIdMedico);
        txtObs = (EditText)findViewById(R.id.txtObs);
        btnSalvar = (Button)findViewById(R.id.btnSalvar);
    }

    //Metódo Preenche Objeto
    public Agenda getDados()
    {
        Agenda  agenda = new Agenda();
        agenda.setData(txtData.getText().toString());
        agenda.setHora(txtHora.getText().toString());
        agenda.setId_medico(new MedicoDAO(this).Consultar(Integer.parseInt(txtIdMedico.getText().toString())));
        if(TextUtils.isEmpty(txtObs.getText().toString()))
            agenda.setObs("");
        else
            agenda.setObs(txtObs.getText().toString());

        return agenda;
    }

    //Função Validar Tela
    private boolean validaTela() {
        // Reset errors.
        txtMedico.setError(null);
        txtData.setError(null);
        txtHora.setError(null);

        // Store values at the time of the login attempt.
        String medico = txtMedico.getText().toString();
        String data = txtData.getText().toString();
        String hora = txtHora.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Valida Hora
        if(TextUtils.isEmpty(hora))
        {
            txtHora.setError(getString(R.string.error_field_required));
            focusView = txtHora;
            cancel = true;
        }

        //Valida Data
        if(TextUtils.isEmpty(data))
        {
            txtData.setError(getString(R.string.error_field_required));
            focusView = txtData;
            cancel = true;
        }

        //Valida Médico
        if(TextUtils.isEmpty(medico))
        {
            txtMedico.setError(getString(R.string.error_field_required));
            focusView = txtMedico;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        return cancel;
    }

    //Get Médicos

}
