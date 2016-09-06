package com.libertsolutions.washington.apppropagandista.domain.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 */
public class Especialidade {
    @SerializedName("id_especialidade")
    @Expose
    public final int idEspecialidade;

    @SerializedName("nome")
    @Expose
    public final String nome;

    public Especialidade(String nome, int idEspecialidade) {
        this.nome = nome;
        this.idEspecialidade = idEspecialidade;
    }

    public int getIdEspecialidade() {
        return idEspecialidade;
    }

    public String getNome() {
        return nome;
    }
}
