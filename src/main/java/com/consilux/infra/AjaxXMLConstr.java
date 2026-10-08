/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/12/2006

  Descricao: Classe de suporte para construção de um XML a ser enviado via AJAX.

  Historico:

    $Log: AjaxXMLConstr.java,v $
    Revision 1.4  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Classe de suporte para construção de um XML a ser enviado via AJAX.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/01/12 12:49:50 $ $Author: fos $
 */
public class AjaxXMLConstr {
	private Document doc;
	private Element topo;
	/**
	 * Incializa objetos para construção do XML.
	 * @param sTopo Nome o NÓ mestre no XML
	 * @throws ParserConfigurationException
	 */
	public AjaxXMLConstr(String sTopo) throws ParserConfigurationException {
		DocumentBuilder builder;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.newDocument();
		
		topo = doc.createElement(sTopo);
		doc.appendChild(topo);
	}
	/**
	 * Adiciona um NÓ no XML e ajusta o conteúdo da TAG.
	 * @param sNome Nome da TAG 
	 * @param sValor Conteúdo da TAG
	 */
	public void adicCampo(String sNome, String sValor) {
		Element e = doc.createElement(sNome);
		e.setTextContent(sValor);
		topo.appendChild(e);
	}
	/**
	 * Transforma o objeto XML em texto e envia ao browser.
	 * @param response Referência para saída dos dados 
	 * @throws IOException 
	 */
	public void dump(HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		String resposta = "";
		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			resposta = writer.writeToString(doc);
		} catch(Exception e) { }
//		XMLSerializer serializer = new XMLSerializer(
//											response.getOutputStream(),
//											new OutputFormat(doc, "UTF-8", true));
//		serializer.serialize(doc);
		response.getWriter().write(resposta);
		response.flushBuffer();
	}
	

}
