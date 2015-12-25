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
    Pendente, EmAtendimento, Cancelado, NaoVisita, Finalizado;

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
