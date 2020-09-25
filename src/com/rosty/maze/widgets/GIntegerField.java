package com.rosty.maze.widgets;

/**
 * Composant {@link GTextField} spécifique permettant à l'utilisateur de
 * renseigner une valeur entière dans le champ de texte.
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class GIntegerField extends GTextField {
	public GIntegerField() {
		setPattern("-?[0-9]*");
	}

	/** Fournit le contenu du champ de texte sous forme d'un entier. */
	public int getValue() {
		return (getText() == null || getText().isEmpty() || getText().equals("-")) ? 0 : Integer.parseInt(getText());
	}

	/** Définit un entier comme valeur du champ de texte. */
	public void setValue(int value) {
		setText("" + value);
	}
}