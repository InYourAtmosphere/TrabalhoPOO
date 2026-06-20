package org.poo.ui.view;

import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;
import org.poo.ui.SessionContext;
import org.poo.ui.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private static final String VEICULOS     = "Veículos";
    private static final String CLIENTES     = "Clientes";
    private static final String CONTRATOS    = "Contratos";
    private static final String FUNCIONARIOS = "Funcionários";
    private static final String UNIDADES     = "Unidades";
    private static final String MANUTENCOES  = "Manutenções";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel areaConteudo = new JPanel(cardLayout);
    private final Map<String, JButton> botoesMenu = new LinkedHashMap<>();
    private String itemAtivo;

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
        getContentPane().setBackground(Estilos.CINZA_FUNDO);

        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cabecalho.setBackground(Estilos.CINZA_HEADER);

        JLabel titulo = new JLabel("AlugaFácil");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 17f));
        cabecalho.add(titulo, BorderLayout.WEST);

        String usuario = SessionContext.getInstance().getNomeUsuario();
        String cargo = SessionContext.getInstance().getCargo();
        JLabel labelUsuario = new JLabel(usuario + "  ·  " + cargo);
        labelUsuario.setForeground(new Color(206, 212, 218));

        JButton btnLogout = new JButton("Sair");
        Estilos.estilizarBotaoSecundario(btnLogout);
        btnLogout.addActionListener(e -> fazerLogout());

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        painelDireito.setOpaque(false);
        painelDireito.add(labelUsuario);
        painelDireito.add(btnLogout);
        cabecalho.add(painelDireito, BorderLayout.EAST);

        add(cabecalho, BorderLayout.NORTH);

        // Menu lateral
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new BoxLayout(menuLateral, BoxLayout.Y_AXIS));
        menuLateral.setBackground(Estilos.CINZA_SIDEBAR);
        menuLateral.setBorder(BorderFactory.createEmptyBorder(14, 8, 14, 8));
        menuLateral.setPreferredSize(new Dimension(180, 0));

        boolean gerente = SessionContext.getInstance().isGerente();

        for (String item : new String[]{VEICULOS, CLIENTES, CONTRATOS, MANUTENCOES}) {
            menuLateral.add(criarBotaoMenu(item));
            menuLateral.add(Box.createVerticalStrut(6));
        }
        if (gerente) {
            menuLateral.add(criarBotaoMenu(FUNCIONARIOS));
            menuLateral.add(Box.createVerticalStrut(6));
            menuLateral.add(criarBotaoMenu(UNIDADES));
            menuLateral.add(Box.createVerticalStrut(6));
        }

        add(menuLateral, BorderLayout.WEST);

        // Panels de conteúdo
        areaConteudo.add(new VeiculoPanel(),     VEICULOS);
        areaConteudo.add(new ClientePanel(),     CLIENTES);
        areaConteudo.add(new ContratoPanel(),    CONTRATOS);
        areaConteudo.add(new ManutencaoPanel(),  MANUTENCOES);
        if (gerente) {
            areaConteudo.add(new FuncionarioPanel(), FUNCIONARIOS);
            areaConteudo.add(new UnidadePanel(),     UNIDADES);
        }

        add(areaConteudo, BorderLayout.CENTER);

        selecionarItemMenu(VEICULOS);
    }

    private JButton criarBotaoMenu(String nome) {
        JButton btn = new JButton(nome);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(164, 38));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.addActionListener(e -> {
            cardLayout.show(areaConteudo, nome);
            selecionarItemMenu(nome);
        });
        botoesMenu.put(nome, btn);
        btn.setBackground(Estilos.CINZA_BOTAO_MENU);
        return btn;
    }

    private void selecionarItemMenu(String nome) {
        itemAtivo = nome;
        botoesMenu.forEach((item, botao) ->
                botao.setBackground(item.equals(nome) ? Estilos.AZUL_PRIMARIO : Estilos.CINZA_BOTAO_MENU));
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
