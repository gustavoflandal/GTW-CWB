package muralha.digital.acessos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ConfiguracaoTempoOcrBlitzResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguracaoTempoOcrBlitzResult {
    
    @XmlElement(name = "configuracao")
    private ConfiguracaoTempoOcrBlitz configuracao;
    
    @XmlElement(name = "sucesso")
    private Boolean sucesso;
    
    @XmlElement(name = "msgResposta")
    private String msgResposta;

    public ConfiguracaoTempoOcrBlitz getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoTempoOcrBlitz configuracao) {
        this.configuracao = configuracao;
    }

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMsgResposta() {
        return msgResposta;
    }

    public void setMsgResposta(String msgResposta) {
        this.msgResposta = msgResposta;
    }
}