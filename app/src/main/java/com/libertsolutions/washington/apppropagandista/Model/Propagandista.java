package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.NonNull;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import com.libertsolutions.washington.apppropagandista.api.models.PropagandistaModel;

/**
 * Classe modelo dos dados do propagandista.
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class Propagandista {
    private String nome;

    private String cpf;

    private String email;

    /**
     * Obtém os dados do propagandista importados do webservice.
     *
     * @param model o modelo de dados do propagandista recuperado do webservice.
     * @return o propagandista.
     */
    public static Propagandista fromModel(@NonNull PropagandistaModel model) {
        Utils.checkNotNull(model, "model não pode ser nulo");
        Utils.checkNotNull(model.nome, "model.nome não pode ser nulo");
        Utils.checkNotNull(model.cpf, "model.cpf não pode ser nulo");
        Utils.checkNotNull(model.usuario, "model.usuario não pode ser nulo");

        return new Propagandista()
                .setNome(model.nome)
                .setCpf(model.cpf)
                .setEmail(model.usuario.email);
    }

    public String getNome() {
        return nome;
    }

    public Propagandista setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public String getCpf() {
        return cpf;
    }

    public Propagandista setCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Propagandista setEmail(String email) {
        this.email = email;
        return this;
    }
}
