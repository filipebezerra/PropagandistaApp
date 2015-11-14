package com.libertsolutions.washington.apppropagandista.Enum;

/**
 * Created by washington on 14/11/2015.
 */
public enum  StatusAgenda {
    Pendente(1),EmAtendimento(2),Finalizado(3);

    private final int codigo;

    StatusAgenda(int codigo) { this.codigo = codigo; }

    int codigo() { return codigo; }

    public static StatusAgenda status(int codigo) {
        for (StatusAgenda cor: StatusAgenda.values()) {
            if (codigo == cor.codigo()) return cor;
        }
        throw new IllegalArgumentException("codigo invalido");
    }
}
