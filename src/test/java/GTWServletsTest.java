/**********************************************************************************

  Projeto: GTW

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 19/11/2009

  Descricao: Classe de testes para Servlets.

  Historico:

    $Log$

*********************************************************************************/
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.servlet.ajax.InfoVeiculo;

/**
 * Classe de testes para Servlets.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

@Test(groups = { "completo" })
public class GTWServletsTest extends ServletsTest {
	private ByteArrayOutputStream out = new ByteArrayOutputStream();//new ByteArrayOutputStream();
	
	@BeforeTest
	public void iniciaServletContext() throws ConexaoException, SQLException, ServletException, IOException, ConfiguracaoException {
		super.doIniciaServletContext();
	}
	
	@BeforeMethod
	public void iniciaRequisicaoHTTP() throws ConexaoException, SQLException, ServletException, IOException {
		out.reset();
		super.doIniciaRequisicaoHTTP(out);
	}
	
	public void InfoInfracaoTest() throws ServletException, IOException, ParserConfigurationException, SAXException {

		
		// RMG - Comentado, pois esta infração não necessáriamente existe.
		// Necessita de banco pré-condicionado.
//		Map<String, Object> parameters = getRequest().getParameterMap(); 
//		parameters.put("id_infracao", "2977");
//		new InfoInfracao().service(getRequest(), getResponse());
//		
//		Assert.assertTrue("Retorno vazio!",out.size() > 0);
//
//		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		Document doc = builder.parse(new ByteArrayInputStream(out.toByteArray()));
//		
//		Assert.assertEquals("Não retornou a mesma infração!","2977",doc.getElementsByTagName("ID_INFRACAO").item(0).getTextContent());
		
	}

	@SuppressWarnings("unchecked")
	public void InfoVeiculoTest() throws ServletException, IOException {
		Map<String, Object> parameters = getRequest().getParameterMap(); 
		parameters.clear();
		parameters.put("id_veiculo", "759");
		doService(new InfoVeiculo());
	}
	
	@SuppressWarnings("unchecked")
	public void PREMTest() throws ServletException, IOException {
		Map<String, Object> parameters = getRequest().getParameterMap(); 
		parameters.clear();
		parameters.put("id_remessa", "68");
		//doService(new PREM());
	}

}
