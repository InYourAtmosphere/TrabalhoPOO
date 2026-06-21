package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

abstract class BaseService {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected void exigirSucesso(ApiClient.ApiResponse resposta) throws ApiException {
        if (!resposta.isSuccess()) {
            throw new ApiException(resposta.status(), resposta.body());
        }
    }

    protected JsonNode paraJson(ApiClient.ApiResponse resposta) throws Exception {
        exigirSucesso(resposta);
        return MAPPER.readTree(resposta.body());
    }

    protected String paraCorpo(Object dados) throws Exception {
        return MAPPER.writeValueAsString(dados);
    }
}
