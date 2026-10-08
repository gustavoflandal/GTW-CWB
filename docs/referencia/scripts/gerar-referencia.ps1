<#
.SYNOPSIS
Regenera os inventários de docs/referencia (servlets, módulos back-end, telas, pacotes Java) por varredura do código.

.DESCRIPTION
Somente LEITURA do código; escreve apenas em -Saida. Os arquivos gerados são heurísticos (regex) - confira no código-fonte.
(menus-e-permissoes.md depende do banco: ver agentes/prompts/regenerar-referencia.md)

.EXAMPLE
.\gerar-referencia.ps1                       # sobrescreve docs/referencia
.\gerar-referencia.ps1 -Saida $env:TEMP\ref   # teste em outra pasta
#>
param(
  [string]$Projeto = (Resolve-Path (Join-Path $PSScriptRoot '..\..\..')).Path,
  [string]$Saida   = (Split-Path -Parent $PSScriptRoot)
)
$ErrorActionPreference = 'Stop'
$utf  = New-Object Text.UTF8Encoding $false
$java = Join-Path $Projeto 'src\main\java'
$web  = Join-Path $Projeto 'src\main\webapp'
New-Item -ItemType Directory $Saida -Force | Out-Null
$hoje = Get-Date -Format yyyy-MM-dd

# ---------- 1. Servlets e endpoints ----------
$ann = Get-ChildItem $java -Recurse -Filter *.java | Select-String -Pattern '@WebServlet\s*\(([^)]*)\)' | % {
  $f = $_.Path.Substring($java.Length + 1); $v = $_.Matches[0].Groups[1].Value
  [pscustomobject]@{ url = ($v -replace '(urlPatterns|value)\s*=\s*','' -replace '[{}"]','' -replace '\s+',' ').Trim(); cls = ($f -replace '\.java$','' -replace '\\','.') } }
$x = [xml](Get-Content (Join-Path $web 'WEB-INF\web.xml') -Raw -Encoding UTF8)
$maps = @{}
foreach ($m in $x.'web-app'.'servlet-mapping') { $n = $m.'servlet-name'.Trim(); if (-not $maps[$n]) { $maps[$n] = @() }; $maps[$n] += @($m.'url-pattern' | % { $_.Trim() }) }
$wx = foreach ($s in $x.'web-app'.servlet) { $n = $s.'servlet-name'.Trim(); [pscustomobject]@{ nome = $n; cls = ($s.'servlet-class' -replace '\s',''); urls = ($maps[$n] -join ', ') } }
$sb = New-Object Text.StringBuilder
[void]$sb.AppendLine("# Referência — Servlets e endpoints HTTP`n`nGerado em $hoje por varredura de ``web.xml`` e ``@WebServlet``. $(@($wx).Count) servlets em web.xml + $(@($ann).Count) anotados.`n`n## 1. web.xml`n`n| Nome | URL(s) | Classe |`n|---|---|---|")
foreach ($w in ($wx | Sort cls)) { [void]$sb.AppendLine("| $($w.nome) | $($w.urls) | ``$($w.cls)`` |") }
[void]$sb.AppendLine("`n## 2. @WebServlet`n`n| URL | Classe |`n|---|---|")
foreach ($a in ($ann | Sort url)) { [void]$sb.AppendLine("| ``$($a.url)`` | ``$($a.cls)`` |") }
[IO.File]::WriteAllText((Join-Path $Saida 'servlets-e-endpoints.md'), $sb.ToString(), $utf)

# ---------- 2. Módulos back-end Muralha ----------
$base = Join-Path $java 'muralha\digital'
$sb = New-Object Text.StringBuilder
[void]$sb.AppendLine("# Referência — Módulos back-end do Muralha Digital (``muralha.digital.*``)`n`nGerado em $hoje. Lista de tabelas/procedures é heurística (regex sobre SQL em strings Java).`n")
foreach ($d in (Get-ChildItem $base -Directory | Sort Name)) {
  $files = @(Get-ChildItem $d.FullName -Recurse -Filter *.java)
  $txt = ($files | % { Get-Content $_.FullName -Raw -Encoding UTF8 }) -join "`n"
  $urls = @([regex]::Matches($txt,'@WebServlet\s*\(\s*(?:urlPatterns\s*=\s*)?\{?\s*"([^"]+)"') | % { $_.Groups[1].Value } | Select -Unique)
  $acoes = @([regex]::Matches($txt,'"(\w{3,40})"\s*\.equals(?:IgnoreCase)?\(\s*(?:acao|strAcao|action|op|operacao)\b|(?:acao|strAcao|action|op|operacao)\s*\.equals(?:IgnoreCase)?\(\s*"(\w{3,40})"|case\s+"(\w{3,40})"\s*:') | % { @($_.Groups[1].Value,$_.Groups[2].Value,$_.Groups[3].Value | ? { $_ })[0] } | Select -Unique)
  $tabs = @([regex]::Matches($txt,'(?i)\b(?:from|join|into|update)\s+(?:\[?(?:dbo|muralha|mobilidade|ia)\]?\.)?\[?([a-z_][a-z0-9_]{3,})\]?') | % { $_.Groups[1].Value.ToLower() } | ? { $_ -notmatch '^(select|where|set|the|values|dual|with|inserted|deleted)$' -and $_ -match '_' } | Select -Unique | Sort)
  $procs = @([regex]::Matches($txt,'(?i)\b((?:spu|sps|spi|spd|fcn|fnc|sp|usp|vw)_[a-z0-9_]+)') | % { $_.Groups[1].Value.ToLower() } | Select -Unique | Sort)
  [void]$sb.AppendLine("## $($d.Name)`n`n- Arquivos Java: $($files.Count) (``muralha.digital.$($d.Name)``)`n- Classes: $(($files | % { $_.BaseName } | Sort) -join ', ')")
  if ($urls.Count)  { [void]$sb.AppendLine("- Servlet URL(s): $(($urls | % { '`' + $_ + '`' }) -join ', ')") }
  if ($acoes.Count) { [void]$sb.AppendLine("- Ações (``acao``): $($acoes -join ', ')") }
  if ($tabs.Count)  { [void]$sb.AppendLine("- Tabelas/objetos em SQL: $($tabs -join ', ')") }
  if ($procs.Count) { [void]$sb.AppendLine("- Procedures/Funções/Views: $($procs -join ', ')") }
  [void]$sb.AppendLine()
}
[IO.File]::WriteAllText((Join-Path $Saida 'modulos-backend-muralha.md'), $sb.ToString(), $utf)

# ---------- 3. Telas Muralha ----------
$sb = New-Object Text.StringBuilder
[void]$sb.AppendLine("# Referência — Telas do Muralha Digital (``/muralha-digital/pages/*``)`n`nGerado em $hoje. Endpoints = strings de URL encontradas em JSP/JS.`n")
$pages = Join-Path $web 'muralha-digital\pages'
foreach ($d in (Get-ChildItem $pages -Directory | Sort Name)) {
  $all = @(Get-ChildItem $d.FullName -Recurse -File)
  $jsp = @($all | ? { $_.Extension -eq '.jsp' })
  $js  = @($all | ? { $_.Extension -eq '.js' -and $_.Name -notmatch 'min|jquery|bootstrap|chart\.|leaflet|xlsx|jspdf' })
  $css = @($all | ? { $_.Extension -eq '.css' -and $_.Name -notmatch 'min|bootstrap' })
  $txt = (($jsp + $js) | % { Get-Content $_.FullName -Raw -Encoding UTF8 -ErrorAction SilentlyContinue }) -join "`n"
  $ep = @([regex]::Matches($txt,'["''`](/(?:MuralhaDigital|rest|ajax|servlet|relatorio|ferramenta|processo|GtwWidgets|muralha)[A-Za-z0-9_/\.\-]*)') | % { $_.Groups[1].Value } | ? { $_ -notmatch '\.(jsp|js|css|png|jpg|gif)$' } | Select -Unique | Sort)
  $titles = @($jsp | % { $t = [regex]::Match((Get-Content $_.FullName -Raw -Encoding UTF8),'<title>([^<]+)</title>'); if ($t.Success) { "$($_.BaseName): $($t.Groups[1].Value.Trim())" } } | Select -First 6)
  $rel = $d.FullName.Substring($web.Length + 1).Replace('\','/')
  [void]$sb.AppendLine("## $($d.Name)`n`n- Caminho: ``$rel`` · $($all.Count) arquivos")
  if ($jsp.Count)    { [void]$sb.AppendLine("- JSPs: $(($jsp | % { $_.FullName.Substring($d.FullName.Length + 1).Replace('\','/') } | Sort) -join ', ')") }
  if ($titles.Count) { [void]$sb.AppendLine("- Títulos: $($titles -join ' · ')") }
  if ($js.Count)     { [void]$sb.AppendLine("- JS próprios: $(($js | % { $_.FullName.Substring($d.FullName.Length + 1).Replace('\','/') } | Sort | Select -First 12) -join ', ')") }
  if ($css.Count)    { [void]$sb.AppendLine("- CSS próprios: $(($css | % { $_.FullName.Substring($d.FullName.Length + 1).Replace('\','/') } | Sort | Select -First 8) -join ', ')") }
  if ($ep.Count)     { [void]$sb.AppendLine("- Endpoints chamados: $(($ep | % { '`' + $_ + '`' }) -join ', ')") }
  [void]$sb.AppendLine()
}
[IO.File]::WriteAllText((Join-Path $Saida 'telas-muralha-digital.md'), $sb.ToString(), $utf)

# ---------- 4. Pacotes Java ----------
$sb = New-Object Text.StringBuilder
[void]$sb.AppendLine("# Referência — Inventário de pacotes Java`n`nGerado em $hoje. Total: $((Get-ChildItem $java -Recurse -Filter *.java).Count) arquivos ``.java``.`n")
foreach ($d in (Get-ChildItem $java -Recurse -Directory | ? { (Get-ChildItem $_.FullName -File -Filter *.java).Count -gt 0 } | Sort FullName)) {
  $pk = $d.FullName.Substring($java.Length + 1).Replace('\','.'); $fs = @(Get-ChildItem $d.FullName -File -Filter *.java | Sort Name)
  [void]$sb.AppendLine("### $pk ($($fs.Count))`n$(($fs | % { $_.BaseName }) -join ', ')`n")
}
[IO.File]::WriteAllText((Join-Path $Saida 'inventario-pacotes-java.md'), $sb.ToString(), $utf)
Write-Host "Referências geradas em $Saida"
