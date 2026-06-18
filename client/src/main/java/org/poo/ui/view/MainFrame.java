package org.poo.ui.view;

import org.poo.ui.ApiClient;
import org.poo.ui.SessionContext;
import org.poo.ui.view.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String VEICULOS     = "Veículos";
    private static final String CLIENTES     = "Clientes";
    private static final String CONTRATOS    = "Contratos";
    private static final String FUNCIONARIOS = "Funcionários";
    private static final String UNIDADES     = "Unidades";
    private static final String MANUTENCOES  = "Manutenções";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel areaConteudo = new JPanel(cardLayout);

    public MainFrame() {
        super("AlugaFácil");
        configurarJanela();
        configurarComponentes();
        setVisible(true);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
    }

    private void configurarComponentes() {
        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cabecalho.setBackground(new Color(33, 37, 41));

        JLabel titulo = new JLabel("AlugaFácil");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        cabecalho.add(titulo, BorderLayout.WEST);

        String usuario = SessionContext.getInstance().getNomeUsuario();
        JLabel labelUsuario = new JLabel("Usuário: " + usuario);
        labelUsuario.setForeground(new Color(200, 200, 200));

        JButton btnLogout = new JButton("Sair");
        btnLogout.addActionListener(e -> fazerLogout());

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelDireito.setOpaque(false);
        painelDireito.add(labelUsuario);
        painelDireito.add(btnLogout);
        cabecalho.add(painelDireito, BorderLayout.EAST);

        add(cabecalho, BorderLayout.NORTH);

        // Menu lateral
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new BoxLayout(menuLateral, BoxLayout.Y_AXIS));
        menuLateral.setBackground(new Color(52, 58, 64));
        menuLateral.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        menuLateral.setPreferredSize(new Dimension(160, 0));

        for (String item : new String[]{VEICULOS, CLIENTES, CONTRATOS, FUNCIONARIOS, UNIDADES, MANUTENCOES}) {
            menuLateral.add(criarBotaoMenu(item));
            menuLateral.add(Box.createVerticalStrut(4));
        }

        add(menuLateral, BorderLayout.WEST);

        // Panels de conteúdo
        areaConteudo.add(new VeiculoPanel(),     VEICULOS);
        areaConteudo.add(new ClientePanel(),     CLIENTES);
        areaConteudo.add(new ContratoPanel(),    CONTRATOS);
        areaConteudo.add(new FuncionarioPanel(), FUNCIONARIOS);
        areaConteudo.add(new UnidadePanel(),     UNIDADES);
        areaConteudo.add(new ManutencaoPanel(),  MANUTENCOES);

        add(areaConteudo, BorderLayout.CENTER);
    }

    private JButton criarBotaoMenu(String nome) {
        JButton btn = new JButton(nome);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(150, 36));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(73, 80, 87));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.addActionListener(e -> cardLayout.show(areaConteudo, nome));
        return btn;
    }

    private void fazerLogout() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ApiClient.delete("/auth/logout");
                return null;
            }

            @Override
            protected void done() {
                SessionContext.getInstance().limpar();
                new LoginFrame();
                dispose();
            }
        }.execute();
    }
}
