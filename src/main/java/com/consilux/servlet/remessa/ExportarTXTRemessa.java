package com.consilux.servlet.remessa;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.Acesso;
import com.consilux.model.ExportaRemessa;
import com.consilux.model.ExportaRemessaItr;
import com.consilux.model.ItemExportaRemessa;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.Remessa;

/**
 * Servlet implementation class ExportarTXTRemessa
 */
public class ExportarTXTRemessa extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ExportarTXTRemessa.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportarTXTRemessa() {
        super();
    }

    private void raiseAndLogError(String message, Throwable rootCause, HttpServletResponse response) throws IOException {
		
		logger.error(message, rootCause);
		new Mensagem(response).showErro(message, null, true);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new MensagemJS(response).showErro("Usuário não atenticado!");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		String sTipoRemessa = request.getParameter("tipo_remessa");
		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");
		
		if (sTipoRemessa != null && sTipoRemessa.length() == 0 ) {
			new Mensagem(response).showErro("Identificador do tipo-remessa enviado inválido!");
			return;
		}
		else if (sDataIni== null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
		    new Mensagem(response).showErro("Data inicial enviada inválida!");
		    return;
		}
		else if (sDataFim== null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
		    new Mensagem(response).showErro("Data final enviada inválida!");
		    return;
		}
		    
		Date dtIni; 
		Date dtFim;
		
		try {
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" 00:00:00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" 23:59:59");
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	    
	    Map<String,Object> mFiltro = new HashMap<String,Object>();
	    if (!"0".equals(sTipoRemessa))
	    	mFiltro.put("tipo", sTipoRemessa);
	    mFiltro.put("data_ini_infracao", new Timestamp(dtIni.getTime()));
	    mFiltro.put("data_fim_infracao", new Timestamp(dtFim.getTime()));

	    List<Remessa> remessas;
		try {
			remessas = Remessa.buscarRemessaPor(mFiltro);
		}
		catch (Exception e) {
			raiseAndLogError("Erro ao buscar as remessas no BD.", e, response);
			return;
		}
		
	    String nomeArquivoZip = "TXTRemessa.zip";
		ExportaRemessa<?> exportaRemessa = null;
		
		try {
			Class<? extends ExportaRemessa<?>> tipoRemessa = 
				ConfiguracaoProvider.getInstance().getImplementacaoExportaRemesssa();
			
			exportaRemessa = tipoRemessa.newInstance();
		}
		catch (Exception e) {
			raiseAndLogError("Erro ao montar estrutura para exportar remessa.", e, response);
			return;
		}

		ZipOutputStream zip = null;

		// Aqui os itens passaram a validação. Então, abre um arquivo ZIP no buffer de saída.
		zip = criaArquivoSaidaZip(nomeArquivoZip, response);
	    
		try {
		    for (Remessa remessa : remessas) {
				exportaRemessa.setRemessa(remessa);
				StringBuilder sbTXT = new StringBuilder();
				
				// Escreve o cabeçalho da remessa no string builder que representa o TXT.
				String cabecalho = exportaRemessa.getCabecalhoRemessa();
				if (cabecalho != null)
					sbTXT.append(cabecalho + "\r\n");
	
				// Como vamos abrir o iterator (e o seu ResultSet), protege com try-finally.
				try {
					// Cria o iterator, com as imagens (BLOBs).
					ExportaRemessaItr<? extends ItemExportaRemessa> remessaItr = new ExportaRemessaItr<ItemExportaRemessa>((ExportaRemessa<ItemExportaRemessa>) exportaRemessa, false);
	
					for (ItemExportaRemessa item : remessaItr) {
						// Criando uma linha, com o nome e colocando no StringBuilder
						sbTXT.append(item.getLinhaRemessa() + "\r\n");
					}
					
					// Adiciona o conteúdo do TXT (StringBuilder) no ZIP
					String nomeArquivoTxt = exportaRemessa.getNomeArquivoTXT();
					adicArquivoNoZip(zip, nomeArquivoTxt, sbTXT);			
					
				}
				catch (SocketException se) {
					// Erro normal que pode acontecer, quando a conexão é encerrada (normalmente pelo usuario, no browser)
					logger.info("Conexão HTTP finalizada.", se);
				}
				catch (Exception e) {
					// Se ocorreu erro, logar e propagar a exceção.
					raiseAndLogError(e.getMessage(), e, response);
				}
		    }
		}
		finally {
			try {
				exportaRemessa.fecharConexaoItens();
			} 
			catch (SQLException ex) {
				throw new ServletException("Erro ao fechar conexão com o banco.", ex);
			}
			zip.close();
			zip = null;
		}
	}  	  	
	
	private ZipOutputStream criaArquivoSaidaZip(String sArq, HttpServletResponse response) throws ServletException, IOException {
		ZipOutputStream zip;
		
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition","attachment; filename=\"" + sArq + "\"");
		
		zip = new ZipOutputStream(response.getOutputStream());
		
		// Desliga a compressão no ZIP (pois JPG não adianta nada de qualquer maneira).
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(9);		
		
		return zip;
	}
	
	private void adicArquivoNoZip(ZipOutputStream zip, String nome, StringBuilder conteudo)
	throws IOException {
		
       	  zip.putNextEntry(new ZipEntry(nome));
       	  PrintStream ps = new PrintStream(zip); 
       	  ps.print(conteudo.toString());
       	  ps.flush();;
          zip.closeEntry();
          
	}	
	
}
