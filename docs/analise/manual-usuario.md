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

Os painéis consultam a camada `analise` do banco do GTW. O nome e as coordenadas de cada equipamento são os da versão do cadastro vigente na data do registro. Os dados ficam em cache de 15 minutos a 1 hora, e as tabelas materializadas são recarregadas pelo script `refresh-materialized.sh`.

### Painéis operacionais

| Painel | Conteúdo |
|---|---|
| **KPIs Operacionais** | Passagens, infrações, alertas, disponibilidade, SLA de latência e assertividade, com evolução mensal. |
| **Assertividade — Dupla Análise** | Assertividade (pré-aprovadas ÷ analisadas), analisadas, desempates, reprovadas e evolução diária. |
| **SLA de Latência** | Percentual de janelas de 5 minutos dentro do limite, latência P95, violações e limite do SLA. |
| **Disponibilidade de Equipamentos** | Horas com captura ÷ 24, incluindo os dias sem captura; evolução mensal, equipamentos com menor disponibilidade e ranking. |
| **Pré-processamento de Imagens** | Recebidas, disponibilizadas, aproveitamento, SLA de 72h, tempo médio até a entrada, motivos de descarte e produtividade por operador (TR 5.5.3.1). |
| **Alertas** | Total, críticos, pendentes, percentual tratado, evolução por tipo, status e principais responsáveis. |
| **Infrações** | Total, excesso médio, equipamentos, participação de velocidade, evolução por categoria, enquadramentos e faixas. |
| **Passagens Veiculares** | Passagens por hora do dia, classificação, faixa × hora, velocidade média, percentual Mercosul e ranking de equipamentos. |
| **Mapa — Equipamentos, Áreas e Infrações** | Equipamentos cadastrados, polígonos das áreas monitoradas, passagens e infrações por equipamento. |

### Painéis de demonstração

Fluxo Veicular, Fiscalização e Infrações, Tráfego Histórico, Mobilidade Urbana (Waze, com o filtro de cidade iniciando em Salvador) e Alertas de Veículos de Interesse.

## 3. Usando filtros

Os filtros ficam na barra lateral à esquerda de cada painel:

1. **Período**: sem filtro por padrão (todo o histórico); escolha um intervalo para restringir.
2. **Equipamento, Tipo, Status, Operador etc.**: filtros específicos de cada painel.
3. **Filtro cruzado**: clicar em uma barra ou fatia filtra os demais gráficos do painel.
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

Os dashboards também podem ser visualizados dentro do GTW sem sair do sistema. Acesse **Tela Inicial → Análise de Dados → Análises** e escolha o painel na lista **Painel** (grupos *Demonstração* e *Painéis operacionais*). O botão **Abrir no Superset** abre o painel em nova aba; ele só aparece para o administrador do sistema ou para quem recebeu o direito *Analises - Abrir no Superset*.

Para abrir no Superset completo (com filtros avançados e SQL Lab para administradores), clique em **Abrir no Superset**. Se o botão não aparece, peça ao administrador o direito *Analises - Abrir no Superset* (funcionalidade 396 em `sis_menu`, concedida por grupo ou por usuário).

## 8. Dúvidas frequentes

**P: O dashboard está mostrando "Sem dados".**
R: Confira se o filtro de período ou um filtro cruzado não excluiu todos os registros (limpe os filtros na barra lateral). Se persistir, verifique no `analise.carga_log` se as cargas `spu_carga_*` estão rodando.

**P: Não consigo ver um dashboard específico.**
R: Seu perfil pode não ter acesso. Solicite ao administrador do sistema.

**P: Os dados estão desatualizados.**
R: Os resultados ficam em cache de 15 minutos a 1 hora (conforme o painel). As tabelas materializadas são recarregadas pelo `refresh-materialized.sh` (sugestão: a cada 30 minutos).

**P: Como criar um novo gráfico?**
R: Apenas perfis Gestor e Administrador podem criar gráficos. Acesse o Superset diretamente e use o menu **+ > Chart**.

**P: O mapa não está carregando.**
R: Os mapas usam tiles OpenStreetMap. Verifique sua conexão com a internet.
