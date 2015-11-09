package com.libertsolutions.washington.apppropagandista.Model;

/**
 * Created by washington on 08/11/2015.
 */
public class Propagandista {
    private int id_propagandista;
    private String nome;
    private String cpf;
    private String email;

    //Metódos Set's
    public void setId_propagandista(int id_propagandista) {
        this.id_propagandista = id_propagandista;
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

    //Metódos Get's
    public int getId_propagandista() {
        return id_propagandista;
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

    public static final String prop_nome = "nome";
    public static final String pro_email = "email";
}
