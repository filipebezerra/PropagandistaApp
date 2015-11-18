package com.libertsolutions.washington.apppropagandista.Model;

/**
 * Created by washington on 17/11/2015.
 */
public class Visita {
    private Integer id_visita;
    private String dtInicio;
    private String horaInicio;
    private Double longInicial;
    private Double latInicial;
    private String dtFim;
    private String horaFim;
    private Double longFinal;
    private Double latFinal;
    private String detalhes;
    private Agenda agenda;

    //Metódos Get's
    public Integer getId_visita() {
        return id_visita;
    }

    public String getDtInicio() {
        return dtInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public Double getLongInicial() {
        return longInicial;
    }

    public Double getLatInicial() {
        return latInicial;
    }

    public String getDtFim() {
        return dtFim;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public Double getLongFinal() {
        return longFinal;
    }

    public Double getLatFinal() {
        return latFinal;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    //Metódos Set's

    public void setId_visita(Integer id_visita) {
        this.id_visita = id_visita;
    }

    public void setDtInicio(String dtInicio) {
        this.dtInicio = dtInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setLongInicial(Double longInicial) {
        this.longInicial = longInicial;
    }

    public void setLatInicial(Double latInicial) {
        this.latInicial = latInicial;
    }

    public void setDtFim(String dtFim) {
        this.dtFim = dtFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public void setLongFinal(Double longFinal) {
        this.longFinal = longFinal;
    }

    public void setLatFinal(Double latFinal) {
        this.latFinal = latFinal;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }
}
