/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 15/01/2007

  Descricao: Classe de negócio para busca de status do local.

  Historico:

    $Log: LocalStatus.java,v $
    Revision 1.12  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.11  2009/01/28 19:25:49  fos
    Consertada ligação incompleta na query com a tabela local.

    Revision 1.10  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.8  2008/11/11 16:25:20  fos
    Consertado BUG que não retornava a data com a hora.

    Revision 1.7  2008/10/23 19:29:22  fos
    Agora também possui o atributo com o número de série do equipamento.
    Os status não verificados correspondem ao número 0.

    Revision 1.6  2008/10/16 21:14:30  fos
    Adequado o LocalStatius para o GTW

    Revision 1.5  2008/07/23 14:24:49  fos
    Ajustado o nome da função.

    Revision 1.4  2007/07/06 13:04:54  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/05/08 12:38:30  fos
    Agora faz uma busca ordenada

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


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
import com.consilux.ws.TConnectionStatusEnum;
import com.consilux.ws.TDIVStatusEnum;
import com.consilux.ws.TPowerStatusEnum;

/**
 * Classe de negócio para busca de status do local.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.12 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class LocalStatus {
	public enum StatusConexao {
		NAO_VERIFICADO(0),
		ON_LINE(1),
		OFF_LINE(2);

		private Integer id;

		StatusConexao(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static StatusConexao valueOf(Integer id) {
			switch (id) {
			case 1:
				return ON_LINE; 
			case 2:
				return OFF_LINE; 
			default:
				return NAO_VERIFICADO; 
			}
		}
	}
	public enum StatusDIV {
		NAO_VERIFICADO(0),
		OPERANTE(1),
		NAO_OPERANTE(2);
		private Integer id;
		StatusDIV(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static StatusDIV valueOf(Integer id) {
			switch (id) {
			case 1:
				return OPERANTE; 
			case 2:
				return NAO_OPERANTE; 
			default:
				return NAO_VERIFICADO; 
			}
		}
	}
	public enum StatusEnergia {
		NAO_VERIFICADO(0),
		COM_ENERGIA(1),
		SEM_ENERGIA(2);
		private Integer id;
		StatusEnergia(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static StatusEnergia valueOf(Integer id) {
			switch (id) {
			case 1:
				return COM_ENERGIA; 
			case 2:
				return SEM_ENERGIA; 
			default:
				return NAO_VERIFICADO; 
			}
		}
	}

	private Integer idLocal;
	private Integer idEquipamento;
	private String nome;
	private Date dataAtualizacao;
	private StatusConexao statusConexao;
	private StatusDIV statusDIV;
	private StatusEnergia statusEnergia;
	private String ip;
	private Integer serieEquipamento;
	
	private LocalStatus(Integer idLocal, Integer idEquipamento, String nome,
			Date dataAtualizacao, StatusConexao statusConexao,
			StatusDIV statusDIV, StatusEnergia statusEnergia, String ip, Integer serieEquipamento) {
		super();
		this.idLocal = idLocal;
		this.idEquipamento = idEquipamento;
		this.nome = nome;
		this.dataAtualizacao = dataAtualizacao;
		this.statusConexao = statusConexao;
		this.statusDIV = statusDIV;
		this.statusEnergia = statusEnergia;
		this.ip = ip;
		this.serieEquipamento = serieEquipamento;
	}

	/**
	 * Busca status de locais no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome.
	 * @return Lista de objetos LocalStatus
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<LocalStatus> buscaLocalStatusPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		return LocalStatus.buscaLocalStatusPor(mFiltros, 1);
	}

	/**
	 * Busca status de locais no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos LocalStatus
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<LocalStatus> buscaLocalStatusPor(Map<String,Object> mFiltros, Integer iOrdem) throws ConexaoException, ModelException {
		List<LocalStatus> lRet = new ArrayList<LocalStatus>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT l.id_local,");
		sbSQL.append("		c.serie_equipamento,");
		sbSQL.append("		l.nome,");
		sbSQL.append("		ls.data_atualizacao,");
		sbSQL.append("		ls.status_conexao,");
		sbSQL.append("		ls.status_DIV,");
		sbSQL.append("		ls.status_energia,");
		sbSQL.append("		lsc.ip");
		sbSQL.append("	FROM");
		sbSQL.append("		local_vigente l");
		sbSQL.append("      LEFT JOIN local_status ls ON ls.id_local = l.id_local");
		sbSQL.append("      LEFT JOIN local_status_conexao lsc ON lsc.id_local = l.id_local");
		sbSQL.append("		INNER JOIN configuracao_equipamento c ON c.id_configuracao_equipamento = l.id_configuracao_equipamento");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("local", "l.id_local = ?");
		mRegras.put("nome", "l.nome LIKE ?");
		mRegras.put("serie_equipamento", "c.serie_equipamento = ?");
		mRegras.put("grupo", "c.id_grupo_equipamento <= ?");
		mRegras.put("naoestatico", "l.id_local > ?");
		
		sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
		sbSQL.append(" ORDER BY "+iOrdem);

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			//Ajustando os valores dos parametros para os wheres:
			ps = conn.prepareStatement(sbSQL.toString());
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new LocalStatus(
						rs.getInt("id_local"),
						rs.getInt("serie_equipamento"),
						rs.getString("nome"),
						rs.getTimestamp("data_atualizacao"),
						StatusConexao.valueOf(rs.getInt("status_conexao")),
						StatusDIV.valueOf(rs.getInt("status_DIV")),
						StatusEnergia.valueOf(rs.getInt("status_energia")),
						rs.getString("ip"), rs.getInt("serie_equipamento")
						
				)
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

	/**
	 * @return Retorna o valor de idLocal atual.
	 */
	public Integer getIdLocal() {
		return idLocal;
	}

	/**
	 * @return Retorna o valor de nome atual.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return Retorna o valor de dataAtualizacao atual.
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/**
	 * @return Retorna o valor de statusConexao atual.
	 */
	public StatusConexao getStatusConexao() {
		return statusConexao;
	}

	/**
	 * @return Retorna o valor de statusDIV atual.
	 */
	public StatusDIV getStatusDIV() {
		return statusDIV;
	}

	/**
	 * @return Retorna o valor de statusEnergia atual.
	 */
	public StatusEnergia getStatusEnergia() {
		return statusEnergia;
	}

	/**
	 * @return Retorna o valor de idEquipamento atual.
	 */
	public Integer getIdEquipamento() {
		return idEquipamento;
	}
	
	/**
	 * @return Retorna o valor de serieEquipamento atual.
	 */
	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}	
	
	/**
	 * Retorna o valor do campo 'ip' atual.
	 * @return the ip
	 */
	public String getIp() {
		return this.ip;
	}

	public static StatusConexao converte(TConnectionStatusEnum connectionStatus) {
		StatusConexao ret = null;
		if (connectionStatus == TConnectionStatusEnum.CS_CONNECTED)
			ret = StatusConexao.ON_LINE;
		if (connectionStatus == TConnectionStatusEnum.CS_DISCONNECTED)
			ret = StatusConexao.OFF_LINE;
		if (connectionStatus == TConnectionStatusEnum.CS_NOT_VERIFIED)
			ret = StatusConexao.NAO_VERIFICADO;
		return ret;
	}

	public static StatusEnergia converte(TPowerStatusEnum energyStatus) {
		StatusEnergia ret = null;
		if (energyStatus == TPowerStatusEnum.PS_AC_POWER)
			ret = StatusEnergia.COM_ENERGIA;
		if (energyStatus == TPowerStatusEnum.PS_BATTERY)
			ret = StatusEnergia.SEM_ENERGIA;
		if (energyStatus == TPowerStatusEnum.PS_CANT_CHECK)
			ret = StatusEnergia.NAO_VERIFICADO;
		return ret;
	}

	public static StatusDIV converte(TDIVStatusEnum divStatus) {
		StatusDIV ret = null;
		if (divStatus == TDIVStatusEnum.DS_WORKING)
			ret = StatusDIV.OPERANTE;
		if (divStatus == TDIVStatusEnum.DS_FAIL)
			ret = StatusDIV.NAO_OPERANTE;
		if (divStatus == TDIVStatusEnum.DS_CANT_CHECK)
			ret = StatusDIV.NAO_VERIFICADO;
		return ret;
	}
	
	
	
	/**
	 * Objetivo: Verifica se o usuário consta no grupo que não pode acessar remoto
	 * Autor: Luiz Amaral
	 * Data: 17/03/2015
	 */
	public static boolean validarAcessoRemoto(Usuario usuario) throws ConexaoException {

		StringBuilder sbSQL = new StringBuilder();
		
		boolean blnRetorno = false; 
		
		sbSQL.append("SELECT ");
		sbSQL.append("	sug.id_usuario, ");
		sbSQL.append("	su.nome, ");
		sbSQL.append("	sg.id_grupo, ");
		sbSQL.append("	sg.descricao ");
		sbSQL.append(" FROM ");
		sbSQL.append("   sis_usuario_grupo sug WITH (NOLOCK) ");
		sbSQL.append("   INNER JOIN  sis_grupo sg WITH (NOLOCK) ");
		sbSQL.append("		ON sg.id_grupo = sug.id_grupo ");
		sbSQL.append("   INNER JOIN sis_usuario su WITH (NOLOCK) ");
		sbSQL.append("		ON su.id_usuario = sug.id_usuario ");
		sbSQL.append(" WHERE ");
		sbSQL.append("   sug.id_usuario = ? ");
		sbSQL.append("   AND ( sg.descricao = 'Consulta Eventos CSX' OR sg.descricao = 'Visualizadores CAV' )  ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, usuario.getId());

			rs = ps.executeQuery();
			
			if (rs.next())
				blnRetorno = true;
				
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL LocalStatus.validarAcessoRemoto", e);
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
				throw new ConexaoException("ERRO de SQL LocalStatus.validarAcessoRemoto", e);
			}			
		}		
		return blnRetorno;
	}	

	
	
	

}
