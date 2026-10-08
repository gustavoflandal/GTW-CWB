package com.consilux.model;

import java.util.Date;

public class InfracaoProcessoConcluido {

	private Integer id_infracao_processo_concluido;
	private Integer id_infracao;
	private Integer id_processo;
	private String nome;
	private Integer cod_agente;
	private Date data_processo;
	private Date data_conclusao;
	private Integer id_inconsistencia_processo;
	private Integer id_inconsistencia;
	private String inconsistencia;
	private Integer id_imagem;
	
	private Integer id_codigo_agente_digitado = 0;
	private String nome_agente_digitado = null;
	
	public InfracaoProcessoConcluido(Integer id_infracao_processo_concluido, Integer id_infracao, 
			Integer id_processo, String nome, Integer cod_agente, Date data_processo, Date data_conclusao, 
			Integer id_inconsistencia_processo, Integer id_inconsistencia, String inconsistencia,  Integer id_imagem,
			Integer id_codigo_agente_digitado, String nome_agente_digitado) {
		this.id_infracao_processo_concluido = id_infracao_processo_concluido;
		this.id_infracao = id_infracao;
		this.id_processo = id_processo;
		this.nome = nome;
		this.cod_agente = cod_agente;
		this.data_processo = data_processo;
		this.data_conclusao = data_conclusao;
		this.id_inconsistencia_processo = id_inconsistencia_processo;
		this.id_inconsistencia = id_inconsistencia;
		this.inconsistencia = inconsistencia;
		this.id_imagem = id_imagem;	
		
		this.id_codigo_agente_digitado = id_codigo_agente_digitado;
		this.nome_agente_digitado = nome_agente_digitado;
	}

	public Integer getId_infracao_processo_concluido() {
		return id_infracao_processo_concluido;
	}

	public Integer getId_infracao() {
		return id_infracao;
	}

	public Integer getId_processo() {
		return id_processo;
	}

	public String getNome() {
		if(nome_agente_digitado != null)
			return nome_agente_digitado;
		else
			return nome;
	}

	public Integer getCod_agente() {
		if(id_codigo_agente_digitado > 0)
			return id_codigo_agente_digitado;
		else
			return cod_agente;
	}

	public Date getData_processo() {
		return data_processo;
	}

	public Date getData_conclusao() {
		return data_conclusao;
	}

	public Integer getId_inconsistencia() {
		return id_inconsistencia;
	}

	public String getInconsistencia() {
		return inconsistencia;
	}

	public Integer getId_imagem() {
		return id_imagem;
	}
	
	public Boolean getValido() {
		return (this.id_inconsistencia == this.id_inconsistencia_processo);
	}
	
	public Integer getId_codigo_agente_digitado() {
		return id_codigo_agente_digitado;
	}

	public void setId_codigo_agente_digitado(Integer id_codigo_agente_digitado) {
		this.id_codigo_agente_digitado = id_codigo_agente_digitado;
	}

	public String getNome_agente_digitado() {
		return nome_agente_digitado;
	}

	public void setNome_agente_digitado(String nome_agente_digitado) {
		this.nome_agente_digitado = nome_agente_digitado;
	}

//	public static InfracaoProcessoConcluido buscarInfracaoProcessoConcluidoPorIdInfracao(Integer id_infracao, Integer id_processo) throws ConexaoException, SQLException {
//		StringBuilder sbSQL = new StringBuilder();
//		sbSQL.append("SELECT TOP (1) ");
//		sbSQL.append("ipc.id_infracao_processo_concluido, ipc.id_infracao, ipc.id_processo, su.nome, su.cod_agente,  ");
//		sbSQL.append("ip.data AS data_processo, ipc.data_conclusao, COALESCE(mi.id_inconsistencia, inf.id_inconsistencia) AS inconsistencia_processo, ip.id_inconsistencia, inc.descricao AS inconsistencia,");
//		sbSQL.append("ipc.id_imagem, ipud.codigo_agente AS codigo_agente_digitado, ipud.nome_agente AS nome_agente_digitado ");
//		sbSQL.append("FROM infracao inf (NOLOCK) ");
//		sbSQL.append("JOIN infracao_remessa ir (NOLOCK) ON inf.id_infracao = ir.id_infracao ");
//		sbSQL.append("JOIN remessa r (NOLOCK) ON r.id_remessa = ir.id_remessa ");
//		sbSQL.append("LEFT JOIN movimento_importacao mi (NOLOCK) ON r.codigo_externo = mi.id_movimento AND r.id_enquadramento = mi.id_enquadramento AND ir.sequencia = mi.sequencia ");
//		sbSQL.append("JOIN infracao_processo_concluido ipc (NOLOCK) ON inf.id_infracao = ipc.id_infracao   ");
//		sbSQL.append("JOIN infracao_processo ip (NOLOCK) ON ipc.id_infracao = ip.id_infracao AND ipc.id_processo = ip.id_processo   ");
//		sbSQL.append("JOIN inconsistencia inc (NOLOCK) ON ip.id_inconsistencia = inc.id_inconsistencia   ");
//		sbSQL.append("JOIN sis_usuario su (NOLOCK) ON ip.id_usuario = su.id_usuario   ");
//		sbSQL.append("LEFT JOIN infracao_processo_usuario_digitado ipud (NOLOCK) ON ipud.id_infracao_processo = ip.id_infracao_processo ");
//		sbSQL.append("WHERE ipc.id_infracao = ? AND ipc.id_processo = ? ");
//		sbSQL.append("ORDER BY data_processo DESC ");
//	
//		Connection conn = null;
//		InfracaoProcessoConcluido ipc = null;
//		try {
//			conn = Conexao.getConexao();
//			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
//			ps.setInt(1, id_infracao);
//			ps.setInt(2, id_processo);
//			ResultSet rs = ps.executeQuery();
//			if(rs.next()) {
//				ipc = new InfracaoProcessoConcluido(
//						rs.getInt("id_infracao_processo_concluido"), 
//						rs.getInt("id_infracao"), 
//						rs.getInt("id_processo"), 
//						rs.getString("nome"), 
//						rs.getInt("cod_agente"), 
//						rs.getDate("data_processo"), 
//						rs.getDate("data_conclusao"), 
//						rs.getInt("inconsistencia_processo"),
//						rs.getInt("id_inconsistencia"), 
//						rs.getString("inconsistencia"), 
//						rs.getInt("id_imagem"),
//						rs.getInt("codigo_agente_digitado"),
//						rs.getString("nome_agente_digitado")
//						);
//			}
//		} finally {
//			if(conn != null)
//				conn.close();
//		}
//		return ipc;
//	}
	
	/*
	 
	 id_infracao_processo_concluido	id_infracao	id_processo	nome	data_processo	data_conclusao	id_inconsistencia	inconsistencia	id_imagem
13031	12409	20	SISTEMA                                                     	2013-10-24 14:15:53.733	2013-10-24 14:15:53.737	0	Consistente                                                 	14337
	 
	 SELECT 
ipc.id_infracao_processo_concluido, ipc.id_infracao, ipc.id_processo, su.nome,
MAX(ip.data) AS data_processo, ipc.data_conclusao, ipc.id_inconsistencia, inc.descricao AS inconsistencia, 
ipc.id_imagem 
--ip.* 
FROM infracao_processo_concluido ipc (NOLOCK) 
JOIN infracao_processo ip (NOLOCK) ON ipc.id_infracao = ip.id_infracao AND ipc.id_processo = ip.id_processo 
JOIN inconsistencia inc (NOLOCK) ON ipc.id_inconsistencia = inc.id_inconsistencia 
JOIN sis_usuario su (NOLOCK) ON ip.id_usuario = su.id_usuario 
--WHERE ipc.id_infracao = ? AND ipc.id_processo = ? 
GROUP BY ipc.id_infracao_processo_concluido, ipc.id_infracao, ipc.id_processo, su.nome, 
ipc.data_conclusao, ipc.id_inconsistencia, inc.descricao, ipc.id_imagem 
	  
	 */
	
}
