# Testes — Plano 19: Integracao com Sensores Externos

## Pré-requisitos

- Servidor rodando (`..\.setup-gtw\run.ps1`)
- Migracao `20261008_disponibilidade_incidentes.sql` executada
- Usuario autenticado com permissao

## Casos de teste

### T01 — Job sem API key

1. Iniciar servidor sem variavel de ambiente `WAZE_API_KEY`
2. Verificar log do servidor

**Resultado esperado:** Log exibe "WAZE_API_KEY nao configurada; importacao ignorada." — sem erro ou excecao

### T02 — Job com API key (quando disponivel)

1. Configurar `WAZE_API_KEY` como variavel de ambiente
2. Reiniciar servidor
3. Aguardar 30 minutos

**Resultado esperado:** Log exibe "Importados N incidentes." e registros inseridos na tabela

### T03 — Incidente manual para teste visual

1. Inserir incidente de teste:
```sql
INSERT INTO muralha.incidente_externo
(fonte, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia)
VALUES ('CSV', 'ACCIDENT', 'Teste - Acidente Av. Paralela',
        -12.9231, -38.4531, 3, SYSDATETIME());
```
2. Acessar mapa: `/muralha-digital/pages/monitoramento/mapa/index.jsp`

**Resultado esperado:** Circulo vermelho aparece no ponto do incidente

### T04 — Popup do incidente

1. Com incidente visivel no mapa (T03)
2. Clicar no circulo do incidente

**Resultado esperado:** Popup exibe tipo (ACCIDENT), descricao, severidade (3) e data/hora

### T05 — Toggle de camada

1. Acessar mapa com incidentes visiveis
2. Desmarcar switch "Incidentes"
3. Marcar novamente

**Resultado esperado:** Camada de incidentes some e reaparece conforme toggle

### T06 — Cores por tipo de incidente

1. Inserir incidentes de tipos diferentes:
```sql
INSERT INTO muralha.incidente_externo
(fonte, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia)
VALUES
('CSV', 'JAM', 'Congestionamento BR-324', -12.9100, -38.4200, 2, SYSDATETIME()),
('CSV', 'HAZARD', 'Buraco na pista', -12.9400, -38.4700, 1, SYSDATETIME());
```
2. Verificar cores no mapa

**Resultado esperado:** ACCIDENT=vermelho, JAM=laranja, HAZARD=amarelo

### T07 — Constraint de unicidade

1. Inserir mesmo incidente duas vezes (mesmo fonte + id_externo + dt_ocorrencia)

**Resultado esperado:** Segunda insercao falha com violacao de constraint — job ignora duplicatas silenciosamente

### T08 — Listagem de incidentes

1. Chamar `/MuralhaDigital/Incidente?acao=listar`

**Resultado esperado:** JSON com array `incidentes` contendo ate 200 registros ordenados por data decrescente
