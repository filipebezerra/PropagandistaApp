package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import com.libertsolutions.washington.apppropagandista.api.models.MedicoModel.EnderecoModel;

/**
 * Classe modelo dos dados da endereço do {@link Medico}.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class Endereco extends ModeloBase<Endereco> {
    // id do endereço no servidor
    private Integer idEndereco;

    private String mEndereco;

    private String mCep;

    private String mNumero;

    private String mBairro;

    private String mComplemento;

    private String mLongitude;

    private String mLatitude;

    private String mObservacao;

    // id de relacionamento com tabela Medico
    private Integer idMedico;

    /**
     * Obtém os endereços recebidos pelo webservice para armazenar local.
     *
     * @param model o modelo de dados da API recuperado do webservice.
     * @return a agenda.
     */
    public static Endereco fromModel(@NonNull EnderecoModel model) {
        Utils.checkNotNull(model, "model não pode ser nulo");
        Utils.checkNotNull(model.idEndereco, "model.idEndereco não pode ser nulo");
        Utils.checkNotNull(model.endereco, "model.endereco não pode ser nulo");
        Utils.checkNotNull(model.bairro, "model.bairro não pode ser nulo");
        Utils.checkNotNull(model.idMedico, "model.idMedico não pode ser nulo");

        return new Endereco()
                .setId(model.idCliente)
                .setIdEndereco(model.idEndereco)
                .setIdMedico(model.idMedico)
                .setEndereco(model.endereco)
                .setCep(model.cep)
                .setNumero(model.numero)
                .setBairro(model.bairro)
                .setComplemento(model.complemento)
                .setLatitude(model.latitude)
                .setLongitude(model.longitude)
                .setObservacao(model.obs);
    }

    /**
     * Transfere os endereços armazenados localmente para webservice.
     *
     * @param endereco o modelo de dados de persistência armazenados localmente.
     * @return o modelo de dados da API.
     */
    public static EnderecoModel toModel(@NonNull Endereco endereco) {
        Utils.checkNotNull(endereco, "model não pode ser nulo");
        Utils.checkNotNull(endereco.getId(), "endereco.getId() não pode ser nulo");
        Utils.checkNotNull(endereco.getIdEndereco(),
                "endereco.getIdEndereco() não pode ser nulo");
        Utils.checkNotNull(endereco.getEndereco(),
                "endereco.getEndereco() não pode ser nulo");
        Utils.checkNotNull(endereco.getBairro(), "endereco.getBairro() não pode ser nulo");
        Utils.checkNotNull(endereco.getIdMedico(),
                "endereco.getIdMedico() não pode ser nulo");

        final EnderecoModel model = new EnderecoModel();
        model.idCliente = endereco.getId();
        model.idEndereco = endereco.getIdEndereco();
        model.idMedico = endereco.getIdMedico();
        model.endereco = endereco.getEndereco();
        model.cep = endereco.getCep();
        model.numero = endereco.getNumero();
        model.bairro = endereco.getBairro();
        model.complemento = endereco.getComplemento();
        model.latitude = endereco.getLatitude();
        model.longitude = endereco.getLongitude();
        model.obs = endereco.getObservacao();

        return model;
    }

    @Override
    public Endereco setId(Long id) {
        mId = id;
        return this;
    }

    @Override
    public Endereco setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdEndereco() {
        return idEndereco;
    }

    public Endereco setIdEndereco(Integer idEndereco) {
        this.idEndereco = idEndereco;
        return this;
    }

    public String getEndereco() {
        return mEndereco;
    }

    public Endereco setEndereco(String endereco) {
        mEndereco = endereco;
        return this;
    }

    public String getCep() {
        return mCep;
    }

    public Endereco setCep(String cep) {
        mCep = cep;
        return this;
    }

    public String getNumero() {
        return mNumero;
    }

    public Endereco setNumero(String numero) {
        mNumero = numero;
        return this;
    }

    public String getBairro() {
        return mBairro;
    }

    public Endereco setBairro(String bairro) {
        mBairro = bairro;
        return this;
    }

    public String getComplemento() {
        return mComplemento;
    }

    public Endereco setComplemento(String complemento) {
        mComplemento = complemento;
        return this;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public Endereco setLongitude(String longitude) {
        mLongitude = longitude;
        return this;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public Endereco setLatitude(String latitude) {
        mLatitude = latitude;
        return this;
    }

    public String getObservacao() {
        return mObservacao;
    }

    public Endereco setObservacao(String observacao) {
        mObservacao = observacao;
        return this;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public Endereco setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
        return this;
    }

    @Override
    public String toString() {
        return getEndereco() + ", " +
                (TextUtils.isEmpty(getNumero()) ? "" : (getNumero() + ", ")) +
                (TextUtils.isEmpty(getCep()) ? "" : (getCep() + ", ")) +
                getBairro();
    }
}
