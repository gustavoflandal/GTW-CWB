package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class IntegracaoSequenciaImagem {

	private static Logger logger = LogManager.getLogger(IntegracaoSequenciaImagem.class);
	
	private Integer SerieEquipamento;
	private Integer IdLocal, IdPista, IdImagemLocal;
	private Date dataHora;
	private String NomeIntegracaoGct, NomeIntegracao, NomeConsilux;
	private IntegracaoSequenciaImagem Anterior;
	private String URL;
	private Integer Tamanho = 0;
	private boolean Processado = false;
	private String MensagemErro;
	private boolean Verificado = false,ErroVerificacao = false;

	private static DateFormat fmt = new SimpleDateFormat("yyyyMMdd");

	public Integer getIncrementoAnterior() {

		if (Anterior != null)
			return IdImagemLocal - Anterior.IdImagemLocal;
		else
			return 0;

	}

	public Integer getSerieEquipamento() {
		return SerieEquipamento;
	}

	public void setSerieEquipamento(Integer serieEquipamento) {
		SerieEquipamento = serieEquipamento;
	}

	public Integer getIdLocal() {
		return IdLocal;
	}

	public void setIdLocal(Integer idLocal) {
		IdLocal = idLocal;
	}

	public Integer getIdPista() {
		return IdPista;
	}

	public void setIdPista(Integer idPista) {
		IdPista = idPista;
	}

	public Integer getIdImagemLocal() {
		return IdImagemLocal;
	}

	public void setIdImagemLocal(Integer idImagemLocal) {
		IdImagemLocal = idImagemLocal;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getNomeIntegracaoGct() {
		return NomeIntegracaoGct != null ? NomeIntegracaoGct.trim() : "Não Há";
	}

	public void setNomeIntegracaoGct(String nomeIntegracaoGct) {
		NomeIntegracaoGct = nomeIntegracaoGct;
	}

	public String getNomeIntegracao() {
		return NomeIntegracao != null ? NomeIntegracao.trim() : "Não Há";
	}

	public void setNomeIntegracao(String nomeIntegracao) {
		NomeIntegracao = nomeIntegracao;
	}

	public String getNomeConsilux() {
		return NomeConsilux != null ? NomeConsilux.trim() : "Não Há";
	}

	public void setNomeConsilux(String nomeConsilux) {
		NomeConsilux = nomeConsilux;
	}

	public IntegracaoSequenciaImagem getAnterior() {
		return Anterior;
	}

	public void setAnterior(IntegracaoSequenciaImagem anterior) {
		Anterior = anterior;
	}

	public String getEnderecoFtp() {
		return "/Integracao/ArquivoIntegracao?URL=/" + SerieEquipamento.toString() +"/"+ fmt.format(dataHora) + "/" + NomeIntegracaoGct;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public Integer getTamanho() {
		return Tamanho;
	}

	public boolean isProcessado() {
		return Processado;
	}

	public String getMensagemErro() {
		return MensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		MensagemErro = mensagemErro;
	}

	public void setProcessado(boolean processado) {
		Processado = processado;
	}

	public void setTamanho(Integer tamanho) {
		Tamanho = tamanho;
	}
	
	public boolean isArquivoRI() {
		return NomeIntegracao != null;
	}

	public static List<IntegracaoSequenciaImagem> ObterIntegracaoSequenciaImagens(int horas) {

		List<IntegracaoSequenciaImagem> ret = new ArrayList<IntegracaoSequenciaImagem>();

		IntegracaoSequenciaImagem isi_ant = null, isi_at = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT id_local,serie_equipamento,id_pista,id_imagem_local,data_imagem,nome_arquivo_gct, "
					+ " nome_arquivo_integracao,nome_arquivo_consilux,diretorio,tamanho_bytes,processado,mensagem FROM fcn_ObterIntegracaoSequenciaImagem(?) "
					+ " ORDER BY id_local, id_pista, CAST(data_imagem AS DATE), DATEPART(DAY, data_imagem), DATEPART(HOUR,data_imagem), id_imagem_local");
			ps.setInt(1, horas);
			rs = ps.executeQuery();
			while (rs.next()) {
				isi_ant = isi_at;

				isi_at = new IntegracaoSequenciaImagem();
				isi_at.setDataHora(rs.getTimestamp("data_imagem"));
				isi_at.setIdImagemLocal(rs.getInt("id_imagem_local"));
				isi_at.setIdLocal(rs.getInt("id_local"));
				isi_at.setSerieEquipamento(rs.getInt("serie_equipamento"));
				isi_at.setIdPista(rs.getInt("id_pista"));
				isi_at.setNomeConsilux(rs.getString("nome_arquivo_consilux"));
				isi_at.setNomeIntegracao(rs.getString("nome_arquivo_integracao"));
				isi_at.setNomeIntegracaoGct(rs.getString("nome_arquivo_gct"));
				isi_at.setURL(rs.getString("diretorio"));
				if (!rs.wasNull()) {
					isi_at.setTamanho(rs.getInt("tamanho_bytes"));
					isi_at.setURL(isi_at.getURL()+"/"+isi_at.getNomeIntegracaoGct());
				}

				if (isi_ant != null) {
					if (isi_ant.getIdLocal() == isi_at.getIdLocal() && isi_ant.getIdPista() == isi_at.getIdPista()
							&& isi_at.getIdImagemLocal() > 1)
						isi_at.setAnterior(isi_ant);
				}
				isi_at.setProcessado(rs.getBoolean("processado"));
				isi_at.setMensagemErro(rs.getString("mensagem"));

				ret.add(isi_at);
			}
		} catch (Exception e) {
			logger.error("ObterIntegracaoSequenciaImagens", e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();

			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static List<IntegracaoSequenciaImagem> ObterIntegracaoSequenciaImagensTeste(int horas) {

		List<IntegracaoSequenciaImagem> ret = new ArrayList<IntegracaoSequenciaImagem>();

		IntegracaoSequenciaImagem isi_ant = null, isi_at = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT id_local,serie_equipamento,id_pista,id_imagem_local,data_imagem,nome_arquivo_gct, "
					+ " nome_arquivo_integracao,nome_arquivo_consilux,diretorio,tamanho_bytes,processado,mensagem FROM fcn_ObterIntegracaoSequenciaImagemTeste(?) "
					+ " ORDER BY id_local, id_pista, CAST(data_imagem AS DATE), DATEPART(DAY, data_imagem), DATEPART(HOUR,data_imagem), id_imagem_local");
			ps.setInt(1, horas);
			rs = ps.executeQuery();
			while (rs.next()) {
				isi_ant = isi_at;

				isi_at = new IntegracaoSequenciaImagem();
				isi_at.setDataHora(rs.getTimestamp("data_imagem"));
				isi_at.setIdImagemLocal(rs.getInt("id_imagem_local"));
				isi_at.setIdLocal(rs.getInt("id_local"));
				isi_at.setSerieEquipamento(rs.getInt("serie_equipamento"));
				isi_at.setIdPista(rs.getInt("id_pista"));
				isi_at.setNomeConsilux(rs.getString("nome_arquivo_consilux"));
				isi_at.setNomeIntegracao(rs.getString("nome_arquivo_integracao"));
				isi_at.setNomeIntegracaoGct(rs.getString("nome_arquivo_gct"));
				isi_at.setURL(rs.getString("diretorio"));
				if (!rs.wasNull()) {
					isi_at.setTamanho(rs.getInt("tamanho_bytes"));
					isi_at.setURL(isi_at.getURL()+"/"+isi_at.getNomeIntegracaoGct());
				}

				if (isi_ant != null) {
					if (isi_ant.getIdLocal() == isi_at.getIdLocal() && isi_ant.getIdPista() == isi_at.getIdPista()
							&& isi_at.getIdImagemLocal() > 1)
						isi_at.setAnterior(isi_ant);
				}
				isi_at.setProcessado(rs.getBoolean("processado"));
				isi_at.setMensagemErro(rs.getString("mensagem"));

				ret.add(isi_at);
			}
		} catch (Exception e) {
			logger.error("ObterIntegracaoSequenciaImagensTeste", e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();

			} catch (Exception e) {
			}
		}

		return ret;
	}
	
	public static List<IntegracaoSequenciaImagem> ObterIntegracaoSequenciaImagensDnit(int dias) {

		List<IntegracaoSequenciaImagem> ret = new ArrayList<IntegracaoSequenciaImagem>();

		IntegracaoSequenciaImagem isi_ant = null, isi_at = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(
						"SELECT id_local	,serie_equipamento	,id_pista	,id_imagem_local	,data_imagem	,nome_arquivo_gct	,nome_arquivo_consilux, tamanho_bytes, verificado, erro_verificacao\r\n"
						+ "FROM fcn_ObterIntegracaoSequenciaImagemDnit(?) \r\n"
						+ "ORDER BY data, id_local, DATEPART(HOUR,data_imagem), id_imagem_local");
			ps.setInt(1, dias - 1);
			rs = ps.executeQuery();
			while (rs.next()) {
				isi_ant = isi_at;

				isi_at = new IntegracaoSequenciaImagem();
				isi_at.setDataHora(rs.getTimestamp("data_imagem"));
				isi_at.setIdImagemLocal(rs.getInt("id_imagem_local"));
				isi_at.setIdLocal(rs.getInt("id_local"));
				isi_at.setSerieEquipamento(rs.getInt("serie_equipamento"));
				isi_at.setIdPista(rs.getInt("id_pista"));
				isi_at.setNomeConsilux(rs.getString("nome_arquivo_consilux"));
				isi_at.setNomeIntegracaoGct(rs.getString("nome_arquivo_gct"));
				isi_at.setTamanho(rs.getInt("tamanho_bytes"));
				isi_at.setVerificado(rs.getBoolean("verificado"));
				isi_at.setErroVerificacao(rs.getBoolean("erro_verificacao"));

				if (isi_ant != null) {
					String s1 = fmt.format(isi_ant.dataHora);
					String s2 = fmt.format(isi_at.dataHora);
					
					if (isi_ant.getIdLocal() == isi_at.getIdLocal() && s1.equals(s2))
						isi_at.setAnterior(isi_ant);
				}

				ret.add(isi_at);
			}
		} catch (Exception e) {
			logger.error("ObterIntegracaoSequenciaImagensDnit", e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();

			} catch (Exception e) {
			}
		}

		return ret;
	}

	public boolean isVerificado() {
		return Verificado;
	}

	public void setVerificado(boolean verificado) {
		Verificado = verificado;
	}

	public boolean isErroVerificacao() {
		return ErroVerificacao;
	}

	public void setErroVerificacao(boolean erroVerificacao) {
		ErroVerificacao = erroVerificacao;
	}
}
