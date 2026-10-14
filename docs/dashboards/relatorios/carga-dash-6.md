# Resultados do Teste de Carga — DASH-6

**Data**: _pendente (executar `scripts/teste-carga-dashboards.ps1`)_
**Ambiente**: Tomcat 9 local + Cube Core (Docker) + SQL Server 10.0.0.200

## Como executar

```powershell
# 1. Inicie o Tomcat e o Cube
..\.setup-gtw\run.ps1
docker compose -f docker-compose.cube.yml up -d

# 2. Faca login na aplicacao e copie o JSESSIONID do cookie

# 3. Execute o teste
pwsh -File scripts/teste-carga-dashboards.ps1 `
    -BaseUrl http://localhost:8080 `
    -Usuarios 10 `
    -DatasetId 1 `
    -Cookie "JSESSIONID=<seu-cookie>"
```

## Cenarios

| # | Cenario | Usuarios | Consultas | Objetivo |
|---|---------|----------|-----------|----------|
| C1 | Concorrente | 10 | 10 (1 cada) | Medir latencia paralela |
| C2 | Refresh simultaneo | 10 | 10 (mesma query) | Avaliar cache hit sob concorrencia |
| C3 | Sequencial com cache | 1 | 50 | Medir taxa de cache hit apos warm-up |
| C4 | Exportacao pesada | 1 | 1 CSV | Medir tempo para export grande |

## Resultados

> **Pendente**: executar o script e colar a saida abaixo.

```
(saida do script sera colada aqui)
```

## Metricas esperadas (meta)

| Metrica | Meta | Resultado |
|---------|------|-----------|
| Media C1 (concorrente) | < 3000ms | _pendente_ |
| P95 C1 | < 5000ms | _pendente_ |
| Erros C1 | 0 | _pendente_ |
| Cache hit rate C3 | > 50% | _pendente_ |
| Export CSV C4 | < 30s | _pendente_ |
