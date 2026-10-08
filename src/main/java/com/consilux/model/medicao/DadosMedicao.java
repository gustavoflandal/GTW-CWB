/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

*********************************************************************************/

package com.consilux.model.medicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.EnquadramentoRegraInfracao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de DADOS para os relatórios de medição
 * @author Luiz Fernando Amaral - Consilux Tecnologia
 * Data: 21/08/2014
 */
public class DadosMedicao {
	
	private static Logger logger = LogManager.getLogger(DadosMedicao.class);

	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Total de Infrações
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 10/09/2014
	 */
	public ArrayList<ItemMedicao> totalInfracoesFixo(Long mes,
											         Long ano, 
											         String velsis) throws ConexaoException, SQLException, ModelException {
		logger.info("totalInfracoesFixo(" + mes +"," + ano + "," + velsis + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes			int = ? ");
		sbSQL.append(" Declare @Ano		    int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(10) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM ( ");
		sbSQL.append("        	select ");
				            			
						
		sbSQL.append(" 				cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append("             	cem.serie_equipamento						as 'Serie_Equipamento', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append("     			DATEPART(DD,i.data)							as 'Dia', ");
		sbSQL.append(" 				i.id_infracao								as 'Quantidade', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append(" 				case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento "); 
						
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		sbSQL.append("			LEFT JOIN veiculo v (nolock) ");
		sbSQL.append("				ON v.id_local = cem.cod_pista ");
		sbSQL.append("				AND ((v.entre_faixa IS NULL AND v.pista = cem.cod_pista_alternativo) OR ");
		sbSQL.append("				(v.pista = cem.cod_pista_tarja AND v.entre_faixa = cem.entre_faixa)) ");
		sbSQL.append("				AND (v.data >= cem.data_inicio or v.data is null) ");
		sbSQL.append("				AND (cast(v.data as date) BETWEEN @Data_Ini AND @Data_Fim or v.data is null) ");
		sbSQL.append("			LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append("				ON v.id_veiculo = i.id_veiculo ");
						
		sbSQL.append(" 			where ");
		sbSQL.append(" 			     cem.cod_pista_prodam > 0 ");
		sbSQL.append(" 			     and cem.id_produto in (1, 2) ");
		sbSQL.append("               and cem.data_inicio <= @Data_Fim ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("           and cem.serie_equipamento > 2014000000 ");
		}
						
		sbSQL.append(" 			group by ");
		sbSQL.append(" 				cem.cod_pista_prodam, ");
		sbSQL.append(" 				cem.serie_equipamento, ");
		sbSQL.append(" 				cem.cod_pista_alternativo, "); 
		sbSQL.append(" 				cem.cod_pista, ");
		sbSQL.append(" 				cem.descricao, ");
		sbSQL.append(" 				DATEPART(DD,i.data), ");
		sbSQL.append(" 				i.id_infracao, ");
		sbSQL.append(" 				cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");

		sbSQL.append(" 		) as Contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Quantidade) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) "); 
		sbSQL.append(" 		) AS contagem_dia ");

		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, "); 
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTotalInfracoes =  new ArrayList<ItemMedicao>();
		ItemMedicao infracoes;
		Long[] celulas;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
					infracoes = new ItemMedicao(
													rs.getInt("Codigo_Pista_Prodam"),
													rs.getString("Faixa"),
													rs.getLong("Serie_Equipamento"),
													rs.getInt("Codigo_Pista"),
													rs.getString("Local"),
													rs.getString("Data_Publicacao"),
													rs.getInt("flagFuncionamento"),
													celulas
													);
				
				listTotalInfracoes.add(infracoes);
			}
			
			return listTotalInfracoes;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	

	/**
	 * Busca TOTAL de VOLUME de Veiculos do dia no BD.
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 21/08/2014
	 */
	public ArrayList<ItemMedicao> VolumePorDia_CAV(Long mes, Long ano, String velsis) throws ConexaoException, SQLException, ModelException {
		
		logger.info("VolumePorDia(" + mes +"," + ano + "," + velsis + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" Declare @Ano		    int = ?  ");
		sbSQL.append(" Declare @Mes			int = ?  ");
		sbSQL.append(" Declare @Data_Ini	varchar(10)  ");
		sbSQL.append(" Declare @Data_Fim	varchar(22)  ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM  ( ");
		sbSQL.append(" 			select ");
		sbSQL.append(" 				cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.serie_equipamento						as 'Serie_Equipamento', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append(" 				DATEPART(DD,ve.data)						as 'Dia', ");
		sbSQL.append(" 				ve.id_veiculo_unic							as 'Trafego', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append("     			case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento ");
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		sbSQL.append("     			left join veiculo_estatistica_medicao ve (nolock)  ");
		sbSQL.append("     			on ve.id_local = cem.cod_pista  ");
		sbSQL.append("     			and ve.pista = cem.cod_pista_alternativo  ");
		sbSQL.append("     			and (cem.data_inicio <= ve.data or ve.data is null)  ");
		sbSQL.append("     			and ve.id_local > 0  ");
		sbSQL.append("     			and cast(ve.data as datetime) BETWEEN @Data_Ini AND @Data_Fim ");
		sbSQL.append("     			and ve.entre_faixa is null "); //Alteração importante para contabilizar apenas a versão nova de DT entre-faixa
		sbSQL.append("     		where   ");
		sbSQL.append("     			cem.cod_pista_prodam > 0 ");
		sbSQL.append("              and cem.id_produto in (1,2) ");			
		sbSQL.append("             	and cem.data_inicio <= @Data_Fim ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("          and cem.serie_equipamento > 2014000000 ");
		}
		
		sbSQL.append("           group by  ");
		sbSQL.append("           	cem.cod_pista_prodam, ");
		sbSQL.append("             	cem.cod_pista_alternativo, ");
		sbSQL.append("             	cem.cod_pista_tarja, ");
		sbSQL.append("             	cem.serie_equipamento, ");
		sbSQL.append("             	cem.cod_pista, ");
		sbSQL.append("             	cem.descricao, ");
		sbSQL.append("             	ve.data, ");
		sbSQL.append("             	ve.id_veiculo_unic, ");
		sbSQL.append("             	cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");
		
		
		//Versão NOVA para obter DT sem entre-Faixa (and ve.entre_faixa is NOT null)
		//Luiz Amaral 20/11/2015
		///---------------------------------------------------------------
		sbSQL.append(" 			UNION  ");
		//---------------------------------------------------------------
		
		sbSQL.append(" 			select ");
		sbSQL.append(" 				cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.serie_equipamento						as 'Serie_Equipamento', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append(" 				DATEPART(DD,ve.data)						as 'Dia', ");
		sbSQL.append(" 				ve.id_veiculo_unic							as 'Trafego', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append("     			case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento ");
		
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		
		sbSQL.append(" 				left join veiculo_estatistica_medicao ve (nolock)  ");
		sbSQL.append(" 					on ve.id_local = cem.cod_pista  ");
		sbSQL.append(" 				    and ve.pista = cem.cod_pista_tarja  "); //Alteração importante de cod_pista_alternativa para cod_pista_tarja
		sbSQL.append(" 					and ve.entre_faixa = cem.entre_faixa "); //Importante para não duplicar no modelo novo de DT Grupo C 
		sbSQL.append(" 				    and (cem.data_inicio <= ve.data or ve.data is null)  ");
		sbSQL.append(" 				    and ve.id_local > 0  ");
		sbSQL.append(" 				    and cast(ve.data as datetime) BETWEEN @Data_Ini AND @Data_Fim ");
		sbSQL.append(" 					and ve.entre_faixa is not null "); //Alteração importante para contabilizar apenas a versão nova de DT entre-faixa
		sbSQL.append("     		where   ");
		sbSQL.append("     			cem.cod_pista_prodam > 0 ");
		sbSQL.append("              and cem.id_produto in (1,2) ");			
		sbSQL.append("             	and cem.data_inicio <= @Data_Fim ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null)
			sbSQL.append("          and cem.serie_equipamento > 2014000000 ");
		
		sbSQL.append("         group by  ");
		sbSQL.append("             cem.cod_pista_prodam, ");
		sbSQL.append("             cem.cod_pista_alternativo, ");
		sbSQL.append("             cem.cod_pista_tarja, ");
		sbSQL.append("             cem.serie_equipamento, ");
		sbSQL.append("             cem.cod_pista, ");
		sbSQL.append("             cem.descricao, ");
		sbSQL.append("             ve.data, ");
		sbSQL.append("             ve.id_veiculo_unic, ");
		sbSQL.append("             cem.data_inicio, ");
		sbSQL.append(" 			   cem.entre_faixa, ");
		sbSQL.append(" 			   cem.cod_pista_tarja ");
		
		
		sbSQL.append(" 		) as contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Trafego) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 		) AS contagem_dia ");
		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, ");
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTrafego =  new ArrayList<ItemMedicao>();
		ItemMedicao trafego;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				trafego = new ItemMedicao(
											rs.getInt("Codigo_Pista_Prodam"),
											rs.getString("Faixa"),
											rs.getLong("Serie_Equipamento"),
											rs.getInt("Codigo_Pista"),
											rs.getString("Local"),
											rs.getString("Data_Publicacao"),
											celulas,
											rs.getInt("flagFuncionamento")
											);
				
				listTrafego.add(trafego);
			}
			
			return listTrafego;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	/**
	 * Busca TOTAL de VOLUME de Veiculos do dia no BD.
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 21/08/2014
	 */
	public ArrayList<ItemMedicao> VolumePorDia_CAI(Long mes, Long ano, String velsis) throws ConexaoException, SQLException, ModelException {
		
		logger.info("VolumePorDia_CAI(" + mes +"," + ano + "," + velsis + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" DECLARE @Ano		    INT = ?  ");
		sbSQL.append(" DECLARE @Mes			INT = ?  ");
		sbSQL.append(" DECLARE @Data_Ini	VARCHAR(10)  ");
		sbSQL.append(" DECLARE @Data_Fim	VARCHAR(22)  ");
		sbSQL.append(" DECLARE @Dias_Mes	DECIMAL(2,0)  ");

		sbSQL.append(" SET @Data_Ini = CAST(@Ano AS VARCHAR) + '-' + CAST(@Mes AS VARCHAR) + '-01' ");
		sbSQL.append(" SET @Dias_Mes = DAY(DATEADD(mm, 1, @Data_Ini) - DAY(DATEADD(mm, 1, @Data_Ini))) ");
		sbSQL.append(" SET @Data_Fim = CAST(@Ano AS VARCHAR) + '-' + CAST(@Mes AS VARCHAR) + '-' + CAST(@Dias_Mes AS VARCHAR) + CAST(' 23:59:59.000' AS VARCHAR) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM  ( ");
		sbSQL.append(" 			SELECT ");
		sbSQL.append(" 				cem.cod_pista_prodam						AS 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											AS 'Faixa', ");
		sbSQL.append(" 				cem.serie_equipamento						AS 'Serie_Equipamento', ");
		sbSQL.append(" 				cem.cod_pista								AS 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								AS 'Local', ");
		sbSQL.append(" 				DATEPART(DD,ve.data)						AS 'Dia', ");
		sbSQL.append(" 				ve.trafego									AS 'Trafego', ");
		sbSQL.append(" 				CONVERT(VARCHAR(10), cem.data_inicio, 103)	AS 'Data_Publicacao', ");
		sbSQL.append("     			CASE WHEN cem.data_inicio > @Data_Ini THEN DAY(cem.data_inicio) ELSE 0 END flagFuncionamento ");
		sbSQL.append(" 			FROM "+tabela_medicao+" cem ");
		sbSQL.append("     			LEFT JOIN veiculo_sumarizado ve (NOLOCK)  ");
		sbSQL.append("     			ON ve.id_local = cem.id_local  ");
		sbSQL.append("     			AND ve.pista = cem.id_pista  ");
		sbSQL.append("     			AND ve.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE) ");
		sbSQL.append("     		WHERE   ");
		sbSQL.append("     			cem.cod_pista_prodam > 0 ");
		sbSQL.append("              AND cem.id_produto IN (1,2) ");			
		sbSQL.append("             	AND cem.data_inicio <= @Data_Fim ");
		sbSQL.append("				AND cem.atualizar = 1 ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("          AND cem.serie_equipamento > 2014000000 ");
		}
		
		sbSQL.append(" 		) AS contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			SUM(Contagem.Trafego) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 		) AS contagem_dia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		Codigo_Pista, ");
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTrafego =  new ArrayList<ItemMedicao>();
		ItemMedicao trafego;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				trafego = new ItemMedicao(
											rs.getInt("Codigo_Pista_Prodam"),
											rs.getString("Faixa"),
											rs.getLong("Serie_Equipamento"),
											rs.getInt("Codigo_Pista"),
											rs.getString("Local"),
											rs.getString("Data_Publicacao"),
											celulas,
											rs.getInt("flagFuncionamento")
											);
				
				listTrafego.add(trafego);
			}
			
			return listTrafego;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	

	/**
	 * monta os enquadramentos numa unica linha por LOCAL
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 21/08/2014
	 */
	public ItemMedicao montaEnquadramentos(ItemMedicao itemMed) throws ConexaoException, SQLException{
		
		Long codSerie = itemMed.getSerieEquipamento();
		Integer codFx = itemMed.getFaixaEquipamento();
		String enquadFinal ="";
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" select  ");
		sbSQL.append(" 	  distinct id_local, ");
		sbSQL.append(" 	  id_pista, ");
		sbSQL.append(" 	  cr.tipo ");
		sbSQL.append(" from local_vigente lv (nolock) ");
		sbSQL.append(" 	inner join configuracao_equipamento_regra_infracao cr (nolock)  ");
		sbSQL.append(" 		on cr.id_configuracao_equipamento = lv.id_configuracao_equipamento ");
		sbSQL.append(" 	WHERE  cr.tipo <> 'TS' ");
		sbSQL.append(" 		AND cr.tipo <> 'MT' ");
		sbSQL.append(" 		and lv.serie_equipamento = ?  ");
		sbSQL.append(" 		and cr.id_pista is null ");

		sbSQL.append(" 		UNION ALL ");

		sbSQL.append(" select  ");
		sbSQL.append(" 	  distinct id_local, ");
		sbSQL.append(" 	  id_pista, ");
		sbSQL.append(" 	  cr.tipo ");
		sbSQL.append(" from local_vigente lv (nolock) ");
		sbSQL.append(" 	inner join configuracao_equipamento_regra_infracao cr (nolock) ");
		sbSQL.append(" 		on cr.id_configuracao_equipamento = lv.id_configuracao_equipamento ");
		sbSQL.append(" 	WHERE  cr.tipo <> 'TS' ");
		sbSQL.append(" 		AND cr.tipo <> 'MT' ");
		sbSQL.append(" 		and lv.serie_equipamento = ?  ");
		sbSQL.append(" 		and cr.id_pista = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,codSerie);
			ps.setLong(2,codSerie);
			ps.setLong(3,codFx);
			
			rs = ps.executeQuery();
			
			//Preenchendo os enquadramentos
			while (rs.next()){
				enquadFinal = enquadFinal + rs.getString("tipo") + " ";
			}

			itemMed.setDescEquadramentos(enquadFinal);
				
			return itemMed;
		}
		finally {
			if (conn != null)
				conn.close();
		}			

	}
	
	/**
	 * monta a Legenda dos Enquadramentos nos relatórios
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 04/09/2014
	 */
	public ArrayList<EnquadramentoRegraInfracao> legendaEnquadramentos() throws ConexaoException, SQLException{
		
		logger.info("legendaEnquadramentos()");
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT ");
		sbSQL.append(" tipo, id_enquadramento, tipo_apait,   ");
		sbSQL.append(" REPLACE(replace(replace(replace(descricao_apait, 'Á','A'), 'Í', 'I'), 'Ç', 'C'), 'Ã', 'A') as descricao_apait ");
		sbSQL.append(" FROM enquadramento_regra_infracao (nolock) ");
		sbSQL.append(" where id_enquadramento > 1 ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			EnquadramentoRegraInfracao enquad;
			ArrayList<EnquadramentoRegraInfracao> lstEnquad = new ArrayList<EnquadramentoRegraInfracao>();
			
			//Preenchendo os enquadramentos
			while (rs.next()){
				enquad = new EnquadramentoRegraInfracao();
				enquad.setIdEnquadramentoRegraInfracao(rs.getInt("id_enquadramento"));
				enquad.setTipoApait(rs.getString("tipo_apait"));
				enquad.setDescApait(rs.getString("descricao_apait"));
				
				lstEnquad.add(enquad);
			}
			return lstEnquad;
		}
		finally {
			if (conn != null)
				conn.close();
		}			

	}
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Atraso de Imagens
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 05/09/2014
	 */
	public ArrayList<ImagensAtrasadas> imagensAtradas(Long mes,
											          Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("imagensAtradas(" + mes +"," + ano + ")");
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes		int = ?  ");
		sbSQL.append(" Declare @Ano		int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10)  ");
		sbSQL.append(" Declare @Data_Fim	varchar(22)  ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");
		
		sbSQL.append(" select  ");
		sbSQL.append(" 	codigo_externo, ");
		sbSQL.append(" 	tipo,  ");
		sbSQL.append(" 	convert(varchar(10),data_remessa,103) as data_remessa, ");
		sbSQL.append(" 	data_infracao, ");
		sbSQL.append(" 	diferenca_dias, ");
		sbSQL.append(" 	case when desconto_dias > 0 then desconto_dias else 0 end as  desconto_dias, ");
		sbSQL.append(" 	count(diferenca_dias) qtde ");
			
		sbSQL.append(" from ( ");

		sbSQL.append(" 		select  ");
		sbSQL.append(" 		r.id_remessa,  ");
		sbSQL.append(" 		r.codigo_externo, ");
		sbSQL.append(" 		i.id_infracao, ");
		sbSQL.append(" 		r.tipo, ");
		sbSQL.append(" 		convert(varchar(10),r.data,103) as 'data_remessa', ");
		sbSQL.append(" 		convert(varchar(10),i.data,103) as 'data_infracao', ");
		sbSQL.append(" 		DATEDIFF(day,i.data, r.data) as diferenca_dias, ");
		sbSQL.append(" 		DATEDIFF(day,i.data, r.data) - 8 as desconto_dias ");
		sbSQL.append(" 		from remessa r (nolock) ");
		sbSQL.append(" 		inner join infracao_remessa ir (nolock) ");
		sbSQL.append(" 			on ir.id_remessa = r.id_remessa  ");
		sbSQL.append(" 		inner join infracao i (nolock) ");
		sbSQL.append(" 			on i.id_infracao = ir.id_infracao ");
		sbSQL.append(" 		where i.data between @Data_Ini and @Data_Fim ");
		sbSQL.append(" 		) as total ");

		sbSQL.append(" where diferenca_dias > 8 ");

		sbSQL.append(" group by  ");
		sbSQL.append(" 	id_remessa,  ");
		sbSQL.append(" 	codigo_externo,  ");
		sbSQL.append(" 	tipo,  ");
		sbSQL.append(" 	data_remessa,  ");
		sbSQL.append(" 	diferenca_dias,  ");
		sbSQL.append(" 	data_infracao,  "); 
		sbSQL.append(" 	desconto_dias ");

		sbSQL.append(" order by  ");
		sbSQL.append(" 	diferenca_dias,  ");
		sbSQL.append(" 	id_remessa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ImagensAtrasadas> listImagensAtraso =  new ArrayList<ImagensAtrasadas>();
		ImagensAtrasadas imagensAtrasadas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){			
				imagensAtrasadas = new ImagensAtrasadas(
														rs.getInt("codigo_externo"),
														rs.getString("tipo"),
														rs.getString("data_remessa"),
														rs.getString("data_infracao"),
														rs.getInt("diferenca_dias"),
														rs.getInt("desconto_dias"),
														rs.getLong("qtde")
												      );
				listImagensAtraso.add(imagensAtrasadas);
			}
			
			return listImagensAtraso;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	

	
	/**
	 * Busca TOTAL de Arquivos de 4 Minutos no BD.
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public ArrayList<Arquivos4Minutos> arquivos4Minutos_CAI(Long mes, Long ano, String velsis) throws SQLException, ConexaoException, ParseException {
		
		logger.info("Gerando Relatorio 4 Minutos CAI (arquivos4Minutos_CAI) (ANO = " + ano + "; MES = " + mes + ")");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		String str_anomes;
		if (mes < 10) 
			str_anomes = ano.toString() + "0" + mes.toString();
		else 
			str_anomes = ano.toString() + mes.toString();
		
		
//		String tabela_medicao = "configuracao_equipamento_medicao";
		String tabela_medicao = "configuracao_equipamento_medicao_novo";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		String where = " where nome_arquivo like 'DTL4%" + str_anomes + "%' ";
		logger.info(where);

		sbSQL.append(" Declare @Ano		int = ? ");
		sbSQL.append(" Declare @Mes		int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(22) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");

		
		sbSQL.append(" SELECT CAST(SUBSTRING(nome_arquivo, 5, 4) AS INT) AS cod_pista, data_arquivo, CAST(crc_cai AS VARCHAR(32)) AS crc_cai INTO #dados ");
		sbSQL.append(" from arquivos_cai_para_cav (NOLOCK) ");
		sbSQL.append(where);
		
		
		sbSQL.append(" select *from ( ");
		sbSQL.append(" 	select  ");
		sbSQL.append(" 		cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 		CASE WHEN cem.entre_faixa = 1  ");
		sbSQL.append(" 			 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' "); 
		sbSQL.append(" 			 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) "); 
		sbSQL.append(" 		END as cod_pista_alternativo,  ");
//		sbSQL.append("      cem.cod_pista_alternativo, ");
		sbSQL.append(" 		cem.serie_equipamento						as 'serie_equipamento', ");
		sbSQL.append(" 		cem.cod_pista								as 'id_local', "); 
		sbSQL.append(" 		cem.descricao								as 'descricao', ");
		sbSQL.append(" 		DATEPART(DD,dt.data_arquivo)				as 'Dia', ");
		sbSQL.append(" 		dt.crc_cai AS 'Quantidade', ");
		sbSQL.append(" 		case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end as 'flagFuncionamento', ");
		sbSQL.append(" 		convert(varchar(10), cem.data_inicio, 120)	as 'data_publicacao', ");
		sbSQL.append(" 		cem.qtde_equipamentos, ");
		sbSQL.append(" 		cast(@Data_Fim as date) as data_fim_relatorio ");
		sbSQL.append(" 	from "+tabela_medicao+" cem ");
		sbSQL.append(" 		left join #Dados dt ");
		sbSQL.append(" 			on dt.cod_pista = cem.cod_pista ");
		sbSQL.append(" 			and (dt.data_arquivo >= cem.data_inicio or dt.data_arquivo is null) ");
		sbSQL.append(" 			and dt.data_arquivo between @Data_ini and @Data_fim  ");
		sbSQL.append(" 	where cem.cod_pista_prodam > 0 ");
		sbSQL.append(" 		  and cem.data_inicio <= @Data_Fim ");
		sbSQL.append(" 		  and cem.id_produto in (1,2) ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("    and cem.serie_equipamento > 2014000000 ");
		}
		
		sbSQL.append(" 	group by ");
		sbSQL.append(" 		cem.cod_pista, ");
		sbSQL.append(" 		cem.cod_pista_prodam, ");
		sbSQL.append(" 		dt.data_arquivo, ");
		sbSQL.append(" 		dt.crc_cai, ");
		sbSQL.append(" 		DATEPART(DD,dt.data_arquivo), ");
		sbSQL.append(" 		cem.descricao, ");
		sbSQL.append(" 		cem.cod_pista_alternativo, ");
		sbSQL.append(" 		cem.serie_equipamento, ");
		sbSQL.append(" 		cem.data_inicio , ");
		sbSQL.append(" 		cem.qtde_equipamentos, ");
		sbSQL.append(" 		cem.entre_faixa, ");
		sbSQL.append(" 		cem.cod_pista_tarja ");
		sbSQL.append(" ) as contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 	( ");
		sbSQL.append(" 		 count(contagem.Quantidade) ");
		sbSQL.append(" 		 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 	) AS contagem_dia ");
		
		sbSQL.append(" order by id_local desc ");

		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Arquivos4Minutos> listItensMedicao =  new ArrayList<Arquivos4Minutos>();
		Arquivos4Minutos itemArquivo4M;
		String[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new String[31];
				
				//Autor: Luiz Amaral
				//Data: 24/03/2015
				//Regra definida por Thiago Hidalgo e Rafael Lima - CET quer que divida os arquivos pela quantidade de equipamentos que enviam ao mesmo tempo
				//Regra valida e definida posterior a 03/2015
				Date data_fim_relatorio = rs.getDate("data_fim_relatorio");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        	Date data_definida = sdf.parse("2015-04-01");			
				
				int equip_dividir = 1;
				if(data_fim_relatorio.after(data_definida))
					equip_dividir = rs.getInt("qtde_equipamentos");
				//////////////////////////////////////////
				
				int valorGet = 0;
				int valor = 0;
				for(int i=1; i<32; i++){
					
					valorGet = 0;
					valor = 0;
					
					if(rs.getInt("flagFuncionamento") <= i){
						
						valorGet = (int) rs.getInt(String.valueOf(i));
						valor = (int) (valorGet/equip_dividir);
						
						if(valor <= 0){
							celulas[i-1] = "0%";
						}else if(valor >= 360){
							celulas[i-1] = "100%";
						}else{
							valor = (valor*100)/360;
							celulas[i-1] = String.valueOf(valor) + "%";
						}
					}
				}
				
				itemArquivo4M = new Arquivos4Minutos(
						rs.getInt("id_local"),
						rs.getInt("Codigo_Pista_Prodam"),
						rs.getString("descricao"),
						celulas,
						rs.getString("data_publicacao"),
						rs.getString("cod_pista_alternativo"),
						rs.getLong("serie_equipamento"));
				
				listItensMedicao.add(itemArquivo4M);
			}
			
			return listItensMedicao;
				
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	public ArrayList<Arquivos4Minutos> arquivos4Minutos_CAV(Long mes, Long ano, String velsis) throws SQLException, ConexaoException, ParseException {
		
		logger.info("Gerando Relatorio 4 Minutos CAV (arquivos4Minutos_CAV)");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Ano		int = ? ");
		sbSQL.append(" Declare @Mes		int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(22) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");

		sbSQL.append(" select *from ( ");
		sbSQL.append(" 	select  ");
		sbSQL.append(" 		day(convert(varchar(10),ai.data_arquivo,120)) as 'dia', ");
		sbSQL.append(" 		id_arquivo, ");
		sbSQL.append(" 		CASE WHEN cem.entre_faixa = 1  ");
		sbSQL.append(" 			 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' "); 
		sbSQL.append(" 			 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) "); 
		sbSQL.append(" 		END as cod_pista_alternativo,  ");
//		sbSQL.append("      cem.cod_pista_alternativo, ");
		sbSQL.append("      cem.serie_equipamento, ");
		sbSQL.append(" 		cem.cod_pista as id_local, ");
		sbSQL.append(" 		cem.cod_pista_prodam, ");
		sbSQL.append(" 		cem.descricao, ");
		sbSQL.append(" 		convert(varchar(10), cem.data_inicio, 103) as data_publicacao, ");
		sbSQL.append(" 		case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento, ");
		sbSQL.append(" 		cem.qtde_equipamentos, ");
		sbSQL.append(" 		cast(@Data_Fim as date) as data_fim_relatorio ");
		sbSQL.append(" 	from "+tabela_medicao+" cem ");
		sbSQL.append(" 		left join arquivos_importados ai (nolock) ");
		sbSQL.append(" 			on ai.id_local = cem.cod_pista ");
		sbSQL.append(" 			and (ai.data_arquivo >= cem.data_inicio or ai.data_arquivo is null) ");
		sbSQL.append(" 			and ai.nome_arquivo like 'DT%'  ");
        sbSQL.append(" 			and ai.data_arquivo between @Data_ini and @Data_fim  ");
		sbSQL.append(" 	where cem.cod_pista_prodam > 0 ");
		sbSQL.append("        and cem.data_inicio <= @Data_Fim ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("    and cem.serie_equipamento > 2014000000 ");
		}
		
		sbSQL.append(" 	group by ");
		sbSQL.append(" 		cem.cod_pista, ");
		sbSQL.append(" 		cem.cod_pista_prodam, ");
		sbSQL.append(" 		ai.id_arquivo, ");
		sbSQL.append(" 		cem.descricao, ");
		sbSQL.append("      cem.cod_pista_alternativo, ");
		sbSQL.append("      cem.serie_equipamento, ");
		sbSQL.append(" 		convert(varchar(10), ai.data_arquivo,120), ");
		sbSQL.append(" 		cem.data_inicio, ");
		sbSQL.append(" 		cem.qtde_equipamentos, ");
		sbSQL.append(" 		cem.entre_faixa, ");
		sbSQL.append(" 		cem.cod_pista_tarja "); 
		sbSQL.append(" ) as contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 	( ");
		sbSQL.append(" 		 count(contagem.id_arquivo) ");
		sbSQL.append(" 		 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 	) AS contagem_dia ");
			
		sbSQL.append(" order by id_local desc ");

		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Arquivos4Minutos> listItensMedicao =  new ArrayList<Arquivos4Minutos>();
		Arquivos4Minutos itemArquivo4M;
		String[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new String[31];
				
				//Autor: Luiz Amaral
				//Data: 24/03/2015
				//Regra definida por Thiago Hidalgo e Rafael Lima - CET quer que divida os arquivos pela quantidade de equipamentos que enviam ao mesmo tempo
				//Regra valida e definida posterior a 03/2015
				Date data_fim_relatorio = rs.getDate("data_fim_relatorio");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        	Date data_definida = sdf.parse("2015-04-01");			
				
				int equip_dividir = 1;
				if(data_fim_relatorio.after(data_definida))
					equip_dividir = rs.getInt("qtde_equipamentos");
				//////////////////////////////////////////
				
				int valorGet = 0;
				int valor = 0;
				for(int i=1; i<32; i++){
					
					valorGet = 0;
					valor = 0;
					
					if(rs.getInt("flagFuncionamento") <= i){
						
						valorGet = (int) rs.getInt(String.valueOf(i));
						valor = (int) (valorGet/equip_dividir);
						
						if(valor <= 0){
							celulas[i-1] = "0%";
						}else if(valor >= 360){
							celulas[i-1] = "100%";
						}else{
							valor = (valor*100)/360;
							celulas[i-1] = String.valueOf(valor) + "%";
						}
					}
				}
				
				itemArquivo4M = new Arquivos4Minutos(
										rs.getInt("id_local"),
										rs.getInt("cod_pista_prodam"),
										rs.getString("descricao"),
										celulas,
										rs.getString("data_publicacao"),
										rs.getString("cod_pista_alternativo"),
										rs.getLong("serie_equipamento"));
				
				listItensMedicao.add(itemArquivo4M);
			}
			
			return listItensMedicao;
				
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Erros de Validação
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 09/09/2014
	 */
	public ArrayList<ErrosValidacao> errosValidacao(Long mes,
											        Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("errosValidacao(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SET NOCOUNT ON ");

		sbSQL.append("  Declare @Mes		int = ? ");
		sbSQL.append("  Declare @Ano		int = ?  ");
		sbSQL.append("  Declare @Data_Ini	varchar(10)  ");
		sbSQL.append("  Declare @Data_Fim	varchar(22)  ");
		sbSQL.append("  Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append("  set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append("  set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append("  set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");


		sbSQL.append("  create table #infracoes (id_infracao INT, id_remessa INT, tipo_validacao VARCHAR(50)) ");


		//Todas as infrações com validação 100% 
		sbSQL.append("  INSERT INTO #infracoes ");
		sbSQL.append("  SELECT  ");
		sbSQL.append("  	distinct  ");
		sbSQL.append("  	ir.id_infracao, ");
		sbSQL.append("  	ir.id_remessa, ");
		sbSQL.append("  	'Validação 100%' ");
		sbSQL.append("  FROM remessa r (NOLOCK)  ");
		sbSQL.append("  inner join infracao_remessa ir (NOLOCK) ");
		sbSQL.append("  	on ir.id_remessa = r.id_remessa ");
		sbSQL.append("  inner join infracao i (NOLOCK)");
		sbSQL.append("  	on i.id_infracao = ir.id_infracao ");
		sbSQL.append("  where   ");
		sbSQL.append("  	i.id_processo = 25  ");
		sbSQL.append("  	and i.data between @data_ini and @data_fim ");
		sbSQL.append("  	and r.id_remessa not in ( ");
		sbSQL.append("  							select  ");
		sbSQL.append("  							distinct id_remessa ");
		sbSQL.append("  							from remessa_amostragem (NOLOCK) ");
		sbSQL.append("  							) ");
		sbSQL.append("  order by 2 ");

		//Todas as infrações com validação dentro Amostra 
		sbSQL.append("  INSERT INTO #infracoes ");
		sbSQL.append("  SELECT  ");
		sbSQL.append("  	distinct  ");
		sbSQL.append("  	ir.id_infracao, ");
		sbSQL.append("  	ir.id_remessa, ");
		sbSQL.append("  	'Validação Amostra' ");
		sbSQL.append("  FROM remessa r (NOLOCK)  ");
		sbSQL.append("  inner join infracao_remessa ir (NOLOCK)  ");
		sbSQL.append("  	on ir.id_remessa = r.id_remessa ");
		sbSQL.append("  inner join infracao i (NOLOCK)  ");
		sbSQL.append("  	on i.id_infracao = ir.id_infracao ");
		sbSQL.append("  inner join remessa_amostragem ra (NOLOCK) "); 
		sbSQL.append("  	ON i.id_infracao = ra.id_infracao  ");
		sbSQL.append("  	AND ir.id_remessa = ra.id_remessa  ");
		sbSQL.append("  where   ");
		sbSQL.append("  	ra.id_infracao is not null  ");
		sbSQL.append("  	and i.id_processo = 25  ");
		sbSQL.append("  	and i.data between @data_ini and @data_fim ");
		sbSQL.append("  order by 2 ");

		
		//Buscar os erros baseados nas infrações pré-seleciondas
		sbSQL.append("  select  ");
		sbSQL.append("  	tipo_apait, ");
		sbSQL.append("  	codigo_externo, ");
		sbSQL.append("  	count(id_infracao) as qtdeErros ");
		sbSQL.append("  from ( ");
		sbSQL.append("           SELECT  distinct ");
		sbSQL.append("           	i.id_infracao, ");
		sbSQL.append("           	eri.tipo_apait, ");
		sbSQL.append("           	r.codigo_externo,  ");
		sbSQL.append("           	inf.tipo_validacao,   ");
		sbSQL.append("           	ip_v.erro_oblit, ");
		sbSQL.append("           	COALESCE(mi.placa, v.placa, REPLICATE(' ', 7)) AS placa,  ");
		sbSQL.append("           	i.placa AS placa_digitada, ");
		sbSQL.append("           	mi.id_marca_cet as id_marca,  ");
		sbSQL.append("           	cmc.descricao AS marca_cet, ");
		sbSQL.append("           	cmc_d.descricao AS marca_cet_val, ");
		sbSQL.append("           	mi.id_local AS cod_pista,  ");
		sbSQL.append("           	mi.descricao_local AS nome_pista, ");
		sbSQL.append("           	mi.cod_pista_prodam AS cod_pista_prodam, ");
		sbSQL.append("           	i.id_inconsistencia,  ");
		sbSQL.append("           	inc_v.descricao AS inconsistencia_validacao, ");
		sbSQL.append("           	inc_l.descricao AS inconsistencia_liberacao ");
					
		sbSQL.append("           FROM infracao i (nolock)  ");
		sbSQL.append("           	INNER JOIN infracao_remessa ir (nolock)  ");
		sbSQL.append("           		ON i.id_infracao = ir.id_infracao  ");
		sbSQL.append("           	INNER JOIN remessa r (nolock)  ");
		sbSQL.append("           		ON r.id_remessa = ir.id_remessa  ");
		sbSQL.append("           	INNER JOIN movimento_importacao mi (NOLOCK) ");
//		sbSQL.append("           		ON r.codigo_externo = mi.id_movimento ");
//		sbSQL.append("           		AND r.id_enquadramento = mi.id_enquadramento ");
		sbSQL.append("  				ON r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("           		AND ir.sequencia = mi.sequencia ");
		sbSQL.append("           	INNER JOIN veiculo v (nolock)  ");
		sbSQL.append("           		ON i.id_veiculo = v.id_veiculo ");  
		sbSQL.append("           	INNER JOIN enquadramento_regra_infracao eri (NOLOCK)  ");
		sbSQL.append("           		ON eri.id_enquadramento = r.id_enquadramento ");
		sbSQL.append("           		AND eri.tipo_apait = r.tipo ");
		sbSQL.append("           	LEFT JOIN cad_veiculo cv  (nolock)  ");
		sbSQL.append("           		ON cv.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN cad_veiculo cvv (nolock)  ");
		sbSQL.append("           		ON cvv.placa = v.placa  ");
		sbSQL.append("           	LEFT JOIN cad_marca_cet_processo cmcetp (nolock)  ");
		sbSQL.append("           		ON cmcetp.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN cad_especie_processo ep (nolock)  ");
		sbSQL.append("           		ON ep.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN infracao_processo_concluido ipc_l (nolock)  ");
		sbSQL.append("           		ON	i.id_infracao = ipc_l.id_infracao  ");
		sbSQL.append("           		AND ipc_l.id_processo = 11  ");
		sbSQL.append("           	JOIN infracao_processo ip_v (nolock)  ");
		sbSQL.append("           		ON	ir.id_infracao = ip_v.id_infracao  ");
		sbSQL.append("           		AND ip_v.id_processo = 3 ");
		sbSQL.append("           	JOIN (	SELECT  ");
		sbSQL.append("           				MAX(id_infracao_processo) AS id_infracao_processo,  ");
		sbSQL.append("           				id_infracao,  ");
		sbSQL.append("           				id_processo  ");
		sbSQL.append("           			FROM  ");
		sbSQL.append("           				infracao_processo ip (nolock)  ");
		sbSQL.append("           			GROUP BY  ");
		sbSQL.append("           				id_infracao,  ");
		sbSQL.append("           				id_processo ");
		sbSQL.append("           		) AS sub1  ");
		sbSQL.append("           		ON	ir.id_infracao = sub1.id_infracao  ");
		sbSQL.append("           		AND sub1.id_processo = 3  ");
		sbSQL.append("           		AND ip_v.id_infracao_processo = sub1.id_infracao_processo ");
		sbSQL.append("           	JOIN inconsistencia inc_l (nolock)  ");
		sbSQL.append("           		ON inc_l.id_inconsistencia = COALESCE(ipc_l.id_inconsistencia, mi.[id_inconsistencia])  ");
		sbSQL.append("           	JOIN inconsistencia inc_v (nolock)  ");
		sbSQL.append("           		ON inc_v.id_inconsistencia = ip_v.id_inconsistencia  ");
		sbSQL.append("           	LEFT JOIN infracao_processo_digitacao ipd (NOLOCK)  ");
		sbSQL.append("           		ON ip_v.id_infracao_processo = ipd.id_infracao_processo  ");
		sbSQL.append("           	JOIN cad_marca_cet cmc (NOLOCK)  ");
		sbSQL.append("           		ON mi.id_marca_cet = cmc.id_marca_cet   ");
		sbSQL.append("           	LEFT JOIN cad_marca_cet cmc_d (NOLOCK) ");
		sbSQL.append("           		ON cmc_d.id_marca_cet = ipd.id_marca_cet ");
		
		//Buscar apenas equipamentos em funcionamento
		//Thiago Surgik - 12/02/2014
		sbSQL.append("           	INNER JOIN "+tabela_medicao+" cem (NOLOCK) ");
		sbSQL.append("           		ON cem.cod_pista = i.id_local ");
		sbSQL.append("           		AND cem.cod_pista_alternativo = i.pista ");
		
		//Nova Regra para excluir infrações dentro da amostra ou com validçaão 100%
		//Luiz Amaral 09/01/2015
		sbSQL.append("           	INNER JOIN #infracoes inf ");
		sbSQL.append("           		ON inf.id_infracao = i.id_infracao ");
		
		
		sbSQL.append("           	WHERE  ");
		sbSQL.append("           	i.data between @Data_Ini and @Data_Fim ");
		sbSQL.append("           	AND	i.id_processo = 25 ");

		sbSQL.append("           	AND   ");
		sbSQL.append("           	(  ");
		sbSQL.append("           		((i.id_inconsistencia = 0 and mi.id_inconsistencia <> 0) OR (mi.id_inconsistencia = 0 and i.id_inconsistencia <> 0))  ");
		
		
//--------------------------------------------------------------------------------------------------------------------------------------------------------		
		//Nova Regra definida pelo Rafael Lima e Thiago Hidalgo - Email 05/12/2014
		//Se Inconsistente CAI e Inconsistente CAV, então não contabiliza alterações
		if(ano > 2014 || mes >= 11){
			sbSQL.append("           			OR ( ");
			sbSQL.append("           				(i.id_inconsistencia = 0 and mi.id_inconsistencia = 0) ");
			sbSQL.append("           				AND ( ");
			sbSQL.append("           				 		(mi.placa <> REPLICATE(' ', 7) AND i.placa <> REPLICATE(' ', 7) AND mi.placa <> i.placa)  ");
			sbSQL.append("           						OR (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet)  ");
			sbSQL.append("           						OR (ip_v.erro_oblit = 1)   ");
			sbSQL.append("           					) ");
			sbSQL.append("           				) ");
			
		}else{
			sbSQL.append("           		OR (mi.placa <> REPLICATE(' ', 7) AND i.placa <> REPLICATE(' ', 7) AND mi.placa <> i.placa) ");
			sbSQL.append("           		OR (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet) ");
			sbSQL.append("           		OR (ip_v.erro_oblit = 1)  ");
		}
//--------------------------------------------------------------------------------------------------------------------------------------------------------		
		sbSQL.append("           			) ");
		
		
		
		sbSQL.append("  ) as totalErros ");

		sbSQL.append("  group by 	 ");
		sbSQL.append("  	tipo_apait, ");
		sbSQL.append("  	codigo_externo ");

		sbSQL.append("  order by  ");
		sbSQL.append("  	tipo_apait, ");
		sbSQL.append("  	codigo_externo ");
		
		sbSQL.append("  SET NOCOUNT OFF ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ErrosValidacao> listErrosValidacao =  new ArrayList<ErrosValidacao>();
		ErrosValidacao errosValidacao;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){			
				errosValidacao = new ErrosValidacao(
													rs.getInt("codigo_externo"),
													rs.getString("tipo_apait"),
													rs.getInt("qtdeErros")
												    );
				listErrosValidacao.add(errosValidacao);
			}
			
			return listErrosValidacao;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Erros de Validação
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 12/02/2015
	 */
	public ArrayList<ErrosValidacao> errosValidacaoPorTipoEquip(Long mes,
											        			Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("errosValidacaoPorTipoEquip(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SET NOCOUNT ON ");

		sbSQL.append("  Declare @Mes		int = ? ");
		sbSQL.append("  Declare @Ano		int = ?  ");
		sbSQL.append("  Declare @Data_Ini	varchar(10)  ");
		sbSQL.append("  Declare @Data_Fim	varchar(22)  ");
		sbSQL.append("  Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append("  set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append("  set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append("  set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");


		sbSQL.append("  create table #infracoes (id_infracao INT, id_remessa INT, tipo_validacao VARCHAR(50)) ");


		//Todas as infrações com validação 100% 
		sbSQL.append("  INSERT INTO #infracoes ");
		sbSQL.append("  SELECT  ");
		sbSQL.append("  	distinct  ");
		sbSQL.append("  	ir.id_infracao, ");
		sbSQL.append("  	ir.id_remessa, ");
		sbSQL.append("  	'Validação 100%' ");
		sbSQL.append("  FROM remessa r (NOLOCK)  ");
		sbSQL.append("  inner join infracao_remessa ir (NOLOCK) ");
		sbSQL.append("  	on ir.id_remessa = r.id_remessa ");
		sbSQL.append("  inner join infracao i (NOLOCK)");
		sbSQL.append("  	on i.id_infracao = ir.id_infracao ");
		sbSQL.append("  where   ");
		sbSQL.append("  	i.id_processo = 25  ");
		sbSQL.append("  	and i.data between @data_ini and @data_fim ");
		sbSQL.append("  	and r.id_remessa not in ( ");
		sbSQL.append("  							select  ");
		sbSQL.append("  							distinct id_remessa ");
		sbSQL.append("  							from remessa_amostragem (NOLOCK) ");
		sbSQL.append("  							) ");
		sbSQL.append("  order by 2 ");

		//Todas as infrações com validação dentro Amostra 
		sbSQL.append("  INSERT INTO #infracoes ");
		sbSQL.append("  SELECT  ");
		sbSQL.append("  	distinct  ");
		sbSQL.append("  	ir.id_infracao, ");
		sbSQL.append("  	ir.id_remessa, ");
		sbSQL.append("  	'Validação Amostra' ");
		sbSQL.append("  FROM remessa r (NOLOCK)  ");
		sbSQL.append("  inner join infracao_remessa ir (NOLOCK)  ");
		sbSQL.append("  	on ir.id_remessa = r.id_remessa ");
		sbSQL.append("  inner join infracao i (NOLOCK)  ");
		sbSQL.append("  	on i.id_infracao = ir.id_infracao ");
		sbSQL.append("  inner join remessa_amostragem ra (NOLOCK) "); 
		sbSQL.append("  	ON i.id_infracao = ra.id_infracao  ");
		sbSQL.append("  	AND ir.id_remessa = ra.id_remessa  ");
		sbSQL.append("  where   ");
		sbSQL.append("  	ra.id_infracao is not null  ");
		sbSQL.append("  	and i.id_processo = 25  ");
		sbSQL.append("  	and i.data between @data_ini and @data_fim ");
		sbSQL.append("  order by 2 ");

		
		//Buscar os erros baseados nas infrações pré-seleciondas
		sbSQL.append("  SELECT ");
		sbSQL.append("  	CASE WHEN serie_equipamento < 9907000 THEN 'ESTÁTICO' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907000 AND 9907099 THEN 'GRUPO B' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907100 AND 9907299 THEN 'GRUPO A' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9908100 AND 9908199 THEN 'BARREIRA' ");
		sbSQL.append("  		 WHEN serie_equipamento >= 2014000000 THEN 'GRUPO C' ");
		sbSQL.append("  	END AS tipoEquipamento, ");
		sbSQL.append("  	COUNT(id_infracao) AS qtdeErros ");
		
		sbSQL.append("  FROM ( ");
		sbSQL.append("           SELECT  DISTINCT ");
		sbSQL.append("           	i.id_infracao, ");
		sbSQL.append("           	eri.tipo_apait, ");
		sbSQL.append("           	r.codigo_externo, ");
		sbSQL.append("           	inf.tipo_validacao, ");
		sbSQL.append("           	ip_v.erro_oblit, ");
		sbSQL.append("           	COALESCE(mi.placa, v.placa, REPLICATE(' ', 7)) AS placa,  ");
		sbSQL.append("           	i.placa AS placa_digitada, ");
		sbSQL.append("           	mi.id_marca_cet AS id_marca,  ");
		sbSQL.append("           	cmc.descricao AS marca_cet, ");
		sbSQL.append("           	cmc_d.descricao AS marca_cet_val, ");
		sbSQL.append("           	mi.id_local AS cod_pista,  ");
		sbSQL.append("           	mi.descricao_local AS nome_pista, ");
		sbSQL.append("           	mi.cod_pista_prodam AS cod_pista_prodam, ");
		sbSQL.append("           	i.id_inconsistencia,  ");
		sbSQL.append("           	inc_v.descricao AS inconsistencia_validacao, ");
		sbSQL.append("           	inc_l.descricao AS inconsistencia_liberacao, ");
		sbSQL.append("           	cem.serie_equipamento ");
					
		sbSQL.append("           FROM infracao i (NOLOCK)  ");
		sbSQL.append("           	INNER JOIN infracao_remessa ir (NOLOCK)  ");
		sbSQL.append("           		ON i.id_infracao = ir.id_infracao  ");
		sbSQL.append("           	INNER JOIN remessa r (NOLOCK)  ");
		sbSQL.append("           		ON r.id_remessa = ir.id_remessa  ");
		sbSQL.append("           	INNER JOIN movimento_importacao mi (NOLOCK) ");
//		sbSQL.append("           		ON r.codigo_externo = mi.id_movimento ");
//		sbSQL.append("           		AND r.id_enquadramento = mi.id_enquadramento ");
		sbSQL.append("  				ON r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("           		AND ir.sequencia = mi.sequencia ");
		sbSQL.append("           	INNER JOIN veiculo v (NOLOCK)  ");
		sbSQL.append("           		ON i.id_veiculo = v.id_veiculo ");  
		sbSQL.append("           	INNER JOIN enquadramento_regra_infracao eri (NOLOCK)  ");
		sbSQL.append("           		ON eri.id_enquadramento = r.id_enquadramento ");
		sbSQL.append("           		AND eri.tipo_apait = r.tipo ");
		sbSQL.append("           	LEFT JOIN cad_veiculo cv  (NOLOCK)  ");
		sbSQL.append("           		ON cv.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN cad_veiculo cvv (NOLOCK)  ");
		sbSQL.append("           		ON cvv.placa = v.placa  ");
		sbSQL.append("           	LEFT JOIN cad_marca_cet_processo cmcetp (NOLOCK)  ");
		sbSQL.append("           		ON cmcetp.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN cad_especie_processo ep (NOLOCK)  ");
		sbSQL.append("           		ON ep.placa = i.placa  ");
		sbSQL.append("           	LEFT JOIN infracao_processo_concluido ipc_l (NOLOCK)  ");
		sbSQL.append("           		ON	i.id_infracao = ipc_l.id_infracao  ");
		sbSQL.append("           		AND ipc_l.id_processo = 11  ");
		sbSQL.append("           	JOIN infracao_processo ip_v (NOLOCK)  ");
		sbSQL.append("           		ON	ir.id_infracao = ip_v.id_infracao  ");
		sbSQL.append("           		AND ip_v.id_processo = 3 ");
		sbSQL.append("           	JOIN (	SELECT  ");
		sbSQL.append("           				MAX(id_infracao_processo) AS id_infracao_processo,  ");
		sbSQL.append("           				id_infracao,  ");
		sbSQL.append("           				id_processo  ");
		sbSQL.append("           			FROM  ");
		sbSQL.append("           				infracao_processo ip (NOLOCK)  ");
		sbSQL.append("           			GROUP BY  ");
		sbSQL.append("           				id_infracao,  ");
		sbSQL.append("           				id_processo ");
		sbSQL.append("           		) AS sub1  ");
		sbSQL.append("           		ON	ir.id_infracao = sub1.id_infracao  ");
		sbSQL.append("           		AND sub1.id_processo = 3  ");
		sbSQL.append("           		AND ip_v.id_infracao_processo = sub1.id_infracao_processo ");
		sbSQL.append("           	JOIN inconsistencia inc_l (nolock)  ");
		sbSQL.append("           		ON inc_l.id_inconsistencia = COALESCE(ipc_l.id_inconsistencia, mi.[id_inconsistencia])  ");
		sbSQL.append("           	JOIN inconsistencia inc_v (nolock)  ");
		sbSQL.append("           		ON inc_v.id_inconsistencia = ip_v.id_inconsistencia  ");
		sbSQL.append("           	LEFT JOIN infracao_processo_digitacao ipd (NOLOCK)  ");
		sbSQL.append("           		ON ip_v.id_infracao_processo = ipd.id_infracao_processo  ");
		sbSQL.append("           	JOIN cad_marca_cet cmc (NOLOCK)  ");
		sbSQL.append("           		ON mi.id_marca_cet = cmc.id_marca_cet   ");
		sbSQL.append("           	LEFT JOIN cad_marca_cet cmc_d (NOLOCK) ");
		sbSQL.append("           		ON cmc_d.id_marca_cet = ipd.id_marca_cet ");
		
		//Buscar apenas equipamentos em funcionamento - Agrupamento por tipo de equipamento
		//Thiago Surgik - 12/02/2014
		sbSQL.append("           	INNER JOIN "+tabela_medicao+" cem (NOLOCK) ");
		sbSQL.append("           		ON cem.cod_pista = i.id_local ");
		sbSQL.append("           		AND cem.cod_pista_alternativo = i.pista ");
		
		
		//Nova Regra para excluir infrações dentro da amostra ou com validçaão 100%
		//Luiz Amaral 09/01/2015
		sbSQL.append("           	INNER JOIN #infracoes inf ");
		sbSQL.append("           		ON inf.id_infracao = i.id_infracao ");
		
		
		sbSQL.append("           	WHERE  ");
		sbSQL.append("           	i.data BETWEEN @Data_Ini AND @Data_Fim ");
		sbSQL.append("           	AND	i.id_processo = 25 ");

		sbSQL.append("           	AND   ");
		sbSQL.append("           	(  ");
		sbSQL.append("           		((i.id_inconsistencia = 0 AND mi.id_inconsistencia <> 0) OR (mi.id_inconsistencia = 0 AND i.id_inconsistencia <> 0))  ");
		
		
//--------------------------------------------------------------------------------------------------------------------------------------------------------		
		//Nova Regra definida pelo Rafael Lima e Thiago Hidalgo - Email 05/12/2014
		//Se Inconsistente CAI e Inconsistente CAV, então não contabiliza alterações
		if(ano > 2014 || mes >= 11){
			sbSQL.append("           			OR ( ");
			sbSQL.append("           				(i.id_inconsistencia = 0 and mi.id_inconsistencia = 0) ");
			sbSQL.append("           				AND ( ");
			sbSQL.append("           				 		(mi.placa <> REPLICATE(' ', 7) AND i.placa <> REPLICATE(' ', 7) AND mi.placa <> i.placa)  ");
			sbSQL.append("           						OR (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet)  ");
			sbSQL.append("           						OR (ip_v.erro_oblit = 1)   ");
			sbSQL.append("           					) ");
			sbSQL.append("           				) ");
			
		}else{
			sbSQL.append("           		OR (mi.placa <> REPLICATE(' ', 7) AND i.placa <> REPLICATE(' ', 7) AND mi.placa <> i.placa) ");
			sbSQL.append("           		OR (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet) ");
			sbSQL.append("           		OR (ip_v.erro_oblit = 1)  ");
		}
//--------------------------------------------------------------------------------------------------------------------------------------------------------		
		sbSQL.append("           			) ");
		
		
		
		sbSQL.append("  ) AS totalErros ");

		sbSQL.append("  GROUP BY ");
		sbSQL.append("  	CASE WHEN serie_equipamento < 9907000 THEN 'ESTÁTICO' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907000 AND 9907099 THEN 'GRUPO B' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907100 AND 9907299 THEN 'GRUPO A' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9908100 AND 9908199 THEN 'BARREIRA' ");
		sbSQL.append("  		 WHEN serie_equipamento >= 2014000000 THEN 'GRUPO C' ");
		sbSQL.append("  	END ");
		
		sbSQL.append("  ORDER BY ");
		sbSQL.append("  	CASE WHEN serie_equipamento < 9907000 THEN 'ESTÁTICO' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907000 AND 9907099 THEN 'GRUPO B' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9907100 AND 9907299 THEN 'GRUPO A' ");
		sbSQL.append("  		 WHEN serie_equipamento BETWEEN 9908100 AND 9908199 THEN 'BARREIRA' ");
		sbSQL.append("  		 WHEN serie_equipamento >= 2014000000 THEN 'GRUPO C' ");
		sbSQL.append("  	END ");
		
		sbSQL.append("  SET NOCOUNT OFF ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ErrosValidacao> listErrosValidacao =  new ArrayList<ErrosValidacao>();
		ErrosValidacao errosValidacao;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){			
				errosValidacao = new ErrosValidacao(
													rs.getString("tipoEquipamento"),
													rs.getInt("qtdeErros")
												    );
				listErrosValidacao.add(errosValidacao);
			}
			
			return listErrosValidacao;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Funcionamento FIXO
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 09/09/2014
	 */
	public ArrayList<ItemMedicao> funcionamentoFixo(Long mes,
											        Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("funcionamentoFixo(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" 			Declare @Mes			int = ?   ");
		sbSQL.append(" 			Declare @Ano		    int = ?   ");
		sbSQL.append(" 			Declare @Data_Ini	varchar(10)   ");
		sbSQL.append(" 			Declare @Data_Fim	varchar(22)   ");
		sbSQL.append(" 			Declare @Dias_Mes	decimal(2,0)   ");

		sbSQL.append(" 			set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'   ");
		sbSQL.append(" 			set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))   ");
		sbSQL.append(" 			set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar)  ");

		sbSQL.append(" 			SELECT *  ");
		sbSQL.append(" 			FROM  (  ");
		sbSQL.append(" 						select  ");
		sbSQL.append(" 							cem.cod_pista_prodam						as 'Codigo_Pista_Prodam',  ");
		sbSQL.append(" 							CASE WHEN cem.entre_faixa = 1  ");
		sbSQL.append(" 								 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E'  ");
		sbSQL.append(" 								 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2))  ");
		sbSQL.append(" 							END											as 'Faixa',  ");
//		sbSQL.append(" 							cem.cod_pista_alternativo					as 'Faixa',  ");
		sbSQL.append(" 							cem.serie_equipamento						as 'Serie_Equipamento',  ");
		sbSQL.append(" 							cem.cod_pista								as 'Codigo_Pista',  ");
		sbSQL.append(" 							cem.descricao								as 'Local',  ");
		sbSQL.append(" 							DATEPART(DD,ve.data)						as 'Dia',  ");
		sbSQL.append(" 							ve.id_veiculo_unic							as 'Quantidade',  ");
		sbSQL.append(" 							case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento,  ");
		sbSQL.append(" 							convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao' ");
		    			
		sbSQL.append(" 						from "+tabela_medicao+" cem  ");
		sbSQL.append(" 							left join veiculo_estatistica_medicao ve (nolock)  ");
		sbSQL.append(" 								on ve.id_local = cem.cod_pista  ");
		sbSQL.append(" 								and ve.pista = cem.cod_pista_alternativo  ");
		sbSQL.append(" 								and (cem.data_inicio <= ve.data or ve.data is null)  ");
		sbSQL.append(" 								and ve.id_local > 0  ");
		sbSQL.append(" 								and cast(ve.data as datetime) BETWEEN @Data_Ini AND @Data_Fim ");
		sbSQL.append(" 						where   ");
		sbSQL.append(" 							 cem.cod_pista_prodam > 0 ");
		sbSQL.append("             				 and cem.data_inicio <= @Data_Fim ");

		sbSQL.append(" 					) as contagem  ");
		sbSQL.append(" 			PIVOT  ");
		sbSQL.append(" 					(  ");
		sbSQL.append(" 						count(Contagem.Quantidade)  ");
		sbSQL.append(" 						FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  ");
		sbSQL.append(" 					) AS contagem_dia  ");
		sbSQL.append(" 			order by  ");
		sbSQL.append(" 					Codigo_Pista,  ");
		sbSQL.append(" 					Faixa  ");	
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listFuncionamento =  new ArrayList<ItemMedicao>();
		ItemMedicao funcionamentoFixo;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					
					if(rs.getLong(String.valueOf(i)) == 0 && rs.getInt("flagFuncionamento") > i){
						celulas[i-1] = (long) -1;
					}else{
						celulas[i-1] = rs.getLong(String.valueOf(i));
					}
				}
				
				funcionamentoFixo = new ItemMedicao(
												rs.getInt("Codigo_Pista_Prodam"),
												rs.getString("Faixa"),
												rs.getLong("Serie_Equipamento"),
												rs.getInt("Codigo_Pista"),
												rs.getString("Local"),
												rs.getString("Data_Publicacao"),
												celulas
												);

				
				listFuncionamento.add(funcionamentoFixo);
			}
			
			return listFuncionamento;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Funcionamento FIXO conforme arquivos de 4 minutos
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 20/10/2014
	 */
	public ArrayList<ItemMedicao> funcionamentoFixo4Minutos(Long mes,
											                Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("funcionamentoFixo4Minutos(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes		int = ?  ");
		sbSQL.append(" Declare @Ano		int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10)  ");
		sbSQL.append(" Declare @Data_Fim	varchar(22)  ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar)  ");

		sbSQL.append(" select *from (  ");
		sbSQL.append(" 	select   ");
		sbSQL.append(" 			cem.cod_pista_prodam						as 'Codigo_Pista_Prodam',  ");
		sbSQL.append(" 			CASE WHEN cem.entre_faixa = 1  ");
		sbSQL.append(" 				 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E'  ");
		sbSQL.append(" 				 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2))  ");
		sbSQL.append(" 			END											as 'Faixa',  ");
//		sbSQL.append(" 			cem.cod_pista_alternativo					as 'Faixa',  ");
		sbSQL.append(" 			cem.serie_equipamento						as 'Serie_Equipamento',  ");
		sbSQL.append(" 			cem.cod_pista								as 'Codigo_Pista',  ");
		sbSQL.append(" 			cem.descricao								as 'Local',  ");
		sbSQL.append(" 			DATEPART(DD,ai.data_arquivo)				as 'Dia',  ");
		sbSQL.append(" 			ai.id_arquivo								as 'Quantidade',  ");
		sbSQL.append(" 			case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end as 'flagFuncionamento',  ");
		sbSQL.append(" 			convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao' ");

		sbSQL.append(" 	from "+tabela_medicao+" cem  ");
		sbSQL.append(" 		left join arquivos_importados ai (nolock)  ");
		sbSQL.append(" 			on ai.id_local = cem.cod_pista  ");
		sbSQL.append(" 			and (ai.data_arquivo >= cem.data_inicio or ai.data_arquivo is null)  ");
		sbSQL.append(" 			and ai.nome_arquivo like 'DT%'   ");
		sbSQL.append(" 			and ai.data_arquivo between @Data_ini and @Data_fim   ");
		sbSQL.append(" 	where cem.cod_pista_prodam > 0  ");
		sbSQL.append("        and cem.data_inicio <= @Data_Fim  ");
		sbSQL.append(" 	   and cem.id_produto in (1,2) ");
		sbSQL.append(" 	group by  ");
		sbSQL.append(" 		cem.cod_pista,  ");
		sbSQL.append(" 		cem.cod_pista_prodam, ");
		sbSQL.append(" 		ai.id_arquivo,  ");
		sbSQL.append(" 		DATEPART(DD,ai.data_arquivo), ");
		sbSQL.append(" 		cem.descricao,  ");
		sbSQL.append(" 		cem.cod_pista_alternativo,  ");
		sbSQL.append(" 		cem.serie_equipamento,  ");
		sbSQL.append(" 		cem.data_inicio,  ");
		sbSQL.append(" 		cem.entre_faixa,  ");
		sbSQL.append(" 		cem.cod_pista_tarja  ");
		sbSQL.append(" ) as contagem  ");
		sbSQL.append(" PIVOT  ");
		sbSQL.append(" 	(  ");
		sbSQL.append(" 		 count(contagem.Quantidade)  ");
		sbSQL.append(" 		 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  ");
		sbSQL.append(" 	) AS contagem_dia  ");
			
		sbSQL.append(" order by Codigo_Pista desc  ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listFuncionamento =  new ArrayList<ItemMedicao>();
		ItemMedicao funcionamentoFixo;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					
					if(rs.getLong(String.valueOf(i)) == 0 && rs.getInt("flagFuncionamento") > i){
						celulas[i-1] = (long) -1;
					}else{
						celulas[i-1] = rs.getLong(String.valueOf(i));
					}
				}
				
				funcionamentoFixo = new ItemMedicao(
												rs.getInt("Codigo_Pista_Prodam"),
												rs.getString("Faixa"),
												rs.getLong("Serie_Equipamento"),
												rs.getInt("Codigo_Pista"),
												rs.getString("Local"),
												rs.getString("Data_Publicacao"),
												celulas
												);

				
				listFuncionamento.add(funcionamentoFixo);
			}
			
			return listFuncionamento;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	

	
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Funcionamento ESTATICO
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 12/09/2014
	 */
	public ArrayList<ItemMedicao> funcionamentoEstatico(Long mes,
											    		Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("funcionamentoEstatico(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" 	Declare @Ano		int = ?  ");
		sbSQL.append(" 	Declare @Mes		int = ?  ");
		sbSQL.append(" 	Declare @Data_Ini	varchar(10)  ");
		sbSQL.append(" 	Declare @Data_Fim	varchar(22)  ");
		sbSQL.append(" 	Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append(" 	set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append(" 	set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" 	set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");

		//Parte responsavel por trazer a agenda do mês
		sbSQL.append(" 	select  ");
		sbSQL.append(" 		ae.id_local, ");
		sbSQL.append(" 		ae.data_referencia, ");
		sbSQL.append(" 		aei.sequencia, ");
		sbSQL.append(" 		sum(datediff(HOUR, aei.hora_inicio, aei.hora_fim)+1) as horas_agenda, ");
		sbSQL.append(" 		aei.horas_funcionamento ");
		sbSQL.append(" 		into #result1 ");
		sbSQL.append(" 	from agenda_estatico ae ");
		sbSQL.append(" 		inner join agenda_estatico_item aei ");
		sbSQL.append(" 			on ae.id_agenda_estatico = aei.id_agenda_estatico ");
		sbSQL.append(" 	where  ");
		sbSQL.append(" 		ae.data_referencia between @Data_Ini and @Data_Fim ");
		sbSQL.append("      and aei.status = 1 ");
		sbSQL.append(" 	group by  ");
		sbSQL.append(" 		ae.id_local, "); 
		sbSQL.append(" 		ae.data_referencia, ");
		sbSQL.append(" 		aei.sequencia, ");
		sbSQL.append(" 		aei.horas_funcionamento ");
		sbSQL.append(" 	order by  ");
		sbSQL.append(" 		ae.id_local, ");
		sbSQL.append(" 		ae.data_referencia ");

		//Esta parte é reponsavel por agrupar as horas de funcionamento por dia 
		sbSQL.append(" 	select  ");
		sbSQL.append(" 		r.id_local, ");
		sbSQL.append("		cem.id_local_principal, ");					
		sbSQL.append(" 		cem.descricao, ");
		sbSQL.append(" 		day(convert(varchar(10),r.data_referencia,120)) as 'dia', ");
		sbSQL.append(" 		convert(varchar(10),cem.data_inicio,103) as data_publicacao, ");
		sbSQL.append(" 		@Data_Ini as dtRelatorio, ");
		sbSQL.append(" 		case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento, ");
		sbSQL.append(" 		sum(r.horas_funcionamento) as horas_funcionamento,  ");
		sbSQL.append(" 		sum(r.horas_agenda) as horas_agenda,  ");
		sbSQL.append(" 		cem.cod_pista,  ");
		sbSQL.append(" 		cem.cod_pista_prodam,  ");
		sbSQL.append(" 		CASE WHEN cem.entre_faixa = 1  ");
		sbSQL.append(" 			 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E'  ");
		sbSQL.append(" 			 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2))  ");
		sbSQL.append(" 		END AS faixa  ");
		sbSQL.append(" 		into #resultComFxExclusiva ");
		sbSQL.append(" 	from #result1 r ");
		sbSQL.append(" 		inner join "+tabela_medicao+" cem ");
		sbSQL.append(" 			on cem.id_local = r.id_local ");
		sbSQL.append(" 			and cem.cod_pista_alternativo = ( ");
		sbSQL.append(" 												select  ");
		sbSQL.append(" 													min(cod_pista_alternativo) as cod_pista_alternativo ");
		sbSQL.append(" 												from "+tabela_medicao+" aux ");
		sbSQL.append(" 												where cem.id_local = aux.id_local ");
		sbSQL.append("											) ");
		sbSQL.append(" 	group by  ");
		sbSQL.append(" 		r.id_local, ");
		sbSQL.append(" 		cem.id_local_principal, ");
		sbSQL.append(" 		r.data_referencia, ");
		sbSQL.append(" 		cem.descricao, ");
		sbSQL.append(" 		cem.data_inicio, ");
		sbSQL.append(" 		cem.cod_pista,  ");
		sbSQL.append(" 		cem.cod_pista_prodam,  ");
		sbSQL.append(" 		cem.entre_faixa,  ");
		sbSQL.append(" 		cem.cod_pista_tarja  ");
		
		
		//Esta parte é reponsavel por juntar as horas de funcionamento para impressão
		//NOVA REGRA em 27/04/2015 - Luiz Amaral:
		//							1º: Para os casos de FxExclusiva, apenas trazer o id_local_principal
		//							2º: Verificar as horas destes locais e imprimir apenas o maior (obedecendo o campo id_local_principal)
		sbSQL.append(" 	select *from    ");
		sbSQL.append(" 			(   ");
		sbSQL.append(" 						select   ");
		sbSQL.append(" 							t.id_local_principal as id_local,  ");
		sbSQL.append(" 							cem.descricao,  ");
		sbSQL.append(" 							t.dia,  ");
		sbSQL.append(" 							t.data_publicacao,  ");
		sbSQL.append(" 							t.dtRelatorio,   ");
		sbSQL.append(" 							t.flagFuncionamento,  ");
		sbSQL.append(" 							(cast(max(t.horas_funcionamento) as varchar(2)) + 'h / ' + cast(max(t.horas_agenda) as varchar(2)) + 'h' )  as horas_total,   ");
		sbSQL.append(" 							t.cod_pista,   ");
		sbSQL.append(" 							t.cod_pista_prodam,   ");
		sbSQL.append(" 							t.faixa   ");
		sbSQL.append(" 						from #resultComFxExclusiva t  ");
		sbSQL.append(" 							inner join "+tabela_medicao+" cem  ");
		sbSQL.append(" 								on cem.id_local = t.id_local_principal  ");
		sbSQL.append(" 						group by   ");
		sbSQL.append(" 							t.id_local_principal,  ");
		sbSQL.append(" 							cem.descricao,  ");
		sbSQL.append(" 							t.dia,  ");
		sbSQL.append(" 							t.data_publicacao,  ");
		sbSQL.append(" 							t.dtRelatorio,   ");
		sbSQL.append(" 							t.flagFuncionamento,  ");
		sbSQL.append(" 							t.cod_pista,   ");
		sbSQL.append(" 							t.cod_pista_prodam,   ");
		sbSQL.append(" 							t.faixa   ");
		sbSQL.append(" 			) as contagem   ");
		sbSQL.append(" 	PIVOT   ");
		sbSQL.append(" 		(   ");
		sbSQL.append(" 			max(contagem.horas_total)    ");
		sbSQL.append(" 			FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])   ");
		sbSQL.append(" 		) AS contagem_dia   ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listFuncionamento =  new ArrayList<ItemMedicao>();
		ItemMedicao funcionamento;
		String[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new String[31];
				for(int i=1; i<32; i++){
					if(rs.getString(String.valueOf(i)) == null && rs.getInt("flagFuncionamento") > i){
						celulas[i-1] = "-1";
					}else if (rs.getString(String.valueOf(i)) == null){
						celulas[i-1] = "0";
					}else{
						celulas[i-1] = rs.getString(String.valueOf(i));
					}

				}
				
				funcionamento = new ItemMedicao(
												rs.getInt("id_local"),
												rs.getString("descricao"),
												rs.getString("data_publicacao"),
												celulas,
												rs.getInt("cod_pista_prodam"),
												rs.getString("faixa")
												);
				
				listFuncionamento.add(funcionamento);
			}
			
			return listFuncionamento;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
		
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Total de Infrações Estatico
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 08/10/2014
	 */
	public ArrayList<ItemMedicao> totalInfracoesEstatico(Long mes,
											    		 Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("totalInfracoesEstatico(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes			int = ? ");
		sbSQL.append(" Declare @Ano		    int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(10) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM ( ");
		sbSQL.append("        	select ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append("     			DATEPART(DD,i.data)							as 'Dia', ");
		sbSQL.append(" 				i.id_infracao								as 'Quantidade', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append(" 				case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento "); 
						
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		sbSQL.append(" 			     left join agenda_estatico ae ");
		sbSQL.append(" 					   ON  ae.id_local = cem.id_local ");
		sbSQL.append(" 				 left join agenda_estatico_item aei ");
		sbSQL.append(" 					   ON  aei.id_agenda_estatico = ae.id_agenda_estatico ");
		sbSQL.append("                     AND aei.status = 1 " );
		sbSQL.append(" 				left join infracao i (nolock) ");
		sbSQL.append(" 					on i.id_local = cem.cod_pista ");
		sbSQL.append(" 						and i.pista = cem.cod_pista_alternativo ");
		sbSQL.append(" 						and (i.data >= cem.data_inicio or i.data is null) ");
		sbSQL.append(" 						and (cast(i.data as date) BETWEEN @Data_Ini AND @Data_Fim or i.data is null)  ");
		sbSQL.append(" 						and ( ");
		sbSQL.append("                   			i.data IS NULL OR ");
		sbSQL.append("                   			(CAST(i.data AS DATE) = aei.data_operacao ");
		sbSQL.append("                   			AND CAST(i.data AS TIME(0)) BETWEEN CAST(aei.hora_inicio AS TIME(0)) AND CAST(aei.hora_fim AS TIME(0)))  ");
		sbSQL.append(" 							) ");
						
		sbSQL.append(" 			where ");
		sbSQL.append(" 			   cem.id_produto = 3 ");
		sbSQL.append("             and cem.data_inicio <= @Data_Fim ");
						
		sbSQL.append(" 			group by ");
		sbSQL.append(" 				cem.cod_pista_alternativo, "); 
		sbSQL.append(" 				cem.cod_pista, ");
		sbSQL.append(" 				cem.descricao, ");
		sbSQL.append(" 				DATEPART(DD,i.data), ");
		sbSQL.append(" 				i.id_infracao, ");
		sbSQL.append(" 				cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");
		
		sbSQL.append(" UNION ALL ");

		sbSQL.append("        	select  ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa',  ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista',  ");
		sbSQL.append(" 				cem.descricao								as 'Local',  ");
		sbSQL.append("     			DATEPART(DD,it.data)						as 'Dia',  ");
		sbSQL.append(" 				it.id_infracao_cai							as 'Quantidade',  ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao',  ");
		sbSQL.append(" 				case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento  "); 
						
		sbSQL.append(" 			from "+tabela_medicao+" cem (nolock) ");
		sbSQL.append(" 			     left join agenda_estatico ae (nolock) ");
		sbSQL.append(" 					   ON  ae.id_local = cem.id_local  ");
		sbSQL.append(" 				 left join agenda_estatico_item aei (nolock) ");
		sbSQL.append(" 					   ON  aei.id_agenda_estatico = ae.id_agenda_estatico  ");
		sbSQL.append(" 				left join imagem_teste it (nolock)  ");
		sbSQL.append(" 					on it.id_local = cem.cod_pista  ");
		sbSQL.append(" 						and it.cod_pista_alternativo = cem.cod_pista_alternativo  ");
		sbSQL.append(" 						and (it.data >= cem.data_inicio or it.data is null)  ");
		sbSQL.append(" 						and (cast(it.data as date) BETWEEN @Data_Ini AND @Data_Fim or it.data is null)   ");
		sbSQL.append(" 						and (  ");
		sbSQL.append("                   			it.data IS NULL OR  ");
		sbSQL.append("                   			(CAST(it.data AS DATE) = aei.data_operacao  ");
		sbSQL.append("                   			AND CAST(it.data AS TIME(0)) BETWEEN CAST(aei.hora_inicio AS TIME(0)) AND CAST(aei.hora_fim AS TIME(0)))   ");
		sbSQL.append(" 							) 				 ");		
		sbSQL.append(" 			where  ");
		sbSQL.append(" 			   cem.id_produto = 3  ");
		sbSQL.append("             and cem.data_inicio <= @Data_Fim ");
		
		sbSQL.append(" 			group by  ");
		sbSQL.append(" 				cem.cod_pista_alternativo,   ");
		sbSQL.append(" 				cem.cod_pista,  ");
		sbSQL.append(" 				cem.descricao,  ");
		sbSQL.append(" 				DATEPART(DD,it.data),  ");
		sbSQL.append(" 				it.id_infracao_cai,  ");
		sbSQL.append(" 				cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");

		sbSQL.append(" 		) as Contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Quantidade) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) "); 
		sbSQL.append(" 		) AS contagem_dia ");

		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, "); 
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTotalInfracoes =  new ArrayList<ItemMedicao>();
		ItemMedicao infracoes;
		Long[] celulas;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				infracoes = new ItemMedicao(
						rs.getString("Faixa"),
						rs.getInt("Codigo_Pista"),
						rs.getString("Local"),
						rs.getString("Data_Publicacao"),
						rs.getInt("flagFuncionamento"),
						celulas
						);					
				
				listTotalInfracoes.add(infracoes);
			}
			
			return listTotalInfracoes;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	@SuppressWarnings("unused")
	public Integer obterUltimoDiaMes(){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime( new Date() );
		         
		int dia = cal.getActualMaximum( Calendar.DAY_OF_MONTH );
		int mes = (cal.get(Calendar.MONDAY)+1);
		int ano = cal.get(Calendar.YEAR);
		         
		try {
		    Date data = (new SimpleDateFormat("dd/MM/yyyy")).parse( dia+"/"+mes+"/"+ano );
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 * Busca TOTAL de VOLUME de Veiculos para os ESTATICOS do dia no BD.
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 15/10/2014
	 */
	public ArrayList<ItemMedicao> VolumePorDiaEstatico_CAV(Long mes, Long ano, String velsis) throws ConexaoException, SQLException, ModelException {
		
		logger.info("VolumePorDiaEstatico(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" Declare @Ano		    int = ?  ");
		sbSQL.append(" Declare @Mes			int = ?  ");
		sbSQL.append(" Declare @Data_Ini	varchar(10)  ");
		sbSQL.append(" Declare @Data_Fim	varchar(22)  ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0)  ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01'  ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM  ( ");
		sbSQL.append(" 			select ");
		sbSQL.append(" 				cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append(" 				cem.serie_equipamento						as 'Serie_Equipamento', ");
		sbSQL.append(" 				DATEPART(DD,ve.data)						as 'Dia', ");
		sbSQL.append(" 				ve.id_veiculo_unic							as 'Trafego', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append("     			case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento ");
		sbSQL.append(" 			from "+tabela_medicao+" cem (nolock) ");
		sbSQL.append("     			left join veiculo_estatistica_medicao ve (nolock)  ");
		sbSQL.append("     			on ve.id_local = cem.cod_pista  ");
		sbSQL.append("     			and ve.pista = cem.cod_pista_alternativo  ");
		sbSQL.append("     			and (cem.data_inicio <= ve.data or ve.data is null)  ");
		sbSQL.append("     			and ve.id_local > 0  ");
		sbSQL.append("     			and cast(ve.data as datetime) BETWEEN @Data_Ini AND @Data_Fim ");
		sbSQL.append("     		where   ");
		sbSQL.append("     			cem.cod_pista_prodam = 0 ");
		sbSQL.append("     			and cem.id_produto = 3 ");
		sbSQL.append("             	and cem.data_inicio <= @Data_Fim ");
		sbSQL.append(" 		) as contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Trafego) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 		) AS contagem_dia ");
		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, ");
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTrafego =  new ArrayList<ItemMedicao>();
		ItemMedicao trafego;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				trafego = new ItemMedicao(
											rs.getInt("Codigo_Pista_Prodam"),
											rs.getString("Faixa"),
											rs.getLong("Serie_Equipamento"),
											rs.getInt("Codigo_Pista"),
											rs.getString("Local"),
											rs.getString("Data_Publicacao"),
											celulas,
											rs.getInt("flagFuncionamento")
										);
				
				listTrafego.add(trafego);
			}
			
			return listTrafego;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Busca TOTAL de VOLUME de Veiculos para os ESTATICOS do dia no BD.
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 15/10/2014
	 */
	public ArrayList<ItemMedicao> VolumePorDiaEstatico_CAI(Long mes, Long ano, String velsis) throws ConexaoException, SQLException, ModelException {
		
		logger.info("VolumePorDiaEstatico_CAI(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" DECLARE @Ano		    INT = ?  ");
		sbSQL.append(" DECLARE @Mes			INT = ?  ");
		sbSQL.append(" DECLARE @Data_Ini	VARCHAR(10)  ");
		sbSQL.append(" DECLARE @Data_Fim	VARCHAR(22)  ");
		sbSQL.append(" DECLARE @Dias_Mes	DECIMAL(2,0)  ");

		sbSQL.append(" SET @Data_Ini = CAST(@Ano AS VARCHAR)+'-'+CAST(@Mes AS VARCHAR)+'-01'  ");
		sbSQL.append(" SET @Dias_Mes = DAY(DATEADD(mm,1,@Data_Ini)-DAY(dateadd(mm,1,@Data_Ini)))  ");
		sbSQL.append(" SET @Data_Fim = CAST(@Ano AS VARCHAR)+'-'+CAST(@Mes AS VARCHAR)+'-'+CAST(@Dias_Mes AS VARCHAR)+CAST(' 23:59:59.000' AS VARCHAR) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM  ( ");
		sbSQL.append(" 			SELECT ");
		sbSQL.append(" 				cem.cod_pista_prodam						AS 'Codigo_Pista_Prodam', ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											AS 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								AS 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								AS 'Local', ");
		sbSQL.append(" 				cem.serie_equipamento						AS 'Serie_Equipamento', ");
		sbSQL.append(" 				DATEPART(DD,ve.data)						AS 'Dia', ");
		sbSQL.append(" 				ve.trafego									AS 'Trafego', ");
		sbSQL.append(" 				CONVERT(VARCHAR(10), cem.data_inicio, 103)	AS 'Data_Publicacao', ");
		sbSQL.append("     			CASE WHEN cem.data_inicio > @Data_Ini THEN DAY(cem.data_inicio) ELSE 0 END AS flagFuncionamento ");
		sbSQL.append(" 			FROM "+tabela_medicao+" cem (NOLOCK) ");
		sbSQL.append("     			LEFT JOIN veiculo_sumarizado ve (NOLOCK)  ");
		sbSQL.append("     			ON ve.id_local = cem.id_local  ");
		sbSQL.append("     			AND ve.pista = cem.id_pista  ");
		sbSQL.append("     			AND ve.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE) ");
		sbSQL.append("     		WHERE   ");
		sbSQL.append("     			cem.cod_pista_prodam = 0 ");
		sbSQL.append("     			AND cem.id_produto = 3 ");
		sbSQL.append("             	AND cem.data_inicio <= @Data_Fim ");
		sbSQL.append("				AND cem.atualizar = 1 ");
		sbSQL.append(" 		) AS contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			COUNT(Contagem.Trafego) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) ");
		sbSQL.append(" 		) AS contagem_dia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		Codigo_Pista, ");
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTrafego =  new ArrayList<ItemMedicao>();
		ItemMedicao trafego;
		Long[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				trafego = new ItemMedicao(
											rs.getInt("Codigo_Pista_Prodam"),
											rs.getString("Faixa"),
											rs.getLong("Serie_Equipamento"),
											rs.getInt("Codigo_Pista"),
											rs.getString("Local"),
											rs.getString("Data_Publicacao"),
											celulas,
											rs.getInt("flagFuncionamento")
										);
				
				listTrafego.add(trafego);
			}
			
			return listTrafego;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	


	/**
	 * Gravar histórico da geração do relatório no banco.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/11/2014
	 */
	public void gravarHistoricoGeracao(HttpServletResponse response, 
									   Date dtIni,
									   Date dtFim,
									   String nomeArquivo,
									   String tipoArquivo,
									   Integer idUsuario,
									   Date data) throws ConexaoException, SQLException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		String sbSQL = "";
		
		try {
			
			sbSQL = sbSQL + " INSERT INTO medicao_historico_geracao ";
			sbSQL = sbSQL + " 	  ( ";
			sbSQL = sbSQL + "	  	  tipo_relatorio ";
			sbSQL = sbSQL + "	  	 ,nome_relatorio ";
			sbSQL = sbSQL + "	  	 ,data_inicio ";
			sbSQL = sbSQL + "	  	 ,data_fim ";
			sbSQL = sbSQL + "	  	 ,id_usuario ";
			sbSQL = sbSQL + "	  	 ,data_geracao ";
			sbSQL = sbSQL + " 	  ) ";
			sbSQL = sbSQL + " VALUES ";
			sbSQL = sbSQL + " 	  ( ";
			sbSQL = sbSQL + "	  	  ? ";
			sbSQL = sbSQL + "	  	 ,? ";
			sbSQL = sbSQL + "	  	 ,? ";
			sbSQL = sbSQL + "	  	 ,? ";
			sbSQL = sbSQL + "	  	 ,? ";
			sbSQL = sbSQL + "	  	 ,? ";
			sbSQL = sbSQL + " 	  ) ";
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL);
			ps.setString(1, tipoArquivo);
			ps.setString(2, nomeArquivo);
			ps.setDate(3, new java.sql.Date(dtIni.getTime()));
			ps.setDate(4, new java.sql.Date(dtFim.getTime()));
			ps.setInt(5, idUsuario);
			ps.setTimestamp(6, new java.sql.Timestamp(data.getTime()));

			ps.executeUpdate();
			
		}		
		
		finally {
			if (conn != null)
				conn.close();							
		}		
	}
	
	/**
	 * Traz os lotes reprovados no CAV por mês
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 22/04/2015
	 */
	public ArrayList<LotesReprovados> LotesReprovados(Long mes, Long ano) throws ConexaoException, SQLException, ModelException {
		
		logger.info("LotesReprovados(" + mes + "," + ano + ")");
		
		StringBuilder sbSQL = new StringBuilder();
	
		sbSQL.append(" Declare @Ano		int = ? ");
		sbSQL.append(" Declare @Mes		int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(22) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");
	
		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar)+cast(' 23:59:59.000' as varchar) ");
	
		sbSQL.append(" SELECT  ");
		sbSQL.append(" 	SUBSTRING(ma.nome_arquivo,3,2) [Grupo_Autuador],  ");
		sbSQL.append(" 	ma.id_movimento [Movimento],  ");
		sbSQL.append(" 	convert(varchar(10), ma.data_movimento, 103) [Data_Movimento], ");
		sbSQL.append(" 	MAX(ma.revisao) [Ultima_Revisao],  ");
		sbSQL.append(" 	convert(varchar(19), MIN(ma.data_arquivo),121) [Data_Envio_Primeira_Revisao], ");
		sbSQL.append(" 	convert(varchar(19), MAX(ma.data_arquivo),121) [Data_Envio_Ultima_Revisao] ");
		sbSQL.append(" FROM movimento_arquivo ma (NOLOCK)  ");
		sbSQL.append(" WHERE  ");
		sbSQL.append(" 	ma.id_tipo = 5  ");
		sbSQL.append(" 	and ma.data_movimento between @Data_Ini and @Data_Fim ");
		sbSQL.append(" GROUP BY  ");
		sbSQL.append(" 	SUBSTRING(ma.nome_arquivo,3,2),  ");
		sbSQL.append(" 	ma.id_movimento,  ");
		sbSQL.append(" 	ma.data_movimento  ");
		sbSQL.append(" HAVING  ");
		sbSQL.append(" 	COUNT(ma.id_movimento_arquivo) > 1  ");
		sbSQL.append(" 	AND MAX(ma.revisao) > 0  ");
		sbSQL.append(" ORDER BY  ");
		sbSQL.append(" 	ma.data_movimento, ");
		sbSQL.append(" 	SUBSTRING(ma.nome_arquivo,3,2) ");
		
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LotesReprovados> listLotesReprovados =  new ArrayList<LotesReprovados>();
		LotesReprovados lote;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			while (rs.next()){			
				lote = new LotesReprovados(
											rs.getInt("Movimento"),
											rs.getString("Grupo_Autuador"),
											rs.getString("Data_Movimento"),
											rs.getInt("Ultima_Revisao"),
											rs.getString("Data_Envio_Primeira_Revisao"),
											rs.getString("Data_Envio_Ultima_Revisao")
										  );
				
				listLotesReprovados.add(lote);
			}
			
			return listLotesReprovados;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL ao obter LISTA de Lotes Reprovados", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Infrações Consistentes Fixo
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/07/2015
	 */
	public ArrayList<ItemMedicao> infracoesConsistentesFixo(Long mes,
											         		Long ano, 
											         		String velsis) throws ConexaoException, SQLException, ModelException {
		logger.info("infracoesConsistentesFixo(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes			int = ? ");
		sbSQL.append(" Declare @Ano		    int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(10) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM ( ");
		sbSQL.append("        	select ");
				            			
						
		sbSQL.append(" 				cem.cod_pista_prodam						as 'Codigo_Pista_Prodam', ");
		sbSQL.append("             	cem.serie_equipamento						as 'Serie_Equipamento', ");
		sbSQL.append("             	CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append("             		 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append("             		 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append("             	END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append("     			DATEPART(DD,i.data)							as 'Dia', ");
		sbSQL.append(" 				i.id_infracao								as 'Quantidade', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append(" 				case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento "); 
						
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		sbSQL.append("				LEFT JOIN veiculo v (nolock) "); 
		sbSQL.append("				ON v.id_local = cem.cod_pista ");
		sbSQL.append("				AND ((v.entre_faixa IS NULL AND v.pista = cem.cod_pista_alternativo) OR ");
		sbSQL.append("				(v.pista = cem.cod_pista_tarja AND v.entre_faixa = cem.entre_faixa)) ");
		sbSQL.append("				AND (v.data >= cem.data_inicio or v.data is null) ");
		sbSQL.append("				AND (cast(v.data as date) BETWEEN @Data_Ini AND @Data_Fim or v.data is null) ");
		sbSQL.append("			LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append("				ON v.id_veiculo = i.id_veiculo ");
		sbSQL.append(" 				and i.id_inconsistencia = 0 ");
		
		sbSQL.append(" 				left join infracao_remessa ir (nolock) ");
		sbSQL.append(" 					on ir.id_infracao = i.id_infracao ");
		sbSQL.append(" 				left join remessa r (nolock) ");
		sbSQL.append(" 					on r.id_remessa = ir.id_remessa ");
		sbSQL.append(" 						and r.data_confirmacao is not null ");
						
		sbSQL.append(" 			where ");
		sbSQL.append(" 			     cem.cod_pista_prodam > 0 "); 
		sbSQL.append(" 			     and cem.id_produto in (1, 2) ");
		sbSQL.append("               and cem.data_inicio <= @Data_Fim ");
		
		//Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		if(velsis != null){
			sbSQL.append("           and cem.serie_equipamento > 2014000000 ");
		}
						
		sbSQL.append(" 			group by ");
		sbSQL.append(" 				cem.cod_pista_prodam, ");
		sbSQL.append(" 				cem.serie_equipamento, ");
		sbSQL.append(" 				cem.cod_pista_alternativo, "); 
		sbSQL.append(" 				cem.cod_pista, ");
		sbSQL.append(" 				cem.descricao, ");
		sbSQL.append(" 				DATEPART(DD,i.data), ");
		sbSQL.append(" 				i.id_infracao, ");
		sbSQL.append(" 				cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");

		sbSQL.append(" 		) as Contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Quantidade) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) "); 
		sbSQL.append(" 		) AS contagem_dia ");

		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, "); 
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listInfracoesConsistentes =  new ArrayList<ItemMedicao>();
		ItemMedicao infracoes;
		Long[] celulas;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
					infracoes = new ItemMedicao(
													rs.getInt("Codigo_Pista_Prodam"),
													rs.getString("Faixa"),
													rs.getLong("Serie_Equipamento"),
													rs.getInt("Codigo_Pista"),
													rs.getString("Local"),
													rs.getString("Data_Publicacao"),
													rs.getInt("flagFuncionamento"),
													celulas
													);
				
				listInfracoesConsistentes.add(infracoes);
			}
			
			return listInfracoesConsistentes;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	


	/**
	 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Infrações Consistentes Estatico
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/07/2015
	 */
	public ArrayList<ItemMedicao> infracoesConsistentesEstatico(Long mes,
											    		 		Long ano) throws ConexaoException, SQLException, ModelException {
		logger.info("infracoesConsistentesEstatico(" + mes +"," + ano + ")");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.set(ano.intValue(), mes.intValue(), 1);
		c2.set(2016, 11, 30);
		
		String tabela_medicao = "configuracao_equipamento_medicao";
		if (c1.before(c2))
		{
			logger.info("Usando tabela anterior a Dezembro 2016");
			tabela_medicao = "configuracao_equipamento_medicao_20161130";
		}
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Declare @Mes			int = ? ");
		sbSQL.append(" Declare @Ano		    int = ? ");
		sbSQL.append(" Declare @Data_Ini	varchar(10) ");
		sbSQL.append(" Declare @Data_Fim	varchar(10) ");
		sbSQL.append(" Declare @Dias_Mes	decimal(2,0) ");

		sbSQL.append(" set @Data_Ini = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-01' ");
		sbSQL.append(" set @Dias_Mes = day(dateadd(mm,1,@Data_Ini)-day(dateadd(mm,1,@Data_Ini))) ");
		sbSQL.append(" set @Data_Fim = cast(@Ano as varchar)+'-'+cast(@Mes as varchar)+'-'+cast(@Dias_Mes as varchar) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM ( ");
		sbSQL.append("        	select ");
		sbSQL.append(" 				CASE WHEN cem.entre_faixa = 1 ");
		sbSQL.append(" 					 THEN CAST(cem.cod_pista_tarja AS VARCHAR(2)) + 'E' ");
		sbSQL.append(" 					 ELSE CAST(cem.cod_pista_tarja AS VARCHAR(2)) ");
		sbSQL.append(" 				END											as 'Faixa', ");
//		sbSQL.append(" 				cem.cod_pista_alternativo					as 'Faixa', ");
		sbSQL.append(" 				cem.cod_pista								as 'Codigo_Pista', ");
		sbSQL.append(" 				cem.descricao								as 'Local', ");
		sbSQL.append("     			DATEPART(DD,i.data)							as 'Dia', ");
		sbSQL.append(" 				i.id_infracao								as 'Quantidade', ");
		sbSQL.append(" 				convert(varchar(10), cem.data_inicio, 103)	as 'Data_Publicacao', ");
		sbSQL.append(" 				case when cem.data_inicio > @Data_Ini then day(cem.data_inicio) else 0 end flagFuncionamento "); 
						
		sbSQL.append(" 			from "+tabela_medicao+" cem ");
		sbSQL.append(" 			     left join agenda_estatico ae ");
		sbSQL.append(" 					   ON  ae.id_local = cem.id_local ");
		sbSQL.append(" 				 left join agenda_estatico_item aei ");
		sbSQL.append(" 					   ON  aei.id_agenda_estatico = ae.id_agenda_estatico ");
		sbSQL.append("                     AND aei.status = 1 " );
		sbSQL.append(" 				left join infracao i (nolock) ");
		sbSQL.append(" 					on i.id_local = cem.cod_pista ");
		sbSQL.append(" 						and i.pista = cem.cod_pista_alternativo ");
		sbSQL.append(" 						and (i.data >= cem.data_inicio or i.data is null) ");
		sbSQL.append(" 						and (cast(i.data as date) BETWEEN @Data_Ini AND @Data_Fim or i.data is null)  ");
		sbSQL.append(" 						and ( ");
		sbSQL.append("                   			i.data IS NULL OR ");
		sbSQL.append("                   			(CAST(i.data AS DATE) = aei.data_operacao ");
		sbSQL.append("                   			AND CAST(i.data AS TIME(0)) BETWEEN CAST(aei.hora_inicio AS TIME(0)) AND CAST(aei.hora_fim AS TIME(0)))  ");
		sbSQL.append(" 							) ");
		sbSQL.append(" 						and i.id_inconsistencia = 0 "); 
		
		sbSQL.append(" 				left join infracao_remessa ir (nolock) ");
		sbSQL.append(" 					on ir.id_infracao = i.id_infracao ");
		sbSQL.append(" 				left join remessa r (nolock) ");
		sbSQL.append(" 					on r.id_remessa = ir.id_remessa ");
		sbSQL.append(" 						and r.data_confirmacao is not null ");
						
		sbSQL.append(" 			where ");
		sbSQL.append(" 			   cem.id_produto = 3 ");
		sbSQL.append("             and cem.data_inicio <= @Data_Fim ");
						
		sbSQL.append(" 			group by ");
		sbSQL.append(" 				cem.cod_pista_alternativo, "); 
		sbSQL.append(" 				cem.cod_pista, ");
		sbSQL.append(" 				cem.descricao, ");
		sbSQL.append(" 				DATEPART(DD,i.data), ");
		sbSQL.append(" 				i.id_infracao, ");
		sbSQL.append(" 				cem.data_inicio, ");
		sbSQL.append(" 				cem.entre_faixa, ");
		sbSQL.append(" 				cem.cod_pista_tarja ");
		
		sbSQL.append(" 		) as Contagem ");
		sbSQL.append(" PIVOT ");
		sbSQL.append(" 		( ");
		sbSQL.append(" 			count(Contagem.Quantidade) ");
		sbSQL.append(" 			FOR Dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]) "); 
		sbSQL.append(" 		) AS contagem_dia ");

		sbSQL.append(" order by ");
		sbSQL.append(" 		Codigo_Pista, "); 
		sbSQL.append(" 		Faixa ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicao> listTotalInfracoes =  new ArrayList<ItemMedicao>();
		ItemMedicao infracoes;
		Long[] celulas;
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,mes);
			ps.setLong(2,ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				celulas = new Long[31];
				for(int i=1; i<32; i++){
					celulas[i-1] = rs.getLong(String.valueOf(i));
				}
				
				infracoes = new ItemMedicao(
						rs.getString("Faixa"),
						rs.getInt("Codigo_Pista"),
						rs.getString("Local"),
						rs.getString("Data_Publicacao"),
						rs.getInt("flagFuncionamento"),
						celulas
						);					
				
				listTotalInfracoes.add(infracoes);
			}
			
			return listTotalInfracoes;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
}
