package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by washington on 01/01/2016.
 */
public class VisitaModel {
    @SerializedName("id_visita")
    public int idVisita;

    @SerializedName("id_cliente")
    public long idCliente;

    public String dtInicio;

    @SerializedName("longInicial")
    public Double longInicial;

    @SerializedName("latInicial")
    public Double latInicial;

    public String dtFim;

    @SerializedName("longFinal")
    public Double longFinal;

    @SerializedName("latFinal")
    public Double latFinal;

    public String detalhes;

    @SerializedName("id_agenda")
    public Long idAgenda;
}
