package com.libertsolutions.washington.apppropagandista.Model;

/**
 * Created by washington on 04/11/2015.
 */
public class Agenda {
    private Integer id_agenda;
    private String data;
    private String hora;
    private String obs;
    private Medico id_medico;
    private Integer status;
    private Integer statusAgenda;
    private Integer id_unico;

    //Metódos Get's
    public Integer getId_agenda() {
        return id_agenda;
    }

    public String getData() {
        return data;
    }
    public String getHora() {
        return hora;
    }

    public String getObs() {
        return obs;
    }

    public Medico getId_medico() {
        return id_medico;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getStatusAgenda() {
        return statusAgenda;
    }

    public Integer getId_unico() {
        return id_unico;
    }
    
    //Metódos Set's
    public void setId_agenda(Integer id_agenda) {
        this.id_agenda = id_agenda;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public void setId_medico(Medico id_medico) {
        this.id_medico = id_medico;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setStatusAgenda(Integer statusAgenda) {
        this.statusAgenda = statusAgenda;
    }

    public void setId_unico(Integer id_unico) {
        this.id_unico = id_unico;
    }
}
