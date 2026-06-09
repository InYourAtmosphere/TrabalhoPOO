CREATE TABLE IF NOT EXISTS unidades (
    id BIGSERIAL PRIMARY KEY,
    nome_unidade VARCHAR(100) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(50),
    cep VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    documento_identidade VARCHAR(50) NOT NULL,
    documento_habilitacao VARCHAR(50),
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(50),
    cep VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    cargo VARCHAR(50),
    unidade_id BIGINT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_funcionario_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS veiculos (
    id BIGSERIAL PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano INTEGER NOT NULL,
    placa VARCHAR(10) UNIQUE NOT NULL,
    chassi VARCHAR(50) UNIQUE NOT NULL,
    km_atual DOUBLE PRECISION DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_veiculo VARCHAR(20) NOT NULL,
    qtd_portas INTEGER,
    tem_ar_condicionado BOOLEAN,
    cilindrada INTEGER,
    tem_bau BOOLEAN,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS contratos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    veiculo_id BIGINT NOT NULL,
    unidade_retirada_id BIGINT NOT NULL,
    unidade_devolucao_id BIGINT,
    data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim_prevista TIMESTAMP NOT NULL,
    data_fim_real TIMESTAMP,
    valor_diaria DOUBLE PRECISION NOT NULL,
    valor_total DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_contrato_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_contrato_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
    CONSTRAINT fk_contrato_retirada FOREIGN KEY (unidade_retirada_id) REFERENCES unidades(id),
    CONSTRAINT fk_contrato_devolucao FOREIGN KEY (unidade_devolucao_id) REFERENCES unidades(id)
);

CREATE TABLE IF NOT EXISTS manutencoes (
    id BIGSERIAL PRIMARY KEY,
    veiculo_id BIGINT NOT NULL,
    data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP,
    descricao TEXT,
    custo DOUBLE PRECISION DEFAULT 0,
    tipo VARCHAR(20) NOT NULL,
    CONSTRAINT fk_manutencao_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS authentication_tokens (
    token UUID PRIMARY KEY,
    funcionario_id BIGINT NOT NULL,
    expira_em BIGINT NOT NULL,
    CONSTRAINT fk_token_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE
);

INSERT INTO funcionarios (nome, email, username, password, matricula, cargo)
SELECT 'Administrador', 'admin@alugafacil.com', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADM001', 'Gerente'
WHERE NOT EXISTS (SELECT 1 FROM funcionarios);

-- Senha padrão: admin123 --