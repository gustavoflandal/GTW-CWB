package muralha.digital.blitz;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Blitz")
public class BlitzDigital {
    private int id;
    private String nome_blitz;
    private String titulo_notificacao;
    private String descricao;
    private String endereco;
    private Boolean tem_abordagens_associadas;
    private Timestamp data_inicio;
    private Timestamp data_fim;
    private Timestamp data_criacao;
    private int ativo;
    private int notificar_agentes_proximos;
    private BigDecimal raio_notificacao_km;
    private Integer id_tipo_blitz;
    
    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    @XmlElement
    public String getNome_blitz() { return nome_blitz; }
    public void setNome_blitz(String nome_blitz) { this.nome_blitz = nome_blitz; }
    
    @XmlElement
    public String getTitulo_notificacao() { return titulo_notificacao; }
    public void setTitulo_notificacao(String titulo_notificacao) { this.titulo_notificacao = titulo_notificacao; }
    
    @XmlElement
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    @XmlElement
    public Timestamp getData_inicio() { return data_inicio; }
    public void setData_inicio(Timestamp data_inicio) { this.data_inicio = data_inicio; }
    
    @XmlElement
    public Timestamp getData_fim() { return data_fim; }
    public void setData_fim(Timestamp data_fim) { this.data_fim = data_fim; }
    
    @XmlElement
    public Timestamp getData_criacao() { return data_criacao; }
    public void setData_criacao(Timestamp data_criacao) { this.data_criacao = data_criacao; }
    
    @XmlElement
    public int getAtivo() { return ativo; }
    public void setAtivo(int ativo) { this.ativo = ativo; }

    @XmlElement
    public int getNotificar_agentes_proximos() { return notificar_agentes_proximos; }
    public void setNotificar_agentes_proximos(int notificar_agentes_proximos) { 
        this.notificar_agentes_proximos = notificar_agentes_proximos; 
    }

    @XmlElement
    public BigDecimal getRaio_notificacao_km() { return raio_notificacao_km; }
    public void setRaio_notificacao_km(BigDecimal raio_notificacao_km) { 
        this.raio_notificacao_km = raio_notificacao_km; 
    }

    @XmlElement
    public Integer getId_tipo_blitz() { return id_tipo_blitz; }
    public void setId_tipo_blitz(Integer id_tipo_blitz) {
        this.id_tipo_blitz = id_tipo_blitz;
    }

    @XmlElement
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    @XmlElement
    public Boolean getTem_abordagens_associadas() { return tem_abordagens_associadas; }
    public void setTem_abordagens_associadas(Boolean tem_abordagens_associadas) { 
        this.tem_abordagens_associadas = tem_abordagens_associadas; 
    }
}