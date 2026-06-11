package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UnidadePanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;

    public UnidadePanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAtualizar = new JButton("Atualizar");
        toolbar.add(btnAtualizar);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Cidade", "Estado", "CEP"};
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
                return MAPPER.readTree(ApiClient.get("/unidades").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode u : lista) {
                        JsonNode end = u.path("endereco");
                        tableModel.addRow(new Object[]{
                            u.path("id").asLong(),
                            u.path("nomeUnidade").asText(),
                            end.path("cidade").asText("-"),
                            end.path("estado").asText("-"),
                            end.path("cep").asText("-")
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UnidadePanel.this,
                            "Erro ao carregar unidades: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
