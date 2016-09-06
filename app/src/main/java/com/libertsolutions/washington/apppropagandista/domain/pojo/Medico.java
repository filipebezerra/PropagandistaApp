package com.libertsolutions.washington.apppropagandista.domain.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author Filipe Bezerra
 */
public class Medico {
    @SerializedName("id_cliente")
    @Expose
    public final long id;

    @SerializedName("id_medico")
    @Expose
    public final long idMedico;

    @SerializedName("nome")
    @Expose
    public final String nome;

    @SerializedName("clinica")
    @Expose
    public final String clinica;

    @SerializedName("dataAniversario")
    @Expose
    public final String dataAniversario;

    @SerializedName("secretaria")
    @Expose
    public final String secretaria;

    @SerializedName("telefone")
    @Expose
    public final String telefone;

    @SerializedName("email")
    @Expose
    public final String email;

    @SerializedName("crm")
    @Expose
    public final String crm;

    @SerializedName("id_especialidade")
    @Expose
    public final int idEspecialidade;

    @SerializedName("Endereco")
    @Expose
    public final List<Endereco> enderecos;

    public Medico(
            long id, long idMedico, String nome, String clinica, String dataAniversario,
            String secretaria, String telefone, String email, String crm, int idEspecialidade,
            List<Endereco> enderecos) {
        this.id = id;
        this.idMedico = idMedico;
        this.nome = nome;
        this.clinica = clinica;
        this.dataAniversario = dataAniversario;
        this.secretaria = secretaria;
        this.telefone = telefone;
        this.email = email;
        this.crm = crm;
        this.idEspecialidade = idEspecialidade;
        this.enderecos = enderecos;
    }

    public long getId() {
        return id;
    }

    public long getIdMedico() {
        return idMedico;
    }

    public String getNome() {
        return nome;
    }

    public String getClinica() {
        return clinica;
    }

    public String getDataAniversario() {
        return dataAniversario;
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

    public int getIdEspecialidade() {
        return idEspecialidade;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }
}
