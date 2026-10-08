package muralha.digital.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "Grafico")
@XmlAccessorType (XmlAccessType.FIELD)
public class Graficos
{	
	private static Logger logger = LogManager.getLogger(Graficos.class);
	
	private List<String> labels;
	
	private List<Grafico> datasets;
	
	public List<String> getLabels() { return labels; }
	public void setLabels(List<String> serieGrafico) { this.labels = serieGrafico; }
	
	public List<Grafico> getDatasets() { return datasets; }
	public void setDatasets(List<Grafico> datasets) { this.datasets = datasets; }
	
	public Graficos()
	{
		super();
	}
	
	public static Graficos ObterDadosVeiculosPorPeriodo(Date dataIni, Date dataFim, Integer idLocal, Integer idTipo) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getVeiculosPorPeriodo ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			ps.setInt(4, idTipo);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	

	public static Graficos ObterDadosVelociadeMediaPorPeriodo(Date dataIni, Date dataFim, Integer idLocal, Integer idTipo) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getVelociadeMediaPorPeriodo ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			ps.setInt(4, idTipo);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos ObterDadosVeiculosPorteVeicular(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getVeiculosPorClassificacao ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	
	public static Graficos ObterDadosFluxoPorteVeicular(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getQtdeFluxoPorPorteVeicular ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 1; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 1; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos ObterDadosDistribOcorrenciaFaixaVel(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getQtdeFluxoFaixaVelocidade ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}

	public static Graficos ObterDadosComparativoPassagensInfracoes(Date dataIni, Date dataFim, Integer idLocal, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getComparativoFluxoInfracao ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			if (municipio == null)
				ps.setNull(4, Types.INTEGER);
			else
				ps.setInt(4, municipio);
			
			if (regiao == null)
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, regiao);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos ObterDadosComparativoAnoAnterior(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";

		try {
			
			sbSQL.append(" EXEC muralha.spu_getComparativoPeriodoAnoAnterior ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}

		return grafico;
	}

	public static Graficos ObterDadosComparativoPrevisaoFuturo(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";

		try {
			
			// TODO - alterar para query correta
			sbSQL.append(" EXEC muralha.spu_getGraficoPrevisaoFutura ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}

		return grafico;
	}
	
	public static Graficos ObterDadosComparativoEvolucaoClassificacao(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";

		try {
			
			sbSQL.append(" EXEC muralha.spu_getEvolucaoPorClassificacao ?, ?, ?, ?, ?, ?");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();

			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}

		return grafico;
	}
	
	public static Graficos ObterDadosDistribuicaoPorFaixa(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getDistribuicaoPorFaixaRolagem ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}

	public static Graficos ObterDadosRankingPorFaixa(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getRankingPorFaixaRolagem ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);

			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos ObterDadosComparativoMesAnterior(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getComparativoPeriodoMesAnterior ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}
				
				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro, e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos FluxoVelMediaPorHorarioMapa(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getFluxoDiaHorarioGrafico ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos FluxoVelMediaPorMinutoMapa(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getFluxoDiaMinutoGrafico ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
	
	public static Graficos InfracoesPorDiaMapa(Date dataIni, Date dataFim, Integer idLocal) throws ConexaoException, SQLException
	{
		Graficos grafico = new Graficos();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getInfracoesDiaGrafico ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(dataIni.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(dataFim.getTime()));
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			List<String> labelsGrafico = new ArrayList<String>();
			List<Grafico> dataset = new ArrayList<Grafico>();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int i = 2; i <= columnCount; i++)
			{
				labelsGrafico.add(rsmd.getColumnName(i));
			}
			
			while (rs.next()) 
			{
				List<Integer> item = new ArrayList<Integer>();
				Grafico graficoItem = new Grafico();

				for (int i = 2; i <= columnCount; i++)
				{
					item.add(rs.getInt(i));
				}

				graficoItem.setLabelDataset(rs.getString("label_dataset"));
				graficoItem.setValores(item);
				dataset.add(graficoItem);
			}
			
			grafico.setLabels(labelsGrafico);
			grafico.setDatasets(dataset);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do gráfico!");
			}
		}
		
		return grafico;
	}
}
