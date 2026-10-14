import { api } from '/static/js/dashboards/api.js';

let listaEl = null;
let buscaEl = null;
let tabelasCache = [];
let onSoltarCallback = null;

export function inicializar(containerEl, inputBusca, onSoltar) {
    listaEl = containerEl;
    buscaEl = inputBusca;
    onSoltarCallback = onSoltar;

    buscaEl.addEventListener('input', function () {
        filtrar(buscaEl.value.trim().toLowerCase());
    });

    carregar();
}

async function carregar() {
    var r = await api.get('/api/dash/catalogo?acao=listarTabelas');
    if (!r.ok) return;

    tabelasCache = (r.tabelas || []).filter(function (t) { return t.ativo; });
    renderizar(tabelasCache);
}

function filtrar(termo) {
    if (!termo) { renderizar(tabelasCache); return; }
    var filtradas = tabelasCache.filter(function (t) {
        var nf = t.nomeFisico || t.nome_fisico || '';
        var sf = t.schemaFisico || t.schema_fisico || '';
        return nf.toLowerCase().indexOf(termo) >= 0 ||
               (t.rotulo && t.rotulo.toLowerCase().indexOf(termo) >= 0) ||
               sf.toLowerCase().indexOf(termo) >= 0;
    });
    renderizar(filtradas);
}

function renderizar(tabelas) {
    listaEl.innerHTML = '';
    if (tabelas.length === 0) {
        listaEl.innerHTML = '<div class="text-muted small p-2">Nenhuma tabela encontrada</div>';
        return;
    }

    tabelas.forEach(function (t) {
        var item = document.createElement('div');
        item.className = 'list-group-item list-group-item-action py-1 px-2 d-flex align-items-center gap-1';
        item.setAttribute('draggable', 'true');
        item.dataset.idTabela = t.id;
        item.style.cursor = 'grab';
        item.style.fontSize = '0.85rem';

        var sf = t.schemaFisico || t.schema_fisico || '';
        var nf = t.nomeFisico || t.nome_fisico || '';

        var badge = document.createElement('span');
        badge.className = 'badge';
        badge.style.fontSize = '0.65rem';
        if (sf === 'dbo') { badge.classList.add('bg-secondary'); }
        else if (sf === 'muralha') { badge.style.backgroundColor = '#0d75bf'; badge.style.color = '#fff'; }
        else { badge.classList.add('bg-success'); }
        badge.textContent = sf;

        var nome = document.createElement('span');
        nome.textContent = t.rotulo || nf;
        nome.title = sf + '.' + nf;

        item.appendChild(badge);
        item.appendChild(nome);

        item.addEventListener('dragstart', function (e) {
            e.dataTransfer.setData('text/plain', JSON.stringify({ id: t.id }));
            e.dataTransfer.effectAllowed = 'copy';
            item.style.opacity = '0.5';
        });
        item.addEventListener('dragend', function () {
            item.style.opacity = '1';
        });

        item.addEventListener('mouseenter', function () {
            carregarAmostra(t.id, item);
        });

        listaEl.appendChild(item);
    });
}

var tooltipTimeout = null;
var tooltipEl = null;

async function carregarAmostra(idTabela, itemEl) {
    if (tooltipTimeout) clearTimeout(tooltipTimeout);
    tooltipTimeout = setTimeout(async function () {
        var r = await api.get('/api/dash/catalogo?acao=amostraTabela&idTabela=' + idTabela);
        if (!r.ok || !r.linhas || r.linhas.length === 0) return;
        mostrarTooltipAmostra(r, itemEl);
    }, 600);

    itemEl.addEventListener('mouseleave', function () {
        if (tooltipTimeout) clearTimeout(tooltipTimeout);
        fecharTooltipAmostra();
    }, { once: true });
}

function mostrarTooltipAmostra(dados, itemEl) {
    fecharTooltipAmostra();

    tooltipEl = document.createElement('div');
    tooltipEl.className = 'card shadow position-fixed';
    tooltipEl.style.cssText = 'z-index:9999; max-width:400px; font-size:0.75rem; overflow:auto; max-height:200px;';

    var rect = itemEl.getBoundingClientRect();
    tooltipEl.style.left = (rect.right + 8) + 'px';
    tooltipEl.style.top = rect.top + 'px';

    var html = '<table class="table table-sm table-bordered mb-0"><thead class="table-light"><tr>';
    dados.colunas.forEach(function (c) { html += '<th class="px-1">' + c + '</th>'; });
    html += '</tr></thead><tbody>';
    dados.linhas.forEach(function (row) {
        html += '<tr>';
        row.forEach(function (v) { html += '<td class="px-1">' + (v === null ? '<span class="text-muted">null</span>' : escHtml(String(v))) + '</td>'; });
        html += '</tr>';
    });
    html += '</tbody></table>';
    tooltipEl.innerHTML = html;
    document.body.appendChild(tooltipEl);
}

function fecharTooltipAmostra() {
    if (tooltipEl) { tooltipEl.remove(); tooltipEl = null; }
}

function escHtml(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(s));
    return d.innerHTML;
}

export function obterTabela(id) {
    return tabelasCache.find(function (t) { return t.id === id; });
}

export async function carregarColunasTabela(idTabela) {
    var tabela = obterTabela(idTabela);
    var pkCol = tabela ? (tabela.chavePrimaria || tabela.chave_primaria || '') : '';

    var r = await api.get('/api/dash/catalogo?acao=listarColunas&idTabela=' + idTabela);
    if (!r.ok) return [];
    return (r.colunas || []).map(function (c) {
        var nf = c.nomeFisico || c.nome_fisico;
        return {
            id: c.id,
            nome_fisico: nf,
            rotulo: c.rotulo,
            tipo: c.tipo,
            sensivel: c.sensivel,
            papel_sugerido: c.papelSugerido || c.papel_sugerido,
            chave_primaria: nf === pkCol,
            ativo: c.ativo
        };
    });
}
