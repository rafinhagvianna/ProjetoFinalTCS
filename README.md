# ⚙️ BankFlow — Backend

Este é o repositório exclusivo do **backend** da aplicação BankFlow, responsável por toda a lógica de negócios, autenticação, gestão de filas e agendamentos bancários. Desenvolvido com **Spring Boot (Java 21)**, o sistema prioriza segurança, performance e escalabilidade.

## 🚀 Funcionalidades

- Sistema de triagem e atendimento com lógica de prioridade
- Gerenciamento de agendamentos com data e horário
- Fila inteligente com temporizador (até 10 minutos)
- Autenticação via JWT
- Controle de chamados por parte dos funcionários
- API documentada e testada

## 🧰 Tecnologias Utilizadas

| Camada               | Tecnologias              |
|----------------------|--------------------------|
| **Backend**          | Spring Boot (Java 21)    |
| **Banco de Dados**   | SQL Server, MongoDB      |
| **Segurança**        | JWT                      |
| **Testes**           | Postman, Swagger         |
| **Modelação**        | Figma                    |
| **Infraestrutura**   | Oracle Cloud, Nginx      |
| **Versionamento**    | Git, GitHub              |
| **Metodologia Ágil** | Kanban                   |

## 🧪 Testes

- Testes de requisições via **Postman**
- Documentação interativa disponível com **Swagger**
- Fluxos de funcionamento modelados em **Figma**

## 📦 Execução local

```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/bankflow-backend.git

# Entrar na pasta
cd bankflow-backend

# Compilar o projeto
./mvnw clean install

# Executar
./mvnw spring-boot:run
