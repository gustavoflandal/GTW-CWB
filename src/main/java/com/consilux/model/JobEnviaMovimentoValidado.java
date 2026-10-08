package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.Conexao;
import com.consilux.servlet.remessa.ExportarRemessaTarefa;
import com.consilux.servlet.remessa.ExportarRemessaTarefa.TipoExportaRemessa;

@DisallowConcurrentExecution
public class JobEnviaMovimentoValidado implements Job {

	private static Logger logger = Logger
			.getLogger(JobEnviaMovimentoValidado.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("XXX> Iniciando JobEnviaMovimentoValidado Sem ExecutorService");
		
		try {
			String diretorio_destino = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_apait");
			
			if(diretorio_destino == null || diretorio_destino.trim().length() == 0) {
				logger.error("Diretório do APAIT não informado!");
				return;
			}
			
			Integer finalizado = FinalizarRemessasValidadas();
			logger.info("Finalizado [" + finalizado + "] Movimentos");

			List<Integer> remessas_validadas = ObterRemessasValidadas(40);
			logger.info("Remessas a serem enviadas: " + remessas_validadas);
			
			for(Integer idRemessa : remessas_validadas) {
				
				Remessa remessa = Remessa.buscarRemessaPorId(idRemessa);
				
				if(remessa.getIntegridade()) {
					logger.info("Enviando Movimento [" + remessa.getDescricao() + "]");
					ExportarRemessaTarefa ert = new ExportarRemessaTarefa(remessa, TipoExportaRemessa.LoteValidado);
					ert.EnviarAPAITAutomatico();
				}
			}

			
		} catch (Exception ex) {
			logger.error("Erro ao executar JobEnviaMovimentoValidado", ex);
		} 

		logger.info("XXX> JobEnviaMovimentoValidado finalizado.");
	}

	private List<Integer> ObterRemessasValidadas(int quantidade) {

		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		
		if (quantidade > 0)
			sbSQL.append("TOP(" + quantidade + ")");
		
		sbSQL.append(" id_remessa FROM remessa (NOLOCK) ");
		sbSQL.append(" WHERE data_validacao IS NOT NULL ");
		sbSQL.append(" AND data_confirmacao IS NULL ");
		sbSQL.append(" ORDER BY data_inicial ");

		Connection conn = null;

		List<Integer> lista_remessas = new ArrayList<Integer>();

		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				lista_remessas.add(rs.getInt(1));
		} catch (Exception ex) {
			logger.error("Erro ao executar ObterRemessasValidadas.", ex);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return lista_remessas;
	}

	public static int FinalizarRemessasValidadas() {

		Connection conn = null;
		Integer finalizado = 0;

		try {
			conn = Conexao.getConexao();

			CallableStatement cs = conn
					.prepareCall("{ ? = call spu_finaliza_remessas_validadas }");
			cs.registerOutParameter(1, Types.INTEGER);

			cs.execute();

			finalizado = cs.getInt(1);

		} catch (Exception e) {
			logger.error("Não foi possível finalizar movimentos validados!");
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return finalizado;
	}

}
