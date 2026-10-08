let dadosDoRelatorio = [], toastContainerElement, recordCountElement, fotoModal;

// Imagem provisória embutida para não depender de internet
const placeholderImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTUwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2VlZSIvPjx0ZXh0IHg9Ijc1IiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjYWFhIj5TZW0gRm90bzwvdGV4dD48L3N2Zz4=';


document.addEventListener('DOMContentLoaded', function() {
    toastContainerElement = document.getElementById('toast-container');
    recordCountElement = document.getElementById('record-count');
    fotoModal = new bootstrap.Modal(document.getElementById('fotoModal'));

    const dataFinalInput = document.getElementById('data-final');
    const dataInicialInput = document.getElementById('data-inicial');
    const hoje = new Date(), umMesAtras = new Date();
    umMesAtras.setMonth(hoje.getMonth() - 1);
    dataFinalInput.value = hoje.toISOString().slice(0, 10);
    dataInicialInput.value = umMesAtras.toISOString().slice(0, 10);

    fetchOperadores(); 

    document.getElementById('gerar-relatorio').addEventListener('click', fetchReportData);
    document.getElementById('limpar-pesquisa').addEventListener('click', limparPesquisa);
    document.getElementById('exportar-pdf').addEventListener('click', exportarParaPDF);
    document.getElementById('exportar-excel').addEventListener('click', exportarParaExcel);
});

function limparPesquisa() {
    document.getElementById('operador').value = '';
    document.getElementById('data-inicial').value = '';
    document.getElementById('data-final').value = '';
    dadosDoRelatorio = [];
    document.getElementById('report-table-body').innerHTML = `<tr><td colspan="3" class="text-center text-muted">Nenhum dado gerado.</td></tr>`;
    recordCountElement.textContent = '0 registros';
}

function showToast(message, type = 'info') {
    const toastId = 'toast-' + Date.now();
    const toastHTML = `<div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert"><div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div></div>`;
    toastContainerElement.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    toast.show();
}

function validarFiltros() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    if (!dataInicial || !dataFinal) {
        showToast("Por favor, preencha as datas de Início e Fim.", 'warning');
        return false;
    }
    return true;
}

function fetchOperadores() {
    fetch(`${servletURL}?acao=getOperadores`)
        .then(response => response.json())
        .then(data => {
            const selectOperador = document.getElementById('operador');
            selectOperador.innerHTML = '<option value="">Todos</option>';
            data.forEach(op => {
                const option = document.createElement('option');
                option.value = op.id_usuario;
                option.textContent = op.nome;
                selectOperador.appendChild(option);
            });
        })
        .catch(error => console.error('Erro ao buscar operadores:', error));
}

function fetchReportData() {
    if (!validarFiltros()) return;
    const operador = document.getElementById('operador').value;
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    const params = new URLSearchParams({
        acao: 'getRelatorio',
        operador: operador,
        dataInicial: dataInicial,
        dataFinal: dataFinal
    });

    fetch(`${servletURL}?${params}`)
        .then(response => response.json())
        .then(data => {
            dadosDoRelatorio = data.erro ? [] : data;
            if (data.erro) showToast(data.erro, 'danger');
            populateTable(dadosDoRelatorio);
        })
        .catch(error => showToast('Erro ao contatar o servidor.', 'danger'));
}

function populateTable(data) {
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = ''; 
    recordCountElement.textContent = `${data.length} registros`;

    if (data.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">Nenhum resultado encontrado.</td></tr>`;
        return;
    }

    data.forEach(item => {
        const row = document.createElement('tr');
        
        const fotoSrc = item.fotoBase64 ? `data:${item.fotoMimeType};base64,${item.fotoBase64}` : placeholderImage;
        const escape = (str) => String(str || '').replace(/[&<>"']/g, s => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'})[s]);
        
        const motivoDescarteHtml = item.motivoDescarte ? `<strong>Motivo Descarte:</strong> ${escape(item.motivoDescarte)}<br>` : '';
        
        // --- LÓGICA ATUALIZADA PARA OS NOVOS CAMPOS DO FATO ---
        const boletimSituacao = item.temBoletimFato ? 'Com boletim' : 'Sem boletim';
        const fatoDescricaoHtml = item.descricaoDoFato ? `<small><strong>Desc. Fato:</strong> ${escape(item.descricaoDoFato)}</small><br>` : '';

        row.innerHTML = `
            <td class="align-middle">
                <img src="${fotoSrc}" alt="Foto" class="passagem-foto" onclick="abrirModalFoto('${fotoSrc}')"><br>
                <strong>Placa:</strong> ${escape(item.placa) || 'N/A'}<br>
                <strong>Data Alerta:</strong> <small>${escape(item.dataAlerta)}</small>
            </td>
            <td class="align-middle">
                <strong>Tipo Registro de Fato:</strong> ${escape(item.tipoOcorrenciaFato) || 'N/A'}<br>
                <strong>R. de Fato Situação:</strong> ${escape(item.situacaoAtualFato) || 'N/A'}<br>
                <strong>Boletim Situação:</strong> ${escape(boletimSituacao)}<br>
                ${motivoDescarteHtml}
                ${fatoDescricaoHtml}
            </td>
            <td class="align-middle">
                <strong>Operador:</strong> ${escape(item.operadorAcao) || 'N/A'}<br>
                <strong>Data/Hora:</strong> ${escape(item.dataAcao)}<br>
                <strong>Descrição da Ação:</strong> <small>${escape(item.descricaoAcao) || 'Nenhuma.'}</small>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function abrirModalFoto(src) {
    document.getElementById('imagemAmpliada').src = src;
    fotoModal.show();
}

function exportarParaExcel() {
    if (dadosDoRelatorio.length === 0) return showToast("Nenhum dado para exportar.", 'warning');
    const data = dadosDoRelatorio.map(item => ({
        'Placa': item.placa,
        'Data Alerta': item.dataAlerta,
        'Tipo Registro de Fato': item.tipoOcorrenciaFato,
        'R. de Fato Situação': item.situacaoAtualFato,
        'Boletim Situação': item.temBoletimFato ? 'Com boletim' : 'Sem boletim',
        'Descrição do Fato': item.descricaoDoFato,
        'Motivo Descarte': item.motivoDescarte,
        'Operador da Acao': item.operadorAcao,
        'Data da Acao': item.dataAcao,
        'Descricao da Acao': item.descricaoAcao
    }));
    const worksheet = XLSX.utils.json_to_sheet(data);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "AcoesConsolidadas");
    XLSX.writeFile(workbook, "relatorio_acoes_consolidadas.xlsx");
}

function exportarParaPDF() {
    if (dadosDoRelatorio.length === 0) return showToast("Nenhum dado para exportar.", 'warning');
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF({ orientation: 'landscape' });
    const body = dadosDoRelatorio.map(item => {
        const boletimSituacao = item.temBoletimFato ? 'Com boletim' : 'Sem boletim';
        return [
            `${item.placa || ''}\n${item.dataAlerta || ''}`,
            `${item.tipoOcorrenciaFato || 'N/A'}\n${item.situacaoAtualFato || 'N/A'}\n${boletimSituacao}`,
            `${item.operadorAcao || 'N/A'}\n${item.dataAcao || ''}`,
            `${item.descricaoAcao || 'Nenhuma.'}\n${item.descricaoDoFato || ''}`
        ];
    });
    doc.autoTable({ 
        head: [['Passagem', 'Fato Registrado', 'Ação Tomada', 'Descrição']], 
        body: body,
        styles: { cellPadding: 2, fontSize: 8 }
    });
    doc.save('relatorio_acoes_consolidadas.pdf');
}