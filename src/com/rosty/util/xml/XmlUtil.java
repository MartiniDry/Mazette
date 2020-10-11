package com.rosty.util.xml;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Classe utilitaire pour la manipulation et la récupération de données depuis
 * un document XML.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class XmlUtil {
	/**
	 * Charge un fichier XML en mémoire et effectue une validation XSD. Le contenu
	 * est chargé sous forme d'une instance {@link Document}.
	 * 
	 * @param xmlFile Fichier XML.
	 * @param xsdFile Schéma XSD de validation.
	 * @return Instance {@link Document} comprenant l'ensemble des informations du
	 *         fichier XML.
	 * @throws ParserConfigurationException En cas d'erreur lors de la génération du
	 *                                      document XML avec la configuration du
	 *                                      fichier.
	 * @throws SAXException                 En cas d'erreur SAX durant la création
	 *                                      du document XML, celle de la source XSD
	 *                                      ou lors de la validation XSD.
	 * @throws IOException                  En cas d'erreur pendant le traitement
	 *                                      des données du fichier XML.
	 */
	public static Document load(File xmlFile, File xsdFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(xmlFile);

		if (xsdFile != null && xsdFile.exists()) {
			SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			xsdFactory.setErrorHandler(new SAXErrorHandler());

			InputSource input = new InputSource(xsdFile.getAbsolutePath());
			SAXSource xsdSource = new SAXSource(input);

			Schema schema = xsdFactory.newSchema(xsdSource);
			Validator xsdValidator = schema.newValidator();
			xsdValidator.validate(new DOMSource(document));
		}

		return document;
	}

	/**
	 * Charge un fichier XML et enregistre les données sous forme d'une instance
	 * {@link Document}.
	 * 
	 * @param xmlFile Fichier XML.
	 * @return Instance {@link Document} comprenant l'ensemble des informations du
	 *         fichier XML.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document load(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
		return load(xmlFile, null);
	}

	/**
	 * Fournit n'importe quelle donnée contenue dans un noeud du fichier XML ;
	 * l'utilisateur fournit un chemin permettant de naviguer dans l'arborescence
	 * des balises XML. La syntaxe est la suivante :
	 * 
	 * <ul>
	 * <li>Un noeud XML est identifié par le nom de sa balise :
	 * <code>Node</code></li>
	 * <li>Lorsque plusieurs noeuds du même nom sont présents, un indice entre
	 * crochets permet de les identifier :
	 * <ul>
	 * <li>Soit par son index dans la liste : <code>Node[1]</code></li>
	 * <li>Soit par la valeur de l'attribut "id", s'il existe :
	 * <code>Node[ELEMENT_1]</code></li>
	 * </ul>
	 * </li>
	 * <li>L'attribut d'un noeud est indiqué par le nom du noeud suivi du nom de
	 * l'attribut entre parenthèses : <code>Node(attribute)</code></li>
	 * <li>Il est possible de mixer ces règles :
	 * <code>Node[1](attribute)</code></li>
	 * <li>Un noeud B contenu dans un noeud A est accesible via le chemin
	 * <code>A.B</code> ; par récurrence, le chemin <code>A.B.C.D</code>... permet
	 * d'ouvrir une série de noeuds emboîtés.</li>
	 * </ul>
	 * <p>
	 * Le noeud parent (à partir duquel le chemin doit partir) est le noeud
	 * {@code <Element>} du fichier de configuration. Si le chemin n'est pas
	 * spécifié i.e. si <code style="font-size: 12">path</code> est vide ou si
	 * <code style="font-size: 12">path=null</code>, alors la méthode retourne le
	 * noeud de départ.
	 * </p>
	 * 
	 * @param startNode Noeud de départ dans le fichier XML.
	 * @param path      Chemin vers l'élément XML, relatif au noeud de départ.
	 * @return L'instance {@link Node} associée à l'élément XML
	 */
	public static Node getElement(Node startNode, String path) {
		Node stepNode = startNode;
		if (path == null || path.isEmpty())
			return startNode;

		String[] tags;
		if (path.contains("."))
			tags = path.split("\\.");
		else
			tags = new String[] { path };

		Pattern wordPattern = Pattern.compile("\\w+");

		for (String tag : tags) {
			if (stepNode != null)
				if (Pattern.matches("^\\w+$", tag)) {
					stepNode = getNode(stepNode.getChildNodes(), tag);
				} else {
					Matcher wordMatch = wordPattern.matcher(tag);
					if (Pattern.matches("^\\w+[\\[]\\w+[\\]]$", tag)) {
						wordMatch.find();
						String nodeName = wordMatch.group();
						wordMatch.find();
						String index = wordMatch.group();

						try {
							stepNode = getNodeByIndex(stepNode.getChildNodes(), nodeName, Integer.parseInt(index));
						} catch (NumberFormatException e) {
							stepNode = getNodeById(stepNode.getChildNodes(), nodeName, index);
						}
					} else if (Pattern.matches("^\\w+[(]\\w+[)]$", tag)) {
						wordMatch.find();
						String nodeName = wordMatch.group();
						wordMatch.find();
						String attributeName = wordMatch.group();

						Node lastNode = getNode(stepNode.getChildNodes(), nodeName);
						stepNode = lastNode.getAttributes().getNamedItem(attributeName);
						if (stepNode == null)
							throw new RuntimeException(
									"L'élément " + nodeName + " ne possède pas d'attribut " + attributeName + ".");
					} else if (Pattern.matches("^\\w+\\[\\w+\\][(]\\w+[)]$", tag)) {
						wordMatch.find();
						String nodeName = wordMatch.group();
						wordMatch.find();
						String index = wordMatch.group();
						wordMatch.find();
						String attributeName = wordMatch.group();

						Node lastNode;
						try {
							lastNode = getNodeByIndex(stepNode.getChildNodes(), nodeName, Integer.parseInt(index));
						} catch (NumberFormatException e) {
							lastNode = getNodeById(stepNode.getChildNodes(), nodeName, index);
						}

						stepNode = lastNode.getAttributes().getNamedItem(attributeName);
						if (stepNode == null)
							throw new RuntimeException(
									"L'élément " + nodeName + " ne possède pas d'attribut " + attributeName + ".");
					} else {
						stepNode = null;
						throw new RuntimeException("Chemin \"" + path + "\" : la syntaxe de l'élément \"" + tag
								+ "\" n'est pas correcte.");
					}
				}
		}

		return stepNode;
	}

	/**
	 * Renvoit le premier noeud possédant le <i>tag</i> correspondant dans la liste
	 * donnée.
	 * 
	 * @param list Instance de {@link NodeList}.
	 * @param tag  Nom de la balise XML.
	 * @return Le noeud XML correspondant, ou <code>null</code> si cet élément
	 *         n'existe pas.
	 */
	private static Node getNode(NodeList list, String tag) {
		Node node = null;

		boolean found = false;
		int j = 0;

		while (j < list.getLength() && !found) {
			if (list.item(j).getNodeName().equals(tag)) {
				node = list.item(j);
				found = true;
			}

			j++;
		}

		if (j >= list.getLength())
			throw new RuntimeException("L'élément " + tag + " n'existe pas.");

		return node;
	}

	/**
	 * Repère un noeud dans une liste donnée. L'indice précise, dans le cas où
	 * plusieurs noeuds de même nom sont présents, celui à sélectionner.
	 * 
	 * @param list  L'instance de {@link NodeList}
	 * @param tag   Le nom de la balise XML
	 * @param index La position dans la liste des balises de nom <b>tag</b>
	 * @return Le noeud XML correspondant, ou <code>null</code> si cet élément
	 *         n'existe pas.
	 */
	private static Node getNodeByIndex(NodeList list, String tag, int index) {
		Node node = null;

		boolean found = false;
		int j = 0, id = 0;

		while (j < list.getLength() && !found) {
			if (list.item(j).getNodeName().equals(tag))
				if (id == index) {
					node = list.item(j);
					found = true;
				} else
					id++;

			j++;
		}

		if (j >= list.getLength())
			throw new RuntimeException("L'élément " + tag + " n'existe pas à l'index " + index + ".");

		return node;
	}

	/**
	 * Repère un noeud dans une liste donnée. La méthode ne travaille que sur les
	 * noeuds possédant un identifiant i.e. un attribut "id".
	 * 
	 * @param list Instance de {@link NodeList}.
	 * @param tag  Nom de la balise XML.
	 * @param id   Attribut d'identification du <b>tag</b>.
	 * @return Le noeud XML correspondant, ou <code>null</code> si cet élément
	 *         n'existe pas.
	 */
	private static Node getNodeById(NodeList list, String tag, String id) {
		Node node = null;

		boolean found = false;
		int j = 0;

		while (j < list.getLength() && !found) {
			Node item = list.item(j);
			if (item.getNodeName().equals(tag))
				if (item.hasAttributes())
					if (item.getAttributes().getNamedItem("id").getNodeValue().equals(id)) {
						node = item;
						found = true;
					}

			j++;
		}

		if (j >= list.getLength())
			throw new RuntimeException("Il n'existe pas d'élément " + tag + " possédant l'attribut \"id=" + id + "\".");

		return node;
	}

	private static class SAXErrorHandler implements ErrorHandler {
		@Override
		public void error(SAXParseException arg0) throws SAXException {
			System.out.println("Erreur");
		}

		@Override
		public void fatalError(SAXParseException arg0) throws SAXException {
			System.out.println("Erreur fatale");
		}

		@Override
		public void warning(SAXParseException arg0) throws SAXException {
			System.out.println("Avertissement");
		}
	}
}