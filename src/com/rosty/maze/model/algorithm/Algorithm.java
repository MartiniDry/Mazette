package com.rosty.maze.model.algorithm;

import com.rosty.maze.Mazette;

/**
 * Classe définissant l'implémentation d'un <b>algorithme</b> i.e. une suite
 * d'opérations finies.
 * <p>
 * Un algorithme s'articule autour de 3 grandes étapes :
 * <ul>
 * <li><u>l'amorce :</u> l'algorithme est initialisé pour préparer son
 * lancement</li>
 * <li><u>la récurrence :</u> l'algorithme enchaîne une succession d'étapes</li>
 * <li><u>la terminaison :</u> l'algorithme ne prend fin que lorsqu'il respecte
 * un critère d'arrêt spécifique.</li>
 * </ul>
 * 
 * Dans certains cas, une dernière étape peut être ajoutée : <u>la finition</u>
 * peut être utilisée pour afficher les résultats de l'algorithme après
 * exécution.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.1
 */
public abstract class Algorithm {
	/**
	 * Fournit un label désignant l'algorithme. Ce label peut-être:
	 * <ul>
	 * <li>Le nom de l'algorithme ou de l'instance courante.</li>
	 * <li>Une clé de propriété pour nommer l'algorithme dans différentes
	 * langues.</li>
	 * </ul>
	 */
	public abstract String getLabel();

	/**
	 * Initialise l'algorithme en préparant le labytinthe et en chargeant des
	 * paramètres spécifiques. Cette méthode est également appelée lorsque l'on
	 * souhaite relancer l'algorithme.
	 */
	public abstract void init();

	/**
	 * Indique si l'algorithme respecte le critère d'arrêt et si l'on peut donc y
	 * mettre fin.
	 */
	public abstract boolean isComplete();

	/** Exécute l'étape suivante de l'algorithme. */
	public abstract void step();

	/**
	 * Lance la dernière action de l'algorithme, une fois le critère d'arrêt
	 * atteint. Cette fonction peut être surchargée.
	 */
	public void finish() {
		Mazette.LOGGER.info(getLabel() + " - end of execution");
	}
}