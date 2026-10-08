package muralha.digital.consulta;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "LinhaTempo")
@XmlAccessorType (XmlAccessType.FIELD)
public class LinhaTempo
{
    @XmlTransient
	private static final Logger logger = Logger.getLogger(LinhaTempo.class);
    
	private int passo;
	private String nomePasso;
	private Date data;
	private String tempo;
	public	String dataFormatada = "";
	
	public LinhaTempo() {}

	public int getPasso() { return passo; }
	public void setPasso(int passo) { this.passo = passo; }

	public String getNomePasso() { return nomePasso; }
	public void setNomePasso(String nomePasso) { this.nomePasso = nomePasso; }

	public Date getData() { return data; }
	public void setData(Date data) { this.data = data; }

	public String getTempo() { return tempo; }
	public void setTempo(String tempo) { this.tempo = tempo; }
	
	public String getDataFormatada() { return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data); }
	public void setDataFormatada(String dataFormatada) { this.dataFormatada = dataFormatada; }
}
