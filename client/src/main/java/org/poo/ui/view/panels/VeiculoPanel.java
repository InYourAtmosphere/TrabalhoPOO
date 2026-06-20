package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;
import org.poo.ui.SessionContext;
import org.poo.ui.view.dialogs.EditarVeiculoDialog;
import org.poo.ui.view.dialogs.NovoVeiculoDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VeiculoPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnExcluir = new JButton("Excluir");

    public VeiculoPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Estilos.estilizarToolbar(toolbar);
        JButton btnAtualizar = new JButton("Atualizar");
        Estilos.estilizarBotaoSecundario(btnAtualizar);
        toolbar.add(btnAtualizar);

        boolean gerente = SessionContext.getInstance().isGerente();
        if (gerente) {
            JButton btnNovoVeiculo = new JButton("Novo Veículo");
            Estilos.estilizarBotaoPrimario(btnNovoVeiculo);
            Estilos.estilizarBotaoSecundario(btnEditar);
            Estilos.estilizarBotaoPerigo(btnExcluir);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
            toolbar.add(Box.createHorizontalStrut(8));
            toolbar.add(btnNovoVeiculo);
            toolbar.addSeparator();
            toolbar.add(btnEditar);
            toolbar.add(Box.createHorizontalStrut(8));
            toolbar.add(btnExcluir);
            btnNovoVeiculo.addActionListener(e ->
                    new NovoVeiculoDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        }

        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Placa", "Marca", "Modelo", "Ano", "KM", "Status", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Estilos.estilizarTabela(tabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        if (gerente) {
            tabela.getSelectionModel().addListSelectionListener(e -> {
                boolean selecionado = tabela.getSelectedRow() >= 0;
                btnEditar.setEnabled(selecionado);
                btnExcluir.setEnabled(selecionado);
            });
            btnEditar.addActionListener(e -> abrirEdicao());
            btnExcluir.addActionListener(e -> excluirSelecionado());
        }

        btnAtualizar.addActionListener(e -> carregarDados());
        carregarDados();
    }

    private long idSelecionado() {
        int row = tabela.getSelectedRow();
        return (long) tableModel.getValueAt(row, 0);
    }

    private void abrirEdicao() {
        new EditarVeiculoDialog(SwingUtilities.getWindowAncestor(this), idSelecionado(), this::carregarDados)
                .setVisible(true);
    }

    private void excluirSelecionado() {
        int row = tabela.getSelectedRow();
        String descricao = tableModel.getValueAt(row, 2) + " " + tableModel.getValueAt(row, 3)
                + " (" + tableModel.getValueAt(row, 1) + ")";
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Excluir o veículo \"" + descricao + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        long id = idSelecionado();
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.delete("/veiculos/" + id);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        carregarDados();
                    } else {
                        JOptionPane.showMessageDialog(VeiculoPanel.this,
                                "Erro ao excluir: " + resposta.body(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VeiculoPanel.this,
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
