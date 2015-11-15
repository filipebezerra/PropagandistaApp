package com.libertsolutions.washington.apppropagandista.Model;

import com.google.gson.annotations.Expose;

/**
 * Created by washington on 04/11/2015.
 */
public class Usuario {
    private Integer id_usuario;
    private String nome;
    private String cpf;
    @Expose
    private String email;
    private String senha;

    //Metódos Set's
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    //Metódos Get's
    public int getId_usuario() {
        return id_usuario;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
