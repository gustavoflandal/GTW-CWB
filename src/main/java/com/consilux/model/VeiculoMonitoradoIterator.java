package com.consilux.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.model.beans.VeiculoMonitoradoBean;

/**
 * Classe auxiliar que implementa um iterator para navegar num
 * ResultSet de veículos monitorados. 
 * @author raoni
 */
public class VeiculoMonitoradoIterator extends GenericSqlIterator<VeiculoMonitoradoBean>
{

	public VeiculoMonitoradoIterator(ResultSet rs, Connection conn, boolean autoCloseConnection) throws IllegalStateException {
		super(rs, conn, autoCloseConnection);
	}

	@Override
	protected VeiculoMonitoradoBean montarElemento() throws SQLException {
		
		VeiculoMonitoradoBean vmBean = new VeiculoMonitoradoBean();

		vmBean.setId(rs.getInt("id_veiculo_monitorado"));
		vmBean.setDataHora(rs.getTimestamp("data"));
		vmBean.setIdLocal(rs.getInt("id_local"));
		vmBean.setNomeLocal(rs.getString("nome_local"));
		vmBean.setPista(rs.getInt("pista"));
		vmBean.setPlaca(rs.getString("placa"));
		vmBean.setVelocidade( (int)rs.getFloat("velocidade"));
		vmBean.setIdImagem( rs.getInt("id_imagem"));
		vmBean.setDescricao( rs.getString("descricao"));
		vmBean.setCor(rs.getString("cor"));
		vmBean.setMarca( rs.getString("marca"));
		vmBean.setDataCadastro(rs.getTimestamp("data_cadastro"));

		boolean falsoPositivo = rs.getBoolean("falsoPositivo");
		vmBean.setFalsoPositivo( rs.wasNull() ? null : falsoPositivo );
		
		vmBean.setSerieEquipamento(rs.getInt("serie_equipamento"));
		
		int idImagemLocal = rs.getInt("id_imagem_local");
		vmBean.setIdImagemLocal(rs.wasNull() ? null : idImagemLocal);
		
		Integer idEmail = rs.getInt("id_email_enviar");
		vmBean.setIdEmail(rs.wasNull() ? idEmail : null);

		vmBean.setSituacao(rs.getString("situacao"));
		vmBean.setEmailDestino(rs.getString("email_destino"));
		
		return vmBean;
	}
	
	
}