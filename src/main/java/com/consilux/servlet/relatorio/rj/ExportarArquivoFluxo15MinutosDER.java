package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.relatorio.rj.DadosFluxoVeicular;

/**
 * Servlet para a gerar arquivo CSV fluxo 15 minutos por classificação do DER-MG. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 27/04/2022
 */

@WebServlet("/relatorio/rj/ExportarArquivoFluxo15MinutosDER")
public class ExportarArquivoFluxo15MinutosDER extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(ExportarArquivoFluxo15MinutosDER.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unused")
		Integer idUsuario = null;
		String usuarioURL = request.getParameter("sistema");
		if(usuarioURL == null)
		{
			final Acesso acessoUsuario = new Acesso(request, response, true);
			if (!acessoUsuario.verificaAcesso())
				return;
			else
			{
				usuarioURL = acessoUsuario.getUsuario().getNome();
				idUsuario = acessoUsuario.getUsuario().getId();
			}
		}
		else
			usuarioURL = "SISTEMA";
		
        SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM"), formatAno = new SimpleDateFormat("yyyy");
		
		String strDataInicio = request.getParameter("dataini");
		String strDataFim = request.getParameter("datafim");
		
		Date dtDataInicio = null, dtDataFim = null;
		
		//Validações Necessárias para montar relatório
		if((strDataInicio.equals("")) || (strDataFim.equals("")))
		{
			new Mensagem(response).showErro("A Data Início e a Data Fim devem ser informadas!", "javascript:window.close();");
			return;
		}
		
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio))
		{
			new Mensagem(response).showErro("Data Início enviada inválida!", "javascript:window.close();");
			return;
		}
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio))
		{
			new Mensagem(response).showErro("Data Fim enviada inválida!", "javascript:window.close();");
			return;
		}
			
		try
		{
			dtDataInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataInicio + " 00:00:00");
			dtDataFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataFim + " 23:59:59");
			
			Calendar calendarioInicio = Calendar.getInstance();
			calendarioInicio.setTime(dtDataInicio);
			Calendar calendarioFim = Calendar.getInstance();
			calendarioFim.setTime(dtDataFim);
			
			if (calendarioInicio.after(calendarioFim))
			{
				new Mensagem(response).showErro("A Data Início deve ser menor ou igual da Data Fim!", "javascript:window.close();");
				return; 
			}
			
		}
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
 			// Criando o arquivo fisico
			String nomeArquivo = "RelatorioFluxo15Minutos-" + mesExtenso.format(dtDataInicio).substring(0,3).toLowerCase() + formatAno.format(dtDataInicio) + ".csv";
	        
			response.setContentType(TipoMime.CSV.getTipo());
			response.setCharacterEncoding("ISO-8859-1");
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
			//Buscando informações para popular planilhas
		    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
		    StringBuilder dadosArquivo = dadosFluxoVeicular.RelatorioFluxo15MinutosPorClassificacao(dtDataInicio, dtDataFim);
			
			if (dadosArquivo != null && dadosArquivo.length() > 0)
			{
				PrintWriter out = null;
				out = response.getWriter();
				
				out.write(dadosArquivo.toString());
				
				{
					// Salvando o arquivo
		 	        out.flush();
		 	        out.close();
		 	        
		 	        out = null;
		 	        response = null;
		        }
			}
			else
			{
				new Mensagem(response).showErro("Não há dados para o período informado!", "javascript:history.back();");
				return;
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	/**
	 * Validar se o valor digitado é um numero
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 27/09/2016
	 */
	boolean isDigit(String s) {  
	    for (int i = 0; i < s.length(); i++) {  
	          char ch = s.charAt(i);  
	          if (ch < 48 || ch > 57)  
	               return false;  
	    }  
	    return true;  
	}	
}
