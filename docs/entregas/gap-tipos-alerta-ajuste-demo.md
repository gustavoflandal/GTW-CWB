# Gap — Configuração dos tipos de alerta

> **Abertura:** 2026-10-17 · **Resolvido:** 2026-10-18
> **Status:** ✅ Resolvido. A tela de configuração foi criada, os valores estão no lugar definitivo e o contorno temporário foi removido. Os tratamentos automáticos passaram a ser executados.
> **Natureza:** interface, dados e execução dos itens 5.3.1.3, 5.3.3.3-4 e 5.3.7.8 (criticidade, destaque visual e tratamentos), entregues no Plano 28.

## Origem

- [Plano 28 — Alertas Operacionais](../planos-salvador/28-alertas-operacionais.md): criou `muralha.tipo_alerta_config`, `muralha.tipo_alerta_tratamento`, o `TipoAlertaConfigServlet` e o `ExecutorTratamento`.
- [Plano 25 — Integridade SHA-256 na recepção](../planos-salvador/25-integridade-sha256-recepcao.md): criou o tipo "Integridade de imagem divergente" sem cor e sem ícone.
- Entrega relacionada: [28-alertas-operacionais.md](28-alertas-operacionais.md).

## Problemas encontrados

| # | Problema | Efeito |
|---|---|---|
| 1 | Não existia tela: o card **Tipos de Alerta** abria o JSON de `/MuralhaDigital/TipoAlertaConfig` | Configuração impossível pela interface |
| 2 | Integridade e Outros sem cor e sem ícone; Roubo à Banco e Sequestro Relâmpago (críticos) em verde | Destaque visual incoerente com a criticidade |
| 3 | O `ExecutorTratamento` não era chamado em nenhum ponto do código | Tratamentos marcados como automáticos nunca eram executados |
| 4 | A listagem exigia a permissão da tela de configuração (só o grupo 42), mas o `notificacao.js` de todos os operadores a usa | Para os demais operadores o aviso caía nas cores fixas do código |
| 5 | A validação de cor aceitava qualquer texto (`#FF5733`, `toast-danger`), mas o aviso só sabe exibir 8 cores (`toastr[cor]`) | Uma cor fora da lista quebraria o aviso com erro de JavaScript |
| 6 | O ícone configurado não aparecia no aviso | Configuração sem efeito |
| 7 | O histórico de parâmetros registrava só `critico`, sempre com valor anterior nulo | Rastreabilidade incompleta (5.4.7.6) |
| 8 | "Falha de Equipamento" tinha o tratamento CAMPO (manual) marcado como automático | Configuração impossível de executar |

Em 2026-10-17 houve um contorno temporário (tabela `muralha.tipo_alerta_ajuste_demo`), já removido.

## Solução definitiva

- **Tela** `muralha-digital/pages/tipos-alerta/` (Bootstrap 5.3, SweetAlert2), aberta pelo card. O servlet, chamado sem `acao`, encaminha para ela. Recursos:
  - Busca, pré-visualização do aviso com a cor e o ícone, chave Crítico, seleção entre as 8 cores suportadas e ícone com sugestões.
  - Modal de tratamentos: ativo, automático (só para os executáveis) e parâmetros em JSON.
- **Servlet** `TipoAlertaConfigServlet`:
  - `listar` exige só sessão; a tela, o catálogo e as gravações exigem a permissão do menu.
  - Valida cor (8 valores), ícone (`bi-*`), parâmetros (objeto JSON de até 2.000 caracteres) e se o tratamento pode ser automático.
  - O histórico de parâmetros registra valor anterior e novo de critico, cor, ícone e de cada tratamento. A auditoria continua.
- **Execução dos tratamentos automáticos:** `JobTratamentoAutomatico`, agendado a cada minuto no `Agendador`, processa os alertas dos últimos `TRATAMENTO_AUTO_JANELA_MIN` minutos (padrão 60). Cada par alerta × tratamento é registrado uma única vez em `muralha.alerta_tratamento_execucao`.
  - Registro estatístico e priorização para auditoria são gravados na auditoria.
  - Geração de ocorrência muda o alerta de PENDENTE para OCORRÊNCIA.
- **Aviso de alerta** (`notificacao.js`): passa a exibir o ícone do tipo antes do título.
- **Migração** [`20261018_tipo_alerta_config_definitivo.sql`](../banco-de-dados/migracoes/20261018_tipo_alerta_config_definitivo.sql), executada no DEV em 2026-10-18:
  - grava as cores e os ícones em `tipo_alerta_config`, com histórico;
  - desmarca como automáticos os tratamentos manuais;
  - cria `alerta_tratamento_execucao` e o parâmetro `TRATAMENTO_AUTO_JANELA_MIN`;
  - remove `tipo_alerta_ajuste_demo`.
- **Testes:** `ConfigETratamentoTest` cobre as cores do aviso, o formato do ícone, os tratamentos executáveis e os parâmetros JSON (12 testes passando).

## Decisões e limites

- **Prioridade e descrição** ficam em `muralha.tipo_alerta_ocorrencia` (legado) e aparecem sem edição, por decisão de não alterar tabelas legadas. Continua o empate de prioridade 5 entre "Falha de Equipamento" e "Veículo Suspeito de Roubo à Banco", e "Outros" segue sem descrição. Se for preciso editá-los, a forma é uma tabela complementar lida também pelas procedures legadas que ordenam os alertas.
- **Criticidade do "Veículo Clonado"** (hoje não crítico) é decisão do negócio e pode ser alterada na própria tela.
- **Tratamentos manuais** (fiscalização em campo, análise operacional, integração corporativa, outro) ficam registrados como encaminhamento do tipo e são executados pelo operador.
