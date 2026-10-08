package muralha.digital.registroDeFato;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="FatoCompleto")
@XmlAccessorType(XmlAccessType.FIELD)
public class FatoSemBoletimCompletoDTO {
	
	public RegistroDeFato registro_fato;
	
	public List<EnvolvidoDTO> envolvidos;
    
	public List<VeiculoDTO> veiculos;
	
	public List<RegistroFatoObjeto> objetos;
	
	public List<RegistroDeFatoPassagemVeiculo> passagens;
	    
	public Localizacao localizacao;	
	
	public List<Localizacao> enderecos;	
	
	public List<Integer> idsUsuarios;
	
	public List<Integer> idsGrupos;
}
