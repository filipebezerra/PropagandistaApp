package com.libertsolutions.washington.apppropagandista.Model;

/**
 * @author Washington, Filipe Bezerra
 */
public class Visita {
    private Integer mId;

    private Long mDataInicio;

    private Double mLatInicial;

    private Double mLongInicial;

    private Long mDataFim;

    private Double mLatFinal;

    private Double mLongFinal;

    private String mDetalhes;

    private Agenda mAgenda;

    private Integer mStatus;

    public static Visita iniciar(final Long data, final Double latitude, final Double longitude) {
        final Visita visita = new Visita();
        visita.setDataInicio(data);
        visita.setLatInicial(latitude);
        visita.setLongInicial(longitude);
        return visita;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Long getDataInicio() {
        return mDataInicio;
    }

    public void setDataInicio(Long dataInicio) {
        this.mDataInicio = dataInicio;
    }

    public Double getLatInicial() {
        return mLatInicial;
    }

    public void setLatInicial(Double latInicial) {
        this.mLatInicial = latInicial;
    }

    public Double getLongInicial() {
        return mLongInicial;
    }

    public void setLongInicial(Double longInicial) {
        this.mLongInicial = longInicial;
    }

    public Long getDataFim() {
        return mDataFim;
    }

    public void setDataFim(Long dataFim) {
        this.mDataFim = dataFim;
    }

    public Double getLatFinal() {
        return mLatFinal;
    }

    public void setLatFinal(Double latFinal) {
        this.mLatFinal = latFinal;
    }

    public Double getLongFinal() {
        return mLongFinal;
    }

    public void setLongFinal(Double longFinal) {
        this.mLongFinal = longFinal;
    }

    public String getDetalhes() {
        return mDetalhes;
    }

    public void setDetalhes(String detalhes) {
        this.mDetalhes = detalhes;
    }

    public Agenda getAgenda() {
        return mAgenda;
    }

    public void setAgenda(Agenda agenda) {
        this.mAgenda = agenda;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer status) {
        this.mStatus = status;
    }
}