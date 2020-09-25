package com.rosty.maze.widgets;

/**
 * Composant {@link GTextField} spécifique permettant à l'utilisateur de
 * renseigner une valeur entière à 8 octets dans le champ de texte.
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class GLongField extends GTextField {
	public GLongField() {
		setPattern("-?[0-9]*");
	}

	/** Fournit le contenu du champ de texte sous forme d'un entier. */
	public long getValue() {
		return (getText() == null || getText().isEmpty() || getText().equals("-")) ? 0 : Long.parseLong(getText());
	}

	/** Définit un entier comme valeur du champ de texte. */
	public void setValue(long value) {
		setText("" + value);
	}
}