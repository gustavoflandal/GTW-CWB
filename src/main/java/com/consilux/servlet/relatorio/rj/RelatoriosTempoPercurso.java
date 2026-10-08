package com.consilux.servlet.relatorio.rj;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Mensagem;
import com.consilux.model.relatorio.rj.RelatorioEditalRJ.TempoPercurso;

/**
 * Servlet para a geração de relatórios de Tempo de Percurso/Fiscalização Eletrônica. 
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 30/09/2016
 */
public class RelatoriosTempoPercurso extends HttpServlet {
	
	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatoriosTempoPercurso.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String paramIdRelatorio = request.getParameter("relatorio");
		String paramMes = request.getParameter("mes");
		String paramAno = request.getParameter("ano");
		
		String[] IdRelatorioSplit = paramIdRelatorio.split("_");
		String strIdRelatorio = IdRelatorioSplit[0]; 
		
		Integer intIdRelatorio = null;

		//Validações Necessárias para montar relatório
		if(strIdRelatorio.equals("0")){
			new Mensagem(response).showErro("Favor selecionar um relatório!", "javascript:window.close();");
			return;
		}

		if((paramMes.equals("")) || (paramAno.equals(""))){
			new Mensagem(response).showErro("O Mês e o Ano devem ser informados!", "javascript:window.close();");
			return;
		}
		
		if (!isDigit(paramMes) || !isDigit(paramAno)) {
			new Mensagem(response).showErro("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
			return; 
		}
			
		try {
			intIdRelatorio = Integer.parseInt(strIdRelatorio);  
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		TempoPercurso idRelatorio = TempoPercurso.GetValue(intIdRelatorio);
		
		switch (idRelatorio) {
		case RELATORIO_1:
			Relatorio1TempoPercurso relatorio1 = new Relatorio1TempoPercurso();
			relatorio1.doGet(request, response);
			break;
		
		case RELATORIO_2:
			Relatorio2TempoPercurso relatorio2 = new Relatorio2TempoPercurso();
			relatorio2.doGet(request, response);
			break;
			
		case RELATORIO_3:
			Relatorio3TempoPercurso relatorio3 = new Relatorio3TempoPercurso();
			relatorio3.doGet(request, response);
			break;

		case RELATORIO_4:
			Relatorio4TempoPercurso relatorio4 = new Relatorio4TempoPercurso();
			relatorio4.doGet(request, response);
			break;
			
		case RELATORIO_5:
			Relatorio5TempoPercurso relatorio5 = new Relatorio5TempoPercurso();
			relatorio5.doGet(request, response);
			break;
			
		case RELATORIO_6:
			Relatorio6TempoPercurso relatorio6 = new Relatorio6TempoPercurso();
			relatorio6.doGet(request, response);
			break;
			
		case RELATORIO_7:
			Relatorio7TempoPercurso relatorio7 = new Relatorio7TempoPercurso();
			relatorio7.doGet(request, response);
			break;
			
		case RELATORIO_8:
			Relatorio8TempoPercurso relatorio8 = new Relatorio8TempoPercurso();
			relatorio8.doGet(request, response);
			break;
			
		case RELATORIO_9:
			Relatorio9TempoPercurso relatorio9 = new Relatorio9TempoPercurso();
			relatorio9.doGet(request, response);
			break;
			
		case RELATORIO_10:
			Relatorio10TempoPercurso relatorio10 = new Relatorio10TempoPercurso();
			relatorio10.doGet(request, response);
			break;
			
		default:
			new Mensagem(response).showErro("Relatório indisponível!", "javascript:window.close();");
			break;
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
