package org.poo.ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8081";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public static ApiResponse get(String path) throws Exception {
        HttpRequest request = baseRequest(path).GET().build();
        return execute(request);
    }

    public static ApiResponse post(String path, String jsonBody) throws Exception {
        HttpRequest request = baseRequest(path)
                .POST(BodyPublishers.ofString(jsonBody))
                .build();
        return execute(request);
    }

    public static ApiResponse patch(String path, String jsonBody) throws Exception {
        HttpRequest request = baseRequest(path)
                .method("PATCH", BodyPublishers.ofString(jsonBody))
                .build();
        return execute(request);
    }

    public static ApiResponse delete(String path) throws Exception {
        HttpRequest request = baseRequest(path).DELETE().build();
        return execute(request);
    }

    private static HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30));

        String token = SessionContext.getInstance().getToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return builder;
    }

    private static ApiResponse execute(HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }

    public record ApiResponse(int status, String body) {
        public boolean isSuccess() {
            return status >= 200 && status < 300;
        }
    }
}
