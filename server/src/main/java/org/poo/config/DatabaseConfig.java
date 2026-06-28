package org.poo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DatabaseConfig {
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String URL = "jdbc:postgresql://" + HOST + ":5432/trabalhopoo";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Erro ao se conectar ao banco de dados: ", e);
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("schema.sql")) {
            
            if (is == null) {
                System.err.println("Arquivo schema.sql não encontrado nos resources.");
                return;
            }

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            stmt.execute(sql);
            System.out.println("Banco de dados inicializado com sucesso.");
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}
