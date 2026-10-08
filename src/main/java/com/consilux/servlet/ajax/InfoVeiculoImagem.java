package com.consilux.servlet.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.VeiculoImagem;

/**
 * Servlet implementation class InfoInfracaoImagem
 */
public class InfoVeiculoImagem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoVeiculoImagem.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfoVeiculoImagem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdInfracao = request.getParameter("id_infracao");
		String sIdVeiculo = request.getParameter("id_veiculo");
		
		if (sIdInfracao != null && !Pattern.matches("[0-9]{1,8}",sIdInfracao))
			throw new ServletException("Identificador da infração enviado invalido. [" + sIdInfracao + "]");

		if (sIdVeiculo != null && !ExpValida.LONGO.validar(sIdVeiculo))
			throw new ServletException("Identificador do veículo enviado invalido. [" + sIdVeiculo + "]");

		try {
			
			Map<String,Object> mFiltro = new HashMap<String,Object>();
			
			if (sIdInfracao != null)
				mFiltro.put("id_infracao", Integer.valueOf(sIdInfracao));
			else if (sIdVeiculo != null)
				mFiltro.put("id_veiculo", Long.valueOf(sIdVeiculo));
			else
				throw new ServletException("Não foi enviado o id_infracao e nem o id_veiculo para carregar a imagem.");
			
			List<VeiculoImagem> veiculoImgs = VeiculoImagem.buscaVeiculoImagemPor(mFiltro);

			AjaxXMLConstr xml = new AjaxXMLConstr("imagens");
			xml.adicCampo("CONTA_IMAGEM", String.valueOf(veiculoImgs.size()));
			
			int i = 0;
			for (VeiculoImagem vi: veiculoImgs) {
				xml.adicCampo("ID_IMAGEM_"+(i), String.valueOf(vi.getIdImagem()));
				xml.adicCampo("TIPO_IMAGEM_"+(i), String.valueOf(vi.getNomeTipoImagem()));
				xml.adicCampo("DESCARGA_"+(i), String.valueOf(vi.getIdDescarga()));
				xml.adicCampo("PASTA_"+(i), String.valueOf(vi.getIdPasta()));
				i++;
			}

			xml.dump(response);
		}
		catch(Exception err) {
			logger.error("Erro ao enviar informações da imagem.", err);
			throw new ServletException("Erro ao montar o XML: "+err.getMessage(), err);
		}
	}

}
