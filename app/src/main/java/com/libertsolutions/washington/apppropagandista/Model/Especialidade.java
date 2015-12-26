package com.libertsolutions.washington.apppropagandista.Model;

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

    @Override
    public Especialidade setId(Integer id) {
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
}
