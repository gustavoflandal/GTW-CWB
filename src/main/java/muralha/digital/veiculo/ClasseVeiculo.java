package muralha.digital.veiculo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "ClasseVeiculo")
@XmlAccessorType (XmlAccessType.FIELD)
public class ClasseVeiculo
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(ClasseVeiculo.class);
    
    private String idClasse;
	private String classe;
	
	public ClasseVeiculo() {}

	public String getIdClasse() {
		return idClasse;
	}
	public void setIdClasse(String idClasse) {
		this.idClasse = idClasse;
	}

	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
}
