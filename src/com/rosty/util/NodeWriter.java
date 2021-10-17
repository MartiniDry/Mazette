package com.rosty.util;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

/**
 * Classe utilitaire pour la sauvegarde des composants JavaFX.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class NodeWriter {
	/**
	 * Fait la capture d'un composant JavaFX sous forme d'une image. Les extensions
	 * autorisées sont <b>JPG</b>, <b>JPEG</b>, <b>GIF</b> et <b>PNG</b>.
	 * 
	 * @param component Composant graphique.
	 * @param file      Fichier de sauvegarde ; ce fichier sera automatiquement créé
	 *                  s'il n'existe pas.
	 * @throws IOException              En cas de problème lors de la création ou de
	 *                                  l'écriture du fichier de sauvegarde.
	 * @throws IllegalArgumentException En cas de problème lors de la capture de
	 *                                  l'image.
	 */
	public static void writeAsImage(Node component, File file) throws IOException, IllegalArgumentException {
		SnapshotParameters param = new SnapshotParameters();
		// Avant d'enregistrer l'image, il faut gérer la mise à l'échelle pour que le
		// labyrinthe soit le plus net possible.
		param.setTransform(new Scale(8, 8));
		param.setFill(Color.TRANSPARENT);

		// En cas d'image trop lourde à calculer, une IllegalArgumentException est
		// lancée.
		writeAsImage(component, file, param);
	}

	/**
	 * Fait la capture d'un composant JavaFX sous forme d'une image. Les extensions
	 * autorisées sont <b>JPG</b>, <b>JPEG</b>, <b>GIF</b> et <b>PNG</b>. Le calcul
	 * de l'image peut être paramétré par une instance {@link SnapshotParameters}.
	 * 
	 * @param component Composant graphique.
	 * @param file      Fichier de sauvegarde ; ce fichier sera automatiquement créé
	 *                  s'il n'existe pas.
	 * @param snap      Outil de paramétrage de la capture.
	 * @throws IOException              En cas de problème lors de la création ou de
	 *                                  l'écriture du fichier de sauvegarde.
	 * @throws IllegalArgumentException En cas de problème lors de la capture de
	 *                                  l'image.
	 */
	public static void writeAsImage(Node component, File file, SnapshotParameters snap)
			throws IOException, IllegalArgumentException {
		WritableImage snapshot = component.snapshot(snap, null);

		if (file != null && !file.getName().isEmpty()) {
			String[] words = file.getName().split("\\.");
			String fileExtension = words[words.length - 1].toLowerCase();
			switch (fileExtension) {
				case "jpg":
				case "jpeg":
				case "png":
				case "gif":
					if (!file.exists())
						file.createNewFile();

					ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), fileExtension, file);
					break;
				default:
					break;
			}
		}
	}
}