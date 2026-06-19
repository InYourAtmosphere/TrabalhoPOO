package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditarClienteDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long clienteId;

    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoTelefone = new JTextField(22);
    private final JTextField campoDocumentoIdentidade = new JTextField(22);
    private final JTextField campoDocumentoHabilitacao = new JTextField(22);
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final Runnable aoSalvarComSucesso;

    public EditarClienteDialog(Window owner, long clienteId, Runnable aoSalvarComSucesso) {
        super(owner, "Editar Cliente", ModalityType.APPLICATION_MODAL);
        this.clienteId = clienteId;
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        configurarComponentes();
        pack();
        setLocationRelativeTo(owner);
        carregarDados();
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
        linha = adicionarCampo(painel, gbc, linha, "Documento de identidade*:", campoDocumentoIdentidade);
        linha = adicionarCampo(painel, gbc, linha, "Documento de habilitação:", campoDocumentoHabilitacao);

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
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private void carregarDados() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/clientes/" + clienteId).body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode c = get();
                    campoNome.setText(c.path("nome").asText());
                    campoTelefone.setText(c.path("telefone").asText(""));
                    campoDocumentoIdentidade.setText(c.path("documentoIdentidade").asText());
                    campoDocumentoHabilitacao.setText(c.path("documentoHabilitacao").asText(""));
                    botaoSalvar.setEnabled(true);
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar dados do cliente.");
                }
            }
        }.execute();
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String documentoIdentidade = campoDocumentoIdentidade.getText().trim();

        if (nome.isBlank() || documentoIdentidade.isBlank()) {
            labelErro.setText("Nome e documento de identidade são obrigatórios.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("telefone", valorOuNulo(campoTelefone));
        corpo.put("documentoIdentidade", documentoIdentidade);
        corpo.put("documentoHabilitacao", valorOuNulo(campoDocumentoHabilitacao));

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                String json = MAPPER.writeValueAsString(corpo);
                return ApiClient.patch("/clientes/" + clienteId, json);
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
