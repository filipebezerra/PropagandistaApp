package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import org.joda.time.DateTime;

import static com.libertsolutions.washington.apppropagandista.Model.StatusAgenda.Finalizado;
import static com.libertsolutions.washington.apppropagandista.Model.StatusAgenda.Pendente;

/**
 * Classe modelo dos dados da agenda.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0
 * @since 1.0
 */
public class Agenda extends ModeloBase<Agenda> {
    // id da agenda no servidor
    private Integer mIdAgenda;

    // data do compromisso, armazenado como milessegundos
    private Long mDataCompromisso;

    private String mObservacao;

    // id de relacionamento com tabela Medico
    private Integer mIdMedico;

    // status do compromisso, é armazenado como inteiro
    private StatusAgenda mStatusAgenda;

    /**
     * Obtém os dados da agenda importados do webservice.
     *
     * @param model o modelo de dados da agenda recuperado do webservice.
     * @return a agenda.
     */
    public static Agenda fromModel(@NonNull AgendaModel model) {
        Preconditions.checkNotNull(model, "model não pode ser nulo");
        Preconditions.checkNotNull(model.idAgenda, "idAgenda não pode ser nulo");
        Preconditions.checkNotNull(model.dtCompromisso, "dtCompromisso não pode ser nulo");
        Preconditions.checkNotNull(model.idMedico, "idMedico não pode ser nulo");
        Preconditions.checkState(
                (model.statusAgenda >= Pendente.ordinal())
                        && (model.statusAgenda <= Finalizado.ordinal()),
                String.format("statusAgenda %d é inválido", model.statusAgenda));

        return new Agenda()
                .setId(model.idCliente)
                .setIdAgenda(model.idAgenda)
                .setDataCompromisso(DateTime.parse(model.dtCompromisso).getMillis())
                .setObservacao(model.observacao)
                .setIdMedico(model.idMedico)
                .setStatusAgenda(StatusAgenda.fromOrdinal(model.statusAgenda))
                .setStatus(Status.Importado);
    }

    public static AgendaModel toModel(@NonNull Agenda agenda) {
        Preconditions.checkNotNull(agenda, "agenda não pode ser nulo");
        Preconditions.checkNotNull(agenda.getId(),
                "agenda.getId() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getIdMedico(),
                "agenda.getIdMedico() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getStatusAgenda(),
                "agenda.getStatusAgenda() não pode ser nulo");
        Preconditions.checkNotNull(agenda.getDataCompromisso(),
                "agenda.getDataCompromisso() não pode ser nulo");

        final AgendaModel model = new AgendaModel();
        model.idCliente = agenda.getId();
        model.idAgenda = agenda.getIdAgenda();
        model.idMedico = agenda.getIdMedico();
        model.statusAgenda = agenda.getStatusAgenda().ordinal();
        model.dtCompromisso = new DateTime(agenda.getDataCompromisso())
                .toString("yyyy-MM-dd'T'HH:mm:ss");
        model.observacao = agenda.getObservacao();

        return model;
    }

    @Override
    public Agenda setId(Integer id) {
        mId = id;
        return this;
    }

    @Override
    public Agenda setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdAgenda() {
        return mIdAgenda;
    }

    public Agenda setIdAgenda(Integer idAgenda) {
        mIdAgenda = idAgenda;
        return this;
    }

    public Long getDataCompromisso() {
        return mDataCompromisso;
    }

    public Agenda setDataCompromisso(Long dataCompromisso) {
        mDataCompromisso = dataCompromisso;
        return this;
    }

    public String getObservacao() {
        return mObservacao;
    }

    public Agenda setObservacao(String observacao) {
        mObservacao = observacao;
        return this;
    }

    public Integer getIdMedico() {
        return mIdMedico;
    }

    public Agenda setIdMedico(Integer idMedico) {
        mIdMedico = idMedico;
        return this;
    }

    public StatusAgenda getStatusAgenda() {
        return mStatusAgenda;
    }

    public Agenda setStatusAgenda(
            StatusAgenda statusAgenda) {
        mStatusAgenda = statusAgenda;
        return this;
    }
}
