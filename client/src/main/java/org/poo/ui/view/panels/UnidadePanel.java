package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;
import org.poo.ui.view.dialogs.NovaUnidadeDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UnidadePanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnExcluir = new JButton("Excluir");

    public UnidadePanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Estilos.estilizarToolbar(toolbar);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovaUnidade = new JButton("Nova Unidade");
        Estilos.estilizarBotaoSecundario(btnAtualizar);
        Estilos.estilizarBotaoPrimario(btnNovaUnidade);
        Estilos.estilizarBotaoPerigo(btnExcluir);
        btnExcluir.setEnabled(false);
        toolbar.add(btnAtualizar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnNovaUnidade);
        toolbar.addSeparator();
        toolbar.add(btnExcluir);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Cidade", "Estado", "CEP"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Estilos.estilizarTabela(tabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.getSelectionModel().addListSelectionListener(e ->
                btnExcluir.setEnabled(tabela.getSelectedRow() >= 0));

        btnAtualizar.addActionListener(e -> carregarDados());
        btnNovaUnidade.addActionListener(e ->
                new NovaUnidadeDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        btnExcluir.addActionListener(e -> excluirSelecionado());
        carregarDados();
    }

    private void excluirSelecionado() {
        int row = tabela.getSelectedRow();
        String nome = (String) tableModel.getValueAt(row, 1);
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Excluir a unidade \"" + nome + "\"?\nVeículos e funcionários vinculados perderão o vínculo.",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        long id = (long) tableModel.getValueAt(row, 0);
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.delete("/unidades/" + id);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        carregarDados();
                    } else {
                        JOptionPane.showMessageDialog(UnidadePanel.this,
                                "Erro ao excluir: " + resposta.body(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UnidadePanel.this,
                            "Erro de conexão com o servidor.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
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
