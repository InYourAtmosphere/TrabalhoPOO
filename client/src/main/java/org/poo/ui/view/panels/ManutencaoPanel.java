package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.view.dialogs.NovaManutencaoDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManutencaoPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;

    public ManutencaoPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovaManutencao = new JButton("Registrar Manutenção");
        toolbar.add(btnAtualizar);
        toolbar.add(btnNovaManutencao);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Veículo", "Tipo", "Descrição", "Data Início", "Custo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnAtualizar.addActionListener(e -> carregarDados());
        btnNovaManutencao.addActionListener(e ->
                new NovaManutencaoDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        carregarDados();
    }

    private void carregarDados() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/manutencoes").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode m : lista) {
                        String veiculo = m.path("veiculo").path("modelo").asText("-")
                                + " (" + m.path("veiculo").path("placa").asText("-") + ")";
                        String dataInicio = m.path("dataInicio").asText("-").replace("T", " ");
                        String custo = String.format("R$ %.2f", m.path("custo").asDouble());

                        tableModel.addRow(new Object[]{
                            m.path("id").asLong(),
                            veiculo,
                            m.path("tipo").asText(),
                            m.path("descricao").asText(),
                            dataInicio,
                            custo
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManutencaoPanel.this,
                            "Erro ao carregar manutenções: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
