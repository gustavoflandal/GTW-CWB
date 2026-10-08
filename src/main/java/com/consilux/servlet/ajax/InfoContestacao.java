package com.consilux.servlet.ajax;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Cadastro;
import com.consilux.model.CadastroBD;
import com.consilux.model.Contestacao;
import com.consilux.model.InfracaoCompleta;
import com.consilux.model.Isento;
import com.consilux.model.Usuario;

/**
 * Servlet implementation class InfoContestacao
 */
public class InfoContestacao extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfoContestacao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String sIdInfracao = request.getParameter("id_infracao");
		
		if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao))
			throw new ServletException("Identificador da Infração enviado invalido!");
		
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat datehourformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String nao_disponivel = "N/D";
			
			Contestacao c = Contestacao.obterContestacao(Integer.parseInt(sIdInfracao));
			
			if (c==null)
				throw new ServletException("CONTESTACAO NAO ENCONTRADA");
			
			InfracaoCompleta ic = InfracaoCompleta.buscaInfracaoPorId(c.getIdInfracao());
			Usuario usr_cai = Usuario.buscaUsuarioPorIdUsuario(c.getIdUsuarioCai());
			Usuario usr_cav = Usuario.buscaUsuarioPorIdUsuario(c.getIdUsuarioCav());
			
			AjaxXMLConstr xml = new AjaxXMLConstr("infracao_completa");
			
			xml.adicCampo("ID_INFRACAO", c.getIdInfracao().toString());
			xml.adicCampo("ID_IMAGEM_OBJ", ic.getIdImagemOBJ().toString());
			xml.adicCampo("ID_VEICULO", ic.getIdVeiculo().toString());
			xml.adicCampo("ID_INCONSISTENCIA", c.getDecisao().toString());
			xml.adicCampo("DATA_INFRACAO", datehourformat.format(c.getData()));
			xml.adicCampo("LOCAL", c.getIdLocal() + " - " + c.getDescricaoLocal().trim());
			xml.adicCampo("CODIGO_FAIXA", c.getCodPistaProdam().toString());
			xml.adicCampo("FAIXA", c.getPista().toString());
			xml.adicCampo("ENQUADRAMENTO", c.getIdEnquadramento() + " - " + c.getDescricaoEnquadramento().trim());
			
			/// CAI
			
			xml.adicCampo("DATA_CAI", dateformat.format(c.getDataAnaliseCai()));
			xml.adicCampo("ANALISE_CAI", c.getInconsistenciaCai() + " - " + c.getInconsistenciaCaiDesc().trim());
			xml.adicCampo("USUARIO_CAI", usr_cai.getNome());
			
			if (c.getPlacaCai() == null)
				xml.adicCampo("PLACA_CAI", nao_disponivel);
			else 
				xml.adicCampo("PLACA_CAI", c.getPlacaCai().trim());
			
			if (c.getMarcaCai() == null)
				xml.adicCampo("MARCA_CAI", nao_disponivel);
			else 
				xml.adicCampo("MARCA_CAI", c.getMarcaCaiDesc());
			
			if (c.getEspecieCai() == null)
				xml.adicCampo("ESPECIE_CAI", nao_disponivel);
			else
				xml.adicCampo("ESPECIE_CAI", c.getEspecieCaiDesc());
			
			if (c.getPlacaCai() != null)
			{
				Cadastro cad = CadastroBD.buscaCadastroPorPlaca(c.getPlacaCai());
				if (cad.getMarca() != null) {
					xml.adicCampo("COR_CAI", cad.getCor());
					xml.adicCampo("MODELO_CAI", cad.getMarca().trim());
				}
				else { 
					xml.adicCampo("COR_CAI", nao_disponivel);
					xml.adicCampo("MODELO_CAI", nao_disponivel);
				}
				
				if (c.getIdEnquadramento() >= 57461 && c.getIdEnquadramento() <= 57463)
				{
					Map<String,Object> mFiltro = new HashMap<String,Object>();
					mFiltro.put("data_validade", c.getData());
					mFiltro.put("area", ic.getAreaPista()); // XXXX: TODO: FELIPE
					
					List<Isento> isentos = Isento.buscaRapidaIsentoPor(c.getPlacaCai(), c.getIdEnquadramento(), c.getData(), mFiltro);
					if(isentos.size() > 0) {
						Isento isento_at = isentos.get(0);
						xml.adicCampo("ISENCAO_CAI", "SIM");
						xml.adicCampo("ISENCAO_PERIODO_CAI", String.format("%s - %s", dateformat.format(isento_at.getDataInicio()), dateformat.format(isento_at.getDataFim())));
						xml.adicCampo("ISENCAO_MOTIVO_CAI", isento_at.getMotivo());
					}
					else {
						xml.adicCampo("ISENCAO_CAI", "NÃO");
						xml.adicCampo("ISENCAO_PERIODO_CAI", "NÃO HÁ");
						xml.adicCampo("ISENCAO_MOTIVO_CAI", "NÃO HÁ");
					}
				}
				else {
					xml.adicCampo("ISENCAO_CAI", nao_disponivel);
					xml.adicCampo("ISENCAO_PERIODO_CAI", nao_disponivel);
					xml.adicCampo("ISENCAO_MOTIVO_CAI", nao_disponivel);
				}
			}
			else 
			{
				xml.adicCampo("COR_CAI", nao_disponivel);
				xml.adicCampo("MODELO_CAI", nao_disponivel);
				xml.adicCampo("ISENCAO_CAI", nao_disponivel);
				xml.adicCampo("ISENCAO_PERIODO_CAI", nao_disponivel);
				xml.adicCampo("ISENCAO_MOTIVO_CAI", nao_disponivel);
			}
			
			/// CAV
			
			xml.adicCampo("DATA_CAV", dateformat.format(c.getDataAnaliseCav()));
			xml.adicCampo("ANALISE_CAV", c.getInconsistenciaCav() + " - " + c.getInconsistenciaCavDesc().trim());
			xml.adicCampo("USUARIO_CAV", usr_cav.getNome());
			
			if (c.getPlacaCav() == null)
				xml.adicCampo("PLACA_CAV", nao_disponivel);
			else 
				xml.adicCampo("PLACA_CAV", c.getPlacaCav().trim());
			
			if (c.getMarcaCav() == null)
				xml.adicCampo("MARCA_CAV", nao_disponivel);
			else 
				xml.adicCampo("MARCA_CAV", c.getMarcaCavDesc());
			
			if (c.getEspecieCav() == null)
				xml.adicCampo("ESPECIE_CAV", nao_disponivel);
			else
				xml.adicCampo("ESPECIE_CAV", c.getEspecieCavDesc());
			
			if (c.getPlacaCav() != null)
			{
				Cadastro cad = CadastroBD.buscaCadastroPorPlaca(c.getPlacaCav());
				if (cad.getMarca() != null) {
					xml.adicCampo("COR_CAV", cad.getCor());
					xml.adicCampo("MODELO_CAV", cad.getMarca().trim());
				}
				else {
					xml.adicCampo("COR_CAV", nao_disponivel);
					xml.adicCampo("MODELO_CAV", nao_disponivel);
				}
				
				if (c.getIdEnquadramento() >= 57461 && c.getIdEnquadramento() <= 57463)
				{
					Map<String,Object> mFiltro = new HashMap<String,Object>();
					mFiltro.put("data_validade", c.getData());
					mFiltro.put("area", ic.getAreaPista()); // XXXX: TODO: FELIPE
					
					List<Isento> isentos = Isento.buscaRapidaIsentoPor(c.getPlacaCav(), c.getIdEnquadramento(), c.getData(), mFiltro);
					if(isentos.size() > 0) {
						Isento isento_at = isentos.get(0);
						xml.adicCampo("ISENCAO_CAV", "SIM");
						xml.adicCampo("ISENCAO_PERIODO_CAV", String.format("%s - %s", dateformat.format(isento_at.getDataInicio()), dateformat.format(isento_at.getDataFim())));
						xml.adicCampo("ISENCAO_MOTIVO_CAV", isento_at.getMotivo());
					}
					else {
						xml.adicCampo("ISENCAO_CAV", "NÃO");
						xml.adicCampo("ISENCAO_PERIODO_CAV", "NÃO HÁ");
						xml.adicCampo("ISENCAO_MOTIVO_CAV", "NÃO HÁ");
					}
				}
				else {
					xml.adicCampo("ISENCAO_CAV", nao_disponivel);
					xml.adicCampo("ISENCAO_PERIODO_CAV", nao_disponivel);
					xml.adicCampo("ISENCAO_MOTIVO_CAV", nao_disponivel);
				}
			}
			else 
			{
				xml.adicCampo("COR_CAV", nao_disponivel);
				xml.adicCampo("MODELO_CAV", nao_disponivel);
				xml.adicCampo("ISENCAO_CAV", nao_disponivel);
				xml.adicCampo("ISENCAO_PERIODO_CAV", nao_disponivel);
				xml.adicCampo("ISENCAO_MOTIVO_CAV", nao_disponivel);
			}
			
			String divergencia = "";
			if (c.getErroConsistencia())
				divergencia += "CONSISTÊNCIA";
			if (c.getErroPlaca()) {
				if (divergencia.length() > 0)
					divergencia += " E PLACA DIGITADA";
				else 
					divergencia += "PLACA DIGITADA";
			}
			xml.adicCampo("DIVERGENCIA", divergencia);
			
			xml.adicCampo("DECISAO", c.getDecisao().toString());
		
			xml.dump(response);
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
