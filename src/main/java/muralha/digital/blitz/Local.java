package muralha.digital.blitz;

import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Local")
public class Local {
    private int id_local;
    private int sequencia_local;
    private Double posicao_lat;
    private Double posicao_lon;
    private String nome;
    
    @XmlElement
    public int getId_local() { return id_local; }
    public void setId_local(int id_local) { this.id_local = id_local; }

    @XmlElement
    public Double getPosicao_lat() { return posicao_lat; }
    public void setPosicao_lat(Double posicao_lat) { this.posicao_lat = posicao_lat; }

    @XmlElement
    public Double getPosicao_lon() { return posicao_lon; }
    public void setPosicao_lon(Double posicao_lon) { this.posicao_lon = posicao_lon; }
    
    @XmlElement
    public int getSequencia_local() { return sequencia_local; }
    public void setSequencia_local(int sequencia_local) { this.sequencia_local = sequencia_local; }
    
    @XmlElement
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}