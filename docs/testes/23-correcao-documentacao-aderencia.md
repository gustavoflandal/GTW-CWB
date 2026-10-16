# Testes — Plano 23: Correção da Documentação de Aderência

Roteiro manual para a [Entrega 23](../entregas/23-correcao-documentacao-aderencia.md). Não precisa de servidor nem de banco; só Python 3 e Excel. Executar a partir de `D:\GTW-CWB`.

## T01 — Script de validação

```powershell
python -I docs\referencia\scripts\validar_aderencia.py --resumo
```

**Esperado:** código de saída 0; "Links quebrados: 0", "Divergências de status: 0", "Termos proibidos: 0", "Planilha (aba GTW-Muralha): 0"; resumo com 232 itens e cobertura global de 55%.

Resultado na nuvem (2026-10-16): ✅ aprovado. No Windows: ⏳ pendente.

## T02 — Termos proibidos

```powershell
Select-String -Path docs-editais\edital-salvador\analise-aderencia.md, docs\entregas\aderencia-comparativa.md, docs\entregas\rastreabilidade-tr.md, docs\entregas\itens-edital-x-entregas.md, docs\entregas\gaps-*.md -Pattern 'IntegridadeJob','Shapefile','5\.3\.7\.5.*4 ?s(eg)?'
```

**Esperado:** nenhuma linha. ⏳ pendente no Windows.

## T03 — Detecção de divergência

Trocar temporariamente a situação de um item em `docs/entregas/aderencia-comparativa.md` (por exemplo, 5.4.7.6 de ❌ para ✅) e rodar T01.

**Esperado:** código 1 e a divergência listada com o arquivo e o item. Desfazer a alteração (`git checkout -- docs/entregas/aderencia-comparativa.md`). ⏳ pendente.

## T04 — Planilha no Excel

Abrir `docs-editais\edital-salvador\Atividades Edital Salvador 2026.xlsx`.

**Esperado:** abre sem aviso de reparo; aba GTW-Muralha com a coluna "Obs:" atualizada (título com cobertura de 55%); comentários encadeados e tarefas das outras abas presentes como antes. ⏳ pendente.

## T05 — Navegação

No GitHub, abrir [`itens-edital-x-entregas.md`](../entregas/itens-edital-x-entregas.md) e seguir três links de evidência ao acaso, e os links de [`rastreabilidade-tr.md`](../entregas/rastreabilidade-tr.md#cobertura-por-plano).

**Esperado:** todos abrem o arquivo certo. ⏳ pendente.
