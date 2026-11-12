# Entrega — Captura da ação de Imprimir no Motor de Logs

Data: 2026-11-12
Status: 🟡 Implementado — aguardando teste manual dos dois caminhos (PDF de relatório e impressão de tela) e execução da migração

## Origem

Desenho aprovado: [design-captura-impressao.md](../captura-logs/design-captura-impressao.md). Motor de referência: [motor-captura-logs.md](../captura-logs/motor-captura-logs.md). Não há plano numerado em `docs/planos-salvador/` para este item.

## Escopo

A ação de imprimir passa a ser um tipo de evento próprio do motor, `IMPRESSAO`, distinto de `EXPORTACAO`. Cada evento registra o formato, o documento impresso e a tela de origem, e pode ser configurado pela tela de regras de captura, sem código por relatório.

Ficam fora desta entrega: captura do conteúdo impresso, bloqueio de impressão, reescrita dos relatórios server-side e nova tela de consulta.

## O que mudou

### 1. Tipo de evento `IMPRESSAO`

- Novo código `IMPRESSAO` em `dbo.sis_log_tipo_evento`, com liga/desliga global na tela de tipos.
- `AuditoriaService.derivarNivel` passou a reconhecer o nível `IMPRESSAO`.
- A retenção herda a política padrão do motor, sem política própria em `sis_log_retencao` (decisão do cliente, §9, item 3 do desenho).

### 2. Caminho 1: documentos gerados no servidor (PDF)

- `AuditoriaService.capturarAutomatico` considera o `Content-Type` da resposta. Resposta `application/pdf` gera nível `IMPRESSAO`. CSV e XLSX continuam `EXPORTACAO`.
- O formato é derivado do Content-Type (`PDF`).
- O documento é a funcionalidade já derivada da rota (`derivarFuncionalidade`), sem instrumentar os relatórios.
- A tela de origem vem do cabeçalho `Referer` (somente o caminho, sem host e sem query). Sem `Referer`, ou com `Referer` malformado, usa-se a rota como fallback.
- Uma regra explícita em `sis_log_regra` com nível definido sempre prevalece sobre a derivação.
- Semântica: registra-se a geração do PDF como a impressão do documento (ver Limitações).

### 3. Caminho 2: impressão de tela pelo navegador

- Script compartilhado `impressao-captura.js`: escuta o evento `beforeprint`, que dispara em Ctrl+P e no menu do navegador. Envia `telaOrigem` (caminho da URL + título da página) e `formato` (`Impressao de tela`) por `navigator.sendBeacon`, com fallback para `fetch` com `keepalive`. Há um intervalo de 1 segundo para evitar registro duplicado.
- O gancho é incluído no cabeçalho comum das telas novas (`cabecalho_bootstrap_simples.jsp`).
- Endpoint `POST /MuralhaDigital/Impressao` (`ImpressaoServlet`): registra apenas com usuário autenticado na sessão e sempre responde 204. Não usa `ControleAcesso` estrito nem `sis_menu`. Essa é uma exceção decidida e registrada no desenho (design §6.6, "Acesso"): o endpoint é um registro interno de log, exige sessão autenticada, não expõe dados e não é uma tela, por isso fica fora do `ControleAcesso` estrito e do `sis_menu` do Plano 35.
- Como é uma rota, o endpoint também fica sujeito às regras de `sis_log_regra`. A migração semeia a regra `/MuralhaDigital/Impressao` (POST, nível `IMPRESSAO`).

### 4. Formato de `dados_extra`

Os eventos de impressão gravam um bloco padronizado em `dados_extra`:

```json
{ "formato": "PDF", "documento": "...", "telaOrigem": "..." }
```

O campo `dados_extra` já entra no hash encadeado de integridade, então os novos campos integram a cadeia sem mudança na função de hash.

### 5. Configuração

- Tela de regras (`logs-regras`): `IMPRESSAO` passa a ser nível selecionável.
- Tipos de evento: `IMPRESSAO` aparece na lista existente, com liga/desliga global.

### 6. Exibição na consulta

- `consulta-log.js` mostra os campos formato, documento e tela de origem no detalhe do evento `IMPRESSAO`.
- Não há nova coluna nem filtro na grade (decisão do cliente, §9, item 2 do desenho).

## Arquivos

| Arquivo | Tipo | Papel |
|---|---|---|
| `docs/banco-de-dados/migracoes/20261112_impressao_captura_logs.sql` | Criado | Seed do tipo `IMPRESSAO` e da regra de `/MuralhaDigital/Impressao`; idempotente; rollback no cabeçalho |
| `src/main/java/muralha/digital/auditoria/AuditoriaService.java` | Alterado | `derivarNivel` (4 argumentos), `derivarFormato`, `ehPdf`, `montarDadosImpressao`, `derivarTelaOrigem`, `registrarImpressao`, `formatoOuPadrao` |
| `src/main/java/muralha/digital/auditoria/ImpressaoServlet.java` | Criado | Endpoint `/MuralhaDigital/Impressao` |
| `src/main/webapp/muralha-digital/assets/js/auditoria/impressao-captura.js` | Criado | Gancho `beforeprint` no navegador |
| `src/main/webapp/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp` | Alterado | Inclui o gancho nas telas novas |
| `src/main/webapp/muralha-digital/pages/admin/logs-regras/index.jsp` | Alterado | Nível `IMPRESSAO` na tela de regras |
| `src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js` | Alterado | Detalhe de formato, documento e tela de origem |
| Testes unitários (`AuditoriaDerivacaoTest`, `FormatoImpressaoTest`, `DadosImpressaoTest`) | Criados | Derivação de nível e formato, endpoint e bloco de `dados_extra` |

## Migrações

- `docs/banco-de-dados/migracoes/20261112_impressao_captura_logs.sql`, a executar depois de `20261105_tipos_evento_log.sql`.
- Rollback: remover o tipo `IMPRESSAO` de `sis_log_tipo_evento` e a regra de `/MuralhaDigital/Impressao` de `sis_log_regra` (instruções no cabeçalho do script).

## Critérios de aceite

- [ ] Gerar um relatório PDF e confirmar o evento `IMPRESSAO` com formato `PDF`, documento e tela de origem no detalhe da consulta.
- [ ] Gerar um CSV ou XLSX e confirmar que continua como `EXPORTACAO`.
- [ ] Usar Ctrl+P em uma tela com o cabeçalho comum e confirmar o evento `IMPRESSAO` com `formato` "Impressao de tela" e tela de origem.
- [ ] Desligar o tipo `IMPRESSAO` em `sis_log_tipo_evento` e confirmar que os novos eventos deixam de ser gravados.
- [ ] Confirmar que a impressão e a resposta do relatório não sofrem atraso perceptível.

## Limitações conhecidas

- **PDF de servidor:** registra-se a geração ou abertura do PDF, não o ato físico de imprimir dentro do visualizador. Pode haver falso-positivo (gerou e não imprimiu) e falso-negativo (reabriu um PDF já salvo). Aceito pelo cliente como proxy adequado (§10 do desenho).
- **Impressão de tela:** o gancho registra a intenção de imprimir no disparo de `beforeprint`. Se o usuário cancelar o diálogo, o evento é gravado mesmo assim, porque o navegador não informa o resultado (§10 do desenho).
- **Legado antigo:** telas sem o cabeçalho comum e sem `window.print` ficam fora da fase 1 (§7 do desenho). Não há captura da impressão de tela nelas.
- **Itens de acompanhamento:** cobertura universal da impressão de navegador no GTW clássico antigo, e rótulo amigável por documento além do derivado da rota (escopo do cliente).
