package com.consilux.servlet.relatorio.rj;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Mensagem;
import com.consilux.model.relatorio.rj.RelatorioEditalRJ.FluxoVeicular;
import com.consilux.servlet.relatorio.RelatorioProdutividadeAuditoria;

/**
 * Servlet para a geração de relatórios de Fluxo Veicular/Fiscalização Eletrônica. 
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 27/09/2016
 */
public class RelatoriosFluxoVeicular extends HttpServlet {
	
	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatoriosFluxoVeicular.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String paramIdRelatorio = request.getParameter("relatorio");
		String paramMes = request.getParameter("mes");
		String paramAno = request.getParameter("ano");
		
		String[] IdRelatorioFiltroLocalSplit = paramIdRelatorio.split("_");
		String strIdRelatorio = IdRelatorioFiltroLocalSplit[0];
		
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
		
		FluxoVeicular idRelatorio = FluxoVeicular.GetValue(intIdRelatorio);
		
		switch (idRelatorio) {
		case RELATORIO_1:
			Relatorio1FluxoVeicular relatorio1 = new Relatorio1FluxoVeicular();
			relatorio1.doGet(request, response);
			break;
		
		case RELATORIO_2:
			Relatorio2FluxoVeicular relatorio2 = new Relatorio2FluxoVeicular();
			relatorio2.doGet(request, response);
			break;
			
		case RELATORIO_3:
			Relatorio3FluxoVeicular relatorio3 = new Relatorio3FluxoVeicular();
			relatorio3.doGet(request, response);
			break;

		case RELATORIO_4:
			Relatorio4FluxoVeicular relatorio4 = new Relatorio4FluxoVeicular();
			relatorio4.doGet(request, response);
			break;
			
		case RELATORIO_5:
			Relatorio5FluxoVeicular relatorio5 = new Relatorio5FluxoVeicular();
			relatorio5.doGet(request, response);
			break;
			
		case RELATORIO_6:
			Relatorio6FluxoVeicular relatorio6 = new Relatorio6FluxoVeicular();
			relatorio6.doGet(request, response);
			break;

		case RELATORIO_7:
			Relatorio7FluxoVeicular relatorio7 = new Relatorio7FluxoVeicular();
			relatorio7.doGet(request, response);
			break;
			
		case RELATORIO_8:
			Relatorio8FluxoVeicular relatorio8 = new Relatorio8FluxoVeicular();
			relatorio8.doGet(request, response);
			break;
			
		case RELATORIO_9:
			Relatorio9FluxoVeicular relatorio9 = new Relatorio9FluxoVeicular();
			relatorio9.doGet(request, response);
			break;
			
		case RELATORIO_10:
			Relatorio10FluxoVeicular relatorio10 = new Relatorio10FluxoVeicular();
			relatorio10.doGet(request, response);
			break;
			
		case RELATORIO_11:
			Relatorio11FluxoVeicular relatorio11 = new Relatorio11FluxoVeicular();
			relatorio11.doGet(request, response);
			break;
			
		case RELATORIO_12:
			Relatorio12FluxoVeicular relatorio12 = new Relatorio12FluxoVeicular();
			relatorio12.doGet(request, response);
			break;
		
		case RELATORIO_13:
			Relatorio13FluxoVeicular relatorio13 = new Relatorio13FluxoVeicular();
			relatorio13.doGet(request, response);
			break;
			
		case RELATORIO_14:
			Relatorio14FluxoVeicular relatorio14 = new Relatorio14FluxoVeicular();
			relatorio14.doGet(request, response);
			break;
			
		case RELATORIO_15:
			RelatorioProdutividadeAuditoria relatorio15 = new RelatorioProdutividadeAuditoria();
			relatorio15.doGet(request, response);
			break;
			
		case RELATORIO_16:
			RelatorioAcompanhamentoManutencao relatorio16 = new RelatorioAcompanhamentoManutencao();
			relatorio16.doGet(request, response);
			break;
			
		default:
			new Mensagem(response).showErro("Relatório não disponível!", "javascript:window.close();");
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
