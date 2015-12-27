package com.libertsolutions.washington.apppropagandista.Model;

/**
 * Classe modelo dos dados do usuário.
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class Usuario extends ModeloBase<Usuario> {
    private Integer mIdUsuario;

    private String mNome;

    private String mCpf;

    private String mEmail;

    private String mSenha;

    @Override
    public Usuario setId(Integer id) {
        mId = id;
        return this;
    }

    @Override
    public Usuario setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdUsuario() {
        return mIdUsuario;
    }

    public Usuario setIdUsuario(Integer idUsuario) {
        mIdUsuario = idUsuario;
        return this;
    }

    public String getNome() {
        return mNome;
    }

    public Usuario setNome(String nome) {
        mNome = nome;
        return this;
    }

    public String getCpf() {
        return mCpf;
    }

    public Usuario setCpf(String cpf) {
        mCpf = cpf;
        return this;
    }

    public String getEmail() {
        return mEmail;
    }

    public Usuario setEmail(String email) {
        mEmail = email;
        return this;
    }

    public String getSenha() {
        return mSenha;
    }

    public Usuario setSenha(String senha) {
        mSenha = senha;
        return this;
    }
}
