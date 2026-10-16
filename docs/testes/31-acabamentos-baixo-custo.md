# Testes — Plano 31: Acabamentos de baixo custo

Roteiro para a [Entrega 31](../entregas/31-acabamentos-baixo-custo.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Usuário do grupo 42.

## T01 — Testes automatizados GisServlet

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=GisServletTest'
```

**Esperado:** 6 testes, 0 falhas. ⏳ Pendente.

## T02 — Inversão de cores na dupla-análise

1. Navegar para Processamento > Dupla Análise.
2. Carregar uma infração com imagem.
3. Clicar em "Inverter" — verificar que a imagem fica com cores invertidas.
4. Clicar novamente — verificar que volta ao normal.
5. Ajustar brilho/contraste com inversão ligada — verificar que ambos os filtros coexistem.
6. Clicar "Resetar" — verificar que inversão é desligada junto.

**Esperado:** toggle funciona, coexiste com brilho/contraste, reset limpa tudo. ⏳ Pendente.

## T03 — Inversão de cores no tratar-alerta

1. Navegar para Alertas > Tratar Alerta.
2. Abrir um alerta com imagem.
3. Repetir os passos de T02 para o botão "Inverter" desta tela.

**Esperado:** mesmo comportamento que T02. ⏳ Pendente.

## T04 — Inversão de cores na consulta-veículo

1. Navegar para Consulta de Veículo.
2. Buscar um veículo e abrir o modal de detalhe.
3. Repetir os passos de T02 para o botão "Inverter" do modal.

**Esperado:** mesmo comportamento que T02. ⏳ Pendente.

## T05 — GIS camada de alertas (GeoJSON)

1. Acessar `/MuralhaDigital/Gis?camada=alertas&formato=geojson&inline=1`.
2. Verificar que retorna FeatureCollection com features de alertas.
3. Verificar campos: id, data, tipo, placa, id_local, nome_local.

**Esperado:** GeoJSON válido com alertas dos últimos 30 dias. ⏳ Pendente.

## T06 — GIS camada de infrações (KML)

1. Acessar `/MuralhaDigital/Gis?camada=infracoes&formato=kml`.
2. Verificar que faz download de `.kml`.
3. Verificar que o KML contém Placemarks com infrações.

**Esperado:** KML válido com infrações dos últimos 30 dias. ⏳ Pendente.

## T07 — GIS camada padrão (backward-compatible)

1. Acessar `/MuralhaDigital/Gis?formato=geojson&inline=1` (sem `camada`).
2. Verificar que retorna equipamentos (comportamento original).

**Esperado:** GeoJSON com equipamentos, igual ao anterior. ⏳ Pendente.

## T08 — Filtro por perfil no log de auditoria

1. Navegar para Auditoria > Consultar Log.
2. Verificar que o select "Perfil" está populado com grupos.
3. Selecionar um perfil e buscar.
4. Verificar que os resultados correspondem a usuários daquele grupo.

**Esperado:** filtro funciona e retorna apenas registros do perfil selecionado. ⏳ Pendente.

## T09 — Filtro por equipamento no log de auditoria

1. No log de auditoria, selecionar um equipamento e buscar.
2. Verificar que os registros retornados contêm o nome do equipamento na descrição.

**Esperado:** filtro filtra por nome do local na descrição do log. ⏳ Pendente.

## T10 — Exportação PDF do log de auditoria

1. Buscar registros no log de auditoria.
2. Clicar no botão "PDF".
3. Abrir o arquivo gerado.

**Esperado:** PDF landscape A4, título "Log de Auditoria — Muralha Digital", cabeçalho azul (#0d75bf), dados da tabela. ⏳ Pendente.
