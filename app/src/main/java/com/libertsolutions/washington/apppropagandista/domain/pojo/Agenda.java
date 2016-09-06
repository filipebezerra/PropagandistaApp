package com.libertsolutions.washington.apppropagandista.domain.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 */
public class Agenda {
    @SerializedName("id_cliente")
    @Expose
    public final long id;

    @SerializedName("id_agenda")
    @Expose
    public final long idAgenda;

    @SerializedName("dtCompromisso")
    @Expose
    public final String dtCompromisso;

    @SerializedName("observacao")
    @Expose
    public final String observacao;

    @SerializedName("id_medico")
    @Expose
    public final long idMedico;

    @SerializedName("status")
    @Expose
    public final int status;

    public Agenda(
            long id, long idAgenda, String dtCompromisso, String observacao,
            long idMedico, int status) {
        this.id = id;
        this.idAgenda = idAgenda;
        this.dtCompromisso = dtCompromisso;
        this.observacao = observacao;
        this.idMedico = idMedico;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getIdAgenda() {
        return idAgenda;
    }

    public String getDtCompromisso() {
        return dtCompromisso;
    }

    public String getObservacao() {
        return observacao;
    }

    public long getIdMedico() {
        return idMedico;
    }

    public int getStatus() {
        return status;
    }
}
