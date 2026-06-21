package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.service.ApiException;
import org.poo.service.UnidadeService;
import org.poo.service.VeiculoService;
import org.poo.ui.Estilos;
import org.poo.ui.SessionContext;
import org.poo.ui.util.ExportUtils;
import org.poo.ui.view.dialogs.EditarVeiculoDialog;
import org.poo.ui.view.dialogs.NovoVeiculoDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VeiculoPanel extends JPanel {

    private static final String OPCAO_MINHA_UNIDADE = "Minha Unidade";
    private static final String OPCAO_TODAS_UNIDADES = "Todas as Unidades";
    private static final int INTERVALO_ATUALIZACAO_MS = 15_000;

    private final VeiculoService veiculoService = new VeiculoService();
    private final UnidadeService unidadeService = new UnidadeService();
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnExcluir = new JButton("Excluir");
    private final JComboBox<String> comboUnidade = new JComboBox<>();
    private final Map<String, Long> idsPorNomeUnidade = new LinkedHashMap<>();
    private final Timer timerAtualizacao;

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
            comboUnidade.addItem(OPCAO_MINHA_UNIDADE);
            comboUnidade.addItem(OPCAO_TODAS_UNIDADES);
            toolbar.add(Box.createHorizontalStrut(8));
            toolbar.add(new JLabel("Unidade: "));
            toolbar.add(comboUnidade);
            carregarUnidadesDisponiveis();

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
            comboUnidade.addActionListener(e -> carregarDados());
        }

        toolbar.add(Box.createHorizontalGlue());
        JButton btnExportarCSV = new JButton("Exportar CSV");
        Estilos.estilizarBotaoSecundario(btnExportarCSV);
        toolbar.add(btnExportarCSV);
        add(toolbar, BorderLayout.NORTH);

        String[] colunas = {"ID", "Placa", "Marca", "Modelo", "Ano", "KM", "Status", "Tipo", "Unidade"};
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
        btnExportarCSV.addActionListener(e -> ExportUtils.exportarCSV(this, "veiculos", tableModel));
        carregarDados();

        timerAtualizacao = new Timer(INTERVALO_ATUALIZACAO_MS, e -> carregarDados());
        timerAtualizacao.setRepeats(true);
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    carregarDados();
                    timerAtualizacao.start();
                } else {
                    timerAtualizacao.stop();
                }
            }
        });
    }

    private void carregarUnidadesDisponiveis() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return unidadeService.listar();
            }

            @Override
            protected void done() {
                try {
                    JsonNode unidades = get();
                    for (JsonNode u : unidades) {
                        String nome = u.path("nomeUnidade").asText();
                        idsPorNomeUnidade.put(nome, u.path("id").asLong());
                        comboUnidade.addItem(nome);
                    }
                } catch (Exception ignored) {
                    // Mantém apenas as opções padrão se a listagem de unidades falhar.
                }
            }
        }.execute();
    }

    private JsonNode buscarVeiculos() throws Exception {
        if (!SessionContext.getInstance().isGerente()) {
            return veiculoService.listar();
        }
        String selecionado = (String) comboUnidade.getSelectedItem();
        if (selecionado == null || OPCAO_MINHA_UNIDADE.equals(selecionado)) {
            return veiculoService.listar();
        }
        if (OPCAO_TODAS_UNIDADES.equals(selecionado)) {
            return veiculoService.listarTodasUnidades();
        }
        Long unidadeId = idsPorNomeUnidade.get(selecionado);
        return unidadeId != null ? veiculoService.listarPorUnidade(unidadeId) : veiculoService.listar();
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
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                veiculoService.deletar(id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    carregarDados();
                } catch (Exception ex) {
                    String mensagem = ApiException.isCausa(ex)
                            ? "Erro ao excluir: " + ApiException.mensagemDe(ex)
                            : "Erro de conexão com o servidor.";
                    JOptionPane.showMessageDialog(VeiculoPanel.this,
                            mensagem,
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void carregarDados() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return buscarVeiculos();
            }

            @Override
            protected void done() {
                try {
                    JsonNode lista = get();
                    int linhaSelecionada = tabela.getSelectedRow();
                    Long idSelecionadoAntes = linhaSelecionada >= 0 ? (Long) tableModel.getValueAt(linhaSelecionada, 0) : null;
                    tableModel.setRowCount(0);
                    for (JsonNode v : lista) {
                        String tipo = v.has("cilindrada") ? "Motocicleta" : "Carro";
                        String unidade = v.path("unidade").path("nomeUnidade").asText("-");
                        tableModel.addRow(new Object[]{
                            v.path("id").asLong(),
                            v.path("placa").asText(),
                            v.path("marca").asText(),
                            v.path("modelo").asText(),
                            v.path("ano").asInt(),
                            String.format("%.0f km", v.path("kmAtual").asDouble()),
                            v.path("status").asText(),
                            tipo,
                            unidade
                        });
                    }
                    if (idSelecionadoAntes != null) {
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (idSelecionadoAntes.equals(tableModel.getValueAt(i, 0))) {
                                tabela.setRowSelectionInterval(i, i);
                                break;
                            }
                        }
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
