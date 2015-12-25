package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.IntRange;

/**
 * Representa o status da sincronização de um registro armazenado local.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 25/12/2015
 * @since 1.0
 */

public enum Status {
    Importado, Pendente, Enviado, Alterado;

    public static Status fromOrdinal(@IntRange(from = 0, to = 3) int ordinal) {
        switch (ordinal) {
            case 0: return Importado;
            case 1: return Pendente;
            case 2: return Enviado;
            case 3: return Alterado;
            default: return null;
        }
    }
}
