package org.poo;

import java.sql.Connection;
import java.sql.SQLException;

import org.poo.config.DatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        Connection c;
        try {
            c = DatabaseConfig.getConnection();
            System.out.println("Conexão com o banco de dados estabelecida com sucesso! " + c.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SpringApplication.run(Main.class, args);
    }
}
