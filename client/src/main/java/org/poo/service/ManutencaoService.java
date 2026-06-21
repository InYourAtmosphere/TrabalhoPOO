package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.ui.ApiClient;

import java.util.Map;

public class ManutencaoService extends BaseService {

    public JsonNode listar() throws Exception {
        return paraJson(ApiClient.get("/manutencoes"));
    }

    public JsonNode criar(Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.post("/manutencoes", paraCorpo(dados)));
    }
}
