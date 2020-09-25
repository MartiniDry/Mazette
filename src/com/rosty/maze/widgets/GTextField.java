package com.rosty.maze.widgets;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

/**
 * Spécialisation du composant {@link TextField} permettant de contrôler le
 * texte renseigné dans le champ. L'utilisateur définit une expression régulière
 * présentant le motif auquel doit se conformer le texte.
 * <p>
 * Le composant contient deux propriétés spécifiques :
 * <ul>
 * <li><b>pattern</b> : le motif de texte que le champ de texte doit respecter.
 * La syntaxe de ce paramètre est celle d'une expression régulière en Java (cf.
 * {@link java.util.regex.Pattern}).</li>
 * <li><b>constraint</b> : précise le comportement à adopter en cas de mauvaise
 * syntaxe du champ de texte. Dans une telle situation, soit le texte n'est pas
 * mis à jour (contrainte <b>HARD</b> car on restreint l'affichage du texte),
 * soit une exception est levée (contrainte <b>SOFT</b>), ce qui permet à
 * l'utilisateur de définir lui-même le comportement à adopter, soit l'on ne
 * fait rien (contrainte <b>NONE</b>).</li>
 * </ul>
 * </p>
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class GTextField extends TextField {
	/** Propriété indiquant le motif que doit respecter le texte. */
	public StringProperty patternProperty = new SimpleStringProperty();

	public String getPattern() {
		return patternProperty.get();
	}

	public void setPattern(String pat) {
		patternProperty.set(pat);
	}

	/**
	 * Propriété indiquant la présence d'une contrainte d'affichage du champ,
	 * obligeant l'utilisateur à taper un texte conforme au motif.
	 */
	public ObjectProperty<Behaviour> constraintProperty = new SimpleObjectProperty<>(Behaviour.NONE);

	public Behaviour getConstraint() {
		return constraintProperty.get();
	}

	public void setConstraint(Behaviour b) {
		constraintProperty.set(b);
	}

	/** Constructeur de la classe {@link GTextField}. */
	public GTextField() {
		textProperty().addListener((bean_p, old_p, new_p) -> {
			if (new_p != null && !new_p.isEmpty())
				if (!new_p.matches(getPattern()))
					switch (getConstraint()) {
					case SOFT:
						throw new RuntimeException(
								"Le texte entré ne respecte pas l'expression régulière " + getPattern());
					case HARD:
						setText(old_p);
						break;
					case NONE:
					default:
						break;
					}
		});
	}

	/** Indique si le contenu du champ de texte respecte l'expression régulière. */
	public boolean respectsPattern() {
		return getText().matches(getPattern());
	}

	/**
	 * Enumération des comportements que peut adopter le champ de texte lorsque
	 * l'utilisateur entre un caractère qui ne satisfait pas le modèle.
	 * 
	 * @author X2020636 (Martin ROSTAGNAT)
	 */
	public enum Behaviour {
		/** Rien ne se passe lorsque le texte ne suit pas le modèle imposé. */
		NONE,

		/**
		 * Une RuntimeException est lancée lorsque le texte ne suit pas le modèle
		 * imposé.
		 */
		SOFT,

		/**
		 * Le texte entré est contraint au modèle imposé ; il est impossible d'écrire du
		 * texte qui ne respecte pas le modèle.
		 */
		HARD;
	}
}