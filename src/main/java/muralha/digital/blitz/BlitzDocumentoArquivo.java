package muralha.digital.blitz;

import java.sql.Timestamp;

public class BlitzDocumentoArquivo {
    private Long id;
    private Long id_documento;
    private String caminho_arquivo;
    private String nome_arquivo_original;
    private String tipo_arquivo;
    private Timestamp data_criacao;
    
    public BlitzDocumentoArquivo() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId_documento() {
        return id_documento;
    }
    
    public void setId_documento(Long id_documento) {
        this.id_documento = id_documento;
    }
    
    public String getCaminho_arquivo() {
        return caminho_arquivo;
    }
    
    public void setCaminho_arquivo(String caminho_arquivo) {
        this.caminho_arquivo = caminho_arquivo;
    }
    
    public String getNome_arquivo_original() {
        return nome_arquivo_original;
    }
    
    public void setNome_arquivo_original(String nome_arquivo_original) {
        this.nome_arquivo_original = nome_arquivo_original;
    }
    
    public String getTipo_arquivo() {
        return tipo_arquivo;
    }
    
    public void setTipo_arquivo(String tipo_arquivo) {
        this.tipo_arquivo = tipo_arquivo;
    }
    
    public Timestamp getData_criacao() {
        return data_criacao;
    }
    
    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }
}