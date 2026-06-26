package org.poo.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
