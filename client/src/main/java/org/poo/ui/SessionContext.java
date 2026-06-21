package org.poo.ui;

public class SessionContext {

    private static SessionContext instance;

    private String token;
    private String nomeUsuario;
    private String cargo;

    private SessionContext() {}

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isGerente() {
        return "GERENTE".equals(cargo);
    }

    public boolean isSupervisor() {
        return "SUPERVISOR".equals(cargo);
    }

    public boolean isGerenteOuSupervisor() {
        return isGerente() || isSupervisor();
    }

    public boolean isAutenticado() {
        return token != null && !token.isBlank();
    }

    public void limpar() {
        this.token = null;
        this.nomeUsuario = null;
        this.cargo = null;
    }
}
