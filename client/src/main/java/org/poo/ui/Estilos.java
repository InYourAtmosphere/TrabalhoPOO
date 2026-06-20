package org.poo.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Estilos {

    public static final Color AZUL_PRIMARIO = new Color(13, 110, 253);
    public static final Color AZUL_PRIMARIO_HOVER = new Color(10, 88, 202);
    public static final Color VERMELHO_PERIGO = new Color(220, 53, 69);
    public static final Color VERMELHO_PERIGO_HOVER = new Color(187, 45, 59);
    public static final Color CINZA_HEADER = new Color(33, 37, 41);
    public static final Color CINZA_SIDEBAR = new Color(52, 58, 64);
    public static final Color CINZA_BOTAO_MENU = new Color(73, 80, 87);
    public static final Color CINZA_TEXTO = new Color(73, 80, 87);
    public static final Color CINZA_FUNDO = new Color(247, 248, 249);
    public static final Color CINZA_BORDA = new Color(222, 226, 230);
    public static final Color BRANCO = Color.WHITE;

    private static final Font FONTE_BASE = criarFonte(Font.PLAIN, 13f);
    private static final Font FONTE_NEGRITO = criarFonte(Font.BOLD, 13f);

    private static Font criarFonte(int estilo, float tamanho) {
        Font base = new Font("Segoe UI", estilo, 12);
        if (!base.getFamily().equals("Segoe UI")) {
            base = new Font("SansSerif", estilo, 12);
        }
        return base.deriveFont(estilo, tamanho);
    }

    public static void aplicarTemaGlobal() {
        UIManager.put("Label.font", FONTE_BASE);
        UIManager.put("Button.font", FONTE_NEGRITO);
        UIManager.put("TextField.font", FONTE_BASE);
        UIManager.put("PasswordField.font", FONTE_BASE);
        UIManager.put("ComboBox.font", FONTE_BASE);
        UIManager.put("CheckBox.font", FONTE_BASE);
        UIManager.put("Table.font", FONTE_BASE);
        UIManager.put("TableHeader.font", FONTE_NEGRITO);
        UIManager.put("TabbedPane.font", FONTE_BASE);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("control", CINZA_FUNDO);
    }

    public static void estilizarBotaoPrimario(JButton botao) {
        estilizarBotaoBase(botao, AZUL_PRIMARIO, AZUL_PRIMARIO_HOVER);
    }

    public static void estilizarBotaoPerigo(JButton botao) {
        estilizarBotaoBase(botao, VERMELHO_PERIGO, VERMELHO_PERIGO_HOVER);
    }

    private static void estilizarBotaoBase(JButton botao, Color cor, Color corHover) {
        botao.setBackground(cor);
        botao.setForeground(BRANCO);
        botao.setFont(FONTE_NEGRITO);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(8, 18, 8, 18));
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.isEnabled()) botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(cor);
            }
        });
    }

    public static void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(BRANCO);
        botao.setForeground(CINZA_TEXTO);
        botao.setFont(FONTE_BASE);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CINZA_BORDA, 1),
                new EmptyBorder(7, 16, 7, 16)));
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.isEnabled()) botao.setBackground(CINZA_FUNDO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(BRANCO);
            }
        });
    }

    public static void estilizarLabelErro(JLabel label) {
        label.setForeground(VERMELHO_PERIGO);
        label.setFont(FONTE_BASE.deriveFont(11f));
    }

    public static void estilizarToolbar(JToolBar toolbar) {
        toolbar.setBackground(CINZA_FUNDO);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CINZA_BORDA),
                new EmptyBorder(8, 8, 8, 8)));
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(28);
        tabela.setGridColor(CINZA_BORDA);
        tabela.setShowGrid(true);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionBackground(new Color(204, 228, 255));
        tabela.setSelectionForeground(Color.BLACK);
        tabela.setFillsViewportHeight(true);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(CINZA_HEADER);
        header.setForeground(BRANCO);
        header.setFont(FONTE_NEGRITO);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);
    }

    public static JPanel criarCartao() {
        JPanel cartao = new JPanel();
        cartao.setBackground(BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CINZA_BORDA, 1),
                new EmptyBorder(25, 30, 25, 30)));
        return cartao;
    }
}
