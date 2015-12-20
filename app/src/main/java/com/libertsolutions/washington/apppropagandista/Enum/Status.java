package com.libertsolutions.washington.apppropagandista.Enum;

/**
 * Created by washington on 20/12/2015.
 */
public enum Status {
    Pendente(1),Enviado(2),Alterado(3);

    public int codigo;

    Status(int codigo) { this.codigo = codigo; }

    int codigo() { return codigo; }

    public static StatusAgenda status(int codigo) {
        for (StatusAgenda cor: StatusAgenda.values()) {
            if (codigo == cor.codigo()) return cor;
        }
        throw new IllegalArgumentException("codigo invalido");
    }
}
