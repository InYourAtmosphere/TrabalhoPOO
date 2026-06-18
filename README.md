# AlugaFácil - Sistema de Aluguel de Veículos

Trabalho de Programação Orientada a Objetos (POO).

**Autores:** Finéias Antônio, Gabriela Brasileiro, Heitor M A Vaz, Túlio Vitorette.

## 🏗️ Estrutura do Projeto

O projeto é dividido em dois módulos Maven **independentes**, que só se comunicam via HTTP/REST:

*   **[`server/`](./server)**: API REST em Spring Boot. Concentra toda a regra de negócio, acesso ao banco (JDBC) e autenticação por token.
*   **[`client/`](./client)**: Aplicação desktop em Swing. Consome a API do `server` exclusivamente via HTTP (`java.net.http.HttpClient` + Jackson) — não acessa o banco de dados nem compartilha código de domínio com o servidor.

Cada módulo tem seu próprio `pom.xml` e pode ser compilado/rodado separadamente, inclusive em máquinas diferentes.

## 🛠️ Tecnologias Utilizadas

*   **Java 17**
*   **Spring Boot 3.3** (server)
*   **Swing** (client)
*   **PostgreSQL**
*   **Docker & Docker Compose**
*   **Lombok**: Biblioteca utilizada para reduzir código repetitivo (Getters/Setters).

## 📖 Documentação do Domínio

Modelagem do domínio 👉 [**DOMAIN_MODEL.md**](./DOMAIN_MODEL.md)

## 🚀 Como Rodar o Projeto

### Pré-requisitos

*   **Java 17** ou superior.
*   **Maven** 3.x.
*   **PostgreSQL** rodando localmente (ou via Docker, veja abaixo).

### 1. Configurar o Banco de Dados

Crie um banco de dados no PostgreSQL chamado `trabalhopoo`.
As configurações padrão no arquivo `server/src/main/java/org/poo/config/DatabaseConfig.java` são:

*   **URL:** `jdbc:postgresql://localhost:5432/trabalhopoo`
*   **Usuário:** `user`
*   **Senha:** `password`

O schema e os dados de exemplo são criados automaticamente na inicialização (`server/src/main/resources/schema.sql`).

### 2. Rodar o Servidor (API REST)

Em um terminal, na pasta `server/`:

```bash
cd server
mvn spring-boot:run
```

O servidor iniciará por padrão na porta **8081**.

### 3. Rodar o Cliente (Desktop)

Em **outro terminal**, na pasta `client/`:

```bash
cd client
mvn compile exec:java
```

> **Nota:** use `mvn compile exec:java` (não só `mvn exec:java`). O goal `exec:java` chamado isolado não compila o código antes — sem o `compile`, dá `ClassNotFoundException: org.poo.Main` se o `target/` ainda não existir.

Isso abrirá a tela de login do Swing, que se conecta ao servidor via HTTP em `http://localhost:8081`.

Se o servidor estiver rodando em outra máquina/porta, aponte o cliente para ele sem recompilar:

```bash
mvn compile exec:java -Dalugafacil.api.url=http://IP_DO_SERVIDOR:8081
```

(ou defina a variável de ambiente `ALUGAFACIL_API_URL`).

Para gerar um `.jar` executável do cliente (com todas as dependências embutidas) e distribuí-lo sem precisar do Maven:

```bash
cd client
mvn package
java -Dalugafacil.api.url=http://IP_DO_SERVIDOR:8081 -jar target/AlugaFacilClient-1.0-SNAPSHOT.jar
```

## 🐳 Como Rodar o Banco com Docker

O projeto inclui um arquivo `docker-compose.yml` na raiz para facilitar a configuração do ambiente de banco de dados.

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

### 3. Rodar o Servidor e o Cliente

Com o banco ativo no Docker, siga os passos 2 e 3 da seção [Como Rodar o Projeto](#-como-rodar-o-projeto) acima.