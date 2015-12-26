package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Classe modelo baseado.
 *
 * @author Filipe Bezerra
 * @version 1.0, 24/12/2015
 * @since 1.0
 */
public class AgendaModel {
    @SerializedName("id_agenda")
    public int idAgenda;

    @SerializedName("id_cliente")
    public int idCliente;

    public String dtCompromisso;

    public String observacao;

    @SerializedName("id_medico")
    public int idMedico;

    @SerializedName("id_propagandista")
    public int idPropagandista;

    @SerializedName("status")
    public int statusAgenda;
}
