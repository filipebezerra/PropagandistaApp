package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.NonNull;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import com.libertsolutions.washington.apppropagandista.api.models.EspecialidadeModel;

/**
 * Classe modelo dos dados.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class Especialidade extends ModeloBase<Especialidade> {
    // id da especialidade no servidor
    private Integer mIdEspecialidade;

    private String mNome;

    /**
     * Obtém os endereços recebidos pelo webservice para armazenar local.
     *
     * @param model o modelo de dados da API recuperado do webservice.
     * @return a especialidade.
     */
    public static Especialidade fromModel(@NonNull EspecialidadeModel model) {
        Utils.checkNotNull(model, "model não pode ser nulo");
        Utils.checkNotNull(model.idEspecialidade,
                "model.idEspecialidade não pode ser nulo");
        Utils.checkNotNull(model.nome, "model.nome não pode ser nulo");

        return new Especialidade()
                .setIdEspecialidade(model.idEspecialidade)
                .setNome(model.nome);
    }

    /**
     * Transfere os endereços armazenados localmente para webservice.
     *
     * @param especialidade o modelo de dados de persistência armazenados localmente.
     * @return o modelo de dados da API.
     */
    public static EspecialidadeModel toModel(@NonNull Especialidade especialidade) {
        Utils.checkNotNull(especialidade, "model não pode ser nulo");
        Utils.checkNotNull(especialidade.getIdEspecialidade(),
                "especialidade.getIdEspecialidade() não pode ser nulo");
        Utils.checkNotNull(especialidade.getNome(),
                "especialidade.getEndereco() não pode ser nulo");

        final EspecialidadeModel model = new EspecialidadeModel();
        model.idEspecialidade = especialidade.getIdEspecialidade();
        model.nome = especialidade.getNome();
        return model;
    }

    @Override
    public Especialidade setId(Long id) {
        mId = id;
        return this;
    }

    @Override
    public Especialidade setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdEspecialidade() {
        return mIdEspecialidade;
    }

    public Especialidade setIdEspecialidade(Integer idEspecialidade) {
        mIdEspecialidade = idEspecialidade;
        return this;
    }

    public String getNome() {
        return mNome;
    }

    public Especialidade setNome(String nome) {
        mNome = nome;
        return this;
    }

    @Override
    public String toString() {
        return getNome();
    }
}
