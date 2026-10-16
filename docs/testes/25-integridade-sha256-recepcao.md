# Testes — Plano 25: Integridade SHA-256 na Recepção

Roteiro para a [Entrega 25](../entregas/25-integridade-sha256-recepcao.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_integridade_sha256_recepcao.sql` executada com autorização (`sqlcmd -b -f 65001 -i …`).
- Usuário do grupo 42 (Supervisores Anel) para a aba Configuração; usuário só do grupo 41 para conferir que ela fica oculta.
- O DEV não recebe passagens desde 2026-10-01. Para o job conferir imagens, aumentar a janela: `UPDATE muralha.config_chave_valor SET valor = '720' WHERE chave = 'INTEGRIDADE_JANELA_HORAS'` (voltar para `24` no fim).

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=ConferenciaIntegridadeTest,OrigemHashTest,ProcessadorIntegridadeTest,FiltroIntegridadeTest'
```

**Esperado:** 31 testes, 0 falhas. No Windows (2026-10-16): ✅ 31/31 (rodados junto com os 59 das Fases 2 e anteriores: 90/90).

## T02 — Migração e legado

Depois do script:

```sql
SELECT status_integridade, COUNT(*) FROM muralha.vtr_imagem_complemento GROUP BY status_integridade;
SELECT status_integridade, COUNT(*) FROM muralha.vtr_imagem_integridade GROUP BY status_integridade;
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'INTEGRIDADE[_]%';
```

**Esperado:** nenhuma linha `OK`; as antigas como `SEM_CONFERENCIA` com o motivo "Hash calculado antes de 2026-10-16…"; cinco chaves. Rodar o script de novo só imprime "Ja existe". ✅ 2026-10-16: nenhum `OK`; 200 000 `SEM_CONFERENCIA` nas duas tabelas; cinco chaves; tipo de alerta `INTEGRIDADE` com prioridade 2; menus 384-386.

## T03 — Job com origem ausente

Com `INTEGRIDADE_ORIGEM_HASH = AUSENTE`, aguardar 5 minutos.

**Esperado:** no `GTW_MURALHA_DIGITAL.log`, `JobSha256Imagem: conferidas=N {SEM_CONFERENCIA=N}`; novas linhas em `vtr_imagem_integridade` com motivo "Equipamento sem origem de hash configurada.". Nenhum `'OK'` novo. ⏳ Pendente.

## T04 — Divergência gera alerta e auditoria

1. Escolher uma imagem pendente de um equipamento X e gravar um hash errado:
   `INSERT INTO muralha.vtr_imagem_hash_origem (id_imagem, sha256_origem) VALUES ('<id>', REPLICATE('0', 64))`.
2. Na aba Configuração, definir o equipamento X como `PAYLOAD_SOAP`.
3. Aguardar o job.

**Esperado:** a imagem fica `DIVERGENTE` com motivo "SHA-256 calculado difere do hash de origem…"; um registro em `muralha.alerta` com `origem = 'INTEGRIDADE_SHA256'` ligado à passagem em `alerta_veiculo` (só um, mesmo se a passagem tiver várias imagens divergentes); uma linha em `dbo.sis_log_auditoria` com `login = 'sistema'` e `operacao = 'sha256-divergente'`. ⏳ Pendente.

## T05 — Hash correto resulta íntegro

Repetir o T04 com o hash certo (`SELECT CONVERT(CHAR(64), HASHBYTES('SHA2_256', imagem), 2) FROM muralha.veiculo_tempo_real_imagem WHERE id = '<id>'`, só para imagens até 8 000 bytes; para maiores, calcular no PowerShell com `Get-FileHash`).

**Esperado:** `INTEGRO`, sem alerta. ⏳ Pendente.

## T06 — Tela Recepção

Menu Análise de Dados → Integridade das Imagens. Período de 7 dias, sem equipamento → Pesquisar.

**Esperado:** cartões com Recebidas, Íntegras, Divergentes, Erro de leitura e Sem conferência; tabela por equipamento e dia (linhas com divergência em vermelho); tabela de imagens paginada (50 por página) com status em selo e motivo. Período acima de 92 dias ou datas invertidas: aviso SweetAlert2, sem chamada ao servidor. CSV: arquivo com BOM, cabeçalho, linhas e rodapé `# SHA-256:`; o download entra em `sis_log_auditoria` (`exportar-csv`). XLS e PDF exportam a tabela da tela. ⏳ Pendente.

## T07 — Tela Configuração e permissão

1. Com usuário do grupo 42: mudar a origem padrão para `DADO_BRUTO` sem campo. **Esperado:** aviso "Campo do dado bruto inválido…".
2. Salvar `DADO_BRUTO` com campo `integridade.sha256`. **Esperado:** sucesso; `INTEGRIDADE_ORIGEM_HASH = DADO_BRUTO:integridade.sha256`; registro `config-origem-padrao` em `sis_log_auditoria` com "De AUSENTE para …". Voltar para `AUSENTE`.
3. Por equipamento: escolher `PAYLOAD_SOAP` e salvar; depois "Padrão" e salvar. **Esperado:** linha em `equipamento_integridade_config` com `ativo = 1` e depois `ativo = 0`.
4. Com usuário só do grupo 41: a aba Configuração não aparece; `POST /MuralhaDigital/IntegridadeConfig` é redirecionado.
5. Sem sessão: `GET /MuralhaDigital/IntegridadeRecepcao?acao=resumo&dtInicio=2026-10-01&dtFim=2026-10-02` → 401.

⏳ Pendente.
