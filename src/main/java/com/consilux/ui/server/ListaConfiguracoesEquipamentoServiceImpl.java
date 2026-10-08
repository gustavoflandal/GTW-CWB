package com.consilux.ui.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.lib.Conexao;
import com.consilux.model.Local;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.ListaConfiguracoesEquipamentoService;
import com.consilux.ui.client.beans.ListaConfiguracoesEquipamentoGwtBean;
import com.consilux.ui.client.beans.LocalGwtBean;

public class ListaConfiguracoesEquipamentoServiceImpl extends GwtBaseServlet
implements ListaConfiguracoesEquipamentoService {

	private static final long serialVersionUID = 7123270219314110932L;

	
	private Map<Integer,Object> processarCriterios(Map<String, String> criterios, StringBuilder sbSQL) throws Exception {
		
		// Valida e processa os critérios e gera o WHERE)
		Integer idLocal = null;
		Integer numeroSerie = null;
		Date periodoDe = null;
		Date periodoAte = null;
		
		Map<Integer,Object> mapaParametros = new HashMap<Integer, Object>();
		
		try {
			int i = 1;
			if (criterios.containsKey("idLocal") && criterios.get("idLocal") != null &&
					criterios.get("idLocal").length() > 0) {
				idLocal = Integer.parseInt(criterios.get("idLocal"));
				mapaParametros.put(i++, idLocal);
				sbSQL.append(" AND l.id_local = ?");
			}
			
			if (criterios.containsKey("numeroSerie")&& criterios.get("numeroSerie") != null
					&& criterios.get("numeroSerie").length() > 0) {
				numeroSerie = Integer.parseInt(criterios.get("numeroSerie"));
				mapaParametros.put(i++, numeroSerie);
				sbSQL.append(" AND ce.serie_equipamento = ?");				
			}
			
			if (criterios.containsKey("periodoDe") && criterios.get("periodoDe") != null
					&& criterios.get("periodoDe").length() > 0) {
				periodoDe = new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
				criterios.get("periodoDe") + " 00:00:00").getTime());
			}
			
			if (criterios.containsKey("periodoAte") && criterios.get("periodoAte") != null
					&& criterios.get("periodoAte").length() > 0)			
				periodoAte = new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").parse(
				criterios.get("periodoAte") + " 23:59:59.999").getTime());

			if (periodoDe == null && periodoAte != null || periodoDe != null && periodoAte == null)
				throw new Exception("Para um período, devem se especificadas ambas as datas");
			
			if (periodoDe != null && periodoAte!= null) {
				if (periodoDe.after(periodoAte))
					throw new Exception("Data inicial deve ser anterior a data final.");
				mapaParametros.put(i++, periodoDe);				
				mapaParametros.put(i++, periodoAte);	
				sbSQL.append(" AND cea.data BETWEEN ? AND ?");
			}
			
		} catch (NumberFormatException nfx) {
			throw new Exception("Parâmetro em formato inválido");
		}
		catch (ParseException pax) {
			throw new Exception("Data em formato inválido");			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return mapaParametros;
	}
	
	private void inserirParametros(PreparedStatement ps, Map<Integer,Object> params) throws SQLException {
		for (Integer i : params.keySet()) {
			ps.setObject(i, params.get(i));
		}
	}
	
	@Override
	public List<ListaConfiguracoesEquipamentoGwtBean> listaConfiguracoesEquipamentos(
			Map<String, String> criterios) throws Exception {
		
		verificarSessaoLogada();

		/*
		if (criterios == null || criterios.size() <= 0)
			throw new Exception("Não foram especificados critérios de pesquisa.");
		*/
		
		List<ListaConfiguracoesEquipamentoGwtBean> listaRetorno = 
			new ArrayList<ListaConfiguracoesEquipamentoGwtBean>();
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT  ");
		sbSQL.append("  l.id_local AS CodLocal, ce.serie_equipamento AS NumSerie, ");
		sbSQL.append("  cep.id_pista AS NumPista, l.nome AS DescLocal, ");
		sbSQL.append("  cep.cod_pista_alternativo AS CodPistaAlt, ");
		sbSQL.append("  cep.cod_pista AS CodPista, cep.cod_pista_prodam AS CodPistaAlt2, ");
		sbSQL.append("  cep.nome_pista AS DescPista, cea.data AS DataAfericao, ");
		sbSQL.append("  cecv.distancia_laco AS DistLaco, ");
		sbSQL.append("  ceri.velocidade_limite AS VelLimite ");
		sbSQL.append("FROM ");
		sbSQL.append("  configuracao_equipamento ce WITH (NOLOCK) ");
		sbSQL.append("  INNER JOIN local l WITH (NOLOCK) ");
		sbSQL.append("    ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento ");
		sbSQL.append("  INNER JOIN configuracao_equipamento_pista cep WITH (NOLOCK) ");
		sbSQL.append("    ON ce.id_configuracao_equipamento = cep.id_configuracao_equipamento ");
		sbSQL.append("  INNER JOIN configuracao_equipamento_afericao cea WITH (NOLOCK) ");
		sbSQL.append("    ON cea.id_configuracao_equipamento = ce.id_configuracao_equipamento ");
		sbSQL.append("    AND cea.id_pista = cep.id_pista ");
		sbSQL.append("  INNER JOIN configuracao_equipamento_captura_veiculo cecv WITH (NOLOCK) ");
		sbSQL.append("    ON cecv.id_configuracao_equipamento = ce.id_configuracao_equipamento ");
		sbSQL.append("    AND cecv.id_pista = cep.id_pista ");
		sbSQL.append("  LEFT JOIN configuracao_equipamento_regra_infracao ceri WITH (NOLOCK) ");
		sbSQL.append("    ON ce.id_configuracao_equipamento = ceri.id_configuracao_equipamento ");
		sbSQL.append("    AND (ceri.id_pista IS NULL OR ceri.id_pista = cep.cod_pista) ");
		sbSQL.append("WHERE ");
		sbSQL.append("  ceri.tipo = 'VL' ");
		sbSQL.append("  AND l.sequencia_local =  ");
		sbSQL.append("    (SELECT MAX(l1.sequencia_local) FROM local l1 WHERE l1.id_local = l.id_local) ");

		Map<Integer,Object> params;
		
		// Processa os critérios
		if (criterios != null)
			params = processarCriterios(criterios, sbSQL);
		else
			params = new HashMap<Integer,Object>();
		
		// Adiciona Ordenação
		sbSQL.append(" ORDER BY ");
		sbSQL.append("  l.id_local, cep.id_pista ");
		
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			inserirParametros(ps, params);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				ListaConfiguracoesEquipamentoGwtBean bean = new ListaConfiguracoesEquipamentoGwtBean();
				bean.setCodigoLocal(rs.getInt("CodLocal"));
				bean.setNumeroSerieEquipamento(rs.getInt("NumSerie"));
				bean.setNumeroPista(rs.getInt("NumPista"));
				bean.setDescricaoLocal(rs.getString("DescLocal"));
				bean.setCodigoPistaAlternativo(rs.getInt("CodPistaAlt"));
				bean.setCodigoPista(rs.getInt("CodPista"));
				bean.setCodigoPistaAlternativo2(rs.getInt("CodPistaAlt2"));
				bean.setDescricaoPista(rs.getString("DescPista"));
				bean.setDataAfericao(new Date(rs.getDate("DataAfericao").getTime()));
				bean.setDistanciaLacos(rs.getFloat("DistLaco"));
				bean.setVelocidadeLimite(rs.getInt("VelLimite"));
				
				listaRetorno.add(bean);
			}
		} catch (SQLException e) {
			throw new Exception("ERRO de SQL");
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (!(conn == null || conn.isClosed()))
				conn.close();
			
		}
		return listaRetorno;
	}

	@Override
	public List<LocalGwtBean> listarLocais() throws Exception {

		verificarSessaoLogada();
		List<LocalGwtBean> listaBeans = new ArrayList<LocalGwtBean>();

		try {
			for (Local local : Local.listarLocaisVigentes()) {
				listaBeans.add(convertToLocalBean(local));
			}
		} catch (Exception ex) {
			throw new Exception("Erro no serviço de manutenção\n ao listar os locais.");
		}

		return listaBeans;
	}
	
	private LocalGwtBean convertToLocalBean(Local local) {
		LocalGwtBean bean = new LocalGwtBean(local.getIdLocal(), local.getSequenciaLocal(),
				local.getIdConfiguracaoEquipamento(), local.getNome());
		return bean;
	}

	
	
}
