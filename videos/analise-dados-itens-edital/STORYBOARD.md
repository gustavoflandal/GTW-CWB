---
format: 1920x1080
duration: 20s
message: "No Módulo de Análise de Dados, o asterisco vermelho marca as funcionalidades que atendem itens do edital"
arc: listicle — Hook → 3 grupos de itens → CTA
audience: equipe do cliente (TRANSALVADOR) e avaliadores do edital
mode: autonomous
music: calm confident corporate tech underscore, light
---

## Video direction

- **Palette:** creme `bg` como fundo; azul do sistema `primary` (#0d75bf) para eyebrows, números de item do TR, bordas de card e barra de progresso; texto `text` quase preto; o **asterisco vermelho** usa `negative` (#dc2626) — é o único vermelho do vídeo e sempre significa "item do edital".
- **Cards:** cada funcionalidade é um card tingido de azul (estilo do preset: 4% fill / 20% borda, sem sombra) com ícone de linha simples (SVG inline), nome em display e o código do item do TR em pill azul; o asterisco vermelho fica colado ao nome, como na tela real.
- **Motion grammar:** eases longas (power3), entradas por slide-up + fade curtas; o asterisco entra sempre por spring-pop pequeno. Cada card revela quando a narração o nomeia — nunca todos em t=0. Holds ficam parados (no máximo um pulso sutil no asterisco).
- **Rhythm:** Frame 1 e Frame 5 são batidas mais calmas; Frames 2–4 são enumerações em cascata. Frame 4 é o mais denso (5 itens), compensado pelo hold do Frame 5.
- **Framing:** F1 centralizado · F2 grade 2×2 · F3 tríptico · F4 lista em faixa larga · F5 centralizado com tooltip.
- **Negative list:** sem gradientes roxos de "IA", sem bokeh, sem navbar/rodapé reais, sem cursor de sistema (exceto o cursor desenhado do F5); sem slideshow (tudo de uma vez e parado) e sem screensaver (tudo flutuando).
- **Caption band:** conteúdo nos 83% superiores.

## Frame 1 — Asterisco = edital

- scene: O título "Módulo de Análise de Dados" entra; um grande asterisco vermelho salta ao lado e a linha "= item do edital" completa a frase
- voiceover: "No Módulo de Análise de Dados, o asterisco vermelho marca itens do edital."
- duration: 4.629s
- transition_in: cut
- status: animated
- src: compositions/frames/01-asterisco-edital.html
- type: hook
- persuasion: Concretization — o símbolo vira a chave de leitura do vídeo
- beat: curiosity
- blueprint: kinetic-type-beats (Adapt)
- focal: o asterisco vermelho gigante
- roles: título "Módulo de Análise de Dados" = foreground · asterisco = foreground subject · eyebrow "GTW · Muralha Digital" = supporting · grade fina azul = background (dim ~40%)
- sfx: pop-soft

narrativeRole: Ensina a chave de leitura — asterisco vermelho significa atendimento a item do edital.
keyMessage: Todo card com asterisco vermelho cumpre um item do TR.

Adapt: keep the spring-pop payoff; a frase se constrói em batidas (título → asterisco → "= item do edital").
Scene 1 (0.0–2.0s): eyebrow pequeno "GTW · MURALHA DIGITAL" e o título "Módulo de Análise de Dados" sobem em per-word reveal, centralizados no terço superior-médio, display grande. Grade hairline azul ao fundo.
Scene 2 (2.0–3.9s): quando a VO diz "asterisco vermelho", um asterisco vermelho enorme (~35% da altura) faz spring-pop à direita do título, glow vermelho suave atrás.
Scene 3 (3.9–4.6s): na palavra "edital", surge abaixo a linha "= item do edital" em azul com accent-line de 60px desenhando; tudo segura parado.

## Frame 2 — Qualidade e prazos

- scene: Grade 2×2 de cards — SLA de Latência, Assertividade, SLA de Pré-processamento (72h) e Pré-Processamento — cada um com asterisco e o item do TR
- voiceover: "SLA de latência, assertividade e prazo do pré-processamento."
- duration: 4.099s
- transition_in: crossfade
- status: animated
- src: compositions/frames/02-qualidade-prazos.html
- type: feature_showcase
- persuasion: Progressive disclosure
- beat: comprehension
- blueprint: grid-card-assemble (Reproduce)
- focal: a grade de 4 cards
- roles: cards = foreground · eyebrow "QUALIDADE E PRAZOS" = supporting · fundo creme com grade hairline = background
- sfx: tick

narrativeRole: Apresenta os indicadores de qualidade e prazo que atendem o edital.
keyMessage: Latência, assertividade e o SLA de 72h são itens exigidos e já estão no módulo.

Cards (nome · ícone · item TR · sublinha curta):
1. SLA de Latência · cronômetro · TR 5.6.2.3 · "recepção em até 4 s"
2. Assertividade · alvo · TR 5.2.2.6 · "meta de 80% de concordância"
3. SLA de Pré-processamento · ampulheta · TR 5.5.2.1 · "prazo de 72 h"
4. Pré-Processamento · funil · TR 5.5.3.1 · "indicadores do pré-processamento"

Scene 1 (0.0–1.6s): eyebrow "QUALIDADE E PRAZOS" no topo à esquerda; card 1 sobe no quadrante superior esquerdo ao som de "SLA de Latência", asterisco faz spring-pop.
Scene 2 (1.6–2.5s): card 2 entra no quadrante superior direito em "Assertividade".
Scene 3 (2.5–3.6s): card 3 entra em "prazo" (2.6s) e card 4 em "pré-processamento" (3.1s); a pill "72 h" do card 3 faz count-up 0→72.
Scene 4 (3.6–4.1s): a grade completa segura parada; os 4 asteriscos fazem um único pulso sincronizado.

## Frame 3 — Equipamentos e imagens

- scene: Tríptico de cards — Disponibilidade, Time-lapse e Integridade das Imagens (SHA-256)
- voiceover: "Disponibilidade, time-lapse e integridade das imagens."
- duration: 3.796s
- transition_in: crossfade
- status: animated
- src: compositions/frames/03-equipamentos-imagens.html
- type: feature_showcase
- persuasion: Progressive disclosure
- beat: comprehension
- blueprint: grid-card-assemble (Adapt)
- focal: os três cards em tríptico
- roles: cards = foreground · eyebrow "EQUIPAMENTOS E IMAGENS" = supporting · fundo creme = background
- sfx: tick

narrativeRole: Mostra o monitoramento dos equipamentos e a evidência das imagens.
keyMessage: Estado dos equipamentos, vídeos time-lapse e conferência SHA-256 atendem o edital.

Adapt: keep the staggered cascade; a grade vira tríptico horizontal de 3 cards altos.
Cards: 1. Disponibilidade · sinal · TR 5.2.7.1 · "online, offline e uptime 24 h" — 2. Time-lapse · play/filme · TR 5.7.7.1 · "vídeo das passagens" — 3. Integridade das Imagens · escudo com check · TR 5.2.1.2 · "SHA-256 conferido"
Scene 1 (0.0–1.3s): eyebrow no topo; card 1 sobe à esquerda em "Disponibilidade", com um ponto verde online piscando uma vez.
Scene 2 (1.3–2.2s): card 2 sobe ao centro em "Time-lapse".
Scene 3 (2.2–3.8s): card 3 sobe à direita em "integridade das imagens"; um hash curto "a3f9…e1" digita-se dentro do card e um check verde aparece; hold.

## Frame 4 — Gestão e rastreabilidade

- scene: Lista em faixa larga com cinco linhas — Log de Auditoria, Logs Técnicos, Tipos de Alerta, Relatórios Gerenciais, Análises (Superset) — cada uma com asterisco e item do TR
- voiceover: "Auditoria, logs técnicos, alertas, relatórios gerenciais e análises."
- duration: 5.024s
- transition_in: crossfade
- status: animated
- src: compositions/frames/04-gestao-rastreabilidade.html
- type: feature_showcase
- persuasion: Accumulation
- beat: comprehension
- blueprint: grid-card-assemble (Adapt)
- focal: a lista vertical que acumula
- roles: linhas = foreground · eyebrow "GESTÃO E RASTREABILIDADE" = supporting · fundo creme = background
- sfx: tick

narrativeRole: Fecha a enumeração com os itens de gestão, auditoria e análise.
keyMessage: Auditoria, logs, alertas, relatórios e a plataforma de análises também cumprem o TR.

Adapt: keep the accumulating cascade; vira uma lista vertical de linhas largas (ícone · nome + asterisco · pill do TR à direita).
Linhas: Log de Auditoria · TR 5.1.10 — Logs Técnicos · TR 5.4.7.1 — Tipos de Alerta · TR 5.3.1.3 — Relatórios Gerenciais · TR 5.4.3.7 — Análises (Superset) · TR 5.4.2.1
Scene 1 (0.0–0.9s): eyebrow à esquerda; linha 1 desliza da esquerda em "Auditoria".
Scene 2 (0.9–4.4s): linhas 2–5 entram quando a VO nomeia cada uma — Logs Técnicos 0.9s, Tipos de Alerta 2.1s ("alertas"), Relatórios Gerenciais 3.0s, Análises 4.35s.
Scene 3 (4.4–5.0s): lista completa parada; pill de contagem "12 itens do edital" aparece no canto superior direito.

## Frame 5 — Passe o mouse

- scene: Um card "Análises" com asterisco; um cursor desenhado pousa sobre o asterisco e abre o tooltip "Edital · TR 5.4.2.1"
- voiceover: "Passe o mouse no asterisco para ver o item."
- duration: 2.499s
- transition_in: crossfade
- status: animated
- src: compositions/frames/05-passe-o-mouse.html
- type: cta
- persuasion: Demonstration
- beat: resolution
- blueprint: compose
- focal: o tooltip sobre o asterisco
- roles: card = foreground · cursor desenhado = supporting · tooltip escuro = foreground subject · fundo creme = background
- sfx: click-soft

narrativeRole: Ensina como ver qual item do edital cada card atende.
keyMessage: O tooltip do asterisco mostra o número do item do TR.

Scene 1 (0.0–0.9s): um card grande centralizado "Análises" com ícone de gráfico e o asterisco vermelho; um cursor desenhado entra da direita inferior e desliza até o asterisco.
Scene 2 (0.9–1.6s): ao pousar ("asterisco"), tooltip escuro arredondado faz spring-pop acima: "Edital · TR 5.4.2.1".
Scene 3 (1.6–2.5s): hold parado; barra de progresso azul fina completa na base da área de conteúdo.
