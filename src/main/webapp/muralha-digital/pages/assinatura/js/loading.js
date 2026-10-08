/**
 * Gerenciador de loading overlay para operações de assinatura e validação digital.
 * Controla a exibição de overlay de carregamento com timeout automático.
 */
class AssinaturaImagemLoadingManager {
    
    /**
     * Inicializa o gerenciador de loading capturando elementos DOM necessários
     * e preparando controle de temporizadores.
     */
    constructor() {
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.loadingText = document.getElementById('loadingText');
        this.loadingSubtext = document.getElementById('loadingSubtext');
        this.loadingTimeout = null;
    }
    
    
    /**
     * Exibe o overlay de loading com título e subtítulo personalizados.
     * Configura timeout de 30 segundos para esconder automaticamente.
     * @param {string} titulo - Título principal exibido no loading.
     * @param {string} subtitulo - Subtítulo com detalhes da operação.
     */
    mostrar(titulo, subtitulo) {
        if (!this.loadingOverlay)
            return;
        
        this.loadingText.textContent = titulo;
        this.loadingSubtext.textContent = subtitulo;
        this.loadingOverlay.style.display = 'flex';
        
        this.loadingTimeout = setTimeout(() => {
            this.esconder();
            alert('⏰ Timeout: A operação demorou mais que 30 segundos e foi cancelada.');
        }, 30000);
    }
    
    /**
     * Esconde o overlay de loading e limpa todos os timers ativos.
     */
    esconder() {
        if (this.loadingOverlay) {
            this.loadingOverlay.style.display = 'none';
        }
        
        if (this.loadingTimeout) {
            clearTimeout(this.loadingTimeout);
            this.loadingTimeout = null;
        }
    }
}