/**
 * 
 */
package com.consilux.infra;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.consilux.model.TConfigEquip.TCollection;
import com.consilux.model.TConfigEquip.TCollectionItem;
import com.consilux.model.TConfigEquip.TStringList;

/**
 * @author fos
 * 
 */
public class SerializadorXML {

	final static String ENCODING = "iso-8859-1";

	private Document doc;
	private Element topo;

	public SerializadorXML() throws ParserConfigurationException {
		DocumentBuilder builder;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.newDocument();
		topo = doc.createElement("classes");
		doc.appendChild(topo);
	}

	public void saveObject(Object obj, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException {
		gravaObjeto(obj, topo, sNomeClasse);
	}

	public void readObject(Object obj, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		leObjeto(obj, topo, sNomeClasse);
	}

	public void saveToStringBuffer(StringBuffer buffer) throws IOException {
		StringWriter sw = new StringWriter();

		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			LSOutput output = impl.createLSOutput();
			output.setEncoding(ENCODING);
			output.setCharacterStream(sw);
			writer.write(doc, output);
		} catch (Exception e) {
		}

		buffer.append(sw.toString());
	}

	public void saveToFile(String path) {
		StringBuffer sb1 = new StringBuffer();

		try {
			saveToStringBuffer(sb1);

			FileWriter writer = new FileWriter(new File(path));
			writer.write(sb1.toString());

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromFile(String sArquivo) throws IOException, SAXException,
			ParserConfigurationException {
		FileInputStream fIn = new FileInputStream(sArquivo);

		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(fIn);
		topo = (Element) doc.getFirstChild();
	}

	public void loadFromStringBuffer(StringBuffer buffer) throws IOException,
			SAXException, ParserConfigurationException {
		byte[] byteArray = buffer.toString().getBytes(ENCODING); // choose a
																	// charset
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray);

		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(in);
		topo = (Element) doc.getFirstChild();
	}

	private void gravaObjeto(Object obj, Element el, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = obj.getClass();
		Element elClasse = doc.createElement("class");
		elClasse.setAttribute("classname", classe.getSimpleName());
		elClasse.setAttribute("name", sNomeClasse);
		gravaAtributos(obj, elClasse);
		el.appendChild(elClasse);
	}

	private void leObjeto(Object obj, Element elPai, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Element elClasse = null;
		NodeList lista = elPai.getChildNodes();
		for (int i = 0; i < lista.getLength(); i++) {
			if (lista.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (((Element) lista.item(i)).getAttribute("name")
					.equalsIgnoreCase(sNomeClasse)) {
				elClasse = (Element) lista.item(i);
				break;
			}
		}
		if (elClasse == null)
			throw new IllegalArgumentException("Classe não encontrada: "
					+ sNomeClasse);
		leAtributos(obj, elClasse);
	}

	private void gravaAtributos(Object obj, Element elPai)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = obj.getClass();
		Element elFilho = doc.createElement("properties");
		for (Field campo : classe.getFields()) {
			gravaPropriedade(obj, elFilho, campo);
		}
		elPai.appendChild(elFilho);
	}

	private void leAtributos(Object obj, Element elPai)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		if (obj == null)
			obj = null;

		Class<?> classe = obj.getClass();
		// Element elFilho = (Element)elPai.getFirstChild();
		Element elFilho = null;
		NodeList elList = elPai.getChildNodes();
		for (int t = 0; t < elList.getLength(); t++) {
			if (elList.item(t).getNodeType() == Node.ELEMENT_NODE) {
				elFilho = (Element) elList.item(t);
				break;
			}
		}

		for (Field campo : classe.getFields()) {
			if (!Modifier.isStatic(campo.getModifiers()))
				lePropriedade(obj, elFilho, campo);
		}
	}

	@SuppressWarnings("unchecked")
	private void gravaColecao(Object obj, Element elPai, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = obj.getClass();

		Element elClasse = doc.createElement("class");
		elClasse.setAttribute("classname", classe.getSimpleName());
		elClasse.setAttribute("name", sNomeClasse);

		ArrayList<Object> lista = (ArrayList<Object>) obj;
		Element elFilho = doc.createElement("customdata");
		elFilho.setAttribute("count", String.valueOf(lista.size()));

		int i = 0;
		for (Object objItem : lista) {
			gravaObjeto(objItem, elFilho, String.format("Item_%d", i++));
		}
		elClasse.appendChild(elFilho);
		elPai.appendChild(elClasse);
	}

	private void gravaStringList(Object obj, Element elPai, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> classe = obj.getClass();

		Element elClasse = doc.createElement("class");
		elClasse.setAttribute("classname", classe.getSimpleName());
		elClasse.setAttribute("name", sNomeClasse);

		Element elFilho = doc.createElement("customdata");
		for (Field campo : classe.getFields()) {
			gravaPropriedade(obj, elFilho, campo);
		}

		elClasse.appendChild(elFilho);
		elPai.appendChild(elClasse);
	}

	@SuppressWarnings("unchecked")
	private void leColecao(Object obj, Element elPai, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Element elClasse = null;
		NodeList lista = elPai.getChildNodes();
		for (int i = 0; i < lista.getLength(); i++) {
			if (lista.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (((Element) lista.item(i)).getAttribute("name")
					.equalsIgnoreCase(sNomeClasse)) {
				elClasse = (Element) lista.item(i);
			}
		}
		if (elClasse == null)
			throw new IllegalArgumentException("Classe não encontrada: "
					+ sNomeClasse);

		TCollection<TCollectionItem> listaItems = (TCollection<TCollectionItem>) obj;
		// Element elFilho = (Element)elClasse.getFirstChild();

		Element elFilho = null;
		try {
			elFilho = (Element) elClasse.getFirstChild();
		} catch (Exception e) {
			NodeList elList = elPai.getChildNodes();
			for (int t = 0; t < elList.getLength(); t++) {
				if (elList.item(t).getNodeType() == Node.ELEMENT_NODE) {
					elFilho = (Element) elList.item(t);
					break;
				}
			}
		}

		int conta = 0;

		try {
			conta = Integer.valueOf(elFilho.getAttribute("count"));
		} catch (Exception e) {
			NodeList elList_1 = elFilho.getChildNodes();
			for (int t = 0; t < elList_1.getLength(); t++) {
				if (elList_1.item(t).getNodeType() == Node.ELEMENT_NODE) {
					elFilho = (Element) elList_1.item(t);
					break;
				}
			}

			conta = Integer.valueOf(elFilho.getAttribute("count"));
		}

		for (int i = 0; i < conta; i++) {
			TCollectionItem objItem = listaItems.add();
			leObjeto(objItem, elFilho, String.format("Item_%d", i));
		}
	}

	private void leStringList(Object obj, Element elPai, String sNomeClasse)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Element elClasse = null;
		NodeList lista = elPai.getChildNodes();
		for (int i = 0; i < lista.getLength(); i++) {
			if (lista.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (((Element) lista.item(i)).getAttribute("name")
					.equalsIgnoreCase(sNomeClasse)) {
				elClasse = (Element) lista.item(i);
				break;
			}
		}
		if (elClasse == null)
			throw new IllegalArgumentException("Classe não encontrada: "
					+ sNomeClasse);

		leAtributos(obj, elClasse);
	}

	private void gravaPropriedade(Object obj, Element elPai, Field campo)
			throws IllegalArgumentException, IllegalAccessException {
		String sTipo = getTipoCampoStr(campo);
		Element elPropriedade = doc.createElement("property");
		elPropriedade.setAttribute("name", campo.getName());
		elPropriedade.setAttribute("type", sTipo);

		if (sTipo.equals("Object")) {
			if (campo.getType() == TCollection.class) {
				elPropriedade.setAttribute("class", campo.getType()
						.getSimpleName());
				gravaColecao(campo.get(obj), elPropriedade, campo.getName());
			} else if (campo.getType() == TStringList.class) {
				elPropriedade.setAttribute("class", campo.getType()
						.getSimpleName());
				gravaStringList(campo.get(obj), elPropriedade, campo.getName());
			} else {
				elPropriedade.setAttribute("class", campo.getType()
						.getSimpleName());
				gravaObjeto(campo.get(obj), elPropriedade, campo.getName());
			}
		} else {
			if (campo.getType() == Character.class)
				elPropriedade.setTextContent(Integer.valueOf(
						((int) ((Character) campo.get(obj)).charValue()))
						.toString());
			else
				elPropriedade.setTextContent(campo.get(obj).toString());
		}

		elPai.appendChild(elPropriedade);
	}

	@SuppressWarnings("unchecked")
	private void lePropriedade(Object obj, Element elPai, Field campo)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Element elPropriedade = null;
		NodeList lista = elPai.getChildNodes();

		for (int i = 0; i < lista.getLength(); i++) {
			if (lista.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (((Element) lista.item(i)).getAttribute("name")
					.equalsIgnoreCase(campo.getName())) {
				elPropriedade = (Element) lista.item(i);
				break;
			}
		}
		if (elPropriedade == null)
			return;
		// throw new
		// IllegalArgumentException(String.format("Propriedade não encontrada: %s.%s",
		// obj.getClass().getSimpleName(), campo.getName()));

		String sTipo = elPropriedade.getAttribute("type");

		if (sTipo.equals("Object")) {
			if (campo.getType() == TCollection.class)
				leColecao(campo.get(obj), elPropriedade, campo.getName());
			else if (campo.getType() == TStringList.class)
				leStringList(campo.get(obj), elPropriedade, campo.getName());
			else
				leObjeto(campo.get(obj), elPropriedade, campo.getName());

		} else if (sTipo.equals("String")) {
			campo.set(obj, elPropriedade.getTextContent());
		} else if (sTipo.equals("Integer")) {
			if (campo.getType() == Character.class)
				campo.set(
						obj,
						Character.valueOf((char) Integer.valueOf(
								elPropriedade.getTextContent()).intValue()));
			else
				campo.set(obj, Integer.valueOf(elPropriedade.getTextContent()));
		} else if (sTipo.equals("Double")) {
			campo.set(obj, Double.valueOf(elPropriedade.getTextContent()));
		} else if (sTipo.equals("Enumeration")) {
			if (campo.getType() == Boolean.class)
				campo.set(obj, Boolean.valueOf(elPropriedade.getTextContent()));
			else {
				Class<?> tipo = campo.getType();
				campo.set(
						obj,
						Enum.valueOf((Class<Enum>) tipo,
								elPropriedade.getTextContent()));
			}
		}
	}

	private String getTipoCampoStr(Field campo) {
		String sRet = "Object";
		if (campo.getType() == String.class)
			sRet = "String";
		else if (campo.getType() == Integer.class)
			sRet = "Integer";
		else if (campo.getType() == Character.class)
			sRet = "Integer";
		else if (campo.getType() == Double.class)
			sRet = "Double";
		else if (campo.getType() == Boolean.class)
			sRet = "Enumeration";
		else if (campo.getType().getSuperclass() == Enum.class)
			sRet = "Enumeration";

		return sRet;
	}
}
