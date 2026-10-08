package com.consilux.servlet.ajax.binding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.disparador.DisparadorStatusEquipamento;

/**
 * Servlet implementation class StatusLocal
 */
public class AguardaStatusEquipamento extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AguardaStatusEquipamento() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		Acesso acesso = new Acesso(request, response, false);
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		DisparadorStatusEquipamento disp = DisparadorStatusEquipamento.getInstance();
	
		System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " > Verificação de estado solicitado ...[" + acesso.getUsuario().getUsuario() + "]" );
		
		if (disp.aguardar()) {
			AjaxXMLConstr xml;
			try {

				System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " > Atualização do estado do equipamento " + disp.getIdEquipamento() + " ...[" + acesso.getUsuario().getUsuario() + "]" );
				
				xml = new AjaxXMLConstr("status_equipamento");
				
				xml.adicCampo("ID_EQUIPAMENTO", String.valueOf(disp.getIdEquipamento()));
				xml.adicCampo("STATUS_CONEXAO", String.valueOf(disp.getStatusConexao().getId()));
				xml.adicCampo("STATUS_ENERGIA", String.valueOf(disp.getStatusEnergia().getId()));
				xml.adicCampo("STATUS_DIV", String.valueOf(disp.getStatusDIV().getId()));
				
				xml.dump(response);
			}
			catch (ParserConfigurationException err) {
				err.printStackTrace();
				throw new ServletException("Erro ao montar o XML: "+err.getMessage());
			}
		}
		else{

			System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " > Não houve atualização de estado de equipamentos ...[" + acesso.getUsuario().getUsuario() + "]" );
			
		}
	}

}
