package org.poo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.poo.config.DatabaseConfig;
import org.poo.ui.view.LoginFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        Connection c;
        try {
            c = DatabaseConfig.getConnection();
            DatabaseConfig.initializeDatabase();
            System.out.println("Conexão com o banco de dados estabelecida com sucesso! " + c.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            SpringApplication app = new SpringApplication(Main.class);
            app.setHeadless(false);
            app.run(args);
        }).start();

        aguardarServidor();

        SwingUtilities.invokeLater(LoginFrame::new);
    }

    private static void aguardarServidor() {
        System.out.println("Aguardando servidor iniciar...");
        for (int tentativa = 0; tentativa < 30; tentativa++) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8081/auth/login").openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(500);
                conn.connect();
                conn.disconnect();
                System.out.println("Servidor pronto.");
                return;
            } catch (Exception ignored) {
                try { Thread.sleep(500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        System.out.println("Servidor não respondeu a tempo — abrindo interface mesmo assim.");
    }
}
