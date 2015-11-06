package com.libertsolutions.washington.apppropagandista.Model;

import java.security.Timestamp;

/**
 * Created by washington on 04/11/2015.
 */
public class Medico {
    private Integer id_medico;
    private String nome;
    private Timestamp dtAniversario;
    private String secretaria;
    private String telefone;
    private String email;
    private String crm;
    private String especialidade;

    //Metódos Set's
    public void setId_medico(int id_medico) {
        this.id_medico = id_medico;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDtAniversario(Timestamp dtAniversario) {
        this.dtAniversario = dtAniversario;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    //Metódos Get's
    public int getId_medico() {
        return id_medico;
    }

    public String getNome() {
        return nome;
    }

    public Timestamp getDtAniversario() {
        return dtAniversario;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCrm() {
        return crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }
}
