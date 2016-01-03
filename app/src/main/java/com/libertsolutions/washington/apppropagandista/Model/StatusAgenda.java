package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.IntRange;

/**
 * Representa as etapas da execução da agenda. Por padrão uma agenda recebe
 * o status {@link #Pendente}.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 25/12/2015
 * @since 1.0
 */

public enum StatusAgenda {
    Pendente("Pendente"),
    EmAtendimento("Em atendimento"),
    Cancelado("Cancelado"),
    NaoVisita("Não visita"),
    Finalizado("Finalizado");

    private final String mDescricao;

    StatusAgenda(String descricao) {
        mDescricao = descricao;
    }

    public String descricao() {
        return mDescricao;
    }

    public static StatusAgenda fromOrdinal(@IntRange(from = 0, to = 4) int ordinal) {
        switch (ordinal) {
            case 0: return Pendente;
            case 1: return EmAtendimento;
            case 2: return Cancelado;
            case 3: return NaoVisita;
            case 4: return Finalizado;
            default: return null;
        }
    }
}
