package org.poo.ui;

public class SessionContext {

    private static SessionContext instance;

    private String token;
    private String nomeUsuario;

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

    public boolean isAutenticado() {
        return token != null && !token.isBlank();
    }

    public void limpar() {
        this.token = null;
        this.nomeUsuario = null;
    }
}
