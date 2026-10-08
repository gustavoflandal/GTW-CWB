package com.consilux.model.relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Relatorio;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.ui.client.beans.AnaliseAutuacoesGwtBean;
import com.google.common.collect.Lists;

public class AnaliseAutuacoes extends Relatorio {

	@Override
	protected void constroi(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void finaliza() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ResultSet montaRel() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static List<AnaliseAutuacoesGwtBean> geraAnaliseAutuacoes(Map<String,Object> params){
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("select  \n");
		sbSQL.append("	id_enquadramento, \n");
		sbSQL.append("	nome as processo, \n");
		sbSQL.append("	estado, \n");
		sbSQL.append("	sum(total) as total \n");
		sbSQL.append("from  \n");
		sbSQL.append("( \n");
		sbSQL.append("	select \n");
		sbSQL.append("		case  \n");
		sbSQL.append("			when t.id_processo = 1 then 1 \n");
		sbSQL.append("			when t.id_processo = 2 then 2 \n");
		sbSQL.append("			when t.id_processo = 11 then 3 \n");
		sbSQL.append("			when t.id_processo = 3 then 4 \n");
		sbSQL.append("		end as ordem_processo, \n");
		sbSQL.append("		t.id_processo, \n");
		sbSQL.append("		id_enquadramento, \n");
		sbSQL.append("		p.nome, \n");
		sbSQL.append("		estado, \n");
		sbSQL.append("		ordem_estado, \n");
		sbSQL.append("		sum(total) as total \n");
		sbSQL.append("	from  \n");
		sbSQL.append("		( \n");
		sbSQL.append("			select \n");
		sbSQL.append("				i.id_infracao, \n");
		sbSQL.append("				i.id_enquadramento, \n");
		sbSQL.append("				'inconsistidas' as estado, \n");
		sbSQL.append("				2 as ordem_estado, \n");
		sbSQL.append("				1 as total, \n");
		sbSQL.append("				ipc.id_processo \n");
		sbSQL.append("			from \n");
		sbSQL.append("				infracao i with (NOLOCK) \n");
		sbSQL.append("				left join infracao_processo_concluido ipc with (NOLOCK) on (ipc.id_infracao = i.id_infracao) \n");
		sbSQL.append("				left join processo p with (NOLOCK) on (p.id_processo = ipc.id_processo) \n");
		sbSQL.append("				 \n");
		sbSQL.append("			where \n");
		sbSQL.append("				p.ativo = 'True' and \n");
		sbSQL.append("				ipc.data_conclusao between '{data_inicial}' and '{data_final}' and \n");
		sbSQL.append("				({id_local} = 0 or i.id_local = {id_local}) and \n");
		sbSQL.append("				({pista} = 0 or i.pista = {pista}) and \n");
		sbSQL.append("				(ipc.id_processo in (1,2,3,11) ) and \n");
		sbSQL.append("				ipc.id_inconsistencia <> 0 \n");
		sbSQL.append("				and i.id_infracao in ( \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {digitador} or id_usuario = {digitador} ) \n");
		sbSQL.append("								INTERSECT \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {auditor} or id_usuario = {auditor} ) \n");
		sbSQL.append("								group by \n");
		sbSQL.append("									id_infracao \n");
		sbSQL.append("								) \n");
		sbSQL.append("			UNION \n");
		sbSQL.append("			select \n");
		sbSQL.append("				i.id_infracao, \n");
		sbSQL.append("				i.id_enquadramento, \n");
		sbSQL.append("				'consistidas' as estado, \n");
		sbSQL.append("				1 as ordem_estado, \n");
		sbSQL.append("				1 as total, \n");
		sbSQL.append("				ipc.id_processo \n");
		sbSQL.append("			from \n");
		sbSQL.append("				infracao i with (NOLOCK) \n");
		sbSQL.append("				left join infracao_processo_concluido ipc with (NOLOCK) on (ipc.id_infracao = i.id_infracao) \n");
		sbSQL.append("				left join processo p with (NOLOCK) on (p.id_processo = ipc.id_processo) \n");
		sbSQL.append("				 \n");
		sbSQL.append("			where \n");
		sbSQL.append("				p.ativo = 'True' and \n");
		sbSQL.append("				ipc.data_conclusao between '{data_inicial}' and '{data_final}' and \n");
		sbSQL.append("				({id_local} = 0 or i.id_local = {id_local}) and \n");
		sbSQL.append("				({pista} = 0 or i.pista = {pista}) and \n");
		sbSQL.append("				(ipc.id_processo in (1,2,3,11) ) and \n");
		sbSQL.append("				(ipc.id_inconsistencia = 0 or ipc.id_inconsistencia is null) \n");
		sbSQL.append("				and i.id_infracao in ( \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {digitador} or id_usuario = {digitador} ) \n");
		sbSQL.append("								INTERSECT \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {auditor} or id_usuario = {auditor} ) \n");
		sbSQL.append("								group by \n");
		sbSQL.append("									id_infracao \n");
		sbSQL.append("								 \n");
		sbSQL.append("								) \n");
		sbSQL.append("			UNION \n");
		sbSQL.append("			select \n");
		sbSQL.append("				i.id_infracao, \n");
		sbSQL.append("				i.id_enquadramento, \n");
		sbSQL.append("				'divergencias' as estado, \n");
		sbSQL.append("				3 as ordem_estado, \n");
		sbSQL.append("				1 as total, \n");
		sbSQL.append("				ipc.id_processo \n");
		sbSQL.append("			from \n");
		sbSQL.append("				infracao i with (NOLOCK) \n");
		sbSQL.append("				left join infracao_processo_concluido ipc with (NOLOCK) on (ipc.id_infracao = i.id_infracao) \n");
		sbSQL.append("				left join processo p with (NOLOCK) on (p.id_processo = ipc.id_processo) \n");
		sbSQL.append("			where \n");
		sbSQL.append("				p.ativo = 'True' and \n");
		sbSQL.append("				ipc.data_conclusao between '{data_inicial}' and '{data_final}' and \n");
		sbSQL.append("				({id_local} = 0 or i.id_local = {id_local}) and \n");
		sbSQL.append("				({pista} = 0 or i.pista = {pista}) and \n");
		sbSQL.append("				(ipc.id_processo in (1,2,3,11) ) and \n");
		sbSQL.append("				(select id_inconsistencia from infracao_processo_concluido where id_infracao = i.id_infracao and id_processo = p.id_processo_anterior) <> ipc.id_inconsistencia \n");
		sbSQL.append("				and i.id_infracao in ( \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {digitador} or id_usuario = {digitador} ) \n");
		sbSQL.append("								INTERSECT \n");
		sbSQL.append("								select  \n");
		sbSQL.append("									id_infracao  \n");
		sbSQL.append("								from  \n");
		sbSQL.append("									infracao_processo with (NOLOCK) \n");
		sbSQL.append("								where  \n");
		sbSQL.append("									( 0 = {auditor} or id_usuario = {auditor} ) \n");
		sbSQL.append("								group by \n");
		sbSQL.append("									id_infracao \n");
		sbSQL.append("								) \n");
		sbSQL.append("		) as t  \n");
		sbSQL.append("		left join processo p with (NOLOCK) on (p.id_processo = t.id_processo) \n");
		sbSQL.append("	group by \n");
		sbSQL.append("		id_enquadramento, \n");
		sbSQL.append("		t.id_processo, \n");
		sbSQL.append("		p.nome, \n");
		sbSQL.append("		estado, \n");
		sbSQL.append("		ordem_estado \n");
		sbSQL.append("	UNION \n");
		sbSQL.append("	select  \n");
		sbSQL.append("		case  \n");
		sbSQL.append("			when id_processo = 1 then 1 \n");
		sbSQL.append("			when id_processo = 2 then 2 \n");
		sbSQL.append("			when id_processo = 11 then 3 \n");
		sbSQL.append("			when id_processo = 3 then 4 \n");
		sbSQL.append("		end as ordem_processo, \n");
		sbSQL.append("		id_processo, \n");
		sbSQL.append("		id_enquadramento,  \n");
		sbSQL.append("		nome, \n");
		sbSQL.append("		estado, \n");
		sbSQL.append("		ordem_estado, \n");
		sbSQL.append("		total \n");
		sbSQL.append("	from \n");
		sbSQL.append("	( \n");
		sbSQL.append("		select  \n");
		sbSQL.append("			id_processo, \n");
		sbSQL.append("			e.id_enquadramento,  \n");
		sbSQL.append("			p.nome, \n");
		sbSQL.append("			'consistidas' as estado, \n");
		sbSQL.append("			1 as ordem_estado, \n");
		sbSQL.append("			0 as total \n");
		sbSQL.append("		from  \n");
		sbSQL.append("			processo p with (NOLOCK), enquadramento e with (NOLOCK) \n");
		sbSQL.append(" \n");
		sbSQL.append("		UNION \n");
		sbSQL.append("		select  \n");
		sbSQL.append("			id_processo, \n");
		sbSQL.append("			e.id_enquadramento,  \n");
		sbSQL.append("			p.nome, \n");
		sbSQL.append("			'inconsistidas' as estado, \n");
		sbSQL.append("			2 as ordem_estado, \n");
		sbSQL.append("			0 as total \n");
		sbSQL.append("		from  \n");
		sbSQL.append("			processo p with (NOLOCK), enquadramento e with (NOLOCK) \n");
		sbSQL.append("			 \n");
		sbSQL.append("		UNION \n");
		sbSQL.append("		select  \n");
		sbSQL.append("			id_processo, \n");
		sbSQL.append("			e.id_enquadramento,  \n");
		sbSQL.append("			p.nome, \n");
		sbSQL.append("			'divergencias' as estado, \n");
		sbSQL.append("			3 as ordem_estado, \n");
		sbSQL.append("			0 as total \n");
		sbSQL.append("		from  \n");
		sbSQL.append("			processo p with (NOLOCK), enquadramento e with (NOLOCK) \n");
		sbSQL.append("	) as t \n");
		sbSQL.append("	 \n");
		sbSQL.append(") as t \n");
		sbSQL.append("where \n");
		sbSQL.append("	id_processo in (1,2,3,11) \n");
		sbSQL.append("group by \n");
		sbSQL.append("	id_enquadramento, \n");
		sbSQL.append("	ordem_processo, \n");
		sbSQL.append("	ordem_estado, \n");
		sbSQL.append("	nome, \n");
		sbSQL.append("	estado \n");
		sbSQL.append("order by \n");
		sbSQL.append("	id_enquadramento, \n");
		sbSQL.append("	ordem_processo \n");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer,AnaliseAutuacoesGwtBean> beans = new HashMap<Integer, AnaliseAutuacoesGwtBean>();
		String query = sbSQL.toString();
		try {
			conn = Conexao.getConexao();
			
			if (params.containsKey("digitador_id")){
				query = query.replaceAll("\\{digitador\\}", ""+(Integer)params.get("digitador_id"));
			} else {
				//Configuracao conf = ConfiguracaoProvider.getInstance();
				//int idDigitadores = conf.getIdGrupoDigitadores();
				query = query.replaceAll("\\{digitador\\}", 
								 "0");
			}
			if (params.containsKey("auditor_id")){
				query = query.replaceAll("\\{auditor\\}", ""+(Integer)params.get("auditor_id"));
			} else {
				//Configuracao conf = ConfiguracaoProvider.getInstance();
				//int idDigitadores = conf.getIdGrupoDigitadores();
				query = query.replaceAll("\\{auditor\\}", 
								 "0");
			}
			
			SimpleDateFormat fmtInicial = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			SimpleDateFormat fmtFinal = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			if (params.containsKey("data_inicial")){
				query = query.replaceAll("\\{data_inicial\\}", fmtInicial.format(params.get("data_inicial")));
			} else {
				query = query.replaceAll("\\{data_inicial\\}", "");
			}
			
			if (params.containsKey("data_final")){
				query = query.replaceAll("\\{data_final\\}", fmtFinal.format(params.get("data_final")));
			} else {
				query = query.replaceAll("\\{data_final\\}", "");
			}
			
			if (params.containsKey("id_local")){
				query = query.replaceAll("\\{id_local\\}", (String)params.get("id_local"));
			} else {
				query = query.replaceAll("\\{id_local\\}", 
								 "0");
			}
			
			if (params.containsKey("pista")){
				query = query.replaceAll("\\{pista\\}", (String)params.get("pista"));
			} else {
				query = query.replaceAll("\\{pista\\}", 
								 "0");
			}
			
			System.out.println(query);
			ps = conn.prepareStatement(query);
			
			rs = ps.executeQuery();
			AnaliseAutuacoesGwtBean bean = null;
			
			String propertyPrefix = "",
				   propertySufix = "";
			Integer id_enquadramento;
			while (rs.next()){
				id_enquadramento = rs.getInt("id_enquadramento");
				if (beans.containsKey(id_enquadramento)) {
					bean = beans.get(id_enquadramento);
				} else {
					bean = new AnaliseAutuacoesGwtBean();
					beans.put(id_enquadramento, bean);
					bean.setIdEnquadramento(id_enquadramento);
				}
				
				String processo = rs.getString("processo").trim();
				String estado = rs.getString("estado").trim();
				Integer total = rs.getInt("total");
				
							
				if ("Triagem".equals(processo)) {
					propertyPrefix = "triagem";
				} else if ("Digitação".equals(processo)) {
					propertyPrefix = "digitacao";
				} else if ("Liberação".equals(processo)) {
					propertyPrefix = "liberacao";
				} else if ("Validação".equals(processo)) {
					propertyPrefix = "validacao";
				}
				
				if ("consistidas".equals(estado)) {
					propertySufix = "Consist";
					bean.set(propertyPrefix+"Total",(Integer)bean.get(propertyPrefix+"Total") + total);
				} else if ("inconsistidas".equals(estado)) {
					propertySufix = "Inconsist";
					bean.set(propertyPrefix+"Total",(Integer)bean.get(propertyPrefix+"Total") + total);
				} else if ("divergencias".equals(estado)) {
					propertySufix = "Divergencia";
				}
				
				bean.set(propertyPrefix+propertySufix, total);
				
			}
			
		} catch (ConexaoException e) {
			e.printStackTrace();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		List<AnaliseAutuacoesGwtBean> lRet = Lists.newArrayList(beans.values());
		return lRet;
	}
}
