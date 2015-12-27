package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Classe modelo para envio para e recebimento de dados de especialdiades do webservice.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/12/2015
 * @since 0.1.0
 */
public class EspecialidadeModel {
    @SerializedName("idEspecialidade")
    public int idEspecialidade;

    public String nome;
}
