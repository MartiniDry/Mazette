package com.rosty.util.xml.checksum;

/**
 * Exception qui est levée en cas d'anomalie liée au calcul de la somme de
 * contrôle (<i>checksum</i>) du fichier.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ChecksumException extends Exception {
	private static final long serialVersionUID = 8204636151367560412L;

	public ChecksumException() {
		super();
	}

	public ChecksumException(String message) {
		super(message);
	}
}