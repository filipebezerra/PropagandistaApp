package com.libertsolutions.washington.apppropagandista.Model;

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
    private Integer mIdAgenda;

    public static Visita iniciar(final Long data, final Double latitude, final Double longitude,
            final Integer idAgenda) {
        return new Visita()
                .setDataInicio(data)
                .setLatInicial(latitude)
                .setLongInicial(longitude)
                .setIdAgenda(idAgenda);
    }

    @Override
    public Visita setId(Integer id) {
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

    public Integer getIdAgenda() {
        return mIdAgenda;
    }

    public Visita setIdAgenda(Integer idAgenda) {
        mIdAgenda = idAgenda;
        return this;
    }
}