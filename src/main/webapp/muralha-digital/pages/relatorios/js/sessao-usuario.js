
// Elemento do contador de registros
let recordCountElement = null;
let dadosModal = null;

document.addEventListener('DOMContentLoaded', function() {
    limparPesquisa();
    toastContainerElement = document.getElementById('toast-container');
    recordCountElement = document.getElementById('record-count');
    // Adiciona listeners aos botões de exportação
    document.getElementById('exportar-pdf').addEventListener('click', exportarParaPDF);
    document.getElementById('exportar-excel').addEventListener('click', exportarParaExcel);
    // document.getElementById('exportar-modal-pdf').addEventListener('click', exportarDetalhesParaPDF);
    // document.getElementById('exportar-modal-excel').addEventListener('click', exportarDetalhesParaExcel);
});

/**
 * Limpa os campos de pesquisa e os dados do relatório na tela.
 *
 * - Limpa os campos de data inicial e final.
 * - Zera o array de dados do relatório.
 * - Atualiza a tabela para mostrar mensagem "Nenhum dado gerado".
 * - Atualiza o contador de registros para zero.
 */
function limparPesquisa() {
    if (!recordCountElement) {
        recordCountElement = document.getElementById('record-count');
    }
    // console.log('Limpando pesquisa e tabela...');
    document.getElementById('data-inicial').value = '';
    document.getElementById('data-final').value = '';
    dadosDoRelatorio = [];
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum dado gerado.</td></tr>`;
    recordCountElement.textContent = '0 registros';
}

/**
 * Gera o relatório de sessões de usuário.
 *
 * - Obtém os valores dos campos de data inicial e final.
 * - Faz requisição ao servlet para buscar os dados.
 * - Atualiza o dropdown de usuários e a tabela.
 */
function gerarRelatorio() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    if (!dataInicial || !dataFinal) {
        return;
    }
    const servletURL = '/MuralhaDigital/Relatorios/SessaoUsuario';
    const params = new URLSearchParams({
        dataInicial: dataInicial + ' 00:00:00',
        dataFinal: dataFinal + ' 23:59:59'
    });
    fetch(`${servletURL}?${params}`)
        .then(response => response.json())
        .then(data => {
            dadosDoRelatorio = data;
            preencherDropUsuarios(data);
            atualizarTabela();
        })
        .catch(error => {
            console.error(error);
        });
}

/**
 * Preenche o dropdown de usuários com nomes únicos extraídos dos dados.
 *
 * @param {Array} data - Array de objetos de sessão de usuário.
 */
function preencherDropUsuarios(data) {
    const selectUsuario = document.getElementById('usuario');
    const nomesUnicos = [...new Set(data.map(item => item.nome))];
    selectUsuario.innerHTML = '<option value="">Todos</option>';
    nomesUnicos.forEach(nome => {
        if (nome && nome.trim() !== '') {
            const option = document.createElement('option');
            option.value = nome;
            option.textContent = nome;
            selectUsuario.appendChild(option);
        }
    });
    selectUsuario.onchange = atualizarTabela;
}

/**
 * Atualiza a tabela principal de sessões de usuário.
 *
 * - Filtra os dados pelo usuário selecionado.
 * - Calcula o total de registros.
 * - Renderiza cada linha da tabela com botão para navegação.
 */
function atualizarTabela() {
    const tableBody = document.getElementById('report-table-body');
    tableBody.innerHTML = '';
    const selectUsuario = document.getElementById('usuario');
    const filtro = selectUsuario.value;
    let dadosFiltrados = dadosDoRelatorio;
    if (filtro) {
        dadosFiltrados = dadosDoRelatorio.filter(item => item.nome === filtro);
    }
    const totalRegistros = dadosFiltrados ? dadosFiltrados.length : 0;
    if (!recordCountElement) {
        recordCountElement = document.getElementById('record-count');
    }
    if (recordCountElement) {
        recordCountElement.textContent = `${totalRegistros} registros`;
    }
    if (totalRegistros === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Nenhum dado gerado.</td></tr>`;
        return;
    }
    dadosFiltrados.forEach((item, idx) => {
        let dataHoraLogin  = item.data_hora_login  != null ? item.data_hora_login.substring(0, 19)  : '-';
        let dataHoraLogout = item.data_hora_logout != null ? item.data_hora_logout.substring(0, 19) : '-';
        let duracao        = item.duracao_sessao   != null ? item.duracao_sessao                    : '-';

        // Para o botão, calcula o timestamp real para navegação
        let timestampLogoutNavegacao = dataHoraLogout;
        if (dataHoraLogout === '-') {
            // Busca próxima sessão do mesmo usuário
            let proximaSessao = null;
            for (let j = idx + 1; j < dadosFiltrados.length; j++) {
                if (dadosFiltrados[j].nome === item.nome) {
                    proximaSessao = dadosFiltrados[j];
                    break;
                }
            }
            if (proximaSessao && proximaSessao.data_hora_login) {
                timestampLogoutNavegacao = proximaSessao.data_hora_login.substring(0, 19);
            }
        }

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.nome}</td>
            <td>${dataHoraLogin}</td>
            <td>${dataHoraLogout}</td>
            <td>${duracao}</td>
            <td style="text-align: right;">
                <button type="button" class="btn btn-sm btn-outline-primary" title="Mais informações" onclick="buscarNavegacaoUsuario('${item.id_usuario}', '${dataHoraLogin}', '${timestampLogoutNavegacao}')">
                    <i class="bi bi-plus"></i>
                </button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}
        // ...existing code...
// Busca os dados de navegação do usuário no intervalo da sessão
/**
 * Busca os dados de navegação do usuário no intervalo da sessão e exibe no modal.
 *
 * @param {string|number} IdUsuario - ID do usuário.
 * @param {string} dataLogin - Data/hora de login da sessão.
 * @param {string} dataLogout - Data/hora de logout da sessão.
 */
function buscarNavegacaoUsuario(IdUsuario, dataLogin, dataLogout) {
    // console.log(`Buscando navegação para usuário ${IdUsuario} entre ${dataLogin} e ${dataLogout}`);

    if (dataLogout == '-') {
        dataLogout = new Date().toISOString().substring(0, 19).replace('T', ' ');
    }
    
    const usuario = IdUsuario;
    const servletURL = '/MuralhaDigital/Relatorios/SessaoUsuario';
    const params = new URLSearchParams({
        acao: 'navegacao',
        usuario: usuario,
        dataInicial: dataLogin,
        dataFinal: dataLogout
    });
    // Exibe modal e mostra carregando
    const modalBody = document.getElementById('modalNavegacaoBody');
    modalBody.innerHTML = 'Carregando...';
    const modal = new bootstrap.Modal(document.getElementById('modalNavegacao'));
    modal.show();
    fetch(`${servletURL}?${params}`)
        .then(response => response.json())
        .then(data => {
            // dadosModal = data;
            // console.log('Dados de navegação recebidos:', data);
            // Buscar dados do usuário na tabela principal
            let usuarioInfo = null;
            if (window.dadosDoRelatorio && window.dadosDoRelatorio.length > 0) {
                usuarioInfo = window.dadosDoRelatorio.find(u => String(u.id_usuario) === String(IdUsuario));
            }
            let nome = usuarioInfo ? usuarioInfo.nome : '';
            let email = usuarioInfo ? usuarioInfo.email : '';
            let nomeUsuario = usuarioInfo ? usuarioInfo.login : '';
            let intervalo = `${dataLogin} até ${dataLogout}`;

            let headerHtml = `
                <div class="mb-2">
                    <strong>Nome:</strong> ${nome || '-'}<br>
                    <strong>Email:</strong> ${email}<br>
                    <strong>Login:</strong> ${nomeUsuario || '-'}<br>
                    <strong>Intervalo:</strong> ${intervalo}
                </div>
            `;

            let html = headerHtml;

            if (!data || data.length === 0) {
                dadosModal = null;

                html += '<div class="text-muted text-center">Nenhum dado de navegação encontrado.</div>';
                // Desabilita os botões de exportação do modal
                document.getElementById('exportar-modal-pdf').disabled = true;
                document.getElementById('exportar-modal-excel').disabled = true;
            } else {
                dadosModal = data;

                html += `<table class="table table-bordered table-sm"><thead><tr><th>Caminho</th><th>Data/Hora</th></tr></thead><tbody>`;

                document.getElementById('exportar-modal-pdf').disabled = false;
                document.getElementById('exportar-modal-excel').disabled = false;

                data.forEach(item => {
                    let dataFormatada = '-';
                    if (item.data) {
                        // Tenta normalizar para 'YYYY-MM-DD HH:mm:ss'
                        let d = item.data.replace('T', ' ').substring(0, 19);
                        // Se vier só data, tenta adicionar hora
                        if (/^\d{4}-\d{2}-\d{2}$/.test(d)) {
                            d += ' 00:00:00';
                        }
                        dataFormatada = d;
                    }
                    html += `<tr><td>${item.descricao || ''}</td><td>${dataFormatada}</td></tr>`;
                });
                html += '</tbody></table>';
            }
            modalBody.innerHTML = html;
        })
        .catch(error => {
            modalBody.innerHTML = '<div class="text-danger">Erro ao buscar dados de navegação.</div>';
            console.error(error);
        });
}

/**
 * Valida se os campos de data inicial e final estão preenchidos.
 *
 * - Exibe aviso se algum campo estiver vazio.
 * - Retorna true se ambos estiverem preenchidos.
 * @returns {boolean} True se filtros válidos, false caso contrário.
 */
function validarFiltros() {
    const dataInicial = document.getElementById('data-inicial').value;
    const dataFinal = document.getElementById('data-final').value;
    if (!dataInicial || !dataFinal) {
        showToast("Por favor, preencha as datas de Início e Fim.", 'warning');
        return false;
    }
    return true;
}

function exportarParaPDF() {
    // Filtra os dados conforme o usuário selecionado
    const selectUsuario = document.getElementById('usuario');
    const filtro = selectUsuario ? selectUsuario.value : '';
    let dadosFiltrados = dadosDoRelatorio;
    if (filtro) {
        dadosFiltrados = dadosDoRelatorio.filter(item => item.nome === filtro);
    }
    if (dadosFiltrados.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new window.jspdf.jsPDF();
    doc.autoTable({
        head: [['Usuário', 'Login', 'Logout', 'Duração']],
        body: dadosFiltrados.map(item => [
            item.nome             || item.usuario    || 'N/A',
            item.data_hora_login  || item.dataLogin  || '-',
            item.data_hora_logout || item.dataLogout || '-',
            item.duracao_sessao   || item.duracao    || '-'
        ]),
    });
    doc.save('relatorio_auditoria.pdf');
}

// Exporta os dados da tabela para Excel (XLSX)
function exportarParaExcel() {
    // Filtra os dados conforme o usuário selecionado
    const selectUsuario = document.getElementById('usuario');
    const filtro = selectUsuario ? selectUsuario.value : '';
    let dadosFiltrados = dadosDoRelatorio;
    if (filtro) {
        dadosFiltrados = dadosDoRelatorio.filter(item => item.nome === filtro);
    }
    if (dadosFiltrados.length === 0) {
        showToast("Nenhum dado para exportar. Gere um relatório primeiro.", 'warning');
        return;
    }
    const dadosParaPlanilha = dadosFiltrados.map(item => ({
        'Usuário': item.nome             || item.usuario    || 'N/A',
        'Login':   item.data_hora_login  || item.dataLogin  || '-',
        'Logout':  item.data_hora_logout || item.dataLogout || '-',
        'Duração': item.duracao_sessao   || item.duracao    || '-'
    }));
    const worksheet = XLSX.utils.json_to_sheet(dadosParaPlanilha);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Auditoria");
    XLSX.writeFile(workbook, "relatorio_auditoria.xlsx");
}

function exportarDetalhesParaPDF() {
    // Exporta os dados presentes no modal (window.dadosNavegacaoModal)
    // const dadosModal = window.dadosNavegacaoModal || [];
    if (!dadosModal.length) {
        showToast("Nenhum dado para exportar no modal.", 'warning');
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new window.jspdf.jsPDF();
    doc.autoTable({
        head: [['Caminho', 'Data/Hora']],
        body: dadosModal.map(item => [
            item.descricao || '-',
            item.data ? item.data.replace('T', ' ').substring(0, 19) : '-'
        ]),
    });
    doc.save('navegacao_usuario.pdf');
}

// Exporta os dados da tabela para Excel (XLSX)
function exportarDetalhesParaExcel() {
    // Exporta os dados presentes no modal (window.dadosNavegacaoModal)
    // console.log(window);
    // const dadosModal = window.dadosNavegacaoModal || [];
    if (!dadosModal.length) {
        showToast("Nenhum dado para exportar no modal.", 'warning');
        return;
    }
    const dadosParaPlanilha = dadosModal.map(item => ({
        'Caminho': item.descricao || '-',
        'Data/Hora': item.data ? item.data.replace('T', ' ').substring(0, 19) : '-'
    }));
    const worksheet = XLSX.utils.json_to_sheet(dadosParaPlanilha);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Navegacao");
    XLSX.writeFile(workbook, "navegacao_usuario.xlsx");
}

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