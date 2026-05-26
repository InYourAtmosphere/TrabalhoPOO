# Documentação do Modelo de Domínio (Proposto)

O sistema **AlugaFácil** foi projetado com uma estrutura de classes profissional, utilizando conceitos de herança e polimorfismo para garantir flexibilidade e organização.

## 🌳 Estrutura de Herança

Para especializar o comportamento do sistema, a modelagem prevê o uso de herança nas seguintes entidades:

### 1. Clientes (`Cliente`)
A classe base será abstrata, permitindo o tratamento polimórfico de diferentes tipos de clientes:
*   **PessoaFísica**: Adiciona atributos como `CPF` e `CNH`.
*   **PessoaJurídica**: Adiciona atributos como `CNPJ` e `Razão Social`.
*   *Vantagem:* Centraliza dados comuns (nome, email, telefone) e permite validações específicas para cada tipo de documento.

### 2. Veículos (`Veiculo`)
Uma classe abstrata que define o contrato básico para a frota:
*   **Carro**: Atributos específicos como `número de portas` e `ar condicionado`.
*   **Moto**: Atributos como `cilindradas`.
*   *Vantagem:* Facilita a implementação de lógicas distintas para cálculo de seguro, manutenção e depreciação.

## 🏗️ Entidades de Negócio

*   **Contrato**: A peça central que vincula um `Cliente`, um `Veiculo` e as `Unidades` (locais de retirada e devolução).
*   **Unidade**: Representa as filiais físicas da locadora, permitindo o controle de estoque e transferências de veículos.
*   **Manutenção**: Registra o histórico de reparos, categorizados em Preventiva, Corretiva ou Preditiva.

## 🛠️ Padrões e Tecnologias
*   **Lombok**: Utilizado para manter o código limpo, gerando automaticamente Getters, Setters e Construtores através de anotações.
*   **Spring Boot**: Gerencia o ciclo de vida da aplicação e expõe os serviços via API REST.
