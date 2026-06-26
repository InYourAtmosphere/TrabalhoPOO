package org.poo.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Faixa de aviso não-modal exibida no topo de um painel. Fica oculta por padrão
 * e, ao contrário de um {@link JOptionPane}, não interrompe o usuário nem
 * empilha quando a mesma mensagem se repete (ex.: falhas de auto-refresh).
 */
public class BannerErro extends JPanel {

    private static final Color FUNDO = new Color(248, 215, 218);
    private static final Color BORDA = new Color(220, 53, 69);
    private static final Color TEXTO = new Color(132, 32, 41);

    private final JLabel label = new JLabel();

    public BannerErro() {
        super(new BorderLayout());
        setBackground(FUNDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                new EmptyBorder(8, 12, 8, 12)));
        label.setForeground(TEXTO);
        add(label, BorderLayout.WEST);
        setVisible(false);
    }

    public void mostrar(String mensagem) {
        // Mesma mensagem já visível: evita re-layout repetido durante o auto-refresh.
        if (isVisible() && mensagem.equals(label.getText())) {
            return;
        }
        label.setText(mensagem);
        setVisible(true);
        revalidate();
        repaint();
    }

    public void ocultar() {
        if (!isVisible()) {
            return;
        }
        setVisible(false);
        revalidate();
        repaint();
    }
}
