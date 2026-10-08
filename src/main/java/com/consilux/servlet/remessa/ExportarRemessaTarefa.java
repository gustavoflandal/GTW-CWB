package com.consilux.servlet.remessa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.Conexao;
import com.consilux.model.AcessoArquivosFTP;
import com.consilux.model.AcessoStorageInterface;
import com.consilux.model.AcessoStorageProvider;
import com.consilux.model.ChaveValor;
import com.consilux.model.ItemExportaRemessa;
import com.consilux.model.ItemExportaRemessaCET;
import com.consilux.model.LogRemessa;
import com.consilux.model.Remessa;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.remessa.ExportaRemessaCET;

public class ExportarRemessaTarefa extends Thread {
	private final static Logger logger = Logger.getLogger(ExportarRemessaTarefa.class);
	
	public enum TipoExportaRemessa { MovimentoLote, LoteValidado }
	
	private Remessa remessa;
	private TipoExportaRemessa tipo;
	private String diretorio_destino;
	private String diretorio_destino_alt;
	private String desc_lote;
	private LogRemessa logRemessa = new LogRemessa();
	
	public ExportarRemessaTarefa(Remessa remessa, TipoExportaRemessa tipo) {
		this.remessa = remessa;
		this.tipo = tipo;
		try {
			if (remessa.getIdEnquadramento() == 99999)
				this.diretorio_destino = ChaveValor.obterMapa().get("diretorio_apait_vm                                ").trim();
			else
				this.diretorio_destino = ChaveValor.obterMapa().get("diretorio_apait                                   ").trim();
			this.diretorio_destino_alt = ChaveValor.obterMapa().get("diretorio_movimento_lote                          ").trim();
		}
		catch(Exception e) {
			if (remessa.getIdEnquadramento() == 99999)
				this.diretorio_destino = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_apait_vm");
			else
				this.diretorio_destino = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_apait");
			this.diretorio_destino_alt = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_movimento_lote");
		}
		this.desc_lote = this.remessa.getDescricao();
	}
	
	@Override
	public void run() {
		logger.info("INICIANDO PROCESSO EXPORTACAO MANUAL [" + desc_lote + "]");
		logger.info("DIRETORIO DESTINO = " + diretorio_destino);

		ExportaRemessaCET exportaRemessa = null;
		
		Integer idRemessa = remessa.getIdRemessa();
		Integer codigoExterno = remessa.getCodigoExterno();
		String tipoRemessa = remessa.getTipo();
		
		logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "INICIANDO PROCESSO EXPORTACAO MANUAL [" + desc_lote + "]");
		
		logger.info("PROCESSO DE EXPORTAÇÂO DE LOTE MANUAL. REVISANDO INCONSISTENCIAS DO LOTE[" + desc_lote + "]");
		AnalisadorDeConsistencia(idRemessa, codigoExterno, tipoRemessa);

		try {
			if (!remessa.getIntegridade()) {
				logger.error("Movimento Inválido ou Reprovado: [" + desc_lote + "]");
				return;	
			}
			 
			//Nova classe de envio ao APAIT comunicação com FTP - Não sabemos a versão oficial da AcessoFTP.java
			AcessoArquivosFTP acessoFTP = new AcessoArquivosFTP();
			AcessoArquivosFTP acessoFTP_APAIT = new AcessoArquivosFTP(diretorio_destino);
			
			AcessoStorageInterface acesso_storage = AcessoStorageProvider.ObterInterface();
			//@SuppressWarnings("unused") 
			//AcessoStorageInterface acesso_storage_int = AcessoStorageProvider.ObterInterface(diretorio_destino);
			
			String diretorio_destino_local;
			if(diretorio_destino.contains("@"))
				diretorio_destino_local = diretorio_destino.substring(0, diretorio_destino.indexOf('@'));
			else
				diretorio_destino_local = diretorio_destino;
			
			String diretorio_destino_alt_local;
			if(diretorio_destino_alt.contains("@"))
				diretorio_destino_alt_local = diretorio_destino_alt.substring(0, diretorio_destino_alt.indexOf('@'));
			else
				diretorio_destino_alt_local = diretorio_destino_alt;
			
			exportaRemessa = new ExportaRemessaCET();
			exportaRemessa.setRemessa(remessa);
			exportaRemessa.setComObliteracao(true);
			exportaRemessa.setTipo(tipo.ordinal());
			
			exportaRemessa.validarRemessa();
			List<ItemExportaRemessa> itens_exporta = exportaRemessa
					.getlistaItens();
			
			StringBuilder sbTXT = new StringBuilder();
			String nova_linha = System.getProperty("line.separator");
			String cabecalho = exportaRemessa.getCabecalhoRemessa();
			if (cabecalho != null)
				sbTXT.append(cabecalho + nova_linha);
			
			boolean exportaImagem = true;
			
			for (ItemExportaRemessa item : itens_exporta) {
				ItemExportaRemessaCET item_cet = (ItemExportaRemessaCET)item;
				
				if (item_cet.getSequenciaImagemLocal() == 0) {
					sbTXT.append(item_cet.getLinhaRemessa() + nova_linha);
					
					if(item_cet.getIdInconsistenciaValidacao() == 0) {
						// COPIA A IMAGEM PARA O DIRETORIO DE IMAGENS
						
						VeiculoImagem vi = VeiculoImagem.buscaVeiculoImagemPorIdImagem(item_cet.getIdImagem());
						
//						byte[] dados = acesso_storage.ObterArquivo(vi.getCaminho());
						byte[] dados = acessoFTP.ObterArquivoAPAIT(vi.getCaminho(), idRemessa);
						
						//Verifica falha na leitura do arquivo
						if(dados == null)
						{
							exportaImagem = false;
							break;
						}
						
						String arquivo_dest = 
								diretorio_destino_local + 
								acesso_storage.Separador() + 
								"IMAGENS" + 
								acesso_storage.Separador() + 
								FilenameUtils.getName(vi.getCaminho());
						
						logger.info("EXPORTANDO [" + desc_lote + "] IMAGEM [" + arquivo_dest + "]");
						
						
//						acesso_storage_int.EnviarArquivo(arquivo_dest, dados);
						//Verifica falha no envio do arquivo
						if(!acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, dados, idRemessa))
						{
							exportaImagem = false;
							break;
						}
						
					}
				}
			}
			
			//A exportação de imagem falhou, então não envia o Lote para o APAIT 
			//e Não grava no banco sucesso na exportação
			if(exportaImagem)
			{
				// COPIA O ARQUIVO DE MOVIMENTO DE LOTE
				String arquivo_movimento = exportaRemessa.getNomeArquivoTXT();
				String arquivo_dest = 
						diretorio_destino_local + 
						acesso_storage.Separador() + 
						"LOTES" + 
						acesso_storage.Separador() + 
						arquivo_movimento;
				String arquivo_dest_alt = 
						diretorio_destino_alt_local +
						acesso_storage.Separador() +
						arquivo_movimento.substring(2, 4) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(11, 15) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(5, 7) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(7, 9) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(9, 11) +
						acesso_storage.Separador() + 
						arquivo_movimento;
				
				logger.info("EXPORTANDO [" + desc_lote + "] MOVIMENTO [" + arquivo_movimento + "] EM [" + arquivo_dest + "] E [" + arquivo_dest_alt + "]");
				byte[] bytes_movimento = sbTXT.toString().getBytes();
//				acesso_storage_int.EnviarArquivo(arquivo_dest, bytes_movimento);
//				acesso_storage.EnviarArquivo(arquivo_dest_alt, bytes_movimento);
//				acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, bytes_movimento, idRemessa);
//				acessoFTP.EnviarArquivoAPAIT(arquivo_dest_alt, bytes_movimento, idRemessa);
				
				if (acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, bytes_movimento, idRemessa) && 
						acessoFTP.EnviarArquivoAPAIT(arquivo_dest_alt, bytes_movimento, idRemessa)) {
					
					remessa.AtualizarDataConfirmacao();
					logger.info("FIM PROCESSO EXPORTACAO [" + desc_lote + "]");
					logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "FIM PROCESSO EXPORTACAO [" + desc_lote + "]");					
				
				} else {
					
					logger.error("Não foi possível enviar Movimento de Lote [" + desc_lote + "]");
					logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "Não foi possível enviar Movimento de Lote [" + desc_lote + "]");
					
				}

			}
		}
		catch (Exception e) 
		{
			logger.error("Não foi possível enviar Movimento de Lote [" + desc_lote + "]", e);
			logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "Não foi possível enviar Movimento de Lote [" + desc_lote + "]" + e.getMessage());
		}

		finally{
			try {
				exportaRemessa.fecharConexaoItens();
				logger.info("Fechando Conexão de Banco de Dados da classe ExportaRemessaCET. RM: [" + desc_lote + "]");
			} catch (SQLException e) {
				logger.error("Erro ao Fechar Conexão com Banco de Dados. RM: [" + desc_lote + "]", e);
				logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "Erro ao Fechar Conexão com Banco de Dados. RM: [" + desc_lote + "]" + e.getMessage());
			}
		}
	}
	
	public void EnviarAPAITAutomatico() 
	{
		logger.info("Iniciando Processo de Exportação Automático [" + desc_lote + "]");
	
		ExportaRemessaCET exportaRemessa = null;
		
		Integer idRemessa = remessa.getIdRemessa();
		Integer codigoExterno = remessa.getCodigoExterno();
		String tipoRemessa = remessa.getTipo();
		
		logRemessa.insereLogRemessa(idRemessa, "Exportação Automatica APAIT", "Iniciando Processo de Exportação Automático [" + desc_lote + "]");
		
		logger.info("PROCESSO DE EXPORTAÇÂO DE LOTE AUTOMATICO. REVISANDO INCONSISTENCIAS DO LOTE[" + desc_lote + "]");
		AnalisadorDeConsistencia(idRemessa, codigoExterno, tipoRemessa);

		try {
			if (!remessa.getIntegridade()) {
				logger.error("Movimento Inválido ou Reprovado: [" + desc_lote + "]");
				return ;	
			}
			
			//Nova classe de envio ao APAIT comunicação com FTP - Não sabemos a versão oficial da AcessoFTP.java
			AcessoArquivosFTP acessoFTP = new AcessoArquivosFTP();
			AcessoArquivosFTP acessoFTP_APAIT = new AcessoArquivosFTP(diretorio_destino);
			
			
			AcessoStorageInterface acesso_storage = AcessoStorageProvider.ObterInterface();
			//@SuppressWarnings("unused")
			//AcessoStorageInterface acesso_storage_int = AcessoStorageProvider
			//		.ObterInterface(diretorio_destino);
			
			String diretorio_destino_local;
			if(diretorio_destino.contains("@"))
				diretorio_destino_local = diretorio_destino.substring(0, diretorio_destino.indexOf('@'));
			else
				diretorio_destino_local = diretorio_destino;
			
			String diretorio_destino_alt_local;
			if(diretorio_destino_alt.contains("@"))
				diretorio_destino_alt_local = diretorio_destino_alt.substring(0, diretorio_destino_alt.indexOf('@'));
			else
				diretorio_destino_alt_local = diretorio_destino_alt;
			
			exportaRemessa = new ExportaRemessaCET();
			exportaRemessa.setRemessa(remessa);
			exportaRemessa.setComObliteracao(true);
			exportaRemessa.setTipo(tipo.ordinal());
			
			exportaRemessa.validarRemessa();
			List<ItemExportaRemessa> itens_exporta = exportaRemessa
					.getlistaItens();
			
			StringBuilder sbTXT = new StringBuilder();
			String nova_linha = System.getProperty("line.separator");
			String cabecalho = exportaRemessa.getCabecalhoRemessa();
			if (cabecalho != null)
				sbTXT.append(cabecalho + nova_linha);
			
			boolean exportaImagem = true;
			
			for (ItemExportaRemessa item : itens_exporta) {
				ItemExportaRemessaCET item_cet = (ItemExportaRemessaCET)item;
				
				if (item_cet.getSequenciaImagemLocal() == 0) {
					sbTXT.append(item_cet.getLinhaRemessa() + nova_linha);
					
					if(item_cet.getIdInconsistenciaValidacao() == 0) {
						// COPIA A IMAGEM PARA O DIRETORIO DE IMAGENS
						
						VeiculoImagem vi = VeiculoImagem.buscaVeiculoImagemPorIdImagem(item_cet.getIdImagem());
						
						byte[] dados = acessoFTP.ObterArquivoAPAIT(vi.getCaminho(), idRemessa);
						//Verifica falha na leitura do arquivo
						if(dados == null)
						{
							exportaImagem = false;
							break;
						}
						
						String arquivo_dest = 
								diretorio_destino_local + 
								acesso_storage.Separador() + 
								"IMAGENS" + 
								acesso_storage.Separador() + 
								FilenameUtils.getName(vi.getCaminho());
						
						logger.info("EXPORTANDO [" + desc_lote + "] IMAGEM [" + arquivo_dest + "]");
						
						//Verifica falha no envio do arquivo
						if(!acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, dados, idRemessa))
						{
							exportaImagem = false;
							break;
						}
					}
				}
			}
			
			//A exportação de imagem falhou, então não envia o Lote para o APAIT 
			//e Não grava no banco sucesso na exportação
			if(exportaImagem)
			{
				// COPIA O ARQUIVO DE MOVIMENTO DE LOTE
				String arquivo_movimento = exportaRemessa.getNomeArquivoTXT();
				String arquivo_dest = 
						diretorio_destino_local + 
						acesso_storage.Separador() + 
						"LOTES" + 
						acesso_storage.Separador() + 
						arquivo_movimento;
				String arquivo_dest_alt = 
						diretorio_destino_alt_local +
						acesso_storage.Separador() +
						arquivo_movimento.substring(2, 4) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(10, 14) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(4, 6) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(6, 8) +
						acesso_storage.Separador() +
						arquivo_movimento.substring(8, 10) +
						acesso_storage.Separador() + 
						arquivo_movimento;
				
				logger.info("EXPORTANDO [" + desc_lote + "] MOVIMENTO [" + arquivo_movimento + "] EM [" + arquivo_dest + "] E [" + arquivo_dest_alt + "]");
				byte[] bytes_movimento = sbTXT.toString().getBytes();
				
//				acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, bytes_movimento, idRemessa);
//				acessoFTP.EnviarArquivoAPAIT(arquivo_dest_alt, bytes_movimento, idRemessa);
//				
//				remessa.AtualizarDataConfirmacao();
//				logger.info("FIM PROCESSO EXPORTACAO [" + desc_lote + "]");
//				logRemessa.insereLogRemessa(idRemessa, "Exportação Automatica APAIT", "FIM PROCESSO EXPORTACAO [" + desc_lote + "]");
				
				if (acessoFTP_APAIT.EnviarArquivoAPAIT(arquivo_dest, bytes_movimento, idRemessa) && 
						acessoFTP.EnviarArquivoAPAIT(arquivo_dest_alt, bytes_movimento, idRemessa)) {
					
					remessa.AtualizarDataConfirmacao();
					logger.info("FIM PROCESSO EXPORTACAO [" + desc_lote + "]");
					logRemessa.insereLogRemessa(idRemessa, "Exportação Automatica APAIT", "FIM PROCESSO EXPORTACAO [" + desc_lote + "]");					
				
				} else {
					
					logger.error("Não foi possível enviar Movimento de Lote [" + desc_lote + "]");
					logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "Não foi possível enviar Movimento de Lote [" + desc_lote + "]");
					
				}
				
			}
		} 
		catch (Exception e) 
		{
			logger.error("Não foi possível enviar Movimento de Lote [" + desc_lote + "]", e);
			logRemessa.insereLogRemessa(idRemessa, "Exportação Automatica APAIT", "Não foi possível enviar Movimento de Lote [" + desc_lote + "]" + e.getMessage());
			
		}
		finally{
			try {
				exportaRemessa.fecharConexaoItens();
				logger.info("Fechando Conexão de Banco de Dados da classe ExportaRemessaCET. RM: [" + desc_lote + "]");
			} catch (SQLException e) {
				logger.error("Erro ao Fechar Conexão com Banco de Dados. RM: [" + desc_lote + "]", e);
				logRemessa.insereLogRemessa(idRemessa, "Exportação Automatica APAIT", "Erro ao Fechar Conexão com Banco de Dados. RM: [" + desc_lote + "]"+ e.getMessage());
			}
		}

	}
	
	//Luiz Amaral
	//05/11/2015
	//Objetivo: Analisar as infrações da remessa e atualizar os dados incoerentes de inconsistencia
	//Tempo = 0 (Processamento Automatico) --> inconsistencia da Movimento Importação deve ser a mesma da infração
	public void AnalisadorDeConsistencia(Integer idRemessa, Integer codigoExterno, String tipo)
	{
		
		StringBuilder sbSQL = new StringBuilder();
		Integer qtdeRegistrosAfetados = 0;

		sbSQL.append("select  ");
		sbSQL.append("	ip.id_infracao_processo, ");
		sbSQL.append("	i.id_infracao, ");
		sbSQL.append("	i.data, ");
		sbSQL.append("  i.placa, ");
		sbSQL.append("	r.id_remessa, ");
		sbSQL.append("	r.codigo_externo, ");
		sbSQL.append("	r.tipo, ");
		sbSQL.append("	i.id_inconsistencia, ");
		sbSQL.append("	ip.id_inconsistencia as id_inconsistencia_ip, ");
		sbSQL.append("  ipc.id_inconsistencia as id_inconsistencia_ipc, ");
		sbSQL.append("	mi.id_inconsistencia as id_inconsistencia_mi ");
		sbSQL.append("from infracao i (NOLOCK) ");
		sbSQL.append("	inner join infracao_remessa ir (NOLOCK) ");
		sbSQL.append("		on ir.id_infracao = i.id_infracao ");
		sbSQL.append("	inner join remessa r (NOLOCK) ");
		sbSQL.append("		on r.id_remessa = ir.id_remessa ");
		sbSQL.append("	inner join movimento_importacao mi (NOLOCK) ");
//		sbSQL.append("		on mi.id_enquadramento = i.id_enquadramento ");
//		sbSQL.append("		and mi.id_movimento = r.codigo_externo ");
		sbSQL.append("      on r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("		and mi.sequencia = ir.sequencia ");
		sbSQL.append("  inner join infracao_processo_concluido ipc (NOLOCK) ");
		sbSQL.append("       on ipc.id_infracao = i.id_infracao ");
		sbSQL.append("	inner join ( ");
		sbSQL.append("				select  ");
		sbSQL.append("					max(ipx2.id_infracao_processo) as id_infracao_processo, ");
		sbSQL.append("					ipx2.id_infracao, ");
		sbSQL.append("					ipx2.id_inconsistencia, ");
		sbSQL.append("					ipx2.data ");
		sbSQL.append("				from infracao_processo ipx2 (NOLOCK) ");
		sbSQL.append("					where ipx2.id_infracao_processo = ( ");
		sbSQL.append("														select  ");
		sbSQL.append("															max(ipx.id_infracao_processo) as id_infracao_processo ");
		sbSQL.append("														from infracao_processo ipx (NOLOCK) ");
		sbSQL.append("														where ipx2.id_infracao = ipx.id_infracao ");
		sbSQL.append("														) ");
		sbSQL.append("					and ipx2.tempo = 0	 ");
		sbSQL.append("					and ipx2.tempo_cliente IS NULL ");
		sbSQL.append("				group by  ");
		sbSQL.append("					ipx2.id_infracao, ");
		sbSQL.append("					ipx2.id_inconsistencia, ");
		sbSQL.append("					ipx2.data ");	
		sbSQL.append("	) as ip ");
		sbSQL.append("	ON i.id_infracao  = ip.id_infracao ");
		
		sbSQL.append("	where ");
		sbSQL.append("		  i.id_inconsistencia <> mi.id_inconsistencia ");
		sbSQL.append("		  and r.id_remessa = ? ");
		sbSQL.append("		  and codigo_externo = ? ");
		sbSQL.append("		  and tipo = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setLong(1, idRemessa);
			ps.setLong(2,  codigoExterno);
			ps.setString(3, tipo);
			
			rs = ps.executeQuery();
 
			while (rs.next()) 
			{
				
				long idInfracaoProcesso 	= rs.getLong("id_infracao_processo");
				long idInfracao 			= rs.getLong("id_infracao");
				String placa				= rs.getString("placa");
				Integer inconsistencia 		= rs.getInt("id_inconsistencia");
				Integer inconsistenciaIP 	= rs.getInt("id_inconsistencia_ip");
				Integer inconsistenciaIPC 	= rs.getInt("id_inconsistencia_ipc");
				Integer inconsistenciaMI 	= rs.getInt("id_inconsistencia_mi");
				
				String motivo = "Alteração de inconsistencia";
				String contextoAnt = "Contexto Anterior: idRemessa: " 	+ idRemessa +
									 ", idInfracao: " 					+ idInfracao +
									 ", idInfracaoProcesso: " 			+ idInfracaoProcesso +
									 ", placa: " 						+ placa +
									 ", inconsistencia: " 				+ inconsistencia +
									 ", inconistenciaIP: " 				+ inconsistenciaIP +
									 ", inconsistenciaMI: "				+ inconsistenciaMI +
									 ", inconsistenciaIPC: " 			+ inconsistenciaIPC;
				
				
				//Alterando consistencia da tabela infração
				try {
					
					PreparedStatement psInf = null;
					psInf = conn.prepareStatement("UPDATE infracao SET id_inconsistencia = ? WHERE id_infracao = ?");
					
					psInf.setInt (1, inconsistenciaMI);
					psInf.setLong(2, idInfracao);
					psInf.executeUpdate();
					if (psInf != null)
						psInf.close();
				}
				catch(Exception e){
					logger.error("AnalisadorDeConsistencia --> Falha no UPDATE da tabela infração. IdRemessa: " + idRemessa + ", idInfracao: " + idInfracao, e);
				}
				

				//Alterando consistencia da tabela infracao_processo
				try {
					
					PreparedStatement psInfProc = null;
					psInfProc = conn.prepareStatement("UPDATE infracao_processo SET id_inconsistencia = ? WHERE id_infracao = ? and id_infracao_processo = ?");

					psInfProc.setInt (1, inconsistenciaMI);
					psInfProc.setLong(2, idInfracao);
					psInfProc.setLong(3, idInfracaoProcesso);
					psInfProc.executeUpdate();
					if (psInfProc != null)
						psInfProc.close();
				}
				catch(Exception e){
					logger.error("AnalisadorDeConsistencia --> Falha no UPDATE da tabela infracao_Processo. IdRemessa: " + idRemessa + ", idInfracao: " + idInfracao + ", idInfracaoProcesso: " + idInfracaoProcesso, e);
				}

				//Alterando consistencia da tabela infracao_processo_concluido
				try {
					
					PreparedStatement psInfProcConc = null;
					psInfProcConc = conn.prepareStatement("UPDATE infracao_processo_concluido SET id_inconsistencia = ? WHERE id_infracao = ? ");

					psInfProcConc.setInt (1, inconsistenciaMI);
					psInfProcConc.setLong(2, idInfracao);
					psInfProcConc.executeUpdate();
					if (psInfProcConc != null)
						psInfProcConc.close();
				}
				catch(Exception e){
					logger.error("AnalisadorDeConsistencia --> Falha no UPDATE da tabela infracao_Processo_concluido. IdRemessa: " + idRemessa + ", idInfracao: " + idInfracao, e);
				}
				
				//Inserindo registro de LOG de alteração da base
				try{
					StringBuilder sbSQLog = new StringBuilder();
					
					sbSQLog.append(" insert into log_analisador_remessa  ");
					sbSQLog.append(" (id_remessa, motivo, contexto_anterior, data_gravacao) ");
					sbSQLog.append(" values(?, ?, ?, ?) ");
					
					
					PreparedStatement psLog = null;
					psLog = conn.prepareStatement(sbSQLog.toString());
					Timestamp dtNow = new Timestamp(System.currentTimeMillis()); 
					
					psLog.setInt(1, idRemessa);
					psLog.setString(2, motivo);
					psLog.setString(3, contextoAnt);
					psLog.setTimestamp(4, new java.sql.Timestamp(dtNow.getTime()));
					
					psLog.executeUpdate();
				}
				catch(Exception e){
					logger.error("AnalisadorDeConsistencia --> Falha no INSERT da tabela log_analisador_remessa. IdRemessa: " + idRemessa + ", idInfracao: " + idInfracao, e);
				}
				
				//Contador de registros afetados
				qtdeRegistrosAfetados++;
			}
			
			if(qtdeRegistrosAfetados > 0)
				logger.info("AnalisadorDeConsistencia executado com sucesso --> Qtde Registros Afetados:" + qtdeRegistrosAfetados + ", idRemessa: " + idRemessa);
			else
				logger.info("AnalisadorDeConsistencia executado com sucesso --> Sem Registros incoerentes , idRemessa: " + idRemessa);
		} 
		catch (Exception e) 
		{
			logger.error("AnalisadorDeConsistencia --> Falha ao analisar a Consistencia das infrações. IdRemessa: [" + idRemessa + "] ", e);
		}
		
		finally 
		{
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}catch(Exception e) {
				logger.error("AnalisadorDeConsistencia --> Falha ao fechar conexão com banco de dados!", e);
			}
		}
	}
}
