package org.poo.ui.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.poo.ui.ApiClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfExporter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // A4 paisagem
    private static final PDRectangle TAMANHO = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
    private static final float MARGEM = 40f;
    private static final float W  = TAMANHO.getWidth();   // 841.89
    private static final float H  = TAMANHO.getHeight();  // 595.28
    private static final float CW = W - 2 * MARGEM;       // 761.89

    private static final Color AZUL       = new Color(59, 130, 246);
    private static final Color CINZA      = new Color(245, 245, 245);
    private static final Color BORDA      = new Color(200, 200, 200);
    private static final Color CINZA_TEXT = new Color(90, 90, 90);

    public static void exportar(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("relatorio_" + LocalDate.now() + ".pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".pdf")) file = new File(file.getAbsolutePath() + ".pdf");
        final File f = file;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                JsonNode veiculos   = MAPPER.readTree(ApiClient.get("/veiculos").body());
                JsonNode contratos  = MAPPER.readTree(ApiClient.get("/contratos").body());
                JsonNode manutencoes = MAPPER.readTree(ApiClient.get("/manutencoes").body());
                gerar(f, veiculos, contratos, manutencoes);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(parent,
                            "Relatório gerado:\n" + f.getAbsolutePath(),
                            "Exportação concluída", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Erro ao gerar PDF: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // Geração do documento
    private static void gerar(File arquivo, JsonNode veiculos, JsonNode contratos, JsonNode manutencoes) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            Ctx ctx = new Ctx(doc);

            cabecalho(ctx);
            metricas(ctx, veiculos, contratos, manutencoes);

            tabelaSecao(ctx, "Contratos",
                    new String[]{"ID", "Cliente", "Veiculo", "Unidade", "Inicio", "Fim", "Valor Diaria", "Status"},
                    new float[]{40, 110, 130, 100, 85, 85, 70, 70},
                    linhasContratos(contratos));

            tabelaSecao(ctx, "Veiculos",
                    new String[]{"ID", "Placa", "Marca", "Modelo", "Ano", "KM", "Status", "Tipo"},
                    new float[]{35, 75, 90, 120, 45, 80, 90, 100},
                    linhasVeiculos(veiculos));

            tabelaSecao(ctx, "Manutencoes",
                    new String[]{"ID", "Veiculo", "Tipo", "Descricao", "Data Inicio", "Custo"},
                    new float[]{35, 155, 85, 250, 120, 80},
                    linhasManutencoes(manutencoes));

            ctx.close();
            doc.save(arquivo);
        }
    }

    // Blocos de conteúdo
    private static void cabecalho(Ctx ctx) throws Exception {
        float bH = 45f;
        ctx.cs.setNonStrokingColor(AZUL);
        ctx.cs.addRect(MARGEM, ctx.y - bH, CW, bH);
        ctx.cs.fill();

        ctx.cs.setNonStrokingColor(Color.WHITE);
        txt(ctx, PDType1Font.HELVETICA_BOLD, 16, MARGEM + 12, ctx.y - bH + 16,
                "Relatorio Gerencial - AlugaFacil");
        txt(ctx, PDType1Font.HELVETICA, 9, W - MARGEM - 100, ctx.y - bH + 16,
                "Emitido em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        ctx.y -= bH + 14;
    }

    private static void metricas(Ctx ctx, JsonNode veiculos, JsonNode contratos, JsonNode manutencoes) throws Exception {
        long totalV = veiculos.size(), dispV = 0;
        for (JsonNode v : veiculos) if ("DISPONIVEL".equals(v.path("status").asText())) dispV++;

        long ativos = 0;
        double receita = 0;
        for (JsonNode c : contratos) {
            if ("ATIVO".equals(c.path("status").asText())) ativos++;
            receita += c.path("valorTotal").asDouble(0);
        }

        String[] labels = {"Total de Veiculos", "Veiculos Disponiveis", "Contratos Ativos", "Receita Total"};
        String[] vals = {
            String.valueOf(totalV),
            String.valueOf(dispV),
            String.valueOf(ativos),
            String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", receita)
        };

        float cW = (CW - 30) / 4;
        float cH = 52f;
        float x = MARGEM;

        for (int i = 0; i < 4; i++) {
            ctx.cs.setNonStrokingColor(CINZA);
            ctx.cs.addRect(x, ctx.y - cH, cW, cH);
            ctx.cs.fill();

            ctx.cs.setStrokingColor(BORDA);
            ctx.cs.setLineWidth(0.5f);
            ctx.cs.addRect(x, ctx.y - cH, cW, cH);
            ctx.cs.stroke();

            ctx.cs.setNonStrokingColor(CINZA_TEXT);
            txt(ctx, PDType1Font.HELVETICA, 8, x + 8, ctx.y - 15, labels[i]);

            ctx.cs.setNonStrokingColor(AZUL);
            txt(ctx, PDType1Font.HELVETICA_BOLD, 14, x + 8, ctx.y - 37, vals[i]);

            x += cW + 10;
        }
        ctx.y -= cH + 14;
    }

    private static void tabelaSecao(Ctx ctx, String titulo, String[] cols, float[] wCols,
                                    List<String[]> rows) throws Exception {
        float rowH = 16f, hdrH = 20f, titleH = 22f;
        ctx.verificar(titleH + hdrH + rowH);

        ctx.cs.setNonStrokingColor(Color.BLACK);
        txt(ctx, PDType1Font.HELVETICA_BOLD, 11, MARGEM, ctx.y - 15,
                titulo + " (" + rows.size() + " registros)");
        ctx.y -= titleH;

        cabecalhoTabela(ctx, cols, wCols, hdrH);

        boolean zebra = false;
        for (String[] row : rows) {
            ctx.verificar(rowH);
            if (ctx.resetou) {
                cabecalhoTabela(ctx, cols, wCols, hdrH);
                zebra = false;
            }

            if (zebra) {
                ctx.cs.setNonStrokingColor(CINZA);
                ctx.cs.addRect(MARGEM, ctx.y - rowH, CW, rowH);
                ctx.cs.fill();
            }

            float x = MARGEM;
            ctx.cs.setNonStrokingColor(Color.BLACK);
            for (int c = 0; c < row.length; c++) {
                String cell = truncar(row[c] != null ? row[c] : "", wCols[c] - 6);
                txt(ctx, PDType1Font.HELVETICA, 8, x + 3, ctx.y - 11, cell);
                x += wCols[c];
            }

            ctx.cs.setStrokingColor(BORDA);
            ctx.cs.setLineWidth(0.3f);
            ctx.cs.moveTo(MARGEM, ctx.y - rowH);
            ctx.cs.lineTo(MARGEM + CW, ctx.y - rowH);
            ctx.cs.stroke();

            ctx.y -= rowH;
            zebra = !zebra;
        }
        ctx.y -= 10;
    }

    private static void cabecalhoTabela(Ctx ctx, String[] cols, float[] wCols, float hdrH) throws Exception {
        ctx.cs.setNonStrokingColor(AZUL);
        ctx.cs.addRect(MARGEM, ctx.y - hdrH, CW, hdrH);
        ctx.cs.fill();

        float x = MARGEM;
        ctx.cs.setNonStrokingColor(Color.WHITE);
        for (int c = 0; c < cols.length; c++) {
            txt(ctx, PDType1Font.HELVETICA_BOLD, 8, x + 3, ctx.y - 13, cols[c]);
            x += wCols[c];
        }
        ctx.y -= hdrH;
    }

    // Helpers de desenho
    private static void txt(Ctx ctx, PDType1Font font, float size, float x, float y, String text) throws Exception {
        ctx.cs.beginText();
        ctx.cs.setFont(font, size);
        ctx.cs.newLineAtOffset(x, y);
        ctx.cs.showText(text);
        ctx.cs.endText();
    }

    private static String truncar(String texto, float maxW) {
        try {
            if (PDType1Font.HELVETICA.getStringWidth(texto) / 1000f * 8 <= maxW) return texto;
            while (texto.length() > 3) {
                texto = texto.substring(0, texto.length() - 1);
                try {
                    if (PDType1Font.HELVETICA.getStringWidth(texto + "...") / 1000f * 8 <= maxW)
                        return texto + "...";
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return texto;
    }

    // Extração de dados
    private static List<String[]> linhasContratos(JsonNode contratos) {
        List<String[]> rows = new ArrayList<>();
        for (JsonNode c : contratos) {
            rows.add(new String[]{
                String.valueOf(c.path("id").asLong()),
                c.path("cliente").path("nome").asText("-"),
                c.path("veiculo").path("modelo").asText("-") + " " + c.path("veiculo").path("placa").asText(),
                c.path("unidadeRetirada").path("nomeUnidade").asText("-"),
                formatarData(c.path("dataInicio").asText("")),
                formatarData(c.path("dataFimPrevista").asText("")),
                String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", c.path("valorDiaria").asDouble()),
                c.path("status").asText()
            });
        }
        return rows;
    }

    private static List<String[]> linhasVeiculos(JsonNode veiculos) {
        List<String[]> rows = new ArrayList<>();
        for (JsonNode v : veiculos) {
            rows.add(new String[]{
                String.valueOf(v.path("id").asLong()),
                v.path("placa").asText(),
                v.path("marca").asText(),
                v.path("modelo").asText(),
                String.valueOf(v.path("ano").asInt()),
                String.format("%.0f km", v.path("kmAtual").asDouble()),
                v.path("status").asText(),
                v.has("cilindrada") ? "Motocicleta" : "Carro"
            });
        }
        return rows;
    }

    private static List<String[]> linhasManutencoes(JsonNode manutencoes) {
        List<String[]> rows = new ArrayList<>();
        for (JsonNode m : manutencoes) {
            String veiculo = m.path("veiculo").path("modelo").asText("-")
                    + " (" + m.path("veiculo").path("placa").asText("-") + ")";
            rows.add(new String[]{
                String.valueOf(m.path("id").asLong()),
                veiculo,
                m.path("tipo").asText(),
                m.path("descricao").asText(),
                formatarData(m.path("dataInicio").asText("")),
                String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", m.path("custo").asDouble())
            });
        }
        return rows;
    }

    private static String formatarData(String iso) {
        if (iso == null || iso.isEmpty()) return "-";
        String s = iso.replace("T", " ");
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    // Contexto de página
    private static class Ctx {
        final PDDocument doc;
        PDPageContentStream cs;
        float y;
        boolean resetou;

        Ctx(PDDocument doc) throws Exception {
            this.doc = doc;
            novaPagina();
        }

        void novaPagina() throws Exception {
            if (cs != null) cs.close();
            PDPage page = new PDPage(TAMANHO);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = H - MARGEM;
            resetou = true;
        }

        void verificar(float h) throws Exception {
            resetou = false;
            if (y - h < MARGEM + 10) novaPagina();
        }

        void close() throws Exception {
            if (cs != null) cs.close();
        }
    }
}
