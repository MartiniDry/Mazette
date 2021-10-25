package com.rosty.maze.application.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.rosty.maze.Mazette;
import com.rosty.util.xml.XmlUtil;
import com.rosty.util.xml.checksum.ChecksumException;
import com.rosty.util.xml.checksum.XmlChecksum;

/**
 * Gestionnaire de configuration du logiciel. La classe combine les données de
 * configuration et les préférences utilisateur pour fournir les informations au
 * reste du logiciel. Lorsque le singleton est instancié, le fichier XML de
 * configuration est chargé en mémoire pour prévenir de toute corruption de
 * fichier lors du fonctionnement. Les préférences utilisateur peuvent en
 * revanche être modifiées et avoir un impact en temps réel sur le logiciel.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ConfigManager {
	/* CONSTANTES */
	private static final String XML_FILENAME = "config.xml"; // Nom du fichier de configuration
	private static final String XSD_FILENAME = "config.xsd"; // Nom du fichier de validation

	/** Fichier de configuration XML. */
	private static File xmlFile = new File("res/install", XML_FILENAME);
	private static File xsdFile = new File("com/ratp/oam/model/config", XSD_FILENAME);
	private static Document document; // Document XML issu du fichier

	/* Singleton */
	private static ConfigManager INSTANCE;

	private ConfigManager() throws ParserConfigurationException, SAXException, IOException, ChecksumException {
		if (!xmlFile.exists())
			throw new IOException("Le fichier de configuration n'est pas présent "
					+ "dans le répertoire d'installation (" + xmlFile.getPath() + ").");

		document = XmlUtil.load(xmlFile, xsdFile);
		if (document == null)
			throw new ParserConfigurationException("Le fichier de configuration est corrompu.");

		try {
			XmlChecksum checker = new XmlChecksum(document);
			Mazette.LOGGER.info("Calcul : " + String.format("0x%08X", checker.calculateChecksum()) + ", sauvegardé : "
					+ String.format("0x%08X", checker.getSavedChecksum()));

			if (checker.calculateChecksum() != checker.getSavedChecksum())
				throw new ChecksumException("Le fichier de configuration est corrompu.");
		} catch (ParserConfigurationException e) {
			throw new ParserConfigurationException("Le fichier de configuration est corrompu.");
		}
	}

	public static ConfigManager getInstance()
			throws ParserConfigurationException, SAXException, IOException, ChecksumException {
		if (INSTANCE == null)
			INSTANCE = new ConfigManager();

		return INSTANCE;
	}

	/**
	 * Fournit n'importe quelle donnée du fichier XML de configuration ; la méthode
	 * est une spécialisation de {@link #getElement(Node, String)} dans laquelle le
	 * noeud de départ correspond à la racine du fichier.
	 * 
	 * @param path Le chemin complet vers l'élément XML
	 * @return L'instance Node associée à l'élément XML
	 */
	public static Node getElement(String path) {
		return XmlUtil.getElement(document.getDocumentElement(), path);
	}

	/* Méthodes d'extraction des données */

	/** Récupère le nom de l'application. */
	public String getAppName() {
		return getElement("ConfigAppli(nomAppli)").getTextContent();
	}

	/** Récupère la version de l'application. */
	public String getAppVersion() {
		return getElement("ConfigAppli(versionAppli)").getNodeValue();
	}

	/** Récupère la version du fichier de configuration. */
	public String getConfigVersion() {
		return getElement("ConfigAppli(versionConfig)").getNodeValue();
	}

	/** Récupère l'attribut présentant les droits d'auteur du projet. */
	public String getCopyright() {
		return getElement("ConfigAppli(copyright)").getNodeValue();
	}

	/** Récupère une description succinte de l'application. */
	public String getAppDescription() {
		return getElement("ConfigAppli(description)").getNodeValue();
	}

	/**
	 * Récupère le répertoire de sauvegarde ayant le nom indiqué en paramètre.
	 * 
	 * @param msgID Identifiant du répertoire dans le fichier de configuration.
	 * @return Chemin vers le dossier de sauvegarde.
	 */
	public String getDirectory(String msgID) {
		return getElement("Repertoire[" + msgID + "]").getTextContent();
	}

	/**
	 * Classe indiquant les exceptions levées pendant la lecture de paramètres dans
	 * le fichier de configuration.
	 * 
	 * @author X2020636 (Martin ROSTAGNAT)
	 */
	public static class ConfigException extends Exception {
		private static final long serialVersionUID = 9093898370192747723L;

		public ConfigException() {
			super();
		}

		public ConfigException(String message) {
			super(message);
		}
	}
}