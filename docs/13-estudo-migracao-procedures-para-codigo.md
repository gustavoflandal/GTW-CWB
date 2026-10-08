# 13 — Estudo: migrar procedures e funções do banco para o código

> Pergunta: vale transferir as 388 procedures e 302 funções do `GTW_MURALHA_DEV` para o código Java, em busca de ganho de performance? Quais ferramentas usar?
>
> **Resposta curta:** migrar tudo, não. Migrar tudo **não é o que dá o ganho de performance**. Os ganhos grandes e baratos estão no próprio banco (nível de compatibilidade 110, cursores, funções escalares, falta de telemetria). A migração para o código vale **de forma seletiva**, por manutenibilidade e testabilidade, e traz ganho de performance só em casos específicos (§4).

Números levantados em 2026-10-08 a partir de `docs/banco-de-dados/codigo-sql/` e de `src/`. A busca no Java é textual (`EXEC schema.proc`), então os números de chamadas são estimativas.

## 1. Diagnóstico do cenário atual

| Item | Valor | Leitura |
|---|---|---|
| Procedures / funções | 388 / 302 (≈ 36 mil e ≈ 17 mil linhas de T-SQL) | A lógica de negócio está dividida entre Java e banco |
| Procedures chamadas direto do Java | ≈ 59 | Cerca de 15%. As demais são chamadas por outras procedures, jobs, relatórios Jasper (7 `.jrxml`) ou estão mortas |
| Procedures com cursor | 56 | Processamento linha a linha: principal suspeita de lentidão |
| Procedures com tabela temporária | 47 | Normal, mas pode causar recompilações |
| Procedures com SQL dinâmico | 33 | Risco de injeção e de plano ruim |
| Procedures com transação explícita | 79 | Regra de consistência hoje vive no banco |
| Procedures com `_old`, `teste`, `bkp`, `new` no nome | ≈ 21 | Código morto ou duplicado |
| Triggers | 8 | Ex.: placa Mercosul, classificação, semelhança de placa |
| Nível de compatibilidade | **110** (SQL Server 2012), em um servidor 2016 SP2 | Usa o estimador de cardinalidade antigo e não aproveita as melhorias do otimizador do 2016 |
| Maiores tabelas | 10,9 M, 8,2 M e 8,2 M de linhas; 736 GB | Há muito dado perto do banco |
| Pool de conexões | `commons-dbcp` (`BasicDataSource`) em `Conexao`, JDBC puro | Sem camada de acesso a dados, sem ORM |
| Testes automatizados | Apenas 6 arquivos em `src/test` (build com `-DskipTests`) | A migração precisa começar pela rede de segurança |

## 2. Vantagens reais de levar a lógica para o código

1. **Testabilidade.** T-SQL não tem teste unitário prático. Em Java dá para usar JUnit e integração contínua.
2. **Versionamento e revisão.** A lógica passa a ser revisada junto com o código que a chama. Hoje o banco DEV é a fonte da verdade e `codigo-sql/` é só uma extração.
3. **Deploy atômico.** O WAR e a lógica sobem juntos. Hoje uma alteração de procedure vai separada, sem rollback fácil.
4. **Escala horizontal.** CPU de aplicação é barata e escala em mais nós. O SQL Server Standard está limitado a 4 sockets/24 núcleos e licença por núcleo.
5. **Menos acoplamento a um fornecedor.** Facilita trocar de SGBD ou usar réplicas de leitura.
6. **Cache.** Resultado de cálculo caro (ex.: totalizadores de painel) pode ser cacheado na aplicação.
7. **Observabilidade.** Métricas, tracing e logs por regra de negócio ficam mais fáceis no Java.
8. **Segurança.** Elimina as 33 procedures de SQL dinâmico, que passam a usar SQL parametrizado montado em código.

## 3. Riscos e custos (o que se perde)

1. **Proximidade dos dados.** Hoje uma procedure agrega milhões de linhas e devolve centenas. Em Java isso vira trafegar milhões de linhas pela rede, ou reescrever em SQL, e então a lógica continua no banco. **Este é o principal motivo para a migração em massa piorar a performance** (relatórios, `spu_getRelatorio`, `spu_obterVeiculos*`, painéis).
2. **Idas e voltas ao banco (N+1).** Um cursor de 10 mil linhas dentro do banco vira 10 mil chamadas JDBC se for traduzido literalmente.
3. **Transações.** As 79 procedures transacionais precisam ter limites claros. Transação aberta em Java por mais tempo segura locks por mais tempo.
4. **Outros consumidores.** Relatórios Jasper (7 arquivos), jobs do SQL Agent, integrações e o esquema `mobilidade` podem chamar procedures sem passar pelo Java. É preciso mapear antes de remover qualquer uma.
5. **Triggers.** As 8 triggers garantem regras mesmo para quem escreve direto no banco. Migrar muda essa garantia.
6. **Volume e risco.** ≈ 53 mil linhas de T-SQL, sem testes. Reescrever tudo é um projeto de meses com risco alto de regressão em um sistema legado de fiscalização.
7. **Restrições do projeto.** O `CLAUDE.md` proíbe novos frameworks e mudança no `pom.xml` sem aprovação, e o banco DEV é somente leitura sem autorização.

## 4. Onde há ganho de performance (e onde não há)

### 4.1 Ganhos que **não exigem migração** (fazer primeiro)

| Ação | Por quê | Esforço |
|---|---|---|
| Subir o nível de compatibilidade de 110 para **130** em um ambiente de teste | Habilita o novo estimador de cardinalidade; costuma melhorar planos de consultas grandes. Precisa de teste de regressão: alguns planos pioram | Baixo |
| Ligar o **Query Store** (funciona mesmo no nível 110) | Mostra as consultas e procedures que mais consomem, por duração, CPU e leituras. Sem isso, qualquer priorização é chute | Baixo |
| Reescrever os **56 cursores** como operações em conjunto (`UPDATE ... FROM`, `MERGE`, `ROW_NUMBER`, CTE) | Cursor é lento no SQL Server. Reescrever em SQL em conjunto costuma dar ganho de 10x ou mais | Médio |
| Substituir **funções escalares** usadas em `WHERE`/`SELECT` de consultas grandes por funções inline ou `JOIN` | Em nível 110/130 a função escalar executa linha a linha e impede paralelismo. A inline do SQL Server 2019 (nível 150) não está disponível aqui | Médio |
| Revisar índices (ausentes, duplicados, não usados) nas maiores tabelas | `veiculo_tempo_real*`, `veiculo_estatistica`, `log_processos` | Médio |
| Corrigir parâmetros que causam *parameter sniffing* e SQL dinâmico sem `sp_executesql` parametrizado | Planos ruins e recompilação | Médio |
| Remover procedures mortas (`_old`, `teste`, `bkp`) | Reduz superfície de manutenção | Baixo |

### 4.2 Candidatos à migração para o código

| Tipo | Exemplos | Ganho esperado |
|---|---|---|
| Loops linha a linha que **não** dá para escrever em conjunto, ou que chamam serviços externos | Importação de imagens e lotes (`spu_finaliza_importacao_*`), notificações | Performance e controle de erro melhores; permite paralelismo e retentativa |
| Regras de negócio e validações | `spu_digitacao_confirmada`, regras de infração, correlacionamento | Testabilidade e manutenção. Performance neutra |
| Formatação e montagem de JSON | `spu_RelatorioEstatisticoAlarmesJson` | Menos CPU no banco; o banco devolve só dados |
| Funções escalares de formatação | `InitCap`, `DAC_AIT`, `TEMPO_DECORRIDO` | Remove linha a linha da consulta; ganho real em listas grandes |
| Cálculos repetidos que cabem em cache | Totalizadores de painel, tabelas de domínio | Reduz carga no banco |

### 4.3 O que deve **ficar** no banco

- Agregações e junções sobre as tabelas grandes (`veiculo_tempo_real*`, `veiculo_estatistica`, `veiculo_sumarizado*`). Se hoje estão em procedure, o caminho é otimizá-las, ou convertê-las em **views indexadas** ou tabelas sumarizadas, e não levá-las ao Java.
- Procedures chamadas por relatórios Jasper e jobs, até que esses consumidores sejam migrados.
- Triggers de integridade (placa Mercosul etc.), a menos que todo acesso passe a ser pelo Java.

## 5. Ferramentas sugeridas

Itens marcados com ⚠️ **exigem aprovação**, pois alteram o `pom.xml`, o que o `CLAUDE.md` restringe.

### Medição e diagnóstico (sem mudança no projeto)
| Ferramenta | Uso |
|---|---|
| **Query Store** (nativo do SQL Server 2016) | Ranking das consultas e procedures mais caras; comparação antes e depois |
| **Extended Events** e DMVs (`sys.dm_exec_procedure_stats`, `sys.dm_db_missing_index_details`) | Medir execuções reais e índices ausentes |
| **sp_BlitzCache / sp_BlitzIndex** (First Responder Kit, código aberto) | Diagnóstico de planos e índices |
| **SQL Server Management Studio / Azure Data Studio** | Planos de execução reais |
| `sys.sql_expression_dependencies` | Mapa de quem chama quem; base do inventário de consumidores |

### Acesso a dados em Java
| Opção | Prós | Contras |
|---|---|---|
| **JDBC puro com `PreparedStatement`** (o que já existe) | Sem dependência nova | Muito código repetido |
| ⚠️ **JDBI 3** ou **Spring JdbcTemplate** | Camada leve, SQL continua explícito, sem ORM | Nova dependência |
| ⚠️ **MyBatis** | SQL em arquivos XML, bom para consultas complexas | Nova dependência |
| ⚠️ **jOOQ** | SQL tipado | **A edição gratuita não suporta SQL Server**; exige licença comercial |
| ⚠️ **Hibernate/JPA** | Mapeamento objeto-relacional | Não indicado: o domínio é de consultas e relatórios e o legado é grande |

**Recomendação:** JDBI ou `JdbcTemplate`, mantendo o SQL escrito à mão e parametrizado. Se não houver aprovação de novas dependências, seguir com JDBC puro e criar um pequeno utilitário próprio.

### Infraestrutura
| Ferramenta | Uso |
|---|---|
| ⚠️ **HikariCP** | Substitui o `commons-dbcp` (BasicDataSource), mais rápido e com boas métricas. Compatível com JDK 13 |
| ⚠️ **Caffeine** | Cache local para totalizadores e domínios |
| **Quartz** (já no `pom.xml`) | Agendamento dos jobs que hoje são procedures chamadas por jobs |
| ⚠️ **Flyway** | Versionar os scripts de `docs/banco-de-dados/migracoes/` |

### Testes e validação
| Ferramenta | Uso |
|---|---|
| ⚠️ **JUnit 5** + **Testcontainers (SQL Server)** | Rodar testes contra um SQL Server real em contêiner, com dados de amostra |
| **Teste de equivalência (modo sombra)** | Executar a procedure e a nova implementação Java com as mesmas entradas e comparar o resultado antes de trocar |
| ⚠️ **JMH** / **Gatling** ou **k6** | Microbenchmark e teste de carga das rotas migradas |

## 6. Roteiro recomendado

1. **Inventário (1–2 semanas).** Montar a matriz procedure → quem chama (Java, outra procedure, job do SQL Agent, `.jrxml`, trigger). Usar `sys.sql_expression_dependencies`, o catálogo em `objetos-programaveis.md` e os agendamentos do SQL Agent. Marcar as mortas.
2. **Medir (1 semana).** Ligar o Query Store em um ambiente de homologação com carga real e ranquear as 20 procedures e consultas mais caras por duração total.
3. **Ganhos no banco (2–4 semanas).** Para o top 20: reescrever cursores, trocar funções escalares, ajustar índices. Testar o nível de compatibilidade 130. Medir de novo.
4. **Rede de segurança.** Criar testes de equivalência para as procedures que serão migradas.
5. **Migrar em fatias (contínuo).** Seguir o padrão "estrangulador": uma procedure por vez, atrás de uma classe de serviço no pacote `muralha.digital.<modulo>`, com chave de configuração para voltar à procedure. Começar por regras de negócio e loops (§4.2), nunca por agregações pesadas.
6. **Remover.** Só apagar a procedure do banco quando o inventário confirmar que não há outro consumidor e após um período em paralelo.

## 7. Decisões que dependem de você

1. Aprovar ou não novas dependências (HikariCP, JDBI/JdbcTemplate, Flyway, Testcontainers, JUnit 5) ou manter JDBC puro.
2. Dar acesso somente leitura a DMVs e ao Query Store de um ambiente com carga realista (o DEV talvez não represente a produção).
3. Confirmar se existem jobs do SQL Agent, outras aplicações ou relatórios externos que chamem as procedures.
4. Definir o critério de sucesso (ex.: reduzir em X% o tempo das 20 consultas mais caras), para saber quando parar de migrar.

## 8. Conclusão

- A hipótese "migrar tudo para o código dá ganho significativo de performance" **não se sustenta como regra geral**. O ganho aparece quando se elimina processamento linha a linha, se corrige o nível de compatibilidade e as funções escalares, e se põe cache onde cabe. Parte disso se faz sem tirar nada do banco.
- Migrar **vale** pelos ganhos de testabilidade, versionamento e segurança, e deve ser feito em fatias, guiado por medição.
- Primeiro passo de menor risco e maior retorno: **ligar o Query Store e ranquear as consultas**. A partir dos dados, decidir o que migrar e o que otimizar no próprio banco.
