/**
 * Utilitários gerais para validação de imagem digital, fornecendo funções auxiliares
 * para manipulação de status, cores e operações de cópia de elementos DOM.
 */
class ValidacaoImagemUtils {

    /**
     * Enum para status de assinatura sincronizado com StatusAssinatura.java.
     * Define os possíveis estados de uma assinatura digital.
     */
    static StatusAssinatura = {
        ASSINADA: 'Assinada',
        VALIDA: 'Válida',
        JA_ASSINADA: 'Já assinada',
        SEM_ASSINATURA: 'Sem assinatura',
        INVALIDA: 'Inválida',
        CORROMPIDA: 'Corrompida',
        ERRO: 'Erro'
    };

    /**
     * Mapa de status ENUM para classes CSS Bootstrap.
     */
    static STATUS_CSS_MAP = {
        // Status de sucesso (VERDE)
        'assinada': 'text-success',
        'válida': 'text-success', 
        'valida': 'text-success',
        'ok': 'text-success',
        'sucesso': 'text-success',
        
        // Status de aviso (AMARELO)
        'já assinada': 'text-warning',
        'ja assinada': 'text-warning',
        'sem assinatura': 'text-warning',
        
        // Status de erro/problema (VERMELHO)
        'inválida': 'text-danger',
        'invalida': 'text-danger',
        'corrompida': 'text-danger',
        'erro': 'text-danger',
    };
    
    /**
     * Obtém o valor string do status baseado no ENUM.
     * @param {string} statusEnum - Chave do ENUM
     * @returns {string} Valor string do status
     */
    static obterTextoStatus(statusEnum) {
        return ValidacaoImagemUtils.StatusAssinatura[statusEnum] || 'Erro';
    }
    
    
    /**
     * Formata timestamp Unix em data/hora brasileira.
     * @param {number} timestamp - Timestamp Unix em milissegundos
     * @returns {string} Data formatada DD/MM/AAAA HH:mm:ss
     */
    static formatarDataHora(timestamp) {
        return new Date(timestamp).toLocaleString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }
    
    /**
     * Retorna ícone FontAwesome com classe CSS baseado no status.
     * @param {string} status - Status da operação
     * @returns {string} HTML do ícone com classes CSS
     */
    static obterIconeStatus(status) {
        switch (status.toLowerCase()) {
            case 'sucesso':
                return '<i class="fas fa-check-circle text-success"></i>';
            case 'erro':
                return '<i class="fas fa-times-circle text-danger"></i>';
            case 'processando':
                return '<i class="fas fa-spinner fa-spin text-warning"></i>';
            default:
                return '<i class="fas fa-info-circle text-secondary"></i>';
        }
    }

    /**
     * Retorna a classe CSS Bootstrap apropriada baseada no status da assinatura digital.
     * @param {string} status - Status da assinatura (chave do ENUM ou valor textual)
     * @returns {string} Classe CSS Bootstrap para coloração
     */
    static obterClasseStatus(status) {
        if (!status) return 'text-secondary';
        return ValidacaoImagemUtils.STATUS_CSS_MAP[status.toLowerCase()] || 'text-secondary';
    }
}