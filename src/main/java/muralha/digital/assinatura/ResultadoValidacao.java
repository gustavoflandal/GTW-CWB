/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 24/01/2025
 *********************************************************************************/
package muralha.digital.assinatura;

/**
 * Resultado da validação de assinatura digital com detalhes.
 */
public class ResultadoValidacao {
    /** Indica se a assinatura digital é válida criptograficamente */
    private final boolean valida;
    
    /** Hash SHA-256 da imagem original reconstruída sem assinatura */
    private final String sha256ImagemReconstruida;
    
    /** Data e hora da assinatura no formato ISO */
    private final String dataAssinatura;
    
    /** Tipo do solicitante da assinatura (MANUAL ou SISTEMA) */
    private final String tipoSolicitante;
    
    /** Sistema de origem da assinatura */
    private final String origem;
    
    /** Alias do certificado digital usado na assinatura */
    private final String aliasCertificado;
    
    /** Coordenadas geográficas opcionais no formato latitude,longitude */
    private final String coordenadas;
    
    /** Metadados extras opcionais em formato customizado */
    private final String metadadosExtras;
    
    /**
     * Construtor do resultado de validação.
     * 
     * @param valida true se a assinatura for válida
     * @param sha256ImagemReconstruida hash SHA-256 da imagem original reconstruída
     * @param dataAssinatura data/hora da assinatura
     * @param tipoSolicitante tipo do solicitante
     * @param origem sistema de origem
     * @param aliasCertificado alias do certificado usado
     * @param coordenadas coordenadas geográficas (opcional)
     * @param metadadosExtras metadados extras (opcional)
     */
    public ResultadoValidacao(boolean valida, String sha256ImagemReconstruida, 
                            String dataAssinatura, String tipoSolicitante, 
                            String origem, String aliasCertificado,
                            String coordenadas, String metadadosExtras) {
        this.valida = valida;
        this.sha256ImagemReconstruida = sha256ImagemReconstruida;
        this.dataAssinatura = dataAssinatura;
        this.tipoSolicitante = tipoSolicitante;
        this.origem = origem;
        this.aliasCertificado = aliasCertificado;
        this.coordenadas = coordenadas;
        this.metadadosExtras = metadadosExtras;
    }
    
    /**
     * @return true se a assinatura for válida
     */
    public boolean isValida() {
        return valida;
    }
    
    /**
     * @return hash SHA-256 da imagem original reconstruída
     */
    public String getSha256ImagemReconstruida() {
        return sha256ImagemReconstruida;
    }
    
    /**
     * @return data/hora da assinatura
     */
    public String getDataAssinatura() {
        return dataAssinatura;
    }
    
    /**
     * @return tipo do solicitante
     */
    public String getTipoSolicitante() {
        return tipoSolicitante;
    }
    
    /**
     * @return sistema de origem
     */
    public String getOrigem() {
        return origem;
    }
    
    /**
     * @return alias do certificado usado
     */
    public String getAliasCertificado() {
        return aliasCertificado;
    }
    
    /**
     * @return coordenadas geográficas
     */
    public String getCoordenadas() {
        return coordenadas;
    }
    
    /**
     * @return metadados extras
     */
    public String getMetadadosExtras() {
        return metadadosExtras;
    }
}