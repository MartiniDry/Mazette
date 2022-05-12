package com.rosty.maze.widgets;

import java.lang.Thread.State;
import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

/**
 * Classe définissant un chronomètre pour le logiciel. Ce chronomètre est
 * exécuté sur un <i>thread</i> spécifique et renvoie une valeur de temps à la
 * nanoseconde près (10<sup>-9</sup> s). La particularité de cette classe est
 * qu'elle fournit le temps écoulé sous forme d'une instance observable
 * {@link Duration}.
 * 
 * @author Martin Rostagnat
 * @version 1.0
 */
public class Chrono extends Observable {
	/* ATTRIBUTS */

	/**
	 * Durée enregistrée par le chronomètre sous forme d'une instance
	 * {@link Duration}.
	 */
	private Duration time;

	private long tic = 0L; // Instant de démarrage du chronomètre
	private long toc = 0L; // Instant d'arrêt du chronomètre

	private long accumulator = 0L; // Accumulateur de temps lorsque le chronomètre est en pause

	private boolean launched = false; // Booléen localisant la phase de lancement du chronomètre
	private volatile boolean elapsing = false; // Booléen indiquant si le chronomètre est lancé
	long updatePeriod = 16L; // Période de mise à jour de la durée

	private Thread thread; // Thread appliqué au calcul de la durée
	private final Runnable timeUpdater = () -> {
		while (true)
			if (elapsing) {
				toc = System.nanoTime();

				if (launched) {
					setTime(Duration.ofNanos(toc - tic + accumulator), ObsChronoState.STARTED);
					launched = false;
				} else
					setTime(Duration.ofNanos(toc - tic + accumulator), ObsChronoState.UPDATED);

				try {
					Thread.sleep(updatePeriod);
				} catch (InterruptedException e) {
					// Aucune erreur à relever ; il est possible d'interrompre le chronomètre avant
					// la fin de la phase d'attente.
				}
			}
	};

	/** Constructeur de la classe {@link Chrono}. */
	public Chrono() {
		setTime(Duration.ZERO, ObsChronoState.NEW);
		newThread();
	}

	/**
	 * Constructeur de la classe {@link Chrono}.
	 * 
	 * @param period Période de mise à jour du chronomètre (en millisecondes).
	 */
	public Chrono(long period) {
		setTime(Duration.ZERO, ObsChronoState.NEW);
		newThread();

		this.updatePeriod = period;
	}

	/**
	 * Fournit la durée enregistrée par le chronomètre sous forme d'une instance
	 * {@link Duration}.
	 */
	public final Duration getTime() {
		return time;
	}

	/**
	 * Assigne une nouvelle valeur de temps au chronomètre et notifie le changement.
	 * 
	 * @param value Nouvelle valeur de temps du chronomètre.
	 * @param state Etat du chronomètre que représente ce changement de valeur.
	 */
	private final void setTime(Duration value, ObsChronoState state) {
		time = value;

		setChanged();
		notifyObservers(state);
	}

	/**
	 * Fournit la période de rafraîchissement de la durée enregistrée par le
	 * chronomètre (en millisecondes).
	 */
	public long getUpdatePeriod() {
		return updatePeriod;
	}

	/** Lance le chronomètre (la durée n'est pas remise à zéro avant lancement). */
	public void start() {
		tic = System.nanoTime();
		elapsing = true;

		if (thread != null && thread.getState() == State.NEW) {
			launched = true;
			thread.start();
		}
	}

	/** Arrête le chronomètre. */
	public void stop() {
		toc = System.nanoTime();
		elapsing = false;
		launched = false;

		accumulator += (toc - tic);
		setTime(Duration.ofNanos(accumulator), ObsChronoState.STOPPED);
	}

	/**
	 * Remet le chronomètre à zéro.
	 * <p>
	 * <b>Attention :</b> cette méthode se stoppe pas le chronomètre si celui-ci est
	 * en marche.
	 * </p>
	 */
	public void reset() {
		accumulator = 0L;
		tic = toc = System.nanoTime();
		setTime(Duration.ofNanos(accumulator), ObsChronoState.RESET);

		thread.interrupt();
		newThread();
	}

	/** Indique si le chronomètre est actuellement lancé. */
	public boolean isElapsing() {
		return thread != null && thread.isAlive();
	}

	/** Rédéfinit le <i>thread</i> de lancement */
	private void newThread() {
		thread = new Thread(timeUpdater);
		thread.setName("Chronomètre");

		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
		setTime(Duration.ZERO, ObsChronoState.UPDATED);
	}

	/**
	 * Enumération des divers états de fonctionnement du chronomètre.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	public enum ObsChronoState {
		/** Création du chronomètre. */
		NEW,

		/** Lancement du chronomètre. */
		STARTED,

		/** Mise à jour de la valeur de temps. */
		UPDATED,

		/** Arrêt du chronomètre. */
		STOPPED,

		/** Mise à zéro du chronomètre. */
		RESET;
	}
}