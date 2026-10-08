package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.AgendamentoProcessamentoBean;

/**
 * Classe de negócio responsável pela persistência de
 * "agendamento de processamento" (direto) no banco de dados.
 * 
 * @author raoni
 */
public class AgendamentoProcessamento {

	/**
	 * Lista todos os agendamentos presentes no banco de dados.
	 * 
	 * @return uma lista.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<AgendamentoProcessamentoBean> listarTodos()
			throws ConexaoException, SQLException {

		List<AgendamentoProcessamentoBean> lRet = new ArrayList<AgendamentoProcessamentoBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("    id_infracao, ");
		sbSQL.append("    id_processo, ");
		sbSQL.append("    data_requisicao, ");
		sbSQL.append("    id_usuario, ");
		sbSQL.append("    status_agendamento, ");
		sbSQL.append("    id_inconsistencia, ");
		sbSQL.append("    msg_erro ");
		sbSQL.append("FROM ");
		sbSQL.append("    agendamento_processamento ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				AgendamentoProcessamentoBean bean = new AgendamentoProcessamentoBean();

				bean.setIdInfracao(rs.getInt("id_infracao"));
				bean.setIdProcesso(rs.getInt("id_processo"));
				bean.setDataRequisicao(new Date(rs.getTimestamp(
						"data_requisicao").getTime()));
				bean.setIdUsuario(rs.getInt("id_usuario"));
				bean.setStatusAgendamento(rs.getInt("status_agendamento"));

				// Campos que podem vir nulos do banco:
				Integer idInconsistencia = rs.getInt("id_inconsistencia");
				if (rs.wasNull()) {
					idInconsistencia = null;
				}

				bean.setIdInconsistencia(idInconsistencia);

				String msgErro = rs.getString("msg_erro");
				if (rs.wasNull())
					msgErro = null;

				bean.setMsgErro(msgErro);
				lRet.add(bean);
			}
		} finally {
			if (conn != null)
				conn.close();
		}

		return lRet;
	}

	@Deprecated
	public static List<AgendamentoProcessamentoBean> listarTodos(boolean consistentes)
			throws ConexaoException, SQLException {

		List<AgendamentoProcessamentoBean> lRet = new ArrayList<AgendamentoProcessamentoBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT TOP 10000 ");
		sbSQL.append("    ap.id_infracao, ");
		sbSQL.append("    ap.id_processo, ");
		sbSQL.append("    ap.data_requisicao, ");
		sbSQL.append("    ap.id_usuario, ");
		sbSQL.append("    ap.status_agendamento, ");
		sbSQL.append("    ap.id_inconsistencia, ");
		sbSQL.append("    ap.msg_erro ");
		sbSQL.append("FROM ");
		sbSQL.append("    agendamento_processamento ap (NOLOCK) ");
		sbSQL.append("JOIN infracao inf (NOLOCK) ON ap.id_infracao = inf.id_infracao ");
		sbSQL.append("WHERE inf.id_inconsistencia " + (consistentes ? "=" : ">") + " 0");
		sbSQL.append(" OR ap.id_processo = 11");
		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				AgendamentoProcessamentoBean bean = new AgendamentoProcessamentoBean();

				bean.setIdInfracao(rs.getInt("id_infracao"));
				bean.setIdProcesso(rs.getInt("id_processo"));
				bean.setDataRequisicao(new Date(rs.getTimestamp(
						"data_requisicao").getTime()));
				bean.setIdUsuario(rs.getInt("id_usuario"));
				bean.setStatusAgendamento(rs.getInt("status_agendamento"));

				// Campos que podem vir nulos do banco:
				Integer idInconsistencia = rs.getInt("id_inconsistencia");
				if (rs.wasNull()) {
					idInconsistencia = null;
				}

				bean.setIdInconsistencia(idInconsistencia);

				String msgErro = rs.getString("msg_erro");
				if (rs.wasNull())
					msgErro = null;

				bean.setMsgErro(msgErro);
				lRet.add(bean);
			}
		} finally {
			if (conn != null)
				conn.close();
		}

		return lRet;
	}
}
