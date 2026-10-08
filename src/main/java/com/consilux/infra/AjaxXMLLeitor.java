/**********************************************************************************



  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 30/05/2011

  Descricao: Classe de suporte para leitura de um XML.

  Historico:

    $Log$


*********************************************************************************/
package com.consilux.infra;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.consilux.model.exception.ModelException;

/**
 * Classe de suporte para leitura de um XML.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/01/12 12:49:50 $ $Author: fos $
 */
public class AjaxXMLLeitor {
	private Document doc;
//	private Node topo;
	
	/**
	 * Incializa objetos para construção do XML.
	 * @param sTopo Nome o NÓ mestre no XML
	 * @throws ParserConfigurationException
	 */
	public AjaxXMLLeitor(InputStream conteudoArquivo, String sTopo) throws ParserConfigurationException, SAXException, IOException, ModelException {
		DocumentBuilder builder;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		doc = builder.parse(conteudoArquivo);
		
		NodeList nl = doc.getElementsByTagName(sTopo);
		
		if (nl.getLength() != 1)
			throw new ModelException("Elemento base inexistente: "+sTopo);
		
//		topo = nl.item(0);
	}
	
	
	public String lerCampo(String sNome) throws ModelException {
		NodeList nl = doc.getElementsByTagName(sNome);
		
		if (nl.getLength() != 1)
			throw new ModelException("Elemento inexistente: "+sNome);
			
		return nl.item(0).getTextContent();
	}
}
