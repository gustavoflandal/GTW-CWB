/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Servlet para envio de informações sobre veículo.

  Historico:

    $Log: InfoVeiculo.java,v $
    Revision 1.2  2009/06/01 18:42:33  fos
    Consertado expressão regular de inteiros.

    Revision 1.1  2009/03/24 21:39:03  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Veiculo;

 /**
 * Servlet para envio de informações sobre veículo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/06/01 18:42:33 $ $Author: fos $
 */
public class InfoVeiculo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public InfoVeiculo() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdVeiculo = request.getParameter("id_veiculo");

		String sRegistrarVisualizacao = request.getParameter("registrar_visualizacao");
		boolean registrarVisualizacao = (sRegistrarVisualizacao != null) && ( Boolean.parseBoolean(sRegistrarVisualizacao) == true );
		
		if (sIdVeiculo == null || !ExpValida.LONGO.validar(sIdVeiculo))
			throw new ServletException("Identificador do veículo enviado invalido!");

		try {
			Veiculo veiculo = Veiculo.buscaVeiculoPorId(Long.parseLong(sIdVeiculo));

			if (veiculo == null) {
				return;
			}

			if ( registrarVisualizacao ){
				Veiculo.registrarVeiculoVisualizado(veiculo.getIdVeiculo(), acesso.getUsuario().getId() );
			}

			AjaxXMLConstr xml = new AjaxXMLConstr("veiculo");
			xml.adicCampo("ID_VEICULO", String.valueOf(veiculo.getIdVeiculo()));
			xml.adicCampo("PLACA", veiculo.getPlaca());
			xml.adicCampo("DATA", new SimpleDateFormat("dd/MM/yyyy").format(veiculo.getData()));
			xml.adicCampo("CLASSE", String.valueOf(veiculo.getIdClasse()));
			xml.adicCampo("ID_IMAGEM_OBJ", String.valueOf(veiculo.getIdImagemObj()));
			xml.adicCampo("COM_VIDEO", String.valueOf(veiculo.getComVideo()));
			xml.adicCampo("COM_PESAGEM", String.valueOf(veiculo.getComPesagem()));
			xml.adicCampo("PORTE_VEICULO", veiculo.getPorteVeiculo());
			
			xml.dump(response);
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		
	}  	
}