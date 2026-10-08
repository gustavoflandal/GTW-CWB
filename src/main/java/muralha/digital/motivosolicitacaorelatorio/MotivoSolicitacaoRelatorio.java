package muralha.digital.motivosolicitacaorelatorio;

import java.sql.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MotivoSolicitacaoRelatorio")
@XmlAccessorType (XmlAccessType.FIELD)
public class MotivoSolicitacaoRelatorio {
	
	public Integer id;
	
	public Integer id_usuario;
	
	public String motivo;
	
	public Date data_criacao;
}
