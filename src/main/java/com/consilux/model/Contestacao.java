package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Contestacao {

	 private Integer idInfracao;
	 private Date data;
	 private Integer idLocal;
	 private String descricaoLocal;
	 private Integer codPistaProdam;
	 private Integer pista;
	 private Integer idEnquadramento;
	 private String descricaoEnquadramento;
	 private Date dataAnaliseCai;
	 private String placaCai;
	 private Integer inconsistenciaCai;
	 private String inconsistenciaCaiDesc;
	 private Date dataAnaliseCav;
	 private String placaCav;
	 private Integer inconsistenciaCav;
	 private String inconsistenciaCavDesc;
	 private Integer marcaCai;
	 private String marcaCaiDesc;
	 private Integer marcaCav; 
	 private String marcaCavDesc;
	 private Integer especieCai;
	 private String especieCaiDesc;
	 private Integer especieCav; 
	 private String especieCavDesc; 
	 private Boolean erroPlaca	;
	 private Boolean erroConsistencia;
	 private Integer idUsuarioCai; 
	 private String nomeUsuarioCai;
	 private Integer idUsuarioCav; 
	 private String nomeUsuarioCav;
	 private Integer idProcessoContestacao;
	 private Integer decisao;
	 
	 public Contestacao(Integer idInfracao, Date data, Integer idLocal, String descricaoLocal, Integer codPistaProdam, Integer pista, Integer idEnquadramento, String descricaoEnquadramento, Date dataAnaliseCai, String placaCai, Integer inconsistenciaCai, String inconsistenciaCaiDesc, Date dataAnaliseCav, String placaCav, Integer inconsistenciaCav, String inconsistenciaCavDesc, Integer marcaCai, String marcaCaiDesc, Integer marcaCav, String marcaCavDesc, Integer especieCai, String especieCaiDesc, Integer especieCav, String especieCavDesc, Boolean erroPlaca	, Boolean erroConsistencia, Integer idUsuarioCai, String nomeUsuarioCai, Integer idUsuarioCav, String nomeUsuarioCav, Integer idProcessoContestacao, Integer decisao) {
		 this.idInfracao = idInfracao;
		 this.data = data;
		 this.idLocal = idLocal;
		 this.descricaoLocal = descricaoLocal;
		 this.codPistaProdam = codPistaProdam;
		 this.pista = pista;
		 this.idEnquadramento = idEnquadramento;
		 this.descricaoEnquadramento = descricaoEnquadramento;
		 this.dataAnaliseCai = dataAnaliseCai;
		 this.placaCai = placaCai;
		 this.inconsistenciaCai = inconsistenciaCai;
		 this.inconsistenciaCaiDesc = inconsistenciaCaiDesc;
		 this.dataAnaliseCav = dataAnaliseCav;
		 this.placaCav = placaCav;
		 this.inconsistenciaCav = inconsistenciaCav;
		 this.inconsistenciaCavDesc = inconsistenciaCavDesc;
		 this.marcaCai = marcaCai;
		 this.marcaCaiDesc = marcaCaiDesc;
		 this.marcaCav = marcaCav; 
		 this.marcaCavDesc = marcaCavDesc;
		 this.especieCai = especieCai;
		 this.especieCaiDesc = especieCaiDesc;
		 this.especieCav = especieCav; 
		 this.especieCavDesc = especieCavDesc; 
		 this.erroPlaca = erroPlaca	;
		 this.erroConsistencia = erroConsistencia;
		 this.idUsuarioCai = idUsuarioCai; 
		 this.nomeUsuarioCai = nomeUsuarioCai;
		 this.idUsuarioCav = idUsuarioCav; 
		 this.nomeUsuarioCav = nomeUsuarioCav;
		 this.idProcessoContestacao = idProcessoContestacao;
		 this.decisao = decisao;
	 }

	public static Contestacao obterContestacao(Integer idInfracao) throws SQLException, ConexaoException {
		 
		 Contestacao c = null;
		 
		 Connection conn = null;
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 
		 StringBuilder sbSQL = new StringBuilder();
		 sbSQL.append("SELECT id_infracao	,data	,id_local	,descricao_local	,cod_pista_prodam	,pista	,id_enquadramento	, ");
		 sbSQL.append("descricao_enquadramento	,data_analise_cai	,placa_cai	,inconsistencia_cai, icai.descricao AS inconsistencia_cai_desc	, ");
		 sbSQL.append("data_analise_cav	,placa_cav	,inconsistencia_cav, icav.descricao AS inconsistencia_cav_desc	,marca_cai, cm_cai.descricao AS marca_cai_desc,	marca_cav,  ");
		 sbSQL.append("cm_cav.descricao AS marca_cav_desc, ");
		 sbSQL.append("especie_cai	, ce_cai.descricao AS especie_cai_desc, especie_cav, ce_cav.descricao AS especie_cav_desc, erro_placa	, ");
		 sbSQL.append("erro_consistencia	,id_usuario_cai, ucai.nome AS nome_usuario_cai	,id_usuario_cav, ucav.nome AS nome_usuario_cav	,id_processo_contestacao	,decisao ");
		 sbSQL.append("FROM infracao_contestacao ic (NOLOCK) ");
		 sbSQL.append("JOIN inconsistencia icai (NOLOCK) ON ic.inconsistencia_cai = icai.id_inconsistencia ");
		 sbSQL.append("JOIN inconsistencia icav (NOLOCK) ON ic.inconsistencia_cav = icav.id_inconsistencia ");
		 sbSQL.append("LEFT JOIN cad_marca_cet cm_cai (NOLOCK) ON ic.marca_cai = cm_cai.id_marca_cet ");
		 sbSQL.append("LEFT JOIN cad_marca_cet cm_cav (NOLOCK) ON ic.marca_cav = cm_cav.id_marca_cet ");
		 sbSQL.append("LEFT JOIN cad_especie ce_cai (NOLOCK) ON ic.especie_cai = ce_cai.id_especie ");
		 sbSQL.append("LEFT JOIN cad_especie ce_cav (NOLOCK) ON ic.especie_cav = ce_cav.id_especie ");
		 sbSQL.append("JOIN sis_usuario ucai (NOLOCK) ON ic.id_usuario_cai = ucai.id_usuario ");
		 sbSQL.append("JOIN sis_usuario ucav (NOLOCK) ON ic.id_usuario_cav = ucav.id_usuario ");
		 sbSQL.append("WHERE ic.id_infracao = ? ");
		 
		 try 
		 {
			 conn = Conexao.getConexao();
			 ps = conn.prepareStatement(sbSQL.toString());
			 ps.setInt(1, idInfracao);
			 rs = ps.executeQuery();
			 if (rs.next())
			 {
				 c = new Contestacao( rs.getInt(1), rs.getTimestamp(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), 
						 rs.getInt(7), rs.getString(8), rs.getDate(9), rs.getString(10), rs.getInt(11), rs.getString(12), rs.getDate(13),
						 rs.getString(14), rs.getInt(15), rs.getString(16), rs.getInt(17), rs.getString(18), rs.getInt(19), rs.getString(20), rs.getInt(21),
						 rs.getString(22),rs.getInt(23),rs.getString(24), rs.getBoolean(25), rs.getBoolean(26), rs.getInt(27), rs.getString(28),
						 rs.getInt(29), rs.getString(30), rs.getInt(31), rs.getInt(32));
			 }
		 }
		 finally
		 {
			 try 
			 {
			 if (conn != null)
				 conn.close();
			 if (ps != null)
				 ps.close();
			 if (rs != null)
				 rs.close();
			 }
			 catch(Exception e)
			 {}
		 }
		 
		 return c;
		 
	 }
	
	public static int ContarInfracoes(int processo) throws ConexaoException, SQLException {
		
		int ret = 0;
		
		Connection conn = null;
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		
		 StringBuilder sbSQL = new StringBuilder();
		 
		 sbSQL.append("SELECT COUNT(*) cnt FROM infracao_contestacao (NOLOCK) "); 
		 sbSQL.append("WHERE id_processo_contestacao = ? ");
		 sbSQL.append("GROUP BY id_processo_contestacao  ");
		 
		try 
		 {
			 conn = Conexao.getConexao();
			 ps = conn.prepareStatement(sbSQL.toString());
			 
			 if (processo == 90)
				 ps.setInt(1, 1);
			 else
				 ps.setInt(1, 2);
			 
			 rs = ps.executeQuery();
			 if (rs.next())
			 {
				 ret = rs.getInt(1);
			 }
		 }
		 finally
		 {
			 try 
			 {
			 if (conn != null)
				 conn.close();
			 if (ps != null)
				 ps.close();
			 if (rs != null)
				 rs.close();
			 }
			 catch(Exception e)
			 {}
		 }
		
		return ret;
	}
	 
	public Integer getIdInfracao() {
		return idInfracao;
	}
	public void setIdInfracao(Integer idInfracao) {
		this.idInfracao = idInfracao;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}
	public String getDescricaoLocal() {
		return descricaoLocal;
	}
	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}
	public Integer getCodPistaProdam() {
		return codPistaProdam;
	}
	public void setCodPistaProdam(Integer codPistaProdam) {
		this.codPistaProdam = codPistaProdam;
	}
	public Integer getPista() {
		return pista;
	}
	public void setPista(Integer pista) {
		this.pista = pista;
	}
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}
	public void setIdEnquadramento(Integer idEnquadramento) {
		this.idEnquadramento = idEnquadramento;
	}
	public String getDescricaoEnquadramento() {
		return descricaoEnquadramento;
	}
	public void setDescricaoEnquadramento(String descricaoEnquadramento) {
		this.descricaoEnquadramento = descricaoEnquadramento;
	}
	public Date getDataAnaliseCai() {
		return dataAnaliseCai;
	}
	public void setDataAnaliseCai(Date dataAnaliseCai) {
		this.dataAnaliseCai = dataAnaliseCai;
	}
	public String getPlacaCai() {
		return placaCai;
	}
	public void setPlacaCai(String placaCai) {
		this.placaCai = placaCai;
	}
	public Integer getInconsistenciaCai() {
		return inconsistenciaCai;
	}
	public void setInconsistenciaCai(Integer inconsistenciaCai) {
		this.inconsistenciaCai = inconsistenciaCai;
	}
	public String getInconsistenciaCaiDesc() {
		return inconsistenciaCaiDesc;
	}
	public void setInconsistenciaCaiDesc(String inconsistenciaCaiDesc) {
		this.inconsistenciaCaiDesc = inconsistenciaCaiDesc;
	}
	public Date getDataAnaliseCav() {
		return dataAnaliseCav;
	}
	public void setDataAnaliseCav(Date dataAnaliseCav) {
		this.dataAnaliseCav = dataAnaliseCav;
	}
	public String getPlacaCav() {
		return placaCav;
	}
	public void setPlacaCav(String placaCav) {
		this.placaCav = placaCav;
	}
	public Integer getInconsistenciaCav() {
		return inconsistenciaCav;
	}
	public void setInconsistenciaCav(Integer inconsistenciaCav) {
		this.inconsistenciaCav = inconsistenciaCav;
	}
	public String getInconsistenciaCavDesc() {
		return inconsistenciaCavDesc;
	}
	public void setInconsistenciaCavDesc(String inconsistenciaCavDesc) {
		this.inconsistenciaCavDesc = inconsistenciaCavDesc;
	}
	public Integer getMarcaCai() {
		return marcaCai;
	}
	public void setMarcaCai(Integer marcaCai) {
		this.marcaCai = marcaCai;
	}
	public String getMarcaCaiDesc() {
		return marcaCaiDesc;
	}
	public void setMarcaCaiDesc(String marcaCaiDesc) {
		this.marcaCaiDesc = marcaCaiDesc;
	}
	public Integer getMarcaCav() {
		return marcaCav;
	}
	public void setMarcaCav(Integer marcaCav) {
		this.marcaCav = marcaCav;
	}
	public String getMarcaCavDesc() {
		return marcaCavDesc;
	}
	public void setMarcaCavDesc(String marcaCavDesc) {
		this.marcaCavDesc = marcaCavDesc;
	}
	public Integer getEspecieCai() {
		return especieCai;
	}
	public void setEspecieCai(Integer especieCai) {
		this.especieCai = especieCai;
	}
	public String getEspecieCaiDesc() {
		return especieCaiDesc;
	}
	public void setEspecieCaiDesc(String especieCaiDesc) {
		this.especieCaiDesc = especieCaiDesc;
	}
	public Integer getEspecieCav() {
		return especieCav;
	}
	public void setEspecieCav(Integer especieCav) {
		this.especieCav = especieCav;
	}
	public String getEspecieCavDesc() {
		return especieCavDesc;
	}
	public void setEspecieCavDesc(String especieCavDesc) {
		this.especieCavDesc = especieCavDesc;
	}
	public Boolean getErroPlaca() {
		return erroPlaca;
	}
	public void setErroPlaca(Boolean erroPlaca) {
		this.erroPlaca = erroPlaca;
	}
	public Boolean getErroConsistencia() {
		return erroConsistencia;
	}
	public void setErroConsistencia(Boolean erroConsistencia) {
		this.erroConsistencia = erroConsistencia;
	}
	public Integer getIdUsuarioCai() {
		return idUsuarioCai;
	}
	public void setIdUsuarioCai(Integer idUsuarioCai) {
		this.idUsuarioCai = idUsuarioCai;
	}
	public String getNomeUsuarioCai() {
		return nomeUsuarioCai;
	}
	public void setNomeUsuarioCai(String nomeUsuarioCai) {
		this.nomeUsuarioCai = nomeUsuarioCai;
	}
	public Integer getIdUsuarioCav() {
		return idUsuarioCav;
	}
	public void setIdUsuarioCav(Integer idUsuarioCav) {
		this.idUsuarioCav = idUsuarioCav;
	}
	public String getNomeUsuarioCav() {
		return nomeUsuarioCav;
	}
	public void setNomeUsuarioCav(String nomeUsuarioCav) {
		this.nomeUsuarioCav = nomeUsuarioCav;
	}
	public Integer getIdProcessoContestacao() {
		return idProcessoContestacao;
	}
	public void setIdProcessoContestacao(Integer idProcessoContestacao) {
		this.idProcessoContestacao = idProcessoContestacao;
	}
	public Integer getDecisao() {
		return decisao;
	}
	public void setDecisao(Integer decisao) {
		this.decisao = decisao;
	}
	
	 
	 
}
