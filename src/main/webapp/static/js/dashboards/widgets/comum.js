/**
 * comum.js — utilitarios compartilhados pelos renderizadores de widget.
 *
 * - Rotulos amigaveis: a consulta (/api/dash/consultas) devolve `annotation`
 *   com o rotulo de cada medida/dimensao; o painel e o editor o repassam em
 *   widget.anotacao. Sem isso o tooltip mostraria o alias tecnico do campo.
 * - Formatacao pt-BR de numeros e de datas por granularidade.
 * - Montagem de categorias/series a partir das linhas (inclusive tempo + dimensao).
 * - Metadados (elemento.__dashMeta) usados pelo filtro cruzado e pelo drill-down
 *   para recuperar o valor bruto da categoria clicada.
 */

export const ROTULO_NULO = '(sem valor)';

const PALETAS = {
    padrao: ['#0d75bf', '#28a745', '#ffc107', '#dc3545', '#17a2b8', '#6f42c1', '#fd7e14', '#20c997'],
    azul:   ['#0d47a1', '#1565c0', '#1976d2', '#1e88e5', '#2196f3', '#42a5f5', '#64b5f6', '#90caf9'],
    verde:  ['#1b5e20', '#2e7d32', '#388e3c', '#43a047', '#4caf50', '#66bb6a', '#81c784', '#a5d6a7']
};

const MESES = ['jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set', 'out', 'nov', 'dez'];

export function obterPaleta(nome) {
    return PALETAS[nome] || PALETAS.padrao;
}

export function escapeHtml(s) {
    if (s == null) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

export function htmlVazio() {
    return '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)">' +
        '<div class="text-center"><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
}

export function htmlErro(msg) {
    return '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ' + escapeHtml(msg) + '</div>';
}

// ---------------------------------------------------------------------------
// Rotulos
// ---------------------------------------------------------------------------

export function rotuloCampo(widget, chave) {
    var a = (widget && widget.anotacao) || {};
    var grupos = ['measures', 'dimensions', 'timeDimensions'];
    for (var i = 0; i < grupos.length; i++) {
        var g = a[grupos[i]];
        if (g && g[chave] && g[chave].title) return g[chave].title;
    }
    return chave;
}

// ---------------------------------------------------------------------------
// Formatacao
// ---------------------------------------------------------------------------

export function formatarNumero(v, casas) {
    if (v == null || v === '') return '-';
    var n = typeof v === 'number' ? v : Number(v);
    if (isNaN(n)) return String(v);
    return n.toLocaleString('pt-BR', { maximumFractionDigits: casas == null ? 2 : casas });
}

export function formatarNumeroCompacto(v) {
    if (typeof v !== 'number') return String(v);
    if (Math.abs(v) < 10000) return v.toLocaleString('pt-BR', { maximumFractionDigits: 2 });
    return v.toLocaleString('pt-BR', { notation: 'compact', maximumFractionDigits: 1 });
}

function doisDigitos(n) { return (n < 10 ? '0' : '') + n; }

/** Le "yyyy-MM-ddTHH:mm:ss(.SSS)" (formato do backend) sem passar por fuso. */
export function lerDataIso(valor) {
    var m = /^(\d{4})-(\d{2})-(\d{2})(?:[T ](\d{2}))?/.exec(String(valor == null ? '' : valor));
    if (!m) return null;
    return { ano: +m[1], mes: +m[2], dia: +m[3], hora: m[4] != null ? +m[4] : 0 };
}

export function formatarTempo(valor, granularidade) {
    if (valor == null) return ROTULO_NULO;
    var d = lerDataIso(valor);
    if (!d) return String(valor);
    var data = doisDigitos(d.dia) + '/' + doisDigitos(d.mes) + '/' + d.ano;
    switch (granularidade) {
        case 'year':    return String(d.ano);
        case 'quarter': return Math.floor((d.mes - 1) / 3) + 1 + 'º tri/' + d.ano;
        case 'month':   return MESES[d.mes - 1] + '/' + d.ano;
        case 'week':    return 'Semana de ' + data;
        case 'hour':    return data + ' ' + doisDigitos(d.hora) + 'h';
        default:        return data;
    }
}

// ---------------------------------------------------------------------------
// Estrutura da consulta
// ---------------------------------------------------------------------------

/** Dimensao temporal agrupada da consulta (chave da coluna = "<campo>.<granularidade>"). */
export function infoTempo(widget, dados) {
    var consulta = (widget && widget.consulta) || {};
    var td = (consulta.timeDimensions || [])[0];
    if (!td || !td.granularity) return null;
    var chave = td.dimension + '.' + td.granularity;
    if (dados && dados.length > 0 && !(chave in dados[0])) return null;
    return { chave: chave, dimensao: td.dimension, granularidade: td.granularity };
}

export function medidasDe(widget, dados, todasNumericas) {
    var consulta = (widget && widget.consulta) || {};
    if (consulta.measures && consulta.measures.length > 0) return consulta.measures.slice();
    var lista = [];
    var keys = Object.keys(dados[0]);
    for (var i = 0; i < keys.length; i++) {
        if (typeof dados[0][keys[i]] === 'number') {
            lista.push(keys[i]);
            if (!todasNumericas) break;
        }
    }
    if (lista.length === 0 && keys.length > 1) lista.push(keys[1]);
    return lista;
}

function primeiraTexto(dados, exceto) {
    var keys = Object.keys(dados[0]);
    for (var i = 0; i < keys.length; i++) {
        if (keys[i] !== exceto && typeof dados[0][keys[i]] === 'string') return keys[i];
    }
    return null;
}

/** Colunas a exibir em tabelas: remove a copia sem granularidade da coluna de tempo. */
export function colunasVisiveis(widget, dados) {
    var keys = Object.keys(dados[0]);
    var tempo = infoTempo(widget, dados);
    if (!tempo) return keys;
    return keys.filter(function(k) { return k !== tempo.dimensao; });
}

export function formatarCelula(widget, chave, valor, tempo) {
    if (valor == null) return '';
    if (tempo && chave === tempo.chave) return formatarTempo(valor, tempo.granularidade);
    if (typeof valor === 'number') return formatarNumero(valor);
    return String(valor);
}

/**
 * Monta categorias e series para graficos cartesianos.
 * Com tempo agrupado e uma dimensao, as linhas chegam como (tempo, dimensao, medida):
 * um eixo recebe as categorias e o outro campo vira serie, para nao repetir
 * a mesma categoria varias vezes no eixo (e no tooltip).
 *
 * opcoes.tempoNoEixo: true para graficos de evolucao (linhas/area).
 */
export function montarSeries(widget, dados, opcoes) {
    opcoes = opcoes || {};
    var consulta = widget.consulta || {};
    var dims = (consulta.dimensions || []).slice();
    var tempo = infoTempo(widget, dados);
    var medidas = medidasDe(widget, dados, opcoes.todasNumericas);

    var eixo, serie;
    if (tempo && (opcoes.tempoNoEixo || dims.length === 0)) {
        eixo = tempo.chave;
        serie = dims[0] || null;
    } else if (dims.length > 0) {
        eixo = dims[0];
        serie = dims[1] || (tempo ? tempo.chave : null);
    } else {
        eixo = primeiraTexto(dados) || Object.keys(dados[0])[0];
        serie = null;
    }

    var formatar = function(chave, v) {
        if (tempo && chave === tempo.chave) return formatarTempo(v, tempo.granularidade);
        return v == null || v === '' ? ROTULO_NULO : String(v);
    };

    var categorias = [];
    var idxCategoria = {};
    var valoresSerie = [];
    var idxSerie = {};
    dados.forEach(function(r) {
        var c = r[eixo] == null ? null : r[eixo];
        var kc = String(c);
        if (!(kc in idxCategoria)) { idxCategoria[kc] = categorias.length; categorias.push(c); }
        if (serie) {
            var s = r[serie] == null ? null : r[serie];
            var ks = String(s);
            if (!(ks in idxSerie)) { idxSerie[ks] = valoresSerie.length; valoresSerie.push(s); }
        }
    });

    var series = [];
    var grupos = serie ? valoresSerie : [undefined];
    grupos.forEach(function(gv) {
        medidas.forEach(function(m) {
            var valores = categorias.map(function() { return null; });
            dados.forEach(function(r) {
                if (serie && String(r[serie] == null ? null : r[serie]) !== String(gv)) return;
                var i = idxCategoria[String(r[eixo] == null ? null : r[eixo])];
                var v = r[m];
                valores[i] = v == null ? null : (valores[i] == null ? v : valores[i] + v);
            });
            var nome = rotuloCampo(widget, m);
            if (serie) {
                var nomeGrupo = formatar(serie, gv);
                nome = medidas.length > 1 ? nomeGrupo + ' · ' + nome : nomeGrupo;
            }
            series.push({ nome: nome, medida: m, dados: valores });
        });
    });

    return {
        eixo: eixo,
        eixoEhTempo: !!(tempo && eixo === tempo.chave),
        granularidade: tempo ? tempo.granularidade : null,
        dimensaoTempo: tempo ? tempo.dimensao : null,
        categorias: categorias,
        rotulosCategorias: categorias.map(function(c) { return formatar(eixo, c); }),
        series: series,
        medidas: medidas
    };
}

/** Guarda no elemento o que o filtro cruzado e o drill-down precisam para ler o clique. */
export function registrarMeta(elemento, meta) {
    elemento.__dashMeta = meta;
}

// ---------------------------------------------------------------------------
// ECharts
// ---------------------------------------------------------------------------

/** Tooltip padrao: valores em pt-BR. O formatter padrao do ECharts ja escapa nomes. */
export function tooltipEixo() {
    return { trigger: 'axis', valueFormatter: function(v) { return formatarNumero(v); } };
}

export function eixoValor(extra) {
    return Object.assign({ type: 'value', axisLabel: { formatter: formatarNumeroCompacto } }, extra || {});
}

export function rotuloValor(extra) {
    return Object.assign({ formatter: function(p) { return formatarNumero(p.value); } }, extra || {});
}

/**
 * Descarta a instancia do ECharts presa ao elemento, se houver. Obrigatorio antes de
 * trocar o innerHTML do elemento: senao a instancia continua registrada, o proximo
 * setOption desenha num canvas que ja saiu da tela e nada aparece.
 */
export function descartarGrafico(elemento) {
    if (typeof echarts === 'undefined' || !elemento) return;
    var inst = echarts.getInstanceByDom(elemento);
    if (inst) inst.dispose();
}

/** Substitui o conteudo do elemento por HTML, descartando o grafico que estivesse nele. */
export function mostrarHtml(elemento, html) {
    descartarGrafico(elemento);
    elemento.innerHTML = html;
}

export function aplicarOpcao(elemento, option) {
    if (typeof echarts === 'undefined') {
        elemento.innerHTML = htmlErro('ECharts não carregado');
        return;
    }
    try {
        var chart = echarts.getInstanceByDom(elemento);
        // Instancia cujo canvas foi removido (alguem trocou o innerHTML): recria.
        if (chart && !elemento.querySelector('canvas, svg')) {
            chart.dispose();
            chart = null;
        }
        if (!chart) chart = echarts.init(elemento);
        chart.setOption(option, true);
        if (!elemento.__observadorResize) {
            elemento.__observadorResize = new ResizeObserver(function() {
                var c = echarts.getInstanceByDom(elemento);
                if (c) c.resize();
            });
            elemento.__observadorResize.observe(elemento);
        }
    } catch (e) {
        mostrarHtml(elemento, htmlErro('Erro: ' + e.message));
    }
}

export function garantirAltura(elemento) {
    if (elemento.clientHeight < 100) elemento.style.height = '300px';
}
