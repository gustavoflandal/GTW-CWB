package muralha.digital.blitz;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AntecedenteCriminal")
public class AntecedenteCriminal {
    private int id;
    private int id_proprietario;
    private String tipo_crime;
    private Date data_ocorrencia;
    private String local_ocorrencia;
    private String descricao;
    private String sentenca;
    
    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    @XmlElement
    public int getId_proprietario() { return id_proprietario; }
    public void setId_proprietario(int id_proprietario) { this.id_proprietario = id_proprietario; }
    
    @XmlElement
    public String getTipo_crime() { return tipo_crime; }
    public void setTipo_crime(String tipo_crime) { this.tipo_crime = tipo_crime; }
    
    @XmlElement
    public Date getData_ocorrencia() { return data_ocorrencia; }
    public void setData_ocorrencia(Date data_ocorrencia) { this.data_ocorrencia = data_ocorrencia; }
    
    @XmlElement
    public String getLocal_ocorrencia() { return local_ocorrencia; }
    public void setLocal_ocorrencia(String local_ocorrencia) { this.local_ocorrencia = local_ocorrencia; }
    
    @XmlElement
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    @XmlElement
    public String getSentenca() { return sentenca; }
    public void setSentenca(String sentenca) { this.sentenca = sentenca; }
}