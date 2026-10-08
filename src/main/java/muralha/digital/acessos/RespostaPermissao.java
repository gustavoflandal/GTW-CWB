package muralha.digital.acessos;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "resposta")
public class RespostaPermissao {

	private boolean permitido;

	public RespostaPermissao() {
	}

	public RespostaPermissao(boolean permitido) {
		this.permitido = permitido;
	}

	@XmlElement(name = "permitido")
	public boolean isPermitido() {
		return permitido;
	}

	public void setPermitido(boolean permitido) {
		this.permitido = permitido;
	}
}
