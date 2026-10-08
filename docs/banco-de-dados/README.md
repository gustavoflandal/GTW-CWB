# Banco de Dados — GTW_MURALHA_DEV

> Catálogo gerado em **2026-10-08** a partir do ambiente DEV, em modo somente leitura. **A senha não está documentada**: ela vem de `confGTW.xml` local (ou variável `GTW_DB_PASSWORD` para o script de extração).

## 1. Identificação

| Item | Valor |
|---|---|
| Servidor | `10.0.0.200` (SQL Server 2016 SP2, 64-bit, Standard Edition, Windows Server 2012 R2) |
| Banco | `GTW_MURALHA_DEV` · collation `Latin1_General_CI_AS` · compatibilidade 110 (SQL Server 2012) · recuperação `SIMPLE` |
| Usuário da aplicação | `consilux` (config `armazenamento/servidor_sql`) |
| Tamanho dos dados | ≈ 736 GB (arquivo de dados) |
| Driver | `mssql-jdbc 9.2.1.jre8`; URL `jdbc:sqlserver://10.0.0.200` + catálogo; `packetSize=16384` |
| Acesso manual | `sqlcmd -S 10.0.0.200 -U consilux -d GTW_MURALHA_DEV -C` (senha em `SQLCMDPASSWORD`) |

> ⚠️ **Compatibilidade 110**: algumas construções do SQL Server 2016 que dependem de nível 130 (ex.: `STRING_SPLIT`) podem não funcionar. Teste antes de usar.

## 2. Inventário

| Schema | Tabelas | Linhas (≈) | Papel |
|---|---|---|---|
| `dbo` | 433 | 24,0 M | GTW clássico (infração, processo, remessa, cadastros, configuração de equipamentos) + tabelas de sistema (`sis_*`) + Quartz (`qrtz_*`) + logs |
| `muralha` | 124 | 19,2 M | Muralha Digital (alertas, ocorrências, atendimento, blitz, registro de fato, boletim, veículo em tempo real, configurações) |
| `mobilidade` | 183 | 4,0 M | Mobilidade urbana/Waze (congestionamentos, incidentes, radares, eventos). **Não usado pelo Java deste projeto** |
| `ia` | 6 | 8,2 M | Características do veículo por IA (`veiculo_caracteristica`, `cad_cor`, `cad_marca`, `cad_modelo`) |
| **Total** | **746** | | + 123 views · 388 procedures · 302 funções · 8 triggers · 636 chaves estrangeiras |

Objetos programáveis: `dbo` = 110 views, 282 procedures, 193 funções inline + 55 escalares + 8 tabela, 5 triggers; `muralha` = 13 views, 106 procedures, 33 inline + 5 escalares + 8 tabela, 3 triggers.

Observações de higiene: ~156 tabelas com sufixo `_test*`, `bkp`, `backup`, `_ant` (principalmente em `mobilidade` e backups em `dbo`); `veiculo_tempo_real_imagem_copiar_dev` (4,4 M) é cópia para DEV.

### Maiores tabelas
| Tabela | Linhas |
|---|---|
| `muralha.veiculo_tempo_real_imagem` | 10.913.304 |
| `muralha.veiculo_tempo_real` | 8.188.803 |
| `ia.veiculo_caracteristica` | 8.185.846 |
| `dbo.veiculo_tempo_real_imagem_copiar_dev` | 4.429.155 |
| `dbo.veiculo_sumarizado` | 4.146.565 |
| `dbo.veiculo_estatistica` | 3.130.567 |
| `dbo.veiculo_sumarizado_faixa_velocidade` | 2.409.354 |
| `dbo.veiculo_pesagem_controle` / `veiculo_pesagem` | 1.482.156 / 1.345.178 |
| `dbo.log_processos` | 1.315.847 |
| `dbo.sis_log` / `sis_log_detalhe` | 1.078.779 |

## 3. Navegação

| Arquivo / pasta | Conteúdo |
|---|---|
| [`tabelas/<schema>/<grupo>.md`](tabelas/) | 61 arquivos agrupados pelo prefixo do nome: colunas (tipo, null, identity, default), PK/índices, FKs de saída e "referenciada por", contagem de linhas. Prefixos com < 4 tabelas vão em `outros.md` |
| [`objetos-programaveis.md`](objetos-programaveis.md) | Catálogo das 821 views/procedures/funções/triggers: parâmetros, **objetos referenciados**, datas de criação/alteração |
| [`codigo-sql/`](codigo-sql/) | **Código-fonte** de cada objeto (`views/ procedures/ funcoes/ triggers/` — `<schema>.<nome>.sql`) |
| [`dominios.md`](dominios.md) | Valores de 43 tabelas de domínio pequenas (tipos, status, motivos…) |
| [`scripts/extrair-catalogo.ps1`](scripts/extrair-catalogo.ps1) | Regenera `tabelas/` e `codigo-sql/` (somente SELECT em `sys.*`) |
| [`../referencia/menus-e-permissoes.md`](../referencia/menus-e-permissoes.md) | Conteúdo de `sis_grupo`, `sis_menu*` (menus e direitos) |
| `migracoes/` *(criar quando necessário)* | Scripts DDL/DML versionados — ver `docs/10-guia-de-implementacao.md` §6 |

## 4. Mapa dos grupos de tabelas (dbo)

| Prefixo / grupo | Tabelas | O que contém |
|---|---|---|
| `configuracao_equipamento*` (44) | `configuracao_equipamento` (470) + satélites (pista, câmera, controlador, aferição, regra_infracao, servidor, painel, rodovia, rodízio…) | Cadastro técnico de cada equipamento/PCL |
| `local*`, `grupo_equipamento`, `faixa_velocidade` | `local` (470), `local_municipio_regiao`, `local_pista_croqui` | Locais e agrupamentos |
| `cad_*` (31) | `cad_veiculo`, `cad_marca/modelo/cor/especie/tipo`, `cad_isento`, `cad_municipio`, `cad_localidade`, `cad_inibicao_infracao`, `cad_evento_manual` | Cadastros de apoio |
| `veiculo*` (35) | `veiculo` (74 mil), `veiculo_imagem`, `veiculo_estatistica` (3,1 M), `veiculo_sumarizado*`, `veiculo_pesagem*`, `veiculo_importacao*`, `veiculo_video` | Passagens do GTW clássico e sumarizações |
| `infracao*` (26), `processo*`, `remessa*`, `inconsistencia*` | `infracao` (73 mil), `infracao_processo` (100 mil), `infracao_imagem`, `infracao_contestacao*`, `remessa*`, `processo_*` | Fluxo de fiscalização |
| `sis_*` (26) | `sis_usuario` (37), `sis_grupo` (27), `sis_usuario_grupo`, `sis_menu*`, `sis_menu_direitos`, `sis_log*`, `sis_usuario_token`, `sis_usuario_recupera_senha`, `sis_localizacao` | Segurança e sistema |
| `qrtz_*` (12) | Tabelas do Quartz (JDBC store **não** habilitado) | — |
| `ppv_*` (31) | Pátio/peso/veículo (praticamente vazias) | Legado |
| `log*`, `eventos*`, `integracao*`, `bkp*`, `temp*` | Logs, eventos CSX, integrações, backups | — |

## 5. Mapa dos grupos (muralha)
`alerta*` · `alerta_veiculo` · `anotacao_contributiva` · `atendimento*` (10) · `blitz*` (13) · `boletim*` · `cad_veiculo_monitorado*` (7) · `config*` (17) · `correlacionamento_automatico*` · `guarnicao*` · `ocorrencia*` · `registro_fato*` (17) · `veiculo_tempo_real*` (3) · `ponto_interesse*` · `area_monitorada` · `agente_localizacao_*` · `motivo_*` · `status_*` · `tipo_*` — detalhes e colunas em `tabelas/muralha/*.md`.

## 6. Relacionamentos centrais (resumo)
```
local ─< configuracao_equipamento ─< configuracao_equipamento_pista (faixas)
muralha.veiculo_tempo_real ─< veiculo_tempo_real_imagem
muralha.cad_veiculo_monitorado ─< _equipamento | _grupo | _periodo | _historico
muralha.alerta ─< alerta_veiculo >─ veiculo_tempo_real      (passagem que gerou o alerta)
muralha.alerta ─ status_alerta, tipo_alerta_ocorrencia, motivo_descarte, anotacao_contributiva
muralha.ocorrencia_notificacao ─ ocorrencia_notificacao_historico
muralha.atendimento ─< atendimento_guarnicao >─ guarnicao ─< guarnicao_integrante >─ sis_usuario
muralha.registro_fato ─< _individuo | _veiculo | _objeto | _endereco | _documento | _historico | _link | _passagem_veic
muralha.blitz_digital ─< blitz_abordagem ─< blitz_pessoa_envolvida | blitz_documento | blitz_abordagem_imagem
sis_usuario >─< sis_grupo (sis_usuario_grupo) ; sis_menu >─ sis_menu_direitos ─> sis_grupo / sis_usuario
infracao ─< infracao_imagem ; infracao ─< infracao_processo ─< infracao_processo_concluido ; remessa ─< infracao_remessa
```
Use `tabelas/<schema>/<grupo>.md` para as FKs reais (636 chaves estrangeiras no banco; muitas relações de `muralha.*` são lógicas, sem FK declarada — validar no código).

## 7. Procedures e funções mais importantes (Muralha)
| Objeto | Função |
|---|---|
| `muralha.spu_ObterNovosAlertas` | Polling de novos alertas (thread `Alertas`, 2 s) |
| `muralha.spu_ObterDadosAlertaOcorrencia`, `fcn_ObterDadosAlertaOcorrencia` | Dados completos de um alerta/ocorrência |
| `muralha.spu_gerar_ocorrencia`, `spu_encerrar_atendimento`, `spu_obtem_ocorrencias` | Fluxo de ocorrência/atendimento |
| `muralha.spu_ObterVeiculosTempoReal(Historico)`, `spu_ObterVeiculosPorFiltros`, `spu_ObterVeiculosGtwPorFiltros`, `spu_ObterVeiculosBlitzEletronica/WebSocket` | Consulta/stream de passagens |
| `muralha.fcn_ObterAlertas(Alt)`, `fcn_ObterOcorrencias(Alt)` | Listagens de alerta/ocorrência |
| `muralha.fcn_perfil_comportamental_*` | Perfil comportamental de veículo |
| `muralha.spu_correlacionamento_placas`, `fcn_ObterInfo*` | Correlacionamento e dados cruzados |
| `muralha.spu_Relatorio*`, `spu_Get*` (≈ 40) | Relatórios/gráficos/dashboards |
| `dbo.fcn_VerificaAcesso(acao, id_usuario)` | Autorização por URL |
| `dbo.spu_ppv_sis_usuario_token_*` | Token de acesso de usuário |
| `muralha.spu_ImportarBase` | Importação CSV de base (JuncaoBase) |

## 8. Regras para agentes e desenvolvedores
1. **Somente leitura por padrão.** Não executar INSERT/UPDATE/DELETE/DDL/EXEC de procedures com efeito colateral sem autorização explícita, nem em DEV.
2. Consultar tabelas gigantes **sempre** com filtro de data/equipamento e `TOP`.
3. Reutilizar procedures/funções existentes; ver "Referencia" em `objetos-programaveis.md` antes de alterar uma (impacto em cascata).
4. Mudanças estruturais = script em `migracoes/` + aprovação + atualização deste catálogo.
5. Nunca copiar dados reais (placas, CPF, imagens, coordenadas de agentes) para docs, testes ou commits.
6. Para regenerar o catálogo: `$env:GTW_DB_PASSWORD='…'; .\scripts\extrair-catalogo.ps1` (não commitar a senha).
