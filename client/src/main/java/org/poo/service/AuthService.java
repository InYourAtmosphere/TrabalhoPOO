package org.poo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.model.dto.request.LoginDTO;
import org.poo.ui.ApiClient;

public class AuthService extends BaseService {

    public LoginResult login(String username, String password) throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        JsonNode json = paraJson(ApiClient.post("/auth/login", paraCorpo(dto)));
        return new LoginResult(json.path("token").asText(), json.path("cargo").asText(), json.path("nome").asText());
    }

    public void logout() throws Exception {
        ApiClient.delete("/auth/logout");
    }

    public record LoginResult(String token, String cargo, String nome) {}
}
