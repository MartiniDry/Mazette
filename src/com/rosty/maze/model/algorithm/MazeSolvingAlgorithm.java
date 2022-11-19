package com.rosty.maze.model.algorithm;

import com.rosty.maze.widgets.MazePanel;

public abstract class MazeSolvingAlgorithm extends Algorithm {
	/** Labyrinthe sur lequel sera appliqué l'algorithme. */
	protected MazePanel mazePanel;
	/**
	 * Nombre de lignes et de colonnes du labyrinthe ; ces attributs sont des
	 * tampons pour ne pas avoir à requérir plusieurs fois la taille du labyrinthe
	 * dans l'attribut {@link Algorithm#mazePanel} ; ils sont facultatifs.
	 */
	protected int nbRow, nbCol;

	/**
	 * Constructeur de la classe {@link MazeSolvingAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public MazeSolvingAlgorithm(MazePanel panel) {
		this.mazePanel = panel;

		// Stockage des dimensions du labyrinthe
		nbRow = mazePanel.getMaze().getNbRows();
		nbCol = mazePanel.getMaze().getNbColumns();
	}
	
	@Override
	public String getLabel() {
		return "resolution";
	}
}