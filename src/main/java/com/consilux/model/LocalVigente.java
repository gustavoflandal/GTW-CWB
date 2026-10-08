/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 22/09/2008

  Descrição: Classe de negócio que busca informações básicas de um local vigente.
  Histórico:

    $Log: LocalVigente.java,v $
    Revision 1.7  2009/05/21 14:20:09  fos
    Colocado WITH (NOLOCK) na busca dos locais vigentes.

    Revision 1.6  2009/04/17 17:48:51  fernando
    - Adicionado atributo emOperacao.

    Revision 1.5  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.4  2009/01/16 14:00:27  fos
    Agora o local vigente também retorna a sequencia atual.

    Revision 1.3  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.1  2008/10/13 17:13:38  fos
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
import com.consilux.model.beans.LocalVigenteBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio que busca informações básicas de um local vigente.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/05/21 14:20:09 $ $Author: fos $
 */
public class LocalVigente {
	private Integer idLocal;
	private Integer sequenciaLocal;
	private Integer idLocalEquipamento;
	private Integer idConfiguracaEquipamento;
	private Integer serieEquipamento;
	private String nome;
	private Boolean emOperacao;
	private Double latitude;
	private Double longitude;
	private Integer pista;
	private String codigoEquipamentoDER;
	private String codigosEquipamentosDER;
	private Date dataInicioOperacao;
	
	private Integer idPista;
	private Integer faixa;
	private Long codigoGIT;
	private String nomeAbreviado;
	private Integer gstPistaSentido;
	private Integer gstPistaSentidoFaixa;
	private String equipamento;
	private String referencia;
	private String sentido;
	private String pistaDescricao;
	private String nomePistaSentido;
	private Boolean faixaBRS;
	
	private String velocidadeRegulamentada;
	private Integer velocidadeMinima;
	private String horaMinima;
	private Integer velocidadeMaxima;
	private String horaMaxima;
	private Integer velocidade85Percentil;
	private Integer velocidadeMedia;
	
	public LocalVigente(){
	};
	
	public LocalVigente(Integer idLocal, Integer sequenciaLocal, Integer idConfiguracaEquipamento,
			Integer serieEquipamento, String nome, Boolean emOperacao,
			Double latitude, Double longitude) {
		super();
		this.idLocal = idLocal;
		this.sequenciaLocal = sequenciaLocal;
		this.idConfiguracaEquipamento = idConfiguracaEquipamento;
		this.serieEquipamento = serieEquipamento;
		this.nome = nome;
		this.emOperacao = emOperacao;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public LocalVigente(Integer idLocal, Integer sequenciaLocal, Integer idConfiguracaEquipamento,
			Integer serieEquipamento, String nome, Boolean emOperacao) {
		super();
		this.idLocal = idLocal;
		this.sequenciaLocal = sequenciaLocal;
		this.idConfiguracaEquipamento = idConfiguracaEquipamento;
		this.serieEquipamento = serieEquipamento;
		this.nome = nome;
		this.emOperacao = emOperacao;
	}
	
	public LocalVigente(Integer idLocal,  String descLocal) {
		super();
		this.idLocal = idLocal;
		this.nome = descLocal;
	}
	
	public LocalVigente(Integer idLocal,  String descLocal, Integer serieEquipamento) {
		super();
		this.idLocal = idLocal;
		this.nome = descLocal;
		this.serieEquipamento = serieEquipamento;
	}
	
	public LocalVigente(Integer idLocal, Integer idLocalEquipamento, String descLocal, Integer serieEquipamento) {
		super();
		this.idLocal = idLocal;
		this.idLocalEquipamento = idLocalEquipamento;
		this.nome = descLocal;
		this.serieEquipamento = serieEquipamento;
	}
	
	
	public LocalVigente(Integer idLocal, Integer serieEquipamento, String nome, Integer idPista, Integer pista, Long codigoGIT, Double latitude, Double longitude, String codigoEquipamentoDER) {
		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.nome = nome;
		this.idPista = idPista;
		this.pista = pista;
		this.codigoGIT = codigoGIT;
		this.latitude = latitude;
		this.longitude = longitude;
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}
	
	
	public LocalVigente(Integer idLocal, Integer serieEquipamento, String descLocal, String nomeAbreviado) {
		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.nome = descLocal;
		this.nomeAbreviado = nomeAbreviado;
	}
	
	
	public LocalVigente(Integer idLocal, Integer serieEquipamento, String descLocal, String nomeAbreviado,
						Double longitude, Double latitude, Date dataInicioOperacao, String codigosEquipamentosDER) {
		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.nome = descLocal;
		this.nomeAbreviado = nomeAbreviado;
		this.longitude = longitude;
		this.latitude = latitude;
		this.dataInicioOperacao = dataInicioOperacao;
		this.codigosEquipamentosDER = codigosEquipamentosDER;
	}
	
	
	public LocalVigente(Integer idLocal, Integer idPista, Integer faixa, Integer gstPistaSentidoFaixa, String codigoEquipamentoDER, Boolean faixaBRS, Boolean relFluxo) {
		super();
		this.idLocal = idLocal;
		this.idPista = idPista;
		this.faixa = faixa;
		this.gstPistaSentidoFaixa = gstPistaSentidoFaixa;
		this.codigoEquipamentoDER = codigoEquipamentoDER;
		this.faixaBRS = faixaBRS;
	}
	
	public LocalVigente(Integer idLocal,
						String velocidadeRegulamentada,
						Integer velocidadeMinima,
						String horaMinima,
						Integer velocidadeMaxima,
						String horaMaxima,
						Integer velocidade85Percentil,
						Integer velocidadeMedia) {
		super();
		this.idLocal = idLocal;
		this.velocidadeRegulamentada = velocidadeRegulamentada;
		this.velocidadeMinima = velocidadeMinima;
		this.horaMinima = horaMinima;
		this.velocidadeMaxima = velocidadeMaxima;
		this.horaMaxima = horaMaxima;
		this.velocidade85Percentil = velocidade85Percentil;
		this.velocidadeMedia = velocidadeMedia;
	}
	
	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	
	public Integer getSequenciaLocal() {
		return sequenciaLocal;
	}
	public void setSequenciaLocal(Integer sequenciaLocal) {
		this.sequenciaLocal = sequenciaLocal;
	}

	
	public Integer getIdLocalEquipamento() {
		return idLocalEquipamento;
	}
	public void setIdLocalEquipamento(Integer idLocalEquipamento) {
		this.idLocalEquipamento = idLocalEquipamento;
	}

	
	public Integer getIdConfiguracaEquipamento() {
		return idConfiguracaEquipamento;
	}
	public void setIdConfiguracaEquipamento(Integer idConfiguracaEquipamento) {
		this.idConfiguracaEquipamento = idConfiguracaEquipamento;
	}

	
	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}


	public Integer getPista() {
		return pista;
	}
	public void setPista(Integer pista) {
		this.pista = pista;
	}

	
	public Integer getIdPista() {
		return idPista;
	}
	public void setIdPista(Integer idPista) {
		this.idPista = idPista;
	}
	
	
	public Integer getFaixa() {
		return faixa;
	}
	public void setFaixa(Integer faixa) {
		this.faixa = faixa;
	}

	
	public Long getCodigoGIT() {
		return codigoGIT;
	}
	public void setCodigoGIT(Long codigoGIT) {
		this.codigoGIT = codigoGIT;
	}

	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	
	public Double getLongitude() {
		return longitude;
	}	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	
	public Boolean getEmOperacao() {
		return this.emOperacao;
	}
	public void setEmOperacao(Boolean emOperacao) {
		this.emOperacao = emOperacao;
	}
	
	
	public String getNomeAbreviado() {
		return nomeAbreviado;
	}
	public void setNomeAbreviado(String nomeAbreviado) {
		this.nomeAbreviado = nomeAbreviado;
	}

	
	public Integer getGstPistaSentido() {
		return gstPistaSentido;
	}
	public void setGstPistaSentido(Integer gstPistaSentido) {
		this.gstPistaSentido = gstPistaSentido;
	}
	
	
	public Integer getGstPistaSentidoFaixa() {
		return gstPistaSentidoFaixa;
	}
	public void setGstPistaSentidoFaixa(Integer gstPistaSentidoFaixa) {
		this.gstPistaSentidoFaixa = gstPistaSentidoFaixa;
	}

	
	public String getEquipamento() {
		return equipamento;
	}
	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	
	public String getSentido() {
		return sentido;
	}
	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	
	public String getPistaDescricao() {
		return pistaDescricao;
	}
	public void setPistaDescricao(String pistaDescricao) {
		this.pistaDescricao = pistaDescricao;
	}

	
	public String getNomePistaSentido() {
		return nomePistaSentido;
	}
	public void setNomePistaSentido(String nomePistaSentido) {
		this.nomePistaSentido = nomePistaSentido;
	}

	
	public Boolean getFaixaBRS() {
		return faixaBRS;
	}
	public void setFaixaBRS(Boolean faixaBRS) {
		this.faixaBRS = faixaBRS;
	}

	
	public String getVelocidadeRegulamentada() {
		return velocidadeRegulamentada;
	}
	public void setVelocidadeRegulamentada(String velocidadeRegulamentada) {
		this.velocidadeRegulamentada = velocidadeRegulamentada;
	}

	
	public Integer getVelocidadeMinima() {
		return velocidadeMinima;
	}
	public void setVelocidadeMinima(Integer velocidadeMinima) {
		this.velocidadeMinima = velocidadeMinima;
	}

	
	public String getHoraMinima() {
		return horaMinima;
	}
	public void setHoraMinima(String horaMinima) {
		this.horaMinima = horaMinima;
	}

	
	public Integer getVelocidadeMaxima() {
		return velocidadeMaxima;
	}
	public void setVelocidadeMaxima(Integer velocidadeMaxima) {
		this.velocidadeMaxima = velocidadeMaxima;
	}

	
	public String getHoraMaxima() {
		return horaMaxima;
	}
	public void setHoraMaxima(String horaMaxima) {
		this.horaMaxima = horaMaxima;
	}

	
	public Integer getVelocidade85Percentil() {
		return velocidade85Percentil;
	}
	public void setVelocidade85Percentil(Integer velocidade85Percentil) {
		this.velocidade85Percentil = velocidade85Percentil;
	}

	
	public Integer getVelocidadeMedia() {
		return velocidadeMedia;
	}
	public void setVelocidadeMedia(Integer velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}
	
	
	public String getCodigoEquipamentoDER() {
		return codigoEquipamentoDER;
	}
	public void setCodigoEquipamentoDER(String codigoEquipamentoDER) {
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}
	
	
	public String getCodigosEquipamentosDER() {
		return codigosEquipamentosDER;
	}
	public void setCodigosEquipamentosDER(String codigosEquipamentosDER) {
		this.codigosEquipamentosDER = codigosEquipamentosDER;
	}
	
		
	public Date getDataInicioOperacao() {
		return dataInicioOperacao;
	}
	public void setDataInicioOperacao(Date dataInicioOperacao) {
		this.dataInicioOperacao = dataInicioOperacao;
	}
	

	/**
	 * Busca locais vegentes no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome, grupo.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos Local
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<LocalVigente> buscaLocalVigentePor(Map<String,Object> mFiltros, Integer iOrdem) throws ConexaoException, SQLException {
		List<LocalVigente> lRet = new ArrayList<LocalVigente>();
		lRet = buscaLocalVigentePor(mFiltros, iOrdem, false);
		return lRet;
	}
	
	public static List<LocalVigente> buscaLocalVigentePor(Map<String,Object> mFiltros, Integer iOrdem, Boolean grupoVelsis) throws ConexaoException, SQLException {
		List<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("SELECT l.id_local, l.sequencia_local,");
		sbSQL.append("		  l.id_configuracao_equipamento,");
		sbSQL.append("		  l.serie_equipamento,");
		sbSQL.append("		  l.nome,");
		sbSQL.append("		  l.em_operacao");
		sbSQL.append("	FROM");
		sbSQL.append("		local_vigente l WITH (NOLOCK) ");
		sbSQL.append("		INNER JOIN configuracao_equipamento e WITH (NOLOCK) ON e.id_configuracao_equipamento = l.id_configuracao_equipamento");
		sbSQL.append("	WHERE ");
		
		if (grupoVelsis) {
			sbSQL.append(" e.serie_equipamento NOT BETWEEN 9907000 AND 2014000000 AND ");
		}
		
		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("nome", "l.nome LIKE ?");
		mRegras.put("grupo", "e.id_grupo_equipamento = ?");
		mRegras.put("serie_equipamento", "l.serie_equipamento = ?");
		mRegras.put("id_local", "l.id_local = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY " + iOrdem);
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(
							rs.getInt("id_local"), rs.getInt("sequencia_local"),
							rs.getInt("id_configuracao_equipamento"), 
							rs.getInt("serie_equipamento"), 
							rs.getString("nome"),
							rs.getBoolean("em_operacao"))
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
	
	public static LocalVigente buscaLocalVigentePorSerieEquipamento(Integer serieEquipamento) throws SQLException, ConexaoException {
	
		Map<String, Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("serie_equipamento", serieEquipamento);
		
		List<LocalVigente> lRet = buscaLocalVigentePor(mFiltros, 1);
		
		if( lRet.size() > 0){
			return lRet.get(0);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Busca um local vigente no BD, considerando que ele está vigente.
	 * @param idLocal Identificador do local.
	 * @return Objeto Local materializado, ou null se não encontrar.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static LocalVigente buscaLocalVigentePorIdLocal(Integer idLocal) throws SQLException, ConexaoException {
		Map<String, Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_local", idLocal);
		
		List<LocalVigente> lRet = buscaLocalVigentePor(mFiltros, 1);
		
		if( lRet.size() > 0){
			return lRet.get(0);
		}
		else {
			return null;
		}
	}


	
	/**
	 * Preenche um bean com os dados.
	 * @param bean Objeto bean que será preenchido.
	 */
	public void getToGrupoBean(LocalVigenteBean bean) {
		bean.setIdConfiguracaEquipamento(this.idConfiguracaEquipamento);
		bean.setIdLocal(this.idLocal);
		bean.setNome(this.nome);
		bean.setSerieEquipamento(this.serieEquipamento);
	}


	public Integer getTipoEquipamento() {
		Integer tipoEquipamento = -1;
		
		if (serieEquipamento != null) {
		if     ( serieEquipamento < 9907000                                 ) tipoEquipamento = 5;  				
		else if( serieEquipamento >= 9907000 && serieEquipamento <= 9907099 ) tipoEquipamento = 2;   	
		else if( serieEquipamento >= 9907100 && serieEquipamento <= 9907299 ) tipoEquipamento = 1;   	
		else if( serieEquipamento >= 9908100 && serieEquipamento <= 9908199 ) tipoEquipamento = 4;   
		else if( serieEquipamento >= 2014000000                             ) tipoEquipamento = 3;
		}
		
		return tipoEquipamento;
	}
	
	public String getTipoEquipamentoStr() {
		switch (getTipoEquipamento()) {
		case 1:
			return "Grupo A";
		case 2:
			return "Grupo B";
		case 3:
			return "Grupo C";
		case 4:
			return "BARREIRA";
		case 5:
			return "ESTÁTICO";
		default:
			return "";
		}
	}
	
	/**
	 * Busca locais vegentes no BD - APENAS PARA O CAV.
	 * Luiz Amaral 23/06/2014
	 * 
	 * 19/03/2015 - Felipe - Atualizado para buscar da tabela configuracao_equipamento_medicao
	 * - Adicionado funcionalidade para filtrar por tipo de equipamento
	 */
	public static List<LocalVigente> buscaLocalVigenteCAV() throws ConexaoException, SQLException {
		return buscaLocalVigenteCAV(0);
	}
	public static List<LocalVigente> buscaLocalVigenteCAV(int tipo_equipamento) throws ConexaoException, SQLException {
		
		List<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT cod_pista, descricao, tipoEquipamento, serie_equipamento FROM ");
		sbSQL.append(" (SELECT DISTINCT  ");
		sbSQL.append(" l.cod_pista,    ");
		sbSQL.append(" l.descricao,  ");
		sbSQL.append(" l.serie_equipamento,  ");
		sbSQL.append(" CASE WHEN serie_equipamento < 9907000 THEN 5 ");  				
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9907000 AND 9907099 THEN 2 ");  	
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9907100 AND 9907299 THEN 1 ");  	
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9908100 AND 9908199 THEN 4 ");  
		sbSQL.append(" 	 WHEN serie_equipamento >= 2014000000 THEN 3 ");  				
		sbSQL.append(" END AS tipoEquipamento "); 
		sbSQL.append(" FROM configuracao_equipamento_medicao l (NOLOCK)) AS sub1 "); 
		sbSQL.append(" WHERE ? IN (0, tipoEquipamento)  ");
		sbSQL.append(" ORDER BY tipoEquipamento, cod_pista ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, tipo_equipamento);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(
							rs.getInt("cod_pista"), 
							rs.getString("descricao"),
							rs.getInt("serie_equipamento"))
						);
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
				throw new ConexaoException("ERRO de SQL - Obter Locais Vigente para o CAV.", e);
			}			
		}
		return lRet;
	}
	
	public static List<LocalVigente> buscaLocalVigenteCAVEquipamento(int tipo_equipamento, int cod_pista, int cod_pista_prodam) throws ConexaoException, SQLException {
		
		List<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT cod_pista, cod_pista_prodam, descricao, tipoEquipamento, serie_equipamento  FROM ");
		sbSQL.append(" (SELECT DISTINCT ");  
		sbSQL.append(" l.cod_pista, ");    
		sbSQL.append(" CASE WHEN l.cod_pista_prodam = 0 AND mi.cod_pista_prodam IS NOT NULL THEN mi.cod_pista_prodam ELSE l.cod_pista_prodam END AS cod_pista_prodam, ");
		sbSQL.append(" l.descricao, ");  
		sbSQL.append(" l.serie_equipamento, ");  
		sbSQL.append(" CASE WHEN serie_equipamento < 9907000 THEN 5 ");   				
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9907000 AND 9907099 THEN 2 ");   	
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9907100 AND 9907299 THEN 1 ");   	
		sbSQL.append(" 	 WHEN serie_equipamento BETWEEN 9908100 AND 9908199 THEN 4 ");   
		sbSQL.append(" 	 WHEN serie_equipamento >= 2014000000 THEN 3 ");   				
		sbSQL.append(" END AS tipoEquipamento ");  
		sbSQL.append(" FROM configuracao_equipamento_medicao l (NOLOCK) ");
		sbSQL.append(" LEFT JOIN movimento_importacao mi (NOLOCK) ON l.cod_pista = mi.id_local AND l.cod_pista_alternativo = mi.pista ");
		sbSQL.append(" ) AS sub1 ");  
		sbSQL.append(" WHERE ? IN (0, tipoEquipamento) ");  
		sbSQL.append(" AND   ? IN (0, cod_pista) ");
		sbSQL.append(" AND   ? IN (0, cod_pista_prodam) "); 
		sbSQL.append(" AND cod_pista_prodam > 0 ");
		sbSQL.append(" ORDER BY tipoEquipamento, cod_pista_prodam ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, tipo_equipamento);
			ps.setInt(2, cod_pista);
			ps.setInt(3, cod_pista_prodam);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(
							rs.getInt("cod_pista"), 
							rs.getInt("cod_pista_prodam"), 
							rs.getString("descricao"),
							rs.getInt("serie_equipamento"))
						);
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
				throw new ConexaoException("ERRO de SQL - Obter Locais Vigente para o CAV.", e);
			}			
		}
		return lRet;
	}

	public static List<LocalVigente> buscaLocalVigente() throws ConexaoException, SQLException {
		
		List<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT l.id_local ");
		sbSQL.append("		 ,l.sequencia_local ");
		sbSQL.append("		 ,l.id_configuracao_equipamento ");
		sbSQL.append("		 ,l.serie_equipamento ");
		sbSQL.append("		 ,l.nome ");
		sbSQL.append("		 ,l.em_operacao ");
		sbSQL.append(" FROM   local_vigente l (NOLOCK) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(rs.getInt("id_local"),
										  rs.getInt("sequencia_local"),
										  rs.getInt("id_configuracao_equipamento"),
										  rs.getInt("serie_equipamento"),
										  rs.getString("nome"),
										  rs.getBoolean("em_operacao"))
				);
			}
			
		} catch (Exception e) {
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
	
	
	public ArrayList<LocalVigente> buscaListaLocalVigente() throws ConexaoException, SQLException {
		return buscaListaLocalVigente(null);
	}
	
	public ArrayList<LocalVigente> buscaListaLocalVigente(Integer idLocal) throws ConexaoException, SQLException {
		
		ArrayList<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT l.id_local ");
		sbSQL.append("		 ,l.serie_equipamento ");
		sbSQL.append("		 ,l.nome ");
		sbSQL.append("		 ,l.nome_abreviado ");
		sbSQL.append(" FROM   local_vigente l (NOLOCK) ");
		
		if (idLocal != null) {
			sbSQL.append(" WHERE l.id_local = ? ");
		}
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  l.id_local ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if (idLocal != null) {
				ps.setInt(1, idLocal);
			}
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(rs.getInt("id_local"),
										  rs.getInt("serie_equipamento"),
										  rs.getString("nome"),
										  rs.getString("nome_abreviado"))
				);
			}
			
		} catch (Exception e) {
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
	
	/*
	 * Buscar locais para relatórios de fluxo veicular com os dados necessários para preenchimento dos cabeçalhos
	 */
	public ArrayList<LocalVigente> buscaListaLocalVigenteRelFluxoRJ(Integer idLocal) throws ConexaoException, SQLException {
		
		ArrayList<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT lv.id_local, ");
		sbSQL.append("		  lv.serie_equipamento, ");
		sbSQL.append("		  lv.nome, ");
//		sbSQL.append("		  ISNULL(SUBSTRING(lv.nome_abreviado, 1, 31), SUBSTRING(lv.nome, 1, 31)) AS nome_abreviado, ");
		sbSQL.append("		  LTRIM(RTRIM(lv.serie_equipamento)) AS nome_abreviado, ");
		sbSQL.append("		  lv.posicao_lon, ");
		sbSQL.append("		  lv.posicao_lat, ");
		sbSQL.append("		  lv.data_inicio, ");
		sbSQL.append("		  LTRIM(STR(lv.serie_equipamento)) AS codigos_equipamentos ");
		sbSQL.append(" FROM   local_vigente lv (NOLOCK) ");
		sbSQL.append(" WHERE  lv.desativado = 0 ");
//		sbSQL.append(" 		  AND CAST(lv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE) ");
				
		if (idLocal != null) {
			sbSQL.append(" 		  AND lv.id_local = ? ");
		}
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  lv.id_local ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if (idLocal != null) {
				ps.setInt(1, idLocal);
			}
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(rs.getInt("id_local"),
										  rs.getInt("serie_equipamento"),
										  rs.getString("nome"),
										  rs.getString("nome_abreviado"),
										  rs.getDouble("posicao_lon"),
										  rs.getDouble("posicao_lat"),
										  rs.getDate("data_inicio"),
										  rs.getString("codigos_equipamentos"))
				);
			}
			
		} catch (Exception e) {
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
	
	/*
	 * Buscar faixas por local para relatórios de fluxo veicular
	 */
	public ArrayList<LocalVigente> buscaListaLocalVigenteFaixaRelFluxoRJ(Integer idLocal) throws ConexaoException, SQLException {
		
		ArrayList<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT lv.id_local, ");
		sbSQL.append(" 		  lv.id_pista, ");
		sbSQL.append(" 		  lv.nome, ");
		sbSQL.append(" 		  lv.serie_equipamento, ");
		sbSQL.append(" 		  lv.posicao_lat,	lv.posicao_lon, ");
		sbSQL.append(" 		  lv.cod_pista_alternativo AS faixa, ");
		sbSQL.append(" 		  lv.codigo_GIT AS gst_pista_sentido_faixa, ");
		sbSQL.append(" 		  lv.codigo_equipamento, ");
		sbSQL.append(" 		  CASE WHEN CHARINDEX('BRS', lv.nome) > 0 THEN 1 ELSE 0 END faixa_brs ");
		sbSQL.append(" FROM   local_pista_vigente lv (NOLOCK) ");
		
		sbSQL.append(" WHERE  lv.id_local = ? ");
		
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  lv.id_local, ");
		sbSQL.append(" 		  lv.id_pista, ");
		sbSQL.append(" 		  lv.cod_pista_alternativo, ");
		sbSQL.append(" 		  lv.codigo_GIT, ");
		sbSQL.append(" 		  lv.codigo_equipamento, ");
		sbSQL.append(" 		  lv.nome, ");
		sbSQL.append(" 		  lv.serie_equipamento, ");
		sbSQL.append(" 		  lv.posicao_lat,	lv.posicao_lon ");
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  lv.codigo_equipamento ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idLocal);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				LocalVigente lv = new LocalVigente(rs.getInt("id_local"),
										  rs.getInt("id_pista"),
										  rs.getInt("faixa"),
										  rs.getInt("gst_pista_sentido_faixa"),
										  rs.getString("codigo_equipamento"),
										  rs.getBoolean("faixa_brs"),
										  true);
				lv.setNome(rs.getString("nome"));
				lv.setSerieEquipamento(rs.getInt("serie_equipamento"));
				lv.setLongitude(rs.getDouble("posicao_lon"));
				lv.setLatitude(rs.getDouble("posicao_lat"));
				lv.setCodigosEquipamentosDER(rs.getString("codigo_equipamento"));
				lv.setNomeAbreviado(rs.getString("codigo_equipamento"));
				lRet.add(lv);
			}
			
		} catch (Exception e) {
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

	public ArrayList<LocalVigente> buscaLocalFaixa() throws ConexaoException, SQLException {
		return buscaLocalFaixa(null);
	}
	public ArrayList<LocalVigente> buscaLocalFaixa(Integer idLocal) throws ConexaoException, SQLException {
		
		ArrayList<LocalVigente> lRet = new ArrayList<LocalVigente>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT lv.id_local, ");
		sbSQL.append("		  lv.serie_equipamento, ");
		sbSQL.append("		  lv.nome, ");
		sbSQL.append("		  lv.id_pista, ");
		sbSQL.append("		  lv.cod_pista_alternativo, ");
		sbSQL.append("		  lv.codigo_GIT, ");
		sbSQL.append("		  lv.posicao_lat, ");
		sbSQL.append("		  lv.posicao_lon, ");
		sbSQL.append("		  lv.codigo_equipamento ");
		sbSQL.append(" FROM   local_pista_vigente lv (NOLOCK) ");
		
		if (idLocal != null) {
			sbSQL.append(" WHERE  lv.id_local = ? ");
		}
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  lv.id_local, ");
		sbSQL.append(" 		  lv.id_pista ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if (idLocal != null) {
				ps.setInt(1, idLocal);
			}
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalVigente(rs.getInt("id_local"),
										  rs.getInt("serie_equipamento"),
										  rs.getString("nome"),
										  rs.getInt("id_pista"),
										  rs.getInt("cod_pista_alternativo"),
										  rs.getLong("codigo_GIT"),
										  rs.getDouble("posicao_lat"),
										  rs.getDouble("posicao_lon"),
										  rs.getString("codigo_equipamento")
										)
						);
			}
			
		} catch (Exception e) {
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
	
	public static LocalVigente obterInfoVelocidadeEquipamento(Integer idLocal, Date data) throws ConexaoException {
		
		LocalVigente lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" EXEC dbo.spu_obterInfoVelocidadeEquipamento ?, ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idLocal);
			ps.setDate(2, new java.sql.Date(data.getTime()));

			rs = ps.executeQuery();
			if (rs.next()) {
				lRet = new LocalVigente(
						rs.getInt("id_local"), 
						rs.getString("velocidade_regulamentada"), 
						rs.getInt("velocidade_min"), 
						rs.getString("hora_min"),
						rs.getInt("velocidade_max"),
						rs.getString("hora_max"),
						rs.getInt("velocidade_85_percentil"),
						rs.getInt("velocidade_media")
				);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
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
	
}
