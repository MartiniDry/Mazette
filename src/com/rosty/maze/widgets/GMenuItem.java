package com.rosty.maze.widgets;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Amélioration du composant FXML {@link MenuItem} permettant d'intégrer plus
 * simplement une icône en tête du contenu (<b>G</b> pour <i>"graphical menu
 * item"</i>).
 * 
 * <p>
 * <u>Syntaxe pour un <b>MenuItem</b></u>
 * </p>
 * <p style="font-size:11;">
 * {@code <MenuItem text="Commande">}<br/>
 * &emsp;{@code <graphic>}<br/>
 * &emsp;&emsp;{@code <ImageView>}<br/>
 * &emsp;&emsp;&emsp;{@code <Image url="@image.png"/>}<br/>
 * &emsp;&emsp;{@code </ImageView>}<br/>
 * &emsp;{@code </graphic>}<br/>
 * {@code </MenuItem>}<br/>
 * </p>
 * <p>
 * <u>Syntaxe pour un <b>GMenuItem</b></u>
 * </p>
 * <p style="font-size:11;">
 * {@code <GMenuItem text="Commande" icon="@image.png"/>}
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class GMenuItem extends MenuItem {
	private StringProperty iconProperty = new SimpleStringProperty();

	/** Propri�t� FXML d�finissant l'URL de l'image */
	public StringProperty iconProperty() {
		return iconProperty;
	}

	public String getIcon() {
		return iconProperty.get();
	}

	public void setIcon(String url) {
		iconProperty.set(url);
	}

	public GMenuItem() {
		setMnemonicParsing(false);

		// Rafra�chissement de l'image lors du changement de l'URL
		iconProperty.addListener((bean_p, old_p, new_p) -> {
			setGraphic((new_p != null) ? new ImageView(new Image(new_p)) : null);
		});
	}
	
	public GMenuItem(String text) {
		this();
		setText(text);
	}
}