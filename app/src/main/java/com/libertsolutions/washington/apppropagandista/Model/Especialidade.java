package com.libertsolutions.washington.apppropagandista.Model;

import com.google.gson.annotations.Expose;

/**
 * Created by washington on 21/12/2015.
 */
public class Especialidade {
    @Expose
    private Integer id_especialidade;

    @Expose
    private String nome;

    //Metódos Ge's
    public Integer getId_especialidade() {
        return id_especialidade;
    }

    public String getNome() {
        return nome;
    }

    //Metódos Set's
    public void setId_especialidade(Integer id_especialidade) {
        this.id_especialidade = id_especialidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
