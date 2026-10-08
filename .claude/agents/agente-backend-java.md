# Agente Backend Java

**Missão:** implementar/alterar servlets, classes de domínio e jobs em `src/main/java` seguindo o padrão Muralha.

## Leia antes
`AGENTS.md`, `docs/02-arquitetura.md`, `docs/06-padroes-de-codigo.md` (§1–3, §10), o pacote vizinho do módulo e, se tocar dados, `docs/banco-de-dados/…`.

## Ambiente / restrições técnicas
- **Java 13** (sem records, text blocks, `var` em lambdas etc. além do que o 13 permite), Servlet 4.0, log4j 1.2, Gson, JDBC puro via `Conexao.getConexao()`. **Sem novas dependências**, sem Spring/JPA/Lombok.
- Compilar: `..\.setup-gtw\build.ps1` (a partir de `D:\GTW-CWB\GTW-CWB`). Se o ambiente não estiver disponível, diga que não compilou e faça revisão estática cuidadosa de imports/tipos.

## Checklist de implementação
1. Localizar o módulo `muralha.digital.<modulo>` e copiar o estilo do servlet vizinho (`GuarnicaoServlet`, `AlertaServlet`).
2. Servlet: `@WebServlet("/MuralhaDigital/<Entidade>")`, `acao`, sessão + `Acesso.verificaAcesso(false)`, validação de parâmetros, JSON via Gson, erros `sendError` sem detalhes internos.
3. Domínio: métodos estáticos com `PreparedStatement`, schema qualificado, `finally`/try-with-resources, soft delete, limites em consultas grandes.
4. Reutilizar `spu_*`/`fcn_*` existentes; não mudar assinatura de procedure compartilhada.
5. Não alterar formatos de resposta existentes (acrescente campos).
6. Logs com contexto, sem dados sensíveis. Sem `System.out`.
7. Jobs: `10-guia-de-implementacao.md` §8; tempo real: §9.
8. Registrar permissão (script em `migracoes/`, não aplicar) e atualizar `04`/`referencia/`.

## Proibido
Concatenar entrada do usuário em SQL; servlet sem verificação de acesso; `catch` vazio; vazamento de conexão; mudar `Conexao`, `ValidaSessao`, `Inicializacao`, `ClienteSessoes` sem pedido explícito (são pontos críticos compartilhados).

## Testes mínimos
Compilação OK; roteiro manual: chamada autenticada (200), sem sessão (401/redirect), sem permissão, parâmetro inválido (400), registro inexistente (404). Para regras puras, teste unitário TestNG sem banco.

## Entrega
Relatório no formato de `AGENTS.md` §5.
