package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.ui.ApiClient;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NovaUnidadeDialog extends JDialog {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoLogradouro = new JTextField(22);
    private final JTextField campoNumero = new JTextField(22);
    private final JTextField campoComplemento = new JTextField(22);
    private final JTextField campoBairro = new JTextField(22);
    private final JTextField campoCidade = new JTextField(22);
    private final JTextField campoEstado = new JTextField(22);
    private final JTextField campoCep = new JTextField(22);
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final Runnable aoSalvarComSucesso;

    public NovaUnidadeDialog(Window owner, Runnable aoSalvarComSucesso) {
        super(owner, "Nova Unidade", ModalityType.APPLICATION_MODAL);
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        configurarComponentes();
        pack();
        setLocationRelativeTo(owner);
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        int linha = 0;
        linha = adicionarCampo(painel, gbc, linha, "Nome*:", campoNome);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        JLabel tituloEndereco = new JLabel("Endereço*");
        tituloEndereco.setFont(tituloEndereco.getFont().deriveFont(Font.BOLD));
        painel.add(tituloEndereco, gbc);
        gbc.gridwidth = 1;
        linha++;

        linha = adicionarCampo(painel, gbc, linha, "Logradouro:", campoLogradouro);
        linha = adicionarCampo(painel, gbc, linha, "Número:", campoNumero);
        linha = adicionarCampo(painel, gbc, linha, "Complemento:", campoComplemento);
        linha = adicionarCampo(painel, gbc, linha, "Bairro:", campoBairro);
        linha = adicionarCampo(painel, gbc, linha, "Cidade:", campoCidade);
        linha = adicionarCampo(painel, gbc, linha, "Estado:", campoEstado);
        linha = adicionarCampo(painel, gbc, linha, "CEP:", campoCep);

        Estilos.estilizarLabelErro(labelErro);
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(labelErro, gbc);
        linha++;

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Estilos.estilizarBotaoPrimario(botaoSalvar);
        JButton botaoCancelar = new JButton("Cancelar");
        Estilos.estilizarBotaoSecundario(botaoCancelar);
        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isBlank()) {
            labelErro.setText("Nome da unidade é obrigatório.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        Map<String, Object> endereco = new LinkedHashMap<>();
        endereco.put("logradouro", valorOuNulo(campoLogradouro));
        endereco.put("numero", valorOuNulo(campoNumero));
        endereco.put("complemento", valorOuNulo(campoComplemento));
        endereco.put("bairro", valorOuNulo(campoBairro));
        endereco.put("cidade", valorOuNulo(campoCidade));
        endereco.put("estado", valorOuNulo(campoEstado));
        endereco.put("cep", valorOuNulo(campoCep));

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nomeUnidade", nome);
        corpo.put("endereco", endereco);

        new SwingWorker<ApiClient.ApiResponse, Void>() {
            @Override
            protected ApiClient.ApiResponse doInBackground() throws Exception {
                String json = MAPPER.writeValueAsString(corpo);
                return ApiClient.post("/unidades", json);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.ApiResponse resposta = get();
                    if (resposta.isSuccess()) {
                        aoSalvarComSucesso.run();
                        dispose();
                    } else {
                        labelErro.setText("Erro ao salvar: " + resposta.body());
                        botaoSalvar.setEnabled(true);
                    }
                } catch (Exception ex) {
                    labelErro.setText("Erro de conexão com o servidor.");
                    botaoSalvar.setEnabled(true);
                }
            }
        }.execute();
    }

    private String valorOuNulo(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }
}
