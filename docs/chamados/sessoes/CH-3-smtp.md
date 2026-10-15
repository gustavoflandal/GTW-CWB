# SESSAO CH-3 — SMTP dos chamados configurável pelo administrador

## Antes de qualquer código
1. Ler a Tarefa 19 e o complemento de SMTP em [`sistema-chamados-arquitetura.md`](../sistema-chamados-arquitetura.md).
2. Ler [`07-configuracao-e-segredos.md`](../../requisitos/07-configuracao-e-segredos.md): a chave de cifragem fica em variável de ambiente, nunca no repositório.

## Objetivo
Pedido do responsável: "As configurações do SMTP também deverão constar em configurações do sistema de chamados." O administrador define, na tela, o servidor usado para os e-mails dos chamados; a senha é guardada cifrada no banco.

## Tarefas do plano
Detalhamento em [`22-sistema-chamados.md`](../../planos-salvador/22-sistema-chamados.md) (Tarefa 19).

## Escopo
- Chaves `chamado_smtp_host`, `_porta`, `_usuario`, `_senha`, `_remetente`, `_starttls` em `muralha.config_chave_valor`, criadas vazias; host vazio mantém o servidor do `confGTW.xml`.
- `CifraSegredo` (AES/GCM do JDK, IV aleatório, formato `v1:` + Base64) com chave de 32 bytes em Base64 na variável `GTW_CHAMADO_CHAVE_SMTP`; senha limitada a 120 caracteres.
- `ConfiguracaoSmtpChamado`: resolução do SMTP efetivo e validação (host DNS ou IPv4, porta 1–65535, remetente, usuário).
- `EmailChamado` passa a enviar com JavaMail próprio (STARTTLS conforme a chave, autenticação só com usuário, tempo-limite de 10 s); o método `enviarEmailChamado` sai de `ServicoEmailMuralha`, que volta ao estado anterior ao plano.
- API `ChamadoConfig` (somente `ADMIN`): GET `smtp`, POST `salvarSmtp`, POST `testarSmtp`; auditoria `SALVAR_SMTP` e `TESTAR_SMTP`; a senha nunca sai pela API, por logs, histórico ou auditoria.
- Aba "E-mail (SMTP)" na Configuração, com o botão "Enviar e-mail de teste".

## Arquivos
- `docs/banco-de-dados/migracoes/20261015_sistema_chamados_smtp.sql`
- `CifraSegredo.java`, `ConfiguracaoSmtpChamado.java`, `EmailChamado.java`, `ChamadoEmailJob.java`, `ChamadoConfigDao.java`, `ChamadoConfigServlet.java`
- `pages/chamados/configuracao.jsp`, `assets/js/chamados/chamados-configuracao.js`
- Testes `CifraSegredoTest`, `ConfiguracaoSmtpChamadoTest`
- `docs/requisitos/07-configuracao-e-segredos.md`, entrega, roteiro de testes e arquitetura

## Critérios de aceite
- Cifra: ida e volta, IVs diferentes a cada cifragem, adulteração, chave errada e prefixo desconhecido rejeitados; 120 caracteres cabem em `varchar(255)`.
- Validação do SMTP cobrindo casos válidos e inválidos; bateria completa de testes passando.
- Teste integrado por HTTP: senha gravada com prefixo `v1:`, GET sem a senha, nenhuma ocorrência da senha em claro no banco, histórico, auditoria ou log do servidor; sem a chave no servidor, senha nova recusada; configuração devolvida ao `confGTW.xml` ao final.
- Migração idempotente; nenhum segredo no repositório; `ServicoEmailMuralha.java` idêntico ao do `main`.

## Resultado
Ver [`SESSAO-CH-3-relatorio.md`](../relatorios/SESSAO-CH-3-relatorio.md).
