/**
 * Sistema de Gerenciamento do Relatório de Placas Veiculares
 */
class RelatorioPlacasVeiculares {
    constructor() {
        this.tabelaId = 'tabela-relatorio-placas';
        this.tamanhoPagina = 50;
        this.paginaAtual = 1;
        this.totalPaginas = 1;
        this.dados = [];

        this.badgeTotal = document.getElementById('totalRegistros');
        this.infoPaginacao = document.getElementById('infoPaginacao');
        this.indicadorPagina = document.getElementById('indicadorPagina');
        this.botaoAnterior = document.getElementById('btnPaginaAnterior');
        this.botaoProximo = document.getElementById('btnPaginaProxima');
        this.cabecalhoTabela = document.getElementById('resultados-thead');
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.loadingMessage = document.getElementById('loadingMessage');

        // Configuração padrão da tabela
        this.cabecalhosPadrao = ['Placa Original', 'Placa Corrigida', 'Usuário', 'Data da Correção'];

        this.initializeDates();
        this.bindEvents();
        this.atualizarDisponibilidadeExportacao();
        this.renderizarCabecalho();
        this.renderizarTabela();
    }

    /**
     * Inicializa o sistema de datas automático
     */
    initializeDates() {
        const dataInicio = document.getElementById('dataInicio');
        const dataFinal = document.getElementById('dataFinal');
        
        if (dataInicio && dataFinal) {
            // Define data inicial padrão como 30 dias atrás
            const hoje = new Date();
            const trintaDiasAtras = new Date(hoje.getTime() - (30 * 24 * 60 * 60 * 1000));
            
            dataInicio.value = this.formatDate(trintaDiasAtras);
            dataFinal.value = this.formatDate(hoje);
        }
    }

    /**
     * Vincula eventos aos elementos da página
     */
    bindEvents() {
        const dataInicio = document.getElementById('dataInicio');
        
        if (dataInicio) {
            dataInicio.addEventListener('change', () => this.ajustarPeriodo());
        }

        this.bindPaginationControls();
    }

    /**
     * Registra eventos dos controles de paginação.
     */
    bindPaginationControls() {
        if (this.botaoAnterior) {
            this.botaoAnterior.addEventListener('click', () => this.irParaPagina(this.paginaAtual - 1));
        }

        if (this.botaoProximo) {
            this.botaoProximo.addEventListener('click', () => this.irParaPagina(this.paginaAtual + 1));
        }
    }

    /**
     * Ajusta automaticamente a data final baseada na data inicial
     */
    ajustarPeriodo() {
        const dataInicio = document.getElementById('dataInicio');
        const dataFinal = document.getElementById('dataFinal');
        
        if (!dataInicio.value || !dataFinal) return;

        const dataInicioObj = new Date(dataInicio.value);
        const dataFinalObj = dataFinal.value ? new Date(dataFinal.value) : null;
        
        // Se não há data final ou se a data final é anterior à inicial
        if (!dataFinalObj || dataFinalObj < dataInicioObj) {
            // Define data final como 30 dias após a inicial (máximo permitido)
            const novaDataFinal = new Date(dataInicioObj.getTime() + (30 * 24 * 60 * 60 * 1000));
            const hoje = new Date();
            
            // Não pode ser maior que hoje
            dataFinal.value = this.formatDate(novaDataFinal > hoje ? hoje : novaDataFinal);
        }
        
        this.validarPeriodo();
    }

    /**
     * Valida o período selecionado
     */
    validarPeriodo() {
        const dataInicio = document.getElementById('dataInicio');
        const dataFinal = document.getElementById('dataFinal');
        
        if (!dataInicio.value || !dataFinal.value) return;

        const dataInicioObj = new Date(dataInicio.value);
        const dataFinalObj = new Date(dataFinal.value);
        const hoje = new Date();
        
        // Validações
        if (dataInicioObj > hoje) {
            this.showMessage('Data inicial não pode ser maior que a data atual.', 'error');
            return false;
        }
        
        if (dataFinalObj > hoje) {
            this.showMessage('Data final não pode ser maior que a data atual.', 'error');
            return false;
        }
        
        if (dataInicioObj > dataFinalObj) {
            this.showMessage('Data inicial não pode ser maior que a data final.', 'error');
            return false;
        }
        
        // Verifica intervalo máximo de 365 dias
        const diffTime = Math.abs(dataFinalObj - dataInicioObj);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays > 365) {
            this.showMessage('O período não pode ser superior a 365 dias.', 'warning');
            return false;
        }
        
        this.hideMessage();
        return true;
    }

    /**
     * Formata data para o formato YYYY-MM-DD
     * @param {Date} date - Data a ser formatada
     * @returns {string} Data formatada
     */
    formatDate(date) {
        if (!date) return '';
        return date.getFullYear() + '-' + 
               String(date.getMonth() + 1).padStart(2, '0') + '-' + 
               String(date.getDate()).padStart(2, '0');
    }

    /**
     * Formata data/timestamp para o formato brasileiro dd/MM/yyyy HH:mm:ss
     * @param {string} dataString - String da data/timestamp a ser formatada
     * @returns {string} Data formatada no padrão PT-BR
     */
    formatarDataPtBr(dataString) {
        if (!dataString) {
            return '';
        }

        try {
            // Tenta parsear a data
            const data = new Date(dataString);
            
            // Verifica se a data é válida
            if (isNaN(data.getTime())) {
                return dataString; // Retorna original se não conseguir parsear
            }

            // Formata no padrão brasileiro dd/MM/yyyy HH:mm:ss
            const dia = String(data.getDate()).padStart(2, '0');
            const mes = String(data.getMonth() + 1).padStart(2, '0');
            const ano = data.getFullYear();
            const hora = String(data.getHours()).padStart(2, '0');
            const minuto = String(data.getMinutes()).padStart(2, '0');
            const segundo = String(data.getSeconds()).padStart(2, '0');

            return `${dia}/${mes}/${ano} ${hora}:${minuto}:${segundo}`;
        } catch (error) {
            // Em caso de erro, retorna a string original
            return dataString;
        }
    }

    /**
     * Sanitiza a entrada de placas removendo caracteres não alfanuméricos e normalizando para caixa alta.
     * @param {string} textoPlacas - Valor informado no campo de placas.
     * @returns {string} Lista de placas válidas separadas por vírgula contendo apenas letras e números.
     */
    sanitizarPlacas(textoPlacas) {
        if (!textoPlacas) {
            return '';
        }

        const partes = textoPlacas.split(',');
        const tratadas = new Set();

        partes.forEach(parte => {
            if (!parte) {
                return;
            }

            const placaLimpa = parte
                .trim()
                .toUpperCase()
                .replace(/[^A-Z0-9]/g, '');

            if (placaLimpa) {
                tratadas.add(placaLimpa);
            }
        });

        return Array.from(tratadas).join(',');
    }

    /**
     * Formata a lista de placas sanitizadas para exibição amigável no campo de entrada.
     * @param {string} placasSanitizadas - Lista de placas separadas por vírgula.
     * @returns {string} Texto formatado com placas separadas por vírgula e espaço.
     */
    formatarPlacasParaExibicao(placasSanitizadas) {
        if (!placasSanitizadas) {
            return '';
        }

        return placasSanitizadas
            .split(',')
            .filter(placa => placa && placa.trim())
            .join(', ');
    }

    /**
     * Exibe mensagem para o usuário
     * @param {string} message - Mensagem a ser exibida
     * @param {string} type - Tipo da mensagem (error, warning, loading)
     */
    showMessage(message, type = 'error') {
        const container = document.getElementById('message-container');
        if (!container) return;

        container.innerHTML = type === 'loading' 
            ? `<div class="spinner-border spinner-border-sm me-2"></div>${message}`
            : message;
        
        container.className = `message-container ${type}`;
        container.classList.remove('hidden');
    }

    /**
     * Oculta mensagens
     */
    hideMessage() {
        const container = document.getElementById('message-container');
        if (container) {
            container.classList.add('hidden');
        }
    }

    /**
     * Exibe o overlay de loading com mensagem personalizada
     * @param {string} message - Mensagem do loading
     */
    showLoading(message = 'Processando consulta...') {
        if (this.loadingOverlay && this.loadingMessage) {
            this.loadingMessage.textContent = message;
            this.loadingOverlay.classList.remove('hidden');
        }
    }

    /**
     * Oculta o overlay de loading
     */
    hideLoading() {
        if (this.loadingOverlay) {
            this.loadingOverlay.classList.add('hidden');
        }
    }

    /**
     * Exibe os resultados na tabela
     * @param {Array} dados - Dados a serem exibidos
     */
    exibirResultados(dados) {
        this.atualizarDados(Array.isArray(dados) ? dados : []);
        this.hideMessage();
    }

    /**
     * Atualiza a coleção de dados exibidos e reinicia a paginação.
     * @param {Array} dados - Lista de registros retornados pelo backend.
     */
    atualizarDados(dados) {
        this.dados = Array.isArray(dados) ? dados : [];
        
        // Ajusta tamanho da página se "Tudo" estiver selecionado
        const seletor = document.getElementById('tamanhoPagina');
        if (seletor && parseInt(seletor.value) === -1) {
            this.tamanhoPagina = Math.max(1, this.dados.length);
            this.totalPaginas = 1;
        } else {
            this.totalPaginas = Math.max(1, Math.ceil(this.dados.length / this.tamanhoPagina));
        }
        
        this.paginaAtual = 1;
        this.renderizarCabecalho();
        this.renderizarTabela();
    }

    /**
     * Atualiza o cabeçalho da tabela com os cabeçalhos padrão.
     */
    renderizarCabecalho() {
        if (!this.cabecalhoTabela) return;

        this.cabecalhoTabela.innerHTML = '';
        const linha = document.createElement('tr');
        
        this.cabecalhosPadrao.forEach(titulo => {
            const th = document.createElement('th');
            th.textContent = titulo;
            linha.appendChild(th);
        });
        
        this.cabecalhoTabela.appendChild(linha);
    }

    /**
     * Renderiza a tabela de acordo com a página atual.
     */
    renderizarTabela() {
        const tbody = document.getElementById('resultados-tbody');
        if (!tbody) {
            return;
        }

        tbody.innerHTML = '';

        const totalRegistros = this.dados.length;
        const inicio = (this.paginaAtual - 1) * this.tamanhoPagina;
        const fim = Math.min(inicio + this.tamanhoPagina, totalRegistros);
        const dadosPagina = this.dados.slice(inicio, fim);

        if (dadosPagina.length === 0) {
            const linha = document.createElement('tr');
            linha.innerHTML = `<td colspan="4" class="text-center text-muted py-4">Nenhum registro disponível.</td>`;
            tbody.appendChild(linha);
        } else {
            dadosPagina.forEach(item => {
                const row = document.createElement('tr');
                
                // Formatação da data para PT-BR
                const dataOriginal = item.data || item.DATA || item.data_correcao || '';
                const dataFormatada = this.formatarDataPtBr(dataOriginal);
                
                row.innerHTML = `
                    <td><span class="fw-semibold text-uppercase text-dark">${item.placa_original || item.PLACA_ORIGINAL || ''}</span></td>
                    <td><span class="fw-semibold text-uppercase text-primary">${item.placa_corrigida || item.PLACA_CORRIGIDA || item.placa_nova || ''}</span></td>
                    <td><span class="text-secondary">${item.usuario || item.USUARIO || ''}</span></td>
                    <td><span class="text-secondary">${dataFormatada}</span></td>
                `;
                tbody.appendChild(row);
            });
        }

        if (this.badgeTotal) {
            this.badgeTotal.textContent = `${totalRegistros} registro${totalRegistros !== 1 ? 's' : ''}`;
        }

        if (this.infoPaginacao) {
            if (totalRegistros === 0) {
                this.infoPaginacao.textContent = 'Nenhum registro encontrado.';
            } else {
                this.infoPaginacao.textContent = `Exibindo ${inicio + 1} - ${fim} de ${totalRegistros}`;
            }
        }

        this.atualizarPaginacao();
        this.atualizarDisponibilidadeExportacao();
    }



    /**
     * Atualiza os componentes de paginação conforme o estado atual.
     */
    atualizarPaginacao() {
        if (this.indicadorPagina) {
            this.indicadorPagina.textContent = `Página ${this.paginaAtual} de ${this.totalPaginas}`;
        }

        const totalRegistros = this.dados.length;
        const desabilitar = totalRegistros === 0;

        if (this.botaoAnterior) {
            this.botaoAnterior.disabled = desabilitar || this.paginaAtual <= 1;
        }

        if (this.botaoProximo) {
            this.botaoProximo.disabled = desabilitar || this.paginaAtual >= this.totalPaginas;
        }
    }

    /**
     * Altera a página atual do relatório.
     * @param {number} pagina - Página solicitada (base 1).
     */
    irParaPagina(pagina) {
        const totalPaginas = Math.max(1, this.totalPaginas);
        const paginaNormalizada = Math.min(Math.max(pagina, 1), totalPaginas);

        if (paginaNormalizada === this.paginaAtual || this.dados.length === 0) {
            return;
        }

        this.paginaAtual = paginaNormalizada;
        this.renderizarTabela();
    }

    /**
     * Define a disponibilidade dos botões de exportação conforme a existência de dados.
     */
    atualizarDisponibilidadeExportacao() {
        const botaoPDF = document.getElementById('btnExportarPDF');
        const botaoExcel = document.getElementById('btnExportarExcel');
        const desabilitar = !this.dados || this.dados.length === 0;

        if (botaoPDF) {
            botaoPDF.disabled = desabilitar;
        }

        if (botaoExcel) {
            botaoExcel.disabled = desabilitar;
        }
    }

    /**
     * Altera o tamanho da página e recalcula a paginação
     * @param {number} novoTamanho - Novo tamanho da página (-1 para mostrar todos)
     */
    alterarTamanhoPagina(novoTamanho) {
        this.tamanhoPagina = novoTamanho === -1 ? this.dados.length : novoTamanho;
        this.totalPaginas = novoTamanho === -1 ? 1 : Math.max(1, Math.ceil(this.dados.length / this.tamanhoPagina));
        this.paginaAtual = 1;
        this.renderizarTabela();
    }
}

// Instância global
let relatorioPlacas;

// Inicializa quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    relatorioPlacas = new RelatorioPlacasVeiculares();
});

/**
 * Limpa o formulário e redefine valores padrão
 */
function limparFormulario() {
    const placas = document.getElementById('placas');
    
    if (placas) placas.value = '';
    
    if (relatorioPlacas) {
        relatorioPlacas.initializeDates();
        relatorioPlacas.atualizarDados([]);
        relatorioPlacas.hideMessage();
    }
}

/**
 * Consulta o relatório via AJAX
 */
function consultarRelatorio() {
    if (!relatorioPlacas || !relatorioPlacas.validarPeriodo()) return;

    const campoPlacas = document.getElementById('placas');
    const placasSanitizadas = relatorioPlacas.sanitizarPlacas(campoPlacas ? campoPlacas.value : '');
    const dataInicio = document.getElementById('dataInicio').value;
    const dataFinal = document.getElementById('dataFinal').value;

    if (!dataInicio || !dataFinal) {
        relatorioPlacas.showMessage('Por favor, informe o período para consulta.', 'error');
        return;
    }

    // Placas agora são opcionais - remoção da validação obrigatória

    if (campoPlacas && placasSanitizadas) {
        campoPlacas.value = relatorioPlacas.formatarPlacasParaExibicao(placasSanitizadas);
    }

    const params = new URLSearchParams({
        dataInicio: dataInicio,
        dataFinal: dataFinal
    });
    
    // Adiciona placas apenas se foram informadas
    if (placasSanitizadas) {
        params.set('placas', placasSanitizadas);
    }

    relatorioPlacas.showLoading('Consultando dados...');
    relatorioPlacas.hideMessage();

    fetch('/MuralhaDigital/RelatorioPlacasVeiculares?' + params.toString())
        .then(response => {
            if (!response.ok) {
                return response.text().then(texto => {
                    throw new Error(texto || 'Falha na consulta do relatório');
                });
            }
            return response.json();
        })
        .then(data => {
            relatorioPlacas.hideLoading();
            
            if (data.success && data.dados) {
                relatorioPlacas.exibirResultados(data.dados);

                if (!data.dados.length) {
                    relatorioPlacas.showMessage('Nenhum registro encontrado para os critérios informados.', 'warning');
                }
            } else {
                relatorioPlacas.exibirResultados([]);
                relatorioPlacas.showMessage('Nenhum registro encontrado para os critérios informados.', 'warning');
            }
        })
        .catch(error => {
            relatorioPlacas.hideLoading();
            console.error('Erro:', error);
            const mensagem = error && error.message ? error.message : 'Erro ao consultar dados. Tente novamente.';
            relatorioPlacas.showMessage(mensagem, 'error');
            relatorioPlacas.exibirResultados([]);
        });
}

/**
 * Exporta dados para PDF
 */
function exportarPDF() {
    if (!relatorioPlacas || !relatorioPlacas.dados || relatorioPlacas.dados.length === 0) {
        relatorioPlacas.showMessage('Nenhum dado disponível para exportação.', 'warning');
        return;
    }

    try {
        if (!window.jspdf || !window.jspdf.jsPDF) {
            throw new Error('Biblioteca jsPDF não encontrada.');
        }

        const { jsPDF } = window.jspdf;
        const doc = new jsPDF('landscape', 'pt', 'a4');
        const cabecalho = [['Placa Original', 'Placa Corrigida', 'Usuário', 'Data da Correção']];
        const corpo = relatorioPlacas.dados.map(item => [
            (item.placa_original || item.PLACA_ORIGINAL || '').toString(),
            (item.placa_corrigida || item.PLACA_CORRIGIDA || item.placa_nova || '').toString(),
            (item.usuario || item.USUARIO || '').toString(),
            relatorioPlacas.formatarDataPtBr(item.data || item.DATA || item.data_correcao || '')
        ]);

        doc.autoTable({
            head: cabecalho,
            body: corpo,
            styles: { fontSize: 10 },
            headStyles: { fillColor: [13, 117, 191], textColor: 255, halign: 'center' },
            margin: { top: 60, left: 40, right: 40, bottom: 40 },
            didDrawPage: function(data) {
                doc.setFontSize(14);
                doc.setTextColor(13, 117, 191);
                doc.text('Relatório Histórico de Correção de Placas Veiculares', doc.internal.pageSize.getWidth() / 2, 30, { align: 'center' });
            }
        });

        const nomeArquivo = `relatorio-placas-${new Date().toISOString().slice(0, 10)}.pdf`;
        doc.save(nomeArquivo);
        relatorioPlacas.hideMessage();
    } catch (erro) {
        console.error('Falha ao exportar PDF:', erro);
        relatorioPlacas.showMessage('Não foi possível gerar o PDF. Tente novamente.', 'error');
    }
}

/**
 * Exporta dados para Excel
 */
function exportarExcel() {
    if (!relatorioPlacas || !relatorioPlacas.dados || relatorioPlacas.dados.length === 0) {
        relatorioPlacas.showMessage('Nenhum dado disponível para exportação.', 'warning');
        return;
    }

    try {
        if (typeof XLSX === 'undefined' || !XLSX.utils || !XLSX.writeFile) {
            throw new Error('Biblioteca XLSX não encontrada.');
        }

        const cabecalho = ['Placa Original', 'Placa Corrigida', 'Usuário', 'Data da Correção'];
        const linhas = relatorioPlacas.dados.map(item => [
            (item.placa_original || item.PLACA_ORIGINAL || '').toString(),
            (item.placa_corrigida || item.PLACA_CORRIGIDA || item.placa_nova || '').toString(),
            (item.usuario || item.USUARIO || '').toString(),
            relatorioPlacas.formatarDataPtBr(item.data || item.DATA || item.data_correcao || '')
        ]);

        const planilha = XLSX.utils.aoa_to_sheet([cabecalho, ...linhas]);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, planilha, 'Relatório');

        const nomeArquivo = `relatorio-placas-${new Date().toISOString().slice(0, 10)}.xlsx`;
        XLSX.writeFile(workbook, nomeArquivo);
        relatorioPlacas.hideMessage();
    } catch (erro) {
        console.error('Falha ao exportar Excel:', erro);
        relatorioPlacas.showMessage('Não foi possível gerar o arquivo Excel. Tente novamente.', 'error');
    }
}

/**
 * Função global para alterar tamanho da página
 */
function alterarTamanhoPagina() {
    const seletor = document.getElementById('tamanhoPagina');
    if (seletor && relatorioPlacas) {
        const novoTamanho = parseInt(seletor.value);
        relatorioPlacas.alterarTamanhoPagina(novoTamanho);
    }
}