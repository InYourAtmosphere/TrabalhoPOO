package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;
import org.poo.ui.view.dialogs.EditarFuncionarioDialog;
import org.poo.ui.view.dialogs.NovoFuncionarioDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FuncionarioPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnExcluir = new JButton("Excluir");

    public FuncionarioPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Estilos.estilizarToolbar(toolbar);
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovoFuncionario = new JButton("Novo Funcionário");
        Estilos.estilizarBotaoSecundario(btnAtualizar);
        Estilos.estilizarBotaoPrimario(btnNovoFuncionario);
        Estilos.estilizarBotaoSecundario(btnEditar);
        Estilos.estilizarBotaoPerigo(btnExcluir);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        toolbar.add(btnAtualizar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnNovoFuncionario);
        toolbar.addSeparator();
        toolbar.add(btnEditar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(btnExcluir);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Username", "Cargo", "Unidade"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Estilos.estilizarTabela(tabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabela.getSelectedRow() >= 0;
            btnEditar.setEnabled(selecionado);
            btnExcluir.setEnabled(selecionado);
        });

        btnAtualizar.addActionListener(e -> carregarDados());
        btnNovoFuncionario.addActionListener(e ->
                new NovoFuncionarioDialog(SwingUtilities.getWindowAncestor(this), this::carregarDados).setVisible(true));
        btnEditar.addActionListener(e -> abrirEdicao());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        carregarDados();
    }

    private long idSelecionado() {
        return (long) tableModel.getValueAt(tabela.getSelectedRow(), 0);
    }

    private void abrirEdicao() {
        new EditarFuncionarioDialog(SwingUtilities.getWindowAncestor(this), idSelecionado(), this::carregarDados)
                .setVisible(true);
    }

    private void excluirSelecionado() {
        int row = tabela.getSelectedRow();
        String nome = (String) tableModel.getValueAt(row, 1);
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Excluir o funcionário \"" + nome + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        long id = idSelecionado();
        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                return ApiClient.delete("/funcionarios/" + id);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        carregarDados();
                    } else {
                        JOptionPane.showMessageDialog(FuncionarioPanel.this,
                                "Erro ao excluir: " + resposta.body(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FuncionarioPanel.this,
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
                return MAPPER.readTree(ApiClient.get("/funcionarios").body());
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    tableModel.setRowCount(0);
                    for (JsonNode f : lista) {
                        String unidade = f.path("unidade").path("nomeUnidade").asText("-");
                        tableModel.addRow(new Object[]{
                            f.path("id").asLong(),
                            f.path("nome").asText(),
                            f.path("username").asText(),
                            f.path("cargo").asText(),
                            unidade
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FuncionarioPanel.this,
                            "Erro ao carregar funcionários: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
