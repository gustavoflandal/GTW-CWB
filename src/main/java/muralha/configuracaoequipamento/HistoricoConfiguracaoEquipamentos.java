package muralha.configuracaoequipamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

@XmlRootElement		(name="HistoricoConfiguracaoEquipamentos")
@XmlAccessorType	(XmlAccessType.FIELD)
public class HistoricoConfiguracaoEquipamentos {
	@XmlTransient
	private static Logger logger = LogManager.getLogger(HistoricoConfiguracaoEquipamentos.class);
	
	@XmlElementWrapper	(name = "HistoricoConfiguracaoEquipamentos")
	@XmlElement 		(name = "HistoricoConfiguracaoEquipamento")
	private HistoricoConfiguracaoEquipamento historico = new HistoricoConfiguracaoEquipamento();
	
	public HistoricoConfiguracaoEquipamento obterConfiguracoes()
	{
		return historico;
	}
	
	public static Boolean CadastrarHistorico(
			int idUsuarioResponsavel,
			String dadosJson
		) throws ConexaoException, SQLException {

			String sql = "INSERT INTO muralha.historico_config_grupo_permissao "
			           + "(id, id_usuario_responsavel, data_acao, dados_json) "
			           + "VALUES (NEWID(), ?, GETDATE(), ?)";

			Connection conn = null;
			PreparedStatement ps = null;

			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sql);
				
				ps.setInt(1, idUsuarioResponsavel);
				ps.setString(2, dadosJson);

				ps.executeUpdate();

			} catch (Exception e) {
				throw new SQLException("Erro ao executar INSERT ao cadastrar o histórico do equipamento: ", e);
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					throw new ConexaoException("Erro ao fechar conexão de SQL", e);
				}			
			}
			return true;
		}
}
