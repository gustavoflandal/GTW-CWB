/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Classe de negócio para busca de veículos.

  Historico:

    $Log: Veiculo.java,v $
    Revision 1.1  2009/03/24 21:38:24  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de veículos.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1 $ $Date: 2009/03/24 21:38:24 $ $Author: fos $
 */
public class Veiculo {
	
	
	/**
	 * 999052 em binário = 11110011111010001100
	 * 					   |||||||||||||||||+--- Velocidade
	 * 					   ||||||||||||||||+---- Avanço
	 * 					   ||||||||||||+-------- Contramão
	 * 					   ||||||||||+---------- Rodízio
	 * 					   |||||||||+----------- Faixa exclusiva
	 * 					   ||||||||+------------ Fora de faixa
	 * 					   |||||||+------------- Parada sobre a faixa	
	 * 					   ||||||+-------------- Local/Horário proibido - Carga
	 * 					   |||+----------------- Conversão proibida a direita
	 * 					   ||+------------------ Conversão proibida a esquerda
	 * 					   |+------------------- Retorno proibido
	 * 					   +-------------------- Local/Horário proibido
	 */
	public static final Integer MASCARA_INFRATOR = 999052;
	
	private Integer idVeiculo;
	private String placa;
	private Date data;
	private String idClasse;
	private Integer idImagemObj;
	private Integer comVideo;
	private Integer comPesagem;
	private String porteVeiculo;

	public Veiculo(Integer idVeiculo, String placa, Date data,
			String idClasse, Integer idImagemObj, Integer comVideo, Integer comPesagem) {
		super();
		this.idVeiculo = idVeiculo;
		this.placa = placa;
		this.data = data;
		this.idClasse = idClasse;
		this.idImagemObj = idImagemObj;
		this.comVideo = comVideo;
		this.comPesagem = comPesagem;
	}

	public Veiculo(Integer idVeiculo, String placa, Date data,
			String idClasse, Integer idImagemObj, Integer comVideo, Integer comPesagem, String porteVeiculo) {
		super();
		this.idVeiculo = idVeiculo;
		this.placa = placa;
		this.data = data;
		this.idClasse = idClasse;
		this.idImagemObj = idImagemObj;
		this.comVideo = comVideo;
		this.comPesagem = comPesagem;
		this.porteVeiculo = porteVeiculo;
	}

	/**
	 * Busca uma veículo no BD.
	 * @param iVeiculo Identificador do veículo
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Veiculo buscaVeiculoPorId(Long lVeiculo) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read..... 	
		sbSQL.append(" SELECT v.id_veiculo, ");
		sbSQL.append("	v.placa, ");
		sbSQL.append("	v.data, ");
		sbSQL.append("	v.id_classe, ");
		sbSQL.append("	vi.id_imagem, ");
		sbSQL.append("	CASE WHEN EXISTS(SELECT 1 FROM veiculo_video vv (NOLOCK) WHERE vv.id_veiculo = v.id_veiculo) THEN 1 ELSE 0 END AS com_video, ");
		sbSQL.append("	CASE WHEN vp.id_veiculo_unic IS NOT NULL THEN 1 ELSE 0 END AS com_pesagem, ");
		sbSQL.append("	p.porte_veiculo ");
		sbSQL.append(" FROM ");
		sbSQL.append("	veiculo v WITH (NOLOCK) ");
		sbSQL.append("	JOIN veiculo_pesquisa p (NOLOCK) ON p.id_veiculo = v.id_veiculo ");
		sbSQL.append("	JOIN veiculo_imagem vi WITH (NOLOCK) ON vi.id_veiculo = v.id_veiculo ");
		sbSQL.append("	JOIN imagem_info im WITH (NOLOCK) ON im.id_imagem = vi.id_imagem AND im.id_tipo_imagem = 1 ");

//		sbSQL.append("	LEFT JOIN veiculo_pesagem vp (NOLOCK) ON vp.id_veiculo = v.id_veiculo AND (vp.todas_classificacao_qfv IS NOT NULL AND LTRIM(RTRIM(vp.todas_classificacao_qfv)) <> 'N.A.') ");
		sbSQL.append("	LEFT JOIN veiculo_pesagem vp (NOLOCK) ON vp.id_veiculo_unic = v.id_veiculo_unic AND vp.pbt > 0 AND vp.pesagem_valida = 1 ");
		
		sbSQL.append(" WHERE ");
		sbSQL.append("	v.id_veiculo = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, lVeiculo);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Veiculo(
						rs.getInt("id_veiculo"), 
						rs.getString("placa"), 
						rs.getTimestamp("data"),
						rs.getString("id_classe"),
						rs.getInt("id_imagem"),
						rs.getInt("com_video"),
						rs.getInt("com_pesagem"),
						rs.getString("porte_veiculo")
				);
			}
			else {
				return null;
			}
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
	}

	/**
	 * Busca veículos no BD
	 * @param mFiltros Filtros para a busca.
	 * @return Lista de objetos Veiculo
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Veiculo> buscaVeiculoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<Veiculo> lRet = new ArrayList<Veiculo>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("SELECT v.id_veiculo,");
		sbSQL.append("		v.placa,");
		sbSQL.append("		data,");
		sbSQL.append("		v.id_classe,");
		sbSQL.append("		im.id_imagem");
		sbSQL.append("      CASE WHEN vv.id_veiculo IS NULL THEN 0 ELSE 1 END AS com_video, ");
		sbSQL.append("		CASE WHEN vp.id_veiculo_pesagem IS NOT NULL THEN 1 ELSE 0 END AS com_pesagem ");
		sbSQL.append("	FROM");
		sbSQL.append("		veiculo v WITH (NOLOCK)");
		sbSQL.append("		LEFT JOIN veiculo_imagem vi WITH (NOLOCK) ON vi.id_veiculo = v.id_veiculo");
		sbSQL.append("		LEFT JOIN imagem_info im WITH (NOLOCK) ON im.id_imagem = vi.id_imagem AND im.id_tipo_imagem = 1");
		sbSQL.append("      LEFT JOIN veiculo_video vv (NOLOCK) ON vv.id_veiculo = v.id_veiculo");
		sbSQL.append("		LEFT JOIN veiculo_pesagem vp (NOLOCK) ON vp.id_veiculo = v.id_veiculo ");
		sbSQL.append("	WHERE");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY 1");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Veiculo(
							rs.getInt("id_veiculo"), 
							rs.getString("placa"), 
							rs.getTimestamp("data"),
							rs.getString("id_classe"),
							rs.getInt("id_imagem"),
							rs.getInt("com_video"),
							rs.getInt("com_pesagem")
						)

				);
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}			
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return lRet;
	}
	
	// TODO: Não disponibilizar o ResultSet diretamente, mas sim encapsular
	// dentro de um iterator.
	public static ResultSet getTrafego(Connection conn, PreparedStatement ps, Integer idExportacaoTrafego) throws SQLException{
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("select \n");
		sbSQL.append("	v.data, \n");
		sbSQL.append("	cep.cod_pista, \n");
		sbSQL.append("	cep.cod_pista_alternativo, \n");
		sbSQL.append("	v.placa as placa, \n");
		sbSQL.append("	case  \n");
		sbSQL.append("		when v.id_classe = 'M' then 0 \n");
		sbSQL.append("		when v.id_classe = 'P' then 1 \n");
		sbSQL.append("		when v.id_classe = 'O' then 2 \n");
		sbSQL.append("		when v.id_classe = 'C' then 3 \n");
		sbSQL.append("	end as classificacao, \n");
		sbSQL.append("	cast(v.comprimento * 10 as int) as comprimento, \n");
		sbSQL.append("	CAST((v.velocidade / 3.6) AS NUMERIC(5,2)) as velocidade_ms, \n");
		sbSQL.append("	v.ocupacao \n");
		sbSQL.append("from  \n");
		sbSQL.append("	[veiculo_estatistica] v WITH (NOLOCK) \n");
		sbSQL.append("	inner join [local] l WITH (NOLOCK) on \n");
		sbSQL.append("          (v.id_local = l.id_local and v.sequencia_local = l.sequencia_local) \n");
		sbSQL.append("	inner join [configuracao_equipamento] ce WITH (NOLOCK) on \n");
		sbSQL.append("          (ce.id_configuracao_equipamento = l.id_configuracao_equipamento) \n");
		sbSQL.append("	inner join [configuracao_equipamento_pista] cep WITH (NOLOCK) on \n");
		sbSQL.append("          (cep.id_configuracao_equipamento = ce.id_configuracao_equipamento and cep.id_pista = v.pista) \n");
		sbSQL.append("where  \n");
		sbSQL.append("	id_arquivo IN (SELECT id_arquivo FROM exportacao_trafego_arquivo WHERE id_exportacao_trafego = ?)\n");
		sbSQL.append("	ORDER BY v.data");

		
		ResultSet rs = null;

		ps = conn.prepareStatement(sbSQL.toString());
		ps.setInt(1, idExportacaoTrafego);
		rs = ps.executeQuery();
		
		return rs;
	}

	public static boolean registrarVeiculoVisualizado( long id_veiculo , int id_usuario  )
	throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("  INSERT INTO veiculo_visualizados");
		sbSQL.append("  ([id_veiculo]");
		sbSQL.append("  ,[id_usuario]");
		sbSQL.append("  ,[data_hora])");
		sbSQL.append("  VALUES");
		sbSQL.append("  (?");
		sbSQL.append("  ,?");
		sbSQL.append("  ,GETDATE())");		

		                                  
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setLong(1, id_veiculo );
			ps.setInt(2, id_usuario);
			
			return ps.executeUpdate() > 0;
		}	
		finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		
	}
	
	
	public Integer getIdVeiculo() {
		return idVeiculo;
	}

	public String getPlaca() {
		return placa;
	}

	public Date getData() {
		return data;
	}

	public String getIdClasse() {
		return idClasse;
	}

	public Integer getIdImagemObj() {
		return idImagemObj;
	}

	public Integer getComVideo() {
		return comVideo;
	}
	
	public Integer getComPesagem() {
		return comPesagem;
	}

	public String getPorteVeiculo() {
		return porteVeiculo;
	}

}
