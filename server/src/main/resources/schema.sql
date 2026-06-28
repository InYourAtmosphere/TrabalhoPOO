PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS unidades (
    id INTEGER PRIMARY KEY,
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
    id INTEGER PRIMARY KEY,
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
    id INTEGER PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    cargo VARCHAR(50),
    unidade_id INTEGER,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_funcionario_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS veiculos (
    id INTEGER PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano INTEGER NOT NULL CHECK (ano >= 1900),
    placa VARCHAR(10) UNIQUE NOT NULL,
    chassi VARCHAR(50) UNIQUE NOT NULL,
    km_atual REAL NOT NULL DEFAULT 0 CHECK (km_atual >= 0),
    status VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_veiculo VARCHAR(20) NOT NULL,
    qtd_portas INTEGER,
    tem_ar_condicionado BOOLEAN,
    cilindrada INTEGER,
    tem_bau BOOLEAN,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    unidade_id INTEGER,
    CONSTRAINT fk_veiculo_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS contratos (
    id INTEGER PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    veiculo_id INTEGER NOT NULL,
    unidade_retirada_id INTEGER NOT NULL,
    unidade_devolucao_id INTEGER,
    data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim_prevista TIMESTAMP NOT NULL,
    data_fim_real TIMESTAMP,
    valor_diaria REAL NOT NULL CHECK (valor_diaria > 0),
    valor_total REAL,
    status VARCHAR(20) NOT NULL,
    km_inicial REAL CHECK (km_inicial IS NULL OR km_inicial >= 0),
    km_final REAL CHECK (km_final IS NULL OR km_final >= 0),
    forma_pagamento VARCHAR(50),
    CONSTRAINT fk_contrato_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_contrato_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
    CONSTRAINT fk_contrato_retirada FOREIGN KEY (unidade_retirada_id) REFERENCES unidades(id),
    CONSTRAINT fk_contrato_devolucao FOREIGN KEY (unidade_devolucao_id) REFERENCES unidades(id)
);

CREATE TABLE IF NOT EXISTS manutencoes (
    id INTEGER PRIMARY KEY,
    veiculo_id INTEGER NOT NULL,
    data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP,
    descricao TEXT,
    custo REAL NOT NULL DEFAULT 0 CHECK (custo >= 0),
    tipo VARCHAR(20) NOT NULL,
    CONSTRAINT fk_manutencao_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS authentication_tokens (
    token TEXT PRIMARY KEY,
    funcionario_id INTEGER NOT NULL,
    expira_em INTEGER NOT NULL,
    CONSTRAINT fk_token_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE
);

-- =============================================
-- SEED DATA
-- Senha padrão de todos os usuários: admin123
-- =============================================

-- Unidade
INSERT OR IGNORE INTO unidades (nome_unidade, logradouro, numero, bairro, cidade, estado, cep) VALUES
    ('Unidade Centro', 'Rua das Flores', '100', 'Centro', 'São Paulo', 'SP', '01310-100');

-- Funcionários (senha: admin123)
INSERT INTO funcionarios (nome, email, username, password, matricula, cargo, unidade_id) VALUES
    ('Administrador', 'admin@alugafacil.com',    'admin',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADM001', 'GERENTE',   1),
    ('Carlos Mendes', 'carlos@alugafacil.com',   'carlos',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ATD001', 'ATENDENTE', 1),
    ('Ana Souza',     'ana@alugafacil.com',      'ana',      '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ATD002', 'ATENDENTE', 1),
    ('Bruno Lima',    'bruno@alugafacil.com',    'bruno',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'GES001', 'GERENTE',   1),
    ('Sofia Ramos',   'sofia@alugafacil.com',    'sofia',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'SUP001', 'SUPERVISOR', 1)
ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
    cargo = EXCLUDED.cargo,
    unidade_id = EXCLUDED.unidade_id;

-- Clientes
INSERT OR IGNORE INTO clientes (nome, telefone, email, documento_identidade, documento_habilitacao, logradouro, numero, bairro, cidade, estado, cep) VALUES
    ('João da Silva',    '(11) 91234-5678', 'joao@email.com',    '123.456.789-00', 'CNH-SP-001', 'Rua A', '10', 'Centro',    'São Paulo', 'SP', '01001-000'),
    ('Maria Oliveira',   '(11) 92345-6789', 'maria@email.com',   '234.567.890-11', 'CNH-SP-002', 'Rua B', '20', 'Jardins',   'São Paulo', 'SP', '01401-000'),
    ('Pedro Alves',      '(11) 93456-7890', 'pedro@email.com',   '345.678.901-22', 'CNH-SP-003', 'Rua C', '30', 'Moema',     'São Paulo', 'SP', '04077-000'),
    ('Lucia Ferreira',   '(11) 94567-8901', 'lucia@email.com',   '456.789.012-33', 'CNH-SP-004', 'Rua D', '40', 'Tatuapé',   'São Paulo', 'SP', '03064-000');

-- Veículos
INSERT OR IGNORE INTO veiculos (marca, modelo, ano, placa, chassi, km_atual, status, tipo_veiculo, qtd_portas, tem_ar_condicionado, cilindrada, tem_bau, unidade_id) VALUES
    ('Volkswagen', 'Gol',        2021, 'ABC-1234', 'VW9BWZZZ3MT000001',  15000, 'DISPONIVEL',    'CARRO_POPULAR', 4,    1,    null, null,  1),
    ('Fiat',       'Argo',       2022, 'DEF-5678', 'ZFA19900003010002',  8000,  'DISPONIVEL',    'CARRO_POPULAR', 4,    1,    null, null,  1),
    ('Chevrolet',  'Onix',       2023, 'GHI-9012', '9BGXT48B0LG000003', 3000,  'LOCADO',        'CARRO_POPULAR', 4,    1,    null, null,  1),
    ('Honda',      'CG 160',     2022, 'JKL-3456', '9C2JC3110NR000004', 22000, 'DISPONIVEL',    'MOTOCICLETA',   null, null, 160,  0,     1),
    ('Yamaha',     'Factor 150', 2023, 'MNO-7890', 'LBM11J0BXPP000005', 5000,  'EM_MANUTENCAO', 'MOTOCICLETA',   null, null, 150,  1,     1);
