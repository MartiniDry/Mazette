package com.rosty.util.colormap;

import javafx.scene.paint.Color;

/**
 * Interface définissant une carte de couleurs qui peut être utilisée pour
 * représenter différentes valeurs d'un objet dans l'IHM.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public interface ColorMap<T extends Number> {

	/**
	 * Insère une couleur à la position spécifiée.
	 * 
	 * @param key   Indice de la couleur.
	 * @param value Couleur JavaFX.
	 */
	public void add(T key, Color value);

	/**
	 * Retire, si elle existe, la couleur présente à la position spécifiée.
	 * 
	 * @param key Position de la couleur.
	 */
	public void delete(T key);

	/**
	 * Fournit, si elle existe, la couleur présente à la position indiquée.
	 * 
	 * @param position Position de la couleur dans la carte.
	 */
	public Color get(T position);

	/**
	 * Fournit la couleur présente à la position indiquée ou renvoie une couleur par
	 * défaut si elle n'existe pas.
	 * 
	 * @param position Position de la couleur dans la carte.
	 * @param defColor Couleur par défaut.
	 */
	public Color getOrDefault(T position, Color defColor);

	/** Efface l'ensemble des couleurs de la carte. */
	public void clear();

}
