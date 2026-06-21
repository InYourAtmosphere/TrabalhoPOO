package org.poo.ui.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ExportUtils {

    public static void exportarCSV(Component parent, String nomeBase, DefaultTableModel tableModel) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nomeBase + "_" + LocalDate.now() + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write('﻿'); // BOM para compatibilidade com Excel brasileiro

            int cols = tableModel.getColumnCount();
            String[] headers = new String[cols];
            for (int i = 0; i < cols; i++) headers[i] = tableModel.getColumnName(i);
            writer.write(linhaCSV(headers));

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String[] linha = new String[cols];
                for (int col = 0; col < cols; col++) {
                    Object val = tableModel.getValueAt(row, col);
                    linha[col] = val != null ? val.toString() : "";
                }
                writer.write(linhaCSV(linha));
            }

            JOptionPane.showMessageDialog(parent,
                    "Exportado para:\n" + file.getAbsolutePath(),
                    "Exportação concluída", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Erro ao exportar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String linhaCSV(String[] campos) {
        return Arrays.stream(campos)
                .map(c -> "\"" + c.replace("\"", "\"\"") + "\"")
                .collect(Collectors.joining(";")) + "\n";
    }
}
