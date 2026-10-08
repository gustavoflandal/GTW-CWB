package muralha.digital.blitz;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlitzDocumento {
    private Long id;
    private Long id_abordagem;
    private Long id_pessoa;
    private String tipo_documento;
    private String numero_documento;
    private String nome_titular;
    private Date validade;
    private String situacao;
    private String observacoes;
    private Timestamp data_criacao;
    
    // Campos temporários para processamento
    private transient List<BlitzDocumentoArquivo> arquivos; // transient para não serializar
    
    public BlitzDocumento() {
        this.arquivos = new ArrayList<>();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId_abordagem() {
        return id_abordagem;
    }
    
    public void setId_abordagem(Long id_abordagem) {
        this.id_abordagem = id_abordagem;
    }
    
    public Long getId_pessoa() {
        return id_pessoa;
    }
    
    public void setId_pessoa(Long id_pessoa) {
        this.id_pessoa = id_pessoa;
    }
    
    public String getTipo_documento() {
        return tipo_documento;
    }
    
    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }
    
    public String getNumero_documento() {
        return numero_documento;
    }
    
    public void setNumero_documento(String numero_documento) {
        this.numero_documento = numero_documento;
    }
    
    public String getNome_titular() {
        return nome_titular;
    }
    
    public void setNome_titular(String nome_titular) {
        this.nome_titular = nome_titular;
    }
    
    public Date getValidade() {
        return validade;
    }
    
    public void setValidade(Date validade) {
        this.validade = validade;
    }
    
    public String getSituacao() {
        return situacao;
    }
    
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public Timestamp getData_criacao() {
        return data_criacao;
    }
    
    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }
    
    public List<BlitzDocumentoArquivo> getArquivos() {
        return arquivos;
    }
    
    public void setArquivos(List<BlitzDocumentoArquivo> arquivos) {
        this.arquivos = arquivos;
    }
    
    public void addArquivo(BlitzDocumentoArquivo arquivo) {
        if (this.arquivos == null) {
            this.arquivos = new ArrayList<>();
        }
        this.arquivos.add(arquivo);
    }
}