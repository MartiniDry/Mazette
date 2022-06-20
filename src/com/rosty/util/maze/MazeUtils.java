package com.rosty.util.maze;

import com.rosty.maze.model.Maze;

/**
 * Classe utilitaire dédiée aux opérations de plus haut niveau sur les
 * labyrinthes.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MazeUtils {
	/**
	 * Détermine le nombre d'enclos dans le labyrinthe spécifié.
	 * <p>
	 * <h2>Définition</h2> Un <b>enclos</b> est un ensemble de cellules reliées
	 * entre elles. Dans un enclos, il est possible d'aller d'une cellule à une
	 * autre via un ou plusieurs chemins.
	 * </p>
	 */
	public int enclosures(Maze maze) {
		return 1;
	}

	/**
	 * Détermine le nombre d'ilôts dans le labyrinthe spécifié.
	 * <p>
	 * <h2>Définition</h2> Un <b>ilôt</b> est un ensemble de murs reliés entre eux
	 * qui ne sont pas reliés au bord du labyrinthe. Par conséquent, si l'on
	 * sélectionne une case voisine de l'ilôt, il est possible de tracer un chemin
	 * qui fait tout le tour de cette dernière.
	 * </p>
	 */
	public int islets(Maze maze) {
		return 0;
	}

	/**
	 * Détermine si le labyrinthe spécifié est connexe.
	 * <p>
	 * <h2>Définition</h2> Un labyrinthe est <b>connexe</b> si l'on peut tracer un
	 * chemin entre deux cases quelconques du labyrinthe.
	 * </p>
	 * <p>
	 * <h2>Propriété</h2> Un labyrinthe est connexe si et seulement s'il est
	 * constitué d'un seul et unique enclos. Le résultat se déduit aisément en
	 * observant les définitions.
	 * </p>
	 */
	public boolean isConnected(Maze maze) {
		return (enclosures(maze) == 1);
	}

	/**
	 * Détermine si le labyrinthe spécifié est un labyrinthe parfait.
	 * <p>
	 * <h2>Définition</h2> Un labyrinthe est dit <b>parfait</b> si l'on peut relier
	 * deux cases quelconques avec un seul et unique chemin.
	 * </p>
	 * <p>
	 * <h2>Propriété</h2> Un labyrinthe est parfait si et seulement s'il est connexe
	 * et sans ilôt.
	 * <ul>
	 * <li><u>Condition nécessaire :</u> si un labyrinthe est parfait, alors il est
	 * obligatoirement connexe. La propriété sur les ilôts peut être obtenue par
	 * contraposition. Supposons qu'il existe un ilôt dans le labyrinthe ; on sait
	 * qu'il existe au moins deux cases distinctes qui se trouvent à son bord. Si
	 * l'on sélectionne deux de ces cases, alors on peut les relier grâce à deux
	 * chemins différents (par la gauche et par la droite de l'ilôt), ce qui
	 * implique que le labyrinthe n'est pas parfait.</li>
	 * <li><u>Condition suffisante :</u> faisons un raisonnement par l'absurde.
	 * Supposons un labyrinthe parfait possédant l'une des propriétés suivantes :
	 * <ol>
	 * <li>Si le labyrinthe n'est pas connexe, alors il existe au moins deux enclos.
	 * Il est impossible de tracer un chemin entre deux cases venant de deux enclos
	 * différents, ce qui est ABSURDE puisque le labyrinthe est parfait.</li>
	 * <li>Si le labyrinthe possède au moins un ilôt, alors il est possible de
	 * trouver deux cases distinctes se trouvant à son bord. Comme écrit
	 * précédemment, on peut relier ces deux cases grâce à deux chemins différents,
	 * ce qui est ABSURDE puisque le labyrinthe est parfait.</li>
	 * </ol>
	 * </li>
	 * </ul>
	 * </p>
	 */
	public boolean isPerfect(Maze maze) {
		return (isConnected(maze) && islets(maze) == 0);
	}
}