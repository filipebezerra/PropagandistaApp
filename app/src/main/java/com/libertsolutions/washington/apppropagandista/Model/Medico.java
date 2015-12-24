package com.libertsolutions.washington.apppropagandista.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by washington on 04/11/2015.
 */
public class Medico {
    @Expose(serialize = false)
    private Integer id_medico;

    @Expose
    private Integer id_unico;

    @Expose
    private String nome;

    @Expose
    private String dtAniversario;

    @Expose
    private String secretaria;

    @Expose
    private String telefone;

    @Expose
    private String email;

    @Expose
    private String crm;

    @SerializedName("Especialidade")
    @Expose
    private Especialidade id_espcialidade;

    @Expose(serialize = false)
    private int status;

    //Metódos Set's
    public void setId_medico(Integer id_medico) {
        this.id_medico = id_medico;
    }

    public void setId_unico(Integer id_unico) {
        this.id_unico = id_unico;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDtAniversario(String dtAniversario) {
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

    public void setId_espcialidade(Especialidade id_espcialidade) {
        this.id_espcialidade = id_espcialidade;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    //Metódos Get's
    public Integer getId_medico() {
        return id_medico;
    }

    public Integer getId_unico() {
        return id_unico;
    }

    public String getNome() {
        return nome;
    }

    public String getDtAniversario() {
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

    public int getStatus() {
        return status;
    }

    public Especialidade getId_especialidade() {
        return id_espcialidade;
    }
}
