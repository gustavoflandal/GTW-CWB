package muralha.digital.acessos;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.consilux.infra.SessaoConstantes;
import com.consilux.infra.SessaoFinalizaManager;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.LogonLogoff;
import com.consilux.model.Mensagem;
import com.google.gson.JsonObject;

import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.util.Utils;

@WebServlet("/MuralhaDigital/Usuarios")
public class UsuarioServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UsuarioServlet.class);
    private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

    public UsuarioServlet() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try
    	{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}  
	    	if(strAcao.equals("obterListaUsuariosAtivos"))	    	
	    		obterListaUsuariosAtivos(request, response);
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de Alerta: " + e.getMessage(), e);
	    }       
        
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {

    	String senhaMD5 	= request.getParameter("senhaMD5");
       	String usuario 		= request.getParameter("usuario");
       	String acao 		= request.getParameter("acao");
       	
       	try
       	{
       		logger.debug("doPost - " + usuario + " - " + senhaMD5 + " - " + acao);
       		
	       	if(acao.equals("login"))
	       	{
	       		limpaSessaoAtual(request, response);
	       		validaUsuario(request, response, usuario, senhaMD5);
	       		return;
	       	}
	       	
	       	else if(acao.equals("openview"))
	       	{	       		
				validaAcessoView(request, response);				
	       		return;
	       	}
	       	
	       	else if(acao.equals("out"))
	       	{
	       		fechaSessao(request, response);
	       		return;
	       	}	  
	       	
	       	else if(acao.equals("login_google_token"))
	       	{
	       		String google_token = request.getParameter("google_token");
	       		cria_sessao_usuario(request, response, google_token);
	       		return;
	       	}		       	
	       	
	    	else if (acao.equals("acessoViaToken"))
	    		acessoViaToken(request, response);

	       	else
	       	{
	       		fechaSessao(request, response);
	       		return;
	       	}
       	
       	} 
       	catch (ConexaoException e) 
       	{
       		logger.error("Erro ao processar requisicao. " + e.getMessage(), e);
		}	       	
       		
    }
  
    private void cria_sessao_usuario(HttpServletRequest request, 
    								 HttpServletResponse response, 
    								 String google_token) 
    {
    	try {

	    	Map<String,Object> mFiltro = new HashMap<String,Object>();
	    	mFiltro.put("usuario", "luiz.amaral");
	    	mFiltro.put("ativo", 1 );
	    	com.consilux.model.Usuario usuario;
	    	
	    	
	    	try {
	    		usuario = com.consilux.model.Usuario.buscaUsuarioPorGoogleToken(google_token);
	    	} catch (ConexaoException ex) {
	    		new Mensagem(response).showErroMuralha("Não foi possível conectar-se ao banco de dados.");
	    		return;
	    		
	    	}
	    	
	    	if ( usuario == null) {
	    		new Mensagem(response).showErroMuralha("Usuario não encontrado na base de dados com seu respectivo google token.");
	    		return;	    		
	    	}
  		    		    	
			HttpSession sessaoAntiga = request.getSession();
			SessaoFinalizaManager.removeSessaoFinaliza(sessaoAntiga); 
			sessaoAntiga.invalidate();
			
			HttpSession sessaoNova = request.getSession(true);
			sessaoNova.setAttribute(SessaoConstantes.SESSAO_USUARIO,usuario);

			LogonLogoff ll = LogonLogoff.inserirLogonLogoff(usuario.getId(), request.getRemoteAddr(), "0.0.0.0");
			
			SessaoFinalizaManager.adicSessaoFinaliza(sessaoNova, ll);
    	
    	}catch (Exception e) {
    		{logger.error("Erro ao executar cria_sessao_usuario()" + e.getMessage(), e);}
		} 
    }
  
    @SuppressWarnings("static-access")
	private void validaUsuario(	HttpServletRequest request, 
    							HttpServletResponse response, 
    							String usuario, 
    							String senhaMD5) throws ServletException, IOException 
    {
       	PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		 
		JsonObject myObj = new JsonObject();
		
		String endereco_ip = Utils.getClientIp(request);
		Usuario resultado = Usuario.validarUsuario(usuario, senhaMD5, endereco_ip);
		
		if(resultado != null)
		{
			HttpSession session = request.getSession();				
	        session.setAttribute("nome", resultado.getNome());
	        session.setAttribute("usuario", resultado.getUsuario());
	        session.setAttribute("idUsuario", resultado.getIdUsuario());
	        session.setAttribute("idGrupo", resultado.getIdGrupo());
	        session.setAttribute("descGrupo", resultado.getDescGrupo());
	        session.setAttribute("token", resultado.getToken());
	        
	        logger.debug("Token: " + resultado.getToken());
	        
	        //Setando objeto completo
	        session.setAttribute("objUsuario", resultado); 
	        session.setAttribute("listaMenus", this.ObterListaMenusAcesso(resultado.getIdUsuario(), 0));
		}
		
		myObj.addProperty("validacao", resultado != null);
		myObj.addProperty("senhaMD5", senhaMD5);
		myObj.addProperty("usuario", usuario);
		myObj.addProperty("paginaInicial", resultado != null ? resultado.getPaginaInicial() : "");
		
		if (resultado != null && resultado.getToken() != null)
			myObj.addProperty("token", resultado.getToken());
		else 
			myObj.addProperty("token", "");
		
		out.println(myObj.toString());
		
		out.close();
    }
    
    private void limpaSessaoAtual(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	HttpSession session = request.getSession();
    	String usuario = (String) session.getAttribute("usuario");
    	
    	logger.info("Fechando sessão do usuário: " + usuario);
    	
		session.invalidate();
		
		return;
    }
    
    private void fechaSessao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	HttpSession session = request.getSession(false); //getSession(false) impede que outra sessão seja criada para esse usuário
    	Usuario objUsuario 	= (Usuario) session.getAttribute("objUsuario");	
//    	String usuario = (String) session.getAttribute("[usuario]");
    	com.consilux.model.Usuario objUsuario2 = (com.consilux.model.Usuario) session.getAttribute("[usuario]");

    	if (objUsuario != null && objUsuario.getToken() != null) {
    		logger.debug("Token: " + objUsuario.getToken());
    		EncerrarToken(objUsuario.getToken());
    	}
    	
    	logger.info("Fechando sessão do usuário: " + objUsuario2.getUsuario());	    	
		session.invalidate();

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		JsonObject myObj = new JsonObject();
		
		myObj.addProperty("validacao", false);
		out.println(myObj.toString());			
		out.close();			
		
		return;
    }
    
    public static boolean emSessao(HttpServletRequest request)
    {
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		
		boolean retorno = false;
		
		if(usuario != null)
			retorno = true;
		else
			session.invalidate();			

		return retorno;
    }
    
    private void validaAcessoView( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ConexaoException 
	{
    	logger.debug("validaAcessoView");
    	
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		JsonObject myObj = new JsonObject();
		
		HttpSession session 	= request.getSession();
		Usuario objUsuario 	= (Usuario) session.getAttribute("objUsuario");			
		String url = request.getHeader("x-gtw-custom-header");
		String token = null;
		String endereco_ip = Utils.getClientIp(request);
		
		try 
		{
			for(Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
					break;
				}
			}
		}
		catch(Exception e) { 
			logger.debug("Falha ao ler cookies");
		}
		
		logger.debug("! Token: " + token + " de Endereco IP " + endereco_ip);
		
		if (objUsuario == null && token != null) 
		{
			objUsuario = ObterUsuarioToken(token, endereco_ip);
			
			if(objUsuario != null)
			{		
		        session.setAttribute("nome", objUsuario.getNome());
		        session.setAttribute("usuario", objUsuario.getUsuario());
		        session.setAttribute("idUsuario", objUsuario.getIdUsuario());
		        session.setAttribute("idGrupo", objUsuario.getIdGrupo());
		        session.setAttribute("descGrupo", objUsuario.getDescGrupo());
		        session.setAttribute("token", objUsuario.getToken());
		        
		        //Setando objeto completo
		        session.setAttribute("objUsuario", objUsuario); 
		        session.setAttribute("listaMenus", ObterListaMenusAcesso(objUsuario.getIdUsuario(), 0));			        
			}
		}
		else {
			
			if (token != null)
			UsuarioToken(token, endereco_ip);
			
		}
			
		
		if(objUsuario == null)
		{
			logger.info("Acesso a pagina nao permitido. Motivo: Usuario sem sessao! Pagina: " + url);
			myObj.addProperty("validacao", false);
			fechaSessao(request, response);
		}
		else if(!verificaAcesso(url, objUsuario))
		{
			logger.info("Acesso a pagina NAO PERMITIDO. Pagina: " + request.getRequestURI() + " Usuario: " + objUsuario.getNome());
			myObj.addProperty("validacao", false);
			fechaSessao(request, response);
		}
		else
		{	
			myObj.addProperty("validacao", 	true);
			myObj.addProperty("usuario", 	objUsuario.getUsuario());
			myObj.addProperty("nome", 		objUsuario.getNome());
			myObj.addProperty("descGrupo", 	objUsuario.getDescGrupo());
			myObj.addProperty("idUsuario", 	objUsuario.getIdUsuario());
		}

		out.println(myObj.toString());
		
		out.close();
	}	    
    
    private void EncerrarToken(String token) {
    	Connection conn = null;
		CallableStatement cs = null;
		
		try
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{call spu_ppv_sis_usuario_token_encerra (?)}");
			cs.setString(1,token);
			cs.execute();
		}
		catch(Exception e)
		{
			logger.error("Erro ao encerrar Token", e);
		}
		finally 
		{
			try 
			{
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
    }
    
	private static Usuario ObterUsuarioToken(String token, String endereco_ip) {

		Usuario su = null;
		
		Connection conn = null;
		CallableStatement cs = null;
		
		logger.debug("Obtendo Token: " + token + ", endereco_ip: " + endereco_ip );
		
		try
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{? = call spu_ppv_sis_usuario_token_valida (?,?)}");
			cs.registerOutParameter(1, Types.INTEGER);
			cs.setString(2,token);
			cs.setString(3, endereco_ip);
			cs.execute();
			Integer idUsuario = cs.getInt(1);
			
			if (idUsuario > 0) 
			{
				logger.info("ObterUsuarioToken()(1):: idUsuario: " + idUsuario);
				su = Usuario.validarUsuario(idUsuario);
				su.setToken(token);
				
			}
			else
				logger.info("ObterUsuarioToken()(1):: idUsuario ZERADO");
		}
		catch(Exception e)
		{
			logger.error("ObterUsuarioToken():: Erro ao obter Token", e);
		}
		finally 
		{
			try 
			{
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
				
		
		return su;
	}
	
	private static void UsuarioToken(String token, String endereco_ip) {
		Connection conn = null;
		CallableStatement cs = null;
		
		logger.debug("Validando Token: " + token);
		
		try
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{call spu_ppv_sis_usuario_token_valida (?,?)}");
			cs.setString(1,token);
			cs.setString(2, endereco_ip);
			cs.execute();
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter Token", e);
		}
		finally 
		{
			try 
			{
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
	}

	//Verifica o acesso do usuário.
	//gotoLogin Se True o usuário será direcionado para tela de login se esta não possui acesso.
	public boolean verificaAcesso(HttpServletRequest request, HttpServletResponse response) throws IOException, ConexaoException 
	{
		logger.debug("verificaAcesso");
		
		boolean retorno = false;
		HttpSession session 	= request.getSession();
		Usuario objUsuario 	= (Usuario) session.getAttribute("objUsuario");	
		String token = null;
		String endereco_ip = Utils.getClientIp(request);
		try 
		{
			for(Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
					break;
				}
			}
			
			if (token == null) {
				logger.debug("Nenhum Token encontrado");
			}
			else {
				logger.debug("Token: " + token);
				
				if (objUsuario == null)
					objUsuario = ObterUsuarioToken(token, endereco_ip);
			}
		}
		catch(Exception e) { 
			logger.debug("Falha ao ler cookies");
		}
		
		if (objUsuario == null) 
		{
			logger.info("verificaAcesso(): Usuário não está em sessão");
			retorno = false;
		} 
		else if(!verificaAcesso(request.getRequestURI(), objUsuario))
		{
			response.sendRedirect("/login/login.jsp");
			retorno = false;
		}
		else if(objUsuario.isAtivo() == false) {
			response.sendRedirect("/login/login.jsp");
			retorno = false;
		}
		else
		{
			retorno = true;
		}
					
		return retorno;
	}	    
    
	private boolean verificaAcesso(String url, Usuario usuario) throws ConexaoException
	{
		Connection conn 		= null;
		PreparedStatement ps 	= null;
		ResultSet rs		 	= null;
		
		logger.info("Verificando acesso na pagina: " + url + "  para IdUsuario: " + usuario.getIdUsuario() + " Nome: " + usuario.getNome() + " Gripo: " + usuario.getDescGrupo());
		
		int cnt = 0, iacesso = 0;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT cnt, acesso FROM fcn_VerificaAcesso(?, ?)");
			ps.setString(1, url);
			ps.setInt(2, usuario.getIdUsuario());
			rs = ps.executeQuery();
			if (rs.next())
			{
				cnt = rs.getInt(1);
				if (cnt > 0)
					iacesso = rs.getInt(2);
				else	
					logger.info("Pagina: " + url + " não está cadastrada na base de dados!");				
			}
			else
			{
				logger.info("Nao houve retorno na consulta de verificaAcesso(). Este usuário: " + usuario.getIdUsuario() + " ou pagina: " + url + " não estão cadastrados na base de dados!");
			}
		}
		catch(Exception e)
		{
			logger.error("Falha ao verificar acesso no sistema! " + e.getMessage(), e);
		}
		finally
		{
			try 
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) 
			{
				logger.error("Falha ao verificar acesso no sistema!" + e.getMessage(), e);
			}
		}
		
		return (cnt == 0) || (cnt > 0 && iacesso > 0);
	}
	
	public static List<Menu> ObterListaMenusAcesso(int idUsuario, int id_pai_menu_info)
	{
		
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		
		List<Menu> menus = new ArrayList<Menu>();
		
		try 
		{
			conn = Conexao.getConexao();		
			
			String strSQL = "";
			strSQL = strSQL + " SELECT ";
			strSQL = strSQL + " 	su.id_usuario, ";
			strSQL = strSQL + " 	su.usuario, ";
			strSQL = strSQL + " 	smi.id_infos, ";
			strSQL = strSQL + " 	smi.id_menu, ";
			strSQL = strSQL + " 	smi.src, ";
			strSQL = strSQL + " 	smi.descricao, ";
			strSQL = strSQL + " 	smi.href, ";
			strSQL = strSQL + " 	smi.descricao_detalhada ";
			strSQL = strSQL + " FROM sis_usuario su ";
			strSQL = strSQL + " 	inner join sis_usuario_grupo sug ";
			strSQL = strSQL + " 		on sug.id_usuario = su.id_usuario ";
			strSQL = strSQL + " 	inner join sis_grupo sg ";
			strSQL = strSQL + " 		on sg.id_grupo = sug.id_grupo ";
			strSQL = strSQL + " 	inner join sis_menu_direitos smd ";
			strSQL = strSQL + " 		on smd.id_grupo = sg.id_grupo ";
			strSQL = strSQL + " 	inner join sis_menu sm ";
			strSQL = strSQL + " 		on sm.id_menu = smd.id_menu ";
			strSQL = strSQL + " 	inner join sis_menu_infos smi ";
			strSQL = strSQL + " 		on smi.id_menu = sm.id_menu ";
			strSQL = strSQL + " WHERE su.id_usuario = ? ";
			strSQL = strSQL + "   and su.ativo = 1 ";
			strSQL = strSQL + "   and sm.ativo = 1 ";
			
			if (id_pai_menu_info > 0)
				strSQL = strSQL + "   and smi.menu_pai = ? ";
			else				
				strSQL = strSQL + "   and smi.menu_pai IS NULL ";	
			
			strSQL = strSQL + " GROUP BY ";
			strSQL = strSQL + " 	   su.id_usuario, ";
			strSQL = strSQL + " 	   su.usuario, ";
			strSQL = strSQL + " 	   smi.id_infos, ";
			strSQL = strSQL + " 	   smi.id_menu, ";
			strSQL = strSQL + " 	   smi.src, ";
			strSQL = strSQL + " 	   smi.descricao, ";
			strSQL = strSQL + " 	   smi.href, ";
			strSQL = strSQL + " 	   smi.descricao_detalhada, ";
			strSQL = strSQL + " 	   smi.ordenacao ";
			strSQL = strSQL + " ORDER BY ";
			strSQL = strSQL + " 	   smi.ordenacao ";
			
			stmt = conn.prepareStatement(strSQL);
			
			stmt.setInt(1, idUsuario);
			
			if (id_pai_menu_info > 0)
				stmt.setInt(2, id_pai_menu_info);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				Menu menu = new Menu();
				
				menu.setIdMenuInfo(				rs.getInt(3));
				menu.setIdMenu(					rs.getInt(4));
				menu.setSrc(					rs.getString(5));
				menu.setDescricao(				rs.getString(6));
				menu.setHref(					rs.getString(7));
				menu.setDescDetalhada(			rs.getString(8));
				
				menus.add(menu);
				
				logger.info("Menu de abertura: " + menu.getDescricao() + " Usuário: " + idUsuario);
			}
		} 
		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtenção da lista de menus do usuario! id:" + idUsuario + " Falha: " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return menus;
	}
	
	public static List<Menu> ObterListaMenusRelatorioAcesso(int idUsuario)
	{
		
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		List<Menu> menus = new ArrayList<Menu>();
		
		try 
		{
			conn = Conexao.getConexao();		
			
			sbSQL.append("");
			sbSQL.append(" SELECT su.id_usuario, ");
			sbSQL.append(" 		  su.usuario, ");
			sbSQL.append(" 		  smri.id_infos, ");
			sbSQL.append(" 		  smri.id_menu, ");
			sbSQL.append(" 		  smri.src, ");
			sbSQL.append(" 		  smri.descricao, ");
			sbSQL.append(" 		  smri.href, ");
			sbSQL.append(" 		  smri.descricao_detalhada ");
			sbSQL.append(" FROM   sis_usuario su ");
			sbSQL.append(" 		  INNER JOIN sis_usuario_grupo sug ");
			sbSQL.append(" 		  	   ON sug.id_usuario = su.id_usuario ");
			sbSQL.append(" 		  INNER JOIN sis_grupo sg ");
			sbSQL.append(" 		  	   ON sg.id_grupo = sug.id_grupo ");
			sbSQL.append(" 		  INNER JOIN sis_menu_direitos smd ");
			sbSQL.append(" 		  	   ON smd.id_grupo = sg.id_grupo ");
			sbSQL.append(" 		  INNER JOIN sis_menu sm ");
			sbSQL.append(" 		  	   ON sm.id_menu = smd.id_menu ");
			sbSQL.append(" 		  INNER JOIN sis_menu_relatorio_infos smri ");
			sbSQL.append(" 		  	   ON  smri.id_menu = sm.id_menu ");
			sbSQL.append(" WHERE  su.id_usuario = ? ");
			sbSQL.append(" 		  AND su.ativo = 1 ");
			sbSQL.append(" 		  AND sm.ativo = 1 ");
			sbSQL.append(" GROUP BY ");
			sbSQL.append(" 		  su.id_usuario, ");
			sbSQL.append(" 		  su.usuario, ");
			sbSQL.append(" 		  smri.id_infos, ");
			sbSQL.append(" 		  smri.id_menu, ");
			sbSQL.append(" 		  smri.src, ");
			sbSQL.append(" 		  smri.descricao, ");
			sbSQL.append(" 		  smri.href, ");
			sbSQL.append(" 		  smri.descricao_detalhada ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  smri.descricao ");
			
			stmt = conn.prepareStatement(sbSQL.toString());
			
			stmt.setInt(1, idUsuario);
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				Menu menu = new Menu();
				
				menu.setIdMenuInfo(rs.getInt("id_infos"));
				menu.setIdMenu(rs.getInt("id_menu"));
				menu.setSrc(rs.getString("src"));
				menu.setDescricao(rs.getString("descricao"));
				menu.setHref(rs.getString("href"));
				menu.setDescDetalhada(rs.getString("descricao_detalhada"));
				
				menus.add(menu);
				
				logger.info("Menu de relatórios: " + menu.getDescricao() + " Usuário: " + idUsuario);
			}
		} 
		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtenção da lista de menus de relatório do usuario! id:" + idUsuario + " Falha: " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return menus;
	}

	public static List<Menu> ObterListaMenusGraficosAcesso(int idUsuario)
	{
		
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		List<Menu> menus = new ArrayList<Menu>();
		
		try 
		{
			conn = Conexao.getConexao();		
			
			sbSQL.append("");
			sbSQL.append(" SELECT su.id_usuario, ");
			sbSQL.append(" 		  su.usuario, ");
			sbSQL.append(" 		  smri.id_infos, ");
			sbSQL.append(" 		  smri.id_menu, ");
			sbSQL.append(" 		  smri.src, ");
			sbSQL.append(" 		  smri.descricao, ");
			sbSQL.append(" 		  smri.href, ");
			sbSQL.append(" 		  smri.descricao_detalhada ");
			sbSQL.append(" FROM   sis_usuario su ");
			sbSQL.append(" 		  INNER JOIN sis_usuario_grupo sug ");
			sbSQL.append(" 		  	   ON sug.id_usuario = su.id_usuario ");
			sbSQL.append(" 		  INNER JOIN sis_grupo sg ");
			sbSQL.append(" 		  	   ON sg.id_grupo = sug.id_grupo ");
			sbSQL.append(" 		  INNER JOIN sis_menu_direitos smd ");
			sbSQL.append(" 		  	   ON smd.id_grupo = sg.id_grupo ");
			sbSQL.append(" 		  INNER JOIN sis_menu sm ");
			sbSQL.append(" 		  	   ON sm.id_menu = smd.id_menu ");
			sbSQL.append(" 		  INNER JOIN sis_menu_graficos_infos smri ");
			sbSQL.append(" 		  	   ON  smri.id_menu = sm.id_menu ");
			sbSQL.append(" WHERE  su.id_usuario = ? ");
			sbSQL.append(" 		  AND su.ativo = 1 ");
			sbSQL.append(" 		  AND sm.ativo = 1 ");
			sbSQL.append(" GROUP BY ");
			sbSQL.append(" 		  su.id_usuario, ");
			sbSQL.append(" 		  su.usuario, ");
			sbSQL.append(" 		  smri.id_infos, ");
			sbSQL.append(" 		  smri.id_menu, ");
			sbSQL.append(" 		  smri.src, ");
			sbSQL.append(" 		  smri.descricao, ");
			sbSQL.append(" 		  smri.href, ");
			sbSQL.append(" 		  smri.descricao_detalhada ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  smri.descricao ");
			
			stmt = conn.prepareStatement(sbSQL.toString());
			
			stmt.setInt(1, idUsuario);
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				Menu menu = new Menu();
				
				menu.setIdMenuInfo(rs.getInt("id_infos"));
				menu.setIdMenu(rs.getInt("id_menu"));
				menu.setSrc(rs.getString("src"));
				menu.setDescricao(rs.getString("descricao"));
				menu.setHref(rs.getString("href"));
				menu.setDescDetalhada(rs.getString("descricao_detalhada"));
				
				menus.add(menu);
				
				logger.info("Menu de gráfico: " + menu.getDescricao() + " Usuário: " + idUsuario);
			}
		} 
		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtenção da lista de menus de gráficos do usuario! id:" + idUsuario + " Falha: " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return menus;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Menu> ObterMenusAcesso(HttpServletRequest request)
	{
		HttpSession session 	= request.getSession();
		Usuario objUsuario 	= (Usuario) session.getAttribute("objUsuario");	
		
		String token = null;
		String endereco_ip = Utils.getClientIp(request);
		try 
		{
			for(Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
					break;
				}
			}
			
			if (token == null) {
				logger.debug("Nenhum Token encontrado");
			}
			else {
				logger.debug("Token: " + token);
				
				if (objUsuario == null)
					objUsuario = ObterUsuarioToken(token, endereco_ip);
			}
		}
		catch(Exception e) { 
			logger.debug("Falha ao ler cookies");
		}
		
		
		List<Menu> menus = null;
		
		if(objUsuario == null)
			logger.info("Falha obter lista de menus. Usuário não logado");
			
		else
			menus	= (List<Menu>) session.getAttribute("listaMenus");	
			
		if (menus == null && objUsuario != null) {
			logger.debug("Obtendo Lista de Menus novamente - sem sessão - token");
			menus = ObterListaMenusAcesso(objUsuario.getIdUsuario(), 0);
		}
		
		if (menus == null) logger.info("XXX - getMenusAcesso():: menus is Null");
		if (objUsuario == null) logger.info("XXX - getMenusAcesso():: objUsuario is Null");
		
		return menus;		
	}
	
	public static List<Usuario> obterListaUsuariosAtivos(HttpServletRequest request, HttpServletResponse response) 
	{
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		StringBuilder sbSQL = new StringBuilder();
		List<Usuario> usuarios = new ArrayList<Usuario>();
			
		try 
		{
			conn = Conexao.getConexao();		
			
			sbSQL.append(" SELECT su.id_usuario, 	");			
			sbSQL.append(" 		  su.nome,		 	");
			sbSQL.append(" 		  su.email		 	");
			sbSQL.append(" FROM   sis_usuario su	");			
			sbSQL.append(" WHERE  su.ativo = 1		");
			sbSQL.append(" AND su.id_usuario <> 1   ");				
			sbSQL.append(" ORDER BY 				");
			sbSQL.append(" 		  su.nome 			");
			
			stmt = conn.prepareStatement(sbSQL.toString());			
			
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				Usuario usuario = new Usuario();
				
				usuario.setIdUsuario(rs.getInt("id_usuario"));
				usuario.setNome(rs.getString("nome"));
				usuario.setEmail(rs.getString("email"));
				
				
				usuarios.add(usuario);
				
				logger.info("Lista de usuarios, usuario: "+ usuario.getNome());
			}
			// Configura a resposta como XML
	        response.setContentType("text/xml;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        out.println("<?xml version='1.0' encoding='UTF-8'?>");
	        out.println("<usuarios>");

	        for (Usuario u : usuarios) {
	            out.println("<Usuario>");
	            out.println("<id>" + u.getIdUsuario() + "</id>");
	            out.println("<nome>" + u.getNome() + "</nome>");
	            out.println("</Usuario>");
	        }

	        out.println("</usuarios>");
	        out.flush();
		} 
		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtenção da lista de Usuários " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return usuarios;
	}
	
	public static List<Usuario> obterListaUsuariosEmail() 
	{
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		StringBuilder sbSQL = new StringBuilder();
		List<Usuario> usuarios = new ArrayList<Usuario>();
			
		try 
		{
			conn = Conexao.getConexao();		
			
			sbSQL.append(" SELECT su.id_usuario, 	");			
			sbSQL.append(" 		  su.nome,		 	");
			sbSQL.append(" 		  su.email		 	");
			sbSQL.append(" FROM   sis_usuario su	");			
			sbSQL.append(" WHERE  su.ativo = 1		");
			sbSQL.append(" AND su.id_usuario <> 1	");				
			sbSQL.append(" ORDER BY 				");
			sbSQL.append(" 		  su.nome 			");
			
			stmt = conn.prepareStatement(sbSQL.toString());			
			
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				Usuario usuario = new Usuario();
								
				usuario.setEmail(rs.getString("email"));
				
				
				usuarios.add(usuario);
				
				logger.info("Lista de usuarios, usuario: "+ usuario.getNome());
			}	
	        
		} 
		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtenção da lista de Usuários " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return usuarios;
	}

	public static String obterNomeUsuarioLogado(Integer idUsuario) {
	    String nomeUsuario = null;

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement stmt = conn.prepareStatement(
	            " SELECT SU.NOME FROM sis_usuario su WHERE su.ativo = 1 AND su.id_usuario = ? "
	        )
	    ) {
	        stmt.setInt(1, idUsuario);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                nomeUsuario = rs.getString("nome");
	            }
	        }
	    } catch (Exception e) {
	        logger.error("Erro ao obter nome de usuário logado: " + e.getMessage(), e);
	    }

	    return nomeUsuario != null ? nomeUsuario : ""; 
	}

	public static boolean obterStatusUsuario(String nomeUsuario)
	{
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		StringBuilder sbSQL = new StringBuilder();
		boolean result = false;

		try
		{
			conn = Conexao.getConexao();

			sbSQL.append(" SELECT su.ativo 	");
			sbSQL.append(" FROM   sis_usuario su	");
			sbSQL.append(" WHERE UPPER(su.usuario) = UPPER(?) ");

			stmt = conn.prepareStatement(sbSQL.toString());
			stmt.setString(1, nomeUsuario.toUpperCase());

			rs = stmt.executeQuery();

			while(rs.next())
			{
				result = rs.getInt("ativo") == 0 ? false: true;
			}
		}


		catch (Exception e)
		{
			logger.error("Falha na obtenção do usuário " + e.getMessage(), e);
		}

		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}

		return result;
	}

	//Verifica o acesso do usuário.
	//gotoLogin Se True o usuário será direcionado para tela de login se esta não possui acesso.
	public void verificaAcessoUsuario(HttpServletRequest request, HttpServletResponse response, Usuario objUsuario) throws IOException, ConexaoException
	{
		HttpSession session = request.getSession();

		if (objUsuario == null)
		{
			session.invalidate();
			response.sendRedirect("/login/login.jsp");
		}
		else if(!verificaAcesso(request.getRequestURI(), objUsuario))
		{
			session.invalidate();
			response.sendRedirect("/login/login.jsp");
		}
		else if(objUsuario.isAtivo() == false) {
			session.invalidate();
			response.sendRedirect("/login/login.jsp");
		}
	}

	private void acessoViaToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String idUsuarioS = request.getParameter("idUsuario");
	    String dataCriacao = request.getParameter("dataCriacao");
	    String baseUrl     = request.getParameter("baseUrl");
	    String path        = request.getParameter("path");

	    try {
	        Integer idUsuario = Integer.parseInt(idUsuarioS);

	        String redirectUrl = Usuario.acessoViaToken(idUsuario, dataCriacao, baseUrl, path);

	        if (!redirectUrl.isEmpty()) {
	            response.getWriter().write(redirectUrl); // devolve só a string
	        } else {
	            response.getWriter().write("Erro ao gerar token.");
	        }
	    } catch (Exception e) {
	        logger.error("Erro no acessoViaToken da servlet: " + e.getMessage(), e);
	        response.getWriter().write("Erro interno.");
	    }
	}

}
	


	
