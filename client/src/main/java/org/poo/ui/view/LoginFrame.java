package org.poo.ui.view;

import org.poo.service.ApiException;
import org.poo.service.AuthService;
import org.poo.ui.Estilos;
import org.poo.ui.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

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
        setSize(440, 360);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Estilos.CINZA_FUNDO);
    }

    private void configurarComponentes() {
        setLayout(new GridBagLayout());

        JPanel cartao = Estilos.criarCartao();
        cartao.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JLabel titulo = new JLabel("AlugaFácil", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        titulo.setForeground(Estilos.AZUL_PRIMARIO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 4, 5);
        cartao.add(titulo, gbc);

        JLabel subtitulo = new JLabel("Acesse sua conta para continuar", SwingConstants.CENTER);
        subtitulo.setForeground(Estilos.CINZA_TEXTO);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5);
        cartao.add(subtitulo, gbc);

        campoUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.CINZA_BORDA, 1),
                new EmptyBorder(6, 8, 6, 8)));
        campoSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.CINZA_BORDA, 1),
                new EmptyBorder(6, 8, 6, 8)));

        gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 2, 5);
        gbc.gridy = 2; gbc.gridx = 0;
        cartao.add(new JLabel("Usuário"), gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 5, 14, 5);
        cartao.add(campoUsername, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 5, 2, 5);
        cartao.add(new JLabel("Senha"), gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 5, 6, 5);
        cartao.add(campoSenha, gbc);

        Estilos.estilizarLabelErro(labelErro);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 5, 10, 5);
        cartao.add(labelErro, gbc);

        Estilos.estilizarBotaoPrimario(botaoEntrar);
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 5, 0, 5);
        cartao.add(botaoEntrar, gbc);

        botaoEntrar.addActionListener(e -> tentarLogin());
        campoSenha.addActionListener(e -> tentarLogin());

        add(cartao);
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

        new SwingWorker<AuthService.LoginResult, Void>() {
            @Override
            protected AuthService.LoginResult doInBackground() throws Exception {
                return authService.login(username, senha);
            }

            @Override
            protected void done() {
                try {
                    AuthService.LoginResult resultado = get();
                    SessionContext.getInstance().setToken(resultado.token());
                    SessionContext.getInstance().setCargo(resultado.cargo());
                    SessionContext.getInstance().setNomeUsuario(resultado.nome());
                    new MainFrame();
                    dispose();
                } catch (Exception ex) {
                    labelErro.setText(ApiException.isCausa(ex)
                            ? "Usuário ou senha inválidos."
                            : "Erro de conexão com o servidor.");
                    botaoEntrar.setEnabled(true);
                }
            }
        }.execute();
    }

}
