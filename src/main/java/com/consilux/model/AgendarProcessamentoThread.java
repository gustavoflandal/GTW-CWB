package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class AgendarProcessamentoThread extends Thread {

	private static Logger logger = Logger
			.getLogger(AgendarProcessamentoThread.class);
	private Integer idUsuario;
	private Integer etapa;
	private Integer idRemessa;
	
	public AgendarProcessamentoThread(Integer idUsuario, Integer etapa, Integer idRemessa) {
		this.idUsuario = idUsuario;
		this.etapa = etapa;
		this.idRemessa = idRemessa;
	}
	
	@Override
	public void run() {
		Date dt_inicio = Calendar.getInstance().getTime();
		logger.info("Inicio -> Remessa: " + idRemessa + " ; Etapa: " + etapa + " ; Usuario: " + idUsuario);
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			
			/*
			[spu_agendar_processamento]
				2 @id_usuario INT,
				3 @id_processo INT,
				4 @id_remessa INT,
				5 @id_enquadramento INT,
				6 @consistencia BIT,
				7 @espera BIT,
				8 @periodo_ini DATETIME,
				9 @periodo_fim DATETIME		
			 */			
			CallableStatement cs = conn.prepareCall("{? = call spu_agendar_processamento(?, ?, ?, ?, ?, ?, ?, ?)}");
			
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, idUsuario);
			cs.setInt(3, etapa);
			cs.setInt(4, idRemessa);
			cs.setNull(5, Types.INTEGER); 
			cs.setNull(6, Types.INTEGER);
			cs.setNull(7, Types.INTEGER);
			cs.setNull(8, Types.TIMESTAMP);
			cs.setNull(9, Types.TIMESTAMP);
			
			cs.execute();
			
			Integer qtdAgendou = cs.getInt(1);
			logger.info("Agendamento concluído para Movimento [" + idRemessa + "] Infrações agendadas [" + qtdAgendou + "]");
			
		} catch (Exception e) {
			logger.error("Não foi possível agendar processamento!", e);
		}
		finally {
			try {
			if (conn != null)
				conn.close();
			} catch(Exception e) {}
		}
		Date dt_fim = Calendar.getInstance().getTime();
		Long diff_ms = dt_fim.getTime() - dt_inicio.getTime();
		logger.info("Final -> Remessa: " + idRemessa + " tempo: " + diff_ms + " ms");
	}

}
