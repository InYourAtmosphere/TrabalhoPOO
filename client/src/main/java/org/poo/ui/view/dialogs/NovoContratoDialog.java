package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NovoContratoDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JComboBox<String> comboCliente = new JComboBox<>();
    private final JComboBox<String> comboVeiculo = new JComboBox<>();
    private final JComboBox<String> comboUnidade = new JComboBox<>();
    private final JTextField campoDataFim = new JTextField(18);
    private final JTextField campoValorDiaria = new JTextField(18);
    private final JTextField campoKmInicial = new JTextField(18);
    private final JTextField campoFormaPagamento = new JTextField(18);
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final List<Long> idsCliente = new ArrayList<>();
    private final List<Long> idsVeiculo = new ArrayList<>();
    private final List<Long> idsUnidade = new ArrayList<>();

    private final Runnable aoSalvarComSucesso;

    public NovoContratoDialog(Window owner, Runnable aoSalvarComSucesso) {
        super(owner, "Novo Contrato", ModalityType.APPLICATION_MODAL);
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
        linha = adicionarCombo(painel, gbc, linha, "Cliente*:", comboCliente);
        linha = adicionarCombo(painel, gbc, linha, "Veículo*:", comboVeiculo);
        linha = adicionarCombo(painel, gbc, linha, "Unidade de retirada*:", comboUnidade);
        linha = adicionarCampo(painel, gbc, linha, "Data fim prevista* (dd/MM/yyyy HH:mm):", campoDataFim);
        linha = adicionarCampo(painel, gbc, linha, "Valor diária* (R$):", campoValorDiaria);
        linha = adicionarCampo(painel, gbc, linha, "KM inicial:", campoKmInicial);
        linha = adicionarCampo(painel, gbc, linha, "Forma de pagamento:", campoFormaPagamento);

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

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private int adicionarCombo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JComboBox<String> combo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(combo, gbc);
        return linha + 1;
    }

    private void carregarDados() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode[], Void>() {
            @Override
            protected JsonNode[] doInBackground() throws Exception {
                JsonNode clientes = MAPPER.readTree(ApiClient.get("/clientes").body());
                JsonNode veiculos = MAPPER.readTree(ApiClient.get("/veiculos").body());
                JsonNode unidades = MAPPER.readTree(ApiClient.get("/unidades").body());
                return new JsonNode[]{clientes, veiculos, unidades};
            }

            @Override
            protected void done() {
                try {
                    JsonNode[] resultado = get();

                    for (JsonNode c : resultado[0]) {
                        idsCliente.add(c.path("id").asLong());
                        comboCliente.addItem(c.path("nome").asText()
                                + " (" + c.path("documentoIdentidade").asText() + ")");
                    }

                    for (JsonNode v : resultado[1]) {
                        if ("DISPONIVEL".equals(v.path("status").asText())) {
                            idsVeiculo.add(v.path("id").asLong());
                            comboVeiculo.addItem(v.path("marca").asText() + " " + v.path("modelo").asText()
                                    + " — " + v.path("placa").asText());
                        }
                    }

                    for (JsonNode u : resultado[2]) {
                        idsUnidade.add(u.path("id").asLong());
                        comboUnidade.addItem(u.path("nomeUnidade").asText());
                    }

                    boolean pronto = !idsCliente.isEmpty() && !idsVeiculo.isEmpty() && !idsUnidade.isEmpty();
                    botaoSalvar.setEnabled(pronto);
                    if (idsVeiculo.isEmpty()) labelErro.setText("Nenhum veículo disponível para locação.");

                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar dados.");
                }
            }
        }.execute();
    }

    private void salvar() {
        LocalDateTime dataFim;
        try {
            dataFim = LocalDateTime.parse(campoDataFim.getText().trim(), FMT);
        } catch (DateTimeParseException ex) {
            labelErro.setText("Data inválida. Use o formato dd/MM/yyyy HH:mm.");
            return;
        }

        if (dataFim.isBefore(LocalDateTime.now())) {
            labelErro.setText("A data de fim prevista deve ser futura.");
            return;
        }

        double valorDiaria;
        try {
            valorDiaria = Double.parseDouble(campoValorDiaria.getText().trim().replace(",", "."));
            if (valorDiaria <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            labelErro.setText("Valor da diária deve ser um número maior que zero.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("clienteId", idsCliente.get(comboCliente.getSelectedIndex()));
        corpo.put("veiculoId", idsVeiculo.get(comboVeiculo.getSelectedIndex()));
        corpo.put("unidadeRetiradaId", idsUnidade.get(comboUnidade.getSelectedIndex()));
        corpo.put("dataFimPrevista", dataFim.toString());
        corpo.put("valorDiaria", valorDiaria);

        String kmText = campoKmInicial.getText().trim();
        if (!kmText.isEmpty()) {
            try { corpo.put("kmInicial", Double.parseDouble(kmText.replace(",", "."))); }
            catch (NumberFormatException ex) {
                labelErro.setText("KM inicial deve ser um número válido.");
                botaoSalvar.setEnabled(true);
                return;
            }
        }

        String formaPag = campoFormaPagamento.getText().trim();
        if (!formaPag.isEmpty()) corpo.put("formaPagamento", formaPag);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.post("/contratos", MAPPER.writeValueAsString(corpo));
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
