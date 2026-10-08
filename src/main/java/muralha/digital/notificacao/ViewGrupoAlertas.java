package muralha.digital.notificacao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

@XmlRootElement(name = "ViewGrupoAlertas")
@XmlAccessorType (XmlAccessType.FIELD)
public class ViewGrupoAlertas {
	
	@XmlTransient
	private static final Logger logger = Logger.getLogger(ViewGrupoAlertas.class);
	
	private int id_grupo;
	
	private String descricao;
	
	private int id_grupo_pai;

	public int getId_grupo() {
		return id_grupo;
	}

	public void setId_grupo(int id_grupo) {
		this.id_grupo = id_grupo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId_grupo_pai() {
		return id_grupo_pai;
	}

	public void setId_grupo_pai(int id_grupo_pai) {
		this.id_grupo_pai = id_grupo_pai;
	}		
}

