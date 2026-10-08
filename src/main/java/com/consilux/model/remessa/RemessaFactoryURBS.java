package com.consilux.model.remessa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Enquadramento;
import com.consilux.model.Inconsistencia;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

/**
 * Implementação da fábrica de remessas, voltada para a URBS
 * @author raoni
 */
public class RemessaFactoryURBS extends RemessaFactory {

	@Override
	public CallableStatement getGeraRemessaStatement(Connection conn,
		Enquadramento enquadramento, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, Date dataRemessa, boolean residual, int infracoes, int id_usuario,
			Inconsistencia inconsistencia, Integer idRemessaAutomatico) throws ModelException, SQLException, ConfiguracaoException, ConexaoException {
		
		String tipoRemessa = getTipoRemessa(enquadramento.getIdEnquadramento());
		
		CallableStatement cs = conn.prepareCall(
			"{? = call spu_gera_remessa_urbs(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
		
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(2, etapaProcesso.getId());
		cs.setInt(3, buscaCodigoExterno(conn, tipoRemessa));
		cs.setTimestamp(4, new Timestamp(dataInicial.getTime()));
		cs.setTimestamp(5, new Timestamp(dataFinal.getTime()));
		cs.setString(6, tipoRemessa);
		cs.setDate(7, new java.sql.Date(dataRemessa.getTime()));
		cs.setBoolean(8, residual);
		cs.setInt(9, infracoes);
		cs.setInt(10, id_usuario);
		
		return cs;
		
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
