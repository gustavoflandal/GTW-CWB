# Entrega — Plano 24: Correções Pontuais da Auditoria

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — aguardando migração e teste manual (build e testes automatizados aprovados no Windows em 2026-10-16; ver "Critérios de aceite").
> **Fase:** 2 de 11 da correção da auditoria de 2026-10-16.

## Escopo

As sete correções pontuais apontadas pela auditoria no código e nos scripts (a sétima, código morto, foi apenas confirmada e mantida). Nenhum DDL/DML foi executado; `pom.xml` e `.setup-gtw` não foram alterados.

## Origem

- Plano: [`24-correcoes-pontuais-auditoria.md`](../planos-salvador/24-correcoes-pontuais-auditoria.md)

## Arquivos

| Item | Arquivos |
|---|---|
| 1. Prioridade de tipo de alerta | `src/main/java/muralha/digital/notificacao/ConfiguracaoSons.java`, `ConfiguracaoSonsServlet.java`, `src/main/webapp/muralha-digital/pages/configuracao-monitoramento/assets/js/configuracao.js`, teste `src/test/java/muralha/digital/notificacao/ConfiguracaoSonsPrioridadeTest.java` |
| 2. Relatório do menu 201 | **Desativado em 2026-10-16** (decisão do dono do projeto): `RelatorioAlteracaoConfigEquip.java` só avisa que o relatório foi desativado; `res_alteracoes_configequip.jsp` e `RelatorioAlteracaoConfigEquipTest` removidos (revertidos); script `docs/banco-de-dados/migracoes/20261016_desativa_menu_201.sql`. Ver [Entrega 26](26-logs-tecnicos-historico-parametros.md) |
| 3. `AuditoriaService` | `src/main/java/muralha/digital/auditoria/AuditoriaService.java`, [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1 e §10 |
| 4. `vtr_imagem_complemento` | `docs/banco-de-dados/migracoes/20261016_vtr_imagem_complemento.sql` (novo), `20261008_tabelas_complementares.sql` (cabeçalho), [`migracoes/README.md`](../banco-de-dados/migracoes/README.md) |
| 5. IP fixo dos mapas | `src/main/java/muralha/digital/mapas/MapaConfigServlet.java` (novo), `src/main/webapp/muralha-digital/utils/maps-config.js`, `pages/mapa-situacao-transito/assets/js/mapa-situacao-transito.js`, `docs/banco-de-dados/migracoes/20261016_config_mapa_api_trecho.sql` (novo), [`07-configuracao-e-segredos.md`](../requisitos/07-configuracao-e-segredos.md) §4.1, teste `src/test/java/muralha/digital/mapas/MapaConfigServletTest.java` |
| 6. Waze `jams` | `src/main/java/muralha/digital/sensores/IncidenteImportJob.java`, teste `src/test/java/muralha/digital/sensores/IncidenteImportJobTest.java` |
| 7. Código morto | Nenhum: `Funcoes.invertImage` e `ObterImagemModificada` mantidos (sem chamadas; possível uso na Fase 9) |

## Migrações

| Script | Situação |
|---|---|
| `20261016_vtr_imagem_complemento.sql` | Não executado. No DEV a tabela já existe; o script só cria em ambiente novo |
| `20261016_config_mapa_api_trecho.sql` | **Não executado — necessário** para a tela Mapa de situação do trânsito continuar mostrando os trechos |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro. Executado no Windows em 2026-10-16 sobre o `main` com a Fase 2 (`c8a43ce`): BUILD SUCCESS. (Na nuvem só tinha sido possível compilar classe a classe com `javac`.)
- [x] 2. Testes TestNG novos executados direto pelo TestNG 6.8.21 na nuvem (2026-10-16): `ConfiguracaoSonsPrioridadeTest` 4/4, `MapaConfigServletTest` 2/2, `IncidenteImportJobTest` 5/5, `RelatorioAlteracaoConfigEquipTest` 4/4. Repetidos no Windows pelo Maven (`-Dtest=…`, junto com `ChamadoServiceRegrasTest` e `MontadorSqlTest`): 59/59.
- [ ] 3. Roteiro manual [`docs/testes/24-correcoes-pontuais-auditoria.md`](../testes/24-correcoes-pontuais-auditoria.md) executado no Windows.
- [x] 4. `python3 -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Pendências

- **Item 2, relatório do menu 201:** a consulta mínima foi implementada enquanto a escolha não vinha. Em 2026-10-16 o dono do projeto decidiu desativar o menu se ele não tivesse finalidade: o menu não tem nenhum grupo em `sis_menu_direitos` e o histórico de parâmetros com valor anterior e novo passa a ser o da Fase 4 (5.4.7.6). O código do item 2 foi revertido e entrou `20261016_desativa_menu_201.sql` (pendente de execução). Como `fcn_VerificaAcesso` libera URL sem direitos cadastrados, o servlet passou a responder com aviso de desativação em vez da página em branco.
- Executar `20261016_config_mapa_api_trecho.sql` antes de publicar a tela de situação do trânsito.
