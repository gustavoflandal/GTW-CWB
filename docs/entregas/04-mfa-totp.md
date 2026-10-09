# Entrega — Plano 04: MFA/TOTP

**Data de entrega:** 2026-10-08  
**Commits:** `2500354` · `327d267` · `b38c1ec`  
**Status:** ✅ Entregue (migração SQL pendente de execução manual)

---

## O que foi entregue

### Banco de dados
- 2 novas colunas em `dbo.sis_usuario`: `totp_secret VARCHAR(200)`, `totp_habilitado BIT DEFAULT 0`
- 1 novo parâmetro em `sis_senha_config`: `mfa_obrigatorio` (0/1)
- Script: [`docs/banco-de-dados/migracoes/20261008_mfa_totp.sql`](../banco-de-dados/migracoes/20261008_mfa_totp.sql)

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/acesso/TotpService.java` | HMAC-SHA1 manual (RFC 6238), geração Base32, validação ±1 período |
| `muralha/digital/acesso/MfaConfigServlet.java` | `POST /MuralhaDigital/MfaConfig?acao=ativar\|desativar` |

### Front-end
| Arquivo | Responsabilidade |
|---|---|
| `webapp/muralha-digital/pages/meu-perfil/configurar-mfa.jsp` | Setup: QR Code + confirmação de código |
| `webapp/login/mfa_codigo.jsp` | Tela intermediária de inserção do código (após login) |
| `webapp/login/mfa_verificar.jsp` | Valida TOTP, completa criação da sessão definitiva |
| `webapp/login/login_action.jsp` | Intercepta login quando `totp_habilitado=1`, redireciona para MFA |

---

## Fluxo de autenticação com MFA

```
[login.jsp] → senha correta → [login_action.jsp]
                                    ↓ totp_habilitado=1?
                               SIM  ↓
                         [mfa_codigo.jsp] → código do app
                                    ↓
                         [mfa_verificar.jsp] → TotpService.validar()
                                    ↓ ok
                         Sessão definitiva criada → [abertura-sistemas.jsp]
                               NÃO  ↓
                         Sessão criada normalmente → [abertura-sistemas.jsp]
```

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Colunas `totp_secret` e `totp_habilitado` adicionadas | ⏳ Aguarda migração |
| 2 | `TotpService.validar()` compatível com Google Authenticator | ✅ (RFC 6238, HMAC-SHA1, janela ±1) |
| 3 | Tela de configuração exibe QR Code e valida código antes de ativar | ✅ |
| 4 | Login com MFA habilitado redireciona para `mfa_codigo.jsp` | ✅ |
| 5 | Código inválido exibe erro sem criar sessão | ✅ |
| 6 | Código válido completa a sessão e registra auditoria | ✅ |
| 7 | Sem MFA: fluxo de login inalterado | ✅ |
| 8 | Sessão temporária expira em 5 min se código não for inserido | ✅ |

---

## Decisões de arquitetura

- **Sem nova dependência:** TOTP implementado manualmente com `javax.crypto.Mac` (HmacSHA1) + Base32 próprio — nenhuma biblioteca adicionada
- **Opt-in:** usuários sem `totp_habilitado=1` não são afetados; query falha silenciosamente se colunas não existirem (migração não executada)
- **QR Code no cliente:** `qrcode.js` via CDN (cdnjs.cloudflare.com) — segredo nunca enviado para terceiros; apenas a URI `otpauth://` é passada para a biblioteca JavaScript
- **Segredo em plaintext:** versão inicial armazena o segredo sem criptografia em repouso — identificado como melhoria futura (usar `cript_chave` de `confGTW.xml`)

---

## Como testar

Após executar a migração, acesse `/muralha-digital/pages/meu-perfil/configurar-mfa.jsp` logado no sistema, escaneie o QR Code com um app autenticador e ative o MFA.

Testes detalhados: [`docs/testes/04-mfa-totp.md`](../testes/04-mfa-totp.md)
