/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/07/2008

  Descricao: Classe de negócio para busca de lista de informações da uma infração.

  Historico:

    $Log: InfracaoCompletaLista.java,v $
    Revision 1.9  2009/03/19 23:04:16  fos
    Agora tras a série também.

    Revision 1.8  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.7  2009/03/09 18:48:54  fos
    Agora a remessa controla o tipo para reiniciar o contador de autos.

    Revision 1.6  2009/03/03 21:35:00  fos
    Colocado instrução para não bloquear a tabela.

    Revision 1.5  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/29 20:52:00  fos
    Renomeada métodos e atributos para enquadramento.

    Revision 1.2  2008/08/18 14:49:57  fos
    Agora possui filtros de consistente/inconsistente.

    Revision 1.1  2008/07/31 21:09:55  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

import org.apache.log4j.Logger;

/**
 * Classe de negócio para busca de lista de informações da uma infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/03/19 23:04:16 $ $Author: fos $
 */
public class InfracaoCompletaLista implements Serializable{
	
	private static final long serialVersionUID = -6426378637170930539L;
	private static SimpleDateFormat formatoDiaSemana = new SimpleDateFormat("EE", new Locale("pt", "BR"));
	
	private static Logger logger = Logger.getLogger(InfracaoCompletaLista.class);

	
	private Integer id;
	private Integer idImagemLocal;
	private Integer auto;
	private String serie;
	private String tipoRemessa;
	private Integer idEnquadramento;
	private String placa;
	private String marca;
	private String nomeLocal;
	private Integer pista;
	private Date dataVeiculo;
	private Date dataEntrada;

	@SuppressWarnings("unused")
	private String diaSemana;
	
	private Integer velocidade;
	
	private String classe;
	private Integer comPesagem;
	
	/**
	 * Constrói o objeto InfracaoCompleto com os seus respectivos atributos.
	 * @param id Identificador da infração
	 * @param idImagemLocal Idenficador da imagem no local.
	 * @param auto Idenficador do Auto.
	 * @param serie Idenficador da série.
	 * @param tipoRemesa Tipo da Remessa.
	 * @param idEnquadramento Identificador do enquadramento.
	 * @param placa Placa digitada da infração
	 * @param marca Marca do veículo segundo o cadastro
	 * @param nomeLocal Nome do local
	 * @param dataVeiculo Data/Hora de captura do veículo
	 */

	private InfracaoCompletaLista(Integer id, Integer idImagemLocal, Integer auto, 
			String serie, String tipoRemessa, Integer idEnquadramento, String placa, 
			String marca, String nomeLocal, Integer pista, Date dataVeiculo, 
			Date dataEntrada, Integer velocidade, String classe, Integer comPesagem) {
		super();
		this.id = id;
		this.idImagemLocal = idImagemLocal;
		this.auto = auto;
		this.serie = serie;
		this.tipoRemessa = tipoRemessa;
		this.idEnquadramento = idEnquadramento;
		this.placa = placa;
		this.marca = marca;
		this.nomeLocal = nomeLocal;
		this.pista = pista;
		this.dataVeiculo = dataVeiculo;
		this.dataEntrada = dataEntrada;
		this.velocidade = velocidade;
		this.classe = classe;
		this.comPesagem = comPesagem;
	}

	/**
	 * Busca infrações no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: 
	 * id_infracao_ini, id_infracao_fim, data_infracao_ini, data_infracao_fim, id_imagem_ini, id_imagem_fim, consistente, inconsistente.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos InfracaoCompletaLista
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException 
	 */
	public static List<InfracaoCompletaLista> buscaInfracaoCompletaPor(Map<String,Object> mFiltros, Integer iOrdem, boolean decrescente) throws ConexaoException, SQLException, ModelException {

		List<InfracaoCompletaLista> lRet = new ArrayList<InfracaoCompletaLista>();

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" Set Query_Governor_cost_limit 300; ");
		sbSQL.append("SELECT ");
		sbSQL.append("	ic.id_infracao, ");
		sbSQL.append("	ic.id_imagem_local, ");
		sbSQL.append("	ic.[auto], ");
		sbSQL.append("	ic.serie, ");
		sbSQL.append("	ic.tipo_remessa, ");
		sbSQL.append("	ic.placa, ");
		sbSQL.append("	ic.id_enquadramento, ");
		sbSQL.append("	COALESCE(mc.descricao, mcc.descricao) AS descricaoMarca, ");
		sbSQL.append("	ic.nome_pista, ");
		sbSQL.append("  ic.pista, ");
		sbSQL.append("	i.razao_tecnica, ");
		sbSQL.append("	ic.data_veiculo,  ");
		sbSQL.append("	ic.data_entrada,  ");
		sbSQL.append("	ic.velocidade_veiculo,  ");
		sbSQL.append("  COALESCE(ic.id_classe, 'N/A') AS classe, ");
		sbSQL.append("  ic.com_pesagem ");
		sbSQL.append("FROM ");
		sbSQL.append("	infracao_completa ic WITH (NOLOCK) ");
		sbSQL.append("	LEFT JOIN cad_veiculo cv WITH (NOLOCK) ");
		sbSQL.append("		ON cv.placa = ic.placa ");
		sbSQL.append("	LEFT JOIN cad_marca mc WITH (NOLOCK) ");
		sbSQL.append("		ON mc.id_marca = cv.id_marca ");
		sbSQL.append("	LEFT JOIN cad_marca_cet mcc WITH (NOLOCK) ");
		sbSQL.append("		ON mcc.id_marca_cet = cv.id_marca_cet ");
		sbSQL.append("	LEFT JOIN inconsistencia i WITH (NOLOCK) ");
		sbSQL.append("		ON ic.id_inconsistencia = i.id_inconsistencia ");
		sbSQL.append("	LEFT JOIN infracao_processo ifp WITH (NOLOCK) ");
		sbSQL.append("		ON ifp.id_infracao = ic.id_infracao ");
		sbSQL.append("	LEFT JOIN sis_usuario usu WITH (NOLOCK) ");
		sbSQL.append("		ON usu.id_usuario = ifp.id_usuario ");
		sbSQL.append("  LEFT JOIN enquadramento_regra_infracao EnquadInfra WITH(NOLOCK) ");
		sbSQL.append(" 		on EnquadInfra.id_enquadramento = ic.id_enquadramento ");
		sbSQL.append("  Left Join infracao_remessa RemInfr ");
		sbSQL.append("		ON RemInfr.id_infracao = ic.id_infracao ");
		sbSQL.append("  Left join Remessa Rem ");
		sbSQL.append(" 		ON rem.id_remessa = RemInfr.id_remessa ");
		sbSQL.append("  LEFT JOIN local l ");
		sbSQL.append(" 		ON  l.id_local = ic.id_local ");
		sbSQL.append(" 			AND l.sequencia_local = ic.sequencia_local ");
		sbSQL.append("  LEFT JOIN configuracao_equipamento ce ");
		sbSQL.append(" 		ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento ");
		sbSQL.append("  LEFT JOIN processo_excluir pex ");
		sbSQL.append(" 		ON  pex.id_processo = ic.id_processo ");
		
		//Regra implementada pela atividade O.S 101 - Não trazer imagens com mais de 45 dias 
		//											  e/ou tenham sido validadas pela CET
		//Luiz Amaral 22/07/2015
		sbSQL.append("WHERE 1 = 1  ");
//		sbSQL.append("     	cast(ic.data_veiculo as date)> dateadd(day, -80, cast(getdate() as date)) ");
//		sbSQL.append(" 		AND cast(ic.data_veiculo as date) <= cast(getdate() as date) ");
//		sbSQL.append(" 		AND ic.id_processo <> 99 ");
		sbSQL.append(" 		AND pex.id_processo IS NULL ");
			
		int paramIndex = 1;
		Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();
		
		// ***************************************************************
		// 1ª Linha
		// ***************************************************************
		if (mFiltros.containsKey("id_infracao_ini")) {
			sbSQL.append("	AND ic.id_infracao >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_infracao_ini"));
		}
		if (mFiltros.containsKey("id_infracao_fim")) {
			sbSQL.append("	AND ic.id_infracao <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_infracao_fim"));
		}		
		if (mFiltros.containsKey("data_infracao_ini")) {
			sbSQL.append("	AND ic.data_veiculo >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_infracao_ini"));
		}
		if (mFiltros.containsKey("data_infracao_fim")) {
			sbSQL.append("	AND ic.data_veiculo <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_infracao_fim"));
		}
		if (mFiltros.containsKey("id_enquadramento")) {
			sbSQL.append("	AND ic.id_enquadramento = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_enquadramento"));
		}		
		
		// ***************************************************************
		// 2ª Linha
		// ***************************************************************
		if (mFiltros.containsKey("id_imagem_ini")) {
			sbSQL.append("	AND ic.id_imagem_local >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_imagem_ini"));
		}
		if (mFiltros.containsKey("id_imagem_fim")) {
			sbSQL.append("	AND ic.id_imagem_local <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_imagem_fim"));
		}
		if (mFiltros.containsKey("cod_pista_prodam")) {
			sbSQL.append("	AND ic.cod_pista_prodam = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("cod_pista_prodam"));
		}
		if (mFiltros.containsKey("tipo_equipamento")) {
			sbSQL.append("	AND ic.tipo_equipamento = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("tipo_equipamento"));
		}
		if (mFiltros.containsKey("id_local")) {
			sbSQL.append("	AND ic.id_local = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_local"));
		}
		if (mFiltros.containsKey("pista")) {
			List lista_pistas = (List) mFiltros.get("pista");
			sbSQL.append("	AND ic.pista IN (" + StringUtils.join(Funcoes.repeteString("?", lista_pistas.size()), ",") + ") ");
			for(int t=0; t<lista_pistas.size(); t++)
				mapaParametros.put(paramIndex++, lista_pistas.get(t));
		}		
		if (mFiltros.containsKey("id_processo")) {
			sbSQL.append("	AND ic.id_processo = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_processo"));
		}		
		
		if (mFiltros.containsKey("enquadInfracao")) {
			if (! mFiltros.get("enquadInfracao").equals("0")){				
				sbSQL.append("	AND  REPLACE(replace(replace(replace(EnquadInfra.descricao_apait, 'Á','A'), 'Í', 'I'), 'Ç', 'C'), 'Ã', 'A')  = ? ");
				mapaParametros.put(paramIndex++, mFiltros.get("enquadInfracao"));
			}
		}
		
		// ***************************************************************
		// 3ª Linha
		// ***************************************************************
		if (mFiltros.containsKey("auto_ini")) {
			sbSQL.append("	AND ic.[auto] >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("auto_ini"));
		}
		if (mFiltros.containsKey("auto_fim")) {
			sbSQL.append("	AND ic.[auto] <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("auto_fim"));
		}
		
		if (mFiltros.containsKey("movimentoLote")) {
			sbSQL.append("	AND Rem.[codigo_externo] = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("movimentoLote"));
		}
		
		
		if (mFiltros.containsKey("placa")) {
			sbSQL.append("	AND ic.placa = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("placa"));
		}
		if (mFiltros.containsKey("razao_tecnica")) {

			int iRazaoTecnica = (Integer) mFiltros.get("razao_tecnica");

			///////////////////////////////////////////////////////////////////////////////////
			// WORKAROUND: Na verdade, para infrações consistidas (ic.id_inconsistencia = 0),
			// o campo razao_tecnica deveria ser NULO (e não zero) como é hoje.
			// Pode ser trocado, mas é possível que cause algum problema com relatórios.
			// TODO: É necessário razao_tecnica NULO (para consistentes). Raoni, 02/OUT/2009
			///////////////////////////////////////////////////////////////////////////////////
			if (iRazaoTecnica == 0)
				sbSQL.append("	AND i.razao_tecnica = 0 AND ic.id_inconsistencia > 0 "); // PNT
			else if(iRazaoTecnica == 1)
				sbSQL.append("	AND i.razao_tecnica IN (1,2) AND ic.id_inconsistencia > 0 "); // PTL, PTG
			
		}
		if (mFiltros.containsKey("id_classe")) {
			
			Boolean isNumeric = mFiltros.get("id_classe").toString().matches("[0-9]+");
			
			if (isNumeric) {
				sbSQL.append("	AND cv.id_tipo = ? ");
			} else {
				sbSQL.append("	AND ic.id_classe = ? ");
			}
			mapaParametros.put(paramIndex++, mFiltros.get("id_classe"));
		}
		
		// ***************************************************************
		// 4ª Linha
		// ***************************************************************
		if (mFiltros.containsKey("aproveitaveis")) {
			// 0 = FALSO, 1 = VERDADEIRO, 2 = NULO
			Integer iAproveitavel = (Integer) mFiltros.get("aproveitaveis");
			
			if (iAproveitavel == null) {
				sbSQL.append("	AND ic.aproveitavel IS NULL ");
			} else {
				sbSQL.append("	AND ic.aproveitavel = ? ");
				mapaParametros.put(paramIndex++, iAproveitavel);
			}
		}
		if (mFiltros.containsKey("espera")) {
			if (mFiltros.get("espera") != null) {
				if((Boolean)mFiltros.get("espera"))
					sbSQL.append("	AND ic.espera = 1 ");
				else 
					sbSQL.append("	AND ic.espera IS NULL ");
			}
		}		
		
		// Justificativa (inconsistência) e código agente: vide área comum.
		
		// ***************************************************************
		// 5ª Linha (Validáveis)
		// ***************************************************************
		if (mFiltros.containsKey("validaveis")) {
			// 0 = FALSO, 1 = VERDADEIRO, 2 = NULO
			Integer iValidavel = (Integer) mFiltros.get("validaveis");
			
			if (iValidavel == null) {
				sbSQL.append("	AND ic.validavel IS NULL ");
			} else {
				sbSQL.append("	AND ic.validavel = ? ");
				mapaParametros.put(paramIndex++, iValidavel);
			}
		}		
		
		if (mFiltros.containsKey("data_validavel_ini")) {
			sbSQL.append("	AND ifp.data >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_validavel_ini"));
		}
		
		if (mFiltros.containsKey("data_validavel_fim")) {
			sbSQL.append("	AND ifp.data <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_validavel_fim"));
		}
		
		// Justificativa (inconsistência): vide área comum.
		
		// ***************************************************************
		// 6ª Linha (Válidas)
		// ***************************************************************
		if (mFiltros.containsKey("validas")) {
			// 0 = FALSO, 1 = VERDADEIRO, 2 = NULO
			Integer iValidas = (Integer) mFiltros.get("validas");
			
			if (iValidas == null) {
				sbSQL.append("	AND ic.valida IS NULL ");
			} else {
				sbSQL.append("	AND ic.valida = ? ");
				mapaParametros.put(paramIndex++, iValidas);
			}
		}		

		if (mFiltros.containsKey("data_validas_ini")) {
			sbSQL.append("	AND ifp.data >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_validas_ini"));
		}
		
		if (mFiltros.containsKey("data_validas_fim")) {
			sbSQL.append("	AND ifp.data <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("data_validas_fim"));
		}		
		
		// Justificativa (inconsistência): vide área comum.		
		
		// ***************************************************************
		// 7ª Linha
		// ***************************************************************
		
		// Rg do agente (codigo agente): vide área comum.		
		
		if (mFiltros.containsKey("velocidade_minima")) {
			sbSQL.append("	AND ic.velocidade_veiculo >= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("velocidade_minima"));
		}			

		if (mFiltros.containsKey("velocidade_maxima")) {
			sbSQL.append("	AND ic.velocidade_veiculo <= ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("velocidade_maxima"));
		}
		
		if (mFiltros.containsKey("filtrar_resultados")) {
			
			int iFiltrarResultados = (Integer) mFiltros.get("filtrar_resultados");

			if (iFiltrarResultados == 1) {
				
				sbSQL.append("	AND ic.id_processo IN (");
				sbSQL.append("	SELECT p.id_processo FROM");
				sbSQL.append("	processo p WHERE p.id_processo_proximo IS NULL");
				sbSQL.append("	AND p.ativo = 1 AND p.recebe_consistentes_inconsistentes = 1)");
			
			} else if(iFiltrarResultados == 2) {
				
				sbSQL.append("	AND ic.com_pesagem = 1 ");
			
			}
		}		
		
		// ***************************************************************
		// 15ª Linha
		// ***************************************************************
		if (mFiltros.containsKey("id_produto")) {
			sbSQL.append("	AND ic.tipo_equipamento = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("id_produto"));
		}
		
		// *************************************************************	
		// Área comum entre aproveitavel, validável, válida. 
		// *************************************************************
		if (mFiltros.containsKey("inconsistencia")) {
			int idInconsistencia = (Integer) mFiltros.get("inconsistencia");
			if (idInconsistencia > 0) {
				sbSQL.append("	AND ic.id_inconsistencia = ? ");
				mapaParametros.put(paramIndex++, idInconsistencia);
			}
		}
		if (mFiltros.containsKey("cod_agente")) {
			sbSQL.append("	AND usu.cod_agente = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("cod_agente"));
		}		
		if (mFiltros.containsKey("auto")) {
			sbSQL.append("	AND ic.auto = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("auto"));
		}		
		if (mFiltros.containsKey("autos")) {
			sbSQL.append("	AND ic.auto IN ("+mFiltros.get("autos")+") ");
		}		
		if (mFiltros.containsKey("serie")) {
			sbSQL.append("	AND ic.serie = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("serie"));
		}		
		if (mFiltros.containsKey("tipo_remessa")) {
			sbSQL.append("	AND ic.tipo_remessa = ? ");
			mapaParametros.put(paramIndex++, mFiltros.get("tipo_remessa"));
		}		
		
		sbSQL.append(" GROUP BY ");
		sbSQL.append("	ic.id_infracao, "); 
		sbSQL.append("	ic.id_imagem_local, ");
		sbSQL.append("	ic.[auto], ");
		sbSQL.append("	ic.serie, ");
		sbSQL.append("	ic.tipo_remessa, "); 
		sbSQL.append("	ic.placa, ");
		sbSQL.append("	ic.id_enquadramento, "); 
		sbSQL.append("	COALESCE(mc.descricao, mcc.descricao), "); 
		sbSQL.append("	ic.nome_pista, ");
		sbSQL.append("	ic.pista, ");
		sbSQL.append("	i.razao_tecnica, "); 
		sbSQL.append("	ic.data_veiculo, ");
		sbSQL.append("	ic.data_entrada, ");
		sbSQL.append("	ic.velocidade_veiculo, ");  
		sbSQL.append("	COALESCE(ic.id_classe, 'N/A'), ");
		sbSQL.append("  ic.com_pesagem ");
		
		sbSQL.append(" ORDER BY " + iOrdem + (decrescente ? " DESC" : "") + ";");
		sbSQL.append(" Set Query_Governor_cost_limit 0; ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Iterando e ajustando os valores dos parametros para os wheres (as interrogações):
			Object oVal = null;
			for (Entry<Integer, Object> paramQuery : mapaParametros.entrySet()) {
				oVal = paramQuery.getValue();
				
				if (oVal == null)
					continue;
				else if (oVal instanceof String)
					ps.setString(paramQuery.getKey(), (String) oVal);
				else if (oVal instanceof Integer)
					ps.setInt(paramQuery.getKey(), ((Integer)oVal).intValue());
				else if (oVal instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean)oVal).booleanValue());				
				else if (oVal instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp)oVal);
				else
					throw new SQLException("Tipo " + oVal + " inválido na preparação da query.");
			}

			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new InfracaoCompletaLista(
						rs.getInt("id_infracao"),
						rs.getInt("id_imagem_local"),
						rs.getInt("auto") > 0 ? rs.getInt("auto") : null,
						rs.getString("serie"),
						rs.getString("tipo_remessa"),
						rs.getInt("id_enquadramento"),
						rs.getString("placa"),
						rs.getString("descricaoMarca"),
						rs.getString("nome_pista") != null ? rs.getString("nome_pista").trim() : null,
						rs.getInt("pista"),
						rs.getTimestamp("data_veiculo"),
						rs.getTimestamp("data_entrada"),
						rs.getInt("velocidade_veiculo"),
						rs.getString("classe"),
						rs.getInt("com_pesagem")
				)
				);
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
	}

	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Retorna o valor de idImagemLocal atual.
	 */
	public Integer getIdImagemLocal() {
		return idImagemLocal;
	}

	/**
	 * @return Retorna o valor de placa atual.
	 */
	public String getPlaca() {
		return placa;
	}

	/**
	 * @return Retorna o valor de marca atual.
	 */
	public String getMarca() {
		return marca;
	}

	/**
	 * @return Retorna o valor de nomeLocal atual.
	 */
	public String getNomeLocal() {
		return nomeLocal;
	}

	public Integer getPista() {
		return pista;
	}

	public void setPista(Integer pista) {
		this.pista = pista;
	}

	/**
	 * @return Retorna o valor de dataVeiculo atual.
	 */
	public Date getDataVeiculo() {
		return dataVeiculo;
	}
	
	/**
	 * @return Retorna o valor de dataEntrada atual.
	 */
	public Date getDataEntrada() {
		return dataEntrada;
	}

	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	public Integer getAuto() {
		return auto;
	}
	
	public String getSerie() {
		return serie;
	}

	public String getTipoRemessa() {
		return tipoRemessa;
	}

	public String getDiaSemana() {
		return formatoDiaSemana.format(dataVeiculo);
	}

	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Integer getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(Integer velocidade) {
		this.velocidade = velocidade;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}
	
	public Integer getComPesagem() {
		return comPesagem;
	}
	

	/**
	 * Autor: Thiago Surgik 08/06/2016
	 * Verifica se usuário pertence ao grupo de acesso da Velsis.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static boolean pertenceGrupoAcessoVelsis(Integer idUsuario) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;
		String grupoCAV = "Visualizadores Velsis";

		sbSQL.append(" SELECT 1 AS existe ");
		sbSQL.append(" FROM   sis_usuario_grupo sug ");
		sbSQL.append(" WHERE  sug.id_usuario = ? ");
		sbSQL.append(" 		  AND sug.id_grupo = ( ");
		sbSQL.append(" 		  			SELECT sg.id_grupo ");
		sbSQL.append(" 		  			FROM   sis_grupo sg ");
		sbSQL.append(" 		  			WHERE  sg.descricao = '" + grupoCAV + "' ");
		sbSQL.append(" 		  ) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);

			rs = ps.executeQuery();
			
			if (rs.next()) {
				if(rs.getInt("existe") > 0){
					retorno = true;
				}				
			}
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao verficar grupo de acesso da Velsis.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return retorno;
	}

}
