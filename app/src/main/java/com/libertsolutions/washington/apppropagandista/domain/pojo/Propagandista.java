package com.libertsolutions.washington.apppropagandista.domain.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 */
public class Propagandista {
    @SerializedName("nome")
    @Expose
    public final String nome;

    @SerializedName("cpf")
    @Expose
    public final String cpf;

    @SerializedName("Usuario")
    @Expose
    public final Usuario usuario;

    public Propagandista(Usuario usuario, String cpf, String nome) {
        this.usuario = usuario;
        this.cpf = cpf;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public class Usuario {
        @SerializedName("email")
        @Expose
        public final String email;

        public Usuario(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }
}
