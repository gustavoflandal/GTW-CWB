# Relatório CH-3 — SMTP dos chamados pelo administrador

## Origem

Sessão: [CH-3-smtp.md](../sessoes/CH-3-smtp.md)
Plano: [22-sistema-chamados.md](../../planos-salvador/22-sistema-chamados.md) (Tarefa 19)

Data: 2026-10-15

## Resultado

O administrador configura servidor, porta, usuário, senha, remetente, STARTTLS e o liga/desliga dos e-mails na aba "E-mail (SMTP)" da Configuração e envia um e-mail de teste para si. A senha é gravada cifrada (AES/GCM, formato `v1:`) com a chave da variável de ambiente `GTW_CHAMADO_CHAVE_SMTP` e nunca sai pela API, por logs, histórico ou auditoria. O envio dos chamados passou a usar JavaMail próprio (`EmailChamado` com `ConfiguracaoSmtpChamado`); `ServicoEmailMuralha.java` voltou ao estado anterior ao plano.

## Commits

Intervalo `cf719a0..579b62d`:

| Commit | Assunto |
|---|---|
| `cf719a0` | Acrescenta ao plano 22 a configuracao de SMTP dos chamados |
| `db7ecd6` | Adiciona configuracao do SMTP dos chamados pelo administrador |
| `579b62d` | Documenta a configuracao do SMTP dos chamados e a chave GTW_CHAMADO_CHAVE_SMTP |

## Testes

- 123 testes TestNG passando (bateria completa; 25 novos: 8 de `CifraSegredoTest` e 17 de `ConfiguracaoSmtpChamadoTest`, entre válidos e inválidos).
- Build `BUILD SUCCESS` e `node --check` do script da tela sem erros.
- Teste integrado por HTTP: senha gravada como `v1:` (71 caracteres); o GET não a devolve; senha em claro com 0 ocorrências em `config_chave_valor`, `config_chave_valor_hist`, `sis_log_auditoria` e no log do servidor; senha em branco mantém a atual; validações de porta e host recusam valores inválidos; sem a variável no servidor a senha nova é recusada; o e-mail de teste com host inválido devolve erro legível; configuração devolvida ao `confGTW.xml` ao final.
- Migração `_smtp` executada duas vezes (6 linhas, depois 0).

## Decisões tomadas

- **Ruling 15:** SMTP configurável na tela, com senha cifrada por AES-GCM no banco e chave em `GTW_CHAMADO_CHAVE_SMTP` (escolha do responsável); `EmailChamado` passa a enviar com JavaMail próprio e o método `enviarEmailChamado` sai de `ServicoEmailMuralha` (arquivo legado volta ao original). Se estiver errado: reintroduzir o método.
- Host vazio mantém o servidor do `confGTW.xml` (comportamento anterior).
- O histórico registra host, usuário e remetente em claro, como nos parâmetros de SLA; da senha registra só `alterada` ou `removida`.
- Senha limitada a 120 caracteres para caber em `varchar(255)` depois de cifrada.

## Pendências

- Configurar um SMTP válido (na aba ou no `confGTW.xml`) e definir `GTW_CHAMADO_CHAVE_SMTP` no servidor para confirmar o envio real; no ambiente local o servidor do `confGTW.xml` recusa a autenticação.
- Trocar ou perder a chave obriga a regravar a senha (ver `docs/requisitos/07-configuracao-e-segredos.md`).
- Menores conhecidos: `chamado_smtp_starttls` fica em `1` depois do teste (equivalente a vazio, que também liga o STARTTLS); a auditoria de `SALVAR_SMTP` com "Apagar a senha" diz "senha alterada: sim" (o histórico distingue "removida").
