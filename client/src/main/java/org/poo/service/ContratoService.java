package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.ui.ApiClient;

import java.util.Map;

public class ContratoService extends BaseService {

    public JsonNode listar() throws Exception {
        return paraJson(ApiClient.get("/contratos"));
    }

    public JsonNode criar(Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.post("/contratos", paraCorpo(dados)));
    }

    public JsonNode encerrar(long id, Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.patch("/contratos/" + id + "/encerrar", paraCorpo(dados)));
    }
}
