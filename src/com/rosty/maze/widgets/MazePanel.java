package com.rosty.maze.widgets;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.rosty.maze.Mazette;
import com.rosty.maze.model.Maze;
import com.rosty.maze.model.Maze.Side;
import com.rosty.maze.model.Maze.WallCoord;
import com.rosty.maze.model.MazeRoute;
import com.rosty.util.colormap.DiscreteColorMap;
import com.rosty.util.javafx.NodeWriter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Composant FXML permettant d'afficher un labyrinthe à l'écran, plus
 * précisément une instance de la classe {@link Maze}.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class MazePanel extends Pane {
	/* PROPRIÉTÉS JavaFX */

	private final ObjectProperty<Pos> alignmentProperty = new SimpleObjectProperty<>(Pos.TOP_LEFT);

	/** Propriété indiquant l'alignement du labyrinthe dans le composant FXML. */
	public final ObjectProperty<Pos> alignmentProperty() {
		return alignmentProperty;
	}

	/** Indique l'alignement du labyrinthe par rapport au composant FXML. */
	public final Pos getAlignment() {
		return alignmentProperty.get();
	}

	/**
	 * Définit l'alignement du labyrinthe par rapport au composant FXML.
	 * 
	 * @param value Enuméré localisant le labyrinthe par rapport au conteneur FXML.
	 */
	public final void setAlignment(Pos value) {
		alignmentProperty.set(value);
		update();
	}

	private final ObjectProperty<Maze> mazeProperty = new SimpleObjectProperty<>();

	/** Propriété correspondant au labyrinthe du modèle. */
	public final ObjectProperty<Maze> mazeProperty() {
		return mazeProperty;
	}

	/** Fournit le labyrinthe du modèle. */
	public final Maze getMaze() {
		return mazeProperty.get();
	}

	/** Définit le labyrinthe du modèle. */
	public final void setMaze(Maze value) {
		mazeProperty.set(value);
	}

	private final ObjectProperty<MazeRoute> routeProperty = new SimpleObjectProperty<>();

	/** Propriété correspondant au chemin tracé dans le labyrinthe. */
	public final ObjectProperty<MazeRoute> routeProperty() {
		return routeProperty;
	}

	/** Fournit le chemin du labyrinthe. */
	public final MazeRoute getRoute() {
		return routeProperty.get();
	}

	/** Définit le chemin du labyrinthe. */
	public final void setRoute(MazeRoute value) {
		routeProperty.set(value);
	}

	private final ObjectProperty<Spacing> spacingProperty = new SimpleObjectProperty<>();

	/** Propriété définissant l'émargement du labyrinthe dans le composant FXML. */
	public final ObjectProperty<Spacing> spacingProperty() {
		return spacingProperty;
	}

	/** Fournit l'émargement du labyrinthe (en pixels) dans le conteneur FXML. */
	public final Spacing getSpacing() {
		return spacingProperty.get();
	}

	/**
	 * Définit l'émargement du labyrinthe (en pixels) dans le conteneur FXML.
	 * 
	 * @param value Emargement en pixels. Un seul entier indique que l'émargement
	 *              est le même de chaque côté ; deux entiers indiquent un
	 *              émargement haut/bas et gauche/droite similaires ; quatre entiers
	 *              définissent l'émargement de gauche, du haut, de droite et du
	 *              bas.
	 */
	public final void setSpacing(Spacing value) {
		spacingProperty.set(value);
		setPadding(value);
	}

	private final ObjectProperty<Color> borderColorProperty = new SimpleObjectProperty<>(Color.RED);

	/** Propriété définissant la couleur des bordures délimitant le labyrinthe. */
	public final ObjectProperty<Color> borderColorProperty() {
		return borderColorProperty;
	}

	/** Fournit la couleur des bordures délimitant le labyrinthe. */
	public final Color getBorderColor() {
		return borderColorProperty.get();
	}

	/** Définit la couleur des bordures délimitant le labyrinthe. */
	public final void setBorderColor(Color value) {
		borderColorProperty.set(value);
		update();
	}

	private final ObjectProperty<Color> gridColorProperty = new SimpleObjectProperty<>(Color.BLUE);

	/**
	 * Propriété définissant la couleur du quadrillage du labyrinthe ; celui-ci est
	 * représenté sous la forme de points aux jonctions des cases.
	 */
	public final ObjectProperty<Color> gridColorProperty() {
		return gridColorProperty;
	}

	/** Fournit la couleur du quadrillage du labyrinthe. */
	public final Color getGridColor() {
		return gridColorProperty.get();
	}

	/** Définit la couleur du quadrillage du labyrinthe. */
	public final void setGridColor(Color value) {
		gridColorProperty.set(value);
		update();
	}

	private final ObjectProperty<Color> wallColorProperty = new SimpleObjectProperty<>(Color.web("#080"));

	/** Propriété définissant la couleur des murs du labyrinthe. */
	public final ObjectProperty<Color> wallColorProperty() {
		return wallColorProperty;
	}

	/** Fournit la couleur des murs du labyrinthe. */
	public final Color getWallColor() {
		return wallColorProperty.get();
	}

	/** Définit la couleur des murs du labyrinthe. */
	public final void setWallColor(Color value) {
		wallColorProperty.set(value);
		update();
	}

	private final ObjectProperty<DiscreteColorMap> blockColorMapProperty = new SimpleObjectProperty<>(
			new DiscreteColorMap());

	/** Propriété définissant la plage de couleurs des cases du labyrinthe. */
	public final ObjectProperty<DiscreteColorMap> blockColorMapProperty() {
		return blockColorMapProperty;
	}

	/** Fournit la plage de couleurs des cases du labyrinthe. */
	public final DiscreteColorMap getBlockColorMap() {
		return blockColorMapProperty.get();
	}

	/** Définit la plage de couleurs des cases du labyrinthe. */
	public final void setBlockColorMap(DiscreteColorMap value) {
		blockColorMapProperty.set(value);
		update();
	}

	/* VARIABLES */

	private Rectangle[][] blocks; // Cases du labyrinthe
	private Label[][] labels; // Valeurs des cases du labyrinthe
	private Line[][] vLines; // Murs verticaux du labyrinthe
	private Line[][] hLines; // Murs horizontaux du labyrinthe
	private Circle[][] corners; // Points placés dans les intersections des murs

	private Circle start, end;
	private Polyline path;

	private double W = 0; // Largeur du labyrinthe
	private double H = 0; // Hauteur du labytinthe
	private double deltaX = 0; // Ecartement du labyrinthe à gauche du conteneur graphique
	private double deltaY = 0; // Ecartement du labyrinthe en haut du conteneur graphique

	// Etalon de l'épaisseur des traits affichés à l'écran. Cette épaisseur est
	// proportionnelle aux dimensions du labyrinthe à l'écran.
	private double THICK = 1;

	/** Constructeur de la classe {@link MazePanel}. */
	public MazePanel() {
		initialize();
	}

	@FXML
	public void initialize() {
		mazeProperty.addListener(e -> update());
		routeProperty.addListener(e -> update());

		widthProperty().addListener(e -> update());
		heightProperty().addListener(e -> update());
	}

	/** Définit la taille du labyrinthe à partir de ses propriétés JavaFX. */
	private void defineSize() {
		double padX = getPadding().getLeft();
		double padY = getPadding().getTop();

		double width = getWidth() - padX - getPadding().getRight();
		double height = getHeight() - padY - getPadding().getBottom();

		double dX = 0;
		double dY = 0;

		double normW = width / getMaze().getNbColumns();
		double normH = height / getMaze().getNbRows();
		if (normW <= normH) {
			W = width;
			H = (width * getMaze().getNbRows()) / getMaze().getNbColumns();
			THICK = Math.min(normW / 5, 4);

			dY = height - H;
		} else {
			W = (height * getMaze().getNbColumns()) / getMaze().getNbRows();
			H = height;
			THICK = Math.min(normH / 5, 4);

			dX = width - W;
		}

		switch (getAlignment()) {
			case TOP_LEFT:
				deltaX = padX;
				deltaY = padY;
				break;
			case TOP_CENTER:
				deltaX = padX + 0.5 * dX;
				deltaY = padY;
				break;
			case TOP_RIGHT:
				deltaX = padX + dX;
				deltaY = padY;
				break;
			case CENTER_LEFT:
				deltaX = padX;
				deltaY = padY + 0.5 * dY;
				break;
			case CENTER:
				deltaX = padX + 0.5 * dX;
				deltaY = padY + 0.5 * dY;
				break;
			case CENTER_RIGHT:
				deltaX = padX + dX;
				deltaY = padY + 0.5 * dY;
				break;
			case BOTTOM_LEFT:
			case BASELINE_LEFT:
				deltaX = padX;
				deltaY = padY + dY;
				break;
			case BOTTOM_CENTER:
			case BASELINE_CENTER:
				deltaX = padX + 0.5 * dX;
				deltaY = padY + dY;
				break;
			case BOTTOM_RIGHT:
			case BASELINE_RIGHT:
				deltaX = padX + dX;
				deltaY = padY + dY;
				break;
			default:
				deltaX = 0;
				deltaY = 0;
				break;
		}
	}

	/** Génère les composants FXML représentant les bordures du labyrinthe. */
	protected void displayBorders() {
		Line up = new Line(deltaX, deltaY, deltaX + W, deltaY);
		Line right = new Line(deltaX + W, deltaY, deltaX + W, deltaY + H);
		Line down = new Line(deltaX + W, deltaY + H, deltaX, deltaY + H);
		Line left = new Line(deltaX, deltaY + H, deltaX, deltaY);

		List<Line> borders = Arrays.asList(up, down, left, right);
		borders.forEach(border -> {
			border.setStroke(getBorderColor());
			border.setStrokeWidth(3 * THICK);
			border.setStrokeLineCap(StrokeLineCap.ROUND);
		});

		getChildren().addAll(borders);
	}

	/**
	 * Génère les composants FXML représentant un quadrillage de l'ensemble du
	 * labyrinthe.
	 */
	protected void displayGrid() {
		int X = 2 * getMaze().getNbRows() + 1;
		for (int i = 0; i < X; i += 2) {
			int Y = 2 * getMaze().getNbColumns() + 1;
			for (int j = 0; j < Y; j += 2) {
				if (getMaze().get(i, j) == 1) {
					Circle point = new Circle(deltaX + (H * j) / (X - 1), deltaY + (W * i) / (Y - 1), 1.75 * THICK,
							getGridColor());
					corners[i / 2][j / 2] = point;
					getChildren().add(point);
				}
			}
		}
	}

	/** Génère les composants FXML représentant les murs du labyrinthe. */
	protected void displayWalls() {
		int X = 2 * getMaze().getNbRows() + 1;
		for (int i = 1; i < X - 1; i += 2) {
			int Y = 2 * getMaze().getNbColumns() + 1;
			for (int j = 0; j < Y; j += 2) {
				if (getMaze().get(i, j) == 1) {
					Line wall = new Line(deltaX + (H * j) / (X - 1), deltaY + (W * (i - 1)) / (Y - 1),
							deltaX + (H * j) / (X - 1), deltaY + (W * (i + 1)) / (Y - 1));
					wall.setStroke(getWallColor());
					wall.setStrokeWidth(2 * THICK);
					wall.setStrokeLineCap(StrokeLineCap.ROUND);
					vLines[(i - 1) / 2][j / 2] = wall;
					getChildren().add(wall);
				}
			}
		}

		for (int i = 0; i < X; i += 2) {
			int Y = 2 * getMaze().getNbColumns() + 1;
			for (int j = 1; j < Y - 1; j += 2) {
				if (getMaze().get(i, j) == 1) {
					Line wall = new Line(deltaX + (H * (j - 1)) / (X - 1), deltaY + (W * i) / (Y - 1),
							deltaX + (H * (j + 1)) / (X - 1), deltaY + (W * i) / (Y - 1));
					wall.setStroke(getWallColor());
					wall.setStrokeWidth(2 * THICK);
					wall.setStrokeLineCap(StrokeLineCap.ROUND);
					hLines[i / 2][(j - 1) / 2] = wall;
					getChildren().add(wall);
				}
			}
		}
	}

	/** Génère les composants FXML représentant les cases du labyrinthe. */
	protected void displayBlocks() {
		int X = 2 * getMaze().getNbRows() + 1;
		for (int i = 1; i < X - 1; i += 2) {
			int Y = 2 * getMaze().getNbColumns() + 1;
			for (int j = 1; j < Y - 1; j += 2) {
				if (getMaze().get(i, j) == 1) {
					Rectangle block = new Rectangle(deltaX + (H * (j - 1)) / (X - 1), deltaY + (W * (i - 1)) / (Y - 1),
							(2 * H) / (X - 1), (2 * W) / (Y - 1));
					block.setFill(getBlockColorMap().get(1));
					blocks[(i - 1) / 2][(j - 1) / 2] = block;
					getChildren().add(block);
				} else {
					Rectangle block = new Rectangle(deltaX + (H * (j - 1)) / (X - 1), deltaY + (W * (i - 1)) / (Y - 1),
							(2 * H) / (X - 1), (2 * W) / (Y - 1));
					block.setFill(Color.TRANSPARENT);
					blocks[(i - 1) / 2][(j - 1) / 2] = block;
					getChildren().add(block);
				}
			}
		}
	}

	/** Génère les labels affichant la valeur des cases du labyrinthe. */
	protected void displayLabels() {
		int X = 2 * getMaze().getNbRows() + 1;
		for (int i = 1; i < X - 1; i += 2) {
			int Y = 2 * getMaze().getNbColumns() + 1;
			for (int j = 1; j < Y - 1; j += 2) {
				Label lbl = new Label("" + getMaze().getCell((i - 1) / 2, (j - 1) / 2));
				lbl.setStyle("-fx-font-weight: bold");
				lbl.setLayoutX(deltaX + (H * (j - 1)) / (X - 1) + 3);
				lbl.setLayoutY(deltaY + (W * (i - 1)) / (Y - 1) + 3);
				labels[(i - 1) / 2][(j - 1) / 2] = lbl;
				getChildren().add(lbl);
			}
		}
	}

	protected void displayStart() {
		start = new Circle(2 * THICK, Color.FORESTGREEN);
		int is = getRoute().getStart()[0], js = getRoute().getStart()[1];
		start.setCenterX(deltaX + H * (js + 0.5) / getMaze().getNbRows());
		start.setCenterY(deltaY + W * (is + 0.5) / getMaze().getNbColumns());
		getChildren().add(start);
	}

	protected void displayEnd() {
		end = new Circle(2 * THICK, Color.RED);
		int ie = getRoute().getEnd()[0], je = getRoute().getEnd()[1];
		end.setCenterX(deltaX + H * (je + 0.5) / getMaze().getNbRows());
		end.setCenterY(deltaY + W * (ie + 0.5) / getMaze().getNbColumns());
		getChildren().add(end);
	}

	protected void displayPath() {
		path = new Polyline();
		path.setStroke(Color.ORANGERED);
		path.setStrokeWidth(1.5 * THICK);
		path.setStrokeLineJoin(StrokeLineJoin.ROUND);

		for (int[] pt : getRoute().getPath()) {
			double posX = deltaX + (pt[1] + 0.5) * (H / getMaze().getNbRows());
			double posY = deltaY + (pt[0] + 0.5) * (W / getMaze().getNbColumns());
			path.getPoints().addAll(posX, posY);
		}

		getRoute().getPath().addListener((ListChangeListener<int[]>) change -> {
			path.getPoints().clear();
			for (int[] pt : getRoute().getPath()) {
				double posX = deltaX + (pt[1] + 0.5) * (H / getMaze().getNbRows());
				double posY = deltaY + (pt[0] + 0.5) * (W / getMaze().getNbColumns());
				path.getPoints().addAll(posX, posY);
			}
		});

		getChildren().add(path);
	}

	/** Fournit la valeur de la cellule à la ligne et à la colonne indiquée. */
	public int getCell(int row, int col) {
		return getMaze().getCell(row, col);
	}

	/**
	 * Définit la valeur de la cellule à la ligne et à la colonne indiquée.
	 * 
	 * @param row   Numéro de ligne.
	 * @param col   Numéro de colonne.
	 * @param value Valeur de la case.
	 */
	public void setCell(int row, int col, int value) {
		getMaze().setCell(row, col, value);
//		blocks[row][col].setFill((value == 1) ? getBlockColor() : Color.TRANSPARENT);
		Color blockColor = getBlockColorMap().get(value);
		blocks[row][col].setFill(blockColor == null ? Color.TRANSPARENT : blockColor);

		if (Mazette.arg_fxDebug())
			labels[row][col].setText("" + value);
	}

	/**
	 * Fournit la valeur d'un mur en se référant à une case adjacente, où <b>-1</b>
	 * si ce mur n'est pas présent sur le terrain.
	 * 
	 * @param row  Numéro de ligne de la case adjacente.
	 * @param col  Numéro de colonne de la case adjacente.
	 * @param side Direction du mur par rapport à la case adjacente.
	 */
	public int getWall(int row, int col, Side side) {
		return getMaze().getWall(row, col, side);
	}

	/**
	 * Définit la valeur d'un mur du terrain en se référant à une case adjacente.
	 * 
	 * @param row   Numéro de ligne de la case adjacente.
	 * @param col   Numéro de colonne de la case adjacente.
	 * @param side  Direction du mur par rapport à la case adjacente.
	 * @param value Valeur associée au mur.
	 */
	public void setWall(int row, int col, Side side, int value) {
		getMaze().setWall(row, col, side, value);
		switch (side) {
			case UP:
				hLines[row][col].setStroke((value == 1) ? getWallColor() : Color.TRANSPARENT);
				break;
			case DOWN:
				hLines[row + 1][col].setStroke((value == 1) ? getWallColor() : Color.TRANSPARENT);
				break;
			case LEFT:
				vLines[row][col].setStroke((value == 1) ? getWallColor() : Color.TRANSPARENT);
				break;
			case RIGHT:
				vLines[row][col + 1].setStroke((value == 1) ? getWallColor() : Color.TRANSPARENT);
				break;
			default:
				break;
		}
	}

	public int[] getStart() {
		return getRoute().getStart();
	}

	public void setStart(int x, int y) {
		getRoute().setStart(x, y);

		start.setCenterX(deltaX + H * (y + 0.5) / getMaze().getNbRows());
		start.setCenterY(deltaY + W * (x + 0.5) / getMaze().getNbColumns());
	}

	public int[] getEnd() {
		return getRoute().getEnd();
	}

	public void setEnd(int x, int y) {
		getRoute().setEnd(x, y);

		end.setCenterX(deltaX + H * (y + 0.5) / getMaze().getNbRows());
		end.setCenterY(deltaY + W * (x + 0.5) / getMaze().getNbColumns());
	}

	public ObservableList<int[]> getPath() {
		return getRoute().getPath();
	}

	/**
	 * Récupère la valeur d'une case voisine à la case spécifiée.
	 * 
	 * @param wall Coordonnées du mur séparant les deux cases.
	 * @return Valeur de la case voisine.
	 */
	public int getNeighbourCell(WallCoord wall) {
		switch (wall.side) {
			case LEFT:
				return getMaze().getCell(wall.x, wall.y - 1);
			case RIGHT:
				return getMaze().getCell(wall.x, wall.y + 1);
			case UP:
				return getMaze().getCell(wall.x - 1, wall.y);
			case DOWN:
				return getMaze().getCell(wall.x + 1, wall.y);
			default:
				return -1;
		}
	}

	/** Affiche le labyrinthe sous forme textuelle. */
	public void display() {
		getMaze().display();
	}

	/** Efface les cases et réinitialise le labyrinthe */
	public void clear() {
		getMaze().clear();
		getRoute().getPath().clear();

		update();
	}

	/** Met à jour le labyrinthe dans composant graphique. */
	protected void update() {
		getChildren().clear();

		if (getMaze() != null) {
			int m = getMaze().getNbRows();
			int n = getMaze().getNbColumns();
			if (m != 0 || n != 0) {
				// Définition des éléments graphiques
				vLines = new Line[m][n + 1];
				hLines = new Line[m + 1][n];
				corners = new Circle[m + 1][n + 1];
				blocks = new Rectangle[m][n];
				if (Mazette.arg_fxDebug())
					labels = new Label[m][n];

				defineSize();
//				displayBorders();
				displayBlocks();
				displayWalls();
//				displayGrid();
				if (Mazette.arg_fxDebug())
					displayLabels();
			}

			if (getRoute() != null) {
				start = new Circle();
				end = new Circle();
				path = new Polyline();

				displayPath();
				displayStart();
				displayEnd();
			}
		}
	}

	/**
	 * Enregistre le labyrinthe sous la forme d'une image. Les seules extensions
	 * autorisées sont JPG, JPEG, PNG, TIFF, PDF et GIF.
	 * 
	 * @param file Fichier contenant l'image à enregistrer.
	 * @throws IOException En cas d'erreur lors de la création du fichier ou de la
	 *                     génération de l'image.
	 */
	public final void save(File file) throws IOException, IllegalArgumentException {
		NodeWriter.writeAsImage(this, file);
	}
}