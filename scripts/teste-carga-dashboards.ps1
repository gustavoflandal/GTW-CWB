# teste-carga-dashboards.ps1
# Simula N usuarios concorrentes consultando o proxy de dashboards.
# Uso: pwsh -File scripts/teste-carga-dashboards.ps1 [-BaseUrl http://localhost:8080] [-Usuarios 10] [-DatasetId 1]

param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$Usuarios = 10,
    [int]$DatasetId = 1,
    [string]$Cookie = "JSESSIONID=CHANGEME"
)

$ErrorActionPreference = "Stop"

Write-Host "===== Teste de Carga - Dashboard Builder =====" -ForegroundColor Cyan
Write-Host "URL base : $BaseUrl"
Write-Host "Usuarios : $Usuarios"
Write-Host "Dataset  : $DatasetId"
Write-Host ""

# ---------- Helper ----------
function Invoke-Consulta {
    param([int]$UserId, [string]$Cenario)
    $body = @{
        datasetId = $DatasetId
        query = @{
            measures = @("total_passagens")
            dimensions = @("equipamento")
            limit = 1000
        }
    } | ConvertTo-Json -Depth 5

    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $resp = Invoke-RestMethod -Uri "$BaseUrl/api/dash/consultas" `
            -Method POST -ContentType "application/json; charset=UTF-8" `
            -Body $body -Headers @{ Cookie = $Cookie } `
            -TimeoutSec 60
        $sw.Stop()
        return [PSCustomObject]@{
            Usuario  = $UserId
            Cenario  = $Cenario
            TempoMs  = $sw.ElapsedMilliseconds
            CacheHit = if ($resp.cacheHit) { $true } else { $false }
            Erro     = $null
            Linhas   = if ($resp.data) { $resp.data.Count } else { 0 }
        }
    } catch {
        $sw.Stop()
        return [PSCustomObject]@{
            Usuario  = $UserId
            Cenario  = $Cenario
            TempoMs  = $sw.ElapsedMilliseconds
            CacheHit = $false
            Erro     = $_.Exception.Message
            Linhas   = 0
        }
    }
}

function Show-Estatisticas {
    param([array]$Results, [string]$Titulo)

    $ok = $Results | Where-Object { -not $_.Erro }
    $erros = $Results | Where-Object { $_.Erro }
    $tempos = $ok | ForEach-Object { $_.TempoMs } | Sort-Object

    Write-Host ""
    Write-Host "--- $Titulo ---" -ForegroundColor Yellow
    Write-Host "  Total requisicoes: $($Results.Count)"
    Write-Host "  Sucesso: $($ok.Count)  |  Erros: $($erros.Count)"

    if ($tempos.Count -gt 0) {
        $media = [math]::Round(($tempos | Measure-Object -Average).Average, 0)
        $p95idx = [math]::Floor($tempos.Count * 0.95)
        $p99idx = [math]::Floor($tempos.Count * 0.99)
        if ($p95idx -ge $tempos.Count) { $p95idx = $tempos.Count - 1 }
        if ($p99idx -ge $tempos.Count) { $p99idx = $tempos.Count - 1 }

        $cacheHits = ($ok | Where-Object { $_.CacheHit }).Count
        $cacheRate = if ($ok.Count -gt 0) { [math]::Round($cacheHits / $ok.Count * 100, 1) } else { 0 }

        Write-Host "  Media: ${media}ms  |  P95: $($tempos[$p95idx])ms  |  P99: $($tempos[$p99idx])ms"
        Write-Host "  Min: $($tempos[0])ms  |  Max: $($tempos[-1])ms"
        Write-Host "  Cache hit rate: ${cacheRate}% ($cacheHits/$($ok.Count))"
    }

    if ($erros.Count -gt 0) {
        Write-Host "  Erros:" -ForegroundColor Red
        $erros | ForEach-Object { Write-Host "    [$($_.Usuario)] $($_.Erro)" -ForegroundColor Red }
    }

    return @{
        Total     = $Results.Count
        Sucesso   = $ok.Count
        Erros     = $erros.Count
        Media     = if ($tempos.Count -gt 0) { [math]::Round(($tempos | Measure-Object -Average).Average, 0) } else { 0 }
        P95       = if ($tempos.Count -gt 0) { $tempos[[math]::Floor($tempos.Count * 0.95)] } else { 0 }
        P99       = if ($tempos.Count -gt 0) { $tempos[[math]::Min([math]::Floor($tempos.Count * 0.99), $tempos.Count - 1)] } else { 0 }
    }
}

# ---------- Cenario 1: N usuarios, 1 consulta cada ----------
Write-Host "`n[Cenario 1] $Usuarios usuarios, 1 consulta cada" -ForegroundColor Green

$jobs1 = 1..$Usuarios | ForEach-Object {
    $uid = $_
    Start-ThreadJob -ScriptBlock {
        param($uid, $BaseUrl, $DatasetId, $Cookie)
        . $using:Function:Invoke-Consulta -UserId $uid -Cenario "C1"
    } -ArgumentList $uid, $BaseUrl, $DatasetId, $Cookie
}

$results1 = $jobs1 | Wait-Job | Receive-Job
$jobs1 | Remove-Job -Force
$stats1 = Show-Estatisticas -Results $results1 -Titulo "Cenario 1 — N usuarios concorrentes"

# ---------- Cenario 2: N usuarios, refresh simultaneo ----------
Write-Host "`n[Cenario 2] $Usuarios usuarios, refresh simultaneo (mesma consulta)" -ForegroundColor Green

$jobs2 = 1..$Usuarios | ForEach-Object {
    $uid = $_
    Start-ThreadJob -ScriptBlock {
        param($uid, $BaseUrl, $DatasetId, $Cookie)
        . $using:Function:Invoke-Consulta -UserId $uid -Cenario "C2"
    } -ArgumentList $uid, $BaseUrl, $DatasetId, $Cookie
}

$results2 = $jobs2 | Wait-Job | Receive-Job
$jobs2 | Remove-Job -Force
$stats2 = Show-Estatisticas -Results $results2 -Titulo "Cenario 2 — Refresh simultaneo"

# ---------- Cenario 3: 50 consultas sequenciais (cache) ----------
Write-Host "`n[Cenario 3] 50 consultas sequenciais no mesmo dataset (cache)" -ForegroundColor Green

$results3 = @()
for ($i = 1; $i -le 50; $i++) {
    $results3 += Invoke-Consulta -UserId $i -Cenario "C3"
}
$stats3 = Show-Estatisticas -Results $results3 -Titulo "Cenario 3 — Sequencial com cache"

# ---------- Cenario 4: Consulta pesada (exportacao) ----------
Write-Host "`n[Cenario 4] Consulta pesada (100k linhas)" -ForegroundColor Green

$sw4 = [System.Diagnostics.Stopwatch]::StartNew()
try {
    $resp4 = Invoke-WebRequest -Uri "$BaseUrl/api/dash/exportar?acao=csvWidget&id=1" `
        -Headers @{ Cookie = $Cookie } -TimeoutSec 120
    $sw4.Stop()
    Write-Host "  Exportacao CSV: $($sw4.ElapsedMilliseconds)ms, $([math]::Round($resp4.Content.Length / 1024, 1)) KB"
} catch {
    $sw4.Stop()
    Write-Host "  Exportacao CSV: ERRO - $($_.Exception.Message)" -ForegroundColor Red
}

# ---------- Resumo ----------
Write-Host ""
Write-Host "===== RESUMO FINAL =====" -ForegroundColor Cyan
Write-Host "  C1 (concorrente)  : Media $($stats1.Media)ms | P95 $($stats1.P95)ms | Erros $($stats1.Erros)"
Write-Host "  C2 (refresh)      : Media $($stats2.Media)ms | P95 $($stats2.P95)ms | Erros $($stats2.Erros)"
Write-Host "  C3 (cache seq.)   : Media $($stats3.Media)ms | P95 $($stats3.P95)ms | Erros $($stats3.Erros)"
Write-Host ""
Write-Host "Teste concluido em $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
