package org.poo.ui.view.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DashboardPanel extends JPanel {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final DefaultPieDataset<String> veiculosPorStatus = new DefaultPieDataset<>();
    private final DefaultCategoryDataset veiculosPorTipo = new DefaultCategoryDataset();
    private final DefaultCategoryDataset contratosPorStatus = new DefaultCategoryDataset();
    private final DefaultCategoryDataset faturamentoPorUnidade = new DefaultCategoryDataset();
    private final DefaultPieDataset<String> manutencoesPorTipo = new DefaultPieDataset<>();
    private final DefaultCategoryDataset custoManutencaoPorMes = new DefaultCategoryDataset();

    private final JLabel valorTotalVeiculos = new JLabel("-");
    private final JLabel valorVeiculosDisponiveis = new JLabel("-");
    private final JLabel valorContratosAtivos = new JLabel("-");
    private final JLabel valorReceitaTotal = new JLabel("-");

    public DashboardPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Estilos.estilizarToolbar(toolbar);
        JButton btnAtualizar = new JButton("Atualizar");
        Estilos.estilizarBotaoSecundario(btnAtualizar);
        btnAtualizar.addActionListener(e -> carregarDados());
        toolbar.add(btnAtualizar);
        add(toolbar, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setBackground(Estilos.CINZA_FUNDO);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(criarPainelIndicadores());
        conteudo.add(Box.createVerticalStrut(10));
        conteudo.add(criarPainelGraficos());

        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        carregarDados();
    }

    private JPanel criarPainelIndicadores() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 10, 0));
        painel.setBackground(Estilos.CINZA_FUNDO);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        painel.add(criarCartaoIndicador("Total de Veículos", valorTotalVeiculos));
        painel.add(criarCartaoIndicador("Veículos Disponíveis", valorVeiculosDisponiveis));
        painel.add(criarCartaoIndicador("Contratos Ativos", valorContratosAtivos));
        painel.add(criarCartaoIndicador("Receita Total", valorReceitaTotal));
        return painel;
    }

    private JPanel criarCartaoIndicador(String titulo, JLabel valor) {
        JPanel cartao = Estilos.criarCartao();
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.CINZA_BORDA, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setForeground(Estilos.CINZA_TEXTO);
        labelTitulo.setFont(labelTitulo.getFont().deriveFont(Font.PLAIN, 12f));

        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 22f));
        valor.setForeground(Estilos.AZUL_PRIMARIO);

        cartao.add(labelTitulo, BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelGraficos() {
        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBackground(Estilos.CINZA_FUNDO);
        painel.setPreferredSize(new Dimension(760, 810));

        painel.add(envolverEmCartao(criarGraficoPizza("Veículos por Status", veiculosPorStatus)));
        painel.add(envolverEmCartao(criarGraficoBarras("Veículos por Tipo", "Quantidade", veiculosPorTipo)));
        painel.add(envolverEmCartao(criarGraficoBarras("Contratos por Status", "Quantidade", contratosPorStatus)));
        painel.add(envolverEmCartao(criarGraficoBarras("Faturamento por Unidade", "Valor (R$)", faturamentoPorUnidade)));
        painel.add(envolverEmCartao(criarGraficoPizza("Manutenções por Tipo", manutencoesPorTipo)));
        painel.add(envolverEmCartao(criarGraficoBarras("Custo de Manutenção por Mês", "Custo (R$)", custoManutencaoPorMes)));

        return painel;
    }

    private JPanel envolverEmCartao(ChartPanel chartPanel) {
        JPanel cartao = Estilos.criarCartao();
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(BorderFactory.createLineBorder(Estilos.CINZA_BORDA, 1));
        chartPanel.setPreferredSize(new Dimension(370, 250));
        cartao.add(chartPanel, BorderLayout.CENTER);
        return cartao;
    }

    private ChartPanel criarGraficoPizza(String titulo, DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(titulo, dataset, true, true, false);
        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(null);
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private ChartPanel criarGraficoBarras(String titulo, String rotuloEixoY, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                titulo, null, rotuloEixoY, dataset, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Estilos.CINZA_BORDA);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Estilos.AZUL_PRIMARIO);
        renderer.setShadowVisible(false);
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private void carregarDados() {
        new SwingWorker<JsonNode[], Void>() {
            @Override
            protected JsonNode[] doInBackground() throws Exception {
                JsonNode veiculos = MAPPER.readTree(ApiClient.get("/veiculos").body());
                JsonNode contratos = MAPPER.readTree(ApiClient.get("/contratos").body());
                JsonNode manutencoes = MAPPER.readTree(ApiClient.get("/manutencoes").body());
                return new JsonNode[]{veiculos, contratos, manutencoes};
            }

            @Override
            protected void done() {
                try {
                    JsonNode[] resultado = get();
                    atualizarVeiculos(resultado[0]);
                    atualizarContratos(resultado[1]);
                    atualizarManutencoes(resultado[2]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Erro ao carregar dados do dashboard: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void atualizarVeiculos(JsonNode veiculos) {
        Map<String, Long> porStatus = new LinkedHashMap<>();
        long carros = 0, motos = 0, disponiveis = 0;

        for (JsonNode v : veiculos) {
            String status = rotuloStatusVeiculo(v.path("status").asText());
            porStatus.merge(status, 1L, Long::sum);

            if (v.has("cilindrada")) motos++; else carros++;
            if ("DISPONIVEL".equals(v.path("status").asText())) disponiveis++;
        }

        veiculosPorStatus.clear();
        porStatus.forEach(veiculosPorStatus::setValue);

        veiculosPorTipo.clear();
        veiculosPorTipo.addValue(carros, "Quantidade", "Carro");
        veiculosPorTipo.addValue(motos, "Quantidade", "Motocicleta");

        valorTotalVeiculos.setText(String.valueOf(veiculos.size()));
        valorVeiculosDisponiveis.setText(String.valueOf(disponiveis));
    }

    private void atualizarContratos(JsonNode contratos) {
        Map<String, Long> porStatus = new LinkedHashMap<>();
        Map<String, Double> faturamentoUnidade = new TreeMap<>();
        long ativos = 0;
        double receitaTotal = 0;

        for (JsonNode c : contratos) {
            String status = c.path("status").asText();
            porStatus.merge(rotuloStatusContrato(status), 1L, Long::sum);
            if ("ATIVO".equals(status)) ativos++;

            double valorTotal = c.path("valorTotal").asDouble(0);
            if (valorTotal > 0) {
                receitaTotal += valorTotal;
                String unidade = c.path("unidadeRetirada").path("nomeUnidade").asText("Sem unidade");
                faturamentoUnidade.merge(unidade, valorTotal, Double::sum);
            }
        }

        contratosPorStatus.clear();
        porStatus.forEach((status, qtd) -> contratosPorStatus.addValue(qtd, "Quantidade", status));

        faturamentoPorUnidade.clear();
        faturamentoUnidade.forEach((unidade, valor) -> faturamentoPorUnidade.addValue(valor, "Faturamento", unidade));

        valorContratosAtivos.setText(String.valueOf(ativos));
        valorReceitaTotal.setText(MOEDA.format(receitaTotal));
    }

    private void atualizarManutencoes(JsonNode manutencoes) {
        Map<String, Long> porTipo = new LinkedHashMap<>();
        Map<YearMonth, Double> custoPorMes = new TreeMap<>();
        DateTimeFormatter formatoData = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (JsonNode m : manutencoes) {
            porTipo.merge(rotuloTipoManutencao(m.path("tipo").asText()), 1L, Long::sum);

            double custo = m.path("custo").asDouble(0);
            String dataInicio = m.path("dataInicio").asText(null);
            if (dataInicio != null) {
                YearMonth mes = YearMonth.from(LocalDateTime.parse(dataInicio, formatoData));
                custoPorMes.merge(mes, custo, Double::sum);
            }
        }

        manutencoesPorTipo.clear();
        porTipo.forEach(manutencoesPorTipo::setValue);

        custoManutencaoPorMes.clear();
        DateTimeFormatter formatoMes = DateTimeFormatter.ofPattern("MM/yyyy");
        custoPorMes.forEach((mes, custo) ->
                custoManutencaoPorMes.addValue(custo, "Custo", mes.format(formatoMes)));
    }

    private String rotuloStatusVeiculo(String status) {
        return switch (status) {
            case "DISPONIVEL" -> "Disponível";
            case "LOCADO" -> "Locado";
            case "EM_MANUTENCAO" -> "Em Manutenção";
            case "DESATIVADO" -> "Desativado";
            default -> status;
        };
    }

    private String rotuloStatusContrato(String status) {
        return switch (status) {
            case "ATIVO" -> "Ativo";
            case "FINALIZADO" -> "Finalizado";
            case "CANCELADO" -> "Cancelado";
            default -> status;
        };
    }

    private String rotuloTipoManutencao(String tipo) {
        return switch (tipo) {
            case "PREVENTIVA" -> "Preventiva";
            case "CORRETIVA" -> "Corretiva";
            case "PREDITIVA" -> "Preditiva";
            default -> tipo;
        };
    }
}
