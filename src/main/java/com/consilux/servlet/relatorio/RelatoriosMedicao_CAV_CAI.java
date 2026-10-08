package com.consilux.servlet.relatorio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.AcessoArquivosFTP;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.beans.GrupoBean;
import com.consilux.model.exception.ModelException;

/**
 * Servlet para a geração de todos os relatórios de Medição - Empacotar ZIP com relatórios do CAV
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 10/11/2014
 */
public class RelatoriosMedicao_CAV_CAI extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatoriosMedicao_CAV_CAI.class);
	List<String> nomesRel = new ArrayList<String>();
	private AcessoArquivosFTP acessoFTP = new AcessoArquivosFTP();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		final Acesso acessoUsuario = new Acesso(request, response, true);

		if (!acessoUsuario.verificaAcesso())return;
		
		int mes = 0;  
		int ano = 0;  
		
		String mesGeracao = request.getParameter("mes");
		String anoGeracao = request.getParameter("ano");
		
		String relatorio = request.getParameter("selRelatorio");
		relatorio = relatorio != null ? relatorio : "";
		
		String chkTodos = relatorio.equals("chkTodos") ? relatorio : null;
		String chk4Minutos = relatorio.equals("chk4Minutos") ? relatorio : null;
		String chkFuncionamentoEstatico = relatorio.equals("chkFuncionamentoEstatico") ? relatorio : null;
		String chkInfracoesFixo = relatorio.equals("chkInfracoesFixo") ? relatorio : null;
		String chkInfracoesEstatico = relatorio.equals("chkInfracoesEstatico") ? relatorio : null;
		String chkFluxoFixo = relatorio.equals("chkFluxoFixo") ? relatorio : null;
		String chkFluxoEstatico = relatorio.equals("chkFluxoEstatico") ? relatorio : null;
		String chkAtraso = relatorio.equals("chkAtraso") ? relatorio : null;
		String chkErros = relatorio.equals("chkErros") ? relatorio : null;
		String chkInfracoesConsistFixo = relatorio.equals("chkInfracoesConsistFixo") ? relatorio : null;
		String chkInfracoesConsistEstatico = relatorio.equals("chkInfracoesConsistEstatico") ? relatorio : null;
		String chkLotesReprovados = relatorio.equals("chkLotesReprovados") ? relatorio : null;
		
		String chkPMESPEnvioPlacas = relatorio.equals("chkPMESPEnvioPlacas") ? relatorio : null;

		//Validações Necessárias para montar relatório
		try {
			
			if(mesGeracao == "" || anoGeracao == ""){
				new Mensagem(response).showErro("Favor informar Mês e Ano para importação dos relatórios.", "javascript:window.close();");
				return; 				
			}
			
			if (!isDigit(mesGeracao) || !isDigit(anoGeracao)) {
				new Mensagem(response).showErro("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
				return; 
			}
				
			mes = Integer.parseInt(mesGeracao);  
			ano = Integer.parseInt(anoGeracao);
			
			if(mes < 1 || mes > 12){
				new Mensagem(response).showErro("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
				return; 
			}
			if(ano < 1900 || ano > 2050){
				new Mensagem(response).showErro("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
				return; 
			}			
			
			//Caso o usuário não escolha nenhum dos itens
			if(chkTodos == null && chk4Minutos == null &&	
			   chkFuncionamentoEstatico == null && chkInfracoesFixo == null &&
			   chkInfracoesEstatico == null && chkFluxoFixo == null && chkFluxoEstatico == null &&
			   chkInfracoesEstatico == null && chkAtraso == null &&
			   chkErros == null && chkLotesReprovados == null && chkInfracoesConsistFixo == null && chkInfracoesConsistEstatico == null &&
			   chkPMESPEnvioPlacas == null){
				new Mensagem(response).showErro("Favor informar ao menos um item de geração!", "javascript:window.close();");
				return; 				
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para carga de relatórios Zipados.", e);
			return;
		}
		
		try {
	        
	        //Definindo o endereço do FTP de download
			String strMes = "";
			if (mes <= 9){strMes = "0" + Integer.toString(mes);}
			else{strMes = Integer.toString(mes);}
			
			String endereco = "/PROCESSAR/Medicao"  
							  + "/" + Integer.toString(ano) 
							  + "/" + strMes;
	        
			//Luiz Amaral 30/04/2015
			//Valida se a geração é apenas em excel
			//Estratégia pedido pela CET em 30/04/2015 - Segudo Ronaldo Bueno, ZIP pode cuasar problemas com tribunal de contas
			//Como alternativa gera-se apenas em excel separadamente
			if(validarApenasExcel(chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, 
								  chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico, chkAtraso, 
								  chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico,
								  chkPMESPEnvioPlacas)){
				
				
				GerarApenasExcel(mes, ano, response, acessoUsuario.getUsuario().getId(),
          			  			 chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, 
          			  			 chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico,
          			  			 chkAtraso, chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico,
          			  			 chkPMESPEnvioPlacas, endereco);	      

			}else{
				
				// Criando o arquivo fisico ZIP
		        String nomeArquivo = "Relatorios Medicao - Consorcio LCL 0" + 
		        					 String.valueOf(mes) + "-" +
		        					 String.valueOf(ano) + ".zip";
		        
				ZipOutputStream outZIP = criaArquivoSaidaZip(nomeArquivo, response);
				
				GerarRelatorioZipados(mes, ano, response, outZIP, acessoUsuario.getUsuario().getId(),
		  				  chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, 
		  				  chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico,
		  				  chkAtraso, chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico,
		  				  chkPMESPEnvioPlacas, endereco);			
			}
			
			

            return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório CAV para CAI Zipado.", e);
			new ServletException("Erro ao gerar os relatórios Zipados: " + e.getMessage());
		}
	}

	
	/**
	 * Servlet para a geração de todos os relatórios de Medição - Empacotar ZIP com relatórios do CAV
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 10/11/2014
	 */
	public void GerarRelatorioZipados(int mes, int ano,  HttpServletResponse response, 
									  ZipOutputStream out, Integer idUser,
									  String chkTodos, String chk4Minutos, String chkFuncionamentoEstatico, 
									  String chkInfracoesFixo, String  chkInfracoesEstatico, String chkFluxoFixo, 
									  String chkFluxoEstatico, String chkAtraso, String chkErros, 
									  String chkLotesReprovados, String chkInfracoesConsistFixo, String chkInfracoesConsistEstatico,
									  String chkPMESPEnvioPlacas, String endereco) throws IOException, ConexaoException, SQLException, ModelException{
		
		try{
			
			List<String> arquivos = acessoFTP.ListarArquivosDir(endereco);
			
			if(arquivos.size() == 0){
				new Mensagem(response).showErro("Não há arquivos de Medição para o mês e ano informado.", "javascript:window.close();");
				return; 				
			}
	
			for(int i=0; i<arquivos.size(); i++){
				
				//Valida os usuários e os tipos de relatórios
				if(validarGeracaoGrupo(idUser, arquivos.get(i))){

					//Valida quais relatórios deverão ser carregados no ZIP 
					if(validarArquivos(arquivos.get(i), chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, 
		            			  chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico,
		            			  chkAtraso, chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico,
		            			  chkPMESPEnvioPlacas)){
						
						adicArquivoNoZip(out, removeAcentos(arquivos.get(i)) , acessoFTP.ObterArquivo(endereco + "/" + arquivos.get(i)));
					
					}
				}
			}

			out.close();
		}catch(Exception e){
			logger.error("Erro ao preparar dados para carga de relatórios Zipados.", e);
			return;
		}
	}
	
	
	/**
	 * Validar se o valor digitado é um numero
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 10/11/2014
	 */
	boolean isDigit(String s) {  
	    for (int i = 0; i < s.length(); i++) {  
	          char ch = s.charAt(i);  
	          if (ch < 48 || ch > 57)  
	               return false;  
	    }  
	    return true;  
	}  
	
	public static String removeAcentos(String palavra) {      
        palavra = palavra.replaceAll("[aáàãâä]","a");   
        palavra = palavra.replaceAll("ç", "c");
        palavra = palavra.replaceAll("[óõ]", "o");
        
        return palavra;    
    }  
	
	private ZipOutputStream criaArquivoSaidaZip(String sArq,
			HttpServletResponse response) throws ServletException, IOException {
		ZipOutputStream zip;

		response.setContentType(TipoMime.ZIP.getTipo());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sArq + "\"");
		response.setHeader("Refresh", "300");
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setDateHeader("Expires", 0); //prevents caching at the proxy server

		zip = new ZipOutputStream(response.getOutputStream());

		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(0);

		return zip;
	}

	private void adicArquivoNoZip(ZipOutputStream zip, String nome,
			byte[] conteudo) throws IOException {

		zip.putNextEntry(new ZipEntry(nome));
		zip.write(conteudo);
		zip.closeEntry();
	}
	


		
	public boolean validarGeracaoGrupo(Integer idUser, String nomeArquivo) throws ConexaoException {      

		boolean ret = false;
		
		List<GrupoBean> listGrupo = new ArrayList<GrupoBean>();
		listGrupo = buscaGruposPorIdUsuario(idUser);
		
		if(listGrupo.size() > 0){ //Pertence ao Grupo Velsis
			
			if(nomeArquivo.contains("Estatico") || nomeArquivo.contains("Estático") || nomeArquivo.contains("_GC")){
				ret = true;
			}else{
				ret = false;
			}
			
		}else{
			
			if(!nomeArquivo.contains("_GC")){
				ret = true;
			}else{
				ret = false;
			}
		}
		return ret;
    }  
	
	/**
	 * Busca os grupos a quais um usuário pertence.
	 * @param idUsuario Identificador do usuário
	 * @return Uma lista de objeto GrupoBean materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<GrupoBean> buscaGruposPorIdUsuario(int idUsuario) throws ConexaoException {

		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT");
		sbSQL.append("   sg.id_grupo,");
		sbSQL.append("   sg.descricao");
		sbSQL.append(" FROM");
		sbSQL.append("   sis_usuario_grupo sug WITH (NOLOCK)");
		sbSQL.append("   INNER JOIN  sis_grupo sg WITH (NOLOCK)");
		sbSQL.append("     ON sg.id_grupo = sug.id_grupo");
		sbSQL.append(" WHERE");
		sbSQL.append("   sug.id_usuario = ?");
		sbSQL.append("   and sg.descricao = 'Visualizadores Velsis'"); //Visualizadores Velsis
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<GrupoBean> lRet = new ArrayList<GrupoBean>();
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new GrupoBean(
						rs.getInt("id_grupo"),
						rs.getString("descricao")
				));
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}		
		return lRet;
	}	
	
	//Autor: Luiz Amaral
	//Data: 02/02/2015
	//Valida quais arquivos irão ser impressos dentro do ZIP
	public boolean validarArquivos(String nomeRelatorio, String chkTodos, String chk4Minutos, String chkFuncionamentoEstatico, 
									  String chkInfracoesFixo, String chkInfracoesEstatico, String chkFluxoFixo, 
									  String chkFluxoEstatico, String chkAtraso, String chkErros, String chkLotesReprovados,
									  String chkInfracoesConsistFixo, String chkInfracoesConsistEstatico,
									  String chkPMESPEnvioPlacas){
		
		boolean retorno = false;
		
		nomeRelatorio = removeAcentos(nomeRelatorio);
		
		if(chkTodos != null)
			return true;
		else if(nomeRelatorio.contains("Funcionamento") && nomeRelatorio.contains("Estatico") && chkFuncionamentoEstatico != null)
			return true;
		else if(nomeRelatorio.contains("Total de Image") && nomeRelatorio.contains("Estatico") && chkInfracoesEstatico != null)
			return true;		
		else if(nomeRelatorio.contains("Total de Image") && nomeRelatorio.contains("FixoBarreira") && chkInfracoesFixo != null)
			return true;		
		else if(nomeRelatorio.contains("Volume por dia") && nomeRelatorio.contains("Estatico") && chkFluxoEstatico != null)
			return true;		
		else if(nomeRelatorio.contains("Volume por dia") && nomeRelatorio.contains("FixoBarreira") && chkFluxoFixo != null)
			return true;
		else if(nomeRelatorio.contains("Pacotes de 4 Minutos") && chk4Minutos != null)
			return true;
		else if(nomeRelatorio.contains("Atraso de Imagens") && chkAtraso != null)
			return true;
		else if(nomeRelatorio.contains("Erros na Valida") && chkErros != null)
			return true;
		else if(nomeRelatorio.contains("Lotes Reprovados") && chkLotesReprovados != null)
			return true;
		else if(nomeRelatorio.contains("Total de Infra") && nomeRelatorio.contains("Consistentes") && nomeRelatorio.contains("Estatico") && chkInfracoesConsistEstatico != null)
			return true;		
		else if(nomeRelatorio.contains("Total de Infra") && nomeRelatorio.contains("Consistentes") && nomeRelatorio.contains("FixoBarreira") && chkInfracoesConsistFixo != null)
			return true;
		else if(nomeRelatorio.contains("PMESP") && nomeRelatorio.contains("Envio de Placas") && chkPMESPEnvioPlacas != null)
			return true;
		
		return retorno;
		
	}

	/**
	 * Servlet para a geração de todos os relatórios de Medição - Trazer apenas em Excel
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 30/04/2015
	 */
	public void GerarApenasExcel(int mes, int ano,  HttpServletResponse response, Integer idUser,
								 String chkTodos, String chk4Minutos, String chkFuncionamentoEstatico, 
								 String chkInfracoesFixo, String  chkInfracoesEstatico, String chkFluxoFixo, 
								 String chkFluxoEstatico, String chkAtraso, String chkErros, 
								 String chkLotesReprovados, String chkInfracoesConsistFixo, String chkInfracoesConsistEstatico,
								 String chkPMESPEnvioPlacas, String endereco) throws IOException, ConexaoException, SQLException, ModelException{
		
		try{
						
			List<String> arquivos = acessoFTP.ListarArquivosDir(endereco);
			
			if(arquivos.size() == 0){
				new Mensagem(response).showErro("Não há arquivos de Medição para o mês e ano informado.", "javascript:window.close();");
				return; 				
			}
	
			for(int i=0; i<arquivos.size(); i++){
				
				//Valida os usuários e os tipos de relatórios
				if(validarGeracaoGrupo(idUser, arquivos.get(i))){

					//Valida quais relatórios deverão ser carregados no ZIP 
					if(validarArquivos(arquivos.get(i), chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, 
		            			  chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico,
		            			  chkAtraso, chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico,
		            			  chkPMESPEnvioPlacas)){
						
					  	String nomeArquivoDownload = obterNomeArquivoDownload(arquivos.get(i), chkTodos, chk4Minutos, chkFuncionamentoEstatico, chkInfracoesFixo, chkInfracoesEstatico, chkFluxoFixo, chkFluxoEstatico, chkAtraso, chkErros, chkLotesReprovados, chkInfracoesConsistFixo, chkInfracoesConsistEstatico, chkPMESPEnvioPlacas);
						
						receberArquivoXLS(response,  arquivos.get(i), endereco, nomeArquivoDownload);
					
					}
				}
			}

			
		}catch(Exception e){
			logger.error("Erro ao preparar dados para carga de relatórios Zipados.", e);
			return;
		}
	}
	
	 /**
	 * Servlet para Verificar se a geração é apenas em excel
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 30/04/2015
	 */
	public boolean validarApenasExcel(String chkTodos, String chk4Minutos, String chkFuncionamentoEstatico, 
									  String chkInfracoesFixo, String  chkInfracoesEstatico, String chkFluxoFixo, 
									  String chkFluxoEstatico, String chkAtraso, String chkErros, String chkLotesReprovados,
									  String chkInfracoesConsistentesFixo, String chkInfracoesConsistentesEstatico,
									  String chkPMESPEnvioPlacas) throws IOException, ModelException{
		
		if(chkTodos != null) return false;
		
		int contQtde=0;
		
		if (chk4Minutos != null) contQtde++;
		if (chkFuncionamentoEstatico != null) contQtde++;
		if (chkInfracoesFixo != null) contQtde++;
		if (chkInfracoesEstatico != null) contQtde++;
		if (chkFluxoFixo != null)  contQtde++;
		if (chkFluxoEstatico != null) contQtde++;
		if (chkAtraso != null) contQtde++;
		if (chkErros != null) contQtde++;
		if (chkLotesReprovados != null) contQtde++;
		if (chkInfracoesConsistentesFixo != null) contQtde++;
		if (chkInfracoesConsistentesEstatico != null) contQtde++;
		if (chkPMESPEnvioPlacas != null) contQtde++;
		
		if (contQtde > 1) return false;
		
		return true;
	}
	
	 /**
	 * Servlet para buscar Excel no FTP e disponibilizar ao usuário
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 04/05/2015
	 */
	private void receberArquivoXLS(HttpServletResponse response, String nomeArquivo, 
							  	   String endereco, String nomeArquivoDownload) throws IOException {
		
	    InputStream is = null;
	  	OutputStream os = null;
	  	
		try {
			
			List<String> arquivos = acessoFTP.ListarArquivosDir(endereco);
			
			response.setContentType(TipoMime.XLS.getTipo());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + removeAcentos(nomeArquivoDownload != null ? nomeArquivoDownload : nomeArquivo) + "\"");
			response.setHeader("Refresh", "300");
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
			response.setDateHeader("Expires", 0); //prevents caching at the proxy server

			if(arquivos.size() == 0){
				new Mensagem(response).showErro("Não há arquivos de Medição para o mês e ano informado.", "javascript:window.close();");
				return; 				
			}

		    os = response.getOutputStream();
		    os.write(acessoFTP.ObterArquivo(endereco + "/" + nomeArquivo));

			os.flush();
		  	
		} catch(FileNotFoundException ex){
			logger.error("Erro ao preparar dados para carga de relatórios Zipados.", ex);
			return;

		} finally {
			if(is != null){
				is.close();
			}
			if(os != null){
			  	os.close();
			}
		}
		
	}

	
	//Autor: Thiago Surgik
	//Data: 20/07/2016
	//Obter nomes dos arquivos para download no padrão do CAV
	public String obterNomeArquivoDownload(String nomeRelatorio, String chkTodos, String chk4Minutos, String chkFuncionamentoEstatico, 
									  String chkInfracoesFixo, String chkInfracoesEstatico, String chkFluxoFixo, 
									  String chkFluxoEstatico, String chkAtraso, String chkErros, String chkLotesReprovados,
									  String chkInfracoesConsistFixo, String chkInfracoesConsistEstatico,
									  String chkPMESPEnvioPlacas){
		
		String nomeRelatorioDownload = null;
		
		nomeRelatorio = removeAcentos(nomeRelatorio);
		
		if(nomeRelatorio.contains("Funcionamento") && nomeRelatorio.contains("Estatico") && chkFuncionamentoEstatico != null)
			nomeRelatorioDownload = "Operação";
		else if(nomeRelatorio.contains("Total de Image") && nomeRelatorio.contains("Estatico") && chkInfracoesEstatico != null)
			nomeRelatorioDownload = "Imagens";		
		else if(nomeRelatorio.contains("Total de Image") && nomeRelatorio.contains("FixoBarreira") && chkInfracoesFixo != null)
			nomeRelatorioDownload = "Imagens";
		else if(nomeRelatorio.contains("Volume por dia") && nomeRelatorio.contains("Estatico") && chkFluxoEstatico != null)
			nomeRelatorioDownload = "Fluxo na Via";
		else if(nomeRelatorio.contains("Volume por dia") && nomeRelatorio.contains("FixoBarreira") && chkFluxoFixo != null)
			nomeRelatorioDownload = "Fluxo na Via";
		else if(nomeRelatorio.contains("Pacotes de 4 Minutos") && chk4Minutos != null)
			nomeRelatorioDownload = "4 Minutos";
		else if(nomeRelatorio.contains("Atraso de Imagens") && chkAtraso != null)
			nomeRelatorioDownload = "Imagens Atrasadas";
		else if(nomeRelatorio.contains("Erros na Valida") && chkErros != null)
			nomeRelatorioDownload = "Erros de Análise";
		else if(nomeRelatorio.contains("Lotes Reprovados") && chkLotesReprovados != null)
			nomeRelatorioDownload = "Lotes Reprovados";
		else if(nomeRelatorio.contains("Total de Infra") && nomeRelatorio.contains("Consistentes") && nomeRelatorio.contains("Estatico") && chkInfracoesConsistEstatico != null)
			nomeRelatorioDownload = "Autuações";
		else if(nomeRelatorio.contains("Total de Infra") && nomeRelatorio.contains("Consistentes") && nomeRelatorio.contains("FixoBarreira") && chkInfracoesConsistFixo != null)
			nomeRelatorioDownload = "Autuações";
		else if(nomeRelatorio.contains("PMESP") && nomeRelatorio.contains("Envio de Placas") && chkPMESPEnvioPlacas != null)
			nomeRelatorioDownload = "Envio de Placas - PMESP";
		
		return removeAcentos(nomeRelatorioDownload);
		
	}
}
