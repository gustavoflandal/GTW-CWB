package muralha.digital.registroDeFato;

import java.util.List;

public class CadastroRegistroFatoDTO {
	
	public List<EnvolvidoDTO> envolvidos;
	    
	public List<VeiculoDTO> veiculos;
	
	public List<RegistroFatoObjeto> objetos;
	   
	public RegistroFatoDTO registroFato;
	    
	public Localizacao localizacao;
	
    public List<EnvolvidoDTO> getEnvolvidos() {
        return envolvidos;
    }

    public void setEnvolvidos(List<EnvolvidoDTO> envolvidos) {
        this.envolvidos = envolvidos;
    }

    public List<VeiculoDTO> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<VeiculoDTO> veiculos) {
        this.veiculos = veiculos;
    }

    public RegistroFatoDTO getRegistroFato() {
        return registroFato;
    }

    public void setRegistroFato(RegistroFatoDTO registroFato) {
        this.registroFato = registroFato;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
}
