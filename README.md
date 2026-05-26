# AlugaFácil - Sistema de Aluguel de Veículos

Trabalho de Programação Orientada a Objetos (POO).

**Autores:** Finéias Antônio, Gabriela Brasileiro, Heitor M A Vaz, Túlio Vitorette.

## 🚀 Como Rodar o Projeto

### Pré-requisitos

*   **Java 17** ou superior.
*   **Maven** 3.x.
*   **PostgreSQL** rodando localmente.

### 1. Configurar o Banco de Dados

Crie um banco de dados no PostgreSQL chamado `trabalhopoo`.
As configurações padrão no arquivo `src/main/java/org/poo/config/DatabaseConfig.java` são:

*   **URL:** `jdbc:postgresql://localhost:5432/trabalhopoo`
*   **Usuário:** `user`
*   **Senha:** `password`

Certifique-se de que a tabela `veiculos` existe com a estrutura adequada (conforme definido no `VeiculoRepository`).

### 2. Compilar e Rodar

Na raiz do projeto, execute o seguinte comando para compilar e iniciar o servidor Spring Boot:

```bash
mvn spring-boot:run
```

O servidor iniciará por padrão na porta **8081** (ou conforme configurado para não conflitar com o pgAdmin).

## 🐳 Como Rodar com Docker

O projeto inclui um arquivo `docker-compose.yml` para facilitar a configuração do ambiente.

### 1. Iniciar os Serviços

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Isso iniciará:
*   **PostgreSQL**: O banco de dados para a aplicação (Porta 5432).
*   **pgAdmin**: Interface web para gerenciar o banco (Porta 8080).

### 2. Acessar o pgAdmin

Após subir os containers, você pode acessar o pgAdmin pelo navegador:

*   **URL:** `http://localhost:8080`
*   **Email:** `admin@admin.com`
*   **Senha:** `admin`

Para conectar ao banco dentro do pgAdmin:
1. Clique com o botão direito em "Servers" > "Register" > "Server...".
2. Na aba "General", dê um nome (ex: `LocalDB`).
3. Na aba "Connection", use:
    * **Host name/address:** `db` (se estiver na mesma rede Docker) ou `localhost`.
    * **Port:** `5432`
    * **Username:** `user`
    * **Password:** `password`

### 3. Rodar a Aplicação

Com o banco ativo no Docker, rode a aplicação Spring Boot:

```bash
mvn spring-boot:run
```