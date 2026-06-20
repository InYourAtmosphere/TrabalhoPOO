package org.poo;

import javax.swing.SwingUtilities;

import org.poo.ui.Estilos;
import org.poo.ui.view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Estilos.aplicarTemaGlobal();
            new LoginFrame();
        });
    }
}
