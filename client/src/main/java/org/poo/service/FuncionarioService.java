package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.ui.ApiClient;

import java.util.Map;

public class FuncionarioService extends BaseService {

    public JsonNode listar() throws Exception {
        return paraJson(ApiClient.get("/funcionarios"));
    }

    public JsonNode buscarPorId(long id) throws Exception {
        return paraJson(ApiClient.get("/funcionarios/" + id));
    }

    public JsonNode criar(Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.post("/funcionarios", paraCorpo(dados)));
    }

    public JsonNode atualizar(long id, Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.patch("/funcionarios/" + id, paraCorpo(dados)));
    }

    public void deletar(long id) throws Exception {
        exigirSucesso(ApiClient.delete("/funcionarios/" + id));
    }
}
