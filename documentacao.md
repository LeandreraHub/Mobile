---
title: "Lista de Contatos — Documentação do Projeto"
author: "Leandro Cortes Rezende"
---

# Lista de Contatos

Aplicativo móvel desenvolvido para a disciplina de Programação Mobile.

**Repositório no GitHub:** https://github.com/LeandreraHub/Mobile

# 1. Objetivo

O aplicativo permite que cada usuário crie sua própria conta, faça login e gerencie sua lista pessoal de contatos (nome, telefone e e-mail), com operações completas de cadastro, leitura, edição e exclusão (CRUD). Todos os dados são armazenados localmente no próprio dispositivo, em um banco de dados SQLite.

# 2. Ferramentas e tecnologias utilizadas

| Ferramenta / Tecnologia | Finalidade |
|---|---|
| **Android Studio** | IDE oficial do Google para desenvolvimento Android. Usado para compilar, executar e depurar o aplicativo. |
| **Kotlin** | Linguagem de programação principal do projeto, oficial para desenvolvimento Android. |
| **Jetpack Compose** | Framework moderno do Android para construção de interfaces (UI) de forma declarativa, usado em todas as telas do app. |
| **SQLite (android.database.sqlite)** | Banco de dados local embutido no Android, usado para persistir usuários e contatos no dispositivo. Acessado através da classe `SQLiteOpenHelper`. |
| **Gradle / Gradle Wrapper** | Ferramenta de build do projeto Android, gerencia dependências e compilação. |
| **Git e GitHub** | Controle de versão e hospedagem do código-fonte. |
| **ADB / Depuração USB** | Usado para instalar e executar o aplicativo diretamente em um celular físico conectado via cabo USB, sem necessidade de emulador. |

# 3. Arquitetura do projeto

O código-fonte está organizado da seguinte forma, dentro de `app/src/main/java/com/example/listadecontatos/`:

```
data/
  Models.kt           -> classes de dados Usuario e Contato
  DatabaseHelper.kt    -> classe que cria e gerencia o banco SQLite (CRUD)
ui/
  LoginScreen.kt        -> tela de login
  CadastroScreen.kt     -> tela de criação de conta
  ListaContatosScreen.kt -> tela com a lista de contatos do usuário logado
  ContatoFormScreen.kt  -> formulário único usado tanto para criar quanto editar um contato
  theme/                -> arquivos de tema visual (cores, tipografia) gerados pelo Android Studio
MainActivity.kt          -> ponto de entrada do app; controla a navegação entre as telas
```

Não foi utilizada nenhuma biblioteca de navegação ou de banco de dados (como Room ou Navigation Compose) propositalmente: a navegação entre telas é feita por uma variável de estado simples (`Tela`) dentro do `MainActivity.kt`, e o acesso ao SQLite é feito diretamente pela API nativa do Android (`SQLiteOpenHelper`). Essa escolha manteve o projeto mais simples de entender e sem dependências externas além das já incluídas pelo próprio Android Studio.

# 4. Banco de dados (SQLite)

O banco se chama `lista_contatos.db` e é criado automaticamente na primeira execução do app, dentro do armazenamento privado do aplicativo no celular. Ele contém duas tabelas:

### Tabela `usuarios`
| Coluna | Tipo | Descrição |
|---|---|---|
| id | INTEGER (PK, autoincremento) | identificador único do usuário |
| nome | TEXT | nome do usuário |
| email | TEXT (único) | e-mail usado para login |
| senha_hash | TEXT | hash SHA-256 da senha (a senha nunca é guardada em texto puro) |

### Tabela `contatos`
| Coluna | Tipo | Descrição |
|---|---|---|
| id | INTEGER (PK, autoincremento) | identificador único do contato |
| usuario_id | INTEGER | chave estrangeira para `usuarios.id` — garante que cada usuário só veja seus próprios contatos |
| nome | TEXT | nome do contato |
| telefone | TEXT | telefone do contato |
| email | TEXT | e-mail do contato (opcional) |

# 5. Como funciona o aplicativo (fluxo de uso)

1. **Tela de Login** — usuário já cadastrado informa e-mail e senha. Se as credenciais batem com o hash salvo no banco, é redirecionado para a lista de contatos.
2. **Tela de Cadastro** — novo usuário informa nome, e-mail e senha (com confirmação). A senha é transformada em hash SHA-256 antes de ser salva no banco — assim, mesmo quem tiver acesso ao arquivo do banco não vê a senha original.
3. **Tela de Lista de Contatos** — mostra todos os contatos cadastrados pelo usuário logado (somente os dele, filtrados por `usuario_id`). Possui:
   - botão flutuante "+" para adicionar um novo contato
   - botão "Editar" em cada contato, que abre o formulário preenchido
   - botão "Excluir" em cada contato, que pede confirmação antes de excluir
   - botão "Sair" para fazer logout e voltar à tela de login
4. **Tela de Formulário** — usada tanto para criar um contato novo quanto para editar um existente (o mesmo formulário é reaproveitado nos dois casos).

# 6. Segurança

- As senhas nunca são armazenadas em texto puro: são transformadas em hash SHA-256 antes de serem gravadas no banco.
- O e-mail é definido como campo único (`UNIQUE`) na tabela `usuarios`, impedindo cadastro duplicado.
- Cada contato é vinculado ao `usuario_id` de quem o criou, então um usuário nunca vê os contatos de outro usuário.

# 7. Como executar o projeto

1. Clonar o repositório: `git clone https://github.com/LeandreraHub/Mobile.git`
2. Abrir a pasta do projeto no Android Studio (`File > Open`)
3. Aguardar a sincronização do Gradle
4. Conectar um celular Android via cabo USB com a Depuração USB ativada (ou usar um emulador)
5. Selecionar o dispositivo no topo da tela e clicar no botão verde "Run" (▶)

# 8. Processo de desenvolvimento

O ambiente de desenvolvimento foi montado em um computador com poucos recursos, então o aplicativo foi testado executando diretamente em um celular físico (Samsung SM-A546E) conectado via cabo USB com Depuração USB ativada, evitando o uso do emulador do Android Studio (que exige muito mais memória RAM e processamento).

O código foi escrito com auxílio de IA (Claude Code), com testes de build feitos via linha de comando (`gradlew assembleDebug`) e testes funcionais feitos manualmente no celular (cadastro, login, criação, edição e exclusão de contatos, e logout).
