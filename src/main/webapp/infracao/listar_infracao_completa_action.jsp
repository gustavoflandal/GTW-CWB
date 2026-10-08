<%@page import="com.consilux.model.Usuario"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Locale"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%!
	private static Logger logger = Logger.getLogger(InfracaoCompletaLista.class); 
	private String obterString(HttpServletRequest request, String nomeParametro) {
		Object param = request.getParameter(nomeParametro);
		if (param != null) {
			String sParam = ((String) param).trim();
			if (sParam.length() > 0)
				return sParam;
		}
		return null;		
	}
%>
<%

	///////////////////////////////////////////////////////////////////////////////////////
	// Primeira Parte: Obter os valores da request.
	///////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////
	// 1ª Linha
	///////////////////////////////////////////////////////////////////////////////	
	String sIdInfracaoIni = obterString(request, "id_infracao_ini");
	String sIdInfracaoFim = obterString(request, "id_infracao_fim");
	String sDataInfracaoIni = obterString(request, "data_infracao_ini");
	String sHoraInfracaoIni = obterString(request, "hora_infracao_ini");
	String sDataInfracaoFim = obterString(request, "data_infracao_fim");
	String sHoraInfracaoFim = obterString(request, "hora_infracao_fim");
	String sIdEnquadramento = obterString(request, "id_enquadramento");
	
	///////////////////////////////////////////////////////////////////////////////
	// 2ª Linha
	///////////////////////////////////////////////////////////////////////////////	
	String sIdImagemIni = obterString(request, "id_imagem_ini");
	String sIdImagemFim = obterString(request, "id_imagem_fim");
	String sEquip = obterString(request, "id_equip");
	String sTipo = obterString(request, "id_tipo");
    String sLocal = obterString(request, "id_local");
    String sProcesso = obterString(request, "id_processo");
    String sEnquadInfracao = obterString(request, "id_enquadRegra");

    String[] sPista = request.getParameterValues("id_pista");
 // XXX: FELIPE: Alterado para multiplas pistas
 	ArrayList<String> list_sPista = new ArrayList<String>(
 			Arrays.asList(sPista));
 	ArrayList<Integer> list_iPista = new ArrayList<Integer>();
 	if (!list_sPista.contains("0")) {
 		for (String item_pista : list_sPista) {
 			list_iPista.add(Integer.parseInt(item_pista));
 			logger.info("sPista = " + item_pista);
 		}
 	}
        
	///////////////////////////////////////////////////////////////////////////////
	// 3ª Linha
	///////////////////////////////////////////////////////////////////////////////	
	String sAutoIni = obterString(request, "auto_ini");
	String sAutoFim = obterString(request, "auto_fim");
	String sMovimentoLote = obterString(request, "movimentoLote");
	String sPlaca = obterString(request, "placa");
	String sImagemInconsistencia = obterString(request, "imagemInconsistencia");
	String sTipoVeiculo = obterString(request, "tipoVeiculo");
	
	///////////////////////////////////////////////////////////////////////////////
	// 4ª Linha
	///////////////////////////////////////////////////////////////////////////////	
    String sAproveitaveis = obterString(request, "aproveitaveis");
    String sJustificativaAproveitaveis = obterString(request, "justificativa_aproveitaveis");
    String sRgOperadorAproveitavel = obterString(request, "rg_operador_aproveitavel");
    String sEspera = obterString(request, "espera");

	///////////////////////////////////////////////////////////////////////////////
	// 5ª Linha
	///////////////////////////////////////////////////////////////////////////////
	String sValidaveis  = obterString(request, "validaveis");
	String sJustificativaValidaveis  = obterString(request, "justificativa_validaveis");
	String sDataValidaveisIni = obterString(request, "data_validavel_ini");
	String sHoraValidaveisIni = obterString(request, "hora_validavel_ini");
	String sDataValidaveisFim = obterString(request, "data_validavel_fim");
	String sHoraValidaveisFim = obterString(request, "hora_validavel_fim");	

	///////////////////////////////////////////////////////////////////////////////
	// 6ª Linha
	///////////////////////////////////////////////////////////////////////////////
	String sValidas  = obterString(request, "validas");
	String sJustificativaValidas  = obterString(request, "justificativa_validas");
	String sDataValidasIni = obterString(request, "data_valida_ini");
	String sHoraValidasIni = obterString(request, "hora_valida_ini");
	String sDataValidasFim = obterString(request, "data_valida_fim");
	String sHoraValidasFim = obterString(request, "hora_valida_fim");
	
	///////////////////////////////////////////////////////////////////////////////
	// 7ª Linha
	///////////////////////////////////////////////////////////////////////////////
	String sRgOperadorValidas = obterString(request, "rg_operador_validas");
	String sVelocidadeMinima = obterString(request, "vel_min");
	String sVelocidadeMaxima = obterString(request, "vel_max");
	String sFiltrarResultados = obterString(request, "filtrar_resultados");
	
	///////////////////////////////////////////////////////////////////////////////
	// 15ª Linha
	///////////////////////////////////////////////////////////////////////////////	
	String sProduto = obterString(request, "id_produto");
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Segunda Parte: Validação 
    ///////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////
	// Validação 1ª Linha
	///////////////////////////////////////////////////////////////////////////////	
	if (sIdInfracaoIni != null && !Pattern.matches("[1-9][0-9]{0,7}",sIdInfracaoIni)) {
        new MensagemJS(response).showErro("Identificador de infração inicial enviado inválido!");
        return;
    }
    if (sIdInfracaoFim != null && !Pattern.matches("[1-9][0-9]{0,7}",sIdInfracaoFim)) {
        new MensagemJS(response).showErro("Identificador de infração final enviado inválido!");
        return;
    }
    if ((sIdInfracaoIni != null && sIdInfracaoFim == null) ||
   		(sIdInfracaoFim != null && sIdInfracaoIni == null)) {
        new MensagemJS(response).showErro("Limites de indentificador de infração incompletos!");
        return;
    }
    
    if (sIdInfracaoIni == null && sDataInfracaoIni == null && 
        sIdInfracaoFim == null && sIdImagemIni == null && 
        sAutoIni == null && sPlaca == null) {
        new MensagemJS(response).showErro("Pesquisa muito abrangente, selecione um filtro!");
    	return;
	}    
    
    if (sDataInfracaoIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataInfracaoIni)) {
        new MensagemJS(response).showErro("Data inicial da infração enviada inválida!");
        return;
    }
    if (sDataInfracaoFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataInfracaoFim)) {
        new MensagemJS(response).showErro("Data final da infração enviada inválida!");
        return;
    }
    
    if (sHoraInfracaoIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoIni)) {
        new MensagemJS(response).showErro("Hora inicial da infração enviada inválida!");
        return;
    }
    
    if (sHoraInfracaoFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoFim)) {
        new MensagemJS(response).showErro("Hora final da infração enviada inválida!");
        return;
    }
    
    if ((sDataInfracaoIni != null || sHoraInfracaoIni != null ||
    		sDataInfracaoFim != null || sHoraInfracaoFim != null) &&
    	(sDataInfracaoIni == null || sHoraInfracaoIni == null ||
    		sDataInfracaoFim == null || sHoraInfracaoFim == null)) {
        new MensagemJS(response).showErro("Período de infração incompleto!");
        return;
    }
    if (sIdEnquadramento == null || !Pattern.matches("[0-9]{1,8}",sIdEnquadramento)) {
        new MensagemJS(response).showErro("Enquadramento selecionado inválido!");
        return;
    }
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 2ª Linha
	///////////////////////////////////////////////////////////////////////////////	
    if (sIdImagemIni != null && !Pattern.matches("[1-9][0-9]{0,7}", sIdImagemIni)) {
        new MensagemJS(response).showErro("Identificador de imagem inicial enviado inválido!");
        return;
    }
    if (sIdImagemFim != null && !Pattern.matches("[1-9][0-9]{0,7}", sIdImagemFim)) {
        new MensagemJS(response).showErro("Identificador de imagem final enviado inválido!");
        return;
    }
    if ((sIdImagemIni != null && sIdImagemFim == null) ||
        (sIdImagemFim != null && sIdImagemIni == null)) {
        new MensagemJS(response).showErro("Limites de indentificador de imagem incompletos!");
        return;
    }
    
    // XXX
    if (sEquip != null && !Pattern.matches("[0-9]{1,8}",sEquip)) {
        new MensagemJS(response).showErro("Número Equipamento inválido!");
        return;
    }
    
    if (sTipo != null && !Pattern.matches("[0-9]{1,8}",sTipo)) {
        new MensagemJS(response).showErro("Tipo Equipamento inválido!");
        return;
    }
    
    if (sLocal != null && !Pattern.matches("[0-9]{1,8}",sLocal)) {
        new MensagemJS(response).showErro("Local selecionado inválido!");
        return;
    }

    if (sProcesso != null && !Pattern.matches("[0-9]{1,8}",sProcesso)) {
        new MensagemJS(response).showErro("Processo selecionado inválido!");
        return;
    }
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 3ª Linha
	///////////////////////////////////////////////////////////////////////////////
    if (sAutoIni != null && !Pattern.matches("[1-9][0-9]{0,7}",sAutoIni)) {
        new MensagemJS(response).showErro("Auto inicial enviado inválido!");
        return;
    }
	
    if (sAutoFim != null && !Pattern.matches("[1-9][0-9]{0,7}",sAutoFim)) {
        new MensagemJS(response).showErro("Auto final enviado inválido!");
        return;
    }
    
    if ((sAutoIni != null && sAutoFim == null) ||
        (sAutoFim != null && sAutoIni == null)) {
        new MensagemJS(response).showErro("Limites de indentificador de auto incompletos!");
        return;
    }
    
    if (sPlaca != null && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca)) {
        new MensagemJS(response).showErro("Placa enviada inválida!");
        return;
    }
    
    if (sTipoVeiculo != null && (!Pattern.matches("[POCMT]", sTipoVeiculo) && !Pattern.matches("[0-9]+", sTipoVeiculo))) {
        new MensagemJS(response).showErro("Tipo de veículo enviado é inválido!");
        return;
    }
    
    if (sImagemInconsistencia != null && !Pattern.matches("[0-2]{1}", sImagemInconsistencia)) {
    	System.out.println(sImagemInconsistencia);
        new MensagemJS(response).showErro("Tipo de Imagem selecionada é inválido!");
        return;
    }    
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 4ª Linha
	///////////////////////////////////////////////////////////////////////////////
    if (sAproveitaveis != null && !Pattern.matches("[0-3]", sAproveitaveis)) {
        new MensagemJS(response).showErro("Opção aproveitáveis selecionada é inválida!");
        return;
    }    
   
    if (sJustificativaAproveitaveis != null && !Pattern.matches("-1|\\d*", sJustificativaAproveitaveis)) {
        new MensagemJS(response).showErro("Opção justificativa para aproveitáveis é inválida!");
        return;
    }      

    if (sRgOperadorAproveitavel != null && !Pattern.matches("\\d{1,10}", sRgOperadorAproveitavel)) {
        new MensagemJS(response).showErro("RG do técnico administrativo enviado é inválido!");
        return;
    }

    if (sEspera != null && !sEspera.equals("checked")) {
        new MensagemJS(response).showErro("Marcador de espera inválido!");
        return;
    }
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 5ª Linha
	///////////////////////////////////////////////////////////////////////////////
    if (sValidaveis != null && !Pattern.matches("[0-3]", sValidaveis)) {
        new MensagemJS(response).showErro("Opção validáveis selecionada é inválida!");
        return;
    }    
   
    if (sJustificativaValidaveis != null && !Pattern.matches("-1|\\d*", sJustificativaValidaveis)) {
        new MensagemJS(response).showErro("Opção justificativa para validáveis é inválida!");
        return;
    } 

    if (sDataValidaveisIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataValidaveisIni)) {
        new MensagemJS(response).showErro("Data inicial para validáveis enviada é inválida!");
        return;
    }
    if (sDataValidaveisFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataValidaveisFim)) {
        new MensagemJS(response).showErro("Data final para validáveis enviada é inválida!");
        return;
    }
    
    if (sHoraValidaveisIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraValidaveisIni)) {
        new MensagemJS(response).showErro("Hora inicial para validáveis enviada é inválida!");
        return;
    }
    
    if (sHoraValidaveisFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraValidaveisFim)) {
        new MensagemJS(response).showErro("Hora final para validáveis enviada é inválida!");
        return;
    }
    
    if ((sDataValidaveisIni != null || sDataValidaveisIni != null ||
   		sDataValidaveisFim != null || sHoraValidaveisFim != null) &&
       	(sDataValidaveisIni == null || sHoraValidaveisIni == null ||
       	sDataValidaveisFim == null || sHoraValidaveisFim == null)) {
		new MensagemJS(response).showErro("Período para validáveis está incompleto!");
		return;
	}    
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 6ª Linha
	///////////////////////////////////////////////////////////////////////////////    
    
    if (sValidas != null && !Pattern.matches("[0-3]",  sValidas)) {
        new MensagemJS(response).showErro("Opção válidas selecionada é inválida!");
        return;
    }    
   
    if (sJustificativaValidas != null && !Pattern.matches("-1|\\d*", sJustificativaValidas)) {
        new MensagemJS(response).showErro("Opção justificativa para válidas é inválida!");
        return;
    } 	
	
    if (sDataValidasIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataValidasIni)) {
        new MensagemJS(response).showErro("Data inicial para válidas enviada é inválida!");
        return;
    }
    if (sDataValidasFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataValidasFim)) {
        new MensagemJS(response).showErro("Data final para válidas enviada é inválida!");
        return;
    }
    
    if (sHoraValidasIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraValidasIni)) {
        new MensagemJS(response).showErro("Hora inicial para válidas enviada é inválida!");
        return;
    }
    
    if (sHoraValidasFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])", sHoraValidasFim)) {
        new MensagemJS(response).showErro("Hora final para válidas enviada é inválida!");
        return;
    }
    
    if ((sDataValidasIni != null || sDataValidasIni != null ||
   		sDataValidasFim != null || sHoraValidasFim != null) &&
       	(sDataValidasIni == null || sHoraValidasIni == null ||
       	sDataValidasFim == null || sHoraValidasFim == null)) {
		new MensagemJS(response).showErro("Período para válidas está incompleto!");
		return;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Validação 7ª Linha
	///////////////////////////////////////////////////////////////////////////////
    if (sRgOperadorValidas != null && !Pattern.matches("\\d{1,10}", sRgOperadorValidas)) {
        new MensagemJS(response).showErro("RG do agente de trânsito enviado é inválido!");
        return;
    }
	
    if (sVelocidadeMinima != null && !Pattern.matches("[0-9]{1,3}", sVelocidadeMinima)) {
        new MensagemJS(response).showErro("Opção velocidade mínima selecionada é inválida!");
        return;
    }	

    if (sVelocidadeMaxima != null && !Pattern.matches("[0-9]{1,3}", sVelocidadeMaxima)) {
        new MensagemJS(response).showErro("Opção velocidade máxima selecionada é inválida!");
        return;
    }

    if (sFiltrarResultados != null && !Pattern.matches("[0-2]{1}", sFiltrarResultados)) {
        new MensagemJS(response).showErro("Opção filtrar resultados selecionada é inválida!");
        return;
    }    
    
	///////////////////////////////////////////////////////////////////////////////
	// Validação 15ª Linha
	///////////////////////////////////////////////////////////////////////////////
    if (sProduto != null && !Pattern.matches("[0-9]{1,8}",sProduto)) {
        new MensagemJS(response).showErro("Tipo Equipamento selecionado inválido!");
        return;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
	// Terceira Parte: Definir regras de filtros
    ///////////////////////////////////////////////////////////////////////////////////////
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    
	Integer iJustificativa = null;
	Integer iCodigoAgente = null;
	Boolean bEspera = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 1ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sIdInfracaoIni != null) {
        mFiltro.put("id_infracao_ini", Integer.parseInt(sIdInfracaoIni));
        mFiltro.put("id_infracao_fim", Integer.parseInt(sIdInfracaoFim));
    }
    if (sDataInfracaoIni != null) {
        mFiltro.put("data_infracao_ini", new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        	.parse(sDataInfracaoIni + " " + sHoraInfracaoIni + ":00").getTime()));
        mFiltro.put("data_infracao_fim", new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        	.parse(sDataInfracaoFim + " " + sHoraInfracaoFim + ":59").getTime()));
    }
    if (Integer.parseInt(sIdEnquadramento) > 0)
        mFiltro.put("id_enquadramento",Integer.parseInt(sIdEnquadramento));
    
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 2ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sIdImagemIni != null) {
        mFiltro.put("id_imagem_ini", Integer.parseInt(sIdImagemIni));
        mFiltro.put("id_imagem_fim", Integer.parseInt(sIdImagemFim));
    }
    
    if (sEquip != null && Integer.parseInt(sEquip) > 0)
        mFiltro.put("cod_pista_prodam",Integer.parseInt(sEquip));
    
    if (sTipo != null && Integer.parseInt(sTipo) > 0)
        mFiltro.put("tipo_equipamento",Integer.parseInt(sTipo));
    
    if (sLocal != null && Integer.parseInt(sLocal) > 0)
        mFiltro.put("id_local",Integer.parseInt(sLocal));
    
    if (list_iPista.size() > 0)
        mFiltro.put("pista", list_iPista);
    
    if (sProcesso != null && Integer.parseInt(sProcesso) > 0)
        mFiltro.put("id_processo",Integer.parseInt(sProcesso));

    if (sEnquadInfracao != null)
        mFiltro.put("enquadInfracao",sEnquadInfracao);

    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: Terceira Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sAutoIni != null) {
        mFiltro.put("auto_ini", Integer.parseInt(sAutoIni));
        mFiltro.put("auto_fim", Integer.parseInt(sAutoFim));
    }
    
    if (sMovimentoLote != null) {
        mFiltro.put("movimentoLote", Integer.parseInt(sMovimentoLote));
    }
    
    if (sPlaca != null)
        mFiltro.put("placa", sPlaca);
    
	if("1".equals(sImagemInconsistencia) || "0".equals(sImagemInconsistencia)) {
		mFiltro.put("razao_tecnica", Integer.parseInt(sImagemInconsistencia));
	}

	if(sTipoVeiculo != null && sTipoVeiculo.length() > 0) {
		mFiltro.put("id_classe", sTipoVeiculo);
	}
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 4ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sAproveitaveis != null) {
    	Integer iAproveitaveis = Integer.parseInt(sAproveitaveis);
    	if (iAproveitaveis == 0 || iAproveitaveis == 1)
	        mFiltro.put("aproveitaveis", iAproveitaveis);
    	else if (iAproveitaveis == 2)
    		mFiltro.put("aproveitaveis", null);        
        
    }
    
	if (sJustificativaAproveitaveis != null) {
		iJustificativa = Integer.parseInt(sJustificativaAproveitaveis);
	}
	
	if (sRgOperadorAproveitavel != null) {
		iCodigoAgente = Integer.parseInt(sRgOperadorAproveitavel);
	}
	
	if (sEspera != null && sEspera.equals("checked")) {
		bEspera = true;
	}
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 5ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sValidaveis != null) {
    	int iValidaveis = Integer.parseInt(sValidaveis);
    	if (iValidaveis == 0 || iValidaveis == 1)
	        mFiltro.put("validaveis", iValidaveis);
    	else if (iValidaveis == 2)
    		mFiltro.put("validaveis", null);    		
    }
    
	if (sJustificativaValidaveis != null) {
		iJustificativa = Integer.parseInt(sJustificativaValidaveis);
	}
	
    if (sDataValidaveisIni != null) {
        mFiltro.put("data_validavel_ini", new Timestamp(
       		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
       		.parse(sDataValidaveisIni + " " + sHoraValidaveisIni + ":00").getTime()));
        
        mFiltro.put("data_validavel_fim", new Timestamp(
       		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
       		.parse(sDataValidaveisFim + " " + sHoraValidaveisFim + ":00").getTime()));
    }	    
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 6ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sValidas != null) {
    	int iValidas = Integer.parseInt(sValidas);
    	if (iValidas == 0 || iValidas == 1)
	        mFiltro.put("validas", iValidas);
    	else if (iValidas == 2)
    		mFiltro.put("validas", null);    		
    }
    
	if (sJustificativaValidas != null) {
		iJustificativa = Integer.parseInt(sJustificativaValidas);
	}     
    
    if (sDataValidasIni != null) {
        mFiltro.put("data_validas_ini", new Timestamp(
       		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
       		.parse(sDataValidasIni + " " + sHoraValidasIni + ":00").getTime()));
        
        mFiltro.put("data_validas_fim", new Timestamp(
       		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
       		.parse(sDataValidasFim + " " + sHoraValidasFim + ":00").getTime()));
    }
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 7ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
	if (sRgOperadorValidas != null) {
		iCodigoAgente = Integer.parseInt(sRgOperadorValidas);
	}	
	
	if(sVelocidadeMinima != null && !"".equals(sVelocidadeMinima) &&
		Pattern.matches("[0-9]{1,8}", sVelocidadeMinima)) {
		mFiltro.put("velocidade_minima", Integer.parseInt(sVelocidadeMinima));
	}
    
	if(sVelocidadeMaxima != null && !"".equals(sVelocidadeMaxima) &&
		Pattern.matches("[0-9]{1,8}", sVelocidadeMaxima)) {
		mFiltro.put("velocidade_maxima", Integer.parseInt(sVelocidadeMaxima));
	}	
	
	if(sFiltrarResultados != null && ("1".equals(sFiltrarResultados) || "2".equals(sFiltrarResultados))) {
		mFiltro.put("filtrar_resultados", Integer.parseInt(sFiltrarResultados));
	}
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Filtros: 15ª Linha
    ///////////////////////////////////////////////////////////////////////////////////////
    if (sProduto != null && Integer.parseInt(sProduto) > 0) {
        mFiltro.put("id_produto",Integer.parseInt(sProduto));
    }

	// *************************************************************	
	// Parte comum entre aproveitavel, validável, válida. 
	// *************************************************************	
    // Define a justificativa (inconsistência). 
    if (iJustificativa != null)
		mFiltro.put("inconsistencia", iJustificativa);

    // Define o código do agente. 
    if (iCodigoAgente != null)
		mFiltro.put("cod_agente", iCodigoAgente);	

    // Define o código do agente. 
    if (bEspera != null)
		mFiltro.put("espera", bEspera);	

    
    Integer idUsuario = ((Usuario)session.getAttribute("[usuario]")).getId();
    Boolean grupoVelsis = false;
	
    List<InfracaoCompletaLista> infracoes = null;
    try {
    	infracoes = InfracaoCompletaLista.buscaInfracaoCompletaPor(mFiltro, 12, true);
    }
    catch (Exception err) {
    	logger.error("Erro ao buscar infração completa: "+err.getMessage(),err);
        new MensagemJS(response).showErro("Não foi possível realizar a busca no banco de dados!");
        return;
    }
	
    request.getSession().setAttribute("infracao_completa", infracoes);
    
    InfracaoCompletaBean icli;
	List<InfracaoCompletaBean> licli = new ArrayList<InfracaoCompletaBean>();
	SimpleDateFormat formatoDiaSemana = new SimpleDateFormat("EE", new Locale("pt", "BR"));
	
	for(InfracaoCompletaLista i : infracoes) {
		icli = new InfracaoCompletaBean();
		icli.setAuto(i.getAuto());
		icli.setDataVeiculo(i.getDataVeiculo());
		icli.setId(i.getId());
		icli.setIdEnquadramento(i.getIdEnquadramento());
		icli.setIdImagemLocal(i.getIdImagemLocal());
		icli.setMarca(i.getMarca());
		icli.setNomeLocal(i.getNomeLocal());
		icli.setPista(i.getPista());
		icli.setPlaca(i.getPlaca());
		icli.setSerie(i.getSerie());
		icli.setTipoRemessa(i.getTipoRemessa());
		icli.setVelocidade(i.getVelocidade());
		
		icli.setDiaSemana(formatoDiaSemana.format(i.getDataVeiculo()));
		icli.setClasse(i.getClasse());
		
		licli.add(icli);
	}
    InfracaoCompletaBean.setListaParaRelatorio(licli);
%>
<%@page import="com.consilux.exportalista.InfracaoCompletaBean"%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.List"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.InfracaoCompletaLista"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.exportalista.InfracaoCompletaBean"%>
<%@page import="com.consilux.exportalista.ExportaLista"%>
<%@page import="org.apache.log4j.Logger"%><c:set var="infracoes" value="<%=infracoes%>" />
<script type="text/javascript">
	function mostraDetalhes(idInfracao) {
		window.open("/infracao/infracao_completa.jsp?id_infracao="+idInfracao,"Detalhes","width=1024, height=600");
	}
	function encadeiaInfracaoImagem(idInfracaoAtual, idInfracaoAnterior) {
		var link_imagem_anterior = document.getElementById("link_imagem_"+idInfracaoAnterior);
        var link_imagem_atual = document.getElementById("link_imagem_"+idInfracaoAtual);

        if (link_imagem_anterior) { 
            link_imagem_anterior.idInfracaoProximo = idInfracaoAtual;
            link_imagem_atual.idInfracaoAnterior = idInfracaoAnterior;
        }
        link_imagem_atual.idInfracao = idInfracaoAtual;
	}
	function mostraImagem(idInfracao) {
        window.open("/infracao/infracao_imagem.jsp?id_infracao="+idInfracao+"&encadeado", "Imagem","width=750, height=700");
	}
	function getIdInfracaoAnterior(idInfracao) {
		var ret = 0;
        var link_imagem = document.getElementById("link_imagem_"+idInfracao);
        
        if (link_imagem) {
        	ret = link_imagem.idInfracaoAnterior; 
        } 
        return ret;
	}
    function getIdInfracaoProximo(idInfracao) {
        var ret = 0;
        var link_imagem = document.getElementById("link_imagem_"+idInfracao);
        
        if (link_imagem) {
            ret = link_imagem.idInfracaoProximo; 
        } 
        return ret;
    }
	parent.limparContadorRegistros();
</script>
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="1000">
	            <tr> <!-- XXX: Felipe -->
	                <th class="head_tabela" width="4%" >Nº Infração</th>
                    <th class="head_tabela" width="4%" >Nº Imagem</th>
                    <th class="head_tabela" width="4%" >Nº Auto</th>
                    <th class="head_tabela" width="5%" >Tipo Remessa</th>
                    <th class="head_tabela" width="16%">Data Infração</th>
<!--                     <th class="head_tabela" width="16%">Data Inclusão</th> -->
                    <th class="head_tabela" width="3%" >Dia Sem.</th>
                    <th class="head_tabela" width="5%" >Enquad.</th>
                    <th class="head_tabela" width="2%" >Vel.</th>
	                <th class="head_tabela" width="5%" >Placa</th>
	                <th class="head_tabela" width="13%">Marca</th>
	                <th class="head_tabela" width="2%" >Classe</th>
	                <th class="head_tabela" width="36%">Local</th>
	                <th class="head_tabela" width="2%" >Faixa</th>  
                    <th class="head_tabela" width="15%">Ação</th>
	            </tr>
                <c:set var="idInfracaoAnterior" value="" />
	            <c:forEach var="infracao" varStatus="linhaInfo" items="${infracoes}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})">${infracao.id}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})">${infracao.idImagemLocal}</a></td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})">${infracao.auto}</a></td>
                        <td class="${css_td}" align="center">${infracao.tipoRemessa}</td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})"><fmt:formatDate value="${infracao.dataVeiculo}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></a></td>
<%--                         <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})"><fmt:formatDate value="${infracao.dataEntrada}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></a></td> --%>
                        <td class="${css_td}" align="center">${infracao.diaSemana}</td>
                        <td class="${css_td}" align="center"><a class='link_td' href="javascript:mostraDetalhes(${infracao.id})">${infracao.idEnquadramento}</a></td>
                        <td class="${css_td}" align="center">${infracao.velocidade}</td>
                        <td class="${css_td}" align="center">${infracao.placa}</td>
                        <td class="${css_td}" align="center">${infracao.marca}</td>
                        <td class="${css_td}" align="center">${infracao.classe}</td>
                        <td class="${css_td}" align="center">${infracao.nomeLocal}</td>
                        <td class="${css_td}" align="center">${infracao.pista}</td>  
                        <td class="${css_td}" align="center">
							<a id="link_imagem_${infracao.id}" class='link_td' href="javascript:mostraImagem(${infracao.id})">[ver&nbsp;imagem]</a>
                            <script type="text/javascript">encadeiaInfracaoImagem('${infracao.id}', '${idInfracaoAnterior}')</script>
                        </td>
                        <c:set var="idInfracaoAnterior" value="${infracao.id}" />
	                </tr>
					<script type="text/javascript">
						parent.adicInfracao(${infracao.id});
					</script>
	            </c:forEach>
	        </table>
        </td>
    </tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
