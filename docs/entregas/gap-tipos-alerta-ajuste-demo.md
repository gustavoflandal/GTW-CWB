# Gap — Configuração dos tipos de alerta (ajuste temporário para demonstração)

> **Data:** 2026-10-17
> **Status:** 🟡 Contornado para a demonstração. A correção definitiva está pendente.
> **Natureza:** qualidade de dados e interface. Não é um item novo do TR, mas afeta a exibição dos itens 5.3.3.3-4 e 5.3.7.8 (criticidade e destaque visual) entregues no Plano 28.

## Origem

- [Plano 28 — Alertas Operacionais](../planos-salvador/28-alertas-operacionais.md): criou `muralha.tipo_alerta_config` (criticidade, cor e ícone) e o `TipoAlertaConfigServlet`.
- [Plano 25 — Integridade SHA-256 na recepção](../planos-salvador/25-integridade-sha256-recepcao.md): criou o tipo "Integridade de imagem divergente" sem cor e sem ícone.
- Entrega relacionada: [28-alertas-operacionais.md](28-alertas-operacionais.md).

## Problema encontrado

Na listagem `GET /MuralhaDigital/TipoAlertaConfig?acao=listar`, que também alimenta as cores do popup de notificação (`notificacao.js`):

| Tipo | Inconsistência |
|---|---|
| Integridade de imagem divergente | Sem cor e sem ícone |
| Outros | Sem cor, sem ícone e sem descrição |
| Veículo Suspeito de Roubo à Banco | Crítico, mas com cor verde (`success`) |
| Veículo Suspeito de Sequestro Relâmpago | Crítico, mas com cor verde (`success`) |
| Falha de Equipamento | Prioridade 5, repetida com "Roubo à Banco"; a ordem entre os dois fica indefinida |

Pendente de decisão do negócio, sem ajuste: "Veículo Clonado" está com `critico = 0`, enquanto Furtado e Roubado são críticos.

## Contorno aplicado (temporário)

Por decisão de 2026-10-17, **nenhuma tabela existente foi alterada**: nem `muralha.tipo_alerta_ocorrencia` (legado) nem `muralha.tipo_alerta_config`.

- Migração [`20261017_tipo_alerta_ajuste_demo.sql`](../banco-de-dados/migracoes/20261017_tipo_alerta_ajuste_demo.sql): cria a tabela nova `muralha.tipo_alerta_ajuste_demo`, sem chave estrangeira, e grava 5 ajustes. Executada no DEV em 2026-10-17.
- `TipoAlertaConfigServlet.listarConfigs` aplica a precedência **ajuste ativo > `tipo_alerta_config` > `tipo_alerta_ocorrencia`**, só quando a tabela existe. Os tipos ajustados saem no JSON com `"ajusteTemporario": true`.

| Tipo | Ajuste |
|---|---|
| Integridade de imagem divergente | cor `warning`, ícone `bi-shield-exclamation` |
| Outros | cor `info`, ícone `bi-question-circle`, descrição "Alerta que não se enquadra nos demais tipos cadastrados" |
| Veículo Suspeito de Roubo à Banco | cor `error` |
| Veículo Suspeito de Sequestro Relâmpago | cor `error` |
| Falha de Equipamento | prioridade 4 |

As cores usam só os valores que o `notificacao.js` sabe exibir: `success`, `info`, `warning`, `error`, `orange`, `purple`, `brown` e `pink`.

**Limitações do contorno:**
- Vale apenas para quem usa a listagem do servlet: o popup de notificação e o JSON. As telas legadas que leem `tipo_alerta_ocorrencia.cor` e `.prioridade` diretamente continuam com os valores antigos.
- Enquanto um ajuste estiver ativo, ele prevalece sobre o que for salvo pela configuração (`acao=salvarConfig`), e o valor salvo não aparece.
- A view `analise.v_alertas` e os painéis do Superset continuam usando `tipo_alerta_config.critico`. A criticidade não foi ajustada, então não há divergência.

## Gap relacionado: falta a tela de configuração

A entrega 28 descreve uma "tela de configuração", mas não existe JSP: o card **Tipos de Alerta** do menu aponta para `/MuralhaDigital/TipoAlertaConfig`, que, sem `acao`, devolve o JSON da listagem. Por isso o usuário vê dados crus no navegador.

## Correção definitiva (a fazer)

1. Criar a tela de configuração dos tipos de alerta: JSP no padrão visual (`docs/requisitos/05-padrao-visual.md`) usando as ações `listar`, `salvarConfig` e `tratamentos` já existentes, e apontar o card para ela.
2. Pela tela, gravar em `muralha.tipo_alerta_config` a cor e o ícone dos 4 tipos acima, com o histórico de parâmetros do Plano 26.
3. Definir com a CONTRATANTE a criticidade do "Veículo Clonado" e a ordem de prioridade. A prioridade fica em `tipo_alerta_ocorrencia` (legado): avaliar se ela deve passar para a tabela complementar, em vez de alterar o legado.
4. Fazer o seed de cor e ícone na própria migração sempre que um tipo de alerta novo for criado (evita repetir o caso do Plano 25).
5. Desligar o contorno (`UPDATE muralha.tipo_alerta_ajuste_demo SET ativo = 0`), conferir a listagem e então remover a tabela (`DROP TABLE muralha.tipo_alerta_ajuste_demo`) e o trecho correspondente do servlet.
