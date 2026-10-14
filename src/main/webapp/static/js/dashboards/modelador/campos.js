let containerEl = null;
let camposSelecionados = [];
let onCampoChangeCallback = null;

const CORES_PAPEL = {
    dimensao: { bg: '#cfe2ff', text: '#084298' },
    medida: { bg: '#d1e7dd', text: '#0f5132' },
    tempo: { bg: '#fff3cd', text: '#664d03' },
    geo: { bg: '#e2d9f3', text: '#432874' }
};

export function inicializar(container, onCampoChange) {
    containerEl = container;
    onCampoChangeCallback = onCampoChange;
    renderizar();
}

export function atualizarCampos(tabelasNoCanvas, colunasMap) {
    containerEl.innerHTML = '';
    camposSelecionados = [];

    if (!tabelasNoCanvas || tabelasNoCanvas.length === 0) {
        containerEl.innerHTML = '<div class="text-muted small p-2">Adicione tabelas ao canvas</div>';
        return;
    }

    tabelasNoCanvas.forEach(function (tabela) {
        var colunas = colunasMap[tabela.idTabela] || [];
        if (colunas.length === 0) return;

        var header = document.createElement('div');
        header.className = 'fw-bold small text-muted border-bottom pb-1 mb-1 mt-2 px-1';
        header.textContent = tabela.rotulo || tabela.nomeTabela;
        if (tabela.tipo === 'base') {
            var badge = document.createElement('span');
            badge.className = 'badge bg-primary ms-1';
            badge.style.fontSize = '0.6rem';
            badge.textContent = 'base';
            header.appendChild(badge);
        }
        containerEl.appendChild(header);

        colunas.forEach(function (col) {
            var item = document.createElement('div');
            item.className = 'form-check py-0 px-1';

            var check = document.createElement('input');
            check.className = 'form-check-input';
            check.type = 'checkbox';
            check.id = 'campo_' + tabela.idTabela + '_' + col.nome_fisico;
            check.dataset.idTabela = tabela.idTabela;
            check.dataset.idColuna = col.id;
            check.dataset.nomeFisico = col.nome_fisico;
            check.dataset.papel = col.papel_sugerido || 'dimensao';

            var label = document.createElement('label');
            label.className = 'form-check-label small';
            label.setAttribute('for', check.id);

            var papelBadge = document.createElement('span');
            var papel = col.papel_sugerido || 'dimensao';
            var cores = CORES_PAPEL[papel] || CORES_PAPEL.dimensao;
            papelBadge.className = 'badge me-1';
            papelBadge.style.cssText = 'font-size:0.6rem; background-color:' + cores.bg + '; color:' + cores.text + ';';
            papelBadge.textContent = papel.charAt(0).toUpperCase();

            label.appendChild(papelBadge);
            label.appendChild(document.createTextNode(col.rotulo || col.nome_fisico));

            if (col.sensivel) {
                var sensivelIcon = document.createElement('i');
                sensivelIcon.className = 'bi bi-shield-lock text-warning ms-1';
                sensivelIcon.title = 'Dado sensivel';
                label.appendChild(sensivelIcon);
            }

            check.addEventListener('change', function () {
                if (check.checked) {
                    camposSelecionados.push({
                        idTabela: parseInt(check.dataset.idTabela),
                        idColuna: parseInt(check.dataset.idColuna),
                        nomeFisico: check.dataset.nomeFisico,
                        papel: check.dataset.papel,
                        rotulo: col.rotulo || col.nome_fisico,
                        agregacao: papel === 'medida' ? 'SUM' : 'NONE'
                    });
                } else {
                    camposSelecionados = camposSelecionados.filter(function (c) {
                        return !(c.idTabela === parseInt(check.dataset.idTabela) && c.nomeFisico === check.dataset.nomeFisico);
                    });
                }
                if (onCampoChangeCallback) onCampoChangeCallback(camposSelecionados);
            });

            item.appendChild(check);
            item.appendChild(label);
            containerEl.appendChild(item);
        });
    });
}

export function obterCamposSelecionados() {
    return camposSelecionados;
}

function renderizar() {
    containerEl.innerHTML = '<div class="text-muted small p-2">Adicione tabelas ao canvas</div>';
}
