package org.poo.ui.view.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.service.ApiException;
import org.poo.service.FuncionarioService;
import org.poo.service.UnidadeService;
import org.poo.ui.Estilos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditarFuncionarioDialog extends JDialog {

    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final UnidadeService unidadeService = new UnidadeService();

    private final long funcionarioId;

    private final JTextField campoNome = new JTextField(22);
    private final JTextField campoTelefone = new JTextField(22);
    private final JTextField campoEmail = new JTextField(22);
    private final JTextField campoMatricula = new JTextField(22);
    private final JTextField campoUsername = new JTextField(22);
    private final JPasswordField campoPassword = new JPasswordField(22);
    private final JComboBox<String> comboCargo = new JComboBox<>(new String[]{"ATENDENTE", "SUPERVISOR", "GERENTE"});
    private final JComboBox<String> comboUnidade = new JComboBox<>();
    private final List<Long> idsUnidade = new ArrayList<>();
    private final JLabel labelErro = new JLabel(" ");
    private final JButton botaoSalvar = new JButton("Salvar");

    private final Runnable aoSalvarComSucesso;

    public EditarFuncionarioDialog(Window owner, long funcionarioId, Runnable aoSalvarComSucesso) {
        super(owner, "Editar Funcionário", ModalityType.APPLICATION_MODAL);
        this.funcionarioId = funcionarioId;
        this.aoSalvarComSucesso = aoSalvarComSucesso;
        configurarComponentes();
        pack();
        setLocationRelativeTo(owner);
        carregarDados();
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        int linha = 0;
        linha = adicionarCampo(painel, gbc, linha, "Nome*:", campoNome);
        linha = adicionarCampo(painel, gbc, linha, "Telefone:", campoTelefone);
        linha = adicionarCampo(painel, gbc, linha, "Email:", campoEmail);
        linha = adicionarCampo(painel, gbc, linha, "Matrícula*:", campoMatricula);
        linha = adicionarCampo(painel, gbc, linha, "Username*:", campoUsername);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2;
        JLabel labelSenha = new JLabel("Nova senha (deixe em branco para não alterar):");
        labelSenha.setFont(labelSenha.getFont().deriveFont(Font.ITALIC, 11f));
        painel.add(labelSenha, gbc);
        gbc.gridwidth = 1;
        linha++;

        linha = adicionarCampo(painel, gbc, linha, "Senha:", campoPassword);

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1;
        painel.add(comboCargo, gbc);
        linha++;

        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel("Unidade*:"), gbc);
        gbc.gridx = 1;
        painel.add(comboUnidade, gbc);
        linha++;

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
        gbc.gridy = linha;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
        return linha + 1;
    }

    private void carregarDados() {
        botaoSalvar.setEnabled(false);
        new SwingWorker<JsonNode[], Void>() {
            @Override
            protected JsonNode[] doInBackground() throws Exception {
                JsonNode funcionario = funcionarioService.buscarPorId(funcionarioId);
                JsonNode unidades = unidadeService.listar();
                return new JsonNode[]{funcionario, unidades};
            }

            @Override
            protected void done() {
                try {
                    JsonNode[] resultado = get();
                    JsonNode f = resultado[0];
                    JsonNode unidades = resultado[1];

                    campoNome.setText(f.path("nome").asText());
                    campoTelefone.setText(f.path("telefone").asText(""));
                    campoEmail.setText(f.path("email").asText(""));
                    campoMatricula.setText(f.path("matricula").asText());
                    campoUsername.setText(f.path("username").asText());

                    String cargo = f.path("cargo").asText("ATENDENTE");
                    comboCargo.setSelectedItem(cargo);

                    long unidadeAtualId = f.path("unidade").path("id").asLong(-1);
                    comboUnidade.removeAllItems();
                    idsUnidade.clear();
                    int indexSelecionado = 0;
                    for (int i = 0; i < unidades.size(); i++) {
                        JsonNode u = unidades.get(i);
                        long uid = u.path("id").asLong();
                        idsUnidade.add(uid);
                        comboUnidade.addItem(u.path("nomeUnidade").asText());
                        if (uid == unidadeAtualId) indexSelecionado = i;
                    }
                    if (comboUnidade.getItemCount() > 0) comboUnidade.setSelectedIndex(indexSelecionado);

                    botaoSalvar.setEnabled(true);
                } catch (Exception ex) {
                    labelErro.setText("Erro ao carregar dados do funcionário.");
                }
            }
        }.execute();
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String matricula = campoMatricula.getText().trim();
        String username = campoUsername.getText().trim();

        if (nome.isBlank() || matricula.isBlank() || username.isBlank()) {
            labelErro.setText("Nome, matrícula e username são obrigatórios.");
            return;
        }

        botaoSalvar.setEnabled(false);
        labelErro.setText(" ");

        long unidadeId = idsUnidade.get(comboUnidade.getSelectedIndex());
        String senha = new String(campoPassword.getPassword()).trim();

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("telefone", valorOuNulo(campoTelefone));
        corpo.put("email", valorOuNulo(campoEmail));
        corpo.put("matricula", matricula);
        corpo.put("username", username);
        corpo.put("cargo", comboCargo.getSelectedItem());
        corpo.put("unidadeId", unidadeId);
        if (!senha.isEmpty()) corpo.put("password", senha);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                funcionarioService.atualizar(funcionarioId, corpo);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    aoSalvarComSucesso.run();
                    dispose();
                } catch (Exception ex) {
                    labelErro.setText(ApiException.isCausa(ex)
                            ? "Erro: " + ApiException.mensagemDe(ex)
                            : "Erro de conexão com o servidor.");
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
