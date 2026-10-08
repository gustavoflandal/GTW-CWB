package muralha.digital.relatorios;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "GraficoValores")
@XmlAccessorType (XmlAccessType.FIELD)
public class Grafico
{
    @XmlTransient
	private static final Logger logger = Logger.getLogger(Grafico.class);
	
	public Grafico() {}
	
	private String labelDataset;

	private List<Integer> valor;
	
	
	public String getLabelDataset() { return labelDataset; }
	public void setLabelDataset(String labelDataset) { this.labelDataset = labelDataset; }
	
	public List<Integer> getValores() { return valor; }
	public void setValores(List<Integer> valor) { this.valor = valor; }
}
