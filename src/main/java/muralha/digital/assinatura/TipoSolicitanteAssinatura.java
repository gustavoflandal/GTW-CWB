/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 18/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

/**
 * Enum para definir o tipo de solicitante da assinatura digital
 */
public enum TipoSolicitanteAssinatura {
    NULL("Nulo"),
    SISTEMA("Sistema"),
    MANUAL("Manual");
    
    private final String descricao;
    
    TipoSolicitanteAssinatura(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}