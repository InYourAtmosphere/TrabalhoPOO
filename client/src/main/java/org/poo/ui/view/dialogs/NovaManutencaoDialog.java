package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NovaManutencaoDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String[] TIPOS = {"PREVENTIVA", "CORRETIVA", "PREDITIVA"};

    private final JComboBox<String> comboVeiculo = new JComboBox<>();
    private final JComboBox<String> comboTipo = new JComboBox<>(TIPOS);
    private final JTextArea campoDescricao = new JTextArea(3, 22);
    private final JTextField campoCusto = new JTextField(22);
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final List<Long> idsVeiculo = new ArrayList<>();
    private final Runnable aoSalvarComSucesso;

    public NovaManutencaoDialog(Window owner, Runnable aoSalvarComSucesso) {
        super(owner, "Registrar Manutenção", ModalityType.APPLICATION_MODAL);
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        configurarComponentes();
        pack();
        setLocationRelativeTo(owner);
        carregarVeiculos();
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        int linha = 0;

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel("Veículo*:"), gbc);
        gbc.gridx = 1;
        painel.add(comboVeiculo, gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Tipo*:"), gbc);
        gbc.gridx = 1;
        painel.add(comboTipo, gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Descrição*:"), gbc);
        gbc.gridx = 1;
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        painel.add(new JScrollPane(campoDescricao), gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Custo* (R$):"), gbc);
        gbc.gridx = 1;
        painel.add(campoCusto, gbc);
        linha++;

        Estilos.estilizarLabelErro(labelErro);
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);
        linha++;

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Estilos.estilizarBotaoPrimario(botaoSalvar);
        JButton botaoCancelar = new JButton("Cancelar");
        Estilos.estilizarBotaoSecundario(botaoCancelar);
        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);
        gbc.gridy = linha;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private void carregarVeiculos() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/veiculos").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    for (JsonNode v : lista) {
                        idsVeiculo.add(v.path("id").asLong());
                        comboVeiculo.addItem(v.path("marca").asText() + " " + v.path("modelo").asText()
                                + " — " + v.path("placa").asText()
                                + " [" + v.path("status").asText() + "]");
                    }
                    botaoSalvar.setEnabled(!idsVeiculo.isEmpty());
                    if (idsVeiculo.isEmpty()) labelErro.setText("Nenhum veículo cadastrado.");
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar veículos.");
                }
            }
        }.execute();
    }

    private void salvar() {
        String descricao = campoDescricao.getText().trim();
        if (descricao.isBlank()) {
            labelErro.setText("Descrição é obrigatória.");
            return;
        }

        double custo;
        try {
            custo = Double.parseDouble(campoCusto.getText().trim().replace(",", "."));
            if (custo < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            labelErro.setText("Custo deve ser um número maior ou igual a zero.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("veiculoId", idsVeiculo.get(comboVeiculo.getSelectedIndex()));
        corpo.put("tipo", comboTipo.getSelectedItem());
        corpo.put("descricao", descricao);
        corpo.put("custo", custo);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.post("/manutencoes", MAPPER.writeValueAsString(corpo));
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
}
