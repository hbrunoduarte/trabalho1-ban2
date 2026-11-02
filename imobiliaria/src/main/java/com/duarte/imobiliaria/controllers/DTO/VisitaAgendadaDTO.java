package com.duarte.imobiliaria.controllers.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitaAgendadaDTO {

    private LocalDate data;
    private LocalTime horario;

    public VisitaAgendadaDTO() {
    }

    public VisitaAgendadaDTO(LocalDate data, LocalTime horario) {
        this.data = data;
        this.horario = horario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }
}
