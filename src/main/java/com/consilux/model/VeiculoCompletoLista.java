/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 23/03/2009

  Descricao: Classe de negócio para busca de lista de informações da um veículo.

  Historico:

    $Log: VeiculoCompletoLista.java,v $
    Revision 1.2  2009/05/08 18:56:36  fos
    Acertado a forma de extrati o bit ligado no flag do veículo.

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
 * Classe de negócio para busca de lista de informações da um veículo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/05/08 18:56:36 $ $Author: fos $
 */
public class VeiculoCompletoLista {
	private Integer id;
	private Integer idVeiculoLocal;
	private Integer pista;
	private Integer idLocal;
	private Date data;
	private String placa;
	private Double velocidade;
	private Double comprimento;
	private Integer flag;
	private String idClasse;
	private String nomeClasse;
	private Integer ocupacao;
	private String nomeLocal;
	private Integer comPesagem;
	private Double pbt;
	private String classificacao;
	private Integer numeroEixos;
	private boolean rodagemDupla;
	private Integer categoria;

	private VeiculoCompletoLista(Integer id, Integer idVeiculoLocal,
			Integer pista, Integer idLocal, Date data, String placa,
			Double velocidade, Double comprimento, Integer flag,
			String idClasse, String nomeClasse, Integer ocupacao, String nomeLocal,
			Integer comPesagem, Double pbt, String classificacao, Integer numeroEixos, boolean rodagemDupla, Integer categoria) {
		super();
		this.id = id;
		this.idVeiculoLocal = idVeiculoLocal;
		this.pista = pista;
		this.idLocal = idLocal;
		this.data = data;
		this.placa = placa;
		this.velocidade = velocidade;
		this.comprimento = comprimento;
		this.flag = flag;
		this.idClasse = idClasse;
		this.nomeClasse = nomeClasse;
		this.ocupacao = ocupacao;
		this.nomeLocal = nomeLocal != null ? nomeLocal.trim() : "";
		this.comPesagem = comPesagem;
		this.pbt = pbt;
		this.classificacao = classificacao;
		this.numeroEixos = numeroEixos;
		this.rodagemDupla = rodagemDupla;
		this.categoria = categoria;
	}

	/**
	 * Busca veículos no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: 
	 * id_veiculo_ini, id_veiculo_fim, data_veiculo_ini, data_veiculo_fim, id_veiculo_local_ini, id_veiculo_local_fim, img_teste, img_irregular, img_infrator.
	 * @param idxColunaOrdem Número da coluna de ordem.
	 * @return Lista de objetos VeiculoCompletoLista
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<VeiculoCompletoLista> buscaVeiculoCompletoPor( Map<String,Object> mFiltros ) throws ConexaoException, SQLException {
		return buscaVeiculoCompletoPor(mFiltros, false);	
	}
	public static List<VeiculoCompletoLista> buscaVeiculoCompletoPor( Map<String,Object> mFiltros, boolean infrator ) throws ConexaoException, SQLException {

		List<VeiculoCompletoLista> lRet = new ArrayList<VeiculoCompletoLista>();
		StringBuilder sbSQL = new StringBuilder();
		
		
//		sbSQL.append(" Set Query_Governor_cost_limit 90; ");
		sbSQL.append(" SELECT TOP 1000 ");
		sbSQL.append("	v.id_veiculo,");
		sbSQL.append("	v.id_veiculo_local,");
		sbSQL.append("	v.pista,");
		sbSQL.append("	v.id_local,");
		sbSQL.append("	v.data,");
		sbSQL.append("	v.placa,");
		sbSQL.append("	v.velocidade,");
		sbSQL.append("	v.comprimento,");
		sbSQL.append("	v.flag,");
		sbSQL.append("	v.id_classe,");
		sbSQL.append("	c.descricao AS descricaoClasse, ");
		sbSQL.append("	v.ocupacao, ");
		sbSQL.append("	COALESCE( cep.nome_pista, lc.nome, sub1.descricao_local ) AS nome_local, ");
		sbSQL.append("	CASE WHEN vp.id_veiculo_unic IS NOT NULL THEN 1 ELSE 0 END AS com_pesagem, ");
//		sbSQL.append("	CASE WHEN numero_eixos IS NOT NULL AND numero_eixos > 0 THEN 1 ELSE 0 END AS com_pesagem, ");
		sbSQL.append("	vp.pbt, vpe.qtde_eixos AS numero_eixos, v.classificacao, v.rodagem_dupla, v.categoria ");
		sbSQL.append(" FROM");
		sbSQL.append("	veiculo_pesquisa v WITH (NOLOCK)");
		sbSQL.append("	LEFT JOIN classe_veiculo c WITH (NOLOCK) ON c.id_classe = v.id_classe ");
		sbSQL.append("	LEFT JOIN local lc WITH (NOLOCK) ON lc.id_local = v.id_local AND lc.sequencia_local = v.sequencia_local ");
		sbSQL.append("	LEFT JOIN configuracao_equipamento_pista cep WITH (NOLOCK) ON cep.id_configuracao_equipamento = lc.id_configuracao_equipamento AND cep.id_pista = v.pista ");
		sbSQL.append("  LEFT JOIN ( SELECT id_local, MAX(descricao_local) descricao_local FROM movimento_importacao (NOLOCK) GROUP BY id_local ) AS sub1 ON v.id_local = sub1.id_local ");
		sbSQL.append("  LEFT JOIN v_veiculo_pesagem vp (NOLOCK) ON vp.id_veiculo_unic = v.id_veiculo_unic ");
		sbSQL.append("  LEFT JOIN v_veiculo_pesagem_eixo vpe (NOLOCK) ON vpe.id_veiculo_unic = v.id_veiculo_unic ");
		
//		if (infrator)
//		{
//			sbSQL.append(" 		  LEFT JOIN veiculo_imagem vi (NOLOCK) ");
//			sbSQL.append(" 	   			ON  vi.id_veiculo = v.id_veiculo ");
//			sbSQL.append(" 		  LEFT JOIN imagem img (NOLOCK) ");
//			sbSQL.append(" 	   			ON  img.id_imagem = vi.id_imagem ");
//		}
		
		sbSQL.append(" WHERE ");
		
		//Regra implementada pela atividade O.S 101 - Não trazer veiculos com mais de 45 dias 
		//Luiz Amaral 22/07/2015
		//sbSQL.append(" cast(v.data as date) > dateadd(day, -45, cast(getdate() as date)) AND ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_classe", "v.id_classe = ?");
		mRegras.put("id_local", "v.id_local = ?");
		mRegras.put("id_veiculo_ini", "v.id_veiculo >= ?");
		mRegras.put("id_veiculo_fim", "v.id_veiculo <= ?");
		mRegras.put("data_veiculo_ini", "v.data >= ?");
		mRegras.put("data_veiculo_fim", "v.data <= ?");
		mRegras.put("id_veiculo_local_ini", "v.id_veiculo_local >= ?");
		mRegras.put("id_veiculo_local_fim", "v.id_veiculo_local <= ?");
		mRegras.put("img_teste", "(v.flag & 16384) > 0");
		mRegras.put("img_irregular", "(v.flag & 2) > 0");
		
		if (infrator) {
			mRegras.put("img_infrator", "EXISTS (SELECT 1 FROM veiculo_imagem vi WHERE vi.id_veiculo = v.id_veiculo)");
//			mRegras.put("img_infrator", "(vi.id_imagem IS NOT NULL AND img.indice_imagem = 0)");
		}
		else
		{
			mRegras.put("img_infrator", "(v.flag & "+Veiculo.MASCARA_INFRATOR+") > 0");
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY data DESC; ");
//			sbSQL.append(" Set Query_Governor_cost_limit 0; ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new VeiculoCompletoLista(
						rs.getInt("id_veiculo"),
						rs.getInt("id_veiculo_local"),
						rs.getInt("pista"),
						rs.getInt("id_local"),
						rs.getTimestamp("data"),
						rs.getString("placa"),
						rs.getDouble("velocidade"),
						rs.getDouble("comprimento"),
						rs.getInt("flag"),
						rs.getString("id_classe"),
						rs.getString("descricaoClasse"),
						rs.getInt("ocupacao"),
						rs.getString("nome_local"),
						rs.getInt("com_pesagem"),
						rs.getDouble("pbt"),
						rs.getString("classificacao"),
						rs.getInt("numero_eixos"),
						rs.getBoolean("rodagem_dupla"),
						rs.getInt("categoria")
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

	public Integer getId() {
		return id;
	}

	public Integer getIdVeiculoLocal() {
		return idVeiculoLocal;
	}

	public Date getData() {
		return data;
	}

	public String getPlaca() {
		return placa;
	}

	public Double getVelocidade() {
		return velocidade;
	}

	public Double getComprimento() {
		return comprimento;
	}

	public Integer getFlag() {
		return flag;
	}

	public String getStrFlag() {
		//TODO: Descobrir letras que equivalem aos bits ligados.
		return flag.toString();
	}

	public String getIdClasse() {
		return idClasse;
	}

	public String getNomeClasse() {
		return nomeClasse;
	}

	/**
	 * Retorna o valor do campo 'ocupacao' atual.
	 * @return the ocupacao
	 */
	public Integer getOcupacao() {
		return this.ocupacao;
	}

	/**
	 * Retorna o valor do campo 'pista' atual.
	 * @return the pista
	 */
	public Integer getPista() {
		return this.pista;
	}

	/**
	 * Retorna o valor do campo 'idLocal' atual.
	 * @return the idLocal
	 */
	public Integer getIdLocal() {
		return this.idLocal;
	}

	public String getNomeLocal() {
		return nomeLocal;
	}
	
	/**
	 * Retorna o valor do campo 'comPesagem' atual.
	 * @return the idLocal
	 */
	public Integer getComPesagem() {
		return this.comPesagem;
	}
	
	public Double getPbt() {
		return pbt;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public Integer getNumeroEixos() {
		return numeroEixos;
	}

	public boolean isRodagemDupla() {
		return rodagemDupla;
	}

	public Integer getCategoria() {
		return categoria;
	}
}
