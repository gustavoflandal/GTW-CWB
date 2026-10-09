# Entrega — Plano 17: Time-lapse de Passagens por Equipamento

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Player de time-lapse que exibe sequencia animada de imagens de passagens de um mesmo equipamento em um intervalo de tempo, com controles de play/pause, avanco/retrocesso frame-a-frame e ajuste de velocidade (FPS).

## Origem

- Plano [`17-timelapse.md`](../planos-salvador/17-timelapse.md) — Time-lapse de Passagens por Equipamento

## Arquivos produzidos

| Arquivo | Descricao |
|---|---|
| `src/main/java/muralha/digital/timelapse/TimelapseServlet.java` | GET com `acao=equipamentos` (lista locais ativos) e `acao=frames` (imagens por id_local + periodo). Max 200 frames |
| `src/main/webapp/muralha-digital/pages/monitoramento/timelapse/index.jsp` | Player com controles play/pause, anterior/proximo, slider de posicao, ajuste de FPS (1-10) |

## Arquitetura

- **Sem nova tabela**: consulta `veiculo_tempo_real` + JOIN `veiculo_tempo_real_imagem` para obter UUIDs de imagem
- **Sem geracao de video no servidor**: animacao via `setInterval` no browser, carregando imagens individuais
- **Reutiliza servlet de imagem existente**: `/MuralhaDigital/Veiculo/Imagem?id=<UUID>` (ImgVeiculoTempoReal)
- **Equipamentos via `local_vigente`**: lista locais ativos com nome e id_local
- **Parametros de busca**: id_local (obrigatorio), dtInicio e dtFim (opcionais), SQL parametrizado

## Criterios de aceite

- [x] Build compila sem erros
- [ ] Lista de equipamentos carrega corretamente
- [ ] Busca retorna frames com imagens do periodo
- [ ] Controles play/pause funcionam com velocidade ajustavel
- [ ] Navegacao frame-a-frame (anterior/proximo) funciona
- [ ] Informacoes do frame exibem data/hora e placa

## Pendencias

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/17-timelapse.md`)
