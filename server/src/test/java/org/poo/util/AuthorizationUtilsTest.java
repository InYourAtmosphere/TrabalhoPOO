package org.poo.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.poo.model.Unidade;
import org.poo.model.pessoa.Cargo;
import org.poo.model.pessoa.Funcionario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationUtilsTest {

    private HttpServletRequest requestCom(Funcionario logado) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("usuarioLogado")).thenReturn(logado);
        return request;
    }

    private Funcionario funcionario(Cargo cargo, Long unidadeId) {
        Unidade unidade = null;
        if (unidadeId != null) {
            unidade = new Unidade();
            unidade.setId(unidadeId);
        }
        return Funcionario.builder().cargo(cargo).unidade(unidade).build();
    }

    @Test
    void isGerente_verdadeiroApenasParaGerente() {
        assertThat(AuthorizationUtils.isGerente(requestCom(funcionario(Cargo.GERENTE, 1L)))).isTrue();
        assertThat(AuthorizationUtils.isGerente(requestCom(funcionario(Cargo.SUPERVISOR, 1L)))).isFalse();
        assertThat(AuthorizationUtils.isGerente(requestCom(funcionario(Cargo.ATENDENTE, 1L)))).isFalse();
    }

    @Test
    void isGerente_falsoQuandoNaoHaUsuarioLogado() {
        assertThat(AuthorizationUtils.isGerente(requestCom(null))).isFalse();
    }

    @Test
    void isSupervisor_verdadeiroApenasParaSupervisor() {
        assertThat(AuthorizationUtils.isSupervisor(requestCom(funcionario(Cargo.SUPERVISOR, 1L)))).isTrue();
        assertThat(AuthorizationUtils.isSupervisor(requestCom(funcionario(Cargo.GERENTE, 1L)))).isFalse();
    }

    @Test
    void isGerenteOuSupervisor_aceitaAmbosERejeitaAtendente() {
        assertThat(AuthorizationUtils.isGerenteOuSupervisor(requestCom(funcionario(Cargo.GERENTE, 1L)))).isTrue();
        assertThat(AuthorizationUtils.isGerenteOuSupervisor(requestCom(funcionario(Cargo.SUPERVISOR, 1L)))).isTrue();
        assertThat(AuthorizationUtils.isGerenteOuSupervisor(requestCom(funcionario(Cargo.ATENDENTE, 1L)))).isFalse();
    }

    @Test
    void unidadeIdDoLogado_retornaIdDaUnidade() {
        assertThat(AuthorizationUtils.unidadeIdDoLogado(requestCom(funcionario(Cargo.SUPERVISOR, 7L)))).isEqualTo(7L);
    }

    @Test
    void unidadeIdDoLogado_retornaNullSemUnidade() {
        assertThat(AuthorizationUtils.unidadeIdDoLogado(requestCom(funcionario(Cargo.GERENTE, null)))).isNull();
    }

    @Test
    void podeGerenciarUnidade_gerenteGerenciaQualquerUnidade() {
        HttpServletRequest request = requestCom(funcionario(Cargo.GERENTE, 1L));

        assertThat(AuthorizationUtils.podeGerenciarUnidade(request, 99L)).isTrue();
    }

    @Test
    void podeGerenciarUnidade_supervisorGerenciaApenasAPropria() {
        HttpServletRequest request = requestCom(funcionario(Cargo.SUPERVISOR, 5L));

        assertThat(AuthorizationUtils.podeGerenciarUnidade(request, 5L)).isTrue();
        assertThat(AuthorizationUtils.podeGerenciarUnidade(request, 6L)).isFalse();
    }

    @Test
    void podeGerenciarUnidade_atendenteNuncaPode() {
        HttpServletRequest request = requestCom(funcionario(Cargo.ATENDENTE, 5L));

        assertThat(AuthorizationUtils.podeGerenciarUnidade(request, 5L)).isFalse();
    }
}
