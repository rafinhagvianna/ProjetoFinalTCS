# ProjetoFinalTCS
---

### 🌐 Visão Geral do Sistema

**Componentes principais:**
- **Portal do Cliente**: Agendamento de serviços, preenchimento de informações prévias e acompanhamento do atendimento.
- **Sistema de Triagem**: Avalia prioridade e direciona o atendimento com base no tipo de serviço.
- **Painel do Funcionário (Backoffice)**: Acesso à agenda, triagens, histórico e atendimento personalizado.

---

### 📐 Arquitetura Geral

**Camadas do sistema:**
- **Frontend (Angular)**  
- **API REST (Spring Boot)**  
- **Banco de Dados (MongoDB e SQL Server)**  
- **Autenticação** via JWT  
- **Hospedagem** em ambiente cloud (ex: Azure ou AWS)
- **Documentação das Requisições**  (Swagger)
- **Metodologia Ágil** (Kanban)

---

### 📋 Funcionalidades

#### 1. Cliente (Agendamento e Triagem)
- Login / Cadastro
- Escolha do serviço bancário (ex: abertura de conta, crédito, atendimento com gerente)
- Sugestão de horário disponível
- Preenchimento de dados pré-triagem (documentos, necessidades, perfil)
- Confirmação e lembrete por e-mail/SMS
- Acompanhamento em tempo real do atendimento

#### 2. Triagem Inteligente
- Classificação do atendimento (complexidade, prioridade)
- Agrupamento por setor/responsável
- Encaminhamento para a fila correta
- Estatísticas de tempo médio por serviço

#### 3. Funcionário do Banco
- Login com perfil autorizado
- Visualização da agenda de atendimentos e fila de espera
- Acesso à ficha do cliente e respostas da triagem
- Início e encerramento de atendimentos
- Histórico e relatórios de produtividade

---

### ⚙️ Stack Tecnológica

#### Backend: Spring Boot
- Spring Web (REST API)
- Spring Security (JWT)
- Spring Data JPA
- SQL Server Driver
- Lombok

#### Frontend: Angular
- Angular CLI 17+
- Angular Material
- NgRx (opcional para estados complexos)
- Integração com API via HttpClient

#### Banco de Dados: SQL Server
- Tabelas: `users`, `appointments`, `services`, `triage_answers`, `employees`, `roles`, `sessions`, `notifications`

---

### 🔄 Integrações

- API de envio de notificações (Twilio, SendGrid)
- Login com autenticação multifator (opcional)
- Painel administrativo com dashboards (ex: PrimeNG ou ngx-charts)

---

**Planilhas do Projeto**

- [Planilha de microsserviços](https://docs.google.com/spreadsheets/d/1jcfHNCjiN7VcZw7rTpQ5dDsCOEcvBZwlGwqWPyVROG8/edit?usp=sharing)

### 🛠️ Etapas de Implementação

| Fase | Entregas principais |
|------|---------------------|
| 1. Levantamento | Mapeamento dos serviços, perfis e jornadas |
| 2. Design | Prototipação do frontend, estrutura da API |
| 3. Backend | Desenvolvimento da API REST com validações e segurança |
| 4. Frontend | Interface responsiva com Angular |
| 5. Integração | Comunicação entre front, API e banco |
| 6. Testes | Unitários, integração, performance |

