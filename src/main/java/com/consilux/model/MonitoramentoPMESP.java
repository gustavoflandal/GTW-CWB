package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.lib.Conexao;

public class MonitoramentoPMESP {

	private Integer idEventoConexao;
	private Integer idLocal;
	private String nome;
	private Date dataConexao;
	private Integer desconexoes;
	private Integer tempoOffline;
	private Date dataDesconexao;
	private String enderecoIp;
	private Integer movimentosRecebidos;
	private Integer movimentosTransmitidos;
	private Integer atraso;
	private Integer maximo;
	private Integer placaLida;
	private Integer perda;
	private Date dataUltimoMovimento;
	private Date dataAtualizado;

	public MonitoramentoPMESP(Integer idEventoConexao, Integer idLocal, String nome,
			Date dataConexao, Integer desconexoes, Integer tempoOffline,
			Date dataDesconexao, String enderecoIp,
			Integer movimentosRecebidos, Integer movimentosTransmitidos,
			Integer atraso, Integer maximo, Integer placaLida, Integer perda, Date dataUltimoMovimento, Date dataAtualizado) {

		this.idEventoConexao = idEventoConexao;
		this.idLocal = idLocal;
		this.nome = nome.trim();
		this.dataConexao = dataConexao;
		this.desconexoes = desconexoes;
		this.tempoOffline = tempoOffline;
		this.dataDesconexao = dataDesconexao;
		this.enderecoIp = enderecoIp;
		this.movimentosRecebidos = movimentosRecebidos;
		this.movimentosTransmitidos = movimentosTransmitidos;
		this.atraso = atraso;
		this.maximo = maximo;
		this.placaLida = placaLida;
		this.perda = perda;
		this.dataUltimoMovimento = dataUltimoMovimento;
		this.dataAtualizado = dataAtualizado;
	}

	public Integer getIdEventoConexao() {
		return idEventoConexao;
	}

	public void setIdEventoConexao(Integer idEventoConexao) {
		this.idEventoConexao = idEventoConexao;
	}

	public Integer getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataConexao() {
		return dataConexao;
	}

	public void setDataConexao(Date dataConexao) {
		this.dataConexao = dataConexao;
	}

	public Integer getDesconexoes() {
		return desconexoes;
	}

	public void setDesconexoes(Integer desconexoes) {
		this.desconexoes = desconexoes;
	}

	public Integer getTempoOffline() {
		return tempoOffline;
	}

	public void setTempoOffline(Integer tempoOffline) {
		this.tempoOffline = tempoOffline;
	}

	public Date getDataDesconexao() {
		return dataDesconexao;
	}

	public void setDataDesconexao(Date dataDesconexao) {
		this.dataDesconexao = dataDesconexao;
	}

	public String getEnderecoIp() {
		return enderecoIp;
	}

	public void setEnderecoIp(String enderecoIp) {
		this.enderecoIp = enderecoIp;
	}

	public Integer getMovimentosRecebidos() {
		return movimentosRecebidos;
	}

	public void setMovimentosRecebidos(Integer movimentosRecebidos) {
		this.movimentosRecebidos = movimentosRecebidos;
	}

	public Integer getMovimentosTransmitidos() {
		return movimentosTransmitidos;
	}

	public void setMovimentosTransmitidos(Integer movimentosTransmitidos) {
		this.movimentosTransmitidos = movimentosTransmitidos;
	}

	public Integer getAtraso() {
		return atraso;
	}

	public Integer getMaximo() {
		return maximo;
	}

	public void setMaximo(Integer maximo) {
		this.maximo = maximo;
	}

	public Integer getPlacaLida() {
		return placaLida;
	}

	public void setPlacaLida(Integer placaLida) {
		this.placaLida = placaLida;
	}

	public Integer getPerda() {
		return perda;
	}

	public void setPerda(Integer perda) {
		this.perda = perda;
	}

	public void setAtraso(Integer atraso) {
		this.atraso = atraso;
	}

	public Date getDataUltimoMovimento() {
		return dataUltimoMovimento;
	}

	public void setDataUltimoMovimento(Date dataUltimoMovimento) {
		this.dataUltimoMovimento = dataUltimoMovimento;
	}

	public Date getDataAtualizado() {
		return dataAtualizado;
	}

	public void setDataAtualizado(Date dataAtualizado) {
		this.dataAtualizado = dataAtualizado;
	}
	
	public String getEstado()
	{
		if (this.dataDesconexao != null)
			return "Offline";
		else
			return "Online";
	}
	
	public int getTempoOfflinePorc() {
		return (int) Math.floor((this.tempoOffline.doubleValue() / 1440.0) * 100.0);
	}
	
	public Integer getPerdaPorc() {
		return (int) Math.floor((this.perda.doubleValue() / this.placaLida.doubleValue()) * 100.0);// perda;
	}

	public static List<MonitoramentoPMESP> ObterMonitoramento() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<MonitoramentoPMESP> lista = new ArrayList<MonitoramentoPMESP>();

		try {
			conn = Conexao.getConexao();
			ps = conn
					.prepareStatement("SELECT * FROM v_pmesp_estado ORDER BY 1 DESC");
			rs = ps.executeQuery();
			while (rs.next())
				lista.add(new MonitoramentoPMESP(rs.getInt(1), rs.getInt(2), rs.getString(3), rs
						.getTimestamp(4), rs.getInt(5), rs.getInt(6), rs
						.getTimestamp(7), rs.getString(8), rs.getInt(9), rs
						.getInt(10), rs.getInt(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getTimestamp(15), rs
						.getTimestamp(16)));
		} catch (Exception e) {
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

		return lista;
	}
}
