/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 03/04/2007

  Descricao: Classe de negócio para busca de informações da uma infração.

  Historico:

    $Log: InfracaoCompleta.java,v $
    Revision 1.12  2009/04/16 14:12:20  fos
    Agora mostra se a infrações esta na espera ou não.

    Revision 1.11  2009/04/03 15:45:01  fos
    Agora busca dados da infração no processo atual.

    Revision 1.10  2009/03/12 13:07:36  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.9  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.6  2008/08/12 13:00:42  fos
    Agora a classe já trabalha com o banco de dados novo.

    Revision 1.5  2008/07/31 21:09:34  fos
    - Retirado os atributos do cadastro de veículo.
    - Colocado o identificador de imagem no local.

    Revision 1.4  2008/05/14 21:44:07  fos
    Agora busca as informações específicas da infração.

    Revision 1.3  2008/05/08 21:27:03  fos
    Agora retorna o identificador da InfracaoProcesso.

    Revision 1.2  2008/02/28 18:42:07  fos
    Agora também armazena a inconsistência.

    Revision 1.1  2008/02/06 19:20:24  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/07/06 13:04:14  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.1  2007/04/11 12:07:00  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de informações da uma infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.12 $ $Date: 2009/04/16 14:12:20 $ $Author: fos $
 */
public class InfracaoCompleta {
	private static Logger logger = Logger.getLogger(InfracaoCompleta.class); 
	private Integer id;
	private Integer idInconsistencia;
	private Integer idEnquadramento;
	private Integer idImagemLocal;
	private Integer idLocal;
	private String nomeLocal;
	private Integer idProcesso;
	private String nomeProcesso;
	private Integer idUsuarioAtual;
	private String usuarioAtual;
	private Integer pista;
	private String placa;
	private Date dataVeiculo;
	private Integer velocidadeVeiculo;
	private Integer velocidadeLimite;
	private Integer segundosVeiculo;
	private Integer segundoTolerancia;
	private Integer idImagemPAN;
	private Integer idImagemOBJ;
	private Integer infracaoProcesso;
	private Boolean espera;
	private Integer idRemessa;
	private Long idVeiculo;
	private Boolean equipamentoCapturaFrontal;
	private Integer areaPista;
	private String classificacaoTarja;
	private Integer comPesagem;


	/**
	 * Constrói o objeto InfracaoCompleto com os seus respectivos atributos.
	 * @param id Identificador da infração
	 * @param idInconsistencia Identificador da inconsistência, se existir.
	 * @param idImagemLocal Identificador da imagem no local.
	 * @param idLocal Identificador do local
	 * @param nomeLocal Nome do local
	 * @param pista Pista onde o veículo foi capturado
	 * @param placa Placa digitada da infração
	 * @param dataVeiculo Data/Hora de captura do veículo
	 * @param velocidadeVeiculo Velocidade de captura do veículo
	 * @param velocidadeLimite Velocidade limite da pista onde o veículo foi capturado
	 * @param segundosVeiculo Tempo em segundos que o semáforo estava em sinal vermelho quando o veículo cruzou 
	 * @param segundoTolerancia Tempo em segundos de tolerância para registrar a infração de um veículo que cruzou em sinal vermelho
	 * @param idImagemPAN Identificador da imagem panorâmica para esta infração.
	 * @param idImagemOBJ Identificador da imagem objetiva para esta infração.
	 * @param infracaoProcesso Objeto InfracaoProcesso materializado.
	 * @param equipamentoCapturaFrontal Se True o equipamento têm captura frontal, se False não.
	 * @param areaPista Área de infração qual a pista está enquadrada.
	 */

	public InfracaoCompleta(Integer id, Integer idInconsistencia, Integer idEnquadramento,
			Integer idImagemLocal, Integer idLocal, String nomeLocal,
			Integer idProcesso, String nomeProcesso, Integer idUsuarioAtual,
			String usuarioAtual, Integer pista, String placa, Date dataVeiculo,
			Integer velocidadeVeiculo, Integer velocidadeLimite,
			Integer segundosVeiculo, Integer segundoTolerancia,
			Integer idImagemPAN, Integer idImagemOBJ, Integer infracaoProcesso,
			Boolean espera, Integer id_remessa, Long idVeiculo, Boolean equipamentoCapturaFrontal,
			Integer areaPista, String classificacaoTarja, Integer comPesagem) {
		super();
		this.id = id;
		this.idInconsistencia = idInconsistencia;
		this.idEnquadramento = idEnquadramento;
		this.idImagemLocal = idImagemLocal;
		this.idLocal = idLocal;
		this.nomeLocal = nomeLocal;
		this.idProcesso = idProcesso;
		this.nomeProcesso = nomeProcesso;
		this.idUsuarioAtual = idUsuarioAtual;
		this.usuarioAtual = usuarioAtual;
		this.pista = pista;
		this.placa = placa;
		this.dataVeiculo = dataVeiculo;
		this.velocidadeVeiculo = velocidadeVeiculo;
		this.velocidadeLimite = velocidadeLimite;
		this.segundosVeiculo = segundosVeiculo;
		this.segundoTolerancia = segundoTolerancia;
		this.idImagemPAN = idImagemPAN;
		this.idImagemOBJ = idImagemOBJ;
		this.infracaoProcesso = infracaoProcesso;
		this.espera = espera;
		this.idRemessa = id_remessa;
		this.idVeiculo = idVeiculo;
		this.equipamentoCapturaFrontal = equipamentoCapturaFrontal;
		this.areaPista = areaPista;
		this.classificacaoTarja = classificacaoTarja;
		this.comPesagem = comPesagem;
	}

	/**
	 * Busca uma infração no BD.
	 * @param iInfracao Identificador da infração
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public static InfracaoCompleta buscaInfracaoPorId(Integer iInfracao) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT ");
		sbSQL.append("	id_infracao,");
		sbSQL.append("	id_inconsistencia,");
		sbSQL.append("  id_enquadramento,");
		sbSQL.append("	id_local,");
		sbSQL.append("	id_imagem_local,");
		sbSQL.append("	nome_pista,");
		sbSQL.append("	id_processo,");
		sbSQL.append("	nome_processo,");
		sbSQL.append("	id_usuario_atual,");
		sbSQL.append("	usuario_atual,");
		sbSQL.append("	pista,");
		sbSQL.append("	placa,");
		sbSQL.append("	data_veiculo,");
		sbSQL.append("	velocidade_veiculo,");
		sbSQL.append("	velocidade_limite,");
		sbSQL.append("	segundos_veiculo,");
		sbSQL.append("	segundos_tolerancia,");
		sbSQL.append("	id_imagem_pan,");
		sbSQL.append("	id_imagem_obj,");
		sbSQL.append("	id_infracao_processo, ");
		sbSQL.append("	espera, ");
		sbSQL.append("  id_remessa,");
		sbSQL.append("	id_veiculo, ");
		sbSQL.append("	equipamento_captura_frontal,");
		sbSQL.append("	area_pista, ");
		sbSQL.append("	'' AS porteVeiculo, ");
		sbSQL.append("	com_pesagem ");
		sbSQL.append("FROM");
		sbSQL.append("	infracao_completa WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_infracao = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, iInfracao);
			Date inicio = new Date();
			
			rs = ps.executeQuery();
			logger.debug("[TEMPO] executeQuery infracao_completa: "+(new Date().getTime() - inicio.getTime()));
			if (rs.next()) {
				return new InfracaoCompleta(
						rs.getInt("id_infracao"),
						rs.getInt("id_inconsistencia"),
						rs.getInt("id_enquadramento"),
						rs.getInt("id_imagem_local"),
						rs.getInt("id_local"),
						rs.getString("nome_pista"),
						rs.getInt("id_processo"),
						rs.getString("nome_processo"),
						rs.getInt("id_usuario_atual"),
						rs.getString("usuario_atual"),
						rs.getInt("pista"),
						rs.getString("placa"),
						rs.getTimestamp("data_veiculo"),
						rs.getInt("velocidade_veiculo"),
						rs.getInt("velocidade_limite"),
						rs.getInt("segundos_veiculo"),
						rs.getInt("segundos_tolerancia"),
						rs.getInt("id_imagem_pan"),
						rs.getInt("id_imagem_obj"),
						rs.getInt("id_infracao_processo"),
						rs.getBoolean("espera"),
						rs.getInt("id_remessa"),
						rs.getLong("id_veiculo"),
						rs.getBoolean("equipamento_captura_frontal"),
						rs.getInt("area_pista"),
						rs.getString("porteVeiculo"),
						rs.getInt("com_pesagem")
				);
			}
			else {
				return null;
			}
		} 
		finally {
			if (conn != null)
				conn.close();							
		}
	}


	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Retorna o valor de idLocal atual.
	 */
	public Integer getIdLocal() {
		return idLocal;
	}

	/**
	 * @return Retorna o valor de nomeLocal atual.
	 */
	public String getNomeLocal() {
		return nomeLocal;
	}

	/**
	 * @return Retorna o valor de pista atual.
	 */
	public Integer getPista() {
		return pista;
	}

	/**
	 * @return Retorna o valor de placa atual.
	 */
	public String getPlaca() {
		return placa;
	}

	/**
	 * @return Retorna o valor de dataVeiculo atual.
	 */
	public Date getDataVeiculo() {
		return dataVeiculo;
	}

	/**
	 * @return Retorna o valor de velocidadeVeiculo atual.
	 */
	public Integer getVelocidadeVeiculo() {
		return velocidadeVeiculo;
	}

	/**
	 * @return Retorna o valor de velocidadeLimite atual.
	 */
	public Integer getVelocidadeLimite() {
		return velocidadeLimite;
	}

	/**
	 * @return Retorna o valor de segundosVeiculo atual.
	 */
	public Integer getSegundosVeiculo() {
		return segundosVeiculo;
	}

	/**
	 * @return Retorna o valor de segundoTolerancia atual.
	 */
	public Integer getSegundoTolerancia() {
		return segundoTolerancia;
	}

	/**
	 * @return Retorna o valor de idInconsistencia atual.
	 */
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}

	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return Retorna o valor de idImagemLocal atual.
	 */
	public Integer getIdImagemLocal() {
		return idImagemLocal;
	}

	/**
	 * @return Retorna o valor de infracaoProcesso atual.
	 */
	public Integer getInfracaoProcesso() {
		return infracaoProcesso;
	}
	
	public Boolean getEspera() {
		return espera;
	}

	public Integer getIdRemessa() {
		return idRemessa;
	}

	/**
	 * @return Retorna o valor de idImagemPAN atual.
	 */
	public Integer getIdImagemPAN() {
		return idImagemPAN;
	}

	/**
	 * @return Retorna o valor de idImagemOBJ atual.
	 */
	public Integer getIdImagemOBJ() {
		return idImagemOBJ;
	}

	public Integer getIdProcesso() {
		return idProcesso;
	}

	public String getNomeProcesso() {
		return nomeProcesso;
	}

	public Integer getIdUsuarioAtual() {
		return idUsuarioAtual;
	}

	public String getUsuarioAtual() {
		return usuarioAtual;
	}
	
	public Long getIdVeiculo() {
		return idVeiculo;
	}

	public void setIdVeiculo(Long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	/**
	 * Retorna o valor do campo 'equipamentoCapturaFrontal' atual.
	 * @return the equipamentoCapturaFrontal
	 */
	public Boolean getEquipamentoCapturaFrontal() {
		return this.equipamentoCapturaFrontal;
	}

	/**
	 * @return the areaPista
	 */
	public Integer getAreaPista() {
		return areaPista;
	}
	
	/**
	 * @return the classificacaoTarja
	 */
	public String getClassificacaoTarja() {
		return classificacaoTarja;
	}
	
	/**
	 * @return the comPesagem
	 */
	public Integer getComPesagem() {
		return comPesagem;
	}
	
}
