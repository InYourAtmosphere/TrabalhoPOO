package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.service.ApiException;
import org.poo.service.ContratoService;
import org.poo.service.UnidadeService;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EncerrarContratoDialog extends JDialog {

    private final UnidadeService unidadeService = new UnidadeService();
    private final ContratoService contratoService = new ContratoService();

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

        Estilos.estilizarLabelErro(labelErro);
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);
        linha++;

        Estilos.estilizarBotaoPerigo(botaoConfirmar);
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoCancelar = new JButton("Cancelar");
        Estilos.estilizarBotaoSecundario(botaoCancelar);
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
                return unidadeService.listar();
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

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                contratoService.encerrar(contratoId, corpo);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    aoEncerrarComSucesso.run();
                    dispose();
                } catch (Exception ex) {
                    labelErro.setText(ApiException.isCausa(ex)
                            ? "Erro: " + ApiException.mensagemDe(ex)
                            : "Erro de conexão com o servidor.");
                    botaoConfirmar.setEnabled(true);
                }
            }
        }.execute();
    }
}
