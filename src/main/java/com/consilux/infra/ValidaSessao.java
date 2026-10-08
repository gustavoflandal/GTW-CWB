  package com.consilux.infra;
  
  import java.io.IOException;
  import javax.servlet.Filter;
  import javax.servlet.FilterChain; 
  import javax.servlet.FilterConfig; 
  import javax.servlet.ServletException; 
  import javax.servlet.ServletRequest; 
  import javax.servlet.ServletResponse;  
  import javax.servlet.annotation.WebFilter; 
  import javax.servlet.http.HttpServletRequest; 
  import javax.servlet.http.HttpServletResponse;
  import javax.servlet.http.HttpSession;
  
  import com.consilux.model.Usuario;
  import muralha.digital.acessos.UsuarioServlet;
  
  @WebFilter("/*") public class ValidaSessao implements Filter {
  
  @Override public void init(FilterConfig filterConfig) throws ServletException
  { 
	  // Utilizado para carregar algo na inicialização. Nesse caso há nada. 	  
  }
  
  @Override public void doFilter(ServletRequest req, ServletResponse res,
  FilterChain chain) throws IOException, ServletException {
  
  HttpServletRequest request = (HttpServletRequest) req; HttpServletResponse
  response = (HttpServletResponse) res;
  
  String path = request.getRequestURI();
  
  boolean paginaPublica =
  path.startsWith(request.getContextPath() + "/login/") ||
  path.startsWith(request.getContextPath() + "/Abertura/") ||
  path.contains("/login/") || 
  path.contains("/ClientesWebSocket") ||
  path.contains("/includes/erro.jsp") ||
  path.contains("/includes/erro_muralha.jsp") ||
  path.contains("resources/") || path.contains("assets/") ||
  path.contains("favicon");
  
  if (paginaPublica) { chain.doFilter(request, response); return; }
  
  HttpSession session = request.getSession(false);
  
  if (session != null) {
  
	  Long ultimaVerificacao = (Long) session.getAttribute("ultimaVerificacaoAcesso"); 
	  long agora = System.currentTimeMillis(); 
	  long cincoMinutos = 5 * 60 * 1000;
  
	  if (ultimaVerificacao == null || (agora - ultimaVerificacao) > cincoMinutos)
	  {
	  
	  Usuario objUsuario = (Usuario) session.getAttribute(SessaoConstantes.SESSAO_USUARIO);
	  
		  if(objUsuario != null) {  
			  try {
				  	  
				  String usuario = objUsuario.getUsuario();

				  boolean ativo = UsuarioServlet.obterStatusUsuario(usuario);
				  
				  if(ativo == false) {
					  session.invalidate();
					  response.sendRedirect("/login/login.jsp");
					  return;
				  }				  
			  } catch (IOException e) {			  
				  e.printStackTrace(); session.invalidate();
				  response.sendRedirect("/login/login.jsp"); 
				  return;
			  	}
			  }
		  else{
		  	session.invalidate();
			response.sendRedirect("/login/login.jsp");
			return;
		  }
	  } 
	  
	  session.setAttribute("ultimaVerificacaoAcesso", agora); 
  }
  
  chain.doFilter(request, response); 
  
  }
  
  @Override public void destroy() { 
	  // Utilizado para destruir recursos que não estão mais sendo utilizados. Nesse caso há nada 
	  } 
  }
 