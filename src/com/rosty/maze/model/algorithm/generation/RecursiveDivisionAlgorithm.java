package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

public class RecursiveDivisionAlgorithm extends MazeGenerationAlgorithm {
	List<int[]> sections = new ArrayList<>();
	
	/**
	 * Constructeur de la classe {@link RecursiveDivisionAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public RecursiveDivisionAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		// Effacement de tous les murs à l'intérieur de la grille
		for (int i = 0; i < nbRow - 1; i++) {
			for (int j = 0; j < nbCol - 1; j++) {
				mazePanel.setWall(i, j, Side.DOWN, 0);
				mazePanel.setWall(i, j, Side.RIGHT, 0);
			}

			mazePanel.setWall(i, nbCol - 1, Side.DOWN, 0);
		}

		for (int j = 0; j < nbCol - 1; j++)
			mazePanel.setWall(nbRow - 1, j, Side.RIGHT, 0);
	}

	@Override
	public boolean isComplete() {
		return false;
	}

	@Override
	public void step() {
		;
	}

	/**
	 * Installe une cloison dans la grille. Cette cloison est soit verticale, soit
	 * horizontale ; elle est définie par sa position (i.e. sa distance au bord
	 * supérieur gauche) et par celle des deux points délimitant les bords de la
	 * cloison.
	 * <p>
	 * Dans le cadre de l'algorithme de division récursive, il ne faut pas
	 * cloisonner la grille à seulement une case du bord. Pour cette raison, la
	 * méthode renvoit un booléen indiquant si le mur à placer respecte cette
	 * condition. Le mur sera dessiné si et seulement si ce booléen a pour valeur
	 * <code>true</code>.
	 * </p>
	 * 
	 * @param vertical Booléen indiquant si le mur est vertical ou horizontal.
	 * @param lineId   Numéro de ligne : on considère par convention, que la ligne
	 *                 d'indice 0 est celle qui est voisine du bord de la grille (en
	 *                 haut si la ligne est horizontale, à gauche si elle est
	 *                 verticale).
	 * @param start    Indice localisant le point de départ du mur. Par convention,
	 *                 l'indice vaut 0 si le point se situe sur le bord de la grille
	 *                 (en haut si la ligne est verticale, à gauche si la ligne est
	 *                 horizontale).
	 * @param end      Indice localisant le point d'arrivée du mur. La convention
	 *                 est la même que pour le point de départ.
	 * @return <code>true</code> si le mur a été placé dans la grille,
	 *         <code>false</code> sinon.
	 */
	private boolean partition(boolean vertical, int lineId, int start, int end) {
		if (vertical) {
			if (lineId >= 1 && nbCol - lineId > 2) {
				for (int i = start; i < end; i++)
					mazePanel.setWall(i, lineId, Side.RIGHT, 1);

				return true;
			} else
				return false;
		} else {
			if (lineId >= 1 && nbRow - lineId > 2) {
				for (int j = start; j < end; j++)
					mazePanel.setWall(lineId, j, Side.DOWN, 1);

				return true;
			} else
				return false;
		}
	}
}