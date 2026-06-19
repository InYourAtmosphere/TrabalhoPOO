package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.view.dialogs.EncerrarContratoDialog;
import org.poo.ui.view.dialogs.NovoContratoDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ContratoPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnEncerrar = new JButton("Encerrar Contrato");

    public ContratoPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovoContrato = new JButton("Novo Contrato");
        btnEncerrar.setEnabled(false);
        toolbar.add(btnAtualizar);
        toolbar.add(btnNovoContrato);
        toolbar.addSeparator();
        toolbar.add(btnEncerrar);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Cliente", "Veículo", "Unidade Retirada", "Data Início", "Data Fim", "Valor Diária", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int row = tabela.getSelectedRow();
            boolean ativo = row >= 0 && "ATIVO".equals(tableModel.getValueAt(row, 7));
            btnEncerrar.setEnabled(ativo);
        });

        btnAtualizar.addActionListener(e -> carregarDados());
        btnNovoContrato.addActionListener(e ->
                new NovoContratoDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        btnEncerrar.addActionListener(e -> {
            long id = (long) tableModel.getValueAt(tabela.getSelectedRow(), 0);
            new EncerrarContratoDialog(SwingUtilities.getWindowAncestor(this), id, this::carregarDados).setVisible(true);
        });
        carregarDados();
    }

    private void carregarDados() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return MAPPER.readTree(ApiClient.get("/contratos").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode c : lista) {
                        String cliente = c.path("cliente").path("nome").asText("-");
                        String veiculo = c.path("veiculo").path("modelo").asText("-")
                                + " " + c.path("veiculo").path("placa").asText();
                        String unidade = c.path("unidadeRetirada").path("nomeUnidade").asText("-");
                        String dataInicio = c.path("dataInicio").asText("-").replace("T", " ");
                        String dataFim = c.path("dataFimPrevista").asText("-").replace("T", " ");
                        String valorDiaria = String.format("R$ %.2f", c.path("valorDiaria").asDouble());

                        tableModel.addRow(new Object[]{
                            c.path("id").asLong(),
                            cliente,
                            veiculo,
                            unidade,
                            dataInicio,
                            dataFim,
                            valorDiaria,
                            c.path("status").asText()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ContratoPanel.this,
                            "Erro ao carregar contratos: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
