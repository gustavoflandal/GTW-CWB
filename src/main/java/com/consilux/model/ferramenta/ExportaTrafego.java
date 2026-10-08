package com.consilux.model.ferramenta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.conf.ConfiguracaoExportaTrafego;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.lib.DateUtil;
import com.consilux.model.ExportacaoTrafego;
import com.consilux.model.Veiculo;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio responsável por exportar as imagens do sistema.
 * @author raoni
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExportaTrafego implements Job {

	private static Logger logger = Logger.getLogger(ExportaTrafego.class);
	private String codigoEmpresa = null;
	private String codigoTipoEquipamento = null;
	private String tipoEquipamento = null;
	private File diretorioSaida = null;
	private Date forcaData = null;
	
	
	/**
	 * @throws ModelException 
	 */
	public ExportaTrafego() {
		this(null);
	}
	/**
	 * @throws ModelException 
	 */
	public ExportaTrafego(Date data) {
		ConfiguracaoExportaTrafego conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaTrafego();
		this.codigoEmpresa = conf.getCodigoEmpresa(); 
		this.codigoTipoEquipamento = conf.getCodigoTipoEquipamento(); 
		this.tipoEquipamento = conf.getTipoEquipamento();
		this.forcaData = data;
	}
	
	/**
	 * Classe de negócio responsável por exportar trafego do sistema
	 * @throws SQLException 
	 * @throws ConexaoException 
	 * @throws ModelException 
	 * @throws IOException 
	 */
	public ExportacaoTrafego exportarTrafegoParaData(Date data) throws SQLException, ConexaoException, ModelException, IOException {
		ExportacaoTrafego eiRet = null;
		ExportacaoTrafego exportacaoTrafego = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		SimpleDateFormat fmtData = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat fmtHora = new SimpleDateFormat("HHmmss");
		
		if (!this.diretorioSaida.exists() || !this.diretorioSaida.isDirectory())
			throw new ModelException("Diretório de saída ["+diretorioSaida+"] inválido.");

		logger.info("Exportando a data: ["+new SimpleDateFormat("dd/MM/yyyy").format(data)+"]...");
		
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("data_trafego", data);
		mFiltros.put("nao_exportada", null);
		List<ExportacaoTrafego> let = ExportacaoTrafego.buscaExportacaoTrafegoPor(mFiltros);
		
		if (let.size() > 0) {
			logger.warn("Existe uma exportação incompleta, reiniciando...");
			exportacaoTrafego = let.get(0);
		}
		else
			exportacaoTrafego = ExportacaoTrafego.criaExportacaoTrafegoParaData(data);
		
		logger.info("Número de controle da exportação: ["+String.valueOf(exportacaoTrafego.getIdExportacaoTrafego())+"]...");

		Connection conn = Conexao.getConexao();
		try {
			PreparedStatement ps = null;
			ResultSet rs = Veiculo.getTrafego(conn, ps, exportacaoTrafego.getIdExportacaoTrafego());			
			
			File arquivoSaida = new File(this.diretorioSaida + File.separator +  getNomeArquivoTXT(data)); 
				
			fos = new FileOutputStream(arquivoSaida, true);
			pw = new PrintWriter(fos);
			
			Integer i = 0;
			while (rs.next()) {
				pw.write(codigoEmpresa+"|");
				pw.write(fmtData.format(rs.getTimestamp("data")) + "|");
				pw.write(fmtHora.format(rs.getTimestamp("data")) + "|");
				pw.write(rs.getInt("cod_pista") + "|");
				pw.write(rs.getInt("cod_pista_alternativo") + "|");
				pw.write(this.codigoTipoEquipamento+"|");
				pw.write((rs.getString("placa") != null ? rs.getString("placa") : "")  + "|");
				pw.write(rs.getInt("classificacao") + "|");
				pw.write(rs.getInt("comprimento") + "|");
				pw.write(rs.getString("velocidade_ms") + "|");
				pw.write(rs.getInt("ocupacao") + "\r\n");
				i++;
			}
			if (i.intValue() != exportacaoTrafego.getTotalTrafego())
				throw new ModelException("Contagem calculada de tráfego ["+exportacaoTrafego.getTotalTrafego()+"] diferente da contagem real exportada ["+i+"]!");
			else
				pw.flush();
			
			eiRet = exportacaoTrafego;
		}
		finally {
			if (conn != null)
				conn.close();
			if (fos != null)
				fos.close();
		}
		return eiRet;
	}
	private String getNomeArquivoTXT(Date data) throws ModelException {
		String sulfixo = ".txt";
		String prefixo = "SAIT";
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

		return String.format("%s%s%s%s%s", prefixo, this.codigoEmpresa, this.tipoEquipamento, df.format(new Date().getTime()), sulfixo);	
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Date dataTrafego;
		
		try {
			logger.info("Verificando se existe data para ser exportada o trafego...");
			if (this.forcaData == null)
				dataTrafego = buscaDataExportar();
			else
				dataTrafego = this.forcaData;
			
		}
		catch (Exception e) {
			logger.error("Erro ao buscar a data a ser exportada: "+e.getMessage(), e);
			return;
		}
		
		if (dataTrafego == null) {
			logger.info("Não existe nenhuma data para ser exportada.");
			return;
		}
		
		logger.info("A data ["+new SimpleDateFormat("dd/MM/yyyy").format(dataTrafego)+"] ainda não foi exportada totalmente...");
		logger.info("Iniciando processo de exportação...");
		
		try {
			Date dIni = new Date();
			ConfiguracaoExportaTrafego conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaTrafego();
			
			String dir = conf.getDiretorio();
			String cmdLogon = conf.getCmdLogon();
			String cmdLogoff = conf.getCmdLogoff();
			
			logger.info("Exporta Imagens: Executando comando de logon ["+cmdLogon+"]...");
			Funcoes.execCmd(cmdLogon, logger, Level.INFO);
			
			File directory = new File(dir);
			
			if (!directory.isDirectory() || !directory.exists()) {
				logger.error("Erro: O diretório de exportação de tráfego [" + directory.getPath() +  "] não existe.");
				String tempDir = System.getProperty("java.io.tmpdir");
				logger.warn("Exportação Imagens: Tentando gravar no diretório alternativo: '"+tempDir+"'...");
				directory = new File(tempDir);
				if (!directory.isDirectory() || !directory.exists()) {
					logger.fatal("Erro: O diretório de exportação de tráfego [" + directory.getPath() +  "] não existe.");
					return;
				}
			}
			
			this.diretorioSaida = directory;

			do {
				//Se a data passada for depois da data atual...então para.
				if (dataTrafego != null && dataTrafego.after(DateUtil.clearTime(new Date())))
					break;
				ExportacaoTrafego et = exportarTrafegoParaData(dataTrafego);
				et.confirmaExportacao();
				dataTrafego = buscaDataExportar();
			} while (dataTrafego != null);

			logger.info("Exporta Imagens: Executando comando de logoff ["+cmdLogoff+"]...");
			Funcoes.execCmd(cmdLogoff, logger, Level.INFO);
			
			logger.info("Tráfego exportado com sucesso em ["+String.valueOf((new Date().getTime()-dIni.getTime())/1000)+"] segs.");
		} 
		catch (Exception e) {
			logger.error("Erro ao exportar o tráfego: "+e.getMessage(), e);
		}
	}

	/**
	 * @return
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException
	 */
	private Date buscaDataExportar() throws ConexaoException, SQLException, ModelException {
		Date dRet = null;
		StringBuilder sbSQL = new StringBuilder();
		ConfiguracaoExportaTrafego conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaTrafego();
		
		sbSQL.append("SELECT MIN(data) AS data_minima FROM veiculo_estatistica ve (NOLOCK)"); 
		sbSQL.append("	LEFT JOIN exportacao_trafego_arquivo eta (NOLOCK) ON eta.id_arquivo = ve.id_arquivo");
		sbSQL.append("	LEFT JOIN exportacao_trafego et (NOLOCK) ON et.id_exportacao_trafego = eta.id_exportacao_trafego ");
		sbSQL.append("WHERE");
		sbSQL.append("	ve.data > CAST(? AS DATETIME) AND");
		sbSQL.append("	et.data_exportacao IS NULL");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(DateUtil.addDays(new Date(), conf.getDiasRetroativoMaximo()*(-1)).getTime()));

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				dRet = rs.getDate("data_minima");
			}
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return dRet;
	}

	/**
	 * @return the diretorioSaida
	 */
	public File getDiretorioSaida() {
		return diretorioSaida;
	}

	/**
	 * @param diretorioSaida the diretorioSaida to set
	 */
	public void setDiretorioSaida(File diretorioSaida) {
		this.diretorioSaida = diretorioSaida;
	}
	
}