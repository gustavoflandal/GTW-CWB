package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Video {
	private Integer idVideo;
	private byte video[];
	private Integer sequenciaVideo;
	private String caminho;

	private Video(Integer idVideo, byte[] video, Integer sequenciaVideo, String caminho) {
		super();
		this.idVideo = idVideo;
		this.video = video;
		this.sequenciaVideo = sequenciaVideo;
		this.caminho = caminho;
	}

	public static Video buscaVideoPorIdVeiculo(Long idVeiculo, Integer sequenciaVideo)
			throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT v.id_video, v.video, prox.indice_imagem, v.ds_caminho");
		sbSQL.append("	FROM video v WITH (NOLOCK)");
		sbSQL.append("  JOIN video_info vi (NOLOCK) ON v.id_video = vi.id_video ");
		sbSQL.append("  JOIN veiculo_video vv (NOLOCK) ON v.id_video = vv.id_video ");
		sbSQL.append("  JOIN fcn_ObterProximaSequencia(?) prox ON prox.id_veiculo = vv.id_veiculo ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		vv.id_veiculo = ? AND vi.id_tipo_video = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idVeiculo);
			ps.setLong(2, idVeiculo);
			ps.setInt(3, sequenciaVideo);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Video(rs.getInt("id_video"), rs.getBytes("video"), rs.getInt("indice_imagem"), rs.getString("ds_caminho"));
			} else
				return null;

		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public static Video buscaVideoPorId(Integer idVideo)
			throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_video, video, ds_caminho");
		sbSQL.append("	FROM video v WITH (NOLOCK)");
		sbSQL.append("	WHERE ");
		sbSQL.append("		v.id_video = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVideo);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Video(rs.getInt("id_video"), rs.getBytes("video"), 0, rs.getString("ds_caminho"));
			} else
				return null;

		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public Integer getIdVideo() {
		return idVideo;
	}

	public byte[] getVideo() {
		return video;
	}

	public Integer getSequenciaVideo() {
		return sequenciaVideo;
	}

	public String getCaminho() {
		return caminho;
	}

}
