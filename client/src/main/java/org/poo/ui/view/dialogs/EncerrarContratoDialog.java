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

public class EncerrarContratoDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long contratoId;

    private final JTextField campoKmFinal = new JTextField(18);
    private final JComboBox<String> comboUnidadeDevolucao = new JComboBox<>();
    private final List<Long> idsUnidade = new ArrayList<>();
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoConfirmar = new JButton("Encerrar Contrato");

    private final Runnable aoEncerrarComSucesso;

    public EncerrarContratoDialog(Window owner, long contratoId, Runnable aoEncerrarComSucesso) {
        super(owner, "Encerrar Contrato #" + contratoId, ModalityType.APPLICATION_MODAL);
        this.contratoId = contratoId;
        this.aoEncerrarComSucesso = aoEncerrarComSucesso;
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

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        JLabel aviso = new JLabel("O veículo voltará para DISPONÍVEL após o encerramento.");
        aviso.setFont(aviso.getFont().deriveFont(Font.ITALIC, 11f));
        painel.add(aviso, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("KM final (opcional):"), gbc);
        gbc.gridx = 1;
        painel.add(campoKmFinal, gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Unidade de devolução (opcional):"), gbc);
        gbc.gridx = 1;
        comboUnidadeDevolucao.addItem("— Não informar —");
        painel.add(comboUnidadeDevolucao, gbc);
        linha++;

        labelErro.setForeground(Color.RED);
        labelErro.setFont(labelErro.getFont().deriveFont(Font.PLAIN, 11f));
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);
        linha++;

        botaoConfirmar.setBackground(new Color(200, 60, 60));
        botaoConfirmar.setForeground(Color.WHITE);
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        botaoConfirmar.addActionListener(e -> encerrar());
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoConfirmar);
        gbc.gridy = linha;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private void carregarUnidades() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/unidades").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    for (JsonNode u : lista) {
                        idsUnidade.add(u.path("id").asLong());
                        comboUnidadeDevolucao.addItem(u.path("nomeUnidade").asText());
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar unidades.");
                }
            }
        }.execute();
    }

    private void encerrar() {
        Map<String, Object> corpo = new LinkedHashMap<>();

        String kmText = campoKmFinal.getText().trim();
        if (!kmText.isEmpty()) {
            try {
                corpo.put("kmFinal", Double.parseDouble(kmText.replace(",", ".")));
            } catch (NumberFormatException ex) {
                labelErro.setText("KM final deve ser um número válido.");
                return;
            }
        }

        // índice 0 é "— Não informar —", índices seguintes mapeiam para idsUnidade
        int idx = comboUnidadeDevolucao.getSelectedIndex();
        if (idx > 0) {
            corpo.put("unidadeDevolucaoId", idsUnidade.get(idx - 1));
        }

        botaoConfirmar.setEnabled(false);
        labelErro.setText(" ");

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.patch("/contratos/" + contratoId + "/encerrar",
                        MAPPER.writeValueAsString(corpo));
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        aoEncerrarComSucesso.run();
                        dispose();
                    } else {
                        labelErro.setText("Erro: " + resposta.body());
                        botaoConfirmar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro de conexão com o servidor.");
                    botaoConfirmar.setEnabled(true);
                }
            }
        }.execute();
    }
}
