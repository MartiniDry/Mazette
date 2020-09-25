package com.rosty.maze.view.box;

import java.util.Arrays;

import com.rosty.maze.application.AppLauncher;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Classe définissant les boîtes de dialogue du logiciel. Cette classe surcharge
 * la classe {@link Alert}. Une feuille de style peut être appliquée à la
 * fenêtre pour personnaliser l'affichage graphique.
 * 
 * @author X2020636 (Martin ROSTAGNAT)
 */
public class MessageBox extends Alert {
	/* ATTRIBUTS */

	protected static String styleSheet = "";

	/**
	 * Constructeur de la classe {@link MessageBox}.
	 * 
	 * @param alertType  Type de message applicatif.
	 * @param owner      Fenêtre propriétaire.
	 * @param headerText Thème du message applicatif.
	 * @param buttons    Boutons d'action de la fenêtre.
	 */
	public MessageBox(AlertType alertType, Window owner, String headerText, ButtonType... buttons) {
		super(alertType);

		initDialog(owner, alertType);
		setHeaderText(headerText);
		if (buttons != null && Arrays.asList(buttons).size() != 0)
			getButtonTypes().setAll(buttons);
	}

	/**
	 * Constructeur de la classe {@link MessageBox}. Ce constructeur est uniquement
	 * appelé dans les méthodes initConfiguration() et initDecoders() de la classe
	 * {@link AppLauncher}, i.e. avant la chargement de l'IHM et le lancement du
	 * thread JavaFX. Il corrige le problème posé par le constructeur précédent ; en
	 * cas de problème sur le checksum, une instance MessageBox était créée alors
	 * que AppLauncher.getPrimaryStage() n'existait pas encore, ce qui levait une
	 * RuntimeException et bloquait l'affichage de la fenêtre. Avec ce constructeur
	 * on n'appelle jamais la fenêtre principale et on affiche correctement la
	 * fenêtre de dialogue à l'écran.
	 * 
	 * @param alertType  Type de message applicatif.
	 * @param owner      Fenêtre propriétaire.
	 * @param headerText Thème du message applicatif.
	 */
	public MessageBox(AlertType alertType, Window owner, String headerText) {
		this(alertType, owner, headerText, (ButtonType[]) null);
	}

	/**
	 * Constructeur de la classe {@link MessageBox}.
	 * 
	 * @param alertType  Type de message applicatif.
	 * @param headerText Thème du message applicatif.
	 * @param buttons    Boutons d'action de la fenêtre.
	 */
	public MessageBox(AlertType alertType, String headerText, ButtonType... buttons) {
		this(alertType, AppLauncher.getPrimaryStage(), headerText, buttons);
	}

	/**
	 * Constructeur de la classe {@link MessageBox}.
	 * 
	 * @param alertType  Type de message applicatif.
	 * @param headerText Thème du message applicatif.
	 */
	public MessageBox(AlertType alertType, String headerText) {
		this(alertType, AppLauncher.getPrimaryStage(), headerText, (ButtonType[]) null);
	}

	/**
	 * Constructeur de la classe {@link MessageBox}.
	 * 
	 * @param alertType Type de message applicatif.
	 */
	public MessageBox(AlertType alertType) {
		this(alertType, AppLauncher.getPrimaryStage(), null, (ButtonType[]) null);
	}

	/**
	 * Initialise les paramètres généraux de la fenêtre.
	 * 
	 * @param owner     Fenêtre propriétaire de la boîte de dialogue.
	 * @param alertType Type de message à afficher.
	 */
	private void initDialog(Window owner, AlertType alertType) {
		if (owner != null) {
			initOwner(owner);
			initStyle(StageStyle.TRANSPARENT);
		}

		switch (alertType) {
		case CONFIRMATION:
			getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
			break;
		default:
			break;
		}

		getDialogPane().getScene().setFill(Color.TRANSPARENT);
		getDialogPane().getStylesheets().add(styleSheet);

		// Ajout du raccourci ECHAP pour fermer la boîte de dialogue.
		getDialogPane().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::close);
	}

	/** Applique une nouvelle feuille de style à la boîte de dialogue. */
	public static void setStyleSheet(String ss) {
		styleSheet = ss;
	}
}