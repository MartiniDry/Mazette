package com.rosty.maze.model.algorithm.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.algorithm.MazeGenerationAlgorithm;
import com.rosty.maze.widgets.MazePanel;

/**
 * <h1>Algorithme de Kruskal standard</h1>
 * 
 * <p>
 * <h2>Principe</h2> L'algorithme consiste à retirer pas à pas des murs dans le
 * labyrinthe, de façon aléatoire, jusqu'à obtenir le labyrinthe voulu. A chaque
 * étape, l'algorithme veille à ne pas former d'ilôt dans le labyrinthe.
 * </p>
 * 
 * <p>
 * <h2>Dans le détail :</h2>
 * <ol>
 * <li>Pour commencer, l'agorithme associe un numéro unique à chaque case, en
 * incrémentant de gauche à droite et de haut en bas à partir de 2.<br/>
 * <u>Exemple :</u>
 * <table border="1">
 * <tr>
 * <th>2</th>
 * <th>3</th>
 * <th>4</th>
 * <th>5</th>
 * </tr>
 * <tr>
 * <th>6</th>
 * <th>7</th>
 * <th>8</th>
 * <th>9</th>
 * </tr>
 * <tr>
 * <th>10</th>
 * <th>11</th>
 * <th>12</th>
 * <th>13</th>
 * </tr>
 * <tr>
 * <th>14</th>
 * <th>15</th>
 * <th>16</th>
 * <th>17</th>
 * </tr>
 * </table>
 * </li>
 * <li>On itère en analysant chaque mur intérieur i.e. qui n'est pas présent en
 * bordure du terrain. Pour chaque mur, on identifie les deux cases voisines et
 * on observe leurs valeurs :
 * <ul>
 * <li>Si les valeurs sont différentes, on retire le mur pour relier les deux
 * cases. Plus précisément, si l'on note M la plus petite valeur et N la plus
 * grande, on attribue la valeur M à toutes les cases du terrain ayant pour
 * valeur N.</li>
 * <li>Si les valeurs sont les mêmes, le mur ne sera pas retiré car les cases
 * sont déjà voisines.</li>
 * </ul>
 * </li>
 * <li>L'algorithme s'arrête lorsque tous les murs intérieurs ont été visités. A
 * ce stade, toutes les cases seront obligatoirement réliées entre elles.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <h2>Complexité</h2> Pour un labyrinthe parfait (sans ilôts), le nombre
 * d'étapes est égal au nombre de cases oté de 1. En effet, retirer ce nombre de
 * murs est suffisant pour que toutes les cases soient reliées par un même
 * chemin :<br/>
 * <b>N<sub>steps</sub> = M.N - 1</b><br/>
 * Toute étape supplémentaire ajoute un ilôt dans le labyrinthe.
 * </p>
 * 
 * <p>
 * <b>NB :</b> On nomme cet algorithme par similarité avec le vrai algorithme de
 * Kruskal (cf. <a href="https://fr.wikipedia.org/wiki/Algorithme_de_Kruskal">la
 * page Wikipédia dédiée</a>). Il s'agit d'un abus de langage : en effet, dans
 * une configuration similaire, l'algorithme cherche à recouvrir le plateau en
 * respectant la contrainte du plus proche voisin, pour former un arbre passant
 * par toutes les cases du terrain. Cette contrainte n'existe pas pour notre
 * algorithme puisque toutes les cases sont voisines de la même distance ; il
 * s'agit donc de former un arbre en sélectionnant au hasard deux cases voisines
 * sur le terrain et en retirant le mur les séparant.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class KruskalAlgorithm extends MazeGenerationAlgorithm {
	/**
	 * Liste des murs intérieurs i.e. autres que les bordures du terrain, qui n'ont
	 * pas été analysés par l'algorithme. Tous les murs doivent être analysés pour
	 * déterminer s'il est nécessaire de les enlever ou non pour former l'arbre des
	 * chemins du labyrinthe.
	 */
	private List<WallCoord> walls = new ArrayList<>();

	/** Générateur de nombres aléatoires. */
	private final Random rand = new Random();

	/**
	 * Constructeur de la classe {@link KruskalAlgorithm}.
	 * 
	 * @param panel Composant graphique du labyrinthe.
	 */
	public KruskalAlgorithm(MazePanel panel) {
		super(panel);
	}

	@Override
	public void init() {
		/* Etape 1 : attribution d'un numéro identifiant chaque case du terrain. */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				mazePanel.getMaze().setCell(i, j, 2 + i * nbCol + j);

		/*
		 * Etape 2 : mémorisation des murs "intérieurs" du terrain dans la liste
		 * "walls". Cette liste indiquera en temps réel les murs qui n'ont pas été
		 * analysés par l'algorithme.
		 */
		walls.clear();
		for (int i = 0, lenI = nbRow - 1; i < lenI; i++) {
			for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++)
				walls.add(new WallCoord(i, j, Side.RIGHT));

			for (int j = 0; j < nbCol; j++)
				walls.add(new WallCoord(i, j, Side.DOWN));
		}

		for (int j = 0, lenJ = nbCol - 1; j < lenJ; j++)
			walls.add(new WallCoord(nbRow - 1, j, Side.RIGHT));
	}

	@Override
	public boolean isComplete() {
		// L'algorithme s'arrête lorsque tous les murs intérieurs du terrain ont été
		// analysés.
		return walls.isEmpty();
	}

	@Override
	public void step() {
		/* Etape 1 : sélection aléatoire d'un mur non-analysé. */
		int start = rand.nextInt(walls.size());
		int wallPosition = start;
		WallCoord wall = walls.get(wallPosition);

		int cell = mazePanel.getCell(wall.x, wall.y); // Valeur de la case associée au mur
		int neighbour = mazePanel.getNeighbourCell(wall); // Valeur de la case voisine

		/*
		 * Etape 2 : si les deux cases entourant le mur sont de même valeur, on retire
		 * le mur de la liste et on réitère l'étape 1 jusqu'à trouver deux valeurs
		 * distinctes.
		 */
		while (cell == neighbour) {
			walls.remove(wallPosition);
			// S'il n'y a plus de murs à analyser, l'étape est avortée.
			if (walls.isEmpty())
				return;

			wallPosition = rand.nextInt(walls.size());
//			wallPosition++;
//			wallPosition %= walls.size();
			wall = walls.get(wallPosition);

			cell = mazePanel.getCell(wall.x, wall.y);
			neighbour = mazePanel.getNeighbourCell(wall);
		}

		// Retrait du mur entre les deux cases
		mazePanel.setWall(wall.x, wall.y, wall.side, 0);

		/*
		 * Etape 3 : mise à jour des valeurs du terrain : chaque case ayant la valeur la
		 * plus forte obtient la valeur la plus faible.
		 */
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				if (mazePanel.getCell(i, j) == neighbour)
					mazePanel.getMaze().setCell(i, j, cell);
	}
}