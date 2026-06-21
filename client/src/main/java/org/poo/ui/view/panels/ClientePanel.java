package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;
import org.poo.ui.SessionContext;
import org.poo.ui.util.ExportUtils;
import org.poo.ui.view.dialogs.EditarClienteDialog;
import org.poo.ui.view.dialogs.NovoClienteDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientePanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnExcluir = new JButton("Excluir");

    public ClientePanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Estilos.estilizarToolbar(toolbar);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovoCliente = new JButton("Novo Cliente");
        Estilos.estilizarBotaoSecundario(btnAtualizar);
        Estilos.estilizarBotaoPrimario(btnNovoCliente);
        toolbar.add(btnAtualizar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnNovoCliente);

        boolean gerente = SessionContext.getInstance().isGerente();
        if (gerente) {
            Estilos.estilizarBotaoSecundario(btnEditar);
            Estilos.estilizarBotaoPerigo(btnExcluir);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
            toolbar.addSeparator();
            toolbar.add(btnEditar);
            toolbar.add(Box.createHorizontalStrut(8));
            toolbar.add(btnExcluir);
        }
        toolbar.add(Box.createHorizontalGlue());
        JButton btnExportarCSV = new JButton("Exportar CSV");
        Estilos.estilizarBotaoSecundario(btnExportarCSV);
        toolbar.add(btnExportarCSV);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Email", "Telefone", "Documento"};
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
        btnNovoCliente.addActionListener(e ->
                new NovoClienteDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        btnExportarCSV.addActionListener(e -> ExportUtils.exportarCSV(this, "clientes", tableModel));
        carregarDados();
    }

    private long idSelecionado() {
        int row = tabela.getSelectedRow();
        return (long) tableModel.getValueAt(row, 0);
    }

    private void abrirEdicao() {
        new EditarClienteDialog(SwingUtilities.getWindowAncestor(this), idSelecionado(), this::carregarDados)
                .setVisible(true);
    }

    private void excluirSelecionado() {
        int row = tabela.getSelectedRow();
        String nome = (String) tableModel.getValueAt(row, 1);
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Excluir o cliente \"" + nome + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        long id = idSelecionado();
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.delete("/clientes/" + id);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        carregarDados();
                    } else {
                        JOptionPane.showMessageDialog(ClientePanel.this,
                                "Erro ao excluir: " + resposta.body(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientePanel.this,
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
