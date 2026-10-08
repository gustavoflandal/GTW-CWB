package com.consilux.model.remessa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Enquadramento;
import com.consilux.model.Inconsistencia;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

/**
 * Implementação da fábrica de remessas, voltada para a CET
 * @author raoni
 */
public class RemessaFactoryCET extends RemessaFactory {

	@Override
	public CallableStatement getGeraRemessaStatement(Connection conn, Enquadramento enquadramento, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal,
			Date dataRemessa, boolean residual, int infracoes, int id_usuario, Inconsistencia inconsistencia, Integer idRemessaAutomatico) throws ModelException, SQLException, ConfiguracaoException, ConexaoException {

		boolean habilitar_novo_grupo_autuador = false;
		
		try
		{
			String tmp_str = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_novo_grupo_autuador");
			if(tmp_str != null && tmp_str.equalsIgnoreCase("1"))
				habilitar_novo_grupo_autuador = true;
		} catch(Exception e) {}
		
		// O.S 0107 - Novo Enquadramento de Faixa Proibida
		// Adicionado opção, desabilitado por padrão, para habilitar novo grupo autuador
		//
		if (enquadramento.getIdEnquadramento() == 75870 && habilitar_novo_grupo_autuador == false)
		{
			CallableStatement cs = conn.prepareCall("{? = call spu_gera_remessa_faixa(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, etapaProcesso.getId());
			cs.setTimestamp(3, new Timestamp(dataInicial.getTime()));
			cs.setTimestamp(4, new Timestamp(dataFinal.getTime()));
			cs.setDate(5, new java.sql.Date(dataRemessa.getTime()));
			cs.setBoolean(6, residual);
			cs.setInt(7, infracoes);
			cs.setInt(8, id_usuario);
			if (inconsistencia != null) {
				cs.setInt(9, inconsistencia.getIdInconsistencia());
			}else{
				cs.setNull(9, java.sql.Types.INTEGER);
			}
			if (idRemessaAutomatico > 0) {
				cs.setInt(10, idRemessaAutomatico);
			}else{
				cs.setNull(10, java.sql.Types.INTEGER);
			}
			
			return cs;
		}
		
		else
		
		{
			String tipoRemessa = getTipoRemessa(enquadramento.getIdEnquadramento());
			
			CallableStatement cs = null;
			if (inconsistencia != null && inconsistencia.getIdInconsistencia() == 99)
				cs = conn.prepareCall("{? = call spu_gera_remessa_velocidade_100(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			else 
				cs = conn.prepareCall("{? = call spu_gera_remessa(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, enquadramento.getIdEnquadramento());
			cs.setInt(3, etapaProcesso.getId());
			cs.setInt(4, buscaCodigoExterno(conn, tipoRemessa));
			cs.setTimestamp(5, new Timestamp(dataInicial.getTime()));
			cs.setTimestamp(6, new Timestamp(dataFinal.getTime()));
			cs.setString(7, tipoRemessa);
			cs.setDate(8, new java.sql.Date(dataRemessa.getTime()));
			cs.setBoolean(9, residual);
			cs.setInt(10, infracoes);
			cs.setInt(11, id_usuario);
			if (inconsistencia != null) {
				cs.setInt(12, inconsistencia.getIdInconsistencia());
			}else{
				cs.setNull(12, java.sql.Types.INTEGER);
			}
			if (idRemessaAutomatico > 0) {
				cs.setInt(13, idRemessaAutomatico);
			}else{
				cs.setNull(13, java.sql.Types.INTEGER);
			}
			
			return cs;
		}
	}

	
	@Override
	public CallableStatement getGeraRemessaAutomaticoStatement(Connection conn, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal,
			Date dataRemessa, int infracoesPorLote, int id_usuario) throws ModelException, SQLException, ConfiguracaoException, ConexaoException {

		CallableStatement cs = conn.prepareCall("{? = call spu_gera_agendamento_remessa(?, ?, ?, ?, ?, ?)}");
		
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setDate(2, new java.sql.Date(dataRemessa.getTime()));
		cs.setTimestamp(3, new Timestamp(dataInicial.getTime()));
		cs.setTimestamp(4, new Timestamp(dataFinal.getTime()));
		cs.setInt(5, infracoesPorLote);
		cs.setInt(6, etapaProcesso.getId());
		cs.setInt(7, id_usuario);
		
		return cs;
	}


}
