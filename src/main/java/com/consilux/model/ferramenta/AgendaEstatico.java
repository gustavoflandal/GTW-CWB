package com.consilux.model.ferramenta;

/**********************************************************************************
Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Luiz Amaral
Data: 02/10/2014
*********************************************************************************/

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
* Classe de negócio para popula objeto de AgendaEstatico
* @author Luiz Fernando Amaral - Consilux Tecnologia
* Data: 02/10/2014
*/
public  class AgendaEstatico implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Long idAgendaEstatico;
	Date dtCadastro;
	Date dtReferencia;
	Long idLocal;
	String deescricao;
	Date criacao;
	AgendaEstaticoItem agendaItem;
	String operador;

	public AgendaEstatico(Long idAgenda, Date dtRef, Long idLocal, 
						  String ds, Date dtCri, String op, AgendaEstaticoItem agenda){
		this.dtReferencia = dtRef;
		this.idLocal = idLocal;
		this.deescricao = ds;
		this.criacao = dtCri;
		this.agendaItem = agenda;
		this.idAgendaEstatico = idAgenda;
		this.operador = op;
	}
	
	public AgendaEstatico(){
		
	}
	
	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public Long getIdAgendaEstatico() {
		return idAgendaEstatico;
	}

	public void setIdAgendaEstatico(Long idAgendaEstatico) {
		this.idAgendaEstatico = idAgendaEstatico;
	}

	public AgendaEstaticoItem getAgendaItem() {
		return agendaItem;
	}

	public void setAgendaItem(AgendaEstaticoItem agendaItem) {
		this.agendaItem = agendaItem;
	}
	
	public Date getCriacao() {
		return criacao;
	}

	public void setCriacao(Date criacao) {
		this.criacao = criacao;
	}
	public String getDeescricao() {
		return deescricao;
	}

	public void setDeescricao(String deescricao) {
		this.deescricao = deescricao;
	}
	
	public Date getDtCadastro() {
		return dtCadastro;
	}
	public void setDtCadastro(Date dtCadastro) {
		this.dtCadastro = dtCadastro;
	}
	public Date getDtReferencia() {
		return dtReferencia;
	}
	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}
	public Long getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}
	
	public ArrayList<AgendaEstatico> consultaEscala(String sLocal, String stReferencia) throws SQLException, ConexaoException, ParseException{

		StringBuilder sbSQL = new StringBuilder();
		boolean blnDtRef = true;
		Long idLocal = Long.parseLong(sLocal);
		
		if(stReferencia.equals("")){
			blnDtRef = false;
		}else{
			String dd = stReferencia.substring(0, 2);  
			String mm = stReferencia.substring(3, 5); 
			String yyyy = stReferencia.substring(6, 10); 
			stReferencia = yyyy + "-" + mm + "-" + dd ; 
			dtReferencia = new SimpleDateFormat("yyyy-MM-dd").parse(stReferencia);
		}
		
		sbSQL.append(" declare @horaIni as Time, ");
		sbSQL.append(" 		@horaFim as Time, ");
		sbSQL.append(" 		@DtRef as Date, ");
		sbSQL.append("		@local as Int ");
		
		sbSQL.append(" set @local = ? ");
		if(blnDtRef)
			sbSQL.append(" set @DtRef = ? ");
		
		sbSQL.append(" 	select");
		sbSQL.append(" 		ae.id_agenda_estatico, ");
		sbSQL.append(" 		ae.id_local, ");
		sbSQL.append(" 		cem.descricao, ");		
		sbSQL.append(" 		ae.data_referencia, ");
		sbSQL.append(" 		ai.id_agenda_item, ");
		sbSQL.append(" 		ai.data_operacao, ");
		sbSQL.append(" 		ae.data_criacao_agenda, ");
		sbSQL.append(" 		ai.hora_inicio, ");
		sbSQL.append(" 		ai.hora_fim, ");
		sbSQL.append(" 		ai.sequencia, ");
		sbSQL.append(" 		ai.status, ");
		sbSQL.append("      (cast(ae.id_usuario as varchar(5)) + ' - ' + us.nome) as operador ");
		sbSQL.append(" 	from agenda_estatico ae ");
		sbSQL.append(" 	inner join agenda_estatico_item ai ");
		sbSQL.append(" 		on ae.id_agenda_estatico = ai.id_agenda_estatico ");
		sbSQL.append("	inner join sis_usuario us "); 
		sbSQL.append("		on us.id_usuario = ae.id_usuario "); 
		sbSQL.append(" 		inner join  ");
		sbSQL.append(" 		( select  ");
		sbSQL.append(" 			max(c.cod_Pista_alternativo) as pista,  ");
		sbSQL.append(" 			c.descricao, ");
		sbSQL.append(" 			c.id_local ");
		sbSQL.append(" 		  from configuracao_equipamento_medicao c ");
		sbSQL.append(" 		   group by  ");
		sbSQL.append(" 			c.id_local, ");
		sbSQL.append(" 			c.descricao ");
		sbSQL.append(" 		) as cem ");
		sbSQL.append(" 		on cem.id_local = ae.id_local 	 ");	
		sbSQL.append(" where ae.id_local = @local ");
		if(blnDtRef)
			sbSQL.append(" AND cast(data_referencia as date) = @DtRef ");
		
		sbSQL.append(" order by  ae.data_referencia desc, ai.sequencia, ai.status desc, ai.hora_inicio, ai.data_operacao  ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<AgendaEstatico> listAgendaEstatico = new ArrayList<AgendaEstatico>();
		AgendaEstatico agenda = null;
		AgendaEstaticoItem item = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idLocal);
			if(blnDtRef)
				ps.setDate(2, new java.sql.Date(dtReferencia.getTime()));
	
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				item = new AgendaEstaticoItem(
												 rs.getDate("data_operacao"), 
												 rs.getLong("id_agenda_estatico"),
												 rs.getLong("id_agenda_item"),
												 rs.getTime("hora_inicio"),
												 rs.getTime("hora_fim"),
												 rs.getInt("status")
											 );
			
				agenda = new AgendaEstatico(
										    rs.getLong("id_agenda_estatico"),
											rs.getDate("data_referencia"), 
											rs.getLong("id_local"),
											rs.getString("descricao"),
											rs.getDate("data_criacao_agenda"),
											rs.getString("operador"),
											item
										  );	
			
				listAgendaEstatico.add(agenda);
			}
			
			return listAgendaEstatico;
		}
		catch (Exception e) {
			if (conn != null)
				conn.close();
    		e.printStackTrace();
    		return null;
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	

}