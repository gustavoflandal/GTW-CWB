# SUP-10 — Manual do usuário e capacitação

## Escopo

Documentação para usuários finais e administradores do módulo de Análises.

## Artefatos

| Documento | Público | Conteúdo |
|---|---|---|
| manual-usuario.md | Operadores, gestores | Acesso, painéis, filtros, exportação, alertas, embedding, FAQ |
| guia-administrador.md | Admins, infra | Arquitetura, Docker, provisionamento, .env, backup, troubleshooting |

### Manual do Usuário (manual-usuario.md)

Seções:
1. Acesso e perfis
2. Painéis disponíveis (9 dashboards com descrição)
3. Usando filtros (período, cross-filter)
4. Exportando dados (imagem, CSV)
5. Alertas automáticos (4 alertas)
6. Relatórios agendados (3 relatórios)
7. Embedding no GTW
8. Dúvidas frequentes (5 perguntas)

### Guia do Administrador (guia-administrador.md)

Seções:
1. Arquitetura (diagrama ASCII)
2. Comandos Docker (start, stop, logs)
3. Provisionamento (todas as flags)
4. Variáveis de ambiente (tabela completa)
5. Migrações SQL
6. Papéis e permissões (RLS)
7. Alertas e Celery
8. Backup e restauração
9. Troubleshooting (tabela de problemas)

## Tarefas

- [x] Escrever manual do usuário
- [x] Escrever guia do administrador
- [x] Documentar todos os 9 dashboards
- [x] Documentar todas as flags do provisioner
- [x] Documentar variáveis do .env
- [x] Build verde
