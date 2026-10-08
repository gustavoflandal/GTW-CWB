/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 22/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe para construir dados estruturados que serão incluídos na assinatura digital.
 * 
 * Permite proteger metadados importantes como coordenadas, timestamp, origem, etc.
 * contra alterações não autorizadas através de assinatura digital.
 */
public class DadosAssinatura {
    /** Dados binários da imagem original */
    private final byte[] dadosImagem;
    
    /** Timestamp da assinatura */
    private final String timestamp;
    
    /** Sistema de origem */
    private final String origem;
    
    /** Tipo de solicitante */
    private final TipoSolicitanteAssinatura tipoSolicitante;
    
    /** Alias do certificado usado */
    private final String aliasCertificado;
    
    /** Coordenadas geográficas opcionais */
    private String coordenadas;
    
    /** Metadados extras opcionais */
    private String metadadosExtras;

    /**
     * Construtor para criar nova assinatura digital com timestamp automático.
     *
     * @param dadosImagem dados binários da imagem original
     * @param origem sistema de origem (ex: GTW-MURALHA-DIGITAL-v1.0)
     * @param tipoSolicitante tipo do solicitante (MANUAL ou SISTEMA)
     * @param aliasCertificado alias do certificado usado na assinatura
     */
    public DadosAssinatura(byte[] dadosImagem, String origem, TipoSolicitanteAssinatura tipoSolicitante, String aliasCertificado) {
        this.dadosImagem = dadosImagem != null ? dadosImagem.clone() : new byte[0];
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).format(new Date());
        this.origem = origem != null ? origem : "GTW-MURALHA-DIGITAL-v1.0";
        this.tipoSolicitante = tipoSolicitante != null ? tipoSolicitante : TipoSolicitanteAssinatura.MANUAL;
        this.aliasCertificado = aliasCertificado;
    }
    
    /**
     * Construtor para validação com timestamp específico extraído da assinatura.
     * CRÍTICO: Usado apenas para reconstruir dados durante validação.
     *
     * @param dadosImagem dados binários da imagem original
     * @param origem sistema de origem extraído da assinatura
     * @param tipoSolicitante tipo do solicitante extraído da assinatura
     * @param aliasCertificado alias do certificado extraído da assinatura
     * @param timestamp timestamp extraído da assinatura a ser validada
     */
    public DadosAssinatura(byte[] dadosImagem, String origem, TipoSolicitanteAssinatura tipoSolicitante, String aliasCertificado, String timestamp) {
        this.dadosImagem = dadosImagem != null ? dadosImagem.clone() : new byte[0];
        this.timestamp = timestamp != null ? timestamp : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).format(new Date());
        this.origem = origem != null ? origem : "GTW-MURALHA-DIGITAL-v1.0";
        this.tipoSolicitante = tipoSolicitante != null ? tipoSolicitante : TipoSolicitanteAssinatura.MANUAL;
        this.aliasCertificado = aliasCertificado;
    }
    
    /**
     * Define coordenadas geográficas.
     *
     * @param latitude latitude em graus decimais
     * @param longitude longitude em graus decimais
     * @return esta instância para method chaining
     */
    public DadosAssinatura comCoordenadas(double latitude, double longitude) {
        this.coordenadas = String.format(Locale.US, "%.6f,%.6f", latitude, longitude);
        return this;
    }
    
    /**
     * Define coordenadas geográficas a partir de string.
     *
     * @param coordenadas coordenadas no formato "latitude,longitude"
     * @return esta instância para method chaining
     */
    public DadosAssinatura comCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
        return this;
    }
    
    /**
     * Define metadados extras customizáveis.
     *
     * @param metadados informações adicionais em formato JSON ou chave=valor
     * @return esta instância para method chaining
     */
    public DadosAssinatura comMetadados(String metadados) {
        this.metadadosExtras = metadados;
        return this;
    }
    
    /**
     * Gera string de metadados estruturados usando formato consistente.
     * Método centralizado para evitar duplicação de lógica.
     *
     * @param incluirAssinatura se deve incluir prefixo de assinatura digital
     * @param assinaturaBase64 assinatura em Base64 (apenas se incluirAssinatura = true)
     * @return string de metadados formatada
     */
    private String gerarMetadadosEstruturados(boolean incluirAssinatura, String assinaturaBase64) {
        StringBuilder metadados = new StringBuilder();
        
        if (incluirAssinatura && assinaturaBase64 != null) {
            metadados.append("DIGITAL_SIGNATURE:").append(assinaturaBase64).append("|");
            metadados.append("TIMESTAMP:").append(timestamp).append("|");
        }
        
        metadados.append("ORIGEM:").append(origem).append("|");
        metadados.append("SOLICITANTE:").append(tipoSolicitante.name()).append("|");
        metadados.append("CERTIFICADO:").append(aliasCertificado != null ? aliasCertificado : "N/A").append("|");
        
        if (coordenadas != null && !coordenadas.trim().isEmpty()) {
            metadados.append("COORDENADAS:").append(coordenadas).append("|");
        }
        
        if (metadadosExtras != null && !metadadosExtras.trim().isEmpty()) {
            metadados.append("EXTRAS:").append(metadadosExtras).append("|");
        }
        
        return metadados.toString();
    }

    /**
     * Gera os bytes que serão assinados digitalmente.
     * Inclui imagem + metadados estruturados para garantir integridade completa.
     *
     * @return array de bytes contendo imagem + metadados estruturados
     */
    public byte[] gerarBytesParaAssinatura() {
        String metadadosStr = gerarMetadadosEstruturados(false, null);
        byte[] metadadosBytes = metadadosStr.getBytes(StandardCharsets.UTF_8);
        byte[] resultado = new byte[dadosImagem.length + metadadosBytes.length];
        
        System.arraycopy(dadosImagem, 0, resultado, 0, dadosImagem.length);
        System.arraycopy(metadadosBytes, 0, resultado, dadosImagem.length, metadadosBytes.length);
        
        return resultado;
    }

    /** 
     * Obtém os dados da imagem original.
     *
     * @return array de bytes da imagem
     */
    public byte[] getDadosImagem() {
        return dadosImagem.clone();
    }
    
    /**
     * Obtém o timestamp da assinatura.
     *
     * @return timestamp no formato ISO-8601
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Obtém o sistema de origem.
     *
     * @return identificador do sistema de origem
     */
    public String getOrigem() {
        return origem;
    }
    
    /**
     * Obtém o tipo de solicitante.
     *
     * @return tipo de solicitante da assinatura
     */
    public TipoSolicitanteAssinatura getTipoSolicitante() {
        return tipoSolicitante;
    }
    
    /**
     * Obtém o alias do certificado.
     *
     * @return alias do certificado usado na assinatura
     */
    public String getAliasCertificado() {
        return aliasCertificado;
    }
    
    /**
     * Obtém as coordenadas geográficas.
     *
     * @return coordenadas no formato "latitude,longitude" ou null se não definidas
     */
    public String getCoordenadas() {
        return coordenadas;
    }
    
    /**
     * Obtém os metadados extras.
     *
     * @return metadados adicionais ou null se não definidos
     */
    public String getMetadadosExtras() {
        return metadadosExtras;
    }
    
    /**
     * Gera comentário JPEG com assinatura digital e metadados estruturados.
     *
     * @param assinaturaBase64 assinatura digital codificada em Base64
     * @return comentário formatado para inserção em JPEG
     */
    public String gerarComentarioJPEG(String assinaturaBase64) {
        return gerarMetadadosEstruturados(true, assinaturaBase64);
    }
}