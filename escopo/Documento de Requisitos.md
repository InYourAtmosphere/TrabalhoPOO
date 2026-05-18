## Requisitos Funcionais (RF)

---

### RF01: Instanciação e Cadastro de Veículos
* **Rastreabilidade:** Ator: Atendente
* **Descrição:** O sistema deve permitir instanciar (cadastrar) novos veículos, registrando dados como placa, modelo, ano, chassi e unidade física de destino.
* **Critério de Aceite (CA):**
    * O sistema deve tornar o veículo visível para locação e monitoramento imediato após a sua instanciação.

### RF02: Edição de Dados e Modificação de Status da Frota
* **Rastreabilidade:** Ator: Atendente
* **Descrição:** O sistema deve permitir a edição de dados para modificar status de veículos, como "em manutenção" ou "disponível".
* **Critério de Aceite (CA):**
    * O sistema deve permitir atualizar a quilometragem atual e anexar novos documentos aos veículos cadastrados.

### RF03: Transferência Lógica entre Unidades e Controle de Estoque
* **Rastreabilidade:** Ator: Gestor
* **Descrição:** O sistema deve permitir a transferência lógica de veículos entre diferentes unidades da locadora.
* **Critério de Aceite (CA):**
    * O sistema deve garantir um controle de estoque fluido e fornecer a cada gerente regional a visibilidade exata da frota sob sua responsabilidade.

### RF04: Monitoramento de Disponibilidade em Tempo Real
* **Rastreabilidade:** Ator: Atendente
* **Descrição:** O sistema deve prover visibilidade em tempo real da disponibilidade de veículos para aluguel.
* **Critério de Aceite (CA):**
    * O sistema deve garantir que os dados reflitam sempre o estado físico real do carro, evitando que fiquem obsoletos.

### RF05: Módulo de Inteligência Artificial e Análise Preditiva
* **Rastreabilidade:** Ator: Gestor
* **Descrição:** O sistema deve utilizar Inteligência Artificial para prever a depreciação de veículos e realizar manutenções preditivas.
* **Critério de Aceite (CA):**
    * O sistema deve consolidar as predições geradas por IA em relatórios automatizados para antecipar paradas técnicas e perdas de valor patrimonial.

### RF06: Módulo de Comunicação Automatizada com o Cliente
* **Rastreabilidade:** Ator: Atendente
* **Descrição:** O sistema deve realizar comunicações automatizadas com o cliente por meio de mensageiros como o WhatsApp.
* **Critério de Aceite (CA):**
    * Os disparos de mensagens devem ocorrer de forma automatizada baseando-se em eventos do ciclo de vida dos contratos e alertas de manutenção.

### RF07: Dashboard Estratégico de Tomada de Decisão
* **Rastreabilidade:** Ator: Gestor
* **Descrição:** O sistema deve fornecer uma visão gerencial clara e unificada de toda a frota e fornecer dashboards em tempo real para auxiliar a diretoria em decisões estratégicas e substituir suposições.
* **Critério de Aceite (CA):**
    * A interface deve unificar os dados gerenciais essenciais para que a diretoria visualize os indicadores de todas as unidades simultaneamente em tempo real.

---
## Requisitos Não Funcionais (RNF)
---

### RNF01: Centralização de Plataforma Única
* **Descrição Técnica:** O sistema deve eliminar o uso de planilhas manuais e integrar todas as unidades em uma plataforma única.
* **Critério de Aceite (CA):**
    * A arquitetura da aplicação deve unificar o fluxo de trabalho de todas as filiais regionais em um ambiente compartilhado único, sem dependência de repositórios locais apartados.

### RNF02: Centralização e Integridade de Dados
* **Descrição Técnica:** O sistema deve centralizar e garantir a integridade das informações sobre quilometragem, manutenção e contratos.
* **Critério de Aceite (CA):**
    * A camada de persistência deve aplicar restrições transacionais e validações rígidas para mitigar inconsistências e garantir a conformidade dos dados históricos dos veículos.