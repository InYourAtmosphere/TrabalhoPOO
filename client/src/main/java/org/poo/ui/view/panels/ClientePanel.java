package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.view.dialogs.NovoClienteDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientePanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;

    public ClientePanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovoCliente = new JButton("Novo Cliente");
        toolbar.add(btnAtualizar);
        toolbar.add(btnNovoCliente);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Email", "Telefone", "Documento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnAtualizar.addActionListener(e -> carregarDados());
        btnNovoCliente.addActionListener(e ->
                new NovoClienteDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        carregarDados();
    }

    private void carregarDados() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/clientes").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode c : lista) {
                        tableModel.addRow(new Object[]{
                            c.path("id").asLong(),
                            c.path("nome").asText(),
                            c.path("email").asText(),
                            c.path("telefone").asText(),
                            c.path("documentoIdentidade").asText()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
                            "Erro ao carregar clientes: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
