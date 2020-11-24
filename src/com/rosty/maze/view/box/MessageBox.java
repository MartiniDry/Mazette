package com.rosty.maze.view.box;

import java.util.Arrays;

import com.rosty.maze.application.AppLauncher;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

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

	private final GaussianBlur ownerBlurEffect = new GaussianBlur(0);
	private final ColorAdjust ownerDesaturationEffect = new ColorAdjust(0, 0, 0, 0);
	
	FadeTransition boxFadeTransition = new FadeTransition();
	ScaleTransition boxScaleTransition = new ScaleTransition();
	
	/* CONSTANTES */
	
	private static Duration APPEARANCE_DURATION = Duration.millis(250);
	private static Duration DISAPPEARANCE_DURATION = Duration.millis(100);

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

			ownerBlurEffect.setRadius(0);
			ownerDesaturationEffect.setSaturation(0);
			ownerDesaturationEffect.setBrightness(0);

			ownerBlurEffect.setInput(ownerDesaturationEffect);
			owner.getScene().getRoot().setEffect(ownerBlurEffect);

			Parent boxRoot = getDialogPane().getScene().getRoot();
			boxRoot.setOpacity(0);
			boxRoot.setScaleX(0.9);
			boxRoot.setScaleY(0.9);
			
			boxFadeTransition.setNode(boxRoot);
			boxScaleTransition.setNode(boxRoot);
			
			showingProperty().addListener((bean_p, old_p, new_p) -> {
				if (new_p != null)
					if (new_p) {
						Platform.runLater(() -> {
							Timeline saturationTimeline = new Timeline();
							saturationTimeline.getKeyFrames()
									.add(new KeyFrame(APPEARANCE_DURATION,
											new KeyValue(ownerBlurEffect.radiusProperty(), 5),
											new KeyValue(ownerDesaturationEffect.saturationProperty(), -0.4),
											new KeyValue(ownerDesaturationEffect.brightnessProperty(), -0.4)));
							saturationTimeline.play();

							boxFadeTransition.setDuration(APPEARANCE_DURATION);
							boxFadeTransition.setFromValue(boxRoot.getOpacity());
							boxFadeTransition.setToValue(1);
							boxFadeTransition.play();
							
							boxScaleTransition.setDuration(APPEARANCE_DURATION);
							boxScaleTransition.setFromX(boxRoot.getScaleX());
							boxScaleTransition.setFromY(boxRoot.getScaleY());
							boxScaleTransition.setToX(1);
							boxScaleTransition.setToY(1);
							boxScaleTransition.play();
						});
					} else {
						Platform.runLater(() -> {
							Timeline saturationTimeline = new Timeline();
							saturationTimeline.getKeyFrames()
									.add(new KeyFrame(DISAPPEARANCE_DURATION,
											new KeyValue(ownerBlurEffect.radiusProperty(), 0),
											new KeyValue(ownerDesaturationEffect.saturationProperty(), 0),
											new KeyValue(ownerDesaturationEffect.brightnessProperty(), 0)));
							saturationTimeline.play();
//
//							boxFadeTransition.setDuration(DISAPPEARANCE_DURATION);
//							boxFadeTransition.setFromValue(boxRoot.getOpacity());
//							boxFadeTransition.setToValue(0);
//							boxFadeTransition.play();
//							
//							boxScaleTransition.setDuration(DISAPPEARANCE_DURATION);
//							boxScaleTransition.setFromX(boxRoot.getScaleX());
//							boxScaleTransition.setFromY(boxRoot.getScaleY());
//							boxScaleTransition.setToX(0.9);
//							boxScaleTransition.setToY(0.9);
//							boxScaleTransition.play();
						});
					}
			});
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

	/** Applique une nouvelle feuille de style aux boîtes de dialogue. */
	public static void setStyleSheet(String ss) {
		styleSheet = ss;
	}

	/** Récupère le nom de la feuille de style des boîtes de dialogue. */
	public static String getStyleSheet() {
		return styleSheet;
	}
}