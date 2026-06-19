package org.poo.model.dto.response;

public record LoginResponseDTO(String token, long expiraEm, String cargo, String nome) {}
