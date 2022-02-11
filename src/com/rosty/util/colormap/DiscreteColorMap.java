package com.rosty.util.colormap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;

/**
 * Classe représentant une plage de couleurs discrète : à chaque clé (entier
 * 32bits) est associée une valeur bien définie (instance {@link Color}). Cette
 * plage peut être définie de deux façons différentes :
 * <ol>
 * <li>En plaçant les couleurs une par une dans une <b>carte</b>. Exemple :
 * 
 * <pre>
 * DiscreteColorMap map = new DiscreteColorMap();
 * map.put(1, Color.GREEN);
 * map.put(0, Color.RED);
 * </pre>
 * 
 * La carte est triée par ordre croissant des clés.</li>
 * <li>En définissant un <b>profil</b> pour l'ensemble de la plage de couleurs.
 * Exemple :
 * 
 * <pre>
 * DiscreteColorMap map = new DiscreteColorMap();
 * Function<Integer, Color> fadeIn = (n -> {
 * 	if (n < 0)
 * 		return Color.BLACK;
 * 	else if (n > 255)
 * 		return Color.YELLOW;
 * 	else
 * 		return Color.color(0, n, n);
 * });
 * map.setProfile(fadeIn);
 * </pre>
 * 
 * </li>
 * </ol>
 * <p>
 * On peut composer une plage de couleurs en combinant ces deux méthodes et la
 * première est prioritaire face à la seconde. Lorsque l'utilisateur recherche
 * la couleur pour un entier donné, le programme vérifie d'abord si l'entier est
 * présent dans la carte des couleurs ; dans le cas contraire, la couleur est
 * calculée en utilisant le profil.
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 * @see {@link ColorUtils}
 */
public class DiscreteColorMap {
	/** Profil de couleur global. */
	private Function<Integer, Color> profile = null;

	/** Carte des couleurs, utilisée pour définir des valeurs particulières. */
	private final SortedMap<Integer, Color> map = new TreeMap<>();

	/**
	 * Fournit la couleur présente à l'entier indiqué. La méthode recherche d'abord
	 * dans la carte des couleurs, puis en cas d'absence calcule le résultat grâce
	 * au profil de couleur.
	 * 
	 * @param key Clé de la couleur (entier 32bits).
	 */
	public Color get(int key) {
		if (map.containsKey(key))
			return map.get(key);
		else if (profile != null)
			return profile.apply(key);
		else
			return null;
	}

	/** Définit le profil de couleur global. */
	public void setProfile(Function<Integer, Color> profile) {
		this.profile = profile;
	}

	/** Associe une couleur à un entier particulier. */
	public Color put(int key, Color value) {
		return map.put(key, value);
	}

	/** Ajoute un jeu de couleurs à des entiers particuliers. */
	public void putAll(Map<Integer, Color> map) {
		map.putAll(map);
	}

	/** Retire un élément de la carte des couleurs. */
	public Color remove(int key) {
		return map.remove(key);
	}

	/**
	 * Retire un élément de la carte des couleurs lorsque celle-ci possède la valeur
	 * indiquée.
	 */
	public boolean remove(int key, Color value) {
		return map.remove(key, value);
	}

	/** Fournit l'ensemble des entiers de la carte des couleurs. */
	public Set<Integer> keySet() {
		return map.keySet();
	}

	/** Fournit l'ensemble des couleurs de la carte des couleurs. */
	public Collection<Color> values() {
		return map.values();
	}

	/** Indique si un entier est présent dans la carte des couleurs. */
	public boolean containsKey(int key) {
		return map.containsKey(key);
	}

	/**
	 * Construit une instance {@link DiscreteColorMap} à partir d'une chaîne de
	 * caractères (ou la valeur <code>null</code> si le <i>parsing</i> a échoué).
	 * <p>
	 * Les règles de syntaxe sont les suivantes :
	 * <ul>
	 * <li>Pour remplir la carte des couleurs, on insère des éléments de la façon
	 * suivante : <code>[N; COLOR]</code>.</li>
	 * </ul>
	 * <ul>
	 * <li>Les éléments de la carte sont espacés entre eux.<br/>
	 * Exemple : <code>[0; black] [2; rgb(42,21,68)] [5; #0FF]</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param str Chaîne de caractères représentant une instance
	 *            {@link DiscreteColorMap}.
	 */
	public static final DiscreteColorMap fromString(String str) {
		DiscreteColorMap map = new DiscreteColorMap();

		String regex = "\\[-?\\d+;\\s*(\\w+|#[a-fA-F0-9]+)\\]";
		Matcher match = Pattern.compile(regex).matcher(str);
		while (match.find()) {
			String item = match.group();
			String inside = item.substring(1, item.length() - 1);
			String[] keyAndValue = inside.split(";\\s*");

			int key = Integer.parseInt(keyAndValue[0]);
			Color value = Color.web(keyAndValue[1]);
			map.put(key, value);
		}

		return map;
	}

	@Override
	public final String toString() {
		List<String> items = new ArrayList<>();
		for (Integer k : map.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(k.intValue());
			sb.append("; ");
			sb.append(map.get(k).toString());
			sb.append("]");

			items.add(sb.toString());
		}

		return String.join(" ", items);
	}
}