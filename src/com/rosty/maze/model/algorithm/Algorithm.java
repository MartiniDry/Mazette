package com.rosty.maze.model.algorithm;

/**
 * Classe définissant l'implémentation d'un <b>algorithme</b> i.e. une suite
 * d'opérations finies.
 * <p>
 * Un algorithme s'articule autour de 3 grandes étapes :
 * <ul>
 * <li><u>l'amorce :</u> l'algorithme est initialisé pour préparer son
 * lancement</li>
 * <li><u>la récurrence :</u> l'algorithme enchaîne une succession d'étapes</li>
 * <li><u>la terminaison :</u> l'algorithme ne s'arrête que lorsqu'il respecte
 * un critère d'arrêt spécifique.</li>
 * </ul>
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public abstract class Algorithm {
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
}