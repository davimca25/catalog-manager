# 🛒 Projeto E-Commerce - Microsserviços Orientados a Eventos

## 📚 Sobre o Projeto
Este projeto é um sistema de e-commerce robusto construído sobre uma arquitetura de microsserviços baseada em eventos (Event-Driven Architecture). O ecossistema foi projetado utilizando o padrão **Saga (Choreography)** para garantir o desacoplamento, a alta disponibilidade e a consistência dos dados através de comunicação assíncrona.

##  Arquitetura
A comunicação entre os domínios de negócio foi desenhada com total isolamento de responsabilidades e bancos de dados (Database per Service). O fluxo principal segue a estrutura:

`Auth -> Order Producer -> Batch (Producer/Consumer) -> Stock Consumer`

### Microsserviços
*   **Auth-Service (Porta 8080):** Guardião do sistema. Responsável por validar credenciais de usuários e emitir tokens JWT (*JSON Web Token*) de forma 100% *stateless* via HTTP/REST. 
*   **Order-Service (Porta 8081):** Ponto de entrada (Producer). Recebe pedidos autenticados pelo JWT, salva o estado inicial na base como `PENDING` e publica o evento da compra na fila do RabbitMQ.
*   **Batch-Service (Porta 8082):** Motor de processamento (Consumer & Producer). Escuta a fila de pedidos, processa as regras de negócio em lotes utilizando **Spring Batch**, guarda os dados de *staging* temporários no MongoDB, e notifica o estoque sobre a conclusão.
*   **Stock-Service (Porta 8083):** Autoridade de saldos (Consumer Final). Escuta o evento de lote processado e realiza a baixa (decremento de quantidade) do produto em seu banco de dados exclusivo.

##  Infraestrutura Local (Docker Compose)
Toda a infraestrutura de dados e mensageria é gerenciada via Docker. O arquivo `docker-compose.yml` unificado na raiz do repositório sobe os seguintes serviços:

*   **Bancos de Dados PostgreSQL Isolados:**
    *   `postgres-auth` (Porta 5433)
    *   `postgres-order` (Porta 5434)
    *   `postgres-batch` (Porta 5435) - Armazena os metadados internos do Spring Batch.
    *   `postgres-stock` (Porta 5436)
*   **Banco de Dados NoSQL:**
    *   `mongo-batch` (Porta 27017) - Área de *staging* de documentos para leitura em lotes.
*   **Mensageria:**
    *   `rabbitmq` (Porta 5672 para conexões, 15672 para painel de gerenciamento).

**Para iniciar os serviços localmente:**
```bash
docker-compose up -d
```

##  Integração Contínua (CI)
O projeto implementa uma esteira de CI nativa utilizando o GitHub Actions.

* A cada novo commit ou Pull Request na branch main, o workflow isola a navegação no Monorepo.

* Contêineres temporários de PostgreSQL e MongoDB são ativados dinamicamente na nuvem.

* O comando mvn clean package é executado, validando a compilação do código e testando o comportamento com JUnit e Mockito de ponta a ponta, bloqueando qualquer quebra no repositório.

##  Tecnologias Utilizadas
Linguagem & Framework: Java 21, Spring Boot 3.x

* Segurança: Spring Security, JWT (JJWT)

* Processamento em Lote: Spring Batch

* Mensageria: RabbitMQ (Spring AMQP)

Persistência: Spring Data JPA, PostgreSQL Driver, Spring Data MongoDB

Utilitários: Lombok

DevOps & Automação: Docker, Docker Compose, GitHub Actions
