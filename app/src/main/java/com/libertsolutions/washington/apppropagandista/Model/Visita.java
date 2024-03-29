package com.libertsolutions.washington.apppropagandista.Model;

import android.support.annotation.NonNull;
import com.libertsolutions.washington.apppropagandista.Util.Utils;
import com.libertsolutions.washington.apppropagandista.api.models.VisitaModel;
import org.joda.time.DateTime;

/**
 * Classe modelo dos dados de visita.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0
 * @since 1.0
 */
public class Visita extends ModeloBase<Visita> {
    // id da agenda no servidor
    private Integer mIdVisita;

    // data do início da visita, armazenado como milessegundos
    private Long mDataInicio;

    // latitude inicial
    private Double mLatInicial;

    // longitude inicial
    private Double mLongInicial;

    // data do fim da visita, armazenado como milessegundos
    private Long mDataFim;

    // latitude final
    private Double mLatFinal;

    // longitude final
    private Double mLongFinal;

    private String mDetalhes;

    // id de relacionamento com tabela Agenda
    private Long mIdAgenda;

    public static Visita iniciar(@NonNull final Long data, @NonNull final Double latitude,
            @NonNull final Double longitude, @NonNull final Long idAgenda) {
        return new Visita()
                .setDataInicio(data)
                .setLatInicial(latitude)
                .setLongInicial(longitude)
                .setIdAgenda(idAgenda)
                .setStatus(Status.Pendente);
    }

    public static Visita fromModel(@NonNull VisitaModel model) {
        Utils.checkNotNull(model, "model não pode ser nulo");
        Utils.checkNotNull(model.idVisita, "model.idVisita não pode ser nulo");
        Utils.checkNotNull(model.dtInicio, "model.dtInicio não pode ser nulo");
        Utils.checkNotNull(model.latInicial, "model.latInicial não pode ser nulo");
        Utils.checkNotNull(model.longFinal, "model.latInicial não pode ser nulo");
        Utils.checkState(model.idAgenda != 0, "model.idAgenda é inválido");

        return new Visita()
                .setId(model.idCliente)
                .setIdVisita(model.idVisita)
                .setDataInicio(model.dtInicio != null ?
                        DateTime.parse(model.dtInicio).getMillis() : null)
                .setLatInicial(model.latInicial)
                .setLongInicial(model.longInicial)
                .setDataInicio(model.dtFim != null ?
                        DateTime.parse(model.dtFim).getMillis() : null)
                .setLatInicial(model.latFinal)
                .setLongInicial(model.longFinal)
                .setDetalhes(model.detalhes)
                .setIdAgenda(model.idAgenda);
    }

    public static VisitaModel toModel(@NonNull Visita visita) {
        Utils.checkNotNull(visita, "visita não pode ser nulo");
        Utils.checkNotNull(visita.getId(),
                "visita.getId() não pode ser nulo");
        Utils.checkNotNull(visita.getDataInicio(),
                "visita.getDataInicio() não pode ser nulo");
        Utils.checkNotNull(visita.getLatInicial(),
                "visita.getLatInicial() não pode ser nulo");
        Utils.checkNotNull(visita.getLongInicial(),
                "visita.getLongInicial() não pode ser nulo");
        Utils.checkNotNull(visita.getIdAgenda(),
                "visita.getIdAgenda() não pode ser nulo");

        final VisitaModel model = new VisitaModel();
        model.idCliente = visita.getId();
        model.idVisita = visita.getIdVisita() != null ? visita.getIdVisita() : 0;
        model.dtInicio = visita.getDataInicio() != null ?
                new DateTime(visita.getDataInicio()).toString("yyyy-MM-dd'T'HH:mm:ss") :
                null;
        model.latInicial = visita.getLatInicial();
        model.longInicial = visita.getLongInicial();
        model.dtFim = visita.getDataFim() != null ?
                new DateTime(visita.getDataFim()).toString("yyyy-MM-dd'T'HH:mm:ss") :
                null;
        model.latFinal = visita.getLatFinal();
        model.longFinal = visita.getLongFinal();
        model.detalhes = visita.getDetalhes();
        model.idAgenda = visita.getIdAgenda();

        return model;
    }

    @Override
    public Visita setId(Long id) {
        mId = id;
        return this;
    }

    @Override
    public Visita setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdVisita() {
        return mIdVisita;
    }

    public Visita setIdVisita(Integer idVisita) {
        mIdVisita = idVisita;
        return this;
    }

    public Long getDataInicio() {
        return mDataInicio;
    }

    public Visita setDataInicio(Long dataInicio) {
        mDataInicio = dataInicio;
        return this;
    }

    public Double getLatInicial() {
        return mLatInicial;
    }

    public Visita setLatInicial(Double latInicial) {
        mLatInicial = latInicial;
        return this;
    }

    public Double getLongInicial() {
        return mLongInicial;
    }

    public Visita setLongInicial(Double longInicial) {
        mLongInicial = longInicial;
        return this;
    }

    public Long getDataFim() {
        return mDataFim;
    }

    public Visita setDataFim(Long dataFim) {
        mDataFim = dataFim;
        return this;
    }

    public Double getLatFinal() {
        return mLatFinal;
    }

    public Visita setLatFinal(Double latFinal) {
        mLatFinal = latFinal;
        return this;
    }

    public Double getLongFinal() {
        return mLongFinal;
    }

    public Visita setLongFinal(Double longFinal) {
        mLongFinal = longFinal;
        return this;
    }

    public String getDetalhes() {
        return mDetalhes;
    }

    public Visita setDetalhes(String detalhes) {
        mDetalhes = detalhes;
        return this;
    }

    public Long getIdAgenda() {
        return mIdAgenda;
    }

    public Visita setIdAgenda(Long idAgenda) {
        mIdAgenda = idAgenda;
        return this;
    }
}