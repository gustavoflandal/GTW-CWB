package muralha.digital.painelInformacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

@XmlRootElement(name = "PainelInformacoes")
@XmlAccessorType(XmlAccessType.FIELD)
public class TotalInformacoes {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(TotalInformacoes.class);

	@XmlElementWrapper(name = "PainelInformacao")
	@XmlElement(name = "Consulta")

	TotalInformacaoResponse painelInformacaoResponse;

	public TotalInformacaoResponse getPainelInformacaoResponse() {
		return painelInformacaoResponse;
	}

	public TotalInformacoes() {
		super();
	}

	public static TotalInformacaoResponse ObterTotalInformacao(String periodo) throws SQLException {
	    TotalInformacaoResponse response = new TotalInformacaoResponse();
	    List<IndicadorDTO> indicadores = new ArrayList<>();

	    // converte o período (24, 48 etc.) em horas
	    int horas;
	    try {
	        horas = Integer.parseInt(periodo);
	    } catch (NumberFormatException e) {
	        throw new IllegalArgumentException("Período inválido: " + periodo);
	    }

	    // --- SQLs ---
	    String sqlFatosRegistrados = 
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.registro_fato " +
	        "WHERE data_criacao >= DATEADD(HOUR, -?, GETDATE())";

	    String sqlFatosEncerrados = 
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.registro_fato " +
	        "WHERE (data_encerramento >= DATEADD(HOUR, -?, GETDATE()) OR id_status = 2)";

	    String sqlFatosSemBoletim =
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.registro_fato " +
	        "WHERE data_criacao >= DATEADD(HOUR, -?, GETDATE()) AND tem_boletim = 0";

	    String sqlAlarmesSimples = 
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.alerta a " +
	        "JOIN muralha.cad_veiculo_monitorado v ON a.id_cad_veiculo_monitorado = v.id " +
	        "WHERE v.supervisionado = 0 AND a.data >= DATEADD(HOUR, -?, GETDATE())";

	    String sqlAlarmesSupervisionados = 
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.alerta a " +
	        "JOIN muralha.cad_veiculo_monitorado v ON a.id_cad_veiculo_monitorado = v.id " +
	        "WHERE v.supervisionado = 1 AND a.data >= DATEADD(HOUR, -?, GETDATE())";

	    String sqlAlarmesNaoAssinados = 
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.alerta a " +
	        "JOIN muralha.cad_veiculo_monitorado v ON a.id_cad_veiculo_monitorado = v.id " +
	        "WHERE v.supervisionado = 1 AND a.assinado = 0 AND a.data >= DATEADD(HOUR, -?, GETDATE())";

	    String sqlAlarmesSemConcordancia =
	        "SELECT COUNT(*) AS total " +
	        "FROM muralha.alerta a " +
	        "JOIN muralha.cad_veiculo_monitorado v ON a.id_cad_veiculo_monitorado = v.id " +
	        "WHERE a.id_status_alerta = '298F5A62-C799-4CA9-8220-6B08F8664534' " +
	        "AND a.data >= DATEADD(HOUR, -?, GETDATE())";
	    
	    String sqlFatosAlterados =
	            "SELECT COUNT(rf.id) AS total " +
	            "FROM muralha.registro_fato rf " +
	            "WHERE rf.data_criacao >= DATEADD(HOUR, -?, GETDATE()) " +
	            "AND EXISTS (SELECT 1 FROM muralha.registro_fato_historico rfh WHERE rfh.id_registro = rf.id)";
	    
	    String sqlFatosComplemento =
	            "SELECT COUNT(rf.id) AS total " +
	            "FROM muralha.registro_fato rf " +
	            "WHERE rf.data_criacao >= DATEADD(HOUR, -?, GETDATE()) " +
	            "AND (" +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_documento WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_endereco WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_individuo WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_link WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_objeto WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.registro_fato_veiculo WHERE id_registro_fato = rf.id) " +
	            "    OR " +
	            "    NOT EXISTS (SELECT 1 FROM muralha.boletim WHERE id_registro_fato = rf.id)" +
	            ")";
	    
	    String sqlVeiculosRemovidos =
	            "SELECT COUNT(*) AS total " +
	            "FROM muralha.registro_fato rf " +
	            "JOIN muralha.registro_fato_historico rfh ON rf.id = rfh.id_registro " + // Corrigido o nome da coluna no join
	            "WHERE rf.data_criacao >= DATEADD(HOUR, -?, GETDATE()) " +
	            "AND rfh.dados_novos LIKE '%\"veiculos\":{\"removidos\":[{%'";
	    
	    String sqlFatosAnotacoes =
	    	    "SELECT COUNT(rf.id) AS total " +
	    	    "FROM muralha.registro_fato rf " +
	    	    "WHERE rf.data_criacao >= DATEADD(HOUR, -?, GETDATE()) " +
	    	    "AND EXISTS (" +
	    	    "    SELECT 1 " +
	    	    "    FROM muralha.boletim b " +
	    	    "    WHERE b.id_registro_fato = rf.id " +
	    	    "    AND b.detalhamento IS NOT NULL " +
	    	    "    AND LTRIM(RTRIM(b.detalhamento)) <> ''" + 
	    	    ")";

	    try (Connection conn = Conexao.getConexao()) {

	        // --- FATOS REGISTRADOS ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosRegistrados)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS",
	                        rs.getInt("total"),
	                        "fatos"
	                    ));
	                }
	            }
	        }
	        
	        // --- FATOS QUE NECESSITAM DE COMPLEMENTO ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosComplemento)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS QUE NECESSITAM DE COMPLEMENTO DE INFORMAÇÕES",
	                        rs.getInt("total"),
	                        "fatosComplemento"
	                    ));
	                }
	            }
	        }
	        
	        // --- FATOS QUE RECEBERAM ANOTAÇÕES ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosAnotacoes)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS QUE RECEBERAM ANOTAÇÕES",
	                        rs.getInt("total"),
	                        "fatosAnotacoes"
	                    ));
	                }
	            }
	        }
	        
	        // --- FATOS ALTERADOS OU COMPLEMENTADOS ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosAlterados)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS QUE FORAM ALTERADOS OU COMPLEMENTADOS",
	                        rs.getInt("total"),
	                        "fatosAlterados"
	                    ));
	                }
	            }
	        }
	        
	        // --- VEÍCULOS REMOVIDOS DOS FATOS REGISTRADOS ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlVeiculosRemovidos)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "VEÍCULOS REMOVIDOS DOS FATOS REGISTRADOS",
	                        rs.getInt("total"),
	                        "veiculosRemovidos"
	                    ));
	                }
	            }
	        }

	        // --- FATOS ENCERRADOS ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosEncerrados)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS QUE FORAM ENCERRADOS POR USUÁRIO AUTORIZADO",
	                        rs.getInt("total"),
	                        "fatosEncerrados"
	                    ));
	                }
	            }
	        }

	        // --- FATOS SEM BOLETIM ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlFatosSemBoletim)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "FATOS REGISTRADOS SEM BOLETIM DE OCORRÊNCIA CADASTRADO",
	                        rs.getInt("total"),
	                        "fatosSemBoletim"
	                    ));
	                }
	            }
	        }

	        // --- ALARMES DE MONITORAMENTOS SIMPLES ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlAlarmesSimples)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "ALARMES DE MONITORAMENTOS SIMPLES",
	                        rs.getInt("total"),
	                        "alarmesSimples"
	                    ));
	                }
	            }
	        }

	        // --- ALARMES DE MONITORAMENTOS SUPERVISIONADOS ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlAlarmesSupervisionados)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "ALARMES DE MONITORAMENTOS SUPERVISIONADOS",
	                        rs.getInt("total"),
	                        "alarmesSupervisionados"
	                    ));
	                }
	            }
	        }

	        // --- ALARMES SUPERVISIONADOS NÃO ASSINADOS PELO OPERADOR ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlAlarmesNaoAssinados)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "ALARMES DE MONITORAMENTOS SUPERVISIONADOS NÃO ASSINADOS PELO OPERADOR",
	                        rs.getInt("total"),
	                        "alarmesNaoAssinados"
	                    ));
	                }
	            }
	        }

	        // --- ALARMES SUPERVISIONADOS SEM CONCORDÂNCIA DO SUPERVISOR ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlAlarmesSemConcordancia)) {
	            ps.setInt(1, horas);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    indicadores.add(new IndicadorDTO(
	                        "ALARMES DE MONITORAMENTOS SUPERVISIONADOS SEM CONCORDÂNCIA DO SUPERVISOR",
	                        rs.getInt("total"),
	                        "alarmesSemConcordancia"
	                    ));
	                }
	            }
	        }

	        // --- INDICADORES PLACEHOLDER 0 ---
	        indicadores.add(new IndicadorDTO(
	            "VEÍCULOS CUJAS PLACAS FORAM ALTERADAS NOS FATOS REGISTRADOS",
	            0,
	            "veiculosAlterados"
	        ));

	    } catch (ConexaoException e) {
	        throw new SQLException("Erro ao conectar com o banco", e);
	    }

	    response.setIndicadores(indicadores);
	    return response;
	}
}