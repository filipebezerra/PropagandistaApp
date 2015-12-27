package com.libertsolutions.washington.apppropagandista.Model;

/**
 * Classe modelo base para todos modelos. Esta classe é usada como tipo base para
 * a classe {@link com.libertsolutions.washington.apppropagandista.Dao.DAOGenerico}.
 *
 * @author Filipe Bezerra
 * @version 1.0, 26/12/2015
 * @since 1.0
 */
public abstract class ModeloBase<T> {
    // id de armazenamento interno do SQLite, deverá ser inteiro e auto incremento
    protected Long mId;

    // status da sincronização
    protected Status mStatus;

    public Long getId() {
        return mId;
    }

    public abstract T setId(Long id);

    public Status getStatus() {
        return mStatus;
    }

    public abstract T setStatus(Status status);
}
