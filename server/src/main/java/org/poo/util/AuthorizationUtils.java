package org.poo.util;

import org.poo.model.pessoa.Cargo;
import org.poo.model.pessoa.Funcionario;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationUtils {

    public static boolean isGerente(HttpServletRequest request) {
        Funcionario logado = (Funcionario) request.getAttribute("usuarioLogado");
        return logado != null && logado.getCargo() == Cargo.GERENTE;
    }
}
