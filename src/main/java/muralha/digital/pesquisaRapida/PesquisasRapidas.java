package muralha.digital.pesquisaRapida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@XmlRootElement(name = "PesquisasRapidas")
@XmlAccessorType(XmlAccessType.FIELD)
public class PesquisasRapidas {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(PesquisasRapidas.class);

	@XmlElementWrapper(name = "PesquisaRapida")
	@XmlElement(name = "Consulta")

	PesquisaRapidaResponse pesquisaRapidaResponse;

	public PesquisaRapidaResponse getPesquisaRapidasResponse() {
		return pesquisaRapidaResponse;
	}

	public PesquisasRapidas() {
		super();
	}

	public static PesquisaRapidaResponse ObterPesquisaRapidaPorNomeCPFVei(
	        TipoConsulta tipoConsulta, String valor) throws SQLException {

	    PesquisaRapidaResponse response = new PesquisaRapidaResponse();

	    try (Connection conn = Conexao.getConexao()) {
	        // Agora retorna registros de fato
	        List<RegistroFatoResponse> registrosFato = 
	            buscarRegistrosFatoRelacionados(conn, tipoConsulta, valor, response);

	        List<String> placasRegistroFato = new ArrayList<>();

	        if ((tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) 
	                && registrosFato != null) {

	            for (RegistroFatoResponse registro : registrosFato) {
	                if (registro.getVeiculos() != null) {
	                    for (RegistroFatoResponse.Veiculo veiculo : registro.getVeiculos()) {
	                        String placa = veiculo.getPlaca();
	                        if (placa != null && !placa.trim().isEmpty()) {
	                            placasRegistroFato.add(placa.trim().toUpperCase());
	                        }
	                    }
	                }
	            }
	        }

	        // Busca veículos monitorados ativos
	        List<VeiculoMonitoradoResponse> veiculosMonitorados =
	            buscarVeiculosMonitoradosAtivos(conn, tipoConsulta, valor, placasRegistroFato);
	        response.setVeiculosMonitorados(veiculosMonitorados);

	        // Se houver veículos monitorados, buscar alertas
	        List<AlertaResponse> alertas;
            alertas = buscarAlertasRelacionados(conn, tipoConsulta, valor, placasRegistroFato);

	        // Atualiza o response com registros de fato
	        response.setRegistrosFato(registrosFato);
	        response.setAlertas(alertas);

	        int total = 0;
	        if (registrosFato != null) total += registrosFato.size();
	        if (alertas != null) total += alertas.size();
	        if (veiculosMonitorados != null) total += veiculosMonitorados.size();

	        response.setTotalRegistros(total);
	    } catch (ConexaoException e) {
	        throw new SQLException("Erro ao conectar com o banco", e);
	    }

	    return response;
	}

	private static List<RegistroFatoResponse> buscarRegistrosFatoRelacionados(
	        Connection conn, TipoConsulta tipoConsulta,
	        String valor, PesquisaRapidaResponse response) throws SQLException {

	    List<RegistroFatoResponse> registros = new ArrayList<>();
	    Map<Long, RegistroFatoResponse> registroMap = new LinkedHashMap<>();

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT ");
	    sql.append(" rf.id AS registro_id, ");
	    sql.append(" rfi.nome AS individuo_nome, ");
	    sql.append(" rfi.cpf AS individuo_cpf, ");
	    sql.append(" rfv.id AS veiculo_id, ");
	    sql.append(" rfv.placa AS veiculo_placa, ");
	    sql.append(" rf.tem_boletim AS tem_boletim, ");
	    sql.append(" (SELECT COUNT(*) ");
	    sql.append("    FROM muralha.registro_fato_endereco rfe ");
	    sql.append("   WHERE rfe.id_registro_fato = rf.id ");
	    sql.append("     AND rfe.id_tipo_evento = 2) AS total_abordagens ");
	    sql.append("FROM muralha.registro_fato rf ");
	    sql.append("LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id ");
	    sql.append("LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
	    sql.append("WHERE 1=1 ");

	    switch (tipoConsulta) {
	        case CPF:
	            sql.append(" AND rfi.cpf = ? ");
	            break;
	        case NOME:
	            sql.append(" AND rfi.nome LIKE ? ");
	            valor = "%" + valor + "%";
	            break;
	        case VEICULO:
	            sql.append(" AND rfv.placa = ? ");
	            break;
	        default:
	            throw new IllegalArgumentException("Tipo de consulta inválido");
	    }

	    sql.append("ORDER BY rf.data_criacao DESC ");

	    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
	        ps.setString(1, valor);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                long registroId = rs.getLong("registro_id");

	                // Nome e CPF uma única vez no response geral
	                if (response.getNome() == null) {
	                    response.setNome(rs.getString("individuo_nome"));
	                    response.setCpf(rs.getString("individuo_cpf"));
	                }

	                RegistroFatoResponse registro = registroMap.get(registroId);
	                if (registro == null) {
	                    registro = new RegistroFatoResponse();
	                    registro.setId(registroId);
	                    registro.setTemBoletim(rs.getInt("tem_boletim") == 1);
	                    registro.setTotalDeAbordagens(rs.getInt("total_abordagens"));
	                    registro.setVeiculos(new ArrayList<>());
	                    registroMap.put(registroId, registro);
	                }

	                Integer veiculoId = rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : null;
	                String placa = rs.getString("veiculo_placa");

	                if (veiculoId != null && placa != null) {
	                    RegistroFatoResponse.Veiculo v = new RegistroFatoResponse.Veiculo();
	                    v.setId(veiculoId);
	                    v.setPlaca(placa);
	                    registro.getVeiculos().add(v);
	                }
	            }
	        }
	    }

	    registros.addAll(registroMap.values());
	    return registros;
	}
	
	private static List<AlertaResponse> buscarAlertasRelacionados(
	        Connection conn,
	        TipoConsulta tipoConsulta,
	        String valor,
	        List<String> placas) throws SQLException {

	    List<AlertaResponse> alertas = new ArrayList<>();

	    // Se for CPF ou NOME mas não tiver placas, não há alertas a buscar
	    if ((tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) &&
	        (placas == null || placas.isEmpty())) {
	        return alertas;
	    }

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT a.id AS alerta_id, ");
	    sql.append("       a.id_cad_veiculo_monitorado, ");
	    sql.append("       v.placa, ");
	    sql.append("       v.supervisionado ");
	    sql.append("FROM muralha.alerta a ");
	    sql.append("INNER JOIN muralha.cad_veiculo_monitorado v ON v.id = a.id_cad_veiculo_monitorado ");
	    sql.append("WHERE 1=1 ");

	    // Filtros de acordo com o tipo de consulta
	    if (tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) {
	        sql.append(" AND v.placa IN (");
	        for (int i = 0; i < placas.size(); i++) {
	            sql.append("?");
	            if (i < placas.size() - 1) {
	                sql.append(", ");
	            }
	        }
	        sql.append(") ");
	    } else if (tipoConsulta == TipoConsulta.VEICULO) {
	        sql.append(" AND v.placa = ? ");
	    }

	    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
	        if (tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) {
	            for (int i = 0; i < placas.size(); i++) {
	                ps.setString(i + 1, placas.get(i));
	            }
	        } else if (tipoConsulta == TipoConsulta.VEICULO) {
	            ps.setString(1, valor.trim().toUpperCase());
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                AlertaResponse alerta = new AlertaResponse();
	                alerta.setId(rs.getString("alerta_id"));
	                alerta.setIdVeiculoMonitorado(rs.getString("id_cad_veiculo_monitorado"));
	                alerta.setPlaca(rs.getString("placa"));
	                alerta.setSupervisionado(rs.getBoolean("supervisionado"));
	                alertas.add(alerta);
	            }
	        }
	    }

	    return alertas;
	}
	
	private static List<VeiculoMonitoradoResponse> buscarVeiculosMonitoradosAtivos(
	        Connection conn,
	        TipoConsulta tipoConsulta,
	        String valor,
	        List<String> placas) throws SQLException {

	    List<VeiculoMonitoradoResponse> veiculos = new ArrayList<>();

	    if ((tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) &&
	        (placas == null || placas.isEmpty())) {
	        return veiculos;
	    }

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT v.id, v.placa, v.supervisionado ");
	    sql.append("FROM muralha.cad_veiculo_monitorado v ");
	    sql.append("WHERE v.data_inativacao IS NULL "); // Somente ativos

	    if (tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) {
	        sql.append(" AND v.placa IN (");
	        for (int i = 0; i < placas.size(); i++) {
	            sql.append("?");
	            if (i < placas.size() - 1) {
	                sql.append(", ");
	            }
	        }
	        sql.append(") ");
	    } else if (tipoConsulta == TipoConsulta.VEICULO) {
	        sql.append(" AND v.placa = ? ");
	    }

	    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
	        if (tipoConsulta == TipoConsulta.CPF || tipoConsulta == TipoConsulta.NOME) {
	            for (int i = 0; i < placas.size(); i++) {
	                ps.setString(i + 1, placas.get(i));
	            }
	        } else if (tipoConsulta == TipoConsulta.VEICULO) {
	            ps.setString(1, valor.trim().toUpperCase());
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                VeiculoMonitoradoResponse veiculo = new VeiculoMonitoradoResponse();
	                veiculo.setId(rs.getString("id"));
	                veiculo.setPlaca(rs.getString("placa"));
	                veiculo.setSupervisionado(rs.getBoolean("supervisionado"));
	                veiculos.add(veiculo);
	            }
	        }
	    }

	    return veiculos;
	}
	
	public static List<String> obterSugestoesPorNome(String termo) throws SQLException {
		List<String> sugestoesFormatadas = new ArrayList<>();
	    String sql = "SELECT DISTINCT TOP 10 rfi.nome, rfi.cpf " +
                "FROM muralha.registro_fato_individuo AS rfi " +
                "WHERE rfi.nome LIKE ? " + 
                "ORDER BY rfi.nome";

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, "%" + termo + "%");

	        rs = ps.executeQuery();

	        while (rs.next()) {
	            String nome = rs.getString("nome");
	            String cpf = rs.getString("cpf");
	            // Formato: "valorParaInput|TextoParaExibir"
	            String valorPrincipal = nome;
	            String textoDisplay = nome + " (CPF: " + cpf + ")";
	            
	            sugestoesFormatadas.add(valorPrincipal + "|" + textoDisplay);
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao consultar sugestões por nome no banco de dados", e);
	        throw new SQLException("Erro ao consultar sugestões por nome.", e);
	    } finally {
	        // Fecha as conexões de forma segura
	        if (rs != null) {
	            try { rs.close(); } catch (SQLException e) { /* log ou ignora */ }
	        }
	        if (ps != null) {
	            try { ps.close(); } catch (SQLException e) { /* log ou ignora */ }
	        }
	        if (conn != null) {
	            try { conn.close(); } catch (SQLException e) { /* log ou ignora */ }
	        }
	    }

	    return sugestoesFormatadas;
	}
}