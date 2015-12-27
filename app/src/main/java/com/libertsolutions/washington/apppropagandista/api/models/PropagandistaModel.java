package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Classe modelo para envio e recebimento de dados do Propagandista para webservice.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class PropagandistaModel {
    public String nome;

    public String cpf;

    @SerializedName("Usuario")
    public UsuarioModel usuario;

    public static class UsuarioModel {
        public String email;
    }
}
