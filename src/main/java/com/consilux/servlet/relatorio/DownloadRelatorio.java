package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.RelatorioDinamico;
import com.consilux.model.TipoMime;
import com.consilux.model.beans.Periodo;
import com.consilux.model.beans.RelatorioDinamicoBean;

/**
 * Servlet que permite download de arquivos de relatórios dinâmicos.
 * Útil para baixar arquivos gerados pelo próprio GTW.
 * @author raoni
 */
public class DownloadRelatorio extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 8304241588064424461L;
	private static Logger logger = Logger.getLogger(DownloadRelatorio.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Verifica segurança
		Acesso acesso = new Acesso(request, response, false); 
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		// Recupera os parâmetros
		String sIdRelatorio = request.getParameter("id_relatorio");
		String sDataInicio = request.getParameter("data_inicio");
		String sDataFim = request.getParameter("data_fim");

		// Valida o(s) parâmetros.
		if (sIdRelatorio == null || !ExpValida.NATURAL_COM_ZERO.validar(sIdRelatorio))
		{
			new Mensagem(response).showErro("Identificador do Relatório enviado inválido!");
			return;
		}

	    if (sDataInicio != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataInicio))
	    {
			new Mensagem(response).showErro("Data Inicial enviada invalida!");
			return;
	    }		
		
	    if (sDataInicio != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataFim))
	    {
			new Mensagem(response).showErro("Data Final enviada invalida!");
			return;
	    }		    
	    
		try {
			
			int idRelatorio = Integer.parseInt(sIdRelatorio);
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			
			Date dataInicio = fmt.parse(sDataInicio);
			Date dataFim = fmt.parse(sDataFim);
			
			// Recupera do banco de dados as informações pertinentes a este relatório. 
			RelatorioDinamicoBean beanRelatorio = RelatorioDinamico.buscarRelatorioDisponivelById(idRelatorio);
			if (beanRelatorio == null) {
				new Mensagem(response).showErro("Não foi possível buscar relatório [" + idRelatorio + "]");
				return;
			}

			// Recuper a afunção que deve ser executada
			String funcaoSQL = beanRelatorio.getFunctionSQL();

			// Define o nome do arquivo, com base no nome do relatório.
			String nomeRelatorio = beanRelatorio.getNome();
			
			// Se temos um nome, retira os espaços e os acentos.
			if (nomeRelatorio != null && nomeRelatorio.length() > 0) {
				nomeRelatorio = Funcoes.retirarEspacos(Funcoes.retirarAcentos(nomeRelatorio));
			}

			if (nomeRelatorio == null || nomeRelatorio.length() == 0) {
				// Se após a "Limpeza" acima o nome não servir mais, utilizar um nome padrão.
				nomeRelatorio = "RelatorioDinamico" + TipoMime.XLSX.getExtensao();
			} else {
				// Caso contrário, concatena a extensão 
				nomeRelatorio += TipoMime.XLSX.getExtensao();
			}
			
			// Muda os cabeçalhos			
			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"" + nomeRelatorio +"\"");
			
			// Gera o relatório
			RelatorioDinamico.exportaXLS(funcaoSQL, new Periodo(dataInicio, dataFim), response.getOutputStream());
		}
		catch (Exception ex) {
			String msgErro = "Erro ao exportar relatório dinâmico.";
			logger.error(msgErro, ex);
			new Mensagem(response).showErro(msgErro);
			return;
		}
		finally {
			response.getOutputStream().close();
		}

	}

}
