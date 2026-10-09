# Testes — Plano 06: Obliteração de Imagens (LGPD)

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_complementar.sql` executada no banco GTW_MURALHA_DEV
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado com acesso à fila de análise
- Ao menos uma infração com imagem disponível na fila

---

## T01 — Estrutura do banco criada

**Procedimento:** Executar no SSMS:
```sql
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA='muralha' AND TABLE_NAME IN ('vtr_imagem_obliterada','infracao_imagem_obliteracao');
```

**Resultado esperado:** 2 tabelas retornadas.

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
SELECT id, id_imagem_original, id_veiculo_tempo_real, dt_criacao
FROM muralha.vtr_imagem_obliterada
ORDER BY dt_criacao DESC;

SELECT * FROM muralha.infracao_imagem_obliteracao ORDER BY id DESC;
```

**Resultado esperado:**
- Nova linha em `vtr_imagem_obliterada` com `id_imagem_original` preenchido
- Registro em `infracao_imagem_obliteracao` com `tipo='M'` e `revertida=0`
- Alert de sucesso na tela

**Status:** ⏳ Aguarda teste manual

---

## T05 — Original intacto após obliteração

**Procedimento:**
1. Após T04, verificar que a imagem original em `veiculo_tempo_real_imagem` não foi alterada:
```sql
SELECT id, DATALENGTH(imagem) AS tamanho
FROM muralha.veiculo_tempo_real_imagem
WHERE id = '<id_original>';
```

**Resultado esperado:** Linha original permanece inalterada; tamanho da imagem igual ao anterior.

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

**Resultado esperado:** JSON contém `"idImagem":"<uuid>"` quando a infração possui imagem.

**Status:** ⏳ Aguarda teste manual
