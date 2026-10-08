// Variáveis globais
let dadosDoRelatorio = [];
let toastContainerElement;
let recordCountElement;

document.addEventListener('DOMContentLoaded', function() {
    toastContainerElement = document.getElementById('toast-container');
    recordCountElement = document.getElementById('record-count');

    document.getElementById('gerar-relatorio').addEventListener('click', fetchReportData);
    document.getElementById('limpar-pesquisa').addEventListener('click', limparPesquisa);
    document.getElementById('exportar-pdf').addEventListener('click', exportarParaPDF);
    document.getElementById('exportar-excel').addEventListener('click', exportarParaExcel);
    definirDatasPadrao();
    fetchOperadores();
});

/**
 * Define as datas padrão nos filtros: hoje e 30 dias atrás.
 */
function definirDatasPadrao() {
    const dataFinalInput = document.getElementById('data-final');
    const dataInicialInput = document.getElementById('data-inicial');

    const hoje = new Date();
    const trintaDiasAtras = new Date();
    trintaDiasAtras.setDate(hoje.getDate() - 30);

    // Formata a data para o formato YYYY-MM-DD que o input[type=date] aceita
    const formatarData = (data) => {
        const ano = data.getFullYear();
        const mes = String(data.getMonth() + 1).padStart(2, '0'); // Mês é base 0
        const dia = String(data.getDate()).padStart(2, '0');
        return `${ano}-${mes}-${dia}`;
    };

    dataFinalInput.value = formatarData(hoje);
    dataInicialInput.value = formatarData(trintaDiasAtras);
}


/**
 * Limpa os filtros e a tabela de resultados.
 */
function limparPesquisa() {
    document.getElementById('operador').value = '';
    document.getElementById('placa').value = '';
    
    // Ao limpar, redefine para as datas padrão em vez de deixar em branco
    definirDatasPadrao();

    dadosDoRelatorio = [];
    populateTable(dadosDoRelatorio); 
}

/**
 * Valida se os filtros obrigatórios foram preenchidos.
 */
function validarFiltros() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;

    if (!dataInicial || !dataFinal) {
        showToast("As datas de início e fim são obrigatórias.", 'warning');
        return false;
    }
    return true;
}

/**
 * Busca a lista de operadores no servidor.
 */
function fetchOperadores() {
    fetch(`${servletURL}?acao=getOperadores`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao carregar operadores.');
            return response.json();
        })
        .then(data => {
            const selectOperador = document.getElementById('operador');
            selectOperador.innerHTML = '<option value="">Todos</option>';
            data.forEach(operador => {
                const option = document.createElement('option');
                option.value = operador.id_usuario;
                option.textContent = operador.nome;
                selectOperador.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Erro ao buscar operadores:', error);
            document.getElementById('operador').innerHTML = '<option value="">Erro ao carregar</option>';
            showToast(error.message, 'danger');
        });
}

/**
 * Busca os dados do relatório no servidor com base nos filtros.
 */
function fetchReportData() {
    if (!validarFiltros()) {
        return;
    }

    const operador = document.getElementById('operador').value;
    const placa = document.getElementById('placa').value;
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    
    const params = new URLSearchParams({
        acao: 'getRelatorio',
        operador: operador,
        placa: placa,
        dataInicial: dataInicial,
        dataFinal: dataFinal
    });

    fetch(`${servletURL}?${params}`)
        .then(response => {
            if (!response.ok) throw new Error('A resposta do servidor não foi OK.');
            return response.json();
        })
        .then(data => {
            if (data.erro) {
                throw new Error(data.erro);
            }
            dadosDoRelatorio = data;
            populateTable(data);
        })
        .catch(error => {
            console.error('Erro ao buscar dados do relatório:', error);
            showToast(`Falha ao carregar relatório: ${error.message}`, 'danger');
            dadosDoRelatorio = [];
            populateTable(dadosDoRelatorio);
        });
}

/**
 * Preenche a tabela com os dados.
 */
function populateTable(data) {
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = ''; 

    const totalRegistros = data ? data.length : 0;
    recordCountElement.textContent = `${totalRegistros} registro(s)`;

    if (totalRegistros === 0) {
        tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Nenhum resultado encontrado.</td></tr>`;
        return;
    }

    data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.nomeOperador || ''}</td>
            <td>${item.dataHoraPesquisa || ''}</td>
            <td>${item.placaPesquisada || ''}</td>
            <td>${item.motivo || ''}</td>
        `;
        tableBody.appendChild(row);
    });
}

/**
 * Exporta para PDF.
 */
function exportarParaPDF() {
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar.", 'warning');
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    doc.autoTable({
        head: [['Operador', 'Data/Hora da Pesquisa', 'Placa Pesquisada', 'Motivo']],
        body: dadosDoRelatorio.map(item => [
            item.nomeOperador,
            item.dataHoraPesquisa,
            item.placaPesquisada,
            item.motivo
        ]),
    });
    doc.save('relatorio_pesquisas.pdf');
}

/**
 * Exporta para Excel.
 */
function exportarParaExcel() {
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar.", 'warning');
        return;
    }
    const dadosParaPlanilha = dadosDoRelatorio.map(item => ({
        'Operador': item.nomeOperador,
        'Data/Hora da Pesquisa': item.dataHoraPesquisa,
        'Placa Pesquisada': item.placaPesquisada,
        'Motivo': item.motivo
    }));
    const worksheet = XLSX.utils.json_to_sheet(dadosParaPlanilha);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Relatório");
    XLSX.writeFile(workbook, "relatorio_pesquisas.xlsx");
}

/**
 * Exibe uma notificação (toast).
 */
function showToast(message, type = 'info') {
    if (!toastContainerElement) return;
    const toastId = 'toast-' + Math.random().toString(36).substring(2, 9);
    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>`;
    toastContainerElement.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
    toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    toast.show();
}