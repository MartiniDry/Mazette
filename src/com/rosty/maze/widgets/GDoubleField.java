package com.rosty.maze.widgets;

/**
 * Composant {@link GTextField} spécifique permettant à l'utilisateur de
 * renseigner un nombre décimal dans le champ de texte.
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class GDoubleField extends GTextField {
	public GDoubleField() {
		setPattern("^-?[0-9]*\\.?[0-9]*$");
	}

	/** Fournit le contenu du champ de texte sous forme d'un nombre décimal. */
	public double getValue() {
		return (getText() == null || getText().isEmpty()) ? 0 : Double.parseDouble(getText());
	}

	/** Définit un nombre décimal comme valeur du champ de texte. */
	public void setValue(double value) {
		setText("" + value);
	}
}