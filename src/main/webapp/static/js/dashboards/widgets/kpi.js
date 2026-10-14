export function renderizarKpi(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var valueKey = (consulta.measures && consulta.measures[0]) || null;
    if (!valueKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { valueKey = k; break; } } }
    if (!valueKey) valueKey = keys[0];

    var valor = dados[0][valueKey];
    var valorFormatado = formatarValor(valor, visual.formato);

    var cor = '#0d75bf';
    var badgeTexto = '';
    var badgeCor = '';
    if (visual.thresholds && visual.thresholds.length > 0) {
        var ths = visual.thresholds.sort(function(a, b) { return a.valor - b.valor; });
        for (var i = ths.length - 1; i >= 0; i--) {
            if (valor >= ths[i].valor) {
                cor = ths[i].cor || cor;
                badgeTexto = ths[i].rotulo || '';
                badgeCor = ths[i].cor || '';
                break;
            }
        }
        if (!badgeCor && ths.length > 0) {
            cor = ths[0].cor || cor;
            badgeTexto = ths[0].rotulo || '';
            badgeCor = ths[0].cor || '';
        }
    }

    var subtitulo = visual.subtitulo || widget.titulo || valueKey;
    var icone = visual.icone || 'bi-graph-up';

    var html = '<div style="display:flex;flex-direction:column;align-items:center;justify-content:center;height:100%;padding:1rem;text-align:center">';
    html += '<div style="font-size:1.5rem;color:var(--bs-secondary-color);margin-bottom:0.5rem"><i class="bi ' + escapeHtml(icone) + '"></i></div>';
    html += '<div style="font-size:2.5rem;font-weight:700;color:' + escapeHtml(cor) + ';line-height:1.1">' + escapeHtml(valorFormatado) + '</div>';
    html += '<div style="font-size:0.875rem;color:var(--bs-secondary-color);margin-top:0.25rem">' + escapeHtml(subtitulo) + '</div>';

    if (badgeTexto) {
        html += '<span class="badge mt-2" style="background:' + escapeHtml(badgeCor) + ';font-size:0.75rem">' + escapeHtml(badgeTexto) + '</span>';
    }

    if (visual.comparacao && dados.length > 1) {
        var valorAnterior = dados[1][valueKey] || 0;
        if (valorAnterior !== 0) {
            var variacao = ((valor - valorAnterior) / valorAnterior * 100).toFixed(1);
            var seta = variacao >= 0 ? 'bi-arrow-up' : 'bi-arrow-down';
            var corVariacao = variacao >= 0 ? '#28a745' : '#dc3545';
            html += '<div style="font-size:0.8rem;margin-top:0.5rem;color:' + corVariacao + '">';
            html += '<i class="bi ' + seta + '"></i> ' + variacao + '%</div>';
        }
    }

    html += '</div>';
    elemento.innerHTML = html;
}

function formatarValor(valor, formato) {
    if (valor == null) return '-';
    if (typeof valor !== 'number') return String(valor);

    if (formato === 'inteiro') return Math.round(valor).toLocaleString('pt-BR');
    if (formato === 'decimal') return valor.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    if (formato === 'percentual') return valor.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) + '%';
    if (formato === 'moeda') return 'R$ ' + valor.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    if (valor >= 1000000) return (valor / 1000000).toFixed(1) + 'M';
    if (valor >= 1000) return (valor / 1000).toFixed(1) + 'K';
    return typeof valor === 'number' ? valor.toLocaleString('pt-BR') : String(valor);
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(String(str)));
    return div.innerHTML;
}
