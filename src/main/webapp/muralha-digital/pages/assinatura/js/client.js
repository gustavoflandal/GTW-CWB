/**
 * Cliente API centralizado para comunicação com servlets de assinatura e validação.
 * Responsável por requisições HTTP e processamento inicial de respostas.
 */
class AssinaturaImagemClient {
    
    constructor() {
        // Cache para elementos DOM frequentemente acessados
        this.tempDiv = document.createElement('div');
        this.regexPatterns = this._initializeRegexPatterns();
    }

    /**
     * Inicializa padrões regex para extração de hash (evita recriar em cada chamada).
     * @private
     */
    _initializeRegexPatterns() {
        return [
            /data-sha256="([A-Fa-f0-9]{64})"/i,
            /data-sha256="([A-Fa-f0-9]{32})"/i,
            /Hash SHA-256:\s*<code[^>]*>([A-Fa-f0-9]{32,})<\/code>/i,
            /Hash[:\s]*([A-Fa-f0-9]{64})/i,
            /SHA-?256[:\s]*([A-Fa-f0-9]{64})/i,
            /([A-Fa-f0-9]{64})/,
            /([A-Fa-f0-9]{32})/
        ];
    }

    /**
     * Extrai origem do JSP da URL atual.
     * @private
     */
    _extractMdOrigin() {
        const pathname = window.location.pathname;
        const mdOrigin = pathname?.substring(pathname.lastIndexOf('/') + 1);
        
        if (!mdOrigin || mdOrigin.trim() === '') {
            throw new Error('Origem do JSP não pode ser detectada automaticamente. Informe o parâmetro "origem" explicitamente.');
        }
        
        return mdOrigin.trim();
    }

    /**
     * Envia requisição HTTP para servlet usando FormData.
     * @param {string} url - URL do servlet de destino.
     * @param {FormData} formData - Dados do formulário a serem enviados.
     * @returns {Promise<string>} - Promise com resposta HTML do servlet.
     */
    async enviarRequisicao(url, formData) {
        formData.append('md_origin', this._extractMdOrigin());
        
        const response = await fetch(url, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.text();
    }

    /**
     * Processa resposta HTML do servlet extraindo status, mensagem, hash, imagem assinada e tipo de solicitante.
     * @param {string} htmlResponse - HTML retornado pelo servlet.
     * @returns {Object} - Objeto com {status, mensagem, hash, imagemAssinada, tipoSolicitante}.
     */
    processarRespostaServlet(htmlResponse) {
        // Reutiliza tempDiv para melhor performance
        this.tempDiv.innerHTML = htmlResponse;

        const statusResult = this._extractStatusAndMessage(this.tempDiv, htmlResponse);
        const hash = this._extractHash(htmlResponse, this.tempDiv);
        const additionalData = this._extractAdditionalData(htmlResponse);

        return {
            status: statusResult.status,
            mensagem: statusResult.mensagem,
            hash,
            imagemAssinada: additionalData.imagemAssinada,
            tipoSolicitante: additionalData.tipoSolicitante
        };
    }

    /**
     * Extrai status e mensagem da resposta HTML.
     * @private
     */
    _extractStatusAndMessage(tempDiv, htmlResponse) {
        // Cache seletores DOM para melhor performance
        const elements = {
            alertSuccess: tempDiv.querySelector('.alert-success'),
            alertDanger: tempDiv.querySelector('.alert-danger'),
            cardSuccess: tempDiv.querySelector('.card-header.bg-success'),
            cardDanger: tempDiv.querySelector('.card-header.bg-danger')
        };

        // Prioridade: alertas e cards de erro
        if (elements.alertDanger || elements.cardDanger) {
            const element = elements.alertDanger || elements.cardDanger;
            const mensagem = element.textContent.trim();
            return { status: 'erro', mensagem };
        }

        // Alertas e cards de sucesso
        if (elements.alertSuccess || elements.cardSuccess) {
            const element = elements.alertSuccess || elements.cardSuccess;
            const mensagem = element.textContent.trim();
            return { status: 'sucesso', mensagem };
        }

        // Fallback: análise de conteúdo HTML
        return this._analyzeHtmlContent(htmlResponse);
    }

    /**
     * Analisa conteúdo HTML quando elementos DOM não estão presentes.
     * @private
     */
    _analyzeHtmlContent(htmlResponse) {
        const htmlLower = htmlResponse.toLowerCase();
        
        const errorKeywords = ['erro', 'falha', 'inválid', 'não encontrada'];
        const successKeywords = ['sucesso', 'válid', 'assinada'];
        
        const hasError = errorKeywords.some(keyword => htmlLower.includes(keyword));
        const hasSuccess = successKeywords.some(keyword => htmlLower.includes(keyword));
        
        if (hasError) {
            return { status: 'erro', mensagem: 'Erro no processamento' };
        }
        
        if (hasSuccess) {
            return { status: 'sucesso', mensagem: 'OK' };
        }
        
        return { status: 'sucesso', mensagem: 'OK' };
    }

    /**
     * Extrai hash SHA-256 da resposta.
     * @private
     */
    _extractHash(htmlResponse, tempDiv) {
        // Tenta extração via regex (mais rápido)
        for (const pattern of this.regexPatterns) {
            const match = htmlResponse.match(pattern);
            if (match) {
                return match[1];
            }
        }

        // Fallback: busca em elementos DOM
        return this._extractHashFromDOM(tempDiv);
    }

    /**
     * Extrai hash de elementos DOM como fallback.
     * @private
     */
    _extractHashFromDOM(tempDiv) {
        const hashElements = tempDiv.querySelectorAll('code, .font-monospace, span[data-sha256]');
        
        for (const elem of hashElements) {
            const dataAttr = elem.getAttribute?.('data-sha256');
            if (dataAttr && /^[A-Fa-f0-9]{32,}$/.test(dataAttr)) {
                return dataAttr;
            }

            const text = elem.textContent?.trim();
            if (text && /^[A-Fa-f0-9]{32,}$/.test(text)) {
                return text;
            }
        }

        return '';
    }

    /**
     * Extrai dados adicionais (imagem assinada e tipo de solicitante).
     * @private
     */
    _extractAdditionalData(htmlResponse) {
        const imagemMatch = htmlResponse.match(/data-imagem-assinada="([^"]+)"/i);
        const tipoMatch = htmlResponse.match(/data-tipo-solicitante="([^"]+)"/i);

        return {
            imagemAssinada: imagemMatch?.[1] || '',
            tipoSolicitante: tipoMatch?.[1] || 'Manual'
        };
    }
}