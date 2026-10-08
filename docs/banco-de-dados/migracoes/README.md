# Migracoes de banco de dados

Scripts versionados (AAAAMMDD_HHmm_descricao.sql). Regras: idempotentes, schema explicito, compativeis com SQL Server 2016 (nivel de compatibilidade 110), rollback comentado, sem dados reais, **nunca aplicados por agentes sem autorizacao humana explicita**. Ver docs/10-guia-de-implementacao.md secao 6 e docs/agentes/agente-banco-de-dados.md.
