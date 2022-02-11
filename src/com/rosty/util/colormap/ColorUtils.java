package com.rosty.util.colormap;

import java.util.Random;

import javafx.scene.paint.Color;

/**
 * Classe utilitaire pour la génération de couleurs (cf. classe {@link Color}).
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class ColorUtils {
	/**
	 * Construit une couleur à mi-chemin entre deux couleurs prédéfinies. Un nombre,
	 * défini en entrée de la fonction, indique le pourcentage d'utilisation des
	 * couleurs.
	 * <p>
	 * La couleur résultante est construite grâce à la méthode
	 * {@link Color#interpolate(Color, double)}.
	 * 
	 * @param former Première couleur.
	 * @param latter Seconde couleur.
	 * @param ratio  Ratio d'utilisation des couleurs (<b>0.0</b> : couleur 1 -
	 *               <b>1.0</b> : couleur 2).
	 * @return Couleur obtenue par interpolation.
	 */
	public static Color middleColor(Color former, Color latter, double ratio) {
		return former.interpolate(latter, ratio);
	}

	/**
	 * Construit une couleur avec un code RGB aléatoire et une opacité prédéfinie.
	 * 
	 * @param opacity Seuil d'opacité (<b>0.0</b> : transparent - <b>1.0</b> :
	 *                opaque).
	 */
	public static Color random(double opacity) {
		Random ran = new Random();
		return Color.color(ran.nextDouble(), ran.nextDouble(), ran.nextDouble(), opacity);
	}

	/** Construit une couleur opaque avec un code RGB aléatoire. */
	public static Color random() {
		Random ran = new Random();
		return Color.color(ran.nextDouble(), ran.nextDouble(), ran.nextDouble());
	}

	/**
	 * Construit une couleur avec un code RGB aléatoire. L'opacité, si elle est
	 * désactivée, est aléatoire.
	 */
	public static Color random(boolean opaque) {
		return (opaque ? random() : random((new Random()).nextDouble()));
	}
}