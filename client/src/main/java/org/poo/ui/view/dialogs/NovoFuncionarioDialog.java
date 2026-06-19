package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NovoFuncionarioDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoTelefone = new JTextField(22);
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoMatricula = new JTextField(22);
    private final JTextField campoUsername = new JTextField(22);
    private final JPasswordField campoPassword = new JPasswordField(22);
    private final JComboBox<String> comboCargo = new JComboBox<>(new String[]{"ATENDENTE", "GERENTE"});
    private final JComboBox<String> comboUnidade = new JComboBox<>();
    private final List<Long> idsUnidade = new ArrayList<>();
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final Runnable aoSalvarComSucesso;

    public NovoFuncionarioDialog(Window owner, Runnable aoSalvarComSucesso) {
        super(owner, "Novo Funcionário", ModalityType.APPLICATION_MODAL);
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        configurarComponentes();
        pack();
        setLocationRelativeTo(owner);
        carregarUnidades();
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        int linha = 0;
        linha = adicionarCampo(painel, gbc, linha, "Nome*:", campoNome);
        linha = adicionarCampo(painel, gbc, linha, "Telefone:", campoTelefone);
        linha = adicionarCampo(painel, gbc, linha, "Email:", campoEmail);
        linha = adicionarCampo(painel, gbc, linha, "Matrícula*:", campoMatricula);
        linha = adicionarCampo(painel, gbc, linha, "Username*:", campoUsername);
        linha = adicionarCampo(painel, gbc, linha, "Senha:", campoPassword);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1;
        painel.add(comboCargo, gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Unidade*:"), gbc);
        gbc.gridx = 1;
        painel.add(comboUnidade, gbc);
        linha++;

        labelErro.setForeground(Color.RED);
        labelErro.setFont(labelErro.getFont().deriveFont(Font.PLAIN, 11f));
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);
        linha++;

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);
        gbc.gridy = linha;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private void carregarUnidades() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/unidades").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    comboUnidade.removeAllItems();
                    idsUnidade.clear();
                    for (JsonNode u : lista) {
                        idsUnidade.add(u.path("id").asLong());
                        comboUnidade.addItem(u.path("nomeUnidade").asText());
                    }
                    botaoSalvar.setEnabled(comboUnidade.getItemCount() > 0);
                    if (comboUnidade.getItemCount() == 0) {
                        labelErro.setText("Nenhuma unidade cadastrada.");
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar unidades.");
                }
            }
        }.execute();
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String matricula = campoMatricula.getText().trim();
        String username = campoUsername.getText().trim();

        if (nome.isBlank() || matricula.isBlank() || username.isBlank()) {
            labelErro.setText("Nome, matrícula e username são obrigatórios.");
            return;
        }

        if (idsUnidade.isEmpty()) {
            labelErro.setText("Selecione uma unidade.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        long unidadeId = idsUnidade.get(comboUnidade.getSelectedIndex());
        String senha = new String(campoPassword.getPassword()).trim();

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("telefone", valorOuNulo(campoTelefone));
        corpo.put("email", valorOuNulo(campoEmail));
        corpo.put("matricula", matricula);
        corpo.put("username", username);
        corpo.put("cargo", comboCargo.getSelectedItem());
        corpo.put("unidadeId", unidadeId);
        if (!senha.isEmpty()) corpo.put("password", senha);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.post("/funcionarios", MAPPER.writeValueAsString(corpo));
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        aoSalvarComSucesso.run();
                        dispose();
                    } else {
                        labelErro.setText("Erro: " + resposta.body());
                        botaoSalvar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro de conexão com o servidor.");
                    botaoSalvar.setEnabled(true);
                }
            }
        }.execute();
    }

    private String valorOuNulo(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }
}
