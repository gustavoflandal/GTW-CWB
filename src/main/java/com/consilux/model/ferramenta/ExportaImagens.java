package com.consilux.model.ferramenta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.consilux.conf.ConfiguracaoExportaImagens;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.ExportacaoImagem;
import com.consilux.model.ExportacaoImagemImagem;
import com.consilux.model.Infracao;
import com.consilux.model.InfracaoImagem;
import com.consilux.model.InfracaoObliteracao;
import com.consilux.model.InfracaoRemessa;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.remessa.ExportaRemessaCET;

/**
 * Classe de negócio responsável por exportar as imagens do sistema.
 * @author raoni
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExportaImagens implements Job {

	private static Logger logger = Logger.getLogger(ExportaImagens.class);
	private File diretorioSaida = null;
	
	/**
	 * @throws ModelException 
	 */
	public ExportaImagens() {
	}
	
	/**
	 * Classe de negócio responsável por exportar as imagens do sistema
	 * @throws SQLException 
	 * @throws ConexaoException 
	 * @throws ModelException 
	 * @throws IOException 
	 */
	public ExportacaoImagem exportarImagensParaRemessa(Integer idRemessa) throws SQLException, ConexaoException, ModelException, IOException {
		return exportarImagensParaRemessa(idRemessa, false);
	}
	/**
	 * Classe de negócio responsável por exportar as imagens do sistema
	 * @throws SQLException 
	 * @throws ConexaoException 
	 * @throws ModelException 
	 * @throws IOException 
	 */
	public ExportacaoImagem exportarImagensParaRemessa(Integer idRemessa, Boolean forcarReexportacao) throws SQLException, ConexaoException, ModelException, IOException {
		ExportacaoImagem eiRet = null;
		ExportacaoImagem exportacaoImagem = null;
		
		if (!this.diretorioSaida.exists() || !this.diretorioSaida.isDirectory())
			throw new ModelException("Diretório de saída ["+diretorioSaida+"] inválido.");

		logger.info("Verificando se a remessa: ["+idRemessa+"] já foi exportada...");

		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_remessa", idRemessa);
		
		if (!forcarReexportacao)
			mFiltros.put("nao_exportada", null);

		List<ExportacaoImagem> lei = ExportacaoImagem.buscaExportacaoImagemPor(mFiltros);
		
		if (lei.size() > 0) {
			logger.warn("Reexportando remessa: ["+idRemessa+"]...");
			exportacaoImagem = lei.get(0);
		}
		else {
			logger.info("Remessa ainda não foi exportada, criando registros de controle...");
			exportacaoImagem = ExportacaoImagem.criaExportacaoImagemParaRemessa(idRemessa);
		}
		
		logger.info("Número de controle da exportação: ["+String.valueOf(exportacaoImagem.getIdExportacaoImagem())+"]...");

		List<ExportacaoImagemImagem> lImg = ExportacaoImagemImagem.buscarExportacaoImagemImagemPorIdExportacaoImagem(exportacaoImagem.getIdExportacaoImagem());
		
		Connection conn = Conexao.getConexao();
		try {
			
			for (ExportacaoImagemImagem eii : lImg) {
				VeiculoImagem vi = VeiculoImagem.buscaVeiculoImagemPorIdImagem(eii.getIdImagem());
				byte img[] = vi.getImagem(conn);
				
				Infracao inf = Infracao.buscarInfracaoPorIdVeiculo(vi.getIdVeiculo());
				
				if (inf != null) {
					InfracaoObliteracao io = null;
	
					//AGORA TENTA OBLITERAR INDEPENDENTE SE É OBJETIVA...
//					if (vi.getIdImagem().equals(inf.getIdImagemOBJ())) { //Se é objetiva, então tenta obliterar... 
						io = InfracaoObliteracao.buscaInfracaoObliteracaoPorIdInfracao(inf.getIdInfracao(), vi.getIdImagem());
						if (io != null) 
							img = io.getImagemObliterada();
//					}
					
					InfracaoRemessa ir = InfracaoRemessa.buscarItemRemessaPorInfracao(inf.getIdInfracao());
					if (ir != null) {
						InfracaoImagem ii = InfracaoImagem.buscarInfracaoImagemPorIdInfracao(inf.getIdInfracao());
						Remessa r = Remessa.buscarRemessaPorId(ir.getIdRemessa());
						String nomeArq =  getNomeArquivoImagemCET(r, ir, ii, vi);
						if (nomeArq != null)
							exportarImagem(img, nomeArq);
					}
				}
			}
			
			eiRet = exportacaoImagem;
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return eiRet;
	}
	private String getNomeArquivoImagemCET(Remessa r, InfracaoRemessa ir, InfracaoImagem ii, VeiculoImagem vi) throws ModelException {
		String sulfixo = "";

		if (vi.getIdImagem().equals(ii.getIdImagemObj()))
			sulfixo = "det";
		else if (r.getIdProcesso().equals(EtapaProcesso.REMESSA_CONVERSAO_PROIBIDA.getId())) {
			if (vi.getIdImagem().equals(ii.getIdImagemPan1()))
				sulfixo = "cv1";
			else if (vi.getIdImagem().equals(ii.getIdImagemPan2()))
				sulfixo = "cv2";
		}
		else if (vi.getIdImagem().equals(ii.getIdImagemPan1()))
			sulfixo = "pan";
		else {
			logger.warn("Para exportação da CET, não é possível exportar esta imagem: "+vi.getIdImagem());
//			throw new ModelException("TESTE");
			return null;
		}
		
		return String.format("%2s-%2s-%06d-%1d-%s.jpg", r.getTipo(), ir.getSerie(), ir.getAuto(), ExportaRemessaCET.calcDACAuto(r.getTipo(), ir.getSerie(), ir.getAuto()), sulfixo);	
	}

	private void exportarImagem(byte[] img, String nomeArquivoImagem) throws IOException {
		File arquivoSaida = new File(this.diretorioSaida.getAbsolutePath() + File.separatorChar + nomeArquivoImagem);
		
		if (arquivoSaida.exists())
			arquivoSaida.delete();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(img);
		FileOutputStream fos = new FileOutputStream(arquivoSaida);
		try {
			Funcoes.copyBytes(bais, fos);
		}
		finally {
			fos.close();
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Integer idRemessa;
		
		try {
			logger.info("Verificando se existe remessa para ser exportada as imagens...");
			idRemessa = buscaRemessaExportar();
			
		}
		catch (Exception e) {
			logger.error("Erro ao buscar remessa a ser exportada: "+e.getMessage(), e);
			return;
		}
		
		if (idRemessa == null) {
			logger.info("Não existe nenhuma remessa para ser exportada.");
			return;
		}
		
		logger.info("A remessa ["+idRemessa+"] ainda não foi exportada...");
		logger.info("Iniciando processo de exportação...");
		
		try {
			Date dIni = new Date();
			ConfiguracaoExportaImagens conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaImagens();
			
			String dir = conf.getDiretorio();
			String cmdLogon = conf.getCmdLogon();
			String cmdLogoff = conf.getCmdLogoff();
			
			logger.info("Exporta Imagens: Executando comando de logon ["+cmdLogon+"]...");
			Funcoes.execCmd(cmdLogon, logger, Level.INFO);
			
			File directory = new File(dir);
			
			if (!directory.isDirectory() || !directory.exists()) {
				logger.error("Erro: O diretório de exportação de imagens [" + directory.getPath() +  "] não existe.");
				String tempDir = System.getProperty("java.io.tmpdir");
				logger.info("Exportação Imagens: Tentando gravar no diretório alternativo: '"+tempDir+"'...");
				directory = new File(tempDir);
				if (!directory.isDirectory() || !directory.exists()) {
					logger.fatal("Erro: O diretório de exportação de imagens [" + directory.getPath() +  "] não existe.");
					return;
				}
			}
			
			this.diretorioSaida = directory;

			do {
				ExportacaoImagem ei = exportarImagensParaRemessa(idRemessa);
				ei.confirmaExportacao();
				idRemessa = buscaRemessaParaExportar(idRemessa);
			} while (idRemessa != null);

			logger.info("Exporta Imagens: Executando comando de logoff ["+cmdLogoff+"]...");
			Funcoes.execCmd(cmdLogoff, logger, Level.INFO);
			
			logger.info("Remessa exportada com sucesso em ["+String.valueOf((new Date().getTime()-dIni.getTime())/1000)+"] segs.");
		} 
		catch (Exception e) {
			logger.error("Erro ao exportar a remessa: "+e.getMessage(), e);
		}
	}

	private Integer buscaRemessaExportar() throws ConexaoException, SQLException, ModelException {
		return buscaRemessaParaExportar(0);
	}
	
	private Integer buscaRemessaParaExportar(Integer ultimaRemessaVerificada) throws ConexaoException, SQLException, ModelException {
		Integer iRet = null;
		StringBuilder sbSQL = new StringBuilder();
		
		ConfiguracaoExportaImagens conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaImagens();
		
		sbSQL.append("SELECT MIN(r.id_remessa)");
		sbSQL.append("	FROM remessa r");
		sbSQL.append("	JOIN infracao_remessa ir ON ir.id_remessa = r.id_remessa ");
		sbSQL.append("	JOIN infracao_imagem ii ON ii.id_infracao = ir.id_infracao ");
		sbSQL.append("	LEFT JOIN exportacao_imagem_imagem eii ON eii.id_imagem = ii.id_imagem_obj");
		sbSQL.append("	LEFT JOIN exportacao_imagem ei ON ei.id_exportacao_imagem = eii.id_exportacao_imagem AND ei.data_exportacao IS NOT NULL ");
		sbSQL.append("WHERE");
		sbSQL.append("	ei.id_exportacao_imagem IS NULL AND");
		sbSQL.append("	CAST(r.data AS DATE) BETWEEN CAST(GETDATE()-"+String.valueOf(conf.getDiasRetroativoMaximo())+" AS DATE) AND CAST(GETDATE()-"+String.valueOf(conf.getDiasRetroativoMinimo())+" AS DATE) AND ");
		sbSQL.append("	r.id_remessa > ?");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, ultimaRemessaVerificada);

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				iRet = rs.getInt(1) > 0 ? rs.getInt(1) : null;
			}
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return iRet;
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