package com.libertsolutions.washington.apppropagandista.domain.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 */
public class Endereco {
    @SerializedName("id_cliente")
    public final long id;

    @SerializedName("id_endereco")
    public final int idEndereco;

    @SerializedName("endereco")
    @Expose
    public final String endereco;

    @SerializedName("cep")
    @Expose
    public final String cep;

    @SerializedName("numero")
    @Expose
    public final String numero;

    @SerializedName("bairro")
    @Expose
    public final String bairro;

    @SerializedName("complemento")
    @Expose
    public final String complemento;

    @SerializedName("longitude")
    @Expose
    public final String longitude;

    @SerializedName("latitude")
    @Expose
    public final String latitude;

    @SerializedName("obs")
    @Expose
    public final String observacao;

    @SerializedName("id_medico")
    public final long idMedico;

    public Endereco(
            long id, int idEndereco, String endereco, String cep, String numero,
            String bairro, String complemento, String longitude, String latitude,
            String observacao, long idMedico) {
        this.id = id;
        this.idEndereco = idEndereco;
        this.endereco = endereco;
        this.cep = cep;
        this.numero = numero;
        this.bairro = bairro;
        this.complemento = complemento;
        this.longitude = longitude;
        this.latitude = latitude;
        this.observacao = observacao;
        this.idMedico = idMedico;
    }

    public long getId() {
        return id;
    }

    public int getIdEndereco() {
        return idEndereco;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCep() {
        return cep;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getObservacao() {
        return observacao;
    }

    public long getIdMedico() {
        return idMedico;
    }
}
