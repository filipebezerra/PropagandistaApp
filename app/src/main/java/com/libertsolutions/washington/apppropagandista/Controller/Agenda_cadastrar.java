package com.libertsolutions.washington.apppropagandista.Controller;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.libertsolutions.washington.apppropagandista.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Agenda_cadastrar extends AppCompatActivity {
    EditText txtData;
    EditText txtHora;
    EditText txtMedico;
    EditText txtObs;
    Button btnSalvar;
    DatePickerDialog dataCompromisso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_cadastrar);

        //Recupera Campos
        getCampos();
        setDateTimeField();

        txtData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                dataCompromisso.show();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        dataCompromisso = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //Metódo para recuperar campos
    public void getCampos()
    {
        txtData = (EditText)findViewById(R.id.txtData);
        txtHora = (EditText)findViewById(R.id.txtHora);
        txtMedico = (EditText)findViewById(R.id.txtMedico);
        txtObs = (EditText)findViewById(R.id.txtObs);
        btnSalvar = (Button)findViewById(R.id.btnSalvar);
    }
}
