// Variáveis globais
let dadosDoRelatorio = [];
let toastContainerElement;
let recordCountElement;

// Evento disparado quando o HTML da página foi completamente carregado
document.addEventListener('DOMContentLoaded', function() {
    toastContainerElement = document.getElementById('toast-container');
    recordCountElement = document.getElementById('record-count');
    
    // --- NOVO CÓDIGO PARA DEFINIR AS DATAS ---
    const dataFinalInput = document.getElementById('data-final');
    const dataInicialInput = document.getElementById('data-inicial');

    const hoje = new Date();
    const umMesAtras = new Date();
    umMesAtras.setMonth(hoje.getMonth() - 1);

    // Formata a data para o padrão YYYY-MM-DD, que é o valor esperado pelo input type="date"
    dataFinalInput.value = hoje.toISOString().slice(0, 10);
    dataInicialInput.value = umMesAtras.toISOString().slice(0, 10);
    // --- FIM DO NOVO CÓDIGO ---

    // Busca os usuários para preencher o filtro
    fetchUsuarios(); 
    
    // Adiciona os listeners (eventos) aos botões
    document.getElementById('gerar-relatorio').addEventListener('click', fetchReportData);
    document.getElementById('limpar-pesquisa').addEventListener('click', limparPesquisa);
    document.getElementById('exportar-pdf').addEventListener('click', exportarParaPDF);
    document.getElementById('exportar-excel').addEventListener('click', exportarParaExcel);
});

// Limpa os filtros e a tabela de resultados
function limparPesquisa() {
    document.getElementById('usuario').value = '';
    document.getElementById('data-inicial').value = '';
    document.getElementById('data-final').value = '';
    dadosDoRelatorio = [];
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum dado gerado.</td></tr>`;
    recordCountElement.textContent = '0 registros';
}

// Exibe uma notificação flutuante (toast)
function showToast(message, type = 'info') {
    if (!toastContainerElement) return;
    const toastId = 'toast-' + Date.now();
    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>`;
    toastContainerElement.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    toast.show();
}

// Valida se as datas foram preenchidas
function validarFiltros() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    if (!dataInicial || !dataFinal) {
        showToast("Por favor, preencha as datas de Início e Fim.", 'warning');
        return false;
    }
    return true;
}

// Busca a lista de usuários no servidor
function fetchUsuarios() {
    fetch(`${servletURL}?acao=getUsuarios`)
        .then(response => response.json())
        .then(data => {
            const selectUsuario = document.getElementById('usuario');
            selectUsuario.innerHTML = '<option value="">Todos</option>'; // Opção para buscar todos
            data.forEach(user => {
                const option = document.createElement('option');
                option.value = user.id_usuario;
                option.textContent = user.nome;
                selectUsuario.appendChild(option);
            });
        })
        .catch(error => console.error('Erro ao buscar usuários:', error));
}

// Busca os dados do relatório com base nos filtros
function fetchReportData() {
    if (!validarFiltros()) return;
    const usuario = document.getElementById('usuario').value;
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    const params = new URLSearchParams({
        acao: 'getRelatorio',
        usuario: usuario,
        dataInicial: dataInicial,
        dataFinal: dataFinal
    });

    fetch(`${servletURL}?${params}`)
        .then(response => response.json())
        .then(data => {
            dadosDoRelatorio = data; // Armazena os dados para exportação
            populateTable(data);
        })
        .catch(error => console.error('Erro ao buscar dados do relatório:', error));
}

// Preenche a tabela com os dados recebidos
function populateTable(data) {
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = ''; 

    const totalRegistros = data ? data.length : 0;
    recordCountElement.textContent = `${totalRegistros} registros`;

    if (totalRegistros === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum resultado encontrado.</td></tr>`;
        return;
    }

    data.forEach(item => {
        const row = document.createElement('tr');
        const escape = (str) => String(str).replace(/[&<>"']/g, s => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'})[s]);
        
        row.innerHTML = `
            <td>${escape(item.usuario || 'N/A')}</td>
            <td>${escape(item.tipoAcao || '')}</td>
            <td>${escape(item.modulo || '')}</td> 
            <td>${escape(item.detalheAcao || '')}</td>
            <td>${escape(item.dataHora || '')}</td>`;
        tableBody.appendChild(row);
    });
}

// Exporta os dados da tabela para PDF
function exportarParaPDF() {
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    doc.autoTable({
        head: [['Usuário', 'Tipo da Ação', 'Módulo', 'Detalhe', 'Data e Hora']],
        body: dadosDoRelatorio.map(item => [
            item.usuario || 'N/A',
            item.tipoAcao,
            item.modulo,
            item.detalheAcao,
            item.dataHora
        ]),
    });
    doc.save('relatorio_auditoria.pdf');
}

// Exporta os dados da tabela para Excel (XLSX)
function exportarParaExcel() {
    if (dadosDoRelatorio.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const dadosParaPlanilha = dadosDoRelatorio.map(item => ({
        'Usuário': item.usuario || 'N/A',
        'Tipo da Ação': item.tipoAcao,
        'Módulo': item.modulo,
        'Detalhe': item.detalheAcao,
        'Data e Hora': item.dataHora
    }));
    const worksheet = XLSX.utils.json_to_sheet(dadosParaPlanilha);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Auditoria");
    XLSX.writeFile(workbook, "relatorio_auditoria.xlsx");
}