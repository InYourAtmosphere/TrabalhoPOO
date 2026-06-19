package org.poo.ui.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.model.dto.request.LoginDTO;
import org.poo.ui.ApiClient;
import org.poo.ui.SessionContext;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField campoUsername = new JTextField(20);
    private final JPasswordField campoSenha = new JPasswordField(20);
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoEntrar = new JButton("Entrar");

    public LoginFrame() {
        super("AlugaFácil — Login");
        configurarJanela();
        configurarComponentes();
        setVisible(true);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 260);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titulo = new JLabel("AlugaFácil", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        painel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        painel.add(campoUsername, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        painel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        painel.add(campoSenha, gbc);

        labelErro.setForeground(Color.RED);
        labelErro.setFont(labelErro.getFont().deriveFont(Font.PLAIN, 11f));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);

        gbc.gridy = 4;
        painel.add(botaoEntrar, gbc);

        botaoEntrar.addActionListener(e -> tentarLogin());
        campoSenha.addActionListener(e -> tentarLogin());

        add(painel);
    }

    private void tentarLogin() {
        String username = campoUsername.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (username.isBlank() || senha.isBlank()) {
            labelErro.setText("Preencha usuário e senha.");
            return;
        }

        botaoEntrar.setEnabled(false);
        labelErro.setText(" ");

        new SwingWorker<Boolean, Void>() {
            private String mensagemErro;

            @Override
            protected Boolean doInBackground() throws Exception {
                LoginDTO dto = new LoginDTO();
                dto.setUsername(username);
                dto.setPassword(senha);
                String body = new ObjectMapper().writeValueAsString(dto);
                ApiClient.ApiResponse response = ApiClient.post("/auth/login", body);

                if (response.isSuccess()) {
                    JsonNode json = new ObjectMapper().readTree(response.body());
                    SessionContext.getInstance().setToken(json.path("token").asText());
                    SessionContext.getInstance().setCargo(json.path("cargo").asText());
                    SessionContext.getInstance().setNomeUsuario(json.path("nome").asText());
                    return true;
                } else {
                    mensagemErro = "Usuário ou senha inválidos.";
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        new MainFrame();
                        dispose();
                    } else {
                        labelErro.setText(mensagemErro);
                        botaoEntrar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro de conexão com o servidor.");
                    botaoEntrar.setEnabled(true);
                }
            }
        }.execute();
    }

}
