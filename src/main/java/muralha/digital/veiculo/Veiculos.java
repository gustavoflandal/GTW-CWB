package muralha.digital.veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;

import muralha.digital.util.Paginacao;
import muralha.digital.veiculo.imagem.VeiculoImagem;
import muralha.digital.veiculo.imagem.VeiculoImagens;


@XmlRootElement		(name="Veiculos") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class Veiculos
{	
	@XmlTransient
	private static final Logger logger = LogManager.getLogger(Veiculos.class);
	
	@XmlElementWrapper	(name = "ListaVeiculos")
	@XmlElement			(name = "Veiculo")	
	private List<Veiculo> listaVeiculos;
	
	private Paginacao paginacao;
	
	
	public Veiculos() {super();}
	

	public List<Veiculo> getListaVeiculos() {
		return listaVeiculos;
	}
	public void setListaVeiculos(List<Veiculo> listaVeiculos) {
		this.listaVeiculos = listaVeiculos;
	}
	
	public Paginacao getPaginacao() {
		return paginacao;
	}
	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}
		
	/*
	 * Retornar lista com as novas passagens de veículos
	 */
	public static Veiculos ObterVeiculosTempoReal() 
	{
		
		Veiculos retVeiculos = new Veiculos();
		List<Veiculo> listaVeiculos = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try
		{
			sbSQL.append(" EXEC muralha.spu_obterVeiculosTempoReal ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{				
				Veiculo veic = new Veiculo();
				
				logger.info("XXXXXXXXXXXXX Consulta de Veiculo Tempo Real" + rs.getString("placa"));
				
				veic.setId(UUID.fromString(rs.getString("id")));
				veic.setIdLocal(rs.getInt("id_local"));
				veic.setDescLocal(rs.getString("nome"));
				veic.setIdPista(rs.getInt("id_pista"));
				veic.setFaixa(rs.getInt("faixa"));
				veic.setPlaca(rs.getString("placa") != null ? rs.getString("placa") : "");
				veic.setVelocidade(rs.getInt("velocidade"));
				veic.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				veic.setDataVeic(rs.getTimestamp("data"));
				
				veic.setSerieEquipamento(rs.getInt("serie_equipamento"));
				veic.setCodigoEquipamento(rs.getString("codigo_equipamento"));;
				
				veic.setPerfil_1(rs.getString("perfil_1") != null ? rs.getString("perfil_1").trim() : "");
				veic.setPerfil_2(rs.getString("perfil_2") != null ? rs.getString("perfil_2").trim() : "");
				veic.setPlaca_frontal(rs.getString("placa_frontal") != null ? rs.getString("placa_frontal").trim() : "");
				veic.setInfo_adicional(rs.getString("info_adicional") != null ? rs.getString("info_adicional").trim() : "");
								
				listaVeiculos.add(veic); 
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter Lista de Veiculos Historico: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}	
		
		retVeiculos.setListaVeiculos(listaVeiculos);
		return retVeiculos;
	}    	
	
	/*
	 * Retornar lista com as passagens de veículos
	 */
	public static Veiculos ObterVeiculosHistorico() 
	{
		Veiculos retVeiculos = new Veiculos();
		List<Veiculo> listaVeiculos = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		String erro = "1";
		
		try
		{
			sbSQL.append(" EXEC muralha.spu_obterVeiculosTempoRealHistorico ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{				
				Veiculo veic = new Veiculo();
				
				logger.info("Consulta de Veiculo histórico");
				
				veic.setId(UUID.fromString(rs.getString("id")));erro = erro + "1";
				veic.setIdLocal(rs.getInt("id_local"));erro = erro + "2";
				veic.setDescLocal(rs.getString("nome"));erro = erro + "3";
				veic.setIdPista(rs.getInt("id_pista"));erro = erro + "4";
				veic.setFaixa(rs.getInt("faixa"));erro = erro + "5";
				veic.setPlaca(rs.getString("placa") != null ? rs.getString("placa") : "");erro = erro + "6";
				veic.setVelocidade(rs.getInt("velocidade"));erro = erro + "7";
				veic.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao") : "");erro = erro + "8";
				veic.setDataVeic(rs.getTimestamp("data"));erro = erro + "9";				
				veic.setPerfil_1(rs.getString("perfil_1") != null ? rs.getString("perfil_1").trim() : "");erro = erro + "10";
				veic.setPerfil_2(rs.getString("perfil_2") != null ? rs.getString("perfil_2").trim() : "");erro = erro + "11";
				veic.setPlaca_frontal(rs.getString("placa_frontal") != null ? rs.getString("placa_frontal").trim() : "");erro = erro + "12";
				veic.setInfo_adicional(rs.getString("info_adicional") != null ? rs.getString("info_adicional").trim() : "");erro = erro + "13";
				veic.setSerieEquipamento(rs.getInt("serie_equipamento"));erro = erro + "14";
				veic.setCodigoEquipamento(rs.getString("codigo_equipamento"));;
				
				listaVeiculos.add(veic); 
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter Lista de Veiculos Historico: erro:: " + erro + " --> " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}	
		
		retVeiculos.setListaVeiculos(listaVeiculos);
		return retVeiculos;
	}
	
	public static Veiculos ObterVeiculosPorFiltros(
		String placa, Date dataIni, Date dataFim, String equipamento, String faixa, String classificacao,
		boolean buscarApenasVeiculoComImagem, boolean consultaMapa, Paginacao paginacao,
		boolean trazer_imagens, boolean exportarConsulta, boolean modoGrade, boolean retornarImagens,
		String corVeiculo, String anoFabricacao, String anoModelo, String renavam, String chassi,
		String tipoVeiculo, String restricao, String marca, String modelo,
		boolean filtrarPorRegistroFato, String caracteristicaRegistro, String tipoRegistro, String naturezaRegistro,
		String tipoPlaca, int filtroPlaca, int idLocalidade, boolean somenteUltimaPassagem, boolean deveAplicarFiltroDeImagens
	) throws SQLException {
		
		Veiculos retorno = new Veiculos();
		List<Veiculo> listaRet = new ArrayList<>();
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;

		try {
			sbSQL.append("DECLARE @placa CHAR(7) = ?, ");
			sbSQL.append("        @dataIni DATETIME = ?, ");
			sbSQL.append("        @dataFim DATETIME = ?, ");
			sbSQL.append("        @equipamentos VARCHAR(1600) = ?, ");
			sbSQL.append("        @faixa VARCHAR(100) = ?, ");
			sbSQL.append("        @classificacao VARCHAR(50) = ?, ");
			sbSQL.append("        @buscarApenasVeiculoComImagem BIT = ?, ");
			sbSQL.append("        @consultaMapa BIT = ?, ");
			sbSQL.append("        @exportarConsulta BIT = ?, ");
			sbSQL.append("        @offset INT = ?, ");
			sbSQL.append("        @itensPorPagina INT = ?, ");
			sbSQL.append("        @marca VARCHAR(35) = ?, ");
			sbSQL.append("        @modelo VARCHAR(35) = ?, ");
			sbSQL.append("        @idCor INT = ?, ");
			sbSQL.append("        @anoFabricacao INT = ?, ");
			sbSQL.append("        @anoModelo INT = ?, ");
			sbSQL.append("        @renavam VARCHAR(11) = ?, ");
			sbSQL.append("        @chassi VARCHAR(17) = ?, ");
			sbSQL.append("        @idLocalidade INT = ?, ");
			sbSQL.append("        @restricao VARCHAR(100) = ?, ");
			sbSQL.append("        @filtrarPorRegistroFato BIT = ?, ");
			sbSQL.append("        @caracteristicaRegistro VARCHAR(20) = ?, ");
			sbSQL.append("        @tipoRegistro INT = ?, ");
			sbSQL.append("        @naturezaRegistro INT = ?, ");
			sbSQL.append("        @filtroPlaca INT = ?, ");
			sbSQL.append("        @tipoPlaca VARCHAR(10) = ?, ");
			sbSQL.append("        @tipoVeiculo VARCHAR(100) = ?, ");
			sbSQL.append("        @deveAplicarFiltroDeImagens BIT = ?, ");
			sbSQL.append("        @somenteUltimaPassagem BIT = ? ");
			
			sbSQL.append("EXEC muralha.spu_obterVeiculosPorFiltros ");
			sbSQL.append("@placa, @dataIni, @dataFim, @equipamentos, @faixa, @classificacao, ");
			sbSQL.append("@buscarApenasVeiculoComImagem, @consultaMapa, @exportarConsulta, ");
			sbSQL.append("@offset, @itensPorPagina, @marca, @modelo, @idCor, ");
			sbSQL.append("@anoFabricacao, @anoModelo, @renavam, @chassi, @idLocalidade, @restricao, ");
			sbSQL.append("@filtrarPorRegistroFato, @caracteristicaRegistro, @tipoRegistro, @naturezaRegistro, ");
			sbSQL.append("@filtroPlaca, @tipoPlaca, @tipoVeiculo, @deveAplicarFiltroDeImagens, @somenteUltimaPassagem ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			int idx = 1;

			ps.setString(idx++, placa);
			ps.setTimestamp(idx++, dataIni != null ? new Timestamp(dataIni.getTime()) : null);
			ps.setTimestamp(idx++, dataFim != null ? new Timestamp(dataFim.getTime()) : null);
			ps.setString(idx++, equipamento);
			ps.setString(idx++, faixa);
			ps.setString(idx++, classificacao);
			ps.setBoolean(idx++, buscarApenasVeiculoComImagem);
			ps.setBoolean(idx++, consultaMapa);
			ps.setBoolean(idx++, exportarConsulta);

			if (paginacao.OperacaoValida()) {
				ps.setInt(idx++, paginacao.Offset());
				ps.setInt(idx++, paginacao.ItensPorPagina());
			} else {
				ps.setInt(idx++, 0);
				ps.setInt(idx++, 5);
			}

			ps.setString(idx++, marca);
			ps.setString(idx++, modelo);
			if (corVeiculo != null && !corVeiculo.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(corVeiculo));
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}
			if (anoFabricacao != null && !anoFabricacao.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(anoFabricacao));
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}
			if (anoModelo != null && !anoModelo.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(anoModelo));
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}
			ps.setString(idx++, renavam);
			ps.setString(idx++, chassi);
			if (idLocalidade >= 0) {
			    ps.setInt(idx++, idLocalidade);
			} else {
			    ps.setNull(idx++, Types.INTEGER);
			}
			ps.setString(idx++, restricao);
			
			ps.setBoolean(idx++, filtrarPorRegistroFato);
			ps.setString(idx++, caracteristicaRegistro);

			if (tipoRegistro != null && !tipoRegistro.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(tipoRegistro));
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}
			if (naturezaRegistro != null && !naturezaRegistro.isEmpty()) {
				ps.setInt(idx++, Integer.parseInt(naturezaRegistro));
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}

			ps.setInt(idx++, filtroPlaca);
			ps.setString(idx++, tipoPlaca);        
			if (tipoVeiculo != null && !tipoVeiculo.trim().isEmpty()) {
				ps.setString(idx++, tipoVeiculo);
			} else {
				ps.setNull(idx++, Types.VARCHAR);
			}

			ps.setBoolean(idx++, deveAplicarFiltroDeImagens);
			ps.setBoolean(idx++, somenteUltimaPassagem);

			rs = ps.executeQuery();

			int registros = 0;
			while (rs.next()) {
				if (registros == 0 && !consultaMapa)
					registros = rs.getInt("total_registros");

				Veiculo item = new Veiculo();
				UUID idVeiculo = UUID.fromString(rs.getString("id"));
				
				var corPlaca = rs.getString("cor_placa");
				
				item.setId(idVeiculo);
				item.setPlaca(rs.getString("placa"));
				item.setDataVeic(rs.getTimestamp("data"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setCodigoEquipamento(rs.getString("codigo_equipamento"));
				item.setDescLocal(rs.getString("nome"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setFaixa(rs.getInt("faixa"));
				item.setLatitude(rs.getDouble("latitude"));
				item.setLongitude(rs.getDouble("longitude"));
				item.setVelocidade(rs.getInt("velocidade"));
				item.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				item.setTipoVeiculo(rs.getString("tipo") != null ? rs.getString("tipo").trim() : "");
				item.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
				item.setComImagem(rs.getBoolean("com_imagem"));
				item.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
				item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				item.setMarca(rs.getString("marca"));
				item.setModelo(rs.getString("modelo"));
				item.setDataImportado(rs.getTimestamp("data_importado"));
				item.setIdVeiculoAnterior(rs.getString("veic_anterior") != null ? UUID.fromString(rs.getString("veic_anterior")) : null);
				item.setIdVeiculoProximo(rs.getString("veic_proximo") != null ? UUID.fromString(rs.getString("veic_proximo")) : null);
				item.setCategoria(rs.getInt("categoria"));
				item.setNumeroEixos(rs.getInt("numero_eixos"));
				item.setRodagemDupla(rs.getInt("rodagem_dupla"));
				item.setPlacaMercosul(rs.getBoolean("placa_mercosul"));
				item.setCorPlaca(corPlaca);

				if (modoGrade) {
					List<VeiculoImagem> imagens = VeiculoImagens.ObterListaImagensPorIdVeiculo(item.getId(), trazer_imagens);
					item.setListaImagens(imagens);
				}

				listaRet.add(item);
			}

			if (!consultaMapa) {
				paginacao.TotalRegistros(registros);
				retorno.setPaginacao(paginacao);
			}
			retorno.setListaVeiculos(listaRet);
		} catch (Exception e) {
			erro = true;
			logger.error("Erro ao obter veículos: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null) conn.close();
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos do banco: " + e.getMessage(), e);
			}
			if (erro) throw new SQLException("Erro ao consultar os veículos no banco de dados!");
		}

		return retorno;
	}
	
	public static Veiculo ObterVeiculoPorId(UUID id) throws SQLException
	{
		Veiculo veic = new Veiculo();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro;

		try {
		
			sbSQL.append(" EXEC muralha.spuObterDadosVeiculoTempoReal ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());
			
			rs = ps.executeQuery();
				
			if (rs.next()) 
			{
				veic.setId(UUID.fromString(rs.getString("id")));
				veic.setIdVeiculo(UUID.fromString(rs.getString("id")));
				veic.setPlaca(rs.getString("placa"));
				veic.setDataVeic(rs.getTimestamp("data"));
				veic.setIdLocal(rs.getInt("id_local"));
				veic.setSerieEquipamento(rs.getInt("serie_equipamento"));
				veic.setCodigoEquipamento(rs.getString("codigo_equipamento"));
				veic.setDescLocal(rs.getString("nome"));
				veic.setIdPista(rs.getInt("id_pista"));
				veic.setFaixa(rs.getInt("faixa"));
				veic.setVelocidade(rs.getInt("velocidade"));
				veic.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				veic.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
				veic.setMarca(rs.getString("marca"));
				veic.setModelo(rs.getString("modelo"));
				veic.setLatitude(rs.getDouble("latitude"));
				veic.setLongitude(rs.getDouble("longitude"));
				
				List<VeiculoImagem> imagens = VeiculoImagens.ObterListaImagensPorIdVeiculo(veic.getId(), false);
				veic.setListaImagens(imagens);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter veículo!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}

			if (erro)
				throw new SQLException("Erro ao consultar o veículo no banco de dados!");
		}
		return veic;
	}
	
	/*
	 * Retornar lista de imagens do veículo por idVeiculo
	 */
	public static Veiculo ObterListaImagensPorIdVeiculo(UUID idVeiculo) 
	{
		Veiculo veiculo = new Veiculo();
		
		try
		{
			List<VeiculoImagem> imagens = VeiculoImagens.ObterListaImagensPorIdVeiculo(idVeiculo, false);
			
			if (!imagens.isEmpty())
			{
				veiculo.setId(idVeiculo);
				veiculo.setListaImagens(imagens);
			}
				
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter lista de imagens do veículo: " + e.getMessage(), e);
		}
		
		return veiculo;	
	}
	
	/*
	 * Obter passagens de veículos com mesma placa a partir do ID do veículo alvo, para exibição no mapa
	 */
	public static List<Veiculo> ObterVeiculosMapaPorIdAlvo(UUID idVeiculoAlvo) throws SQLException
	{
		List<Veiculo> listaRet = new ArrayList<>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro;
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_obterPassagensVeiculoMapaPorIdAlvo ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idVeiculoAlvo.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				Veiculo item = new Veiculo();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setPlaca(rs.getString("placa"));
				item.setDataVeic(rs.getTimestamp("data"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setDescLocal(rs.getString("nome"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setLatitude(rs.getDouble("latitude"));
				item.setLongitude(rs.getDouble("longitude"));
				item.setVelocidade(rs.getInt("velocidade"));
				item.setClassificacao((rs.getString("classificacao") != null ? rs.getString("classificacao").trim(): ""));
				item.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
				item.setComImagem(rs.getBoolean("com_imagem"));
				item.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));

				listaRet.add(item);
			}
		}
		catch(Exception e)
		{
			erro = true;
			msgErro = "Erro ao consultar os veículos!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		
			if (erro)
				throw new SQLException("Erro ao consultar os veículos no banco de dados!");
		}
		return listaRet;
	}
	
    public static List<JsonObject> ObterVeiculo(String placa) {
        List<JsonObject> veiculos = new ArrayList<>();
        String sql = "SELECT DISTINCT id, placa FROM muralha.veiculo_tempo_real WHERE placa LIKE ?";

        try (
            Connection conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, placa + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject veiculo = new JsonObject();
                    veiculo.addProperty("id", rs.getString("id"));
                    veiculo.addProperty("placa", rs.getString("placa"));
                    veiculos.add(veiculo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return veiculos;
    }

	public static List<JsonObject> ListaCores(){
		List<JsonObject> listaCores = new ArrayList<>();
		String sql = "SELECT * FROM cad_cor";

		try (Connection conn = Conexao.getConexao();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject cor = new JsonObject();
				cor.addProperty("id_cor", rs.getInt("id_cor"));
				cor.addProperty("descricao", rs.getString("descricao"));
				listaCores.add(cor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaCores;
	}

	public static List<JsonObject> ListarUf(){
		List<JsonObject> listaUf = new ArrayList<>();
		String sql = "SELECT uf FROM cad_localidade group by uf";

		try (Connection conn = Conexao.getConexao();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject uf = new JsonObject();
				uf.addProperty("uf", rs.getString("uf"));
				listaUf.add(uf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaUf;
	}
	
	public static List<JsonObject> ListarTiposVeiculo(){
		List<JsonObject> listaTipos = new ArrayList<>();
		String sql = "SELECT * FROM cad_tipo";

		try (Connection conn = Conexao.getConexao();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject tipos = new JsonObject();
				tipos.addProperty("id_tipo", rs.getInt("id_tipo"));
				tipos.addProperty("descricao", rs.getString("descricao"));
				listaTipos.add(tipos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaTipos;
	}

	public static List<JsonObject> ListarLocalidades(){
		List<JsonObject> listaLocalidades = new ArrayList<>();
		String sql = "SELECT * FROM cad_localidade ORDER BY uf, nome";

		try (Connection conn = Conexao.getConexao();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject localidade = new JsonObject();
				localidade.addProperty("id_localidade", rs.getInt("id_localidade"));
				localidade.addProperty("nome", rs.getString("nome"));
				localidade.addProperty("uf", rs.getString("uf"));
				listaLocalidades.add(localidade);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaLocalidades;
	}
	
	public static boolean RegistrarExportacaoImagem(
		int idUsuario, 
		int idLocal, 
		String placa, 
		String dataHoraExportacao, 
		String dataHoraPassagem, 
		UUID idVeiculoTempoReal
	) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean sucesso = false;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			sbSQL.append("INSERT INTO muralha.relatorio_imagens_exportadas ");
			sbSQL.append("(id_usuario, id_local, placa, data_hora_exportacao, data_hora_passagem, id_veiculo_tempo_real) ");
			sbSQL.append("VALUES (?, ?, ?, ?, ?, ?)");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			int idx = 1;
			ps.setInt(idx++, idUsuario);
			
			if (idLocal >= 0) {
				ps.setInt(idx++, idLocal);
			} else {
				ps.setNull(idx++, Types.INTEGER);
			}
			
			ps.setString(idx++, placa != null ? placa : "");

			if (dataHoraExportacao != null && !dataHoraExportacao.trim().isEmpty()) {
				try {
					String dataExportacaoCorrigida = dataHoraExportacao.replace(",", "");
					SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					SimpleDateFormat sdfSaida = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dataExportacao = sdfEntrada.parse(dataExportacaoCorrigida);
					String dataExportacaoFormatada = sdfSaida.format(dataExportacao);
					Timestamp timestampExportacao = Timestamp.valueOf(dataExportacaoFormatada);
					ps.setTimestamp(idx++, timestampExportacao);
				} catch (Exception e) {
					logger.warn("Erro ao converter dataHoraExportacao, usando null: " + dataHoraExportacao);
					ps.setNull(idx++, Types.TIMESTAMP);
				}
			} else {
				ps.setNull(idx++, Types.TIMESTAMP);
			}

			if (dataHoraPassagem != null && !dataHoraPassagem.trim().isEmpty()) {
				try {
					String dataPassagemCorrigida = dataHoraPassagem.replace(",", "");
					SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					SimpleDateFormat sdfSaida = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dataPassagem = sdfEntrada.parse(dataPassagemCorrigida);
					String dataPassagemFormatada = sdfSaida.format(dataPassagem);
					Timestamp timestampPassagem = Timestamp.valueOf(dataPassagemFormatada);
					ps.setTimestamp(idx++, timestampPassagem);
				} catch (Exception e) {
					logger.warn("Erro ao converter dataHoraPassagem, usando null: " + dataHoraPassagem);
					ps.setNull(idx++, Types.TIMESTAMP);
				}
			} else {
				ps.setNull(idx++, Types.TIMESTAMP);
			}
			
			ps.setString(idx++, idVeiculoTempoReal.toString());
			
			int linhasAfetadas = ps.executeUpdate();
			sucesso = (linhasAfetadas > 0);
			
			logger.info("Registro de exportação inserido: " + (sucesso ? "Sucesso" : "Falha") + 
					" | Usuário: " + idUsuario + " | Placa: " + placa);
			
		} catch (Exception e) {
			logger.error("Erro ao registrar exportação de imagem: " + e.getMessage(), e);
			sucesso = false;
		} finally {
			try {
				if (conn != null) conn.close();
				if (ps != null) ps.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos do banco: " + e.getMessage(), e);
			}
		}
		
		return sucesso;
	}

	public static List<JsonObject> ListarMarcas(){
		List<JsonObject> listaMarcas = new ArrayList<>();
		String sql = "SELECT * FROM cad_marca_cet ORDER BY descricao";

		try (Connection conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject marca = new JsonObject();
				marca.addProperty("id_marca_cet", rs.getInt("id_marca_cet"));
				marca.addProperty("descricao", rs.getString("descricao").trim());
				listaMarcas.add(marca);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaMarcas;
	}

	public static List<JsonObject> ListarModelos(){
		List<JsonObject> listaModelos = new ArrayList<>();
		String sql = "SELECT * FROM cad_marca ORDER BY descricao";

		try (Connection conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				JsonObject modelo = new JsonObject();
				modelo.addProperty("id_marca", rs.getInt("id_marca"));
				modelo.addProperty("descricao", rs.getString("descricao").trim());
				listaModelos.add(modelo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaModelos;
	}

	public static boolean existeRegistroPorPlaca(String placa) {
		String sql = "SELECT TOP 1 1 FROM muralha.registro_fato_veiculo WHERE placa = ?";
		try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, placa);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next();
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static PassagensRelacionadasDTO ObterPassagensRelacionadas(
		UUID idVeiculoAlvo, 
		String idsLocais,
		int quantidade
	) throws SQLException {
		
		PassagensRelacionadasDTO resultado = new PassagensRelacionadasDTO();
		List<PassagemRelacionada> passagensAnteriores = new ArrayList<>();
		List<PassagemRelacionada> passagensPosteriores = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		
		try {
			conn = Conexao.getConexao();
			
			String sqlAtual = 
				"SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, vtr.codigo_equipamento, " +
				"vtr.nome, vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.velocidade, vtr.id_classe, vtr.classificacao, " +
				"vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, vtr.marca, vtr.modelo, vtr.cor, vtr.tipo, " +
				"a.id AS id_alerta, tao.tipo AS tipo_alerta, a.observacao AS observacao_alerta " +
				"FROM muralha.v_veiculo_tempo_real vtr " +
				"LEFT JOIN muralha.alerta_veiculo av ON av.id_veiculo_tempo_real = vtr.id " +
				"LEFT JOIN muralha.alerta a ON a.id = av.id_alerta " +
				"LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia " +
				"WHERE vtr.id = ?";
			
			ps = conn.prepareStatement(sqlAtual);
			ps.setString(1, idVeiculoAlvo.toString());
			rs = ps.executeQuery();
			
			PassagemRelacionada passagemAtual = null;
			long dataPassagemMillis = 0;
			
			if (rs.next()) {
				dataPassagemMillis = rs.getTimestamp("data").getTime(); 
				
				passagemAtual = new PassagemRelacionada();
				passagemAtual.setId(UUID.fromString(rs.getString("id")));
				passagemAtual.setPlaca(rs.getString("placa"));
				passagemAtual.setDataVeic(new Date(dataPassagemMillis));
				passagemAtual.setIdLocal(rs.getInt("id_local"));
				passagemAtual.setSerieEquipamento(rs.getInt("serie_equipamento"));
				passagemAtual.setCodigoEquipamento(rs.getString("codigo_equipamento"));
				passagemAtual.setDescLocal(rs.getString("nome"));
				passagemAtual.setIdPista(rs.getInt("id_pista"));
				passagemAtual.setFaixa(rs.getInt("faixa"));
				passagemAtual.setLatitude(rs.getDouble("latitude"));
				passagemAtual.setLongitude(rs.getDouble("longitude"));
				passagemAtual.setVelocidade(rs.getInt("velocidade"));
				passagemAtual.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				passagemAtual.setComImagem(rs.getBoolean("com_imagem"));
				passagemAtual.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
				passagemAtual.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				passagemAtual.setMarca(rs.getString("marca"));
				passagemAtual.setModelo(rs.getString("modelo"));
				passagemAtual.setCorPlaca(rs.getString("cor"));
				passagemAtual.setTipoVeiculo(rs.getString("tipo") != null ? rs.getString("tipo").trim() : "");
				passagemAtual.setIdAlerta(rs.getString("id_alerta"));
				passagemAtual.setTipoAlerta(rs.getString("tipo_alerta"));
				passagemAtual.setObservacaoAlerta(rs.getString("observacao_alerta"));
			}
			rs.close();
			ps.close();

			if (passagemAtual == null) {
				throw new SQLException("Passagem não encontrada!");
			}
			
			resultado.setPassagemAtual(passagemAtual);
			
			String sqlAnteriores = 
				"SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, " +
				"vtr.codigo_equipamento, vtr.nome, vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.velocidade, " +
				"vtr.id_classe, vtr.classificacao, vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, " +
				"vtr.marca, vtr.modelo, vtr.cor, vtr.tipo, " +
				"a.id AS id_alerta, tao.tipo AS tipo_alerta, a.observacao AS observacao_alerta " +
				"FROM ( " +
				"   SELECT TOP " + quantidade + " * " +
				"   FROM muralha.v_veiculo_tempo_real " +
				"   WHERE id_local IN (" + idsLocais + ") " +
				"   AND data < ? " +
				"   AND data >= DATEADD(day, -10, ?) " +
				"   ORDER BY data DESC " +
				") vtr " +
				"LEFT JOIN muralha.alerta_veiculo av ON av.id_veiculo_tempo_real = vtr.id " +
				"LEFT JOIN muralha.alerta a ON a.id = av.id_alerta " +
				"LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia";
			
			ps = conn.prepareStatement(sqlAnteriores);
			ps.setTimestamp(1, new Timestamp(dataPassagemMillis));
			ps.setTimestamp(2, new Timestamp(dataPassagemMillis));
			
			rs = ps.executeQuery();
			while (rs.next()) {
				PassagemRelacionada item = new PassagemRelacionada();
				item.setId(UUID.fromString(rs.getString("id")));
				item.setPlaca(rs.getString("placa"));
				item.setDataVeic(rs.getTimestamp("data"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setCodigoEquipamento(rs.getString("codigo_equipamento"));
				item.setDescLocal(rs.getString("nome"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setFaixa(rs.getInt("faixa"));
				item.setLatitude(rs.getDouble("latitude"));
				item.setLongitude(rs.getDouble("longitude"));
				item.setVelocidade(rs.getInt("velocidade"));
				item.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				item.setComImagem(rs.getBoolean("com_imagem"));
				item.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
				item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				item.setMarca(rs.getString("marca"));
				item.setModelo(rs.getString("modelo"));
				item.setCorPlaca(rs.getString("cor"));
				item.setTipoVeiculo(rs.getString("tipo") != null ? rs.getString("tipo").trim() : "");
				item.setIdAlerta(rs.getString("id_alerta"));
				item.setTipoAlerta(rs.getString("tipo_alerta"));
				item.setObservacaoAlerta(rs.getString("observacao_alerta"));
				
				passagensAnteriores.add(item);
			}
			rs.close();
			ps.close();
			
			String sqlPosteriores = 
				"SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, " +
				"vtr.codigo_equipamento, vtr.nome, vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.velocidade, " +
				"vtr.id_classe, vtr.classificacao, vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, " +
				"vtr.marca, vtr.modelo, vtr.cor, vtr.tipo, " +
				"a.id AS id_alerta, tao.tipo AS tipo_alerta, a.observacao AS observacao_alerta " +
				"FROM ( " +
				"   SELECT TOP " + quantidade + " * " +
				"   FROM muralha.v_veiculo_tempo_real " +
				"   WHERE id_local IN (" + idsLocais + ") " +
				"   AND data > ? " +
				"   AND data <= DATEADD(day, 10, ?) " +
				"   ORDER BY data ASC " +
				") vtr " +
				"LEFT JOIN muralha.alerta_veiculo av ON av.id_veiculo_tempo_real = vtr.id " +
				"LEFT JOIN muralha.alerta a ON a.id = av.id_alerta " +
				"LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia";
			
			ps = conn.prepareStatement(sqlPosteriores);
			ps.setTimestamp(1, new Timestamp(dataPassagemMillis));
			ps.setTimestamp(2, new Timestamp(dataPassagemMillis));
			
			rs = ps.executeQuery();
			while (rs.next()) {
				PassagemRelacionada item = new PassagemRelacionada();
				item.setId(UUID.fromString(rs.getString("id")));
				item.setPlaca(rs.getString("placa"));
				item.setDataVeic(rs.getTimestamp("data"));
				item.setIdLocal(rs.getInt("id_local"));
				item.setSerieEquipamento(rs.getInt("serie_equipamento"));
				item.setCodigoEquipamento(rs.getString("codigo_equipamento"));
				item.setDescLocal(rs.getString("nome"));
				item.setIdPista(rs.getInt("id_pista"));
				item.setFaixa(rs.getInt("faixa"));
				item.setLatitude(rs.getDouble("latitude"));
				item.setLongitude(rs.getDouble("longitude"));
				item.setVelocidade(rs.getInt("velocidade"));
				item.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
				item.setComImagem(rs.getBoolean("com_imagem"));
				item.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
				item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				item.setMarca(rs.getString("marca"));
				item.setModelo(rs.getString("modelo"));
				item.setCorPlaca(rs.getString("cor"));
				item.setTipoVeiculo(rs.getString("tipo") != null ? rs.getString("tipo").trim() : "");
				item.setIdAlerta(rs.getString("id_alerta"));
				item.setTipoAlerta(rs.getString("tipo_alerta"));
				item.setObservacaoAlerta(rs.getString("observacao_alerta"));
				
				passagensPosteriores.add(item);
			}
			
			resultado.setPassagensAnteriores(passagensAnteriores);
			resultado.setPassagensPosteriores(passagensPosteriores);
			
		} catch (Exception e) {
			erro = true;
			logger.error("Erro ao obter passagens relacionadas: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null) conn.close();
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
			}
			if (erro) throw new SQLException("Erro ao consultar passagens relacionadas no banco de dados!");
		}
		
		return resultado;
	}
}
