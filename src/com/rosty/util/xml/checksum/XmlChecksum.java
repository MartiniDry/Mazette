package com.rosty.util.xml.checksum;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Encapsulation des fonctionnalités de calcul de la somme de contrôle
 * (<i>checksum</i>).
 * <p>
 * <u>Exemple :</u> <code><pre>
 * XmlChecksum checker = new XmlChecksum(document);
 * if (checker.calculatedCheckcum() == checker.savedChecksum()) {
 *     // Action lorsque la somme de contrôle est valide
 * }
 * </pre></code>
 * </p>
 */
public final class XmlChecksum {
	/** La constante HEXADECIMAL */
	private static final int HEXADECIMAL = 16;

	/** La constante MAX_UNSIGNED_INT */
	private static final int MAX_UNSIGNED_INT = 0xFFFFFFFF;

	/** Document XML à analyser */
	private Document mdomDocument;

	/** Somme de contrôle calculée dans le document */
	private long savedChecksum = -1;

	/**
	 * Constructeur de la classe {@link XmlChecksum}.
	 *
	 * @param document Document XML.
	 * @throws ParserConfigurationException En cas d'erreur lors de la construction
	 *                                      du document XML utilisé pour le calcul
	 *                                      du <i>checksum</i>.
	 */
	public XmlChecksum(Document document) throws ParserConfigurationException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		mdomDocument = db.newDocument();
		Node copiedRoot = mdomDocument.importNode(document.getDocumentElement(), true);
		mdomDocument.appendChild(copiedRoot);
	}

	/** Fournit le <i>checksum</i> enregistré dans le document XML. */
	public long getSavedChecksum() {
		return savedChecksum;
	}

	/** Définit le <i>checksum</i> enregistré dans le document XML. */
	public void setSavedChecksum(long lSavedChecksum) {
		this.savedChecksum = lSavedChecksum;
	}

	/**
	 * Calcule le <i>checksum</i> du fichier XML chargé dans l'instance
	 * {@link XmlChecksum}.
	 * 
	 * @return Somme de contrôle du document XML.
	 */
	public long calculateChecksum() {
		formatXml();
		ChecksumCallBack callback = cs -> setSavedChecksum(Long.parseLong(cs, HEXADECIMAL) & MAX_UNSIGNED_INT);

		return calculateChecksum(mdomDocument.getDocumentElement(), 0, callback);
	}

	/**
	 * Calcule le <i>checksum</i> d'un noeud XML et le cumule avec le
	 * <i>checksum</i> fourni.
	 * 
	 * @param node        Noeud dont on veut calculer la somme de contrôle.
	 * @param newChecksum Checksum de départ.
	 * @param callback    Interface de signalement de la somme de contrôle.
	 * @return Nouvelle somme de contrôle.
	 */
	private long calculateChecksum(Node node, long checksum, ChecksumCallBack callback) {
		long newChecksum = checksum;
		if (!node.getNodeName().equalsIgnoreCase("checksum")) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.ELEMENT_NODE)
				newChecksum = calculateChecksum(node.getNodeName(), newChecksum);

			// Checksum sur le texte ou sur les commentaires
			if (nodeType == Node.TEXT_NODE || nodeType == Node.COMMENT_NODE)
				newChecksum = calculateChecksum(node.getTextContent(), newChecksum);

			// Checksum sur les attributs
			NamedNodeMap attributes = node.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Attr attribut = (Attr) attributes.item(i);
					newChecksum = calculateChecksum(attribut.getNodeName(), newChecksum);
					newChecksum = calculateChecksum(attribut.getValue(), newChecksum);
				}
			}

			// Ajouter le checksum des enfants
			Node childNode = node.getFirstChild();
			while (childNode != null) {
				newChecksum = this.calculateChecksum(childNode, newChecksum, callback);
				childNode = childNode.getNextSibling();
			}
		} else {
			String fileChecksum = node.getTextContent();
			if (callback != null)
				callback.checksumFound(fileChecksum);
		}

		return newChecksum;
	}

	/**
	 * Calcule le <i>checksum</i> d'une chaîne de caractères et le cumule avec le
	 * <i>checksum</i> fourni.
	 * 
	 * @param str      : Chaîne dont on calcule le <i>checksum</i>.
	 * @param checksum : Somme de contrôle déjà calculée.
	 * @return Nouvelle somme de contrôle.
	 */
	private long calculateChecksum(String str, long checksum) {
		long newChecksum = checksum;
		for (char c : str.toCharArray())
			newChecksum += c & MAX_UNSIGNED_INT;

		return newChecksum;
	}

	/**
	 * Formate la totalité du document XML en simplifiant :
	 * <ul>
	 * <li>Le nom des noeuds</li>
	 * <li>Le nom des attributs</li>
	 * <li>La valeur des attributs</li>
	 * <li>Le texte des noeuds.</li>
	 * </ul>
	 */
	private void formatXml() {
		formatXml(mdomDocument.getDocumentElement());
	}

	/**
	 * Formate un noeud XML :
	 * <ul>
	 * <li>En retirant les espaces de début et de fin</li>
	 * <li>En réduisant les espaces intermédiaires à un seul caractère d'espacement.
	 * Les blancs sont représentés par les caractères <b>\t</b>, <b>\n</b>,
	 * <b>\v</b>, <b>\f</b>, <b>\r</b> et <b>" "</b>.</li>
	 * </ul>
	 * 
	 * @param node : Noeud XML à formatter.
	 */
	private void formatXml(Node node) {
		short nodeType = node.getNodeType();

		if (nodeType == Node.TEXT_NODE)
			node.setTextContent(format(node.getTextContent()));

		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attribut = (Attr) attributes.item(i);
				attribut.setTextContent(format(attribut.getTextContent()));
			}
		}

		Node childNode = node.getFirstChild();
		while (childNode != null) {
			if (nodeType == Node.TEXT_NODE || nodeType == Node.COMMENT_NODE || nodeType == Node.ATTRIBUTE_NODE
					|| nodeType == Node.ELEMENT_NODE)
				this.formatXml(childNode);

			childNode = childNode.getNextSibling();
		}
	}

	/**
	 * Formate un noeud XML présenté sous forme d'une chaîne de caractères. La
	 * méthode :
	 * <ul>
	 * <li>Retire les espaces de début et de fin</li>
	 * <li>Réduit les espaces intermédiaires à un seul caractère d'espacement. Les
	 * blancs sont représentés par les caractères <b>\t</b>, <b>\n</b>, <b>\v</b>,
	 * <b>\f</b>, <b>\r</b> et <b>" "</b>.</li>
	 * </ul>
	 * 
	 * @param str : Chaîne de caractères représentant le noeud XML.
	 * @return La chaîne de caractères formatée.
	 */
	private String format(String str) {
		if (str != null) {
			StringBuilder formattedStr = new StringBuilder();
			boolean insideSpace = false; // Indique si l'on se trouve au milieu d'un espacement, et s'il est donc
											// nécessaire de sauvegarder le caractère.
			for (char c : str.toCharArray())
				switch (c) { // Si le caractère est celui d'un espacement, alors formatter.
				case '\t':
				case '\n':
				case '\u000b': // Correspond au caractère de tabulation verticale "\v".
								// Ce caractère n'est pas connu en Java.
				case '\f':
				case '\r':
				case ' ':
					if (!insideSpace) {
						insideSpace = true;
						formattedStr.append(' ');
					}

					break;
				default: // Dans le cas contraire, laisser le caractère tel qu'il est.
					insideSpace = false;
					formattedStr.append(c);

					break;
				}

			return formattedStr.toString();
		} else
			return null;
	}

	/**
	 * Interface fonctionnelle informant que le <i>checksum</i> a été trouvé dans le
	 * document XML.
	 */
	@FunctionalInterface
	private interface ChecksumCallBack {
		/**
		 * Cette fonction est appelée lorsque le <i>checksum</i> a été trouvé dans le
		 * document XML.
		 *
		 * @param savedChecksum <i>Checksum</i> trouvé dans le document XML
		 */
		void checksumFound(String savedChecksum);
	}
}