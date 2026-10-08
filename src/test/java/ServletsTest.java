/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.lib.test

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 19/11/2009

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Usuario;

/**
 * Classe de testes para Servlets.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public abstract class ServletsTest {
	
	private HttpServletRequest request;
	private HttpServletResponse  response;
	private ServletConfig servletConfig;  
	private HttpSession session = mock(HttpSession.class);
	
	
	@BeforeTest
	public abstract void iniciaServletContext() throws ConexaoException, SQLException, ServletException, IOException, ConfiguracaoException;

	@BeforeMethod
	public abstract void iniciaRequisicaoHTTP() throws ConexaoException, SQLException, ServletException, IOException;
	
	/**
	 * Retorna o valor do campo 'request' atual.
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		
		return this.request;
	}

	/**
	 * Retorna o valor do campo 'response' atual.
	 * @return the response
	 */
	public HttpServletResponse getResponse() {
		return this.response;
	}

/**
	 * @return the servletConfig
	 */
	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	//    @SuppressWarnings({"unchecked","rawtypes"})
	protected final void doSetEnv() {
		System.setProperty("catalina.base","/tmp");
/*
		String[][] envVars = {{"CATALINA_BASE","ZZZ"}};
		Class[] classes = Collections.class.getDeclaredClasses();
	    Map<String, String> env = System.getenv();
	    try {
		    for(Class cl : classes) {
		        if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
		            Field field = cl.getDeclaredField("m");
		            field.setAccessible(true);
		            Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
		            map.clear();
		            map.putAll(ArrayUtils.toMap(envVars));
		        }
		    }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
		}
*/
	}

	protected final void doIniciaServletContext() throws ConexaoException, SQLException, ServletException, IOException, ConfiguracaoException {
		final ServletContext servletContext = mock(ServletContext.class);  
		final HashMap<String, Object> attributes = new HashMap<String, Object>();
		session = mock(HttpSession.class);
		servletConfig = mock(ServletConfig.class);
		
		doSetEnv();

		when(servletConfig.getServletContext()).thenReturn(servletContext);  
		when(servletContext.getResource(anyString())).thenAnswer(new Answer<URL>() {
				
				/**
				* @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
				*/
				@Override
				public URL answer(InvocationOnMock aInvocation) throws Throwable {
					
					String ret = (String) aInvocation.getArguments()[0];
				
					return new URL(getClass().getResource("/")+"../../src/main/webapp"+ret);
				}
			}
		);
		
		when(servletContext.getRealPath(anyString())).thenAnswer(new Answer<String>() {
				/**
				* @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
				*/
				@Override
				public String answer(InvocationOnMock aInvocation) throws Throwable {
					
					String ret = (String) aInvocation.getArguments()[0];
				
					return getClass().getResource("/")+"../../src/main/webapp"+ret;
				}
			}
		);
		
		when(session.getAttribute(anyString())).thenAnswer(new Answer<Object>() {
			
			/**
			* @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
			*/
			@Override
				public Object answer(InvocationOnMock aInvocation) throws Throwable {
				
					String key = (String) aInvocation.getArguments()[0];
					
					return attributes.get(key);
				}
			});

		Mockito.doAnswer(new Answer<Object>() {
			/**
			* @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
			*/
			@Override
			public Object answer(InvocationOnMock aInvocation) throws Throwable {
				String key = (String) aInvocation.getArguments()[0];
				Object value = aInvocation.getArguments()[1];
				attributes.put(key, value);
				
				return null;
			}

		}).when(session).setAttribute(anyString(), anyObject());	

		
		// Força o carregamento das configurações através de outra classe, a
		// "ConfiguracaoTestes"
		ConfiguracaoTestes.ajustaConfiguracaoTestes();
		
		Conexao.initConexao();
		Map<String,Object> mFiltro = new HashMap<String,Object>();
		mFiltro.put("usuario", "administrador" );
		mFiltro.put("ativo", 1 );
		List<Usuario> usus;

		usus = Usuario.buscaUsuarioPor(mFiltro);
		Usuario usuario = usus.get(0);

		session.setAttribute("[usuario]",usuario);
	}
	

	protected final void doIniciaRequisicaoHTTP(OutputStream out) throws ConexaoException, SQLException, ServletException, IOException {
		final HashMap<String, Object> parameters = new HashMap<String, Object>();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		when(request.getServletPath()).thenReturn("<ServletsTest>");
		when(request.getSession()).thenReturn(session);
		when(request.getParameterMap()).thenReturn(parameters);
		when(request.getMethod()).thenReturn("GET");  
		when(request.getParameter(anyString())).thenAnswer(new Answer<Object>() {
	
			/**
			* @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
			*/
			@Override
			public Object answer(InvocationOnMock aInvocation) throws Throwable {
				
				String key = (String) aInvocation.getArguments()[0];
				
				return parameters.get(key);
			}
		});
		
		when(response.getOutputStream()).thenReturn(new ServletOutputStreamAdapter(out));
	}
	
	
	protected final void doService(GenericServlet servlet) throws ServletException, IOException {
		servlet.init(getServletConfig());
		servlet.service(getRequest(), getResponse());
	}

	
	class ServletOutputStreamAdapter extends ServletOutputStream {
		
		private OutputStream out;
		
		
		private ServletOutputStreamAdapter(OutputStream out) {
			super();
			this.out = out;
		}


		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}


		/**
		 * Ajusta o valor do campo 'out' no objeto.
		 * @param out the out to set
		 */
		public void setOut(OutputStream out) {
			this.out = out;
		}
	}
}
