<#
.SYNOPSIS
Regenera o catálogo do banco (docs/banco-de-dados) a partir do SQL Server.

.DESCRIPTION
Somente LEITURA (SELECT em sys.*). Gera:
  tabelas/<schema>/<grupo>.md, objetos-programaveis.md, dominios.md, codigo-sql/{views,procedures,funcoes,triggers}/*.sql
A senha NUNCA fica no script: informe por variável de ambiente GTW_DB_PASSWORD.

.EXAMPLE
$env:GTW_DB_PASSWORD = '<senha>'   # não gravar em arquivo/histórico compartilhado
.\extrair-catalogo.ps1 -Servidor 10.0.0.200 -Banco GTW_MURALHA_DEV -Usuario consilux
#>
param(
  [string]$Servidor = '10.0.0.200',
  [string]$Banco    = 'GTW_MURALHA_DEV',
  [string]$Usuario  = 'consilux',
  [string]$Saida    = (Split-Path -Parent $PSScriptRoot)
)
$ErrorActionPreference = 'Stop'
if (-not $env:GTW_DB_PASSWORD) { throw 'Defina $env:GTW_DB_PASSWORD' }
$utf = New-Object Text.UTF8Encoding $false
$cn = New-Object System.Data.SqlClient.SqlConnection "Server=$Servidor;Database=$Banco;User Id=$Usuario;Password=$($env:GTW_DB_PASSWORD);TrustServerCertificate=True;Encrypt=False"
$cn.Open()
function Run([string]$sql) { $c=$cn.CreateCommand(); $c.CommandTimeout=300; $c.CommandText=$sql; $dt=New-Object Data.DataTable; $dt.Load($c.ExecuteReader()); ,$dt }
function Esc([object]$v) { ([string]$v) -replace '\|','/' -replace "[\r\n]+",' ' }

# ---------- 1. Tabelas ----------
$cols = Run @"
SELECT s.name sch,t.name tb,c.column_id id,c.name col,
 ty.name+CASE WHEN ty.name IN ('varchar','nvarchar','char','nchar','varbinary') THEN '('+CASE WHEN c.max_length=-1 THEN 'max' WHEN ty.name LIKE 'n%' THEN CAST(c.max_length/2 AS varchar) ELSE CAST(c.max_length AS varchar) END+')'
 WHEN ty.name IN ('decimal','numeric') THEN '('+CAST(c.precision AS varchar)+','+CAST(c.scale AS varchar)+')' ELSE '' END tipo,
 c.is_nullable nul,c.is_identity idt,ISNULL(dc.definition,'') def,ISNULL(CAST(ep.value AS nvarchar(500)),'') descr
FROM sys.tables t JOIN sys.schemas s ON s.schema_id=t.schema_id JOIN sys.columns c ON c.object_id=t.object_id
JOIN sys.types ty ON ty.user_type_id=c.user_type_id LEFT JOIN sys.default_constraints dc ON dc.object_id=c.default_object_id
LEFT JOIN sys.extended_properties ep ON ep.major_id=t.object_id AND ep.minor_id=c.column_id AND ep.name='MS_Description'
ORDER BY 1,2,3
"@
$idx = Run @"
SELECT s.name sch,t.name tb,i.name ix,i.type_desc tp,ic.key_ordinal ord,c.name col,i.is_primary_key pk,i.is_unique uq
FROM sys.tables t JOIN sys.schemas s ON s.schema_id=t.schema_id JOIN sys.indexes i ON i.object_id=t.object_id
JOIN sys.index_columns ic ON ic.object_id=i.object_id AND ic.index_id=i.index_id JOIN sys.columns c ON c.object_id=t.object_id AND c.column_id=ic.column_id
WHERE i.name IS NOT NULL AND ic.is_included_column=0 ORDER BY 1,2,3,5
"@
$fks = Run @"
SELECT ps.name psch,pt.name ptb,fk.name fk,pc.name pcol,rs.name rsch,rt.name rtb,rc.name rcol
FROM sys.foreign_key_columns fkc JOIN sys.foreign_keys fk ON fk.object_id=fkc.constraint_object_id
JOIN sys.tables pt ON pt.object_id=fkc.parent_object_id JOIN sys.schemas ps ON ps.schema_id=pt.schema_id
JOIN sys.columns pc ON pc.object_id=pt.object_id AND pc.column_id=fkc.parent_column_id
JOIN sys.tables rt ON rt.object_id=fkc.referenced_object_id JOIN sys.schemas rs ON rs.schema_id=rt.schema_id
JOIN sys.columns rc ON rc.object_id=rt.object_id AND rc.column_id=fkc.referenced_column_id ORDER BY 1,2,3
"@
$rowsDt = Run "SELECT s.name sch,t.name tb,SUM(p.rows) n FROM sys.tables t JOIN sys.schemas s ON s.schema_id=t.schema_id JOIN sys.partitions p ON p.object_id=t.object_id AND p.index_id IN (0,1) GROUP BY s.name,t.name"
$rows=@{}; foreach($r in $rowsDt.Rows){ $rows["$($r.sch).$($r.tb)"]=$r.n }
$cm=@{}; foreach($r in $cols.Rows){ $k="$($r.sch).$($r.tb)"; if(-not $cm[$k]){$cm[$k]=@()}; $cm[$k]+=,$r }
$im=@{}; foreach($r in $idx.Rows){ $k="$($r.sch).$($r.tb)"; if(-not $im[$k]){$im[$k]=[ordered]@{}}; if(-not $im[$k][$r.ix]){$im[$k][$r.ix]=@{tp=$r.tp;pk=$r.pk;uq=$r.uq;cols=@()}}; $im[$k][$r.ix].cols+=$r.col }
$fo=@{};$fi=@{}; foreach($r in $fks.Rows){ $a="$($r.psch).$($r.ptb)"; $b="$($r.rsch).$($r.rtb)"; if(-not $fo[$a]){$fo[$a]=@()}; $fo[$a]+="$($r.pcol) → $b.$($r.rcol)"; if(-not $fi[$b]){$fi[$b]=@()}; $fi[$b]+="$a.$($r.pcol)" }
$grp=@{}; foreach($k in ($cm.Keys|Sort)){ $sch,$t=$k -split '\.',2; $g="$sch/$(($t -split '_')[0])"; if(-not $grp[$g]){$grp[$g]=@()}; $grp[$g]+=$k }
$fin=@{}; foreach($g in $grp.Keys){ $sch=$g.Split('/')[0]; $tg= if($grp[$g].Count -lt 4){"$sch/outros"}else{$g}; if(-not $fin[$tg]){$fin[$tg]=@()}; $fin[$tg]+=$grp[$g] }
$tabDir = Join-Path $Saida 'tabelas'; if (Test-Path $tabDir) { Remove-Item $tabDir -Recurse -Force }
foreach($g in ($fin.Keys|Sort)){
  $sch,$pre=$g.Split('/'); $sb=New-Object Text.StringBuilder
  [void]$sb.AppendLine("# Tabelas — schema ``$sch`` — grupo ``$pre```n`nGerado de $Banco em $(Get-Date -Format yyyy-MM-dd). Linhas aproximadas.`n")
  foreach($k in ($fin[$g]|Sort)){
    [void]$sb.AppendLine("## $k`n`nLinhas: ~$($rows[$k])`n`n| # | Coluna | Tipo | Null | Ident. | Default | Descrição |`n|---|---|---|---|---|---|---|")
    foreach($c in $cm[$k]){ [void]$sb.AppendLine("| $($c.id) | $($c.col) | $($c.tipo) | $(if($c.nul){'S'}else{'N'}) | $(if($c.idt){'S'}) | $(Esc $c.def) | $(Esc $c.descr) |") }
    if($im[$k]){ [void]$sb.AppendLine("`n**Índices/Chaves:**"); foreach($ik in $im[$k].Keys){ $i=$im[$k][$ik]; $f= if($i.pk){'PK'}elseif($i.uq){'UNIQUE'}else{'IDX'}; [void]$sb.AppendLine("- $f ``$ik`` ($($i.tp)): $($i.cols -join ', ')") } }
    if($fo[$k]){ [void]$sb.AppendLine("`n**FKs (saída):**"); $fo[$k]|%{ [void]$sb.AppendLine("- $_") } }
    if($fi[$k]){ [void]$sb.AppendLine("`n**Referenciada por:**"); ($fi[$k]|Select -Unique)|%{ [void]$sb.AppendLine("- $_") } }
    [void]$sb.AppendLine()
  }
  $d=Join-Path $tabDir $sch; New-Item -ItemType Directory $d -Force | Out-Null
  [IO.File]::WriteAllText("$d\$pre.md",$sb.ToString(),$utf)
}

# ---------- 2. Código SQL (views, procedures, funções, triggers) ----------
$codDir = Join-Path $Saida 'codigo-sql'; if (Test-Path $codDir) { Remove-Item $codDir -Recurse -Force }
$map=@{'V'='views';'P'='procedures';'FN'='funcoes';'IF'='funcoes';'TF'='funcoes';'TR'='triggers'}
$mods = Run "SELECT s.name sch,RTRIM(o.type) typ,o.name nm,m.definition def FROM sys.sql_modules m JOIN sys.objects o ON o.object_id=m.object_id JOIN sys.schemas s ON s.schema_id=o.schema_id WHERE o.is_ms_shipped=0 AND m.definition IS NOT NULL"
foreach($r in $mods.Rows){ if(-not $map.ContainsKey($r.typ)){continue}; $d=Join-Path $codDir $map[$r.typ]; New-Item -ItemType Directory $d -Force | Out-Null
  $fn=("{0}.{1}.sql" -f $r.sch,$r.nm) -replace '[\\/:*?"<>|]','_'; [IO.File]::WriteAllText((Join-Path $d $fn),[string]$r.def,$utf) }

# (objetos-programaveis.md e dominios.md: ver agentes/prompts/regenerar-referencia.md — usam sys.parameters,
#  sys.sql_expression_dependencies e SELECT TOP 40 em tabelas pequenas de domínio.)
$cn.Close()
Write-Host "Catálogo regenerado em $Saida"
