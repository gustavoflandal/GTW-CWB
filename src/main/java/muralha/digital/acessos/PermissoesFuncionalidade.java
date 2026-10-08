package muralha.digital.acessos;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement		(name="PermissoesFuncionalidade") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class PermissoesFuncionalidade
{	
	
	@XmlElementWrapper	(name = "ListaPermissoesFuncionalidade")
	@XmlElement			(name = "PermissaoFuncionalidade")	
	private List<PermissaoFuncionalidade> listaPermissoesFuncionalidade;

	public List<PermissaoFuncionalidade> getListaPermissoesFuncionalidade() {
		return listaPermissoesFuncionalidade;
	}

	public void setListaPermissoesFuncionalidade(List<PermissaoFuncionalidade> listaPermissoesFuncionalidade) {
		this.listaPermissoesFuncionalidade = listaPermissoesFuncionalidade;
	}	
}
