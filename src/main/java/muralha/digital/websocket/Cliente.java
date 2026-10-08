package muralha.digital.websocket;

import java.util.Date;

import javax.websocket.Session;

import com.consilux.model.Usuario;

import muralha.digital.dispositivo.DispositivosEquipamentos;

public class Cliente
{
	// Pode ser um inteiro ou um GUID
	// Caso seja INT precisa ser convertido para realizar as consultas
	private String						tpCliente;
	private boolean						conectado;
	private Date						dataConexao;
	private Session						sessao;
	private Usuario						usuario;
	private DispositivosEquipamentos	equipamentosTempoReal;
	
	public String getTpCliente() {
		return tpCliente;
	}
	
	public void setTpCliente(String tpCliente) {
		this.tpCliente = tpCliente;
	}

	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public Date getDataConexao() {
		return dataConexao;
	}

	public void setDataConexao(Date dataConexao) {
		this.dataConexao = dataConexao;
	}
	
	public Session getSessao() {
		return sessao;
	}

	public void setSessao(Session sessao) {
		this.sessao = sessao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DispositivosEquipamentos getEquipamentosTempoReal() {
		return equipamentosTempoReal;
	}

	public void setEquipamentosTempoReal(DispositivosEquipamentos equipamentosTempoReal) {
		this.equipamentosTempoReal = equipamentosTempoReal;
	}
	
}
