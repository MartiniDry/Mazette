package com.rosty.util.javafx;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.ObjectProperty;

/**
 * Classe utilitaire pour des opérations liées aux instances JavaFX de
 * l'application, aussi bien la liaison de données que l'affichage graphique.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class FxUtils {
	/**
	 * Etablit une liaison de données entre une propriété JavaFX (paramètre-cible)
	 * et un membre de classe (paramètre-source). C'est une alternative à la
	 * fonction {@link ObjectProperty#bind} qui peut être utile lorsque le
	 * paramètre-source n'est pas une propriété JavaFX ou lorsqu'un <i>getter</i> ou
	 * un <i>setter</i> spécifique est défini.
	 * 
	 * @param property Propriété liée au paramètre-cible.
	 * @param getter   Fonction fournissant le paramètre-source.
	 * @param setter   Fonction définissant le paramètre-source.
	 */
	public static final <T> void link(ObjectProperty<T> property, Supplier<T> getter, Consumer<T> setter) {
		property.setValue(getter.get());
		property.addListener((bean_p, old_p, new_p) -> setter.accept(new_p));
	}
}