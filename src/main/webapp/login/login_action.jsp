<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.infra.SessaoFinalizaAdapter"%>
<%@page import="com.consilux.infra.SessaoFinalizaManager"%>
<%@page import="com.consilux.infra.exception.ConexaoException"%><%@page import="java.sql.Connection"%>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="com.consilux.lib.Conexao"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8" session="false"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="muralha.digital.acessos.ConfiguracaoInatividadeServlet"%>
<%@page import="muralha.digital.acessos.ConfiguracaoInatividadeResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="muralha.digital.alerta.Alertas"%>
<%@page import="muralha.digital.notificacao.ConfiguracaoSons"%>
<%@page import="muralha.digital.acesso.SenhaService"%>
<%
    Conexao conexao = Conexao.initConexao();

	String sLogin  = request.getParameter("login");
	String sSenha  = request.getParameter("senha");
	String sIP     = request.getParameter("ip");
	String sRetUrl = request.getParameter("retUrl");
	String sManterConectado = request.getParameter("manterConectado");

	// Aqui abaixo foram declaradas 2 variáveis booleanas para verificação do redirecionamento,
	// elas iniciam falsa pois assim na validação final desse código, entrará na página inicial padrão.
	Boolean bAlerta = false;
	Boolean bRedirecionaAlerta = false;
	
	if (sLogin == null || !Pattern.matches("[A-Z0-9\\.]{2,30}",sLogin.toUpperCase())) {
		new Mensagem(response).showErroMuralha("Usuário não informado!");
		return;
	}
	else if (sSenha == null || !Pattern.matches("\\p{Print}{1,20}",sSenha.toUpperCase())) {
		new Mensagem(response).showErroMuralha("Senha não informada!");
		return;
	}
	
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("usuario", sLogin );
	mFiltro.put("ativo", 1 );
	List<Usuario> usus;
	
	try {
		usus = Usuario.buscaUsuarioPor(mFiltro);
	} catch (ConexaoException ex) {
		new Mensagem(response).showErroMuralha("Não foi possível conectar-se ao banco de dados.");
		return;
	}
	if (usus.size() == 0) {
		new Mensagem(response).showErroMuralha("Usuário ou senha incorretos.", "../login/login.jsp");
		return;
	}
	
	Usuario usuario = usus.get(0);

	// Verificar bloqueio antes de comparar senha
	try {
		String erroBloqueio = SenhaService.verificarBloqueio(sLogin);
		if (erroBloqueio != null) {
			new Mensagem(response).showErroMuralha(erroBloqueio, "../login/login.jsp");
			return;
		}
	} catch (Exception eBloqueio) { /* falha silenciosa, não impede o login */ }

	if (!usuario.comparaSenha(sSenha)) {
		try { SenhaService.registrarFalha(sLogin); } catch (Exception eFalha) {}
		new Mensagem(response).showErroMuralha("Usuário ou senha incorretos.", "../login/login.jsp");
		return;
	}
	else {
		// Verificar se usuário tem MFA habilitado antes de criar a sessão definitiva
		boolean mfaHabilitado = false;
		String totpSegredo = null;
		{
			java.sql.Connection mfaConn = null;
			java.sql.PreparedStatement mfaPs = null;
			java.sql.ResultSet mfaRs = null;
			try {
				mfaConn = Conexao.getConexao();
				mfaPs = mfaConn.prepareStatement(
						"SELECT totp_habilitado, totp_secret FROM dbo.sis_usuario WHERE id=?");
				mfaPs.setInt(1, usuario.getId());
				mfaRs = mfaPs.executeQuery();
				if (mfaRs.next()) {
					mfaHabilitado = mfaRs.getBoolean("totp_habilitado");
					totpSegredo   = mfaRs.getString("totp_secret");
				}
			} catch (Exception eMfa) { /* coluna pode não existir ainda — MFA não aplicado */ }
			finally {
				if (mfaRs != null) try { mfaRs.close(); } catch (Exception e2) {}
				if (mfaPs != null) try { mfaPs.close(); } catch (Exception e2) {}
				if (mfaConn != null) try { mfaConn.close(); } catch (Exception e2) {}
			}
		}

		if (mfaHabilitado && totpSegredo != null) {
			HttpSession sAntiga = request.getSession();
			SessaoFinalizaManager.removeSessaoFinaliza(sAntiga);
			sAntiga.invalidate();
			HttpSession sTemp = request.getSession(true);
			sTemp.setMaxInactiveInterval(300); // 5 min para completar o MFA
			sTemp.setAttribute("mfa_pendente_usuario", usuario);
			sTemp.setAttribute("mfa_pendente_segredo", totpSegredo);
			sTemp.setAttribute("mfa_pendente_sip", sIP);
			sTemp.setAttribute("mfa_pendente_retUrl", request.getParameter("retUrl"));
			response.sendRedirect("mfa_codigo.jsp");
			return;
		}

		//Cria a sessão aqui porque esta pagina só deve criar uma sessão depois que a autenticação estiver ok:
		HttpSession sessaoAntiga = request.getSession();
		SessaoFinalizaManager.removeSessaoFinaliza(sessaoAntiga); //Garante que os listeners de finalização da sessão antiga seja executado.
		sessaoAntiga.invalidate();
		HttpSession sessaoNova = request.getSession(true);
		
		ConfiguracaoInatividadeResult configs = ConfiguracaoInatividadeServlet.buscarConfigsInatividade();
		
		if(configs.LoginNuncaBloqueia.getValor() == 1){
			sessaoNova.setMaxInactiveInterval(-1);
			sessaoNova.setAttribute("manterConectado", true);
		}else{
			sessaoNova.setMaxInactiveInterval(configs.LoginTempoInatividade.getValor());
		}

		if (request.isSecure()) {
		    // Se a requisição veio por HTTPS
		    Cookie cookie = new Cookie("JSESSIONID", sessaoNova.getId());
		    cookie.setPath(request.getContextPath());
		    cookie.setSecure(request.isSecure()); // true se HTTPS
		    response.addCookie(cookie);
		} else {
		    // Se veio por HTTP
		    Cookie cookie = new Cookie("JSESSIONID_HTTP", sessaoNova.getId());
		    cookie.setPath(request.getContextPath());
		    cookie.setSecure(request.isSecure()); // true se HTTPS
		    response.addCookie(cookie);
		}
		
		sessaoNova.setAttribute(SessaoConstantes.SESSAO_USUARIO,usuario);
		LogonLogoff ll = LogonLogoff.inserirLogonLogoff(usuario.getId(), request.getRemoteAddr(), sIP);
		
		if (ll == null)
		{
			new Mensagem(response).showErroMuralha("Host não reconhecido!");
			return;
		}

		SessaoFinalizaManager.adicSessaoFinaliza(sessaoNova, ll);
	}

	// Aqui abaixo no try e no if é feito uma validação do bRedirecionaAlerta, que pega da função do banco true ou false
	// para validar se o usuário será redirecionado para a tela de alertas ou para a tela inicial padrão.
	try {
		bRedirecionaAlerta = ConfiguracaoSons.buscarLoginRedirecionaTelaAlerta();
	} catch (Exception ex) {
		new Mensagem(response).showErroMuralha("Erro ao validar alerta.");
		return;
	}

	if (bRedirecionaAlerta == true) {
		try {
			bAlerta = Alertas.buscarAlertasPendentes();
		} catch (Exception ex) {
			new Mensagem(response).showErroMuralha("Erro ao validar usuário.");
			return;
		}		
	}
	
	//Autenticação Ok, agora só falta redirecionar para pagina principal:
	if (sRetUrl != null && sRetUrl.length() > 0) {
		sRetUrl = request.getParameter("retUrl");
		try {
			sRetUrl = URLDecoder.decode(sRetUrl,"UTF-8");
		}
		catch(Exception e) { }
		
	// Zerar tentativas inválidas após login bem-sucedido
	try { SenhaService.zerarTentativas(sLogin); } catch (Exception eZerar) {}

	} else if (usuario.isAlterarSenha() || SenhaService.senhaExpirada(usuario.getId())) {
		sRetUrl = "/login/login_change.jsp";
	} else {
		// Aqui fará uma validação final do bAlerta, se o valor recebido do banco for true,
		// a tela inicial será de alertas, caso contrário, será a tela padrão.
		if (bAlerta == true) {
			sRetUrl = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp";
		} else {
			sRetUrl = "/login/abertura-sistemas.jsp";
		}
	}
	response.sendRedirect(sRetUrl);
%>
