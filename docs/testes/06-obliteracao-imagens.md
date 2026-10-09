# Testes — Plano 06: Obliteração de Imagens (LGPD)

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_obliteracao.sql` executada no banco GTW_MURALHA_DEV
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado com acesso à fila de análise
- Ao menos uma infração com imagem disponível na fila

---

## T01 — Estrutura do banco criada

**Procedimento:** Executar no SSMS:
```sql
SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA='muralha' AND TABLE_NAME='veiculo_tempo_real_imagem'
  AND COLUMN_NAME IN ('obliterada','id_original');

SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA='muralha' AND TABLE_NAME='infracao_imagem_obliteracao';
```

**Resultado esperado:** 2 colunas retornadas na primeira query; 1 tabela na segunda.

**Status:** ⏳ Aguarda execução da migração

---

## T02 — Botões de obliteração visíveis na tela de análise

**Procedimento:**
1. Acessar a fila de análise e abrir uma infração
2. Verificar presença dos botões "Iniciar Obliteração", "Aplicar" e "Cancelar" abaixo dos controles de brilho/contraste

**Resultado esperado:** Três botões visíveis abaixo da barra de filtros de imagem.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Canvas de seleção de área

**Procedimento:**
1. Com imagem carregada, clicar "Iniciar Obliteração"
2. Arrastar o mouse sobre a imagem para desenhar um retângulo
3. Verificar aparecimento do canvas overlay com a área selecionada em preto semitransparente

**Resultado esperado:** Canvas overlay aparece; cursor muda para crosshair; retângulo vermelho durante drag, preto após soltar.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Aplicar obliteração persiste no banco

**Procedimento:**
1. Selecionar uma área sobre o rosto na imagem
2. Clicar "Aplicar" → confirmar no SweetAlert2
3. Verificar no banco:
```sql
SELECT id, obliterada, id_original
FROM muralha.veiculo_tempo_real_imagem
ORDER BY id DESC;

SELECT * FROM muralha.infracao_imagem_obliteracao ORDER BY id DESC;
```

**Resultado esperado:**
- Nova linha com `obliterada=1` e `id_original` preenchido
- Registro em `infracao_imagem_obliteracao` com `tipo='M'` e `revertida=0`
- Alert de sucesso na tela

**Status:** ⏳ Aguarda teste manual

---

## T05 — Original intacto após obliteração

**Procedimento:**
1. Após T04, verificar linha original no banco:
```sql
SELECT id, obliterada FROM muralha.veiculo_tempo_real_imagem
WHERE obliterada = 0 AND id = <id_original>;
```
2. Carregar imagem original pelo endpoint:
`GET /MuralhaDigital/VeiculoTempoReal?acao=obterImagem&id=<uuid>`

**Resultado esperado:** Linha original permanece com `obliterada=0`; bytes não alterados.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Cancelar obliteração limpa o canvas

**Procedimento:**
1. Iniciar obliteração, desenhar retângulo
2. Clicar "Cancelar"

**Resultado esperado:** Canvas removido do DOM; retângulos desmarcados; sem chamada ao servidor.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Registro de auditoria gerado

**Procedimento:**
1. Após T04, verificar log de auditoria:
```sql
SELECT TOP 5 * FROM muralha.log_auditoria
WHERE modulo='Imagem' AND acao='obliterar'
ORDER BY dt_evento DESC;
```

**Resultado esperado:** Registro com `acao='obliterar'` e `id_referencia` igual ao ID da imagem original.

**Status:** ⏳ Aguarda teste manual

---

## T08 — idImagem retornado na fila

**Procedimento:**
1. Verificar resposta JSON do endpoint:
`GET /MuralhaDigital/InfracaoAnalise?acao=proximaFila`
2. Checar presença do campo `idImagem` no objeto `infracao`

**Resultado esperado:** JSON contém `"idImagem": <número>` quando a infração possui imagem.

**Status:** ⏳ Aguarda teste manual
