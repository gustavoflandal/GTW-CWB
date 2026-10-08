package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistrosPassagensVeiculos {
	@XmlTransient
	private static Logger logger = LogManager.getLogger(RegistrosPassagensVeiculos.class);

	
	public static List<RegistroDeFatoTipo> InserirPassagemVeiculo(int idRegistroFato, String idVeiculo, int idUsuario) throws ConexaoException, SQLException {
		
		List<RegistroDeFatoTipo> listaRet = new ArrayList<RegistroDeFatoTipo>();
		StringBuilder sbSQL = new StringBuilder();		
		
		sbSQL.append("insert into muralha.registro_fato_passagem_veic ");
		sbSQL.append("(id_registro_fato, id_veiculo, id_usuario, data) ");
		sbSQL.append("values (?,?,?,GETDATE()) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idRegistroFato);
			ps.setString(2, idVeiculo);
			ps.setInt(3, idUsuario);
			
			ps.execute();
			
		} catch (Exception e) {
			throw new SQLException("Erro ao inserir registro de passagem!", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
}
