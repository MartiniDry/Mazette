package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de séparation récursive</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à diviser la grille en 2 pièces
 * (reliées par une porte), puis à répéter l'opération pour les deux pièces
 * résultantes. L'opération fractionne peu à peu les zones dans la grille
 * jusqu'à rendre les pièces indivisibles i.e. d'une largeur égale à une case.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2> Partant d'une grille vide, l'algorithme place au
 * hasard un mur dans la grille ; celle-ci sera placée soit sur toute la largeur
 * soit sur toute la hauteur de la grille. La grille est alors divisée en deux
 * pièces. Comme toutes les cases doivent rester unies, une ouverture de largeur
 * 1 est placée sur le mur pour relier les deux pièces.
 * </p>
 * <p>
 * Le procédé est itératif ; après division de la grille en deux pièces,
 * l'opération est répétée pour chacune d'elles, et ainsi de suite. Si un mur
 * vertical a été placé lors de l'itération N, les murs qui seront placés à la
 * N+1<sup>ème</sup> itération seront horizontaux ; inversement si un mur
 * horizontal a été placé.
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Pour un labyrinthe parfait (sans ilôts), en considérant
 * que le premier mur est horizontal, l'opération place un mur, puis deux, puis
 * quatre, etc.
 * </p>
 * <p>
 * La complexité totale est donc :
 * 
 * <pre>
 * SUM<sub> 0,log<sub>2</sub>(min(M,N)) </sub>(2<sup>k</sup>) = 2<sup>log<sub>2</sub>(min(M,N))</sup> - 1
 *                    = min(M, N) - 1
 * </pre>
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class RecursiveDivisionAlgorithm extends MazeGenerationAlgorithm {
	/** Liste des pièces obtenues par divisions successives. */
	List<Section> sections = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

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

		// Initialisation de la liste avec la première section
		sections.add(new Section(0, 0, nbRow, nbCol));
	}

	@Override
	public boolean isComplete() {
		boolean done = true;
		for (Section sect : sections)
			done &= isIndivisible(sect);

		return done;
	}

	@Override
	public void step() {
		placeWallWithDoor(true, 3, 1, 7);
		placeWallWithDoor(true, 4, 1, 7);
		placeWallWithDoor(true, 5, 1, 7);
		placeWallWithDoor(true, 6, 1, 7);
		placeWallWithDoor(true, 7, 1, 7);
	}

	/**
	 * Installe une cloison dans la grille (via la méthode
	 * {@link RecursiveDivisionAlgorithm::placeWall}) puis place aléatoirement une
	 * ouverture de taille 1 dans ce mur.
	 */
	private void placeWallWithDoor(boolean vertical, int lineId, int start, int end) {
		placeWall(vertical, lineId, start, end);
		if (end - start > 1) {
			int index = start + rand.nextInt(end - start);
			Side wallSide = vertical ? Side.RIGHT : Side.DOWN;
			mazePanel.setWall(index, lineId, wallSide, 0);
		}
	}

	/**
	 * Installe une cloison dans la grille. Cette cloison est soit verticale, soit
	 * horizontale ; elle est définie par sa position (i.e. sa distance au bord
	 * supérieur gauche) et par celle des deux points délimitant les bords de la
	 * cloison.
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
	 */
	private void placeWall(boolean vertical, int lineId, int start, int end) {
		Side wallSide = vertical ? Side.RIGHT : Side.DOWN;
		for (int i = start; i < end; i++)
			mazePanel.setWall(i, lineId, wallSide, 1);
	}

	/**
	 * Indique si la section de la grille peut être divisée par un mur.
	 * 
	 * @param section Pièce de la grille obtenue par divisio de la grille.
	 */
	private boolean isIndivisible(Section sect) {
		return sect.width == 1 || sect.height == 1;
	}

	/**
	 * Classe définissant une section de la grille délimitée par des murs (également
	 * nommée une <b>pièce</b>).
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	private class Section {
		int leftIndex, topIndex; // Coordonnées du bord supérieur gauche de la section
		int width, height; // Largeur et hauteur de la section

		/**
		 * Constructeur de la classe {@link Section}.
		 * 
		 * @param lId Indice du bord gauche de la section.
		 * @param tId Indice du bord supérieur de la section.
		 * @param w   Largeur en nombre de cases.
		 * @param h   Hauteur en nombre de cases.
		 */
		Section(int lId, int tId, int w, int h) {
			this.leftIndex = lId;
			this.topIndex = tId;
			this.width = w;
			this.height = h;
		}
	}
}