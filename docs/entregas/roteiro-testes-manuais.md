# Roteiro de Testes Manuais — PoC TRANSALVADOR

Documento consolidado com todos os testes manuais dos Planos 01–19, organizados em trilha sequencial para o programador.

> **Pré-requisitos globais:**
> 1. ~~Migrações executadas~~ ✅ Executadas em 2026-10-09 (ver [`pendencias-banco-de-dados.md`](pendencias-banco-de-dados.md))
> 2. Servidor rodando: `..\.setup-gtw\run.ps1` → `http://localhost:8080/`
> 3. Usuário autenticado com permissão de administrador
> 4. Browser com DevTools aberto (aba Console + Network)

---

## Fase 1 — Infraestrutura e Segurança (Planos 01–04)

### 1.1 — Log de Auditoria (Plano 01)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 01-T01 | Tabela existe | `SELECT TOP 1 * FROM dbo.sis_log_auditoria` | Sem erro; 0 rows | [x] ✅ |
| 01-T02 | Tela acessível | Acessar `/muralha-digital/pages/auditoria/consulta-log.jsp` | Renderiza sem erro 500 | [ ] |
| 01-T03 | Endpoint JSON | `GET /MuralhaDigital/Auditoria?acao=consultar` | `{"ok":true,"registros":[]}` | [ ] |
| 01-T04 | Registro de login | Fazer login → verificar `SELECT TOP 5 * FROM dbo.sis_log_auditoria ORDER BY dt_operacao DESC` | Linha com funcionalidade='Acesso', operacao='login' | [ ] |

### 1.2 — SHA-256 Integridade (Plano 02)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 02-T01 | Job sem erro | Subir servidor → verificar console sem ERROR para `JobSha256Imagem` | Sem stack trace | [ ] |
| 02-T02 | Backfill 5min | Aguardar 5 min → `SELECT TOP 5 id_imagem, sha256 FROM muralha.vtr_imagem_complemento` | sha256 de 64 chars hex | [ ] |
| 02-T03 | Pendentes | `SELECT COUNT(*) FROM muralha.veiculo_tempo_real_imagem WHERE NOT EXISTS (SELECT 1 FROM muralha.vtr_imagem_complemento c WHERE c.id_imagem = vtri.id AND c.sha256 IS NOT NULL)` | Número decresce ao longo do tempo | [ ] |

### 1.3 — Política de Senhas (Plano 03)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 03-T01 | Config carregada | `SELECT * FROM dbo.sis_senha_config ORDER BY chave` | 9 linhas de parâmetros | [x] ✅ |
| 03-T02 | Colunas criadas | `SELECT TOP 1 senha_hash, tentativas_invalidas, bloqueado_ate FROM dbo.sis_usuario` | Sem erro | [x] ✅ |
| 03-T03 | Bloqueio 5 tentativas | Login com senha errada 5x → 6a tentativa | Mensagem "Usuário bloqueado até..." | [ ] |
| 03-T04 | Senha fraca rejeitada | Troca de senha para `"abc"` | Mensagem de complexidade insuficiente | [ ] |
| 03-T05 | Expiração | `UPDATE sis_usuario SET dt_ultima_troca_senha = DATEADD(DAY,-91,SYSDATETIME()) WHERE login='<user>'` → login | Redireciona para troca de senha | [ ] |
| 03-T06 | Histórico | Trocar para `"Senha@2024"`, trocar novamente para `"Senha@2024"` | "Esta senha já foi usada recentemente" | [ ] |
| 03-T07 | Desbloqueio | `GET /MuralhaDigital/Usuarios?acao=desbloquear&id=<id>` → verificar banco | tentativas=0, bloqueado_ate=NULL | [ ] |

### 1.4 — MFA/TOTP (Plano 04)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 04-T01 | Colunas criadas | `SELECT TOP 1 totp_secret, totp_habilitado FROM dbo.sis_usuario` | Sem erro; NULL/0 | [x] ✅ |
| 04-T02 | Tela MFA | Acessar `/muralha-digital/pages/meu-perfil/configurar-mfa.jsp` | QR Code exibido | [ ] |
| 04-T03 | Ativar MFA | Escanear QR → digitar código 6 dígitos → Ativar | totp_habilitado=1 no banco | [ ] |
| 04-T04 | Login com MFA | Logout → login → após senha correta | Redireciona para `mfa_codigo.jsp` | [ ] |
| 04-T05 | Código inválido | Digitar `000000` na tela MFA | "Código inválido. Tente novamente." | [ ] |
| 04-T06 | Código válido | Digitar código correto do app | Redireciona para `abertura-sistemas.jsp` | [ ] |
| 04-T07 | Timeout MFA | Aguardar 5 min sem digitar código → tentar validar | Redireciona para `login.jsp` | [ ] |

---

## Fase 2 — Processamento de Infrações (Planos 05–08)

### 2.1 — Dupla Análise (Plano 05)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 05-T01 | Tabelas criadas | `SELECT TOP 1 * FROM muralha.infracao_analise; SELECT TOP 1 * FROM muralha.vtr_status_analise` | Sem erro | [x] ✅ |
| 05-T02 | Fila retorna próxima | `GET /MuralhaDigital/InfracaoAnalise?acao=proximaFila` | JSON com infração ou `filaVazia:true` | [ ] |
| 05-T03 | 1o operador classifica | Usuário A: Próxima Infração → "Válida" → Confirmar | `novoStatus:"PRIMEIRA_ANALISE"` | [ ] |
| 05-T04 | Mesmo operador bloqueado | Usuário A tenta ver a mesma infração | Infração não aparece na fila | [ ] |
| 05-T05 | 2o operador concorda | Usuário B classifica como "Válida" | `novoStatus:"PRE_APROVADA"` | [ ] |
| 05-T06 | Divergência → desempate | A="Válida", B="Inválida" → C classifica | `novoStatus:"DESEMPATE"` → resultado final | [ ] |
| 05-T07 | Indicadores | `GET /MuralhaDigital/InfracaoAnalise?acao=indicadores` | Array com contagens por status | [ ] |

### 2.2 — Obliteração de Imagens (Plano 06)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 06-T01 | Tabelas criadas | `SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('vtr_imagem_obliterada','infracao_imagem_obliteracao')` | 2 tabelas | [x] ✅ |
| 06-T02 | Botões visíveis | Abrir infração na fila de análise | Botões "Iniciar Obliteração", "Aplicar", "Cancelar" | [ ] |
| 06-T03 | Canvas de seleção | Clicar "Iniciar Obliteração" → arrastar sobre imagem | Retângulo preto semitransparente | [ ] |
| 06-T04 | Persistência | Aplicar → confirmar SweetAlert → verificar banco | Registro em `vtr_imagem_obliterada` e `infracao_imagem_obliteracao` | [ ] |
| 06-T05 | Original intacto | `SELECT DATALENGTH(imagem) FROM muralha.veiculo_tempo_real_imagem WHERE id='<id>'` | Tamanho inalterado | [ ] |
| 06-T06 | Cancelar | Iniciar → desenhar → Cancelar | Canvas removido, sem chamada ao servidor | [ ] |

### 2.3 — SLA de Latência (Plano 07)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 07-T01 | Tabela e config | `SELECT * FROM muralha.alerta_sla; SELECT valor FROM muralha.config_chave_valor WHERE chave='sla_latencia_threshold_ms'` | Tabela existe; valor=4000 | [x] ✅ |
| 07-T02 | Painel carrega | Acessar `/muralha-digital/pages/monitoramento/sla-latencia/index.jsp` | 4 cards + gráfico sem erro 500 | [ ] |
| 07-T03 | Endpoint atual | `GET /MuralhaDigital/SlaLatencia?acao=atual` | JSON com total, media, maximo | [ ] |
| 07-T04 | Job Quartz | Aguardar 5 min → `SELECT TOP 5 * FROM muralha.alerta_sla ORDER BY id DESC` | Registro com dt_alerta recente | [ ] |
| 07-T05 | Violação visual | Setar threshold para 1ms → aguardar job → recarregar painel | Card "Status SLA" vermelho "Violado" | [ ] |

### 2.4 — Painel de Assertividade (Plano 08)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 08-T01 | Painel carrega | Acessar `/muralha-digital/pages/monitoramento/assertividade/index.jsp` | Taxa de assertividade + gráfico Chart.js | [ ] |
| 08-T02 | Alerta <80% | Verificar indicador visual quando assertividade < 80% | Alerta vermelho | [ ] |
| 08-T03 | Filtros | Filtrar por período e equipamento | Dados atualizados conforme filtro | [ ] |

---

## Fase 3 — Identidade e Compliance (Planos 09–13)

### 3.1 — CPF/Matrícula (Plano 09)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 09-T01 | Tela carrega | Acessar `/muralha-digital/pages/admin/cpf-matricula/index.jsp` | Tabela de usuários ativos | [ ] |
| 09-T02 | CPF válido | Editar → `529.982.247-25` → Salvar | Sucesso, CPF exibido formatado | [ ] |
| 09-T03 | CPF inválido | Editar → `000.000.000-00` → Salvar | "CPF inválido" | [ ] |
| 09-T04 | CPF duplicado | Salvar mesmo CPF para 2 usuários | "CPF já cadastrado para outro usuário" | [ ] |
| 09-T05 | Matrícula | Editar → `MAT-2026-001` → Salvar | Matrícula salva e exibida | [ ] |
| 09-T06 | Matrícula duplicada | Salvar mesma matrícula para 2 usuários | "Matrícula já cadastrada" | [ ] |
| 09-T07 | Limpar campos | Apagar CPF e matrícula → Salvar | Campos vazios; NULL no banco | [ ] |

### 3.2 — SLA Pré-processamento 72h (Plano 10)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 10-T01 | Painel carrega | Acessar `/muralha-digital/pages/monitoramento/sla-preproc/index.jsp` | 4 cards + doughnut + tabela | [ ] |
| 10-T02 | Endpoint aging | `GET /MuralhaDigital/SlaPreprocessamento` | JSON com totalPendentes, faixas de aging | [ ] |
| 10-T03 | Endpoint histórico | `GET /MuralhaDigital/SlaPreprocessamento?acao=historico` | JSON com snapshots | [ ] |
| 10-T04 | Job Quartz | Log: "Job SLA Pré-processamento agendada a cada 60 minutos" → verificar `alerta_sla_preproc` | Registro inserido | [ ] |
| 10-T05 | Cores da tabela | Locais > 72h vermelho, 48-72h amarelo, < 48h OK | Badges corretos | [ ] |
| 10-T06 | Auto-refresh | Aguardar 65s | Dados atualizam automaticamente | [ ] |

### 3.3 — Exportação Padronizada (Plano 11)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 11-T01 | Tela carrega | Acessar `/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp` | Filtros + botões CSV/XLS/PDF | [ ] |
| 11-T02 | Consulta | Informar período com dados → Consultar | Tabela preenchida | [ ] |
| 11-T03 | CSV | Consultar → clicar CSV | `.csv` com BOM UTF-8, separador `;`, hash SHA-256 no rodapé | [ ] |
| 11-T04 | XLS | Consultar → clicar XLS | `.xlsx` abre no Excel com dados corretos | [ ] |
| 11-T05 | PDF | Consultar → clicar PDF | `.pdf` com tabela formatada | [ ] |
| 11-T06 | Período obrigatório | Consultar sem datas | SweetAlert pedindo período | [ ] |

### 3.4 — Retenção de Dados (Plano 12)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 12-T01 | Tabela e config | `SELECT COUNT(*) FROM muralha.expurgo_log; SELECT valor FROM muralha.config_chave_valor WHERE chave='retencao_anos'` | Tabela existe; valor=5 | [x] ✅ |
| 12-T02 | Tela carrega | Acessar `/muralha-digital/pages/admin/retencao/index.jsp` | Cards + tabela histórico | [ ] |
| 12-T03 | GET JSON | DevTools: `GET /MuralhaDigital/Retencao` | JSON com retencaoAnos, estimativa, logs | [ ] |
| 12-T04 | Alterar período | Mudar para 3 anos → Salvar | Card atualiza; estimativa recalculada | [ ] |
| 12-T05 | Período inválido | Informar 0 ou 25 → Salvar | "Período deve ser entre 1 e 20 anos" | [ ] |
| 12-T06 | Sem sessão | Aba anônima → acessar endpoint | Acesso negado | [ ] |

### 3.5 — Anonimização de Placas (Plano 13)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 13-T01 | View e config | `SELECT COUNT(*) FROM muralha.vw_passagem_anonimizada; SELECT valor FROM muralha.config_chave_valor WHERE chave='anonimizar_placa_padrao'` | View com ~8,2M rows; valor=1 | [x] ✅ |
| 13-T02 | Tela carrega | Acessar `/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp` | Filtros + badge "Anonimizado" | [ ] |
| 13-T03 | Com anonimização | Período com dados + "Ativada" → Consultar | Placas mascaradas (`ABC****`) | [ ] |
| 13-T04 | Sem anonimização | "Desativada" → Consultar | Placas completas; badge amarelo | [ ] |
| 13-T05 | Config padrão | "Padrão (config)" → Consultar | Conforme `anonimizar_placa_padrao` | [ ] |
| 13-T06 | Sem sessão | Aba anônima → acessar endpoint | Acesso negado | [ ] |

---

## Fase 4 — Gestão e Analytics (Planos 14–16)

### 4.1 — Gestão de Lotes (Plano 14)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 14-T01 | Tabelas criadas | `SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('lote_infracao','lote_infracao_item')` | 2 tabelas | [x] ✅ |
| 14-T02 | Tela carrega | Acessar `/muralha-digital/pages/processamento/lotes/index.jsp` | Tabela de lotes + botão "Novo Lote" | [ ] |
| 14-T03 | Criar lote | "Novo Lote" → descrição → Confirmar | Código gerado; status RASCUNHO | [ ] |
| 14-T04 | Ver itens | Clicar olho de um lote | Modal com tabela de itens | [ ] |
| 14-T05 | Enviar lote | Olho → "Enviar para DETRAN" → Confirmar | Status ENVIADO; data preenchida | [ ] |
| 14-T06 | Cancelar lote | Olho de lote RASCUNHO → "Cancelar Lote" | Status CANCELADO | [ ] |
| 14-T07 | Lote enviado imutável | Olho de lote ENVIADO | Botões Enviar/Cancelar ausentes | [ ] |
| 14-T08 | Sem sessão | Aba anônima → `GET /MuralhaDigital/Lote?acao=listar` | Acesso negado | [ ] |

### 4.2 — KPIs Configuráveis (Plano 15)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 15-T01 | KPIs padrão | `SELECT id, nome, unidade, ativo FROM muralha.kpi_config ORDER BY ordem` | 4 KPIs ativos | [x] ✅ |
| 15-T02 | Dashboard carrega | Acessar `/muralha-digital/pages/dashboard/kpis/index.jsp` | 4 cards coloridos com badges | [ ] |
| 15-T03 | Auto-refresh | Aguardar 60s | Horário de atualização muda | [ ] |
| 15-T04 | Config lista KPIs | Acessar `/muralha-digital/pages/admin/kpis/index.jsp` | Tabela com 4 KPIs padrão | [ ] |
| 15-T05 | Editar KPI | Editar → alterar threshold → Salvar | Tabela atualiza; dashboard reflete cor | [ ] |
| 15-T06 | Criar KPI | "Novo KPI" → `SELECT CAST(1 AS NUMERIC) AS valor` → Salvar | KPI aparece no dashboard | [ ] |
| 15-T07 | Query não-SELECT | Alterar SQL para `DELETE FROM...` → Salvar | "query_sql deve iniciar com SELECT" | [ ] |
| 15-T08 | Desativar KPI | Editar → desmarcar "Ativo" → Salvar | Não aparece no dashboard | [ ] |
| 15-T09 | Sem sessão | Aba anônima → `GET /MuralhaDigital/Kpi` | Acesso negado | [ ] |

### 4.3 — Exportação GIS (Plano 16)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 16-T01 | Mapa carrega | Acessar `/muralha-digital/pages/monitoramento/mapa/index.jsp` | Leaflet + tiles + pontos circulares | [ ] |
| 16-T02 | Popup equipamento | Clicar em ponto | Nome, ID Local, total passagens | [ ] |
| 16-T03 | Cores por volume | Observar cores | Azul (<100), amarelo (100-1000), vermelho (>1000) | [ ] |
| 16-T04 | Download GeoJSON | Clicar botão "GeoJSON" | `.geojson` válido com coordenadas [lon,lat] | [ ] |
| 16-T05 | Download KML | Clicar botão "KML" | `.kml` abre no Google Earth | [ ] |
| 16-T06 | Sem coordenadas | Ambiente sem locais com coords | SweetAlert informativo | [ ] |
| 16-T07 | Sem sessão | Aba anônima → `GET /MuralhaDigital/Gis?formato=geojson` | Acesso negado | [ ] |

---

## Fase 5 — Monitoramento e Integração (Planos 17–19)

### 5.1 — Time-lapse (Plano 17)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 17-T01 | Lista equipamentos | Acessar `/muralha-digital/pages/monitoramento/timelapse/index.jsp` | Combo com locais ativos | [ ] |
| 17-T02 | Buscar com dados | Selecionar equip + período com passagens → buscar | Imagem exibida; slider; "1/N" | [ ] |
| 17-T03 | Buscar sem dados | Período futuro → buscar | "Nenhum frame encontrado" | [ ] |
| 17-T04 | Controles | Play → Pause → Anterior → Próximo → Slider | Todos funcionais | [ ] |
| 17-T05 | FPS | Slider FPS: 1 (lento) → 10 (rápido) | Velocidade muda; label atualiza | [ ] |
| 17-T06 | Info do frame | Navegar entre frames | Data/hora, placa, número do frame | [ ] |
| 17-T07 | Limite 200 | Período longo > 200 passagens | Máximo 200 frames retornados | [ ] |

### 5.2 — Disponibilidade (Plano 18)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 18-T01 | Tabela e config | `SELECT COUNT(*) FROM muralha.equipamento_disponibilidade; SELECT valor FROM muralha.config_chave_valor WHERE chave='disponibilidade_threshold_min'` | Tabela existe; valor=15 | [x] ✅ |
| 18-T02 | Job registrado | Log: "Job Disponibilidade Equipamentos agendada a cada 10 minutos" | Confirmação no log | [ ] |
| 18-T03 | Verificação | 10 min → `SELECT TOP 10 * FROM muralha.equipamento_disponibilidade ORDER BY id DESC` | Registros com id_local, disponivel | [ ] |
| 18-T04 | Painel status | Acessar `/muralha-digital/pages/monitoramento/disponibilidade/index.jsp` | 4 cards numéricos | [ ] |
| 18-T05 | Tabela status | Verificar tabela "Status atual" | Nome, badge, última passagem, tempo offline | [ ] |
| 18-T06 | Gráfico uptime | Verificar gráfico horizontal | Verde ≥95%, amarelo ≥80%, vermelho <80% | [ ] |
| 18-T07 | Auto-refresh | Aguardar 65s | Dados atualizam sem recarregar | [ ] |
| 18-T08 | Threshold | `UPDATE config_chave_valor SET valor='5' WHERE chave='disponibilidade_threshold_min'` → aguardar job | Equipamentos com >5min offline | [ ] |
| 18-T09 | Sem dados | Acessar antes do 1o job | Cards 0/0/0; sem erro JS | [ ] |

### 5.3 — Sensores Externos (Plano 19)

| ID | Teste | Procedimento | Resultado esperado | Aceite |
|---|---|---|---|---|
| 19-T01 | Tabela e configs | `SELECT COUNT(*) FROM muralha.incidente_externo; SELECT chave,valor FROM muralha.config_chave_valor WHERE chave LIKE 'waze%'` | Tabela existe; 2 configs (url + bbox) | [x] ✅ |
| 19-T02 | Job sem API key | Iniciar sem `WAZE_API_KEY` → log | "WAZE_API_KEY nao configurada; importacao ignorada." | [ ] |
| 19-T03 | Incidente manual | INSERT de teste (ver SQL abaixo) → acessar mapa | Círculo vermelho no ponto | [ ] |
| 19-T04 | Popup | Clicar no círculo do incidente | Tipo, descrição, severidade, data/hora | [ ] |
| 19-T05 | Toggle | Desmarcar/marcar switch "Incidentes" | Camada some/reaparece | [ ] |
| 19-T06 | Cores por tipo | INSERT de JAM e HAZARD (ver SQL abaixo) | Vermelho=ACCIDENT, laranja=JAM, amarelo=HAZARD | [ ] |
| 19-T07 | Duplicata | INSERT mesmo incidente 2x | 2a inserção falha (constraint) | [ ] |
| 19-T08 | Listagem | `GET /MuralhaDigital/Incidente?acao=listar` | JSON com até 200 registros desc | [ ] |

**SQL para teste 19-T03:**
```sql
INSERT INTO muralha.incidente_externo
(fonte, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia)
VALUES ('CSV', 'ACCIDENT', 'Teste - Acidente Av. Paralela', -12.9231, -38.4531, 3, SYSDATETIME());
```

**SQL para teste 19-T06:**
```sql
INSERT INTO muralha.incidente_externo
(fonte, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia)
VALUES
('CSV', 'JAM', 'Congestionamento BR-324', -12.9100, -38.4200, 2, SYSDATETIME()),
('CSV', 'HAZARD', 'Buraco na pista', -12.9400, -38.4700, 1, SYSDATETIME());
```

---

## Resumo de Aceite

| Fase | Planos | Total testes | Aceitos |
|---|---|---|---|
| 1 — Infraestrutura e Segurança | 01–04 | 21 | 4/21 |
| 2 — Processamento de Infrações | 05–08 | 21 | 3/21 |
| 3 — Identidade e Compliance | 09–13 | 31 | 2/31 |
| 4 — Gestão e Analytics | 14–16 | 24 | 2/24 |
| 5 — Monitoramento e Integração | 17–19 | 24 | 2/24 |
| **Total** | **01–19** | **121** | **13/121** |

> **13 testes de banco de dados** verificados via `sqlcmd` em 2026-10-09.  
> **108 testes restantes** requerem servidor rodando + browser.

---

## Referências

- Testes individuais detalhados: [`docs/testes/`](../testes/)
- Pendências de banco: [`pendencias-banco-de-dados.md`](pendencias-banco-de-dados.md)
- Análise de aderência: [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)
