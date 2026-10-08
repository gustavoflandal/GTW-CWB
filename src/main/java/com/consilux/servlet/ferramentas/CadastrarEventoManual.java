package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.EventoCSX;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.ferramenta.EventoManual;
import com.consilux.model.ferramenta.EventoManualGrupo;

/**
 * Servlet para a gravação evento manual
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 06/10/2015
 */
public class CadastrarEventoManual extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(CadastrarEventoManual.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; 
		}

		try	{
			
			String tiposEvento = request.getParameter("selTipoEvento");
			tiposEvento = tiposEvento != null ? tiposEvento : "";
			
			String strDataEvento = request.getParameter("dataEvento");
			String strHoraEvento = request.getParameter("horaEvento");
			String strDataHoraEvento = null;
			String strIdLocal = request.getParameter("id_local");
			String chkInicio = tiposEvento.equals("chkInicio") ? tiposEvento : null;
			String chkRetorno = tiposEvento.equals("chkRetorno") ? tiposEvento : null;
			String strIdCategoriaEventoManual = request.getParameter("id_evento_manual_categoria");
			
			//Validação dos campos da Tela
			if(strDataEvento.equals("")) {
				new Mensagem(response).showErro("Data Evento não informada!");
				return;
			}
			if(strHoraEvento.equals("")) {
				new Mensagem(response).showErro("Hora Evento não informada!");
				return;
			}
			if(strIdLocal.equals("")) {
				new Mensagem(response).showErro("Local não informado!");
				return;
			}
			if(Integer.parseInt(strHoraEvento.substring(0, 2)) > 23){
				new Mensagem(response).showErro("Favor informar corretamente a hora do Evento! Entre 00h e 23h.");
				return;
			}
			if(Integer.parseInt(strHoraEvento.substring(3, 5)) > 59){
				new Mensagem(response).showErro("Favor informar corretamente os minutos do Evento! Entre 00min e 59min.");
				return;
			}
			if(strHoraEvento.length() != 5){
				new Mensagem(response).showErro("Favor informar corretamente o horário do Evento.");
				return;
			}
			if(chkInicio == null && chkRetorno == null){
				new Mensagem(response).showErro("Favor informar um tipo de evento!");
				return; 				
			}
			if(chkInicio != null && chkRetorno != null){
				new Mensagem(response).showErro("Favor informar apenas um tipo de evento por cadastro!");
				return; 				
			}
			if(strIdCategoriaEventoManual.equals("")) {
				new Mensagem(response).showErro("Grupo de Eventos não informado!");
				return;
			}
			
			//Declarando variaveis
			EventoCSX eventoCSX = new EventoCSX();
			LocalVigente local = null;
			
			Integer idLocal = 0, tipoEventoManual = 0, idCategoriaEventoManual = 0;
			String serieEquipamento = "";
			Date dataHoraEvento = null;
			Boolean resultado = false;
			
			try{
				//divido data em partes 
				String dd = strDataEvento.substring(0, 2);  
				String mm = strDataEvento.substring(3, 5); 
				String yyyy = strDataEvento.substring(6, 10); 
				strDataEvento = yyyy + "-" + mm + "-" + dd;
				
				strDataHoraEvento = strDataEvento + " " + strHoraEvento + ":00";
				
				dataHoraEvento = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDataHoraEvento);
				idLocal = Integer.parseInt(strIdLocal);
				if (chkInicio != null) {
					tipoEventoManual = EventoManualGrupo.TipoEventoManual.INICIO.getId();
				} else if (chkRetorno != null) {
					tipoEventoManual = EventoManualGrupo.TipoEventoManual.RETORNO.getId();
				}
				idCategoriaEventoManual = Integer.parseInt(strIdCategoriaEventoManual);
				
				local = LocalVigente.buscaLocalVigentePorIdLocal(idLocal);
				
				serieEquipamento = local.getSerieEquipamento().toString();
				
				eventoCSX.setProprietario(serieEquipamento);
				eventoCSX.setData_hora(dataHoraEvento);
				eventoCSX.setUsuario(acesso.getUsuario().getUsuario());
				eventoCSX.setTipoEventoManual(tipoEventoManual);
				eventoCSX.setIdCategoriaEventoManual(idCategoriaEventoManual);
				
			}catch(Exception e)	{
				logger.error(e);
	    		new Mensagem(response).showErro("Favor informar corretamente os dados de entrada para cadastro.");
	    		return;
			}
	    
			resultado = EventoManual.gravarEvento(eventoCSX);
			
			if (resultado) {
 				new Mensagem(response).showSucesso("Evento gravado com sucesso!", "/cadastro/cad_evento_manual.jsp");
			}else {
				new Mensagem(response).showErro("Falha ao gravar Evento!");
			}
			
			return;
			
		}catch(Exception e)	{
			new Mensagem(response).showErro("Erro ao gravar Evento!");

		}
	}
}
