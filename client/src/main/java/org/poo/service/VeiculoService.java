package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.ui.ApiClient;

import java.util.Map;

public class VeiculoService extends BaseService {

    public JsonNode listar() throws Exception {
        return paraJson(ApiClient.get("/veiculos"));
    }

    public JsonNode listarPorUnidade(long unidadeId) throws Exception {
        return paraJson(ApiClient.get("/veiculos?unidadeId=" + unidadeId));
    }

    public JsonNode listarTodasUnidades() throws Exception {
        return paraJson(ApiClient.get("/veiculos?todasUnidades=true"));
    }

    public JsonNode buscarPorId(long id) throws Exception {
        return paraJson(ApiClient.get("/veiculos/" + id));
    }

    public JsonNode criar(String tipo, Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.post("/veiculos?tipo=" + tipo, paraCorpo(dados)));
    }

    public JsonNode atualizar(long id, Map<String, Object> dados) throws Exception {
        return paraJson(ApiClient.patch("/veiculos/" + id, paraCorpo(dados)));
    }

    public JsonNode atualizarStatus(long id, String status) throws Exception {
        return paraJson(ApiClient.patch("/veiculos/" + id + "/status", paraCorpo(Map.of("status", status))));
    }

    public void deletar(long id) throws Exception {
        exigirSucesso(ApiClient.delete("/veiculos/" + id));
    }
}
