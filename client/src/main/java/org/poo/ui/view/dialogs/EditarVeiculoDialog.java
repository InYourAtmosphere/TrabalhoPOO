package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditarVeiculoDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long veiculoId;

    private final JTextField campoMarca = new JTextField(18);
    private final JTextField campoModelo = new JTextField(18);
    private final JTextField campoAno = new JTextField(18);
    private final JTextField campoPlaca = new JTextField(18);
    private final JTextField campoKmAtual = new JTextField(18);

    // Campos carro
    private final JTextField campoQtdPortas = new JTextField(18);
    private final JCheckBox checkArCondicionado = new JCheckBox("Possui ar-condicionado");

    // Campos moto
    private final JTextField campoCilindrada = new JTextField(18);
    private final JCheckBox checkBau = new JCheckBox("Possui baú");

    private final JPanel painelTipoEspecifico = new JPanel(new GridBagLayout());
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private boolean isMoto = false;
    private final Runnable aoSalvarComSucesso;

    public EditarVeiculoDialog(Window owner, long veiculoId, Runnable aoSalvarComSucesso) {
        super(owner, "Editar Veículo", ModalityType.APPLICATION_MODAL);
        this.veiculoId = veiculoId;
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
        linha = adicionarCampo(painel, gbc, linha, "Marca*:", campoMarca);
        linha = adicionarCampo(painel, gbc, linha, "Modelo*:", campoModelo);
        linha = adicionarCampo(painel, gbc, linha, "Ano*:", campoAno);
        linha = adicionarCampo(painel, gbc, linha, "Placa*:", campoPlaca);
        linha = adicionarCampo(painel, gbc, linha, "KM atual:", campoKmAtual);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(painelTipoEspecifico, gbc);
        linha++;

        Estilos.estilizarLabelErro(labelErro);
        gbc.gridy = linha;
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

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private void configurarCamposTipoCarro() {
        painelTipoEspecifico.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0; gbc.gridy = 0;
        painelTipoEspecifico.add(new JLabel("Qtd. de portas:"), gbc);
        gbc.gridx = 1;
        painelTipoEspecifico.add(campoQtdPortas, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        painelTipoEspecifico.add(checkArCondicionado, gbc);
    }

    private void configurarCamposTipoMoto() {
        painelTipoEspecifico.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0; gbc.gridy = 0;
        painelTipoEspecifico.add(new JLabel("Cilindrada:"), gbc);
        gbc.gridx = 1;
        painelTipoEspecifico.add(campoCilindrada, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        painelTipoEspecifico.add(checkBau, gbc);
    }

    private void carregarDados() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/veiculos/" + veiculoId).body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode v = get();
                    campoMarca.setText(v.path("marca").asText());
                    campoModelo.setText(v.path("modelo").asText());
                    campoAno.setText(v.path("ano").asText());
                    campoPlaca.setText(v.path("placa").asText());
                    campoKmAtual.setText(v.path("kmAtual").asText("0"));

                    isMoto = v.has("cilindrada");
                    if (isMoto) {
                        configurarCamposTipoMoto();
                        campoCilindrada.setText(v.path("cilindrada").asText("0"));
                        checkBau.setSelected(v.path("temBau").asBoolean());
                    } else {
                        configurarCamposTipoCarro();
                        campoQtdPortas.setText(v.path("quantidadePortas").asText("0"));
                        checkArCondicionado.setSelected(v.path("temArCondicionado").asBoolean());
                    }

                    pack();
                    botaoSalvar.setEnabled(true);
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar dados do veículo.");
                }
            }
        }.execute();
    }

    private void salvar() {
        String marca = campoMarca.getText().trim();
        String modelo = campoModelo.getText().trim();
        String ano = campoAno.getText().trim();
        String placa = campoPlaca.getText().trim();

        if (marca.isBlank() || modelo.isBlank() || ano.isBlank() || placa.isBlank()) {
            labelErro.setText("Marca, modelo, ano e placa são obrigatórios.");
            return;
        }

        int anoNumero;
        double kmNumero;
        try {
            anoNumero = Integer.parseInt(ano);
            kmNumero = campoKmAtual.getText().isBlank() ? 0 : Double.parseDouble(campoKmAtual.getText().trim());
        } catch (NumberFormatException ex) {
            labelErro.setText("Ano e KM atual devem ser números válidos.");
            return;
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("marca", marca);
        corpo.put("modelo", modelo);
        corpo.put("ano", anoNumero);
        corpo.put("placa", placa);
        corpo.put("kmAtual", kmNumero);

        if (isMoto) {
            try {
                corpo.put("cilindrada", campoCilindrada.getText().isBlank() ? 0 : Integer.parseInt(campoCilindrada.getText().trim()));
            } catch (NumberFormatException ex) {
                labelErro.setText("Cilindrada deve ser um número válido.");
                return;
            }
            corpo.put("temBau", checkBau.isSelected());
        } else {
            try {
                corpo.put("quantidadePortas", campoQtdPortas.getText().isBlank() ? 0 : Integer.parseInt(campoQtdPortas.getText().trim()));
            } catch (NumberFormatException ex) {
                labelErro.setText("Quantidade de portas deve ser um número válido.");
                return;
            }
            corpo.put("temArCondicionado", checkArCondicionado.isSelected());
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                String json = MAPPER.writeValueAsString(corpo);
                return ApiClient.patch("/veiculos/" + veiculoId, json);
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
