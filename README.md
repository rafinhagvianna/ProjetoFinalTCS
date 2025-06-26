# ProjetoFinalTCS


### 🧭 Visão Geral do Projeto

**Objetivo:** Reduzir o tempo de espera em filas de banco por meio de um aplicativo que coleta informações prévias do cliente, orienta conforme o tipo de atendimento e organiza a fila com inteligência.

---

### 🧱 Estrutura Básica do Sistema

**Frontend (Angular):**
- Tela de login/cadastro
- Tela para escolha do serviço bancário
- Orientações e FAQs conforme o tipo de serviço escolhido
- Painel de status (posição na fila, estimativa de tempo)

**Backend (Spring Boot):**
- Autenticação com JWT
- APIs REST para registro e autenticação de usuários
- APIs para cadastro e consulta de solicitações
- Sistema de fila inteligente
- Dashboard para os atendentes verem quem está na fila e por quê

**Banco de Dados (por exemplo, PostgreSQL):**
- Usuários
- Tipos de serviço
- Solicitações
- Histórico de atendimentos

---

### ⛓️ Fluxo Principal do Usuário

1. **Login/Cadastro**
2. **Seleção do serviço bancário** (ex: abrir conta, renegociar dívida, saque, etc.)
3. **Orientações automáticas** (ex: documentos necessários)
4. **Fila virtual** com estimativa de espera
5. **Atendimento com base no perfil do cliente** – o funcionário acessa os dados antes mesmo do atendimento iniciar

---

### 🛠️ Recursos Adicionais

- **Notificações em tempo real** com WebSocket (ex: "você é o próximo")
- **Chat com atendente** ou bot para perguntas frequentes
- **Login com biometria ou token temporário** para agilidade

---

### 🚀 Primeiros Passos

1. **Criar o escopo técnico detalhado** (posso te ajudar com isso!)
2. **Definir as tecnologias complementares** (autenticação, banco, deploy, etc.)
3. **Montar os protótipos de telas com Figma ou similar**
4. **Iniciar desenvolvimento com módulos bem definidos**

---
