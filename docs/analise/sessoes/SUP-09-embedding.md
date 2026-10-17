# SUP-9 — Embedding em telas do GTW

## Escopo

Permitir que dashboards do Superset sejam visualizados dentro do GTW
via iframe, com autenticação via guest token JWT gerado pelo backend Java.

## Artefatos

### Java

| Classe | Endpoint | Descrição |
|---|---|---|
| GuestTokenServlet | /MuralhaDigital/AnaliseGuestToken?dashboardId=N | Gera JWT guest token para embedding |

O token JWT contém:
- `user`: dados do usuário GTW
- `resources`: dashboard permitido
- `exp`: expiração em 1 hora
- Assinatura HMAC-SHA256 com SUPERSET_SECRET_KEY

### Frontend

| Arquivo | Descrição |
|---|---|
| analise/embedding.jsp | Tela com seletor de dashboard e iframe |
| analise/assets/js/embedding.js | Lógica de carregamento e guest token |

### Configuração superset_config.py

| Configuração | Valor |
|---|---|
| EMBEDDED_SUPERSET | True (já existia) |
| TALISMAN_ENABLED | False (permite iframe) |
| HTTP_HEADERS.X-Frame-Options | ALLOWALL |
| CORS_OPTIONS | Permite origem GTW_BASE_URL |
| GUEST_TOKEN_JWT_EXP_SECONDS | 3600 |

## Fluxo

1. Usuário acessa menu **Análises > Painéis** no GTW
2. Seleciona um dashboard no dropdown
3. JSP monta URL do Superset com `?standalone=3` (modo embedding)
4. iframe carrega o dashboard via nginx (auth_request valida sessão)
5. Botão "Abrir no Superset" disponível para acesso completo

## Tarefas

- [x] Criar GuestTokenServlet com geração de JWT
- [x] Criar embedding.jsp com seletor e iframe
- [x] Criar embedding.js
- [x] Configurar CORS e X-Frame-Options no Superset
- [x] Build verde
- [ ] Testar embedding no navegador (requer GTW + Superset Docker)
- [ ] Validar guest token com Superset
