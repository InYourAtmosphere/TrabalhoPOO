package org.poo.model;

import java.time.LocalDateTime;
import java.time.Year;

public class Veiculo {

    private long id;
    private String marca;
    private String modelo;
    private Year ano;
    private String placa;
    private String chassi;
    private float kmAtual;
    private StatusVeiculo status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public long getId() {
        return id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Year getAno() {
        return ano;
    }

    public void setAno(Year ano) {
        this.ano = ano;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public float getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(float kmAtual) {
        this.kmAtual = kmAtual;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void atualizarStatus(StatusVeiculo status) {

    }

    public void atualizarKm(float kmAtual) {

    }

}
