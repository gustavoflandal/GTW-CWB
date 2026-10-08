/**
 * Controlador principal da página de validação de imagem digital.
 * Coordena interações entre formulários, carregamento, resultados e comunicação com servlet.
 */
class AssinaturaImagemController {
    
    /**
     * Inicializa o controlador configurando gerenciadores, cliente API, flags de estado
     * e executando procedimentos de inicialização necessários.
     */
    constructor() {
        this.loadingManager = new AssinaturaImagemLoadingManager();
        this.resultadosManager = new AssinaturaImagemResultadosManager();
        this.apiClient = new AssinaturaImagemClient();
        this.isSubmittingAssinar = false;
        this.isSubmittingValidar = false;
        
        this.inicializarElementos();
        this.inicializarEventos();
        this.verificarResultadosIniciais();
    }
    
    /**
     * Inicializa referências aos elementos DOM utilizados pelo controlador.
     * Captura formulários, botões e campos de upload de arquivos.
     */
    inicializarElementos() {
        this.imgAssinar = document.getElementById('imgAssinar');
        this.btnAssinar = document.getElementById('btnAssinar');
        this.formAssinar = this.imgAssinar?.closest('form');
        
        this.imgVerificar = document.getElementById('imgVerificar');
        this.btnValidar = document.getElementById('btnValidar');
        this.formValidar = this.imgVerificar?.closest('form');
    }
    
    /**
     * Configura todos os event listeners necessários para o funcionamento da página.
     * Inclui eventos de arquivos, formulários e funções globais.
     */
    inicializarEventos() {
        this.configurarEventosArquivos();
        this.configurarEventosFormularios();
        this.configurarFuncoesGlobais();
    }
    
    /**
     * Configura eventos para mudanças nos campos de upload de arquivo.
     * Atualiza o estado visual dos botões baseado na seleção de arquivos.
     */
    configurarEventosArquivos() {
        if (this.imgAssinar && this.btnAssinar) {
            this.imgAssinar.addEventListener('change', () => {
                this.atualizarEstadoBotao(this.imgAssinar, this.btnAssinar, 'btn-primary', 'btn-secondary');
            });
        }
        
        if (this.imgVerificar && this.btnValidar) {
            this.imgVerificar.addEventListener('change', () => {
                this.atualizarEstadoBotao(this.imgVerificar, this.btnValidar, 'btn-success', 'btn-secondary');
            });
        }
    }
    
    /**
     * Configura eventos de submissão dos formulários de assinatura e validação.
     * Implementa envio AJAX com prevenção de submissões duplicadas e feedback visual.
     */
    configurarEventosFormularios() {
        if (this.formAssinar && this.imgAssinar) {
            this.formAssinar.addEventListener('submit', (e) => {
                if (this.isSubmittingAssinar) return;
                
                e.preventDefault();
                const fileName = this.imgAssinar.files[0]?.name || 'arquivo';
                
                this.loadingManager.mostrar('Assinando...', `Arquivo: ${fileName}`);
                
                this.isSubmittingAssinar = true;
                this.submeterFormularioAjax(this.formAssinar, 'Assinar', fileName);
            });
        }
        
        if (this.formValidar && this.imgVerificar) {
            this.formValidar.addEventListener('submit', (e) => {
                if (this.isSubmittingValidar) return;
                
                e.preventDefault();
                const fileName = this.imgVerificar.files[0]?.name || 'arquivo';
                
                this.loadingManager.mostrar('Validando...', `Arquivo: ${fileName}`);
                
                this.isSubmittingValidar = true;
                this.submeterFormularioAjax(this.formValidar, 'Validar', fileName);
            });
        }
    }
    
    /**
     * Submete formulário via AJAX usando cliente centralizado com feedback visual.
     * @param {HTMLFormElement} form - Formulário a ser submetido.
     * @param {string} tipoOperacao - Tipo da operação ('Assinar' ou 'Validar').
     * @param {string} nomeArquivo - Nome do arquivo sendo processado.
     */
    async submeterFormularioAjax(form, tipoOperacao, nomeArquivo) {
        const formData = new FormData(form);

        // Timeout otimizado para evitar operações desnecessárias
        const timeoutId = setTimeout(() => {
            this.loadingManager.esconder();
            this.resultadosManager.adicionarResultadoCompleto(tipoOperacao, 'erro', nomeArquivo, '', 'Erro: Timeout', 'Manual');
            this.limparCamposArquivo(tipoOperacao);
            this.resetarFlags();
        }, 30000);

        try {
            const htmlResponse = await this.apiClient.enviarRequisicao(form.action, formData);
            clearTimeout(timeoutId);
            
            const resultado = this.apiClient.processarRespostaServlet(htmlResponse);
            
            this.loadingManager.esconder();
            this.resultadosManager.adicionarResultadoCompleto(tipoOperacao, resultado.status, nomeArquivo, resultado.hash, resultado.mensagem, resultado.tipoSolicitante);

            // Download automático otimizado apenas quando necessário
            if (tipoOperacao === 'Assinar' && resultado.status === 'sucesso' && resultado.imagemAssinada) {
                AssinaturaImagemDownloadManager.downloadImagemAssinada(resultado.imagemAssinada, nomeArquivo);
            }
            
            this.limparCamposArquivo(tipoOperacao);
            this.resetarFlags();
        } catch (error) {
            clearTimeout(timeoutId);
            this.loadingManager.esconder();
            this.resultadosManager.adicionarResultadoCompleto(tipoOperacao, 'erro', nomeArquivo, '', `Erro: ${error.message}`, 'Manual');
            this.limparCamposArquivo(tipoOperacao);
            this.resetarFlags();
        }
    }
    
    
    /**
     * Reseta as flags de submissão para permitir novas operações.
     * Chamado após conclusão de qualquer operação AJAX.
     */
    resetarFlags() {
        this.isSubmittingAssinar = false;
        this.isSubmittingValidar = false;
    }
    
    /**
     * Limpa os campos de arquivo e reseta o estado visual dos botões.
     * @param {string} tipoOperacao - Tipo da operação ('Assinar' ou 'Validar').
     */
    limparCamposArquivo(tipoOperacao) {
        if (tipoOperacao === 'Assinar' && this.imgAssinar) {
            this.imgAssinar.value = '';
            this.btnAssinar.disabled = true;
            this.btnAssinar.classList.remove('btn-primary');
            this.btnAssinar.classList.add('btn-secondary');
        } else if (tipoOperacao === 'Validar' && this.imgVerificar) {
            this.imgVerificar.value = '';
            this.btnValidar.disabled = true;
            this.btnValidar.classList.remove('btn-success');
            this.btnValidar.classList.add('btn-secondary');
        }
    }
    
    /**
     * Configura funções globais acessíveis no escopo da janela.
     * Inclui função para limpar resultados do histórico
     */
    configurarFuncoesGlobais() {
        window.limparResultados = () => {
            if (confirm('Tem certeza que deseja limpar todos os resultados do histórico?')) {
                if (this.formAssinar) 
                    this.formAssinar.reset();
                if (this.formValidar) 
                    this.formValidar.reset();
                
                this.resetarBotoes();
                this.resultadosManager.limpar();
            }
        };
    }
    
    /**
     * Atualiza o estado visual de um botão baseado na seleção de arquivo.
     * @param {HTMLInputElement} inputFile - Campo de seleção de arquivo.
     * @param {HTMLButtonElement} button - Botão a ser atualizado.
     * @param {string} classeAtiva - Classe CSS quando arquivo selecionado.
     * @param {string} classeInativa - Classe CSS quando nenhum arquivo.
     */
    atualizarEstadoBotao(inputFile, button, classeAtiva, classeInativa) {
        if (!inputFile || !button) return;
        
        if (inputFile.files && inputFile.files.length > 0) {
            button.disabled = false;
            button.className = button.className.replace(classeInativa, classeAtiva);
        } else {
            button.disabled = true;
            button.className = button.className.replace(classeAtiva, classeInativa);
        }
    }
    
    /**
     * Reseta o estado visual de todos os botões para o estado inicial.
     * Chamado durante limpeza de dados ou inicialização.
     */
    resetarBotoes() {
        this.atualizarEstadoBotao(this.imgAssinar, this.btnAssinar, 'btn-primary', 'btn-secondary');
        this.atualizarEstadoBotao(this.imgVerificar, this.btnValidar, 'btn-success', 'btn-secondary');
    }
    
    /**
     * Verifica resultados iniciais ao carregar a página e processa reloads forçados.
     * Detecta tipo de carregamento e limpa dados em caso de reload forçado.
     */
    verificarResultadosIniciais() {
        this.loadingManager.esconder();
        
        // Detecção de reload otimizada com API moderna
        const navigationEntries = performance.getEntriesByType('navigation');
        if (navigationEntries.length > 0) {
            const navEntry = navigationEntries[0];

            // Detecta qualquer tipo de reload (Ctrl+Shift+R, Ctrl+F5, F5, etc.)
            const isForceReload = navEntry.type === 'reload';

            if (isForceReload) {
                this.resultadosManager.limpar();
                return;
            }
        }
        
        this.resultadosManager.verificarResultadosServlet();
    }
}