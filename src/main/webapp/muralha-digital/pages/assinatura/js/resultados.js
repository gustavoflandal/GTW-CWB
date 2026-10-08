/**
 * Gerenciador de resultados e localStorage para operações de assinatura e validação digital.
 * Controla de operações, persistência em localStorage e atualização da interface.
 */
class AssinaturaImagemResultadosManager {
    
    /**
     * Converte uma classe de status de texto para uma classe de badge.
     * Centraliza a lógica de conversão para evitar duplicação de código.
     * @param {string} statusClass - Classe de status de texto (text-success, text-danger, etc.).
     * @returns {string} Classe de badge correspondente (bg-success, bg-danger, etc.).
     */
    static converterStatusParaBadge(statusClass) {
        switch (statusClass) {
            case 'text-success':
                return 'bg-success';
            case 'text-danger':
                return 'bg-danger';
            case 'text-warning':
                return 'bg-warning text-dark';
            default:
                return 'bg-secondary';
        }
    }
    
    /**
     * Detecta qual página está sendo usada baseado no título da página.
     * @returns {string} Tipo da página: 'assinatura' ou 'validacao'.
     */
    static detectarTipoPagina() {
        const titulo = document.title.toLowerCase();
        
        if (titulo.includes('assinatura digital')) {
            return 'assinatura';
        } else if (titulo.includes('validação')) {
            return 'validacao';
        }
        
        // Fallback: verifica pela URL também
        const url = window.location.pathname.toLowerCase();
        if (url.includes('assinar-imagem')) {
            return 'assinatura';
        } else if (url.includes('validacao-imagem')) {
            return 'validacao';
        }
        
        return 'validacao';
    }
    
    /**
     * Exibe mensagem padrão apropriada baseada no tipo de página.
     * Mensagens diferentes para assinatura e validação.
     */
    exibirMensagemPadrao() {
        const resultadoOperacao = document.getElementById('resultadoOperacao');
        if (!resultadoOperacao) return;
        
        const tipoPagina = AssinaturaImagemResultadosManager.detectarTipoPagina();
        
        let icone, titulo, instrucao;
        
        if (tipoPagina === 'assinatura') {
            icone = 'bi-shield-lock';
            titulo = 'Aguardando assinatura';
            instrucao = 'Selecione uma imagem e clique em "Assinar Imagem" para ver o resultado aqui.';
        } else {
            icone = 'bi-clipboard-check';
            titulo = 'Aguardando validação';
            instrucao = 'Selecione uma imagem e clique em "Validar Assinatura" para ver o resultado aqui.';
        }
        
        resultadoOperacao.innerHTML = `
            <div class="text-center text-muted">
                <i class="bi ${icone} fs-1 mb-3 d-block"></i>
                <p class="mb-0">${titulo}</p>
                <small>${instrucao}</small>
            </div>
        `;
    }
    
    /**
     * Verifica se deve exibir mensagem padrão baseado no estado atual.
     * Exibe mensagem se não há resultado de operação atual na tela.
     */
    verificarExibirMensagemPadrao() {
        const resultadoOperacao = document.getElementById('resultadoOperacao');
        if (!resultadoOperacao) return;
        
        const conteudo = resultadoOperacao.innerHTML.trim();
        if (!conteudo || conteudo.includes('Conteúdo será preenchido dinamicamente')) {
            this.exibirMensagemPadrao();
        }
    }
    
    /**
     * Inicializa o gerenciador carregando resultados salvos do localStorage
     * e configurando referências aos elementos DOM necessários.
     */
    constructor() {
        this.resultados = JSON.parse(localStorage.getItem('gtw-resultados') || '[]');
        this.limiteResultados = 100;
        this.resultadosCard = document.getElementById('resultadosCard');
        this.tabelaResultados = document.getElementById('corpoTabelaResultados');
        
        this.atualizarDisplay();
        this.verificarExibirMensagemPadrao();
    }
    
    /**
     * Adiciona um novo resultado ao histórico de operações.
     * @param {string} tipoOperacao - Tipo da operação ('Assinar' ou 'Validar').
     * @param {string} status - Status da operação (sucesso, erro, processando).
     * @param {string} arquivo - Nome do arquivo processado.
     * @param {string} hash - Hash SHA-256 do arquivo (se disponível).
     * @param {string} mensagem - Mensagem detalhada do resultado.
     * @returns {string} ID único do resultado adicionado.
     */
    adicionarResultado(tipoOperacao, status, arquivo, hash, mensagem) {
        return this.adicionarResultadoCompleto(tipoOperacao, status, arquivo, hash, mensagem, 'Manual');
    }
    
    /**
     * Adiciona um resultado completo ao histórico de operações.
     * @param {string} tipoOperacao - Tipo da operação ('Assinar' ou 'Validar').
     * @param {string} statusInterno - Status interno da operação.
     * @param {string} arquivo - Nome do arquivo processado.
     * @param {string} hash - Hash SHA-256 do arquivo.
     * @param {string} statusDisplay - Status para exibição na interface.
     * @param {string} tipoSolicitante - Tipo do solicitante (Sistema/Manual).
     * @returns {number} ID único do resultado adicionado.
     */
    adicionarResultadoCompleto(tipoOperacao, statusInterno, arquivo, hash, statusDisplay, tipoSolicitante) {
        const novoResultado = {
            id: Date.now(),
            timestamp: Date.now(),
            dataHora: ValidacaoImagemUtils.formatarDataHora(Date.now()),
            tipoOperacao: tipoOperacao,
            status: statusDisplay || statusInterno,
            arquivo: arquivo,
            hash: hash || '',
            tipoSolicitante: tipoSolicitante || 'Manual'
        };
        
        this.resultados.unshift(novoResultado);
        this.aplicarLimite();
        this.salvar();
        this.atualizarDisplay();
        this.atualizarResultadoOperacao(novoResultado);
        
        return novoResultado.id;
    }
    
    /**
     * Atualiza um resultado existente com novos dados.
     * @param {number} idResultado - ID único do resultado a ser atualizado.
     * @param {string} status - Novo status da operação.
     * @param {string} hash - Hash SHA-256 atualizado.
     * @param {string} statusDisplay - Status para exibição.
     * @param {string} tipoSolicitante - Tipo do solicitante (Sistema/Manual).
     */
    atualizarResultado(idResultado, status, hash, statusDisplay, tipoSolicitante) {
        const resultado = this.resultados.find(r => r.id === idResultado);
        if (resultado) {
            resultado.status = statusDisplay || status;
            resultado.hash = hash || '';
            resultado.timestamp = Date.now();
            resultado.dataHora = ValidacaoImagemUtils.formatarDataHora(Date.now());
            if (tipoSolicitante) {
                resultado.tipoSolicitante = tipoSolicitante;
            }
            this.salvar();
            this.atualizarDisplay();
            this.atualizarResultadoOperacao(resultado);
            return true;
        }
        return false;
    }
    
    /**
     * Atualiza status e mensagem de um resultado específico no histórico.
     * Método simplificado para atualizações de cancelamento e erro.
     * @param {string} idResultado - ID único do resultado a ser atualizado.
     * @param {string} status - Novo status da operação (cancelado, erro, etc).
     * @param {string} mensagem - Mensagem descritiva do novo status.
     */
    atualizarStatusResultado(idResultado, status, mensagem) {
        this.atualizarResultado(idResultado, status, '', mensagem);
    }
    
    /**
     * Aplica o limite máximo de resultados mantidos no histórico (100 registros).
     * Remove os registros mais antigos quando o limite é excedido.
     */
    aplicarLimite() {
        if (this.resultados.length > this.limiteResultados) {
            this.resultados = this.resultados.slice(0, this.limiteResultados);
        }
    }
    
    /**
     * Salva os resultados atuais no localStorage do navegador.
     * Inclui tratamento de erro para problemas de quota ou permissões.
     */
    salvar() {
        try {
            localStorage.setItem('gtw-resultados', JSON.stringify(this.resultados));
        } catch (e) {
            console.error('Erro ao salvar resultados:', e);
        }
    }
    
    /**
     * Atualiza a exibição da tabela de histórico com os resultados salvos.
     * Ordena por timestamp (mais recentes primeiro) e controla visibilidade do card.
     */
    atualizarDisplay() {
        if (!this.tabelaResultados) return;
        
        const resultadosOrdenados = [...this.resultados].sort((a, b) => b.timestamp - a.timestamp);
        
        this.tabelaResultados.innerHTML = '';
        
        if (resultadosOrdenados.length === 0) {
            this.tabelaResultados.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum resultado disponível</td></tr>';
            if (this.resultadosCard) {
                this.resultadosCard.style.display = 'none';
            }
            this.exibirMensagemPadrao();
            return;
        }
        
        if (this.resultadosCard) {
            this.resultadosCard.style.display = 'block';
        }
        
        resultadosOrdenados.forEach(resultado => {
            const row = document.createElement('tr');
            
            const statusIcon = ValidacaoImagemUtils.obterIconeStatus(resultado.status);
            const statusClass = ValidacaoImagemUtils.obterClasseStatus(resultado.status);
            const statusBadgeClass = AssinaturaImagemResultadosManager.converterStatusParaBadge(statusClass);
            
            // Não mostrar hash para status de erro/problema
            const statusLower = resultado.status.toLowerCase();
            const shouldShowHash = !(statusLower === ValidacaoImagemUtils.StatusAssinatura.INVALIDA.toLowerCase() ||
                                statusLower === ValidacaoImagemUtils.StatusAssinatura.SEM_ASSINATURA.toLowerCase() ||
                                statusLower === ValidacaoImagemUtils.StatusAssinatura.CORROMPIDA.toLowerCase() ||
                                statusLower === ValidacaoImagemUtils.StatusAssinatura.ERRO.toLowerCase());
            
            const hashDisplay = shouldShowHash ? resultado.hash : '';
            
            row.innerHTML = `
                <td><small class="text-muted">${resultado.dataHora}</small></td>
                <td><strong>${resultado.tipoOperacao}</strong></td>
                <td><span class="badge ${statusBadgeClass}">${resultado.status}</span></td>
                <td><span class="badge bg-secondary">${resultado.tipoSolicitante || 'Manual'}</span></td>
                <td>
                    <span class="text-break" title="${resultado.arquivo}">
                        ${resultado.arquivo}
                    </span>
                </td>
                <td>
                    <code class="hash-field" title="${hashDisplay}">
                        ${hashDisplay}
                    </code>
                </td>
            `;
            
            this.tabelaResultados.appendChild(row);
        });
    }
    
    /**
     * Limpa todos os resultados do histórico e atualiza a interface.
     * Remove dados do localStorage e limpa alertas de feedback da página.
     */
    limpar() {
        this.resultados = [];
        this.salvar();
        
        if (this.tabelaResultados) {
            this.tabelaResultados.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum resultado disponível</td></tr>';
        }
        
        if (this.resultadosCard) {
            this.resultadosCard.style.display = 'none';
        }
        
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            if (alert.classList.contains('alert-success') || alert.classList.contains('alert-danger')) {
                alert.remove();
            }
        });
        
        const dadosServlet = document.getElementById('dadosServlet');
        if (dadosServlet) {
            dadosServlet.innerHTML = '';
        }
        
        this.exibirMensagemPadrao();
    }

    /**
     * Verifica se há dados do servlet na página e processa automaticamente.
     * Captura dados de elementos DOM específicos e adiciona ao histórico.
     */
    verificarResultadosServlet() {
        const dadosServlet = document.getElementById('dadosServlet');
        if (dadosServlet) {
            const span = dadosServlet.querySelector('span[data-acao]');
            if (span) {
                const acao = span.getAttribute('data-acao');
                const okString = span.getAttribute('data-ok');
                const status = span.getAttribute('data-status') || '';
                const tipoSolicitante = span.getAttribute('data-tipo-solicitante') || 'Manual';
                const nomeArquivo = span.getAttribute('data-nome-arquivo') || 'arquivo';
                const sha256 = span.getAttribute('data-sha256') || '';
                const imagemAssinada = span.getAttribute('data-imagem-assinada') || '';
                
                const ok = okString === 'true';
                
                if (acao) {
                    const statusInterno = ok ? 'sucesso' : 'erro';
                    const tipoOperacao = acao === 'assinar' ? 'Assinar' : 'Validar';
                    
                    const jaExiste = this.resultados.some(r => 
                        r.arquivo === nomeArquivo && 
                        r.tipoOperacao === tipoOperacao && 
                        Math.abs(Date.now() - r.timestamp) < 5000
                    );
                    
                    if (!jaExiste) {
                        const resultadoId = this.adicionarResultadoCompleto(tipoOperacao, statusInterno, nomeArquivo, sha256, status, tipoSolicitante);
                        
                        // Se foi assinatura bem-sucedida e há imagem assinada, faz download automaticamente
                        if (acao === 'assinar' && ok && imagemAssinada) {
                            AssinaturaImagemDownloadManager.downloadImagemAssinada(imagemAssinada, nomeArquivo);
                        }
                    }
                }
                
                span.remove();
            }
        }
    }
    
    /**
     * Atualiza o painel "Resultado da Operação" com o último resultado processado.
     * Aplica as mesmas cores do histórico e exibe arquivo e status.
     * @param {Object} resultado - Objeto resultado com dados da operação.
     */
    atualizarResultadoOperacao(resultado) {
        const resultadoOperacao = document.getElementById('resultadoOperacao');
        if (!resultadoOperacao) return;
        
        const statusClass = ValidacaoImagemUtils.obterClasseStatus(resultado.status);
        const badgeClass = AssinaturaImagemResultadosManager.converterStatusParaBadge(statusClass);
        
        resultadoOperacao.innerHTML = `
            <div class="resultado-info">
                <div class="resultado-item">
                    <strong>Arquivo:</strong> ${resultado.arquivo}
                </div>
                <div class="resultado-item">
                    <strong>Status:</strong> 
                    <span class="badge ${badgeClass}">${resultado.status}</span>
                </div>
            </div>
        `;
    }
}