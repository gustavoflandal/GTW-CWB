package muralha.digital.registroDeFato;

import java.util.Date;
import java.util.UUID;

public class RegistroFatoPassagemVeiculo {

    private Integer id;            
    private Long idRegistroFato;     
    private UUID idVeiculo;          
    private Integer idUsuario;        
    private Date data;    
    private Date data_vinculo;
    
    //VeiculoTempoReal
    private String placa;
    private UUID idVeiculoTempoReal;

    public RegistroFatoPassagemVeiculo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(Long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }

    public UUID getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(UUID idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    public UUID getIdVeiculoTempoReal() {
        return idVeiculoTempoReal;
    }

    public void setIdVeiculoTempoReal(UUID idVeiculoTempoReal) {
        this.idVeiculoTempoReal = idVeiculoTempoReal;
    }
    
    public boolean equalsConteudo(RegistroFatoPassagemVeiculo outro) {
        if (outro == null) return false;
        return equalsNullable(idVeiculo, outro.idVeiculo)
            && equalsNullable(placa, outro.placa)
            && equalsNullable(idVeiculoTempoReal, outro.idVeiculoTempoReal)
            && equalsNullable(idUsuario, outro.idUsuario)
            && equalsNullable(data, outro.data);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

	public Date getData_vinculo() {
		return data_vinculo;
	}

	public void setData_vinculo(Date data_vinculo) {
		this.data_vinculo = data_vinculo;
	} 
}
