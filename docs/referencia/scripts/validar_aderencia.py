#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Valida a documentação de aderência ao TR Salvador.

Origem: docs/planos-salvador/23-correcao-documentacao-aderencia.md (Fase 1 da
correção da auditoria de 2026-10-16).

Verificações:
  (a) links relativos em docs/ e docs-editais/ (arquivos .md) apontam para
      arquivos existentes;
  (b) os status (✅ / ⚠️ / ❌) das fontes de aderência coincidem com a matriz
      canônica docs/entregas/itens-edital-x-entregas.md;
  (c) termos proibidos não aparecem nas fontes de aderência (IntegridadeJob,
      Shapefile, 5.3.7.5 associado a 4 segundos);
  (d) a coluna "Obs:" da aba GTW-Muralha da planilha "Atividades Edital
      Salvador 2026.xlsx" começa com a mesma situação da matriz canônica
      ("Implementado"/"Já atendido" = ✅, "Parcial" = ⚠️, "Não implementado" = ❌;
      "N/A" só é aceito para item ❌ marcado com ‡). Lida só com a biblioteca
      padrão (zipfile), sem openpyxl.

Uso (na raiz do repositório; -I isola o interpretador):
  python -I docs/referencia/scripts/validar_aderencia.py [--raiz CAMINHO] [--resumo]

Código de saída 0 quando não há problema; 1 caso contrário.

Regras de comparação de status:
  - Uma referência numérica ("5.2.3.2-3", "5.2.7", "5.4.7.1–7") é expandida para
    os itens da matriz canônica. O status esperado é o agregado: todos ✅ → ✅;
    todos ❌ → ❌; qualquer outra combinação → ⚠️. A comparação é exata.
  - Uma referência com alínea ("5.1.5a", "5.4.2.3.e", "5.3.7.4d-e") é exata
    quando a matriz tem a alínea como item (ex.: 5.1.5.a). Quando a matriz só
    tem o item, a alínea pode ser mais ou menos completa que o item; só há
    divergência se uma fonte disser ✅ e a outra ❌.
"""
import argparse
import html
import os
import re
import sys
import urllib.parse
import zipfile

CANONICA = "docs/entregas/itens-edital-x-entregas.md"
FONTES = [
    "docs-editais/edital-salvador/analise-aderencia.md",
    "docs/entregas/aderencia-comparativa.md",
    "docs/entregas/rastreabilidade-tr.md",
]
PLANILHA = "docs-editais/edital-salvador/Atividades Edital Salvador 2026.xlsx"
PLANILHA_ABA = "GTW-Muralha"
PLANILHA_STATUS = [("Implementado", "✅"), ("Já atendido", "✅"), ("Parcial", "⚠️"),
                   ("Não implementado", "❌"), ("N/A", "N/A")]
FONTES_TERMOS = FONTES + [
    CANONICA,
    "docs/entregas/gaps-amarelos-pendentes.md",
    "docs/entregas/gaps-vermelhos-pendentes.md",
]
TERMOS_PROIBIDOS = [
    (re.compile(r"IntegridadeJob"), "IntegridadeJob (o job real é JobSha256Imagem)"),
    (re.compile(r"Shapefile", re.I), "Shapefile (a exportação GIS só gera GeoJSON e KML)"),
    (re.compile(r"5\.3\.7\.5.{0,80}?(\b4\s*s\b|\b4\s*seg|≤\s*4)|(\b4\s*seg|≤\s*4\s*s).{0,80}?5\.3\.7\.5", re.I),
     "5.3.7.5 associado a 4 segundos (5.3.7.5 é acesso via navegador)"),
]
COL_REF = {"ref. tr", "item tr", "item do edital"}
COL_STATUS = {"status", "depois", "situação"}
SIMBOLOS = ("✅", "⚠️", "❌")


def simbolo(texto):
    """Primeiro símbolo de status encontrado na célula, ou None."""
    pos = [(texto.find(s), s) for s in SIMBOLOS if s in texto]
    return min(pos)[1] if pos else None


def agrega(status):
    if all(s == "✅" for s in status):
        return "✅"
    if all(s == "❌" for s in status):
        return "❌"
    return "⚠️"


def linhas_tabela(texto):
    """Gera (cabecalho, celulas, numero_linha) para cada linha de tabela markdown."""
    cab = None
    for n, linha in enumerate(texto.splitlines(), 1):
        if not linha.startswith("|"):
            cab = None
            continue
        cel = [c.strip() for c in linha.strip().strip("|").split("|")]
        if cab is None:
            cab = [c.lower() for c in cel]
            continue
        if all(re.fullmatch(r":?-+:?", c) for c in cel if c):
            continue
        yield cab, cel, n


CONTRATUAIS = set()  # itens marcados com ‡ na matriz canônica


def carrega_canonica(raiz):
    itens = {}
    texto = open(os.path.join(raiz, CANONICA), encoding="utf-8").read()
    for cab, cel, n in linhas_tabela(texto):
        if "item do edital" not in cab or "situação" not in cab:
            continue
        ref = cel[cab.index("item do edital")]
        st = simbolo(cel[cab.index("situação")])
        if re.fullmatch(r"5(\.\w+)+", ref) and st:
            itens[ref] = st
            if "‡" in cel[cab.index("situação")]:
                CONTRATUAIS.add(ref)
    return itens


def expande(ref, itens):
    """Devolve (lista_de_itens, alinea_nao_separada) ou (None, None) se não reconhecer."""
    ref = ref.replace("§", "").strip()
    m = re.fullmatch(r"(\d+(?:\.\d+)*)(?:[-–](\d+))?", ref)
    if m:
        base, fim = m.group(1), m.group(2)
        if fim:
            pre, ini = base.rsplit(".", 1)
            nums = [f"{pre}.{i}" for i in range(int(ini), int(fim) + 1)]
        else:
            nums = [base]
        res = []
        for x in nums:
            if x in itens:
                res.append(x)
            else:
                res += [k for k in itens if k.startswith(x + ".")]
        return (res or None), False
    m = re.fullmatch(r"(\d+(?:\.\d+)*)\.?([a-z])(?:[-–]([a-z]))?", ref)
    if m:
        base, a, b = m.group(1), m.group(2), m.group(3) or m.group(2)
        letras = [chr(c) for c in range(ord(a), ord(b) + 1)]
        separadas = [f"{base}.{l}" for l in letras if f"{base}.{l}" in itens]
        if separadas and len(separadas) == len(letras):
            return separadas, False
        if base in itens:
            return [base], True
        return None, None
    return None, None


def compara_status(raiz, itens):
    problemas = []
    for fonte in FONTES:
        texto = open(os.path.join(raiz, fonte), encoding="utf-8").read()
        for cab, cel, n in linhas_tabela(texto):
            iref = next((i for i, c in enumerate(cab) if c in COL_REF), None)
            ist = next((i for i, c in enumerate(cab) if c in COL_STATUS), None)
            if iref is None or ist is None or iref >= len(cel) or ist >= len(cel):
                continue
            st = simbolo(cel[ist])
            ref_txt = cel[iref].replace("§", "")
            if not st or not re.match(r"\s*5(\.|\b)", ref_txt):
                continue
            for ref in re.split(r"\s*[,/]\s*", ref_txt):
                alvo, alinea = expande(ref, itens)
                if alvo is None:
                    problemas.append(f"{fonte}:{n}: referência não reconhecida '{ref}'")
                    continue
                esperado = agrega([itens[i] for i in alvo])
                if alinea:
                    diverge = {st, esperado} == {"✅", "❌"}
                else:
                    diverge = st != esperado
                if diverge:
                    problemas.append(
                        f"{fonte}:{n}: {ref} = {st}, matriz canônica = {esperado} ({', '.join(alvo)})")
    return problemas


def valida_links(raiz):
    problemas = []
    for base in ("docs", "docs-editais"):
        for dp, _, arquivos in os.walk(os.path.join(raiz, base)):
            for nome in arquivos:
                if not nome.endswith(".md"):
                    continue
                caminho = os.path.join(dp, nome)
                texto = open(caminho, encoding="utf-8", errors="replace").read()
                texto = re.sub(r"```.*?```", "", texto, flags=re.S)
                for m in re.finditer(r"\[[^\]]*\]\(([^)\s]+)(?:\s+\"[^\"]*\")?\)", texto):
                    alvo = m.group(1)
                    if re.match(r"^[a-z]+:", alvo) or alvo.startswith("#"):
                        continue
                    alvo = urllib.parse.unquote(alvo.split("#")[0])
                    if alvo and not os.path.exists(os.path.normpath(os.path.join(dp, alvo))):
                        problemas.append(f"{os.path.relpath(caminho, raiz)}: link quebrado '{m.group(1)}'")
    return problemas


def valida_termos(raiz):
    problemas = []
    for fonte in FONTES_TERMOS:
        for n, linha in enumerate(open(os.path.join(raiz, fonte), encoding="utf-8"), 1):
            for rx, desc in TERMOS_PROIBIDOS:
                if rx.search(linha):
                    problemas.append(f"{fonte}:{n}: termo proibido: {desc}")
    return problemas


def _texto_si(si):
    return html.unescape("".join(re.findall(r"<t[^>]*>(.*?)</t>", si, re.S)))


def le_planilha(raiz):
    """Devolve {linha: (item, obs)} da aba GTW-Muralha, lendo o XML do .xlsx."""
    with zipfile.ZipFile(os.path.join(raiz, PLANILHA)) as z:
        ss = [_texto_si(si) for si in re.findall(r"<si>(.*?)</si>", z.read("xl/sharedStrings.xml").decode("utf-8"), re.S)]
        wb = z.read("xl/workbook.xml").decode("utf-8")
        rid = re.search(r'<sheet name="%s"[^>]*r:id="(\w+)"' % re.escape(PLANILHA_ABA), wb).group(1)
        rels = z.read("xl/_rels/workbook.xml.rels").decode("utf-8")
        alvo = re.search(r'<Relationship Id="%s"[^>]*Target="([^"]+)"' % rid, rels)
        if not alvo:
            alvo = re.search(r'<Relationship [^>]*Target="([^"]+)"[^>]*Id="%s"' % rid, rels)
        aba = z.read("xl/" + alvo.group(1).lstrip("/").replace("xl/", "")).decode("utf-8")
    valores = {}
    for m in re.finditer(r'<c r="([AD])(\d+)"([^>]*?)(?:/>|>(.*?)</c>)', aba, re.S):
        col, lin, attrs, corpo = m.group(1), int(m.group(2)), m.group(3), m.group(4) or ""
        v = re.search(r"<v>(.*?)</v>", corpo, re.S)
        if 't="s"' in attrs and v:
            texto = ss[int(v.group(1))]
        elif 't="inlineStr"' in attrs:
            texto = _texto_si(corpo)
        else:
            texto = html.unescape(v.group(1)) if v else ""
        valores.setdefault(lin, {})[col] = texto
    return {l: (c.get("A", "").strip(), c.get("D", "")) for l, c in valores.items()}


def compara_planilha(raiz, itens):
    problemas = []
    for lin, (item, obs) in sorted(le_planilha(raiz).items()):
        if item not in itens:
            continue
        st = next((s for p, s in PLANILHA_STATUS if obs.startswith(p)), None)
        if st is None:
            continue
        ok = st == itens[item] or (st == "N/A" and itens[item] == "❌" and item in CONTRATUAIS)
        if not ok:
            problemas.append(f"{PLANILHA} [{PLANILHA_ABA}!D{lin}]: {item} = {st}, matriz canônica = {itens[item]}")
        for rx, desc in TERMOS_PROIBIDOS:
            if rx.search(obs):
                problemas.append(f"{PLANILHA} [{PLANILHA_ABA}!D{lin}]: termo proibido: {desc}")
    return problemas


def resumo(itens):
    secoes = sorted({".".join(k.split(".")[:2]) for k in itens}, key=lambda s: int(s.split(".")[1]))
    print("Seção | Itens | ✅ | ⚠️ | ❌ | Cobertura")
    tot = {"✅": 0, "⚠️": 0, "❌": 0}
    for s in secoes:
        st = [v for k, v in itens.items() if k.startswith(s + ".")]
        c = {x: st.count(x) for x in SIMBOLOS}
        for x in c:
            tot[x] += c[x]
        print(f"{s} | {len(st)} | {c['✅']} | {c['⚠️']} | {c['❌']} | {round(100 * (c['✅'] + 0.5 * c['⚠️']) / len(st))}%")
    n = sum(tot.values())
    print(f"Total | {n} | {tot['✅']} | {tot['⚠️']} | {tot['❌']} | {round(100 * (tot['✅'] + 0.5 * tot['⚠️']) / n)}%")


def main():
    ap = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    ap.add_argument("--raiz", default=".", help="raiz do repositório (padrão: diretório atual)")
    ap.add_argument("--resumo", action="store_true", help="imprime contagem e cobertura por seção")
    args = ap.parse_args()

    itens = carrega_canonica(args.raiz)
    if not itens:
        print("Matriz canônica sem itens reconhecidos.")
        return 1
    grupos = [
        ("Links quebrados", valida_links(args.raiz)),
        ("Divergências de status", compara_status(args.raiz, itens)),
        ("Termos proibidos", valida_termos(args.raiz)),
        ("Planilha (aba GTW-Muralha)", compara_planilha(args.raiz, itens)),
    ]
    falhou = False
    for titulo, lista in grupos:
        print(f"{titulo}: {len(lista)}")
        for p in lista:
            print("  " + p)
        falhou = falhou or bool(lista)
    print(f"Itens na matriz canônica: {len(itens)}")
    if args.resumo:
        resumo(itens)
    return 1 if falhou else 0


if __name__ == "__main__":
    sys.exit(main())
