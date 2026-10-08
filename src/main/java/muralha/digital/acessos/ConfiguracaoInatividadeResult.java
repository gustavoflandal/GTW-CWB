package muralha.digital.acessos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuracaoInatividade")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguracaoInatividadeResult {

	public ConfiguracaoInatividade LoginTempoInatividade;
	
	public ConfiguracaoInatividade LoginNuncaBloqueia;

	public ConfiguracaoInatividade getLoginTempoInatividade() {
		return LoginTempoInatividade;
	}

	public void setLoginTempoInatividade(ConfiguracaoInatividade loginTempoInatividade) {
		LoginTempoInatividade = loginTempoInatividade;
	}

	public ConfiguracaoInatividade getLoginNuncaBloqueia() {
		return LoginNuncaBloqueia;
	}

	public void setLoginNuncaBloqueia(ConfiguracaoInatividade loginNuncaBloqueia) {
		LoginNuncaBloqueia = loginNuncaBloqueia;
	}
}
