# Manual do Usuário — Análises (Superset)

## 1. Acesso

O módulo de Análises é acessado pelo menu **Análises** no sistema GTW / Muralha Digital. O login é automático (SSO) — basta estar logado no GTW.

### Perfis de acesso

| Perfil | O que pode fazer |
|---|---|
| **Operador** | Visualizar dashboards publicados, aplicar filtros |
| **Gestor** | Criar gráficos e explorar dados, sem SQL Lab |
| **Auditor** | Visualizar dashboards e consultar log de ações |
| **Administrador** | Acesso total: SQL Lab, gerenciar usuários, configurar alertas |

## 2. Painéis disponíveis

### KPIs Operacionais
Visão geral dos principais indicadores: passagens, infrações, alertas, disponibilidade, SLA e assertividade. Atualização a cada 60 segundos.

### Assertividade
Taxa de assertividade do pré-processamento. Destaque vermelho quando abaixo de 80% (meta do TR). Evolução diária e distribuição por operador.

### SLA de Latência
Percentual de janelas dentro do SLA de latência. Meta: 95%. Latência P95 e evolução temporal. Alertas para violações acima de 4000ms.

### Disponibilidade de Equipamentos
Disponibilidade média dos equipamentos. Meta: 95%. Ranking por equipamento e evolução diária.

### Pré-processamento
Indicadores de produtividade do pré-processamento conforme TR 5.5.3.1: recebidos, processados, descartados, aproveitamento, tempo médio e SLA 72h.

### Alertas
Visão geral dos alertas do sistema: total, críticos, pendentes, distribuição por tipo e status. Atualização a cada 30 segundos.

### Infrações
Análise de infrações: total por equipamento, enquadramento, excesso médio e evolução temporal.

### Passagens Veiculares
Volume de passagens por hora, equipamento, faixa e classificação. Velocidade média e percentual Mercosul.

### Mapa de Equipamentos
Mapa interativo com camadas:
- **Pontos verdes/vermelhos**: equipamentos ativos/inativos
- **Heatmap**: concentração de infrações
- **Polígonos**: áreas monitoradas

## 3. Usando filtros

Todos os painéis possuem filtros na barra superior:

1. **Período** (obrigatório): selecione o intervalo de datas. Padrão: últimos 7 dias.
2. **Equipamento/Tipo/Status**: filtros específicos de cada painel.
3. **Cross-filter**: clique em um gráfico para filtrar os demais automaticamente.

### Como aplicar um filtro

1. Clique no ícone de filtro na barra superior do dashboard
2. Selecione o período ou valor desejado
3. Clique em **Aplicar filtros**
4. Para limpar, clique em **Limpar todos**

## 4. Exportando dados

### Exportar gráfico como imagem
1. Passe o mouse sobre o gráfico
2. Clique no menu **⋯** (três pontos) no canto superior direito
3. Selecione **Download > Download as Image**

### Exportar dados como CSV
1. Passe o mouse sobre o gráfico
2. Clique no menu **⋯** (três pontos)
3. Selecione **Download > Export to CSV**

### Exportar dashboard completo
1. No menu superior do dashboard, clique em **⋯**
2. Selecione **Download as Image** para screenshot completo

## 5. Alertas automáticos

O sistema envia alertas por e-mail quando:

| Alerta | Condição | Frequência |
|---|---|---|
| Disponibilidade baixa | Média < 95% nas últimas 24h | A cada 15 minutos |
| SLA violado | Cumprimento < 95% nas últimas 24h | A cada 15 minutos |
| Assertividade baixa | Média < 80% nas últimas 24h | A cada 30 minutos |
| Alertas críticos | Pendentes há mais de 2 horas | A cada 10 minutos |

## 6. Relatórios agendados

Relatórios são enviados automaticamente por e-mail:

| Relatório | Frequência | Horário |
|---|---|---|
| Resumo Diário — KPIs | Diário | 07:00 |
| Semanal — Infrações | Segunda-feira | 08:00 |
| Semanal — SLA | Segunda-feira | 08:30 |

## 7. Embedding no GTW

Os dashboards também podem ser visualizados dentro do GTW sem sair do sistema. Acesse pelo menu **Análises > Painéis** e selecione o painel desejado na lista.

Para abrir no Superset completo (com filtros avançados e SQL Lab para administradores), clique em **Abrir no Superset**.

## 8. Dúvidas frequentes

**P: O dashboard está mostrando "Sem dados".**
R: Verifique se o filtro de período está selecionado. Alguns dashboards requerem dados recentes nas tabelas materializadas.

**P: Não consigo ver um dashboard específico.**
R: Seu perfil pode não ter acesso. Solicite ao administrador do sistema.

**P: Os dados estão desatualizados.**
R: Os dashboards atualizam automaticamente a cada 30-120 segundos. As tabelas materializadas são recalculadas periodicamente pelas stored procedures.

**P: Como criar um novo gráfico?**
R: Apenas perfis Gestor e Administrador podem criar gráficos. Acesse o Superset diretamente e use o menu **+ > Chart**.

**P: O mapa não está carregando.**
R: Os mapas usam tiles OpenStreetMap. Verifique sua conexão com a internet.
