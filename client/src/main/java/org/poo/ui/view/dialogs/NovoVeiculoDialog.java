package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.service.ApiException;
import org.poo.service.UnidadeService;
import org.poo.service.VeiculoService;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NovoVeiculoDialog extends JDialog {

    private static final String CARRO = "Carro Popular";
    private static final String MOTO = "Motocicleta";

    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{CARRO, MOTO});
    private final JTextField campoMarca = new JTextField(18);
    private final JTextField campoModelo = new JTextField(18);
    private final JTextField campoAno = new JTextField(18);
    private final JTextField campoPlaca = new JTextField(18);
    private final JTextField campoChassi = new JTextField(18);
    private final JTextField campoKmAtual = new JTextField(18);

    private final JTextField campoQtdPortas = new JTextField(18);
    private final JCheckBox checkArCondicionado = new JCheckBox("Possui ar-condicionado");

    private final JTextField campoCilindrada = new JTextField(18);
    private final JCheckBox checkBau = new JCheckBox("Possui baú");

    private final CardLayout cardLayoutTipo = new CardLayout();
    private final JPanel painelCamposTipo = new JPanel(cardLayoutTipo);

    private final JComboBox<String> comboUnidade = new JComboBox<>();
    private final List<Long> idsUnidade = new ArrayList<>();

    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final UnidadeService unidadeService = new UnidadeService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final Runnable aoSalvarComSucesso;

    public NovoVeiculoDialog(Window owner, Runnable aoSalvarComSucesso) {
        super(owner, "Novo Veículo", ModalityType.APPLICATION_MODAL);
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        campoKmAtual.setText("0");
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
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        painel.add(comboTipo, gbc);
        linha++;

        linha = adicionarCampo(painel, gbc, linha, "Marca*:", campoMarca);
        linha = adicionarCampo(painel, gbc, linha, "Modelo*:", campoModelo);
        linha = adicionarCampo(painel, gbc, linha, "Ano*:", campoAno);
        linha = adicionarCampo(painel, gbc, linha, "Placa*:", campoPlaca);
        linha = adicionarCampo(painel, gbc, linha, "Chassi*:", campoChassi);
        linha = adicionarCampo(painel, gbc, linha, "KM atual:", campoKmAtual);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel("Unidade*:"), gbc);
        gbc.gridx = 1;
        painel.add(comboUnidade, gbc);
        linha++;

        JPanel painelCarro = new JPanel(new GridBagLayout());
        GridBagConstraints gbcCarro = new GridBagConstraints();
        gbcCarro.fill = GridBagConstraints.HORIZONTAL;
        gbcCarro.insets = new Insets(4, 4, 4, 4);
        gbcCarro.gridx = 0; gbcCarro.gridy = 0;
        painelCarro.add(new JLabel("Quantidade de portas:"), gbcCarro);
        gbcCarro.gridx = 1;
        painelCarro.add(campoQtdPortas, gbcCarro);
        gbcCarro.gridx = 0; gbcCarro.gridy = 1; gbcCarro.gridwidth = 2;
        painelCarro.add(checkArCondicionado, gbcCarro);

        JPanel painelMoto = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMoto = new GridBagConstraints();
        gbcMoto.fill = GridBagConstraints.HORIZONTAL;
        gbcMoto.insets = new Insets(4, 4, 4, 4);
        gbcMoto.gridx = 0; gbcMoto.gridy = 0;
        painelMoto.add(new JLabel("Cilindrada:"), gbcMoto);
        gbcMoto.gridx = 1;
        painelMoto.add(campoCilindrada, gbcMoto);
        gbcMoto.gridx = 0; gbcMoto.gridy = 1; gbcMoto.gridwidth = 2;
        painelMoto.add(checkBau, gbcMoto);

        painelCamposTipo.add(painelCarro, CARRO);
        painelCamposTipo.add(painelMoto, MOTO);
        comboTipo.addActionListener(e -> cardLayoutTipo.show(painelCamposTipo, (String) comboTipo.getSelectedItem()));

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(painelCamposTipo, gbc);
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

    private void carregarUnidades() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return unidadeService.listar();
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
        String marca = campoMarca.getText().trim();
        String modelo = campoModelo.getText().trim();
        String ano = campoAno.getText().trim();
        String placa = campoPlaca.getText().trim();
        String chassi = campoChassi.getText().trim();

        if (marca.isBlank() || modelo.isBlank() || ano.isBlank() || placa.isBlank() || chassi.isBlank()) {
            labelErro.setText("Marca, modelo, ano, placa e chassi são obrigatórios.");
            return;
        }

        if (idsUnidade.isEmpty()) {
            labelErro.setText("Selecione uma unidade.");
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

        boolean isCarro = CARRO.equals(comboTipo.getSelectedItem());
        String tipoParam = isCarro ? "carro" : "moto";

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("marca", marca);
        corpo.put("modelo", modelo);
        corpo.put("ano", anoNumero);
        corpo.put("placa", placa);
        corpo.put("chassi", chassi);
        corpo.put("kmAtual", kmNumero);
        corpo.put("unidadeId", idsUnidade.get(comboUnidade.getSelectedIndex()));

        if (isCarro) {
            try {
                corpo.put("quantidadePortas", campoQtdPortas.getText().isBlank() ? 0 : Integer.parseInt(campoQtdPortas.getText().trim()));
            } catch (NumberFormatException ex) {
                labelErro.setText("Quantidade de portas deve ser um número válido.");
                return;
            }
            corpo.put("temArCondicionado", checkArCondicionado.isSelected());
        } else {
            try {
                corpo.put("cilindrada", campoCilindrada.getText().isBlank() ? 0 : Integer.parseInt(campoCilindrada.getText().trim()));
            } catch (NumberFormatException ex) {
                labelErro.setText("Cilindrada deve ser um número válido.");
                return;
            }
            corpo.put("temBau", checkBau.isSelected());
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                veiculoService.criar(tipoParam, corpo);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    aoSalvarComSucesso.run();
                    dispose();
                } catch (Exception ex) {
                    labelErro.setText(ApiException.isCausa(ex)
                            ? "Erro ao salvar: " + ApiException.mensagemDe(ex)
                            : "Erro de conexão com o servidor.");
                    botaoSalvar.setEnabled(true);
                }
            }
        }.execute();
    }
}
