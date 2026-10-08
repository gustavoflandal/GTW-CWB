/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 18/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

/**
 * Enum para padronizar os status de assinatura digital de imagens.
 * Facilita a comunicação entre backend e frontend mantendo consistência.
 */
public enum StatusAssinatura {
    ASSINADA("Assinada"),
    VALIDA("Válida"), 
    JA_ASSINADA("Já assinada"),
    SEM_ASSINATURA("Sem assinatura"),
    INVALIDA("Inválida"),
    CORROMPIDA("Corrompida"),
    ERRO("Erro");
    
    private final String descricao;
    
    StatusAssinatura(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}