// Variáveis globais
let dadosDoRelatorio = [];
let toastContainerElement;
let recordCountElement;

document.addEventListener('DOMContentLoaded', function() {
    toastContainerElement = document.getElementById('toast-container');
    recordCountElement = document.getElementById('record-count'); 
    
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
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum dado gerado.</td></tr>`;
    
    // Reseta a contagem de registros
    recordCountElement.textContent = '0 registros';
}

function showToast(message, type = 'info') {
    if (!toastContainerElement) return;
    const toastId = 'toast-' + Math.random().toString(36).substring(2, 9);
    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>`;
    toastContainerElement.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    toast.show();
}

function validarFiltros() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    if (!dataInicial) {
        showToast("Por favor, preencha a Data de Início.", 'warning');
        return false;
    }
    if (!dataFinal) {
        showToast("Por favor, preencha a Data de Fim.", 'warning');
        return false;
    }
    // Validar se data inicial é menor ou igual à data final
    if (dataInicial > dataFinal) {
        showToast("A Data de Início não pode ser posterior à Data de Fim.", 'warning');
        return false;
    }
    return true;
}

function fetchOperadores() {
    const selectOperador = document.getElementById('operador');
    selectOperador.innerHTML = '<option value="">Carregando...</option>';
    
    fetch(`${servletURL}?acao=getOperadores`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro HTTP ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Operadores carregados:', data);
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
            selectOperador.innerHTML = '<option value="">Erro ao carregar operadores</option>';
            showToast('Erro ao carregar lista de operadores: ' + error.message, 'danger');
        });
}

function fetchReportData() {
    if (!validarFiltros()) return;
    
    const operador = document.getElementById('operador').value;
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    
    const btnGerar = document.getElementById('gerar-relatorio');
    const btnTextoOriginal = btnGerar.innerHTML;
    
    // Desabilita o botão e mostra loading
    btnGerar.disabled = true;
    btnGerar.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Consultando...';
    
    const params = new URLSearchParams({
        acao: 'getRelatorio',
        operador: operador,
        dataInicial: dataInicial,
        dataFinal: dataFinal
    });
    
    console.log('Fazendo requisição para:', servletURL + '?' + params.toString());
    
    fetch(`${servletURL}?${params}`)
        .then(response => {
            console.log('Status da resposta:', response.status);
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`Erro HTTP ${response.status}: ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Dados recebidos:', data);
            dadosDoRelatorio = data;
            populateTable(data);
            showToast('Consulta realizada com sucesso!', 'success');
        })
        .catch(error => {
            console.error('Erro ao buscar dados do relatório:', error);
            showToast('Erro ao buscar dados: ' + error.message, 'danger');
            // Limpa a tabela em caso de erro
            const tableBody = document.getElementById('report-table-body');
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar dados. Veja o console para mais detalhes.</td></tr>`;
            recordCountElement.textContent = '0 registros';
        })
        .finally(() => {
            // Restaura o botão
            btnGerar.disabled = false;
            btnGerar.innerHTML = btnTextoOriginal;
        });
}

function populateTable(data) {
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = ''; 

    // ===============================================
    //          ATUALIZA A CONTAGEM DE REGISTROS
    // ===============================================
    const totalRegistros = data ? data.length : 0;
    recordCountElement.textContent = `${totalRegistros} registros`;

    if (totalRegistros === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum resultado encontrado.</td></tr>`;
        return;
    }

    data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.nomeOperador || 'N/A'}</td>
            <td>${item.dataHoraExportacao || ''}</td> <td>${item.placa || ''}</td> 
            <td>${item.dataHoraPassagem || ''}</td>
            <td>${item.pontoCaptura || ''}</td> `;
        tableBody.appendChild(row);
    });
}

function exportarParaPDF() {
    if (!validarFiltros()) return;
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    doc.autoTable({
        head: [['Operador', 'Data/Hora da Exportação', 'Placa', 'Data/Hora da Passagem', 'Ponto de Captura']], // MODIFICADO
        body: dadosDoRelatorio.map(item => [
            item.nomeOperador || 'N/A',
            item.dataHoraExportacao, // MODIFICADO
            item.placa,
            item.dataHoraPassagem,
            item.pontoCaptura
        ]),
    });
    doc.save('relatorio_imagens.pdf');
}

function exportarParaExcel() {
    if (!validarFiltros()) return;
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const dadosParaPlanilha = dadosDoRelatorio.map(item => ({
        'Operador': item.nomeOperador || 'N/A',
        'Data/Hora da Exportação': item.dataHoraExportacao, // MODIFICADO
        'Placa': item.placa,
        'Data/Hora Passagem': item.dataHoraPassagem,
        'Ponto de Captura': item.pontoCaptura
    }));
    const worksheet = XLSX.utils.json_to_sheet(dadosParaPlanilha);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Relatório");
    XLSX.writeFile(workbook, "relatorio_imagens.xlsx");
}