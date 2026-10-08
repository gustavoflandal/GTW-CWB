package muralha.digital.registroDeFato;

import java.util.Date;
import java.util.UUID;

public class RegistroDeFatoPassagemVeiculo {
	private Integer id;
    private Long id_registro_fato;
    private UUID id_veiculo;
    private Integer id_usuario;
    private Date data_passagem;
    private String placa;
	
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }  	
	
	public Long getId_registro_fato() {
		return id_registro_fato;
	}

	public void setId_registro_fato(Long id_registro_fato) {
		this.id_registro_fato = id_registro_fato;
	}

	public UUID getId_veiculo() {
		return id_veiculo;
	}
	
	public void setId_veiculo(UUID id_veiculo) {
		this.id_veiculo = id_veiculo;
	}
	
	public Integer getId_usuario() {
		return id_usuario;
	}
	
	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}
			
	public Date getData_passagem() {
		return data_passagem;
	}

	public void setData_passagem(Date data_passagem) {
		this.data_passagem = data_passagem;
	}

	public String getPlaca() {
		return placa;
	}
	
	public void setPlaca(String placa) {
		this.placa = placa;
	}		
}
