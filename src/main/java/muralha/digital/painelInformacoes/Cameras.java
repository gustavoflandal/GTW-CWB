package muralha.digital.painelInformacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import java.io.File;

@XmlRootElement(name = "Cameras")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cameras {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(Cameras.class);

	@XmlElementWrapper(name = "Camera")
	@XmlElement(name = "Consulta")

	CameraResponse cameraResponse;

	public CameraResponse getCameraResponse() {
		return cameraResponse;
	}

	public Cameras() {
		super();
	}

	public static CameraResponse ObterCamerasPorDispositivoId(int id_dispositivo)
			throws ConexaoException, SQLException {
		CameraResponse cameras = new CameraResponse();

		String sql = "SELECT id_local, id_camera, ip_camera, tipo_camera, relevante "
				+ "FROM v_equipamento_cameras_todas WHERE id_local = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id_dispositivo);

			rs = ps.executeQuery();

			while (rs.next()) {
				Camera cam = new Camera();
				cam.setIdLocal(rs.getInt("id_local"));
				cam.setIdCamera(rs.getInt("id_camera"));
				cam.setIpCamera(rs.getString("ip_camera"));
				cam.setTipoCamera(rs.getString("tipo_camera"));
				cam.setRelevante(rs.getInt("relevante"));

				cameras.getCamera().add(cam);
			}

		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterCamerasPorNumeroSerie):: ", e);
		} finally {
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

		return cameras;
	}

	public static LeituraPlacasResponse ObterPercentualPlacas(List<Integer> idsCameras, Date dataInicio, Date dataFim)
			throws ConexaoException, SQLException {

		LeituraPlacasResponse response = new LeituraPlacasResponse();

		if (idsCameras == null || idsCameras.isEmpty() || dataInicio == null || dataFim == null) {
			throw new IllegalArgumentException("Câmeras e datas não podem ser nulas ou vazias.");
		}

		StringBuilder sql = new StringBuilder();
				sql.append("SELECT ").append("    id_local, ").append("    COUNT(placa) AS placas_lidas, ")
				.append("    COUNT(*) AS total_registros ")
				.append("FROM muralha.veiculo_tempo_real ").append("WHERE data BETWEEN ? AND ? ")
				.append("AND id_local IN (");

		for (int i = 0; i < idsCameras.size(); i++) {
			sql.append("?");
			if (i < idsCameras.size() - 1)
				sql.append(",");
		}
		sql.append(") GROUP BY id_local");

		class Contagem {
			int lidas = 0;
			int total = 0;
		}

		try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

			java.sql.Timestamp tsInicio = new java.sql.Timestamp(dataInicio.getTime());
			java.sql.Timestamp tsFim = new java.sql.Timestamp(dataFim.getTime());

			ps.setTimestamp(1, tsInicio);
			ps.setTimestamp(2, tsFim);

			for (int i = 0; i < idsCameras.size(); i++) {
				ps.setInt(3 + i, idsCameras.get(i));
			}

			try (ResultSet rs = ps.executeQuery()) {
				Map<Integer, Contagem> contagensPorCamera = new HashMap<>();

				while (rs.next()) {
					Contagem c = new Contagem();
					c.lidas = rs.getInt("placas_lidas");
					c.total = rs.getInt("total_registros");
					contagensPorCamera.put(rs.getInt("id_local"), c);
				}

				for (Integer idLocal : idsCameras) {
					LeituraPlaca leitura = new LeituraPlaca();
					leitura.setIdLocal(idLocal);

					Contagem contagem = contagensPorCamera.getOrDefault(idLocal, new Contagem());

					double percentual = 0.0;
					if (contagem.total > 0) {
						double valorCalculado = ((double) contagem.lidas / contagem.total) * 100.0;

						percentual = Math.round(valorCalculado * 100.0) / 100.0;
					}

					leitura.setPercentual(percentual);
					response.getLeituras().add(leitura);
				}
			}
		} catch (Exception e) {
			throw new SQLException("Erro ao consultar veiculo_tempo_real", e);
		}

		response.setSucesso(true);
		return response;
	}

	public static List<DispositivoSimples> ObterListaDispositivosContagensMisto(List<Integer> equipamentos)
	        throws ConexaoException, SQLException {

	    List<DispositivoSimples> listaRet = new ArrayList<>();
	    if (equipamentos == null || equipamentos.isEmpty()) {
	        return listaRet;
	    }

	    StringBuilder sbSQLDispositivos = new StringBuilder();
	    sbSQLDispositivos.append("SET NOCOUNT ON; ");
	    sbSQLDispositivos.append("DECLARE @equipamentos ListaEquipamentos; ");
	    for (Integer idLocal : equipamentos) {
	        sbSQLDispositivos.append(String.format("INSERT INTO @equipamentos VALUES (%d); ", idLocal));
	    }
	    sbSQLDispositivos.append("EXEC muralha.spuObterListaDispositivosContagensMisto @equipamentos;");

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sbSQLDispositivos.toString());
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("id_local");
	            String nome = rs.getString("nome");
	            boolean conectado = rs.getBoolean("conectado");
	            listaRet.add(new DispositivoSimples(id, conectado, nome, false, null));
	        }

	        if (!listaRet.isEmpty()) {
	        	String sqlMensagens = 
	        		    "SELECT lv.id_local, ca.data_hora " +
	        		    "FROM local_vigente lv WITH (NOLOCK) " +
	        		    "INNER JOIN eventos_csx_desc_proprietario ecdp WITH (NOLOCK) " +
	        		    "       ON ecdp.proprietario = lv.serie_equipamento " +
	        		    "       AND TRY_CAST(ecdp.proprietario AS INT) IS NOT NULL " +
	        		    "CROSS APPLY ( " +
	        		    "    SELECT TOP 1 ec.data_hora " +
	        		    "    FROM eventos_csx ec WITH (NOLOCK) " +
	        		    "    INNER JOIN eventos_csx_desc_proprietario ecdp_inner WITH (NOLOCK) " +
	        		    "           ON ecdp_inner.id_proprietario = ec.id_proprietario " +
	        		    "           AND ecdp_inner.proprietario = ecdp.proprietario " +
	        		    "    ORDER BY ec.data_hora DESC " +
	        		    ") ca " +
	        		    "WHERE lv.desativado = 0;";

	            try (PreparedStatement psMsg = conn.prepareStatement(sqlMensagens);
	                 ResultSet rsMsg = psMsg.executeQuery()) {

	                Map<Integer, Timestamp> ultimasMensagens = new HashMap<>();
	                while (rsMsg.next()) {
	                    ultimasMensagens.put(rsMsg.getInt("id_local"), rsMsg.getTimestamp("data_hora"));
	                }

	                for (DispositivoSimples ds : listaRet) {
	                    if (ultimasMensagens.containsKey(ds.getIdDispositivo())) {
	                        ds.setSeComunicou(true);
	                        ds.setUltimaDataRegistrada(ultimasMensagens.get(ds.getIdDispositivo()));
	                    } else {
	                        ds.setSeComunicou(false);
	                        ds.setUltimaDataRegistrada(null);
	                    }
	                }
	            }
	        }

	    } catch (SQLException e) {
	        throw new ConexaoException("Erro ao consultar dispositivos: " + e.getMessage(), e);
	    }

	    return listaRet;
	}

	public static List<Camera> ObterCamerasPorNumeroSerie(int id_dispositivo) throws ConexaoException, SQLException {
		List<Camera> cameras = new ArrayList<>();

		String sql = "SELECT id_local, id_camera, ip_camera, tipo_camera, relevante "
				+ "FROM v_equipamento_cameras_todas WHERE id_local = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id_dispositivo);

			rs = ps.executeQuery();

			while (rs.next()) {
				Camera cam = new Camera();
				cam.setIdLocal(rs.getInt("id_local"));
				cam.setIdCamera(rs.getInt("id_camera"));
				cam.setIpCamera(rs.getString("ip_camera"));
				cam.setTipoCamera(rs.getString("tipo_camera"));
				cam.setRelevante(rs.getInt("relevante"));

				cameras.add(cam);
			}

		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterCamerasPorNumeroSerie):: ", e);
		} finally {
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
		return cameras;
	}

	public static RecursoServidorResponse ObterRecursoServidor() throws ConexaoException, SQLException {
		RecursoServidorResponse response = new RecursoServidorResponse();

		String sql = "SELECT " +
			    "  (SELECT COUNT_BIG(*) " +
			    "     FROM muralha.veiculo_tempo_real_imagem WITH (NOLOCK)) AS total_imagens, " +
			    "  (SELECT COUNT_BIG(*) " +
			    "     FROM (SELECT CAST(data AS DATE) AS data " +
			    "             FROM muralha.veiculo_tempo_real WITH (NOLOCK) " +
			    "            GROUP BY CAST(data AS DATE)) AS sub) AS dias_armazenados, " +
			    "  (SELECT COUNT_BIG(*) " +
			    "     FROM muralha.veiculo_tempo_real WITH (NOLOCK)) AS total_passagens";

		try (Connection conn = Conexao.getConexao();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				long totalImagens = rs.getLong("total_imagens");
				long diasArmazenados = rs.getLong("dias_armazenados");
				long totalPassagens = rs.getLong("total_passagens");

				// Lista mutável para indicadores
				List<RecursoServidorDTO> indicadores = new ArrayList<>();
				indicadores.add(new RecursoServidorDTO("Quantidade de Imagens", totalImagens, "registros", null));
				indicadores.add(new RecursoServidorDTO("Dias Armazenados", diasArmazenados, "dias", null));
				indicadores.add(
						new RecursoServidorDTO("Passagens Veiculares Registradas", totalPassagens, "registros", null));

				// Capacidade de armazenamento do disco
				File root = new File(System.getProperty("os.name").toLowerCase().contains("win") ? "C:\\" : "/");
				long totalGB = root.getTotalSpace() / (1024 * 1024 * 1024);
				double percentualDisco = obterPercentualDisco();
				indicadores.add(new RecursoServidorDTO("Capacidade de Armazenamento", totalGB, "GB", percentualDisco));

				response.setIndicadores(indicadores);
				response.setSucesso(true);
			}

		} catch (SQLException e) {
			throw new ConexaoException("Erro ao montar SQL (ObterResumoVeiculoTempoReal):: ", e);
		}

		return response;
	}

	public static double obterPercentualDisco() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String path;

			if (os.contains("win")) {
				path = "C:\\"; // Windows: ajuste se os dados estiverem em outro drive
			} else {
				path = "/"; // Linux/macOS: ajuste para o volume correto
			}

			File root = new File(path);
			long totalBytes = root.getTotalSpace();
			long usableBytes = root.getUsableSpace();

			if (totalBytes <= 0)
				return -1; // erro ao obter info do volume

			double usedGB = (totalBytes - usableBytes) / (1024.0 * 1024.0 * 1024.0);
			double totalGB = totalBytes / (1024.0 * 1024.0 * 1024.0);
			double percentUsed = (usedGB / totalGB) * 100.0;

			// arredonda para 1 casa decimal
			return Math.round(percentUsed * 10.0) / 10.0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
