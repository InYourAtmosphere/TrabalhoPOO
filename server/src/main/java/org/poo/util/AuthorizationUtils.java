package org.poo.util;

import org.poo.model.pessoa.Cargo;
import org.poo.model.pessoa.Funcionario;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationUtils {

    public static boolean isGerente(HttpServletRequest request) {
        Funcionario logado = (Funcionario) request.getAttribute("usuarioLogado");
        return logado != null && logado.getCargo() == Cargo.GERENTE;
    }

    public static boolean isSupervisor(HttpServletRequest request) {
        Funcionario logado = (Funcionario) request.getAttribute("usuarioLogado");
        return logado != null && logado.getCargo() == Cargo.SUPERVISOR;
    }

    public static boolean isGerenteOuSupervisor(HttpServletRequest request) {
        return isGerente(request) || isSupervisor(request);
    }

    public static Long unidadeIdDoLogado(HttpServletRequest request) {
        Funcionario logado = (Funcionario) request.getAttribute("usuarioLogado");
        return (logado != null && logado.getUnidade() != null) ? logado.getUnidade().getId() : null;
    }

    // GERENTE gerencia qualquer unidade; SUPERVISOR só a própria.
    public static boolean podeGerenciarUnidade(HttpServletRequest request, Long unidadeId) {
        if (isGerente(request)) return true;
        if (!isSupervisor(request)) return false;
        Long propriaUnidade = unidadeIdDoLogado(request);
        return propriaUnidade != null && propriaUnidade.equals(unidadeId);
    }
}
