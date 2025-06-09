package com.rosty.util.colormap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rosty.util.javafx.ColorUtils;

import javafx.scene.paint.Color;

/**
 * Classe représentant une plage de couleurs continue : à chaque clé (nombre
 * décimal 64bits) est associée une valeur bien définie (instance
 * {@link Color}). Cette plage est définie en plaçant les couleurs une par une
 * dans une <b>carte</b>. Exemple :
 * 
 * <pre>
 * ColorMap<Integer> palette = new DiscreteColorMap();
 * palette.add(1.5, Color.BLACK);
 * palette.add(0D, Color.GREEN);
 * palette.add(2., Color.RED);
 * </pre>
 * <p>
 * La carte est triée par ordre croissant des clés.
 * </p>
 * <p>
 * Lorsque la couleur n'est pas définie dans la carte, on la déduit en faisant
 * une interpolation linéaire entre les deux couleurs voisines (cf.
 * {@link ColorUtils#middleColor}). Dans le cas où la couleur dépasse la plage
 * de valeurs de la carte (i.e. un seul voisin), on récupère la couleur de cet
 * item voisin. Pour reprendre l'exemple précédent :
 * 
 * <pre>
 * palette.get(3.0) -> Color.RED
 * palette.get(-1.) -> Color.GREEN
 * palette.get(0.75) -> #080 (vert foncé)
 * </pre>
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 * @see {@link ColorUtils}
 */
public class ContinuousColorMap<T extends Number> implements ColorMap<T> {
	/** Plage des couleurs triée par ordre croissant des indices. */
	private final SortedMap<T, Color> colorSet = new TreeMap<>();

	@Override
	public void add(T key, Color value) {
		colorSet.put(key, value);
	}

	@Override
	public void delete(T key) {
		colorSet.remove(key);
	}

	@Override
	public Color get(T position) {
		if (colorSet.isEmpty())
			return null;

		if (position.intValue() < colorSet.firstKey().intValue())
			return colorSet.get(colorSet.firstKey());

		if (position.intValue() > colorSet.lastKey().intValue())
			return colorSet.get(colorSet.lastKey());

		if (colorSet.containsKey(position))
			return colorSet.get(position);

		List<T> keys = new ArrayList<>(colorSet.keySet());

		int a = 0; // a est la clé de la couleur au-dessus de celle demandée (After).
		for (T k : keys)
			if (k.intValue() > position.intValue()) {
				a = k.intValue();
				break;
			}

		Collections.reverse(keys);

		int b = colorSet.size() - 1; // b est pour la couleur d'en dessous (Before).
		for (T k : keys)
			if (k.intValue() < position.intValue()) {
				b = k.intValue();
				break;
			}

		Color before = colorSet.get(b), after = colorSet.get(a);
		double ratio = 1.0 * (position.intValue() - b) / (a - b);

		return ColorUtils.middleColor(before, after, ratio);
	}

	@Override
	public Color getOrDefault(T position, Color defColor) {
		Color color = this.get(position);

		return (color == null) ? defColor : color;
	}

	@Override
	public void clear() {
		colorSet.clear();
	}

	/**
	 * Construit une instance {@link ContinuousColorMap} à partir d'une chaîne de
	 * caractères (ou la valeur <code>null</code> si le <i>parsing</i> a échoué).
	 * <p>
	 * Les règles de syntaxe sont les suivantes :
	 * <ul>
	 * <li>Pour remplir la carte des couleurs, on insère des éléments de la façon
	 * suivante : <code>[N; COLOR]</code>.</li>
	 * </ul>
	 * <ul>
	 * <li>Les éléments de la carte sont espacés entre eux.<br/>
	 * Exemple : <code>[0.0; black] [1.5; rgb(42,21,68)] [2D; #0FF]</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param str Chaîne de caractères représentant une instance
	 *            {@link ContinuousColorMap}.
	 */
	public static final <T extends Number> ContinuousColorMap<T> fromString(String str, Class<T> type) {
		ContinuousColorMap<T> map = new ContinuousColorMap<>();

		String regex = "\\[[^\\]]*;[^\\[]*\\]"; // Extrait les chaînes de la forme "[xxx; xxx]"
		Matcher match = Pattern.compile(regex).matcher(str);
		while (match.find()) {
			String item = match.group();
			String inside = item.substring(1, item.length() - 1);
			String[] keyAndValue = inside.split(";\\s*");

			T key = parse(keyAndValue[0], type); // FIXME
			Color value = Color.web(keyAndValue[1]);
			map.add(key, value);
		}

		return map;
	}

	@Override
	public final String toString() {
		List<String> items = new ArrayList<>();
		for (Entry<T, Color> k : colorSet.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(k.getKey().toString()); // FIXED
			sb.append("; ");
			sb.append(k.getValue().toString());
			sb.append("]");

			items.add(sb.toString());
		}

		return String.join(" ", items);
	}

	/**
	 * Décode le nombre affiché dans la chaîne de caractères selon le type spécifié.
	 * 
	 * @param s    Chaîne de caractères représentant le nombre.
	 * @param type Classe de destination, héritée de {@link java.lang.Number}.
	 * @return Instance T du nombre.
	 * @throws NumberFormatException Lorsque la lecture du nombre est impossible.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parse(String s, Class<T> type) throws NumberFormatException {
		if (type == Integer.class)
			return (T) new Integer(Integer.parseInt(s));
		else if (type == Short.class)
			return (T) new Short(Short.parseShort(s));
		else if (type == Long.class)
			return (T) new Long(Long.parseLong(s));
		else if (type == Double.class)
			return (T) new Double(Double.parseDouble(s));
		else
			throw new NumberFormatException("Cannot parse '" + s + "' to any number.");
	}
}
