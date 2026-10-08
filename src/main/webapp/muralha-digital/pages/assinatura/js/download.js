/**
 * Utilitário para download de arquivos no navegador.
 * Centraliza funcionalidades de download de imagens assinadas e outros arquivos.
 */
class AssinaturaImagemDownloadManager {
    
    /**
     * Faz download de arquivo através do navegador usando blob e URL.
     * @param {string} base64Data - Dados do arquivo em base64.
     * @param {string} nomeArquivo - Nome do arquivo para download.
     * @param {string} mimeType - Tipo MIME do arquivo.
     */
    static downloadArquivo(base64Data, nomeArquivo, mimeType = 'image/jpeg') {
        try {
            // Converte base64 para blob
            const byteCharacters = atob(base64Data);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: mimeType });
            
            // Cria URL e elemento de download
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = nomeArquivo;
            
            // Executa download
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            
            // Limpa URL para liberar memória
            URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Erro ao fazer download:', error);
            alert('Erro ao baixar arquivo: ' + error.message);
        }
    }
    
    /**
     * Faz download específico de imagem assinada com nomenclatura padrão.
     * @param {string} base64Data - Dados da imagem assinada em base64.
     * @param {string} nomeOriginal - Nome original do arquivo.
     */
    static downloadImagemAssinada(base64Data, nomeOriginal) {
        const nomeBase = nomeOriginal.replace(/\.[^.]+$/, '');
        const nomeDownload = nomeBase + '-assinado.jpg';
        this.downloadArquivo(base64Data, nomeDownload, 'image/jpeg');
    }
}