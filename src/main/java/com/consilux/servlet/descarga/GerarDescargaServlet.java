package com.consilux.servlet.descarga;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Usuario;
import com.consilux.model.descarga.Descarga;

/**
 * Implementação do servlet de geração de descargas.
 * @author raoni
 *
 */
public class GerarDescargaServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GerarDescargaServlet.class);
	
    public GerarDescargaServlet() {
        super();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {

    	// Verifica segurança
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!    	
    	
		// Recupera os parâmetros
		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");
		
		if (sDataIni == null || !ExpValida.DATA.validar(sDataIni)) {
			new Mensagem(response).showErro("Data inicial enviada inválida!");
			return;
		}
		
		if (sDataIni == null || !ExpValida.DATA.validar(sDataFim)) {
			new Mensagem(response).showErro("Data final enviada inválida!");
			return;
		}		
		
		// Converte.
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date diaIni = null;
		Date diaFim = null;
		
		try {
			diaIni = df.parse(sDataIni);
			diaFim = df.parse(sDataFim);
		} 
		catch (ParseException pe) {
			new Mensagem(response).showErro("Data inicial/final em formato inválido!");
			return;
		}
		
		if (diaFim.before(diaIni)) {
			new Mensagem(response).showErro("Data final deve ser igual ou posterior a data inicial!");
			return;			
		}
		
		// Limite máximo para geração de descarga (para garantir que permanecem pelo menos 'N' dias de dados no banco)
		int maxDiasDescarga = ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getNumeroMaximoDiasFinalDescarga();
		Date maxDiaDescarga = DateUtils.addDays(new Date(), -maxDiasDescarga);
		maxDiaDescarga = DateUtils.truncate(maxDiaDescarga, Calendar.DAY_OF_MONTH); // Remove a parte tempo (hora,minuto,segundo,mili)

		// Verifica se por acaso não estamos excedendo o limite.
		if (!diaFim.before(maxDiaDescarga)) {
			String errMessage = "Erro: excedido o limite máximo do dia [" + maxDiaDescarga + "] para geração de descarga.";
			logger.error(errMessage);
			new Mensagem(response).showErro(errMessage);
			return;			
		}
		
		// Recupera o usuário atual
		HttpSession sessao = request.getSession(false);
		Usuario usuario = (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO);		
		Integer idUsuario = usuario.getId();
		
		try {
			int idDescarga = Descarga.criarDescarga(diaIni, diaFim, idUsuario);
			
			new Mensagem(response).showSucesso("Descarga gerada com sucesso!<br>" +
					   "Clique <a href='#' onclick='window.open(\"/descarga/descarga.jsp?id_descarga=" +
					   idDescarga + "\",\"Detalhes\",\"width=700, height=160\")'>&lt;aqui&gt;</a> " +
					   "para ver detalhes.");
			
		}
		catch (Exception e) {
			logger.error("Erro na geração da descarga.", e);
			new Mensagem(response).showErro("Erro na geração da descarga: "+e.getMessage());
			return;
		}
    };

}
