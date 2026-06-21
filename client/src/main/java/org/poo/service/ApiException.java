package org.poo.service;

public class ApiException extends Exception {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static boolean isCausa(Throwable t) {
        return t instanceof ApiException || t.getCause() instanceof ApiException;
    }

    public static String mensagemDe(Throwable t) {
        Throwable causa = t.getCause() != null ? t.getCause() : t;
        return causa.getMessage();
    }
}
