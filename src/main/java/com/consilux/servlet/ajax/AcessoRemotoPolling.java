package com.consilux.servlet.ajax;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.AcessoRemotoHeartBeatCtrl;
import com.consilux.model.LocalVigente;
import com.consilux.model.MensagemJS;

/**
 * Servlet implementation class AcessoRemotoPolling
 */
public class AcessoRemotoPolling extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcessoRemotoPolling() {
    	super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false);
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdLocal = request.getParameter("id_local") != null ? request.getParameter("id_local").trim() : null;
		if (sIdLocal != null && !Pattern.matches("[1-9][0-9]{0,9}",sIdLocal)) {
	        new MensagemJS(response).showErro("Identificador do local inválido!");
	        return;
	    }
		
		LocalVigente local = null;
		
		AjaxXMLConstr xml;
		try {

			local = LocalVigente.buscaLocalVigentePorIdLocal( Integer.valueOf( sIdLocal ) );

			System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " > AcessoRemotoPolling do equipamento " + local.getSerieEquipamento() + " ...[" + acesso.getUsuario().getUsuario() + "]" );
			
			AcessoRemotoHeartBeatCtrl ctrl = AcessoRemotoHeartBeatCtrl.getInstance(); 
			ctrl.notificar( acesso.getUsuario() , local );
			
			xml = new AjaxXMLConstr("acesso_remoto_pooling");
			
			xml.adicCampo("RETORNO", "OK");

			xml.dump(response);
		}
		catch(Exception err) {
//			throw new ServletException("Erro ao processar requisição: "+err.getMessage());
			err.printStackTrace();
			try {
				xml = new AjaxXMLConstr("infracao");
				xml.adicCampo("ERRO", err.getMessage());
				xml.dump(response);
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
	}

}
