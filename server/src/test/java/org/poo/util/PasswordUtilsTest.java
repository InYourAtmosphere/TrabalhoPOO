package org.poo.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilsTest {

    @Test
    void hashPassword_ehDeterministico() {
        assertThat(PasswordUtils.hashPassword("admin123"))
                .isEqualTo(PasswordUtils.hashPassword("admin123"));
    }

    @Test
    void hashPassword_geraSha256EmHexadecimal() {
        String hash = PasswordUtils.hashPassword("admin123");

        assertThat(hash).hasSize(64).matches("[0-9a-f]{64}");
    }

    @Test
    void hashPassword_senhasDiferentesGeramHashesDiferentes() {
        assertThat(PasswordUtils.hashPassword("admin123"))
                .isNotEqualTo(PasswordUtils.hashPassword("admin124"));
    }

    @Test
    void hashPassword_retornaNullParaSenhaNull() {
        assertThat(PasswordUtils.hashPassword(null)).isNull();
    }

    @Test
    void verifyPassword_aceitaSenhaCorreta() {
        String hash = PasswordUtils.hashPassword("segredo");

        assertThat(PasswordUtils.verifyPassword("segredo", hash)).isTrue();
    }

    @Test
    void verifyPassword_rejeitaSenhaIncorreta() {
        String hash = PasswordUtils.hashPassword("segredo");

        assertThat(PasswordUtils.verifyPassword("errada", hash)).isFalse();
    }

    @Test
    void verifyPassword_retornaFalseParaArgumentosNull() {
        assertThat(PasswordUtils.verifyPassword(null, "hash")).isFalse();
        assertThat(PasswordUtils.verifyPassword("senha", null)).isFalse();
    }
}
