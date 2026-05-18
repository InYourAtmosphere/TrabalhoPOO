package org.poo.model;

import java.time.LocalDateTime;

public class Cliente {

    private long id;
    private String nome;
    private String documentoIdentidade;
    private String codHabilitacao;
    private String telefone;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumentoIdentidade() {
        return documentoIdentidade;
    }

    public void setDocumentoIdentidade(String documentoIdentidade) {
        this.documentoIdentidade = documentoIdentidade;
    }

    public String getCodHabilitacao() {
        return codHabilitacao;
    }

    public void setCodHabilitacao(String codHabilitacao) {
        this.codHabilitacao = codHabilitacao;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean validarDocumento() {
        return true;
    }
}
