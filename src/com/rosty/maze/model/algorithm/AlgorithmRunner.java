package com.rosty.maze.model.algorithm;

import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

import com.rosty.maze.widgets.Chrono;
import com.rosty.maze.widgets.Chrono.ObsChronoState;

/**
 * Classe permettant l'exécution d'un algorithme, soit en un coup, soit pas à
 * pas.
 * <ul>
 * <li><u>Exécution directe :</u> l'algorithme est exécuté dans un thread
 * spécifique via une action de l'utilisateur. Un <b>laps de temps</b> peut être
 * paramétré entre deux itérations de l'algorithme.</li>
 * <li><u>Exécution pas-à-pas :</u> l'utilisateur peut lancer l'algorithme une
 * étape à la fois, jusqu'à atteindre le critère d'arrêt.</li>
 * </ul>
 * 
 * <p>
 * Lors d'une exécution directe, il est également possible de mettre
 * l'algorithme en pause puis de le relancer. Il est également possible, en
 * plein fonctionnement, de passer d'une exécution directe à une exécution
 * pas-à-pas (et vice-versa).
 * </p>
 * 
 * @author Martin Rostagnat
 * @version 1.0
 * @see Algorithm
 */
public class AlgorithmRunner extends Observable implements Observer {
	/** Algorithme qui va être exécuté. */
	private Algorithm algorithm;

	/** Chronomètre utilisé lors de l'exécution directe d'un algorithme. */
	private Chrono timer;

	/** Durée entre deux itérations de l'algorithme (en microsecondes). */
	private long timeout = 0L;

	/** Fonction réalisant une exécution directe de l'algorithme. */
	private final Runnable directExecution = () -> {
		try {
			timer.start();

			while (!algorithm.isComplete())
				nextStep();

			timer.stop();
			double elapsedTime = timer.getTime().getSeconds() + timer.getTime().getNano() / 1e9D;
			System.out.println("Fini ! Temps écoulé : " + elapsedTime + " secondes");
		} catch (InterruptedException e) {
			// Ne rien faire
		}
	};

	private Thread thread; // Thread de lancement d'un algorithme
	private boolean begun = false; // Booléen indiquant si l'algorithme a été lancé i.e. si l'étape à exécuter est
									// la première étape

	/** Constructeur de la classe {@link AlgorithmRunner}. */
	public AlgorithmRunner() {
		thread = newThread();
		timer = new Chrono();
		timer.addObserver(this);
	}

	/**
	 * Constructeur de la classe {@link AlgorithmRunner}.
	 * 
	 * @param algo Algorithme à exécuter.
	 */
	public AlgorithmRunner(Algorithm algo) {
		this();
		algorithm = algo;
	}

	/** Fournit l'algorithme qui doit être exécuté. */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	/** Définit l'algorithme à exécuter. */
	public void setAlgorithm(Algorithm value) {
		algorithm = value;

		setChanged();
		notifyObservers(ObsRunnerState.ALGORITHM);
	}

	/**
	 * Fournit le laps de temps (en millisecondes) entre deux itérations de
	 * l'algorithme.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Définit le laps de temps (en <b>ms</b>) entre deux itérations de
	 * l'algorithme.
	 */
	public void setTimeout(long value) {
		timeout = value;

		setChanged();
		notifyObservers(ObsRunnerState.TIMEOUT);
	}

	/** Fournit la durée enregistrée par le chronomètre. */
	public Duration getTime() {
		return timer.getTime();
	}

	/**
	 * Lance la totalité de l'algorithme courant en un seul coup. Chaque étape de
	 * l'algorithme est exécutée avec une période définie par l'utilisateur.
	 * 
	 * @throws InterruptedException En cas d'interruption du <i>thread</i>
	 *                              d'exécution de l'algorithme.
	 */
	public void start() throws InterruptedException {
		if (thread != null) {
			thread.interrupt();
			if (!thread.isAlive()) {
				thread = newThread();
				if (algorithm != null)
					thread.start();
			} else
				System.out.println("Veuillez sélectionner un algorithme.");
		} else
			System.out.println("Le thread courant n'est pas défini.");
	}

	/**
	 * Met en pause l'algorithme dans le cas d'une exécution directe.
	 * 
	 * @throws InterruptedException En cas d'interruption du <i>thread</i>
	 *                              d'exécution de l'algorithme.
	 */
	public void stop() throws InterruptedException {
		if (thread != null) {
			timer.stop();
			thread.interrupt();
		} else
			System.out.println("Le thread courant n'est pas défini.");
	}

	/**
	 * Dans le cas d'un lancement pas-à-pas, exécute l'étape suivante de
	 * l'algorithme.
	 * 
	 * @throws InterruptedException En cas d'interruption du <i>thread</i>
	 *                              d'exécution de l'algorithme.
	 */
	public void step() throws InterruptedException {
		if (algorithm != null) {
			nextStep();
			if (algorithm.isComplete())
				System.out.println("Fini !");
		}
	}

	/**
	 * Exécute l'étape suivante de l'algorithme courant. Dans le cas où l'algorithme
	 * est terminé, la méthode n'exécute rien.
	 * 
	 * @throws InterruptedException En cas d'interruption du thread d'exécution de
	 *                              l'algorithme.
	 */
	private void nextStep() throws InterruptedException {
		if (!begun)
			if (algorithm.isComplete())
				return;
			else {
				algorithm.step();
				begun = true;
			}
		else if (algorithm.isComplete())
			return;
		else {
			algorithm.step();
			
			int timeoutMs = (int) timeout / 1000;
			int timeoutNs = ((int) timeout % 1000) * 1000;
			Thread.sleep(timeoutMs, timeoutNs); // Pause du thread à la microseconde près
		}
	}

	/**
	 * Renouvelle le <i>thread</i> permettant de lancer l'algorithme de manière
	 * directe.
	 */
	private Thread newThread() {
		Thread th = new Thread(directExecution);
		th.setName("Lanceur d'algorithme");

		return th;
	}

	/** Réinitialise l'algorithme et remet le chronomètre à zéro. */
	public void reset() {
		timer.reset();
		if (algorithm != null)
			algorithm.init();
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((ObsChronoState) arg) {
		case STARTED:
			setChanged();
			notifyObservers(ObsRunnerState.STARTED);

			break;
		case UPDATED:
			setChanged();
			notifyObservers(ObsRunnerState.UPDATED);

			break;
		case STOPPED:
			setChanged();
			notifyObservers(ObsRunnerState.STOPPED);

			break;
		case RESET:
			setChanged();
			notifyObservers(ObsRunnerState.RESET);

			break;
		case NEW:
		default:
			break;
		}
	}

	/**
	 * Enumération des états possibles du lanceur d'algorithme.
	 * 
	 * @author Martin Rostagnat
	 * @version 1.0
	 */
	public enum ObsRunnerState {
		/** Définition de l'algorithme à exécuter. */
		ALGORITHM,
		/** Définition de la durée entre deux étapes de l'algorithme. */
		TIMEOUT,
		/** Lancement du chronomètre. */
		STARTED,
		/** Mise à jour du temps mesuré. */
		UPDATED,
		/** Arrêt du chronomètre. */
		STOPPED,
		/** Remise à zéro du chronomètre. */
		RESET;
	}
}