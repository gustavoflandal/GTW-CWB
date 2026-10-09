# Testes — Plano 17: Time-lapse de Passagens

## Pré-requisitos

- Servidor rodando (`..\.setup-gtw\run.ps1`)
- Usuario autenticado com permissao
- Dados existentes em `veiculo_tempo_real` com imagens associadas

## Casos de teste

### T01 — Carregar lista de equipamentos

1. Acessar `/muralha-digital/pages/monitoramento/timelapse/index.jsp`
2. Verificar que o combo "Equipamento" carrega locais ativos
3. Cada opcao mostra nome + ID local

**Resultado esperado:** Lista preenchida com locais de `local_vigente` onde `desativado = 0`

### T02 — Buscar frames com periodo valido

1. Selecionar equipamento com passagens conhecidas
2. Definir periodo de inicio e fim que contenha passagens
3. Clicar no botao de busca

**Resultado esperado:** Primeira imagem exibe no player, slider configurado, contador "1/N"

### T03 — Buscar frames sem resultados

1. Selecionar equipamento
2. Definir periodo futuro (sem passagens)
3. Clicar buscar

**Resultado esperado:** Mensagem "Nenhum frame encontrado para o periodo selecionado"

### T04 — Controles de reproducao

1. Carregar frames (T02)
2. Clicar Play → imagens avancam automaticamente
3. Clicar Pause → animacao para
4. Clicar anterior/proximo → navega frame-a-frame
5. Arrastar slider → pula para frame especifico

**Resultado esperado:** Todos os controles funcionam. Icone alterna entre play/pause

### T05 — Ajuste de FPS

1. Carregar frames
2. Ajustar slider FPS para 1 → animacao lenta
3. Ajustar para 10 → animacao rapida
4. Verificar label atualiza ("X fps")

**Resultado esperado:** Velocidade da animacao muda conforme FPS selecionado

### T06 — Informacoes do frame

1. Navegar entre frames
2. Verificar painel lateral com data/hora, placa e numero do frame

**Resultado esperado:** Informacoes atualizam a cada mudanca de frame

### T07 — Limite de 200 frames

1. Selecionar periodo longo com mais de 200 passagens
2. Buscar frames

**Resultado esperado:** Retorna no maximo 200 frames (TOP 200 no SQL)
