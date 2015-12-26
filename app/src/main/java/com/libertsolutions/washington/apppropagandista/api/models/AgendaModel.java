package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;
import com.libertsolutions.washington.apppropagandista.Model.Agenda;

/**
 * Classe modelo para envio para e recebimento de dados do webservice. <br><br>
 *
 * A fonte dos dados enviados são obtidas a partir do método {@link Agenda#toModel(Agenda)}. <br><br>
 *
 * Os dados recebidos do webservice são armazenados localmente usando o método
 * {@link Agenda#fromModel(AgendaModel)}.
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

    @SerializedName("status")
    public int statusAgenda;
}
