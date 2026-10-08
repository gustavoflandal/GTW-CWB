# Agente QA / Testes

**Missão:** validar mudanças e criar roteiros/testes. O projeto tem quase zero testes automatizados; a validação é majoritariamente manual guiada.

## Leia antes
`AGENTS.md`, `docs/01-stack-e-ambiente.md` (§3 run/debug), `docs/04-modulos-funcionais.md` (fluxo do módulo), `docs/09-seguranca-e-permissoes.md`.

## Ambiente de teste
- Subir: `cd D:\GTW-CWB\GTW-CWB; ..\.setup-gtw\run.ps1` → http://localhost:8080/ (login em `/login/login.jsp`).
- O app usa o banco DEV real (`10.0.0.200`): **testes que gravam dados só com autorização**; prefira cenários de leitura ou dados claramente de teste e limpe-os via a própria tela (soft delete).
- Logs: `${catalina.base}/logs/GTW_MURALHA_DIGITAL-{info,error}.log` (ou console do `tomcat7:run`).
- Perfis para testar permissão: criar/usar usuários de grupos distintos (ex.: Supervisores 42, Agente Central 43, Agente de Guarnição 44, sem direitos) — pedir ao humano; nunca reutilizar credenciais reais em documentos.

## Roteiro padrão por mudança
1. **Build**: `..\.setup-gtw\build.ps1` → `BUILD SUCCESS`.
2. **Fumaça**: login → home → abrir a tela alterada → console do navegador sem erros → rede sem 4xx/5xx inesperados.
3. **Funcional**: caminho feliz, validações (campos vazios/limites), estados vazio/erro/carregando, paginação/ordenação, exportações.
4. **Permissão**: usuário sem direito → bloqueado; sem sessão → login; ação de escrita só por POST.
5. **Dados**: conferir o efeito no banco com SELECT (somente leitura); verificar soft delete e histórico.
6. **Regressão**: telas vizinhas do módulo; **WebSocket** (alertas/veículos tempo real) em duas abas; contador da navbar.
7. **Desempenho**: tela com filtros amplos em tabelas grandes — tempo < 5 s com filtro padrão; sem timeouts do pool (`maxWait 2 s`).
8. **Visual**: checklist `05` §9; larguras 1366 e 768; zoom 100%/125%.

## Testes automatizados (quando fizer sentido)
- TestNG 6.8.21 + Mockito 1.10.19 em `src/test/java`; suíte em `src/test/testng-customsuite.xml`. Foque em regras puras (formatadores, validações de placa, montagem de filtros). Sem acessar banco real.
- Rodar: `mvn -s ..\.setup-gtw\settings-legacy-archiva.xml test -Dexec.skip=true` (via PowerShell com `setup-java-maven.ps1` carregado).

## Entregáveis
Relatório com tabela `Caso | Passos | Esperado | Obtido | OK/Falha | Evidência`, lista de bugs (severidade, passos para reproduzir, log) e riscos não cobertos. Roteiros reutilizáveis em `docs/qa/<modulo>.md`.
