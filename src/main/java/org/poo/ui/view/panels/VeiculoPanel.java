package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VeiculoPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;

    public VeiculoPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAtualizar = new JButton("Atualizar");
        toolbar.add(btnAtualizar);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Placa", "Marca", "Modelo", "Ano", "KM", "Status", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnAtualizar.addActionListener(e -> carregarDados());
        carregarDados();
    }

    private void carregarDados() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/veiculos").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode v : lista) {
                        String tipo = v.has("cilindrada") ? "Motocicleta" : "Carro";
                        tableModel.addRow(new Object[]{
                            v.path("id").asLong(),
                            v.path("placa").asText(),
                            v.path("marca").asText(),
                            v.path("modelo").asText(),
                            v.path("ano").asInt(),
                            String.format("%.0f km", v.path("kmAtual").asDouble()),
                            v.path("status").asText(),
                            tipo
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VeiculoPanel.this,
                            "Erro ao carregar veículos: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
