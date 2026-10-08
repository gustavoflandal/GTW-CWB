package muralha.digital.boletim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

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

import muralha.digital.util.Paginacao;

@XmlRootElement(name = "Boletins")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boletins {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(Boletins.class);

	@XmlElementWrapper(name = "ListaBoletins")
	@XmlElement(name = "Boletim")
	private List<Boletim> listaBoletins;

	private Paginacao paginacao;

	public List<Boletim> getListaBoletins() {
		return listaBoletins;
	}

	public void setListaBoletins(List<Boletim> listaBoletins) {
		this.listaBoletins = listaBoletins;
	}

	public Paginacao getPaginacao() {
		return paginacao;
	}

	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}

	public Boletins() {
		super();
	}

	public static Boletins ObterListaBoletins(Date dataIni, Date dataFim, Integer idSituacao, Integer idTipoOcorrencia,
			String placa, String cpf, Integer idCidade, Paginacao paginacao) throws ConexaoException, SQLException {

		Boletins retorno = new Boletins();
		Map<Integer, Boletim> boletinsMap = new LinkedHashMap<>();

		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		boolean erro = false;
		String msgErro = "";

		try {
			// Monta a query
			sbSQL.append(" SELECT b.id, ");
			sbSQL.append("        b.id_situacao, ");
			sbSQL.append("        bs.descricao AS situacao, ");
			sbSQL.append("        b.id_tipo, ");
			sbSQL.append("        ot.descricao AS tipo_ocorrencia, ");
			sbSQL.append("        b.detalhamento, ");
			sbSQL.append("        b.data_criacao, ");
			sbSQL.append("        b.data_encerramento, ");
			sbSQL.append("        b.id_usuario, ");
			sbSQL.append("        su.usuario AS usuario_criacao, ");
			sbSQL.append("        su.nome AS nome_usuario_criacao, ");
			sbSQL.append("        bl.id AS local_id, ");
			sbSQL.append("        bl.id_cidade, ");
			sbSQL.append("        bl.bairro, ");
			sbSQL.append("        bl.rua, ");
			sbSQL.append("        bl.numero, ");
			sbSQL.append("        bl.complemento, ");
			sbSQL.append("        bi.id AS individuo_id, ");
			sbSQL.append("        bi.nome AS individuo_nome, ");
			sbSQL.append("        bi.cpf AS individuo_cpf, ");
			sbSQL.append("        bv.id AS veiculo_id, ");
			sbSQL.append("        bv.placa AS veiculo_placa, ");
			sbSQL.append("        bv.marca AS veiculo_marca, ");
			sbSQL.append("        c.id AS cidade_id, ");
			sbSQL.append("        c.nome AS cidade_nome, ");
			sbSQL.append("        c.id_estado, ");
			sbSQL.append(paginacao.QueryTotalRegistros());
			sbSQL.append(" FROM   muralha.boletim b ");
			sbSQL.append("        LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao ");
			sbSQL.append("        LEFT JOIN muralha.ocorrencia_tipo ot ON ot.id = b.id_tipo ");
			sbSQL.append("        LEFT JOIN sis_usuario su ON su.id_usuario = b.id_usuario ");
			sbSQL.append("        LEFT JOIN muralha.boletim_local bl ON bl.id = b.id_local ");
			sbSQL.append("        LEFT JOIN muralha.cidade c ON c.id = bl.id_cidade ");
			sbSQL.append("        LEFT JOIN muralha.boletim_individuo bi ON bi.id_boletim = b.id ");
			sbSQL.append("        LEFT JOIN muralha.boletim_veiculo bv ON bv.id_boletim = b.id ");
			sbSQL.append(" WHERE  1=1 ");

			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<>();

			if (dataIni != null) {
				sbSQL.append(" AND b.data_criacao >= ? ");
				mapaParametros.put(paramIndex++, new java.sql.Date(dataIni.getTime()));
			}
			if (dataFim != null) {
				sbSQL.append(" AND b.data_criacao <= ? ");
				mapaParametros.put(paramIndex++, new java.sql.Date(dataFim.getTime()));
			}
			if (idSituacao != null) {
				sbSQL.append(" AND b.id_situacao = ? ");
				mapaParametros.put(paramIndex++, idSituacao);
			}
			if (idTipoOcorrencia != null) {
				sbSQL.append(" AND b.id_tipo = ? ");
				mapaParametros.put(paramIndex++, idTipoOcorrencia);
			}
			if (idCidade != null) {
				sbSQL.append(" AND bl.id_cidade = ? ");
				mapaParametros.put(paramIndex++, idCidade);
			}
			if (cpf != null && !cpf.isEmpty()) {
				sbSQL.append(" AND EXISTS ( ");
				sbSQL.append("     SELECT 1 FROM muralha.boletim_individuo bi2 ");
				sbSQL.append("     WHERE bi2.id_boletim = b.id AND bi2.cpf = ? ");
				sbSQL.append(" ) ");
				mapaParametros.put(paramIndex++, cpf);
			}
			if (placa != null && !placa.isEmpty()) {
				sbSQL.append(" AND EXISTS ( ");
				sbSQL.append("     SELECT 1 FROM muralha.boletim_veiculo bv2 ");
				sbSQL.append("     WHERE bv2.id_boletim = b.id AND bv2.placa = ? ");
				sbSQL.append(" ) ");
				mapaParametros.put(paramIndex++, placa);
			}

			sbSQL.append(" ORDER BY b.data_criacao DESC ");
			sbSQL.append(paginacao.QueryPaginacao());

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			for (Map.Entry<Integer, Object> entry : mapaParametros.entrySet()) {
				Object value = entry.getValue();
				int index = entry.getKey();

				if (value instanceof String) {
					ps.setString(index, (String) value);
				} else if (value instanceof Integer) {
					ps.setInt(index, (Integer) value);
				} else if (value instanceof java.sql.Date) {
					ps.setDate(index, (java.sql.Date) value);
				} else {
					erro = true;
					msgErro = "Erro ao preparar parâmetros de filtro.";
					break;
				}
			}

			if (!erro) {
				rs = ps.executeQuery();

				while (rs.next()) {
			        if (paginacao.TotalRegistros() == 0) {
			            paginacao.TotalRegistros(rs.getInt("total_registros"));
			        }
					int boletimId = rs.getInt("id");
					Boletim item = boletinsMap.get(boletimId);

					if (item == null) {
						item = new Boletim();
						item.setId(boletimId);
						item.setIdSituacao(rs.getInt("id_situacao"));
						item.setSituacao(rs.getString("situacao"));
						item.setIdTipo(rs.getInt("id_tipo"));
						item.setTipo(rs.getString("tipo_ocorrencia"));
						item.setDetalhamento(rs.getString("detalhamento"));
						item.setDataCriacao(rs.getTimestamp("data_criacao"));
						item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
						item.setIdUsuario(rs.getInt("id_usuario"));
						item.setUsuario(rs.getString("usuario_criacao"));
						item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
						item.setIndividuos(new ArrayList<>());
						item.setVeiculos(new ArrayList<>());

						// Local
						if (rs.getObject("local_id") != null) {
							BoletimLocal local = new BoletimLocal();
							local.setId(rs.getInt("local_id"));
							local.setBairro(rs.getString("bairro"));
							local.setRua(rs.getString("rua"));
							local.setNumero(rs.getInt("numero"));
							local.setComplemento(rs.getString("complemento"));

							if (rs.getObject("id_cidade") != null) {
								Cidade cidade = new Cidade();
								cidade.setId(rs.getInt("id_cidade"));
								cidade.setNome(rs.getString("cidade_nome"));
								cidade.setIdEstado(rs.getInt("id_estado"));
								local.setCidade(cidade);
							}

							item.setLocal(local);
						}

						boletinsMap.put(boletimId, item);
					}

					// Adiciona individuo
					if (rs.getObject("individuo_id") != null) {
						Integer individuoId = rs.getInt("individuo_id");
						if (item.getIndividuos().stream().noneMatch(i -> i.getId().equals(individuoId))) {
							BoletimIndividuo individuo = new BoletimIndividuo();
							individuo.setId(individuoId);
							individuo.setNome(rs.getString("individuo_nome"));
							individuo.setCpf(rs.getString("individuo_cpf"));
							item.getIndividuos().add(individuo);
						}
					}

					// Adiciona veiculo
					if (rs.getObject("veiculo_id") != null) {
						Integer veiculoId = rs.getInt("veiculo_id");
						if (item.getVeiculos().stream().noneMatch(v -> v.getId().equals(veiculoId))) {
							BoletimVeiculo veiculo = new BoletimVeiculo();
							veiculo.setId(veiculoId);
							veiculo.setPlaca(rs.getString("veiculo_placa"));
							veiculo.setMarca(rs.getString("veiculo_marca"));
							item.getVeiculos().add(veiculo);
						}
					}
				}

				// Retorna lista final
				retorno.setListaBoletins(new ArrayList<>(boletinsMap.values()));
				retorno.setPaginacao(paginacao); // totalRegistros já populado pela query
			}

		} catch (Exception e) {
			erro = true;
			msgErro = "Erro ao obter boletins!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
			}

			if (erro)
				throw new SQLException("Erro ao consultar boletins!");
		}

		return retorno;
	}
	
	public static Boletins ObterBoletinsPorIds(List<Integer> ids) throws ConexaoException, SQLException {
	    Boletins retorno = new Boletins();
	    Map<Integer, Boletim> boletinsMap = new LinkedHashMap<>();

	    if (ids == null || ids.isEmpty()) {
	        return retorno; // Retorna vazio se lista nula ou vazia
	    }

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";

	    try {
	        // Monta a query
	        sbSQL.append(" SELECT b.id, ");
	        sbSQL.append("        b.id_situacao, ");
	        sbSQL.append("        bs.descricao AS situacao, ");
	        sbSQL.append("        b.id_tipo, ");
	        sbSQL.append("        ot.descricao AS tipo_ocorrencia, ");
	        sbSQL.append("        b.detalhamento, ");
	        sbSQL.append("        b.data_criacao, ");
	        sbSQL.append("        b.data_encerramento, ");
	        sbSQL.append("        b.id_usuario, ");
	        sbSQL.append("        su.usuario AS usuario_criacao, ");
	        sbSQL.append("        su.nome AS nome_usuario_criacao, ");
	        sbSQL.append("        bl.id AS local_id, ");
	        sbSQL.append("        bl.id_cidade, ");
	        sbSQL.append("        bl.bairro, ");
	        sbSQL.append("        bl.rua, ");
	        sbSQL.append("        bl.numero, ");
	        sbSQL.append("        bl.complemento, ");
	        sbSQL.append("        bi.id AS individuo_id, ");
	        sbSQL.append("        bi.nome AS individuo_nome, ");
	        sbSQL.append("        bi.cpf AS individuo_cpf, ");
	        sbSQL.append("        bv.id AS veiculo_id, ");
	        sbSQL.append("        bv.placa AS veiculo_placa, ");
	        sbSQL.append("        bv.marca AS veiculo_marca, ");
	        sbSQL.append("        c.id AS cidade_id, ");
	        sbSQL.append("        c.nome AS cidade_nome, ");
	        sbSQL.append("        c.id_estado ");
	        sbSQL.append(" FROM   muralha.boletim b ");
	        sbSQL.append("        LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao ");
	        sbSQL.append("        LEFT JOIN muralha.ocorrencia_tipo ot ON ot.id = b.id_tipo ");
	        sbSQL.append("        LEFT JOIN sis_usuario su ON su.id_usuario = b.id_usuario ");
	        sbSQL.append("        LEFT JOIN muralha.boletim_local bl ON bl.id = b.id_local ");
	        sbSQL.append("        LEFT JOIN muralha.cidade c ON c.id = bl.id_cidade ");
	        sbSQL.append("        LEFT JOIN muralha.boletim_individuo bi ON bi.id_boletim = b.id ");
	        sbSQL.append("        LEFT JOIN muralha.boletim_veiculo bv ON bv.id_boletim = b.id ");
	        sbSQL.append(" WHERE  b.id IN (");

	        // Monta placeholders ? para o IN, conforme quantidade de ids
	        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(", "));
	        sbSQL.append(placeholders);
	        sbSQL.append(") ");

	        sbSQL.append(" ORDER BY b.data_criacao DESC ");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        // Seta os parâmetros dos IDs
	        int paramIndex = 1;
	        for (Integer id : ids) {
	            ps.setInt(paramIndex++, id);
	        }

	        rs = ps.executeQuery();

	        while (rs.next()) {
	            int boletimId = rs.getInt("id");
	            Boletim item = boletinsMap.get(boletimId);

	            if (item == null) {
	                item = new Boletim();
	                item.setId(boletimId);
	                item.setIdSituacao(rs.getInt("id_situacao"));
	                item.setSituacao(rs.getString("situacao"));
	                item.setIdTipo(rs.getInt("id_tipo"));
	                item.setTipo(rs.getString("tipo_ocorrencia"));
	                item.setDetalhamento(rs.getString("detalhamento"));
	                item.setDataCriacao(rs.getTimestamp("data_criacao"));
	                item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
	                item.setIdUsuario(rs.getInt("id_usuario"));
	                item.setUsuario(rs.getString("usuario_criacao"));
	                item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
	                item.setIndividuos(new ArrayList<>());
	                item.setVeiculos(new ArrayList<>());

	                // Local
	                if (rs.getObject("local_id") != null) {
	                    BoletimLocal local = new BoletimLocal();
	                    local.setId(rs.getInt("local_id"));
	                    local.setBairro(rs.getString("bairro"));
	                    local.setRua(rs.getString("rua"));
	                    local.setNumero(rs.getInt("numero"));
	                    local.setComplemento(rs.getString("complemento"));

	                    if (rs.getObject("id_cidade") != null) {
	                        Cidade cidade = new Cidade();
	                        cidade.setId(rs.getInt("id_cidade"));
	                        cidade.setNome(rs.getString("cidade_nome"));
	                        cidade.setIdEstado(rs.getInt("id_estado"));
	                        local.setCidade(cidade);
	                    }

	                    item.setLocal(local);
	                }

	                boletinsMap.put(boletimId, item);
	            }

	            // Indivíduos
	            if (rs.getObject("individuo_id") != null) {
	                Integer individuoId = rs.getInt("individuo_id");
	                if (item.getIndividuos().stream().noneMatch(i -> i.getId().equals(individuoId))) {
	                    BoletimIndividuo individuo = new BoletimIndividuo();
	                    individuo.setId(individuoId);
	                    individuo.setNome(rs.getString("individuo_nome"));
	                    individuo.setCpf(rs.getString("individuo_cpf"));
	                    item.getIndividuos().add(individuo);
	                }
	            }

	            // Veículos
	            if (rs.getObject("veiculo_id") != null) {
	                Integer veiculoId = rs.getInt("veiculo_id");
	                if (item.getVeiculos().stream().noneMatch(v -> v.getId().equals(veiculoId))) {
	                    BoletimVeiculo veiculo = new BoletimVeiculo();
	                    veiculo.setId(veiculoId);
	                    veiculo.setPlaca(rs.getString("veiculo_placa"));
	                    veiculo.setMarca(rs.getString("veiculo_marca"));
	                    item.getVeiculos().add(veiculo);
	                }
	            }
	        }

	        retorno.setListaBoletins(new ArrayList<>(boletinsMap.values()));

	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao obter boletins por IDs!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null)
	                rs.close();
	            if (ps != null)
	                ps.close();
	            if (conn != null)
	                conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro)
	            throw new SQLException(msgErro);
	    }

	    return retorno;
	}

	public static Integer inserirBoletim(Boletim boletim) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer idGerado = null;

		try {
			sbSQL.append("INSERT INTO muralha.boletim ");
			sbSQL.append(
					"(id_tipo, id_local, id_situacao, detalhamento, id_usuario, data_criacao, data_encerramento, permite_atendimento) ");
			sbSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

			// 1. id_tipo
			if (boletim.getIdTipo() != null) {
				ps.setInt(1, boletim.getIdTipo());
			} else {
				ps.setNull(1, java.sql.Types.INTEGER);
			}

			// 2. id_local
			if (boletim.getIdLocal() != null) {
				ps.setInt(2, boletim.getIdLocal());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}

			// 3. id_situacao
			if (boletim.getIdSituacao() != null) {
				ps.setInt(3, boletim.getIdSituacao());
			} else {
				ps.setNull(3, java.sql.Types.INTEGER);
			}

			// 4. detalhamento
			if (boletim.getDetalhamento() != null && !boletim.getDetalhamento().trim().isEmpty()) {
				ps.setString(4, boletim.getDetalhamento());
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);
			}

			// 5. id_usuario
			if (boletim.getIdUsuario() != null) {
				ps.setInt(5, boletim.getIdUsuario());
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}

			// 6. data_criacao
			if (boletim.getDataCriacao() != null) {
				ps.setTimestamp(6, new java.sql.Timestamp(boletim.getDataCriacao().getTime()));
			} else {
				ps.setNull(6, java.sql.Types.TIMESTAMP);
			}

			// 7. data_encerramento
			if (boletim.getDataEncerramento() != null) {
				ps.setTimestamp(7, new java.sql.Timestamp(boletim.getDataEncerramento().getTime()));
			} else {
				ps.setNull(7, java.sql.Types.TIMESTAMP);
			}

			// 8. permite_atendimento
			if (boletim.getPermitirAtendimento() != null) {
				ps.setInt(8, boletim.getPermitirAtendimento());
			} else {
				ps.setNull(8, java.sql.Types.INTEGER);
			}

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 1) {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					idGerado = rs.getInt(1); // ID auto-gerado
				}
			}

		} catch (Exception e) {
			String msgErro = "Erro ao inserir Boletim!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados!", e);
			}
		}

		return idGerado;
	}

	public static Boletins ObterBoletimPorId(Integer id) throws ConexaoException, SQLException {
		Map<Integer, Boletim> boletinsMap = new LinkedHashMap<>();
		Boletins retorno = new Boletins();
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(" SELECT b.id, ");
			sbSQL.append("        b.id_situacao, ");
			sbSQL.append("        bs.descricao AS situacao, ");
			sbSQL.append("        b.id_tipo, ");
			sbSQL.append("        ot.descricao AS tipo_ocorrencia, ");
			sbSQL.append("        b.detalhamento, ");
			sbSQL.append("        b.permite_atendimento, ");
			sbSQL.append("        b.data_criacao, ");
			sbSQL.append("        b.data_encerramento, ");
			sbSQL.append("        b.id_usuario, ");
			sbSQL.append("        su.usuario AS usuario_criacao, ");
			sbSQL.append("        su.nome AS nome_usuario_criacao, ");
			sbSQL.append("        bl.id AS local_id, ");
			sbSQL.append("        bl.id_cidade, ");
			sbSQL.append("        bl.bairro, ");
			sbSQL.append("        bl.rua, ");
			sbSQL.append("        bl.numero, ");
			sbSQL.append("        bl.complemento, ");

			sbSQL.append("        bi.id AS individuo_id, ");
			sbSQL.append("        bi.nome AS individuo_nome, ");
			sbSQL.append("        bi.cpf AS individuo_cpf, ");
			sbSQL.append("        bi.id_tipo_envolvimento AS individuo_tipo_id, ");
			sbSQL.append("        bi.detalhe_envolvimento AS individuo_detalhamento, ");
			sbSQL.append("        bit.descricao AS individuo_tipo_nome, ");

			sbSQL.append("        bv.id AS veiculo_id, ");
			sbSQL.append("        bv.placa AS veiculo_placa, ");
			sbSQL.append("        bv.marca AS veiculo_marca, ");
			sbSQL.append("        bv.modelo AS veiculo_modelo, ");
			sbSQL.append("        bv.cor AS veiculo_cor, ");

			sbSQL.append("        bd.id AS documento_id, ");
			sbSQL.append("        bd.tipo AS documento_tipo, ");
			sbSQL.append("        bd.dir_arquivo AS documento_dir, ");
			sbSQL.append("        bd.detalhamento AS documento_detalhamento, ");

			sbSQL.append("        ba.id AS apreensao_id, ");
			sbSQL.append("        ba.tipo AS apreensao_tipo, ");
			sbSQL.append("        ba.descricao AS apreensao_descricao, ");

			sbSQL.append("        c.id AS cidade_id, ");
			sbSQL.append("        c.nome AS cidade_nome, ");
			sbSQL.append("        c.id_estado ");

			sbSQL.append(" FROM muralha.boletim b ");
			sbSQL.append("      LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao ");
			sbSQL.append("      LEFT JOIN muralha.ocorrencia_tipo ot ON ot.id = b.id_tipo ");
			sbSQL.append("      LEFT JOIN sis_usuario su ON su.id_usuario = b.id_usuario ");
			sbSQL.append("      LEFT JOIN muralha.boletim_local bl ON bl.id = b.id_local ");
			sbSQL.append("      LEFT JOIN muralha.cidade c ON c.id = bl.id_cidade ");
			sbSQL.append("      LEFT JOIN muralha.boletim_individuo bi ON bi.id_boletim = b.id ");
			sbSQL.append("      LEFT JOIN muralha.boletim_individuo_tipo bit ON bit.id = bi.id_tipo_envolvimento ");
			sbSQL.append("      LEFT JOIN muralha.boletim_veiculo bv ON bv.id_boletim = b.id ");
			sbSQL.append("      LEFT JOIN muralha.boletim_documento bd ON bd.id_boletim = b.id ");
			sbSQL.append("      LEFT JOIN muralha.boletim_apreensao ba ON ba.id_boletim = b.id ");
			sbSQL.append(" WHERE b.id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id);
			rs = ps.executeQuery();

			while (rs.next()) {
				int boletimId = rs.getInt("id");
				Boletim item = boletinsMap.get(boletimId);

				if (item == null) {
					item = new Boletim();
					item.setId(boletimId);
					item.setIdSituacao(rs.getInt("id_situacao"));
					item.setSituacao(rs.getString("situacao"));
					item.setIdTipo(rs.getInt("id_tipo"));
					item.setTipo(rs.getString("tipo_ocorrencia"));
					item.setDetalhamento(rs.getString("detalhamento"));
					item.setDataCriacao(rs.getTimestamp("data_criacao"));
					item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
					item.setIdUsuario(rs.getInt("id_usuario"));
					item.setUsuario(rs.getString("usuario_criacao"));
					item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
					item.setPermitirAtendimento(rs.getInt("permite_atendimento"));
					item.setIndividuos(new ArrayList<>());
					item.setVeiculos(new ArrayList<>());
					item.setDocumentos(new ArrayList<>());
					item.setApreensoes(new ArrayList<>());

					// Local
					if (rs.getObject("local_id") != null) {
						BoletimLocal local = new BoletimLocal();
						local.setId(rs.getInt("local_id"));
						local.setBairro(rs.getString("bairro"));
						local.setRua(rs.getString("rua"));
						local.setNumero(rs.getInt("numero"));
						local.setComplemento(rs.getString("complemento"));
						local.setIdCidade(rs.getInt("id_cidade"));
						if (rs.getObject("id_cidade") != null) {
							Cidade cidade = new Cidade();
							cidade.setId(rs.getInt("cidade_id"));
							cidade.setNome(rs.getString("cidade_nome"));
							cidade.setIdEstado(rs.getInt("id_estado"));
							local.setCidade(cidade);
						}

						item.setLocal(local);
					}

					boletinsMap.put(boletimId, item);
				}

				// Adiciona veiculo
				if (rs.getObject("veiculo_id") != null) {
					Integer veiculoId = rs.getInt("veiculo_id");
					if (item.getVeiculos().stream().noneMatch(v -> v.getId().equals(veiculoId))) {
						BoletimVeiculo veiculo = new BoletimVeiculo();
						veiculo.setId(veiculoId);
						veiculo.setPlaca(rs.getString("veiculo_placa"));
						veiculo.setMarca(rs.getString("veiculo_marca"));
						veiculo.setModelo(rs.getString("veiculo_modelo"));
						veiculo.setCor(rs.getString("veiculo_cor"));
						item.getVeiculos().add(veiculo);
					}
				}

				// Adiciona Documento
				if (rs.getObject("documento_id") != null) {
					Integer documentoId = rs.getInt("documento_id");
					if (item.getDocumentos().stream().noneMatch(d -> d.getId().equals(documentoId))) {
						BoletimDocumento doc = new BoletimDocumento();
						doc.setId(documentoId);
						doc.setIdBoletim(id);
						doc.setTipo(rs.getString("documento_tipo"));
						doc.setDirArquivo(rs.getString("documento_dir"));
						doc.setDetalhamento(rs.getString("documento_detalhamento"));
						item.getDocumentos().add(doc);
					}
				}

				// Adiciona Individuos
				if (rs.getObject("individuo_id") != null) {
					Integer individuoId = rs.getInt("individuo_id");
					if (item.getIndividuos().stream().noneMatch(i -> i.getId().equals(individuoId))) {
						BoletimIndividuo individuo = new BoletimIndividuo();
						individuo.setId(individuoId);
						individuo.setIdBoletim(id);
						individuo.setNome(rs.getString("individuo_nome"));
						individuo.setCpf(rs.getString("individuo_cpf"));
						individuo.setIdTipoEnvolvimento(rs.getInt("individuo_tipo_id"));
						individuo.setDetalheEnvolvimento(rs.getString("individuo_detalhamento"));

						BoletimIndividuoTipo tipo = new BoletimIndividuoTipo();
						tipo.setId(rs.getInt("individuo_tipo_id"));
						tipo.setDescricao(rs.getString("individuo_tipo_nome"));

						individuo.setTipoEnvolvimento(tipo);

						item.getIndividuos().add(individuo);
					}
				}

				// Adiciona Apreensao
				if (rs.getObject("apreensao_id") != null) {
					Integer apreensaoId = rs.getInt("apreensao_id");
					if (item.getApreensoes().stream().noneMatch(a -> a.getId().equals(apreensaoId))) {
						BoletimApreensao apreensao = new BoletimApreensao();
						apreensao.setId(apreensaoId);
						apreensao.setIdBoletim(id);
						apreensao.setTipo(rs.getString("apreensao_tipo"));
						apreensao.setDescricao(rs.getString("apreensao_descricao"));
						item.getApreensoes().add(apreensao);
					}
				}
			}

			Boletim primeiroBoletim = boletinsMap.values().stream().findFirst().orElse(null);

	        if (primeiroBoletim != null) {
	            retorno.setListaBoletins(Collections.singletonList(primeiroBoletim));
	        } else {
	            retorno.setListaBoletins(new ArrayList<>());
	        }

		} catch (Exception e) {
			logger.error("Erro ao consultar boletim por ID: " + e.getMessage(), e);
			throw new SQLException("Erro ao consultar boletim por ID");
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ignored) {
				}
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ignored) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception ignored) {
				}
		}
		
		return retorno; 
	}
}