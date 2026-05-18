package org.poo.model;

public record UpdateClienteDTO(
    String nome,
    String documentoIdentidade,
    String cnh,
    String telefone
) {}