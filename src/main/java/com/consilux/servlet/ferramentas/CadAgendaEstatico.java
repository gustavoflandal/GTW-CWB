package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;

/**
 * Servlet para a gravação e consulta da Agenda dos Equipamentos Estaticos
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 30/09/2014
 */
public class CadAgendaEstatico extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(CadAgendaEstatico.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; 
		}

		try	{
			String stReferencia = request.getParameter("dtReferencia");
			String strHoraIni = request.getParameter("horaInicio");
			String strHoraFim = request.getParameter("horaFim");
			
			//Validação dos campos da Tela
			if((stReferencia.equals("")) || (strHoraIni.equals("")) || (strHoraFim.equals("")) || (request.getParameter("id_local").equals(""))){
				new Mensagem(response).showErro("Todos os dados devem ser fornecidos para o correto Cadastro!", "javascript:window.close();");
				return;
			}
			
			if((Integer.parseInt(strHoraIni.substring(0, 2)) > 23) || Integer.parseInt(strHoraFim.substring(0, 2)) > 23){
				new Mensagem(response).showErro("Favor informar corretamente as horas de Inicio e Fim! Entre 00h e 23h.", "javascript:window.close();");
				return;
			}
			
			if((Integer.parseInt(strHoraIni.substring(3, 5)) > 59) || Integer.parseInt(strHoraFim.substring(3, 5)) > 59){
				new Mensagem(response).showErro("Favor informar corretamente os minutos de Inicio e Fim! Entre 00min e 59min.", "javascript:window.close();");
				return;
			}
			
			if((strHoraFim.length() != 5) || (strHoraIni.length() != 5)){
				new Mensagem(response).showErro("Favor informar corretamente o horário de inócio e Fim.", "javascript:window.close();");
				return;
			}
			
			//Declarando variaveis
			Long idLocal = (long) 0;
			Date dtReferencia = null;
			Time horaInicio = null;
			Time horaFim = null;
			
			try{
				//divido data em partes 
				String dd = stReferencia.substring(0, 2);  
				String mm = stReferencia.substring(3, 5); 
				String yyyy = stReferencia.substring(6, 10); 
				stReferencia = yyyy + "-" + mm + "-" + dd ; 
				
				idLocal = Long.parseLong(request.getParameter("id_local")); 	
				dtReferencia = new SimpleDateFormat("yyyy-MM-dd").parse(stReferencia);
				
				SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");  
				Date dataTemp = formatador.parse(strHoraIni);  
				horaInicio = new Time(dataTemp.getTime()); 
				dataTemp = formatador.parse(strHoraFim);  
				horaFim = new Time(dataTemp.getTime()); 
				
		    	//Hora Fim não pode ser redondo: ex: 4:00, mas deve ser gravado 03:59:59
		    	Calendar editFim = Calendar.getInstance();
		    	editFim.setTime(horaFim);
		    	editFim.add(Calendar.SECOND, -1);
		    	Date dtTemp = editFim.getTime();
		    	horaFim = new Time(dtTemp.getTime());
			}catch(Exception e)	{
	    		new Mensagem(response).showErro("Favor informar corretamente os dados entrada para Cadastro.", "javascript:window.close();");
	    		return;
			}
	    
	    	Long idAgendaEstatico = consultaEscala(idLocal, dtReferencia, horaInicio, horaFim, true, (long) 0);
			
			if(idAgendaEstatico == 0) {
				
				gravarEscala(idLocal, 
							 dtReferencia, 
							 consultaEscala(idLocal, dtReferencia, horaInicio, horaFim, false, (long) 0),
							 horaInicio, 
							 horaFim, 
							 acesso.getUsuario().getId(),
							 (long) 0);
			}else{
				new Mensagem(response).showErro("Já existe uma escala cadastrada nesse intervalo de Datas e Horários!", "javascript:window.close();");
				return;
			}

			new Mensagem(response).showSucesso("Cadastro realizado com sucesso!", "javascript:window.close();");
			return;
			
		}catch(Exception e)	{
			new Mensagem(response).showErro("Erro ao gravar Agenda do Equipamento Estático!", "javascript:window.close();");

		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; 
		}

		try {
			
			String sAcao = request.getParameter("acao");
			String sIdLocal = request.getParameter("idLocal");
			String sDtRef = request.getParameter("dtRef");
			String sHraini = request.getParameter("hraIni");
			String sHraFim = request.getParameter("hraF");
			String sIdAgendaEstatico = request.getParameter("idAgendaAlteracao");
			String sIdAgendaItem = request.getParameter("idAgendaItemAlteracao");
			
			if(sAcao.equals("desabilitarItem")){
				AtualizaAgendaEstatico(Long.parseLong(sIdLocal), 
									   Long.parseLong(sIdAgendaEstatico), 
									   Long.parseLong(sIdAgendaItem),
									   sDtRef, sHraini, 
									   sHraFim, 
									   response,
									   acesso.getUsuario().getId());
			}
			
			return; 
		} 
		catch (Exception e) {
    		new MensagemJS(response).showErro("Erro ao Consultar Agenda do Equipamento Estático: "+e.getMessage());
    		logger.error("Erro ao Consultar Agenda do Equipamento Estático!", e);
		}

	}
	
	@SuppressWarnings("deprecation")
	public void gravarEscala(Long idLocal, Date dtRef, Long idAgendaEstatico,
							 Time hraIni, Time hraFim, int idUsuario, Long idAgendaItemOrigem) throws ConexaoException, SQLException{
		
		StringBuilder sbSQL = new StringBuilder();
		PreparedStatement ps = null;
		PreparedStatement psItem = null;
		Connection conn = null;
		StringBuilder sbSQLItem = new StringBuilder();
		int sequencia = 1;
		
		try {
			if(idAgendaEstatico == 0){
			
				// Insere no banco agenda pai.
				sbSQL.append(" insert into agenda_estatico ");
				sbSQL.append(" (id_local, data_referencia, id_usuario, data_criacao_agenda)  ");
				sbSQL.append(" values(?, ?, ?, ?) ");
				
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
				Date dtAtual = new Date();
				
				ps.setLong(1, idLocal);
				ps.setDate(2, new java.sql.Date(dtRef.getTime()));
				ps.setLong(3, idUsuario);
				ps.setDate(4, new java.sql.Date(dtAtual.getTime()));
				
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
			    if(rs.next()) {
			    	idAgendaEstatico = rs.getLong(1);
			    }
			}
		    if(idAgendaEstatico > 0){
		    	
		    	Time horaInicio = hraIni;
		    	Time horaFim = hraFim;
		    	Date dtOperacao = dtRef;
		    	//Variaveis para ser usadas caso o dia de operação ultrapasse o dia de referencia (Ex: 22:00 as 4:00)
		    	Time horaInicioDiaSeguinte = null;
		    	Time horaFimDiaSeguinte = hraFim;
		    	Date dtOperacoDiaSeguinte = null;
		    	
		    	if (hraFim.compareTo(hraIni) < 0){
		    		sequencia = 2;
		    		horaFim = new Time(23, 59, 59);
		    		horaInicioDiaSeguinte = new Time(0, 0, 0);
		    		
		    		Calendar cal = Calendar.getInstance();
		    		cal.setTime(dtRef);
		    		cal.add(Calendar.DAY_OF_MONTH, 1); 
		    		dtOperacoDiaSeguinte = cal.getTime();
		    	}
		    	
				// Insere no banco agenda de Item/Filho.
				sbSQLItem.append("insert into agenda_estatico_item ");
				sbSQLItem.append("(id_agenda_estatico, data_operacao, hora_inicio, hora_fim, horas_funcionamento, sequencia, status, id_agenda_item_origem)  ");
				sbSQLItem.append("values(?, ?, ?, ?, null, ?, 1, ?) ");
				
				conn = Conexao.getConexao();
				psItem = conn.prepareStatement(sbSQLItem.toString());
				psItem.setLong(1, idAgendaEstatico );
				psItem.setDate(2, new java.sql.Date(dtOperacao.getTime()));
				psItem.setTime(3, new java.sql.Time(horaInicio.getTime()));
				psItem.setTime(4, new java.sql.Time(horaFim.getTime()));
				psItem.setInt (5, sequencia);
				if(idAgendaItemOrigem > 0)
					psItem.setLong(6, idAgendaItemOrigem);
				else
					psItem.setNull(6, Types.NULL);
				
				psItem.execute();
				
				if (hraFim.compareTo(hraIni) < 0){
					StringBuilder sbSQLItemDiaSeguinte = new StringBuilder();
					
					// Insere no banco agenda de Item/Filho.
					sbSQLItemDiaSeguinte.append("insert into agenda_estatico_item ");
					sbSQLItemDiaSeguinte.append("(id_agenda_estatico, data_operacao, hora_inicio, hora_fim, horas_funcionamento, sequencia, status, id_agenda_item_origem)  ");
					sbSQLItemDiaSeguinte.append("values(?, ?, ?, ?, null, ?, 1, ?) ");
					
					psItem = conn.prepareStatement(sbSQLItemDiaSeguinte.toString());
					psItem.setLong (1, idAgendaEstatico );
					psItem.setDate(2, new java.sql.Date(dtOperacoDiaSeguinte.getTime()));
					psItem.setTime(3, new java.sql.Time(horaInicioDiaSeguinte.getTime()));
					psItem.setTime(4, new java.sql.Time(horaFimDiaSeguinte.getTime()));
					psItem.setInt (5, sequencia);
					if(idAgendaItemOrigem > 0)
						psItem.setLong(6, idAgendaItemOrigem);
					else
						psItem.setNull(6, Types.NULL);
					
					psItem.execute();
				}
		    }

		} catch (SQLException e) {
			e.printStackTrace();
		
		} finally {
			if (ps != null)
				ps.close();
				psItem.close();
			if (conn != null)
				conn.close();							
		}
	}
	
	
	public Long consultaEscala(Long idLocal, Date dtRef, 
								Time hraIni, Time hraFim,
								boolean validaIntervalo,
								Long idAgendaItemAlteracao) throws SQLException, ConexaoException{
		
		StringBuilder sbSQL = new StringBuilder();
		Long ret = (long) 0;
		
		sbSQL.append(" declare @horaIni as Time, ");
		sbSQL.append(" 		@horaFim as Time, ");
		sbSQL.append(" 		@DtRef as Date, ");
		sbSQL.append("		@local as Int ");

		sbSQL.append(" set @horaIni = ? ");
		sbSQL.append(" set @horaFim = ? ");
		sbSQL.append(" set @DtRef = ? ");
		sbSQL.append(" set @local = ? ");

		sbSQL.append(" 	select ");
		sbSQL.append(" 		ae.id_agenda_estatico, ");
		sbSQL.append(" 		ae.id_local, ");
		sbSQL.append(" 		ae.data_referencia, ");
		sbSQL.append(" 		ai.id_agenda_item, ");
		sbSQL.append(" 		ai.data_operacao, ");
		sbSQL.append(" 		ai.hora_inicio, ");
		sbSQL.append(" 		ai.hora_fim, ");
		sbSQL.append(" 		ai.sequencia ");
		sbSQL.append(" 	from agenda_estatico ae ");
		sbSQL.append(" 	inner join agenda_estatico_item ai ");
		sbSQL.append(" 		on ae.id_agenda_estatico = ai.id_agenda_estatico ");
		sbSQL.append(" where cast(data_referencia as date) = @DtRef ");
		sbSQL.append("       and ae.id_local = @local" );	
		
		if(validaIntervalo){
			sbSQL.append(" and  ");
			sbSQL.append(" 	( ");
			sbSQL.append(" 		(@horaIni between hora_inicio and hora_fim OR @horaFim between hora_inicio and hora_fim) ");
			sbSQL.append(" 		OR  ");
			sbSQL.append(" 		(hora_inicio between @horaIni and @horaFim OR hora_fim between @horaIni and @horaFim) ");
			sbSQL.append(" 	) ");
		}
		
		//Consulta os casos de alteação de agenda, sem considerar a propria que está sendo alterada E quando é quebrada em duas partes 
		if(idAgendaItemAlteracao > 0){
			sbSQL.append(" 	  and ai.id_agenda_item not in ");
			sbSQL.append(" 		 ( ");
			sbSQL.append(" 	  		select   ");
			sbSQL.append(" 				ae.id_agenda_estatico ");
			sbSQL.append(" 			from agenda_estatico ae  ");
			sbSQL.append(" 				inner join agenda_estatico_item aei  ");
			sbSQL.append(" 					on aei.id_agenda_estatico = ae.id_agenda_estatico  ");
			sbSQL.append(" 			where aei.sequencia = 2  ");
			sbSQL.append(" 				  and aei.id_agenda_estatico = ?  ");
			sbSQL.append(" 		) ");
			sbSQL.append(" 	  and ai.id_agenda_item not in (?) ");
			sbSQL.append(" 	  and ai.status = 1 ");
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTime(1, new java.sql.Time(hraIni.getTime()));
			ps.setTime(2, new java.sql.Time(hraFim.getTime()));
			ps.setDate(3, new java.sql.Date(dtRef.getTime()));
			ps.setLong(4, idLocal);
			if(idAgendaItemAlteracao > 0){
				ps.setLong(5, idAgendaItemAlteracao);
				ps.setLong(6, idAgendaItemAlteracao);
			}
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				ret = rs.getLong("id_agenda_estatico");
			}
			
			return ret;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	private void AtualizaAgendaEstatico(Long idLocal, Long idAgendaAlteracao, Long idAgendaItemAlteracao,
										String dtRef, String hraIni, String hraFim,
										HttpServletResponse response,
										int idUsuario) throws Exception {
	
		StringBuilder sbSQL = new StringBuilder();
		StringBuilder sbSQLAlter = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			if(!validarAlteracao(hraIni, hraFim, response)) return;
		
			//divido data em partes 
			String dd = dtRef.substring(0, 2);  
			String mm = dtRef.substring(3, 5); 
			String yyyy = dtRef.substring(6, 10); 
			dtRef = yyyy + "-" + mm + "-" + dd ; 
			
			Date dtReferencia = new SimpleDateFormat("yyyy-MM-dd").parse(dtRef);
			SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");  
			Date dataTemp = formatador.parse(hraIni);  
			Time horaInicio = new Time(dataTemp.getTime()); 
			dataTemp = formatador.parse(hraFim);  
			Time horaFim = new Time(dataTemp.getTime()); 
			
			
	    	if (consultaEscala(idLocal, 
	    					   dtReferencia, 
	    						horaInicio, 
	    						horaFim, 
	    						true,
	    						idAgendaItemAlteracao) > 0)
	    	{
				mostraMensagemUsuario(response,"Já existe uma escala cadastrada nesse intervalo de Datas e Horários!");
				return;
			}
			
			//Caso tenha valores em sequencia deve-se desabilitar os dois itens
			sbSQL.append(" 		declare @idAgenda int, ");
			sbSQL.append(" 				@duplicado int, ");
			sbSQL.append(" 				@idItem int ");
			
			sbSQL.append(" 		set @idItem = ? ");
			sbSQL.append(" 		set @idAgenda = ( ");
			sbSQL.append(" 							select  ");
			sbSQL.append(" 								id_agenda_estatico  ");
			sbSQL.append(" 							from agenda_estatico_item  ");
			sbSQL.append(" 							where id_agenda_item = @idItem ");
			sbSQL.append(" 								  and status = 1	 ");
			sbSQL.append(" 						) ");
			sbSQL.append(" 		set @duplicado = ( ");
			sbSQL.append(" 							select   ");
			sbSQL.append(" 								sequencia  ");
			sbSQL.append(" 							from agenda_estatico_item  ");
			sbSQL.append(" 							where id_agenda_item = @idItem ");
			sbSQL.append(" 								  and status = 1	 ");
			sbSQL.append(" 						) ");
			
			sbSQL.append(" 		IF(@duplicado = 2) ");
			sbSQL.append(" 		BEGIN ");
			sbSQL.append(" 			select  ");
			sbSQL.append(" 				aei.id_agenda_estatico,  ");
			sbSQL.append(" 				aei.id_agenda_item ");
			sbSQL.append(" 			from agenda_estatico ae ");
			sbSQL.append(" 				inner join agenda_estatico_item aei ");
			sbSQL.append(" 					on aei.id_agenda_estatico = ae.id_agenda_estatico ");
			sbSQL.append(" 			where  ");
			sbSQL.append(" 				aei.id_agenda_estatico = @idAgenda ");
			sbSQL.append(" 				and aei.sequencia = @duplicado ");
			sbSQL.append(" 		END ");
			
			sbSQL.append(" 		ELSE ");
			sbSQL.append(" 		BEGIN ");
			sbSQL.append(" 			select  ");
			sbSQL.append(" 				aei.id_agenda_estatico,  ");
			sbSQL.append(" 				aei.id_agenda_item "); 
			sbSQL.append(" 			from agenda_estatico ae ");
			sbSQL.append(" 				inner join agenda_estatico_item aei ");
			sbSQL.append(" 					on aei.id_agenda_estatico = ae.id_agenda_estatico ");
			sbSQL.append(" 			where  ");
			sbSQL.append(" 				aei.id_agenda_item = @idItem ");
			sbSQL.append(" 		END ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idAgendaItemAlteracao);
			
			rs = ps.executeQuery();
			
			Long idAgendaItem = (long) 0;
			while (rs.next()){
				
				sbSQLAlter = null;
				ps = null;
				sbSQLAlter = new StringBuilder();
				idAgendaItem = rs.getLong("id_agenda_item"); 
				Timestamp dtNow = new Timestamp(System.currentTimeMillis());  
				
				sbSQLAlter.append(" 	  update agenda_estatico_item  ");
				sbSQLAlter.append(" 	  	set status = 0, ");
				sbSQLAlter.append(" 	  	    id_usuario_alter = ?, ");
				sbSQLAlter.append(" 	  	    dt_ult_alteracao = ? ");
				sbSQLAlter.append(" 	  where id_agenda_item = ? ");
				
				ps = conn.prepareStatement(sbSQLAlter.toString());
				ps.setLong(1, idUsuario);
				ps.setTimestamp(2, new java.sql.Timestamp(dtNow.getTime()));
				ps.setLong(3, idAgendaItem);
				
				ps.execute();
			}
			
	    	//Hora Fim não pode ser redondo: ex: 4:00, mas deve ser gravado 03:59:59
	    	Calendar editFim = Calendar.getInstance();
	    	editFim.setTime(horaFim);
	    	editFim.add(Calendar.SECOND, -1);
	    	Date dtTemp = editFim.getTime();
	    	horaFim = new Time(dtTemp.getTime());
	    	
			gravarEscala(idLocal, 
					 dtReferencia, 
					 consultaEscala(idLocal, dtReferencia, horaInicio, horaFim, false, idAgendaItemAlteracao),
					 horaInicio, 
					 horaFim, 
					 idUsuario,
					 idAgendaItem);
			
			mostraMensagemUsuario(response,"Alteração realizada com sucesso! Local: " + idLocal + ", Horário início: " + horaInicio + ", Hora Fim: " + horaFim + 
										   ". Esta janela já pode ser fechada!");
			
		}catch(Exception ex){
			logger.error("Erro ao entregar XML de resposta.", ex);
			mostraMensagemUsuario(response,"A alteração da agenda não pode ser completada por erros" +
											   " de processamento. Favor entrar em contato com a" +
											   " administração do sistema.");
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	private void mostraMensagemUsuario(HttpServletResponse response, String mensagem) {
		AjaxXMLConstr xml;
		try {
			xml = new AjaxXMLConstr("ErrosAgenda");
			xml.adicCampo("RETORNO", mensagem);
			xml.dump(response);
		}
		catch (Exception e) {
			logger.error("Erro ao entregar XML de resposta.", e);
		}
	}
	
	private boolean validarAlteracao(String strHoraIni, String strHoraFim, HttpServletResponse response){
		
		boolean ret = true;
		
		try{
			if((Integer.parseInt(strHoraIni.substring(0, 2)) > 23) || Integer.parseInt(strHoraFim.substring(0, 2)) > 23){
				mostraMensagemUsuario(response,"As horas de fucionamento estão fora do padrão. Favor verificar!");
				ret=false;
			}
			
			if((Integer.parseInt(strHoraIni.substring(3, 5)) > 59) || Integer.parseInt(strHoraFim.substring(3, 5)) > 59){
				mostraMensagemUsuario(response,"As horas de fucionamento estão fora do padrão. Favor verificar!");
				ret=false;
			}
			
			if((strHoraFim.length() != 5) || (strHoraIni.length() != 5)){
				mostraMensagemUsuario(response,"As horas de fucionamento estão fora do padrão. Favor verificar!");
				ret=false;
			}
			
			return ret;
			
		}catch(Exception ex){
			logger.error("Erro na validação das horas de funcionamento.", ex);
			mostraMensagemUsuario(response,"As horas de fucionamento estão fora do padrão. Favor verificar!");
			return false;
		}
	}
	

	
	
	
}
