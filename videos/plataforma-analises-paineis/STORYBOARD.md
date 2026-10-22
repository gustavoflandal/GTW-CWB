---
format: 1920x1080
duration: 120s
message: "A Plataforma de Análises do GTW reúne 14 dashboards interativos — 5 de demonstração e 9 operacionais"
arc: Hook (mosaico) → 5 painéis de demonstração → 9 painéis operacionais → CTA (filtro cruzado)
audience: equipe do cliente (TRANSALVADOR) e avaliadores do edital
mode: autonomous
music: calm confident corporate tech underscore, light
---

## Video direction

- **Palette:** `bg` creme; `primary` #0d75bf para eyebrows, contadores, molduras e barra de progresso; texto `text`. Sem segundo acento.
- **Painel = captura real.** Cada quadro de painel mostra a captura de página inteira do Superset (`assets/<slug>.png`) numa moldura de janela clara, à direita (~62% da largura). **Regra do usuário: sempre mostrar o painel inteiro com todos os gráficos** — cada quadro abre com o dashboard inteiro visível (fit na altura), percorre o painel em leitura e volta ao painel inteiro no final.
- **Coluna de texto** à esquerda (~30%): eyebrow com grupo e posição, título do painel em display, descrição curta (campo `descricao` do painel). A narração não é repetida como texto.
- **Motion grammar:** eases longas (power3; sine.inOut no percurso da câmera); entradas slide-up + fade curtas; o percurso pela captura é um único movimento contínuo e lento. Holds sem movimento extra.
- **Consistência:** os 14 quadros de painel usam o mesmo layout e ritmo (template).
- **Negative list:** sem recriar gráficos em HTML (a captura é a fonte da verdade), sem gradientes de "IA", sem slideshow parado, sem tudo flutuando.
- **Caption band:** todo o conteúdo acima de y ≤ 900.

## Frame 1 — Plataforma de Análises

- scene: Mosaico com as 14 capturas dos painéis se monta em grade; título "Plataforma de Análises" e contadores 14 · 5 · 9
- voiceover: "Na tela de Análise de Dados, a Plataforma de Análises reúne 14 dashboards interativos: 5 de demonstração e 9 operacionais."
- duration: 8.788s
- transition_in: cut
- status: animated
- src: compositions/frames/01-plataforma-analises.html
- type: hook
- beat: overview
- blueprint: grid-card-assemble (Adapt)
- asset_candidates: assets/<slug>.png — as 14 capturas dos painéis, como miniaturas do mosaico
- focal: o mosaico das 14 miniaturas
- roles: miniaturas = foreground · título + contadores = foreground (coluna esquerda) · grade hairline = background

narrativeRole: Apresenta a plataforma e o tamanho do catálogo.
keyMessage: São 14 dashboards interativos, 5 de demonstração e 9 operacionais.

Scene 1 (0.0–2.0s): eyebrow "ANÁLISE DE DADOS · TR 5.4.2.1" e título "Plataforma de Análises" sobem à esquerda.
Scene 2 (2.0–4.6s): as 14 miniaturas (topo de cada captura) entram em cascata numa grade 4×4 à direita; as 5 de demonstração com borda azul mais forte.
Scene 3 (4.6–8.8s): contadores fazem count-up: "14 dashboards" (≈3.8s), "5 demonstração" (≈4.9s), "9 operacionais" (≈5.9s); hold.

## Frame 2 — Fluxo Veicular

- scene: Captura real do painel "Fluxo Veicular" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Fluxo Veicular: passagens capturadas pelos equipamentos, por hora, com velocidade média, placas Mercosul e mapa de calor semanal."
- duration: 8.748s
- transition_in: crossfade
- status: animated
- src: compositions/frames/02-demo-fluxo-veicular.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/demo-fluxo-veicular.png — captura de página inteira do dashboard "Fluxo Veicular" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: demo-fluxo-veicular
- panel_eyebrow: DEMONSTRAÇÃO · DADOS REAIS · 1/5
- panel_title: Fluxo Veicular
- panel_desc: Passagens capturadas pelos equipamentos, agregadas por hora.

narrativeRole: Mostra o painel "Fluxo Veicular" inteiro e o que ele mede.
keyMessage: Passagens capturadas pelos equipamentos, agregadas por hora.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 3 — Fiscalização e Infrações

- scene: Captura real do painel "Fiscalização e Infrações" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Fiscalização e Infrações: infrações da fiscalização eletrônica por categoria, tipo, equipamento e faixa de excesso de velocidade."
- duration: 8.763s
- transition_in: crossfade
- status: animated
- src: compositions/frames/03-demo-infracoes.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/demo-infracoes.png — captura de página inteira do dashboard "Fiscalização e Infrações" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: demo-infracoes
- panel_eyebrow: DEMONSTRAÇÃO · DADOS REAIS · 2/5
- panel_title: Fiscalização e Infrações
- panel_desc: Infrações registradas pelos equipamentos de fiscalização eletrônica.

narrativeRole: Mostra o painel "Fiscalização e Infrações" inteiro e o que ele mede.
keyMessage: Infrações registradas pelos equipamentos de fiscalização eletrônica.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 4 — Tráfego Histórico

- scene: Captura real do painel "Tráfego Histórico" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Tráfego Histórico: série histórica de volume, velocidade e leitura de placas, com a composição da frota por equipamento."
- duration: 7.746s
- transition_in: crossfade
- status: animated
- src: compositions/frames/04-demo-trafego-historico.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/demo-trafego-historico.png — captura de página inteira do dashboard "Tráfego Histórico" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: demo-trafego-historico
- panel_eyebrow: DEMONSTRAÇÃO · DADOS REAIS · 3/5
- panel_title: Tráfego Histórico
- panel_desc: Série histórica de volume, velocidade e leitura de placas por equipamento.

narrativeRole: Mostra o painel "Tráfego Histórico" inteiro e o que ele mede.
keyMessage: Série histórica de volume, velocidade e leitura de placas por equipamento.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 5 — Mobilidade Urbana (Waze)

- scene: Captura real do painel "Mobilidade Urbana (Waze)" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Mobilidade Urbana: ocorrências de trânsito reportadas no Waze, no mapa de Salvador, por tipo, via e horário."
- duration: 7.191s
- transition_in: crossfade
- status: animated
- src: compositions/frames/05-demo-mobilidade-waze.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/demo-mobilidade-waze.png — captura de página inteira do dashboard "Mobilidade Urbana (Waze)" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: demo-mobilidade-waze
- panel_eyebrow: DEMONSTRAÇÃO · DADOS REAIS · 4/5
- panel_title: Mobilidade Urbana (Waze)
- panel_desc: Ocorrências de trânsito reportadas por usuários do Waze.

narrativeRole: Mostra o painel "Mobilidade Urbana (Waze)" inteiro e o que ele mede.
keyMessage: Ocorrências de trânsito reportadas por usuários do Waze.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 6 — Alertas de Veículos de Interesse

- scene: Captura real do painel "Alertas de Veículos de Interesse" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Alertas de Veículos de Interesse: veículos monitorados, furtados, roubados e clonados, por tipo, criticidade e status."
- duration: 8.334s
- transition_in: crossfade
- status: animated
- src: compositions/frames/06-demo-alertas-veiculos.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/demo-alertas-veiculos.png — captura de página inteira do dashboard "Alertas de Veículos de Interesse" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: demo-alertas-veiculos
- panel_eyebrow: DEMONSTRAÇÃO · DADOS REAIS · 5/5
- panel_title: Alertas de Veículos de Interesse
- panel_desc: Alertas para veículos monitorados, furtados, roubados e clonados.

narrativeRole: Mostra o painel "Alertas de Veículos de Interesse" inteiro e o que ele mede.
keyMessage: Alertas para veículos monitorados, furtados, roubados e clonados.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 7 — KPIs Operacionais

- scene: Captura real do painel "KPIs Operacionais" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "KPIs Operacionais: passagens, infrações, alertas, disponibilidade, SLA de latência e assertividade, com a evolução mensal."
- duration: 9.383s
- transition_in: crossfade
- status: animated
- src: compositions/frames/07-kpis-operacionais.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/kpis-operacionais.png — captura de página inteira do dashboard "KPIs Operacionais" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: kpis-operacionais
- panel_eyebrow: PAINEL OPERACIONAL · 1/9
- panel_title: KPIs Operacionais
- panel_desc: Visão consolidada dos principais indicadores operacionais.

narrativeRole: Mostra o painel "KPIs Operacionais" inteiro e o que ele mede.
keyMessage: Visão consolidada dos principais indicadores operacionais.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 8 — Assertividade — Dupla Análise

- scene: Captura real do painel "Assertividade — Dupla Análise" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Assertividade da Dupla Análise: concordância entre a pré-classificação e a validação, com aprovadas, reprovadas e desempates."
- duration: 7.99s
- transition_in: crossfade
- status: animated
- src: compositions/frames/08-assertividade-dupla-analise.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/assertividade-dupla-analise.png — captura de página inteira do dashboard "Assertividade — Dupla Análise" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: assertividade-dupla-analise
- panel_eyebrow: PAINEL OPERACIONAL · 2/9
- panel_title: Assertividade — Dupla Análise
- panel_desc: Concordância entre a pré-classificação e a validação dos operadores.

narrativeRole: Mostra o painel "Assertividade — Dupla Análise" inteiro e o que ele mede.
keyMessage: Concordância entre a pré-classificação e a validação dos operadores.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 9 — SLA de Latência

- scene: Captura real do painel "SLA de Latência" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "SLA de Latência: percentual de janelas no prazo, latência diária e as violações do limite."
- duration: 7.167s
- transition_in: crossfade
- status: animated
- src: compositions/frames/09-sla-latencia.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/sla-latencia.png — captura de página inteira do dashboard "SLA de Latência" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: sla-latencia
- panel_eyebrow: PAINEL OPERACIONAL · 3/9
- panel_title: SLA de Latência
- panel_desc: Cumprimento do SLA entre a passagem e a chegada ao servidor.

narrativeRole: Mostra o painel "SLA de Latência" inteiro e o que ele mede.
keyMessage: Cumprimento do SLA entre a passagem e a chegada ao servidor.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 10 — Disponibilidade de Equipamentos

- scene: Captura real do painel "Disponibilidade de Equipamentos" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Disponibilidade de Equipamentos: horas com captura, dias sem captura e o ranking dos equipamentos."
- duration: 7.177s
- transition_in: crossfade
- status: animated
- src: compositions/frames/10-disponibilidade-equipamentos.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/disponibilidade-equipamentos.png — captura de página inteira do dashboard "Disponibilidade de Equipamentos" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: disponibilidade-equipamentos
- panel_eyebrow: PAINEL OPERACIONAL · 4/9
- panel_title: Disponibilidade de Equipamentos
- panel_desc: Disponibilidade medida pelas horas com captura.

narrativeRole: Mostra o painel "Disponibilidade de Equipamentos" inteiro e o que ele mede.
keyMessage: Disponibilidade medida pelas horas com captura.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 11 — Pré-processamento de Imagens

- scene: Captura real do painel "Pré-processamento de Imagens" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Pré-processamento de Imagens: recebidas, disponibilizadas, aproveitamento, prazo de 72 horas e produtividade por operador."
- duration: 8.887s
- transition_in: crossfade
- status: animated
- src: compositions/frames/11-preprocessamento-imagens.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/preprocessamento-imagens.png — captura de página inteira do dashboard "Pré-processamento de Imagens" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: preprocessamento-imagens
- panel_eyebrow: PAINEL OPERACIONAL · 5/9
- panel_title: Pré-processamento de Imagens
- panel_desc: Volumes, aproveitamento, prazo e produtividade do pré-processamento.

narrativeRole: Mostra o painel "Pré-processamento de Imagens" inteiro e o que ele mede.
keyMessage: Volumes, aproveitamento, prazo e produtividade do pré-processamento.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 12 — Alertas

- scene: Captura real do painel "Alertas" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Alertas: total, críticos e pendentes, a evolução por tipo e o percentual tratado."
- duration: 7.159s
- transition_in: crossfade
- status: animated
- src: compositions/frames/12-alertas.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/alertas.png — captura de página inteira do dashboard "Alertas" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: alertas
- panel_eyebrow: PAINEL OPERACIONAL · 6/9
- panel_title: Alertas
- panel_desc: Alertas gerados pela Muralha Digital e seu tratamento.

narrativeRole: Mostra o painel "Alertas" inteiro e o que ele mede.
keyMessage: Alertas gerados pela Muralha Digital e seu tratamento.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 13 — Infrações

- scene: Captura real do painel "Infrações" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Infrações: total, excesso médio de velocidade, enquadramentos e o ranking de equipamentos."
- duration: 7.164s
- transition_in: crossfade
- status: animated
- src: compositions/frames/13-infracoes.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/infracoes.png — captura de página inteira do dashboard "Infrações" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: infracoes
- panel_eyebrow: PAINEL OPERACIONAL · 7/9
- panel_title: Infrações
- panel_desc: Infrações registradas pelos equipamentos de fiscalização.

narrativeRole: Mostra o painel "Infrações" inteiro e o que ele mede.
keyMessage: Infrações registradas pelos equipamentos de fiscalização.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 14 — Passagens Veiculares

- scene: Captura real do painel "Passagens Veiculares" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Passagens Veiculares: passagens por hora do dia, por faixa e por classificação, com o ranking de equipamentos."
- duration: 7.187s
- transition_in: crossfade
- status: animated
- src: compositions/frames/14-passagens-veiculares.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/passagens-veiculares.png — captura de página inteira do dashboard "Passagens Veiculares" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: passagens-veiculares
- panel_eyebrow: PAINEL OPERACIONAL · 8/9
- panel_title: Passagens Veiculares
- panel_desc: Passagens por hora, faixa e classificação.

narrativeRole: Mostra o painel "Passagens Veiculares" inteiro e o que ele mede.
keyMessage: Passagens por hora, faixa e classificação.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 15 — Mapa — Equipamentos, Áreas e Infrações

- scene: Captura real do painel "Mapa — Equipamentos, Áreas e Infrações" inteira na moldura, depois percorrida de cima a baixo e de volta ao painel inteiro
- voiceover: "Mapa: equipamentos cadastrados, áreas monitoradas, passagens e infrações por equipamento."
- duration: 7.174s
- transition_in: crossfade
- status: animated
- src: compositions/frames/15-mapa-equipamentos-infracoes.html
- type: feature_showcase
- beat: tour
- blueprint: device-surface-showcase (Adapt)
- asset_candidates: assets/mapa-equipamentos-infracoes.png — captura de página inteira do dashboard "Mapa — Equipamentos, Áreas e Infrações" (Superset, 1920 px de largura, todos os gráficos)
- focal: a captura do painel
- roles: captura na moldura = foreground subject · coluna de texto = supporting · grade hairline = background
- panel_slug: mapa-equipamentos-infracoes
- panel_eyebrow: PAINEL OPERACIONAL · 9/9
- panel_title: Mapa — Equipamentos, Áreas e Infrações
- panel_desc: Equipamentos, áreas monitoradas, passagens e infrações no mapa.

narrativeRole: Mostra o painel "Mapa — Equipamentos, Áreas e Infrações" inteiro e o que ele mede.
keyMessage: Equipamentos, áreas monitoradas, passagens e infrações no mapa.

Adapt: keep the held floating-window hero; a câmera percorre uma única captura longa em vez de trocar telas.
Scene 1 (0.0–2.4s): moldura entra já com o painel INTEIRO visível (fit na altura, todos os gráficos); eyebrow, título e descrição sobem à esquerda.
Scene 2 (2.4–5.6s): push-in até a largura da moldura e pan contínuo de cima a baixo pela captura enquanto a VO enumera os indicadores.
Scene 3 (5.6–end): pull-back ao painel inteiro e hold.

## Frame 16 — Filtros e filtro cruzado

- scene: O mosaico das 14 capturas volta; um cursor desenhado clica numa miniatura e as demais recebem o destaque do filtro; frase final
- voiceover: "São 14 painéis interativos, com filtros e filtro cruzado: clique em um gráfico para filtrar o painel inteiro."
- duration: 7.377s
- transition_in: crossfade
- status: animated
- src: compositions/frames/16-filtro-cruzado.html
- type: cta
- beat: resolution
- blueprint: grid-card-assemble (Adapt)
- asset_candidates: assets/<slug>.png — as 14 capturas como miniaturas
- focal: o mosaico com o destaque do filtro cruzado
- roles: miniaturas = foreground · pills = foreground · grade = background

narrativeRole: Fecha mostrando que todos os painéis são interativos.
keyMessage: Filtros e filtro cruzado em todos os 14 painéis.

Scene 1 (0.0–2.4s): grade 4×4 das 14 miniaturas montada; "14 painéis interativos" à esquerda.
Scene 2 (2.4–5.0s): pills "Filtros" (≈2.6s) e "Filtro cruzado" (≈3.3s) fazem spring-pop; um cursor desenhado pousa numa miniatura e as outras ganham contorno azul em onda.
Scene 3 (5.0–end): hold com o caminho "Análise de Dados → Análises"; fim do vídeo.
