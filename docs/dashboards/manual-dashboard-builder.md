# Manual do Dashboard Builder — Muralha Digital

Manual completo para criacao de dashboards e KPIs no sistema Muralha Digital.
Este guia conduz voce por todas as etapas, desde a configuracao inicial ate um painel
funcionando com graficos e indicadores em tempo real.

---

## Sumario

1. [Visao Geral](#1-visao-geral)
2. [Acesso ao Sistema](#2-acesso-ao-sistema)
3. [Etapa 1 — Catalogo de Dados (Admin)](#3-etapa-1--catalogo-de-dados-admin)
4. [Etapa 2 — Criar um Dataset](#4-etapa-2--criar-um-dataset)
5. [Etapa 3 — Criar um Painel](#5-etapa-3--criar-um-painel)
6. [Etapa 4 — Adicionar Widgets](#6-etapa-4--adicionar-widgets)
7. [Etapa 5 — Montar um KPI](#7-etapa-5--montar-um-kpi)
8. [Etapa 6 — Organizar e Personalizar o Painel](#8-etapa-6--organizar-e-personalizar-o-painel)
9. [Etapa 7 — Compartilhar e Exportar](#9-etapa-7--compartilhar-e-exportar)
10. [Resolucao de Problemas](#10-resolucao-de-problemas)
11. [Glossario](#11-glossario)

---

## 1. Visao Geral

O **Dashboard Builder** e um modulo do sistema Muralha Digital que permite criar
paineis personalizados com graficos, tabelas e indicadores (KPIs) sem escrever
codigo ou SQL. O processo completo segue este fluxo:

```
Catalogo de Dados  →  Dataset  →  Painel  →  Widgets
  (tabelas do BD)    (modelo)    (tela)    (graficos/KPIs)
```

**O que cada peca faz:**

| Conceito | O que e | Quem cria |
|---|---|---|
| **Catalogo de Dados** | Registro das tabelas e colunas disponiveis no banco de dados | Administrador |
| **Dataset** | Selecao de tabelas, colunas e relacionamentos que formam uma "fonte de dados" | Operador ou Admin |
| **Painel** | Uma tela (dashboard) onde voce organiza os widgets | Operador |
| **Widget** | Um componente visual: grafico de barras, linha, pizza, KPI, tabela, etc. | Operador |

---

## 2. Acesso ao Sistema

### 2.1 Login

1. Abra o navegador e acesse o endereco do sistema (ex.: `http://localhost:8080`)
2. Na tela de login, informe seu **usuario** e **senha**
3. Clique em **Entrar**

### 2.2 Permissoes necessarias

Para usar o Dashboard Builder, seu usuario deve pertencer a pelo menos um destes grupos:

| Grupo | Permissao |
|---|---|
| **11** (Administrador) | Acesso total: catalogo, datasets, paineis e widgets |
| **41** (Analista) | Criar e editar datasets, paineis e widgets |
| **42** (Visualizador) | Visualizar paineis compartilhados |

> Se voce nao ve os menus descritos abaixo, solicite ao administrador que
> adicione seu usuario ao grupo apropriado.

### 2.3 Navegacao pelo menu

Apos o login, o menu lateral esquerdo exibe as opcoes. Localize:

- **Paineis de Analise** — abre a galeria de paineis (dashboards)
- **Catalogo de Datasets** — lista de datasets criados
- **Catalogo de Datasets > botao Catalogo de Dados** — gerenciamento de tabelas (admin)

---

## 3. Etapa 1 — Catalogo de Dados (Admin)

> **Quem faz:** apenas o administrador do sistema.
> Se o catalogo ja estiver configurado, pule para a [Etapa 2](#4-etapa-2--criar-um-dataset).

O Catalogo de Dados e o "dicionario" do banco — ele diz ao sistema quais tabelas
e colunas estao disponiveis para montar datasets.

### 3.1 Acessar o Catalogo

1. Na tela inicial, abra **Cadastro e Configuracao > Catalogo de Datasets**
2. Clique no botao **Catalogo de Dados**
3. A tela abre com tres abas: **Tabelas**, **Colunas** e **Relacoes**

### 3.2 Cadastrar uma Tabela

1. Na aba **Tabelas**, clique no botao **Nova Tabela** (canto superior direito)
2. Um painel lateral abre a direita com o formulario:

| Campo | O que preencher | Exemplo |
|---|---|---|
| **Nome Fisico** | Nome real da tabela no banco de dados | `veiculo_tempo_real` |
| **Schema** | Esquema do banco (selecione na lista) | `muralha` |
| **Rotulo** | Nome amigavel que aparecera nas telas | `Passagens de Veiculos` |
| **Descricao** | Texto livre explicando o conteudo (opcional) | `Registros de passagem dos veiculos nos pontos` |
| **Chave Primaria** | Coluna que identifica cada registro | `id` |
| **Sensivel** | Marque se a tabela contem dados pessoais | _(deixe desmarcado se nao houver)_ |

3. Clique em **Salvar**
4. A tabela aparece na lista com um selo colorido indicando o schema:
   - Cinza = `dbo`
   - Azul = `muralha`
   - Verde = `dash`

### 3.3 Cadastrar Colunas

1. Clique na aba **Colunas**
2. No topo, selecione a tabela desejada na lista suspensa
3. Clique no botao **Nova Coluna**
4. Preencha o formulario:

| Campo | O que preencher | Exemplo |
|---|---|---|
| **Nome Fisico** | Nome real da coluna no banco | `velocidade` |
| **Rotulo** | Nome amigavel | `Velocidade` |
| **Tipo** | Tipo do dado | `numerico` |
| **Papel Sugerido** | Como a coluna sera usada em graficos | `Medida` |
| **Coluna sensivel** | Marque se contem dados pessoais (CPF, placa) | _(marque se aplicavel)_ |

**Tipos de dado disponiveis:**

| Tipo | Quando usar |
|---|---|
| `texto` | Nomes, descricoes, categorias |
| `numerico` | Valores decimais (velocidade, percentual) |
| `inteiro` | Contadores, quantidades, IDs |
| `data` | Somente data (sem horario) |
| `data_hora` | Data com horario |
| `booleano` | Verdadeiro/Falso |

**Papeis sugeridos:**

| Papel | Significado | Icone na tela |
|---|---|---|
| **Dimensao** | Categoriza os dados (eixo X, agrupamento) | Selo azul |
| **Medida** | Valor numerico a ser medido (eixo Y) | Selo verde |
| **Tempo** | Coluna de data/hora para eixo temporal | Selo ambar |
| **Geo** | Coordenada geografica (latitude/longitude) | Selo roxo |

5. Clique em **Salvar**
6. Repita para todas as colunas da tabela

### 3.4 Cadastrar Relacoes entre Tabelas

Relacoes conectam tabelas entre si (como um JOIN no SQL).

1. Clique na aba **Relacoes**
2. Clique no botao **Nova Relacao**
3. Preencha:

| Campo | O que preencher | Exemplo |
|---|---|---|
| **Tabela Origem** | Tabela que contem a chave estrangeira | `veiculo_tempo_real` |
| **Tabela Destino** | Tabela referenciada | `equipamento` |
| **Coluna Origem** | Coluna na tabela origem | `id_equipamento` |
| **Coluna Destino** | Coluna na tabela destino | `id` |
| **Cardinalidade** | Tipo de relacionamento | `N:1 (muitos para um)` |

**Tipos de cardinalidade:**

| Valor | Significado |
|---|---|
| `many_to_one` (N:1) | Muitos registros da origem apontam para um da destino |
| `one_to_many` (1:N) | Um registro da origem tem muitos na destino |
| `one_to_one` (1:1) | Um para um |

4. Clique em **Salvar**
5. A relacao aparece na lista com as colunas de ligacao exibidas no formato
   `colunaOrigem = colunaDestino`

---

## 4. Etapa 2 — Criar um Dataset

O Dataset e o "modelo de dados" que alimentara seus graficos e KPIs. Ele define
quais tabelas, colunas e filtros serao usados.

### 4.1 Acessar a lista de Datasets

1. No menu lateral, clique em **Catalogo de Datasets**
2. A tela lista todos os datasets existentes
3. Clique no botao **Novo Dataset** (canto superior direito)

### 4.2 Wizard — Passo 1: Tabela Base

Voce entra no editor no modo wizard (assistente de 6 passos). O indicador no topo
mostra em qual passo voce esta:

```
1. Tabela Base  /  2. Relacoes  /  3. Campos  /  4. Calculados  /  5. Preview  /  6. Publicar
```

1. Preencha os campos:

| Campo | O que preencher | Exemplo |
|---|---|---|
| **Nome do dataset** | Nome que identifica esta fonte de dados | `Passagens por Dia` |
| **Descricao** | Texto explicativo (opcional) | `Total de passagens diarias por equipamento` |
| **Tabela base** | Tabela principal (selecione na lista) | `veiculo_tempo_real` |
| **Visibilidade** | Quem pode usar este dataset | `Publico` (todos) ou `Privado` (so voce) |

2. Clique no botao **Proximo →** no canto inferior direito

### 4.3 Wizard — Passo 2: Relacoes

Nesta tela o sistema mostra as tabelas relacionadas a sua tabela base (conforme
cadastrado no Catalogo de Dados).

1. Cada tabela relacionada aparece como um cartao clicavel
2. **Clique no cartao** para incluir a tabela no dataset:
   - Cartao com **borda verde e icone ✓** = incluida
   - Cartao com **borda cinza e icone +** = nao incluida
3. Cada cartao mostra os detalhes da relacao (colunas de ligacao e cardinalidade)
4. Inclua todas as tabelas que possuem dados que voce precisa nos graficos

> **Dica:** Se nao houver relacoes cadastradas, o passo mostrara uma mensagem vazia.
> Neste caso, volte ao Catalogo de Dados e cadastre as relacoes necessarias.

5. Clique em **Proximo →**

### 4.4 Wizard — Passo 3: Campos

Aqui voce seleciona quais colunas farao parte do dataset.

1. As colunas aparecem agrupadas por tabela (tabela base primeiro, depois as relacionadas)
2. Para cada coluna que voce deseja usar:
   - **Marque o checkbox** a esquerda — o item fica destacado com borda azul
   - Selecione o **Tipo**:
     - `Dimensao` — para agrupar dados (nomes, categorias, locais)
     - `Medida` — para valores numericos (contagens, somas, medias)
     - `Tempo` — para colunas de data/hora
   - Se escolher `Medida`, selecione tambem a **Agregacao**:
     - `SUM` — soma
     - `AVG` — media
     - `COUNT` — contagem
     - `COUNT DISTINCT` — contagem de valores unicos
     - `MIN` / `MAX` — menor / maior valor
     - `NONE` — sem agregacao

> **Exemplo pratico:** Para um grafico de "Total de passagens por equipamento":
> - Marque `nome_equipamento` como **Dimensao**
> - Marque `id` (da tabela de passagens) como **Medida** com agregacao **COUNT**

3. Clique em **Proximo →**

### 4.5 Wizard — Passo 4: Campos Calculados (Opcional)

Campos calculados permitem criar colunas derivadas usando expressoes SQL simples.

1. Se nao precisar de campos calculados, clique em **Proximo →** para pular
2. Para criar um campo calculado:
   - Clique em **Adicionar campo calculado**
   - Preencha:

| Campo | O que preencher | Exemplo |
|---|---|---|
| **Alias** | Identificador interno (sem espacos) | `faixa_velocidade` |
| **Rotulo** | Nome exibido nos graficos | `Faixa de Velocidade` |
| **Tipo** | Dimensao ou Medida | `Dimensao` |
| **Expressao** | Formula SQL | `CASE WHEN velocidade > 60 THEN 'alta' ELSE 'normal' END` |

   - O sistema valida a expressao em tempo real:
     - ✅ Verde = expressao valida
     - ❌ Vermelho = erro de sintaxe (corrija antes de prosseguir)
   - Clique em **Adicionar**

3. Clique em **Proximo →**

### 4.6 Wizard — Passo 5: Preview

Nesta tela voce pode visualizar uma amostra dos dados que o dataset retornara.

1. Clique no botao **Carregar preview**
2. O sistema executa uma consulta e mostra ate 50 linhas de resultado
3. Verifique se os dados estao corretos:
   - As colunas selecionadas aparecem?
   - Os valores fazem sentido?
   - Os relacionamentos trouxeram os dados esperados?

> **Se aparecer "Campo '...' nao existe no dataset" ou outra mensagem de validacao:**
> corrija o campo indicado e salve novamente. Veja [Resolucao de Problemas](#10-resolucao-de-problemas).

4. Clique em **Proximo →**

### 4.7 Wizard — Passo 6: Publicar

Esta e a tela final do wizard.

1. A tela mostra um resumo: nome do dataset, quantidade de campos e tabelas relacionadas
2. Voce tem duas opcoes:
   - **Publicar Dataset** (botao verde) — publica o dataset, tornando-o disponivel
     para uso em widgets. Antes de publicar, todos os campos e relacoes sao validados;
     se algum estiver invalido, a mensagem informa qual.
   - **Salvar Rascunho** (botao cinza) — salva sem publicar. Voce pode voltar
     depois para editar e publicar.
3. Clique em **Publicar Dataset**
4. Voce sera redirecionado para a lista de datasets, onde o novo aparece com
   status **Publicado** (selo verde)

---

## 5. Etapa 3 — Criar um Painel

O Painel e a tela do dashboard onde voce organizara seus graficos e KPIs.

### 5.1 Acessar a galeria de Paineis

1. No menu lateral, clique em **Paineis de Analise**
2. A tela mostra tres abas:
   - **Meus Paineis** — paineis que voce criou
   - **Compartilhados** — paineis que outros compartilharam com voce
   - **Todos** — todos os paineis do sistema

### 5.2 Criar um novo Painel

1. Clique no botao **Novo Painel** (canto superior direito)
2. Uma caixa de dialogo pede o nome do painel
3. Digite um nome descritivo (ex.: `Operacao Diaria - Zona Norte`)
4. Clique em **OK**
5. Voce e redirecionado para o **Editor do Painel** — uma tela em branco pronta
   para receber widgets

### 5.3 Estrutura do Editor do Painel

O editor tem duas areas:

```
┌──────────────┬──────────────────────────────────────────────┐
│              │  [Nome do Painel]    [Refresh] [+Widget] [⚙] │
│  Lista de    │                                               │
│  Paineis     │                                               │
│  (lateral)   │          Area do Dashboard                    │
│              │        (arraste e redimensione                │
│              │         os widgets aqui)                      │
│              │                                               │
└──────────────┴──────────────────────────────────────────────┘
```

- **Lateral esquerda**: lista de todos os paineis. Clique em um para abri-lo.
- **Barra superior**: nome do painel, indicador de atualizacao, botoes de acao.
- **Area central**: grade onde os widgets sao posicionados (12 colunas).

---

## 6. Etapa 4 — Adicionar Widgets

Widgets sao os componentes visuais do dashboard: graficos, tabelas e KPIs.

### 6.1 Abrir o Editor de Widget

1. No editor do painel, clique no botao **+ Widget** na barra superior
2. Voce e levado ao **Editor de Widget**, dividido em duas colunas:
   - **Esquerda**: configuracao (dataset, tipo, campos, filtros)
   - **Direita**: preview ao vivo do widget

### 6.2 Selecionar Dataset e Tipo

1. No campo **Dataset**, selecione o dataset publicado que voce criou
   (ex.: `Passagens por Dia`)
2. No campo **Titulo**, de um nome ao widget (ex.: `Passagens por Equipamento`)
3. Em **Tipo de Visualizacao**, selecione o tipo de grafico:

| Tipo | Icone | Melhor para |
|---|---|---|
| **Barras** | 📊 | Comparar quantidades entre categorias |
| **Linhas** | 📈 | Tendencias ao longo do tempo |
| **Area** | 📈↑ | Tendencias com enfase no volume |
| **Pizza** | 🥧 | Proporcoes (maximo 7-8 fatias) |
| **Rosca** | 🎯 | Igual a pizza, com espaco central |
| **Dispersao** | ⚬ | Correlacao entre duas medidas |
| **Combinado** | 📊📈 | Barras + linhas no mesmo grafico |
| **Funil** | ⏬ | Processos com etapas sequenciais |
| **Gauge** | 🕐 | Indicador tipo velocimetro |
| **Mapa de Calor** | 🟧 | Intensidade em grade (hora x dia, etc.) |
| **KPI** | 🔢 | Card com numero grande e variacao |
| **Tabela** | 📋 | Dados tabulares com ordenacao |
| **Tabela Dinamica** | 🔄 | Pivot table (linhas x colunas x valores) |

### 6.3 Mapear Campos nos Eixos (Arrastar e Soltar)

Apos selecionar o dataset, a secao **Campos Disponiveis** mostra tres categorias:

- **Dimensoes** — campos de agrupamento (azul)
- **Medidas** — campos numericos (verde)
- **Tempo** — campos de data/hora (ambar)

Para montar o grafico, **arraste** os campos para as zonas de eixo:

```
┌─────────────────────────────────────────────┐
│  Campos Disponiveis                         │
│  [equipamento] [cidade] [velocidade] [data] │
│                                             │
│  Arraste para:                              │
│  ┌─────────────────────────────────────┐    │
│  │ Eixo X / Dimensoes:  [           ] │    │
│  ├─────────────────────────────────────┤    │
│  │ Eixo Y / Medidas:    [           ] │    │
│  ├─────────────────────────────────────┤    │
│  │ Cores / Series:      [           ] │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

| Zona | O que colocar | Efeito |
|---|---|---|
| **Eixo X / Dimensoes** | Campos de agrupamento | Define as categorias no eixo horizontal |
| **Eixo Y / Medidas** | Campos numericos | Define os valores no eixo vertical |
| **Cores / Series** | Campo de agrupamento (opcional) | Divide os dados em series coloridas |

**Exemplo — Grafico de barras "Passagens por Equipamento":**
- Arraste `equipamento` para **Eixo X**
- Arraste `total_passagens` para **Eixo Y**
- Resultado: uma barra por equipamento mostrando o total de passagens

**Exemplo — Grafico de linhas "Passagens por Dia e Equipamento":**
- Arraste `data` para **Eixo X**
- Arraste `total_passagens` para **Eixo Y**
- Arraste `equipamento` para **Cores / Series**
- Resultado: uma linha por equipamento ao longo do tempo

> **Preview automatico:** a cada alteracao, o preview a direita atualiza
> automaticamente apos meio segundo. Se os dados aparecerem, o mapeamento esta correto.

### 6.4 Configurar Filtros (Opcional)

Na secao **Filtros**, voce pode restringir os dados exibidos:

1. Clique em **+ Condicao**
2. Preencha a linha de filtro:
   - **Campo**: selecione a coluna (ex.: `cidade`)
   - **Operador**: selecione a condicao (ex.: `=`)
   - **Valor**: digite o valor (ex.: `Salvador`)
3. Para adicionar mais condicoes, clique em **+ Condicao** novamente
4. No topo, escolha se as condicoes se combinam com **E** (todas devem ser verdadeiras)
   ou **OU** (basta uma ser verdadeira)
5. Para remover uma condicao, clique no **X** vermelho ao lado

### 6.5 Configurar Dimensao Temporal (Opcional)

Se o dataset tem campos de data/hora, a secao **Dimensao Temporal** aparece:

| Campo | Opcoes |
|---|---|
| **Granularidade** | Dia, Semana, Mes, Trimestre, Ano |
| **Periodo** | Hoje, Ontem, Ultimos 7/30 dias, Este mes, Mes passado, Ultimos 3/6/12 meses, Este ano, Ano passado |

### 6.6 Opcoes Visuais

Na secao **Opcoes Visuais**:

| Opcao | O que faz |
|---|---|
| **Paleta** | Esquema de cores: Padrao, Azul, Verde |
| **Orientacao** | Vertical (barras para cima) ou Horizontal (barras para o lado) |
| **Rotulos** | Mostrar valores sobre as barras/fatias |
| **Empilhado** | Empilhar barras em vez de colocar lado a lado |

### 6.7 Limite (Top N)

No campo **Limite**, defina quantos registros o grafico mostra (padrao: 20).
Util para "Top 10 equipamentos com mais passagens".

### 6.8 Salvar o Widget

1. Verifique o preview a direita — o grafico deve aparecer com os dados corretos
2. Clique em **Salvar e Voltar** para salvar e retornar ao painel
3. O widget aparece automaticamente na grade do painel

---

## 7. Etapa 5 — Montar um KPI

Um KPI (Key Performance Indicator) e um card com um numero grande e destaque,
ideal para metricas principais como "Total de Passagens Hoje" ou "Velocidade Media".

### 7.1 Criar um Widget KPI

1. No editor do painel, clique em **+ Widget**
2. Selecione o **Dataset** desejado
3. No **Titulo**, digite o nome do KPI (ex.: `Total de Passagens`)
4. Em **Tipo de Visualizacao**, selecione **KPI** (icone 🔢)

### 7.2 Configurar o KPI

O KPI precisa de apenas um campo de medida:

1. Arraste um campo numerico para **Eixo Y / Medidas**
   (ex.: `total_passagens` com agregacao `SUM`)
2. O preview mostra um card com o numero grande centralizado

### 7.3 Exemplo pratico — 4 KPIs para um painel operacional

Crie 4 widgets KPI separados:

| KPI | Campo (Medida) | Agregacao | Filtro sugerido |
|---|---|---|---|
| **Total de Passagens** | `id` | COUNT | Periodo: Hoje |
| **Velocidade Media** | `velocidade` | AVG | Periodo: Hoje |
| **Equipamentos Ativos** | `id_equipamento` | COUNT DISTINCT | Periodo: Hoje |
| **Infrações Detectadas** | `id` | COUNT | `tipo = 'infracao'`, Periodo: Hoje |

### 7.4 Posicionar os KPIs no Painel

Apos criar cada KPI, posicione-os no topo do dashboard:

1. Clique e **arraste** o titulo do widget para move-lo
2. **Arraste a borda inferior** do widget para redimensiona-lo
3. Os KPIs ficam melhores como cards pequenos em linha:

```
┌──────────┬──────────┬──────────┬──────────┐
│  12.458  │  47 km/h │    32    │    156   │
│ Passagens│ Vel.Media│ Equip.   │ Infracoes│
└──────────┴──────────┴──────────┴──────────┘
┌────────────────────────────────────────────┐
│                                            │
│        Grafico de Barras / Linhas          │
│                                            │
└────────────────────────────────────────────┘
```

---

## 8. Etapa 6 — Organizar e Personalizar o Painel

### 8.1 Mover e Redimensionar Widgets

- **Mover**: clique e segure o titulo do widget (cursor muda para "mao"), arraste
  para a posicao desejada
- **Redimensionar**: arraste a borda ou canto inferior do widget
- A grade tem 12 colunas — um widget pode ocupar de 1 a 12 colunas de largura
- O layout e salvo automaticamente 1,5 segundo apos qualquer alteracao

### 8.2 Editar um Widget Existente

1. No widget, clique no icone **⋮** (tres pontos) no canto superior direito
2. Selecione **Editar** (icone de lapis)
3. Voce retorna ao Editor de Widget com a configuracao atual
4. Faca as alteracoes e clique em **Salvar e Voltar**

### 8.3 Remover um Widget

1. Clique no icone **⋮** do widget
2. Selecione **Remover** (texto vermelho)
3. Confirme a remocao na caixa de dialogo

### 8.4 Ver Widget como Tabela

1. Clique no icone **⋮** do widget
2. Selecione **Ver como tabela**
3. O grafico e substituido por uma tabela com os mesmos dados
4. Clique novamente em **Ver como tabela** para voltar ao grafico

### 8.5 Atualizar Dados

- **Widget individual**: clique em **⋮** > **Atualizar**
- **Painel inteiro**: o indicador "Atualizado" na barra superior mostra quanto
  tempo se passou. Muda de cor:
  - 🟢 Verde: menos de 5 minutos
  - 🟡 Amarelo: 5 a 15 minutos
  - 🔴 Vermelho: mais de 15 minutos

### 8.6 Configurar Atualizacao Automatica

Na barra superior, use a lista suspensa de **intervalo de refresh**:

| Opcao | Comportamento |
|---|---|
| Sem refresh | Dados atualizados somente manualmente |
| 1 min | Atualiza todos os widgets a cada 1 minuto |
| 5 min | Atualiza a cada 5 minutos |
| 15 min | Atualiza a cada 15 minutos |

### 8.7 Salvar o Painel

Clique no botao **Salvar** (icone ✓) na barra superior.
O layout, posicoes e configuracoes sao salvos.

---

## 9. Etapa 7 — Compartilhar e Exportar

### 9.1 Compartilhar com Outros Usuarios

1. Na barra superior do painel, clique no icone **⚙** (engrenagem)
2. Selecione **Compartilhar**
3. Informe o **ID do usuario** que recebera o acesso
4. Escolha o nivel de acesso:
   - `ver` — somente visualizacao
   - `editar` — pode alterar o painel
5. Confirme

O usuario compartilhado vera o painel na aba **Compartilhados** da galeria.

### 9.2 Duplicar um Painel

1. Clique em **⚙** > **Duplicar**
2. Uma copia do painel e criada com todos os widgets
3. Util para criar variacoes (ex.: mesmo layout para outra regiao)

### 9.3 Criar Versao (Snapshot)

1. Clique em **⚙** > **Criar versao**
2. O estado atual do painel e salvo como uma versao
3. Para ver o historico ou restaurar uma versao anterior:
   - Clique em **⚙** > **Historico**
   - Na lista de versoes, clique em **Restaurar** na versao desejada

### 9.4 Exportar Dados

**Widget individual:**
1. Clique em **⋮** do widget
2. Selecione **Exportar CSV** ou **Exportar XLSX**
3. O arquivo e baixado com os dados do widget

**Todos os widgets do painel:**
1. Clique em **⚙** > **Exportar todos (CSV)**
2. Um arquivo CSV consolidado e baixado

### 9.5 Imprimir / Gerar PDF

1. Clique em **⚙** > **Imprimir / PDF**
2. A tela de impressao do navegador abre
3. Selecione **Salvar como PDF** na impressora para gerar um arquivo PDF
4. O cabecalho de impressao inclui o nome do painel e a data/hora

---

## 10. Resolucao de Problemas

### "A consulta excedeu 30 s"

**Causa:** A consulta percorre dados demais (por exemplo, todas as passagens sem periodo).

**Solucao:** Defina um periodo no widget ou nos filtros do painel, ou reduza a quantidade de campos.

### "A medida ... seria multiplicada pela relacao 1:N"

**Causa:** Somar ou contar uma tabela "um" junto com campos de uma tabela "muitos" repetiria cada valor.

**Solucao:** Use COUNT_DISTINCT, MIN ou MAX, ou crie um dataset com a tabela "muitos" como base.

### "Nenhuma tabela encontrada" ao criar Dataset

**Causa:** O Catalogo de Dados esta vazio.

**Solucao:** Peca ao administrador para cadastrar tabelas no Catalogo
(veja [Etapa 1](#3-etapa-1--catalogo-de-dados-admin)).

### Widget mostra "Erro ao carregar dados"

**Causas possiveis:**
- O dataset nao esta publicado (status "Rascunho"). Publique-o primeiro.
- Um campo usado no widget foi removido do dataset. A mensagem informa qual.
- A tabela no banco de dados nao existe ou mudou de nome.

### Preview demora muito (mais de 30 segundos)

**Solucao:**
- Reduza o campo **Limite (Top N)** para um valor menor (ex.: 10)
- Adicione filtros para restringir os dados
- Verifique se a tabela possui indices adequados

### Nao consigo ver os menus do Dashboard Builder

**Causa:** Seu usuario nao tem permissao.

**Solucao:** Peca ao administrador para adicionar seu usuario ao grupo 41 (Analista)
ou 11 (Administrador).

### Widgets nao atualizam automaticamente

**Solucao:** Verifique se o intervalo de refresh esta configurado na barra superior
do painel (padrao: "Sem refresh"). Selecione "1 min", "5 min" ou "15 min".

---

## 11. Glossario

| Termo | Definicao |
|---|---|
| **Agregacao** | Operacao matematica aplicada a uma medida: soma (SUM), media (AVG), contagem (COUNT), minimo (MIN), maximo (MAX) |
| **Catalogo de Dados** | Registro das tabelas e colunas do banco de dados disponiveis para uso em datasets |
| **Cardinalidade** | Tipo de relacionamento entre tabelas: N:1, 1:N ou 1:1 |
| **Dashboard** | Sinonimo de Painel — uma tela com graficos, tabelas e KPIs organizados |
| **Dataset** | Modelo de dados que define quais tabelas, colunas e relacoes alimentam os widgets |
| **Dimensao** | Campo usado para agrupar ou categorizar dados (eixo X de um grafico) |
| **Drill-down** | Recurso que permite clicar em um elemento do grafico para ver dados mais detalhados |
| **Filtro cruzado** | Recurso que permite clicar em um grafico para filtrar automaticamente os outros widgets do painel |
| **GridStack** | Tecnologia que permite arrastar e redimensionar os widgets na grade do painel |
| **KPI** | Key Performance Indicator — indicador numerico destacado (ex.: "Total de Passagens: 12.458") |
| **Medida** | Campo numerico que sera somado, contado ou calculado (eixo Y de um grafico) |
| **Painel** | Tela do dashboard que contem um ou mais widgets organizados em grade |
| **Schema** | Agrupamento logico de tabelas no banco de dados (ex.: `dbo`, `muralha`, `dash`) |
| **Widget** | Componente visual dentro de um painel: grafico, tabela, KPI, mapa, etc. |
