package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.ui.ApiClient;

import java.util.Map;

public class UnidadeService extends BaseService {

    public JsonNode listar() throws Exception {
        return paraJson(ApiClient.get("/unidades"));
    }

    public JsonNode criar(Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.post("/unidades", paraCorpo(dados)));
    }

    public void deletar(long id) throws Exception {
        exigirSucesso(ApiClient.delete("/unidades/" + id));
    }
}
