package batailleNavale.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameThread extends Thread{
	private BufferedReader in1;
	private PrintWriter out1;
	private BufferedReader in2;
	private PrintWriter out2;
	private Game game = new Game();
	
	public GameThread(Socket client1, Socket client2){
		super();
		try {
			this.in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
			this.out1 = new PrintWriter(client1.getOutputStream(), true);
			this.in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
			this.out2 = new PrintWriter(client2.getOutputStream(), true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void run() {
		try {
			out1.println("----- Début de la partie -----\nVous êtes le joueur 1");
			out2.println("----- Début de la partie -----\nVous êtes le joueur 2");
			
			// Phase de placement (thread pour chaque joueur)
			BoatPlacingThread bst1 = new BoatPlacingThread(in1, out1, game, 1);
			BoatPlacingThread bst2 = new BoatPlacingThread(in2, out2, game, 2);
			bst1.start();
			bst2.start();
			while(bst1.isAlive() | bst2.isAlive()) {} // execution bloquée tant que les 2 joueurs n'ont pas placé tous leurs bateaux
			
			// Phase de tir
			do {	
				printBoardPlayer(game.isFirstPlayerActive(), false);
				
				if(game.isFirstPlayerActive()) {
					shootingPhase(in1, out1, 1, 2, out2);
					game.setFirstPlayerActive(false);
				}else {
					shootingPhase(in2, out2, 2, 1, out1);
					game.setFirstPlayerActive(true);
				}
			}while(!game.isGameEnded());
			
			//Fin de partie
			endMessage();
			
		} catch (Exception e) {
			out1.println("Connexion perdue...");
		}
	}
	
	/**
	 * Affiche le tableau de jeu global pour le joueur passé en argument.
	 * @param player
	 */
	private void printBoardPlayer(boolean isFirstPlayer, boolean isFirstDisplay) {
		if(isFirstPlayer) out1.println(game.getPlayersBoards(isFirstDisplay)[0]);
		else out2.println(game.getPlayersBoards(isFirstDisplay)[1]);
	}
	
	/**
	 * Retourne true si le tir est validé. Effectue le tir entre le joueur shooter et le joueur target. Retourne false sinon.
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isValidShoot
	 */
	private boolean isValidShot(int target, int shooter, String coord) { 
		if(!Utils.isValidCoord(coord)) { // si coordonnée en dehors de la grille de jeu
			display(shooter, "Veuillez choisir une case existante...");
			return false;
		}
		else if(!game.isAlreadyShot(shooter, coord)) { // si tir invalide (case déjà ciblée)
			display(shooter, "Veuillez choisir une autre case, vous avez déjà tiré sur cette dernière...");
			return false;
		}else { // tir valide
			if(game.isShot(target, coord)) { // touché
				display(shooter, "Adversaire touché : " + coord);
				display(target, "Vous êtes touché : " + coord);
				game.shotResult(shooter, coord, true);
				
				String sunkBoatName = game.isSunk(target, coord);
				if(sunkBoatName != null) { // coulé
					display(shooter, sunkBoatName + " coulé !");
					display(target, "Votre "+ sunkBoatName + " est coulé...");
				}
				return true;
			}else{ // loupé
				display(shooter, "Tir loupé : " + coord);
				display(target, "L'adversaire à loupé son tir : " + coord);
				game.shotResult(shooter, coord, false);
				return true;
			}
		}
	}
	
	/**
	 * Effectue la phase de tir pour un joueur dont le BufferedReader et le PrintWriter sont en paramètres.
	 * @param shooterIn
	 * @param shooterOut
	 * @param target
	 * @param shooter
	 */
	private void shootingPhase(BufferedReader shooterIn, PrintWriter shooterOut, int shooter, int target, PrintWriter targetOut) {
		shooterOut.println("\n--- Votre tour ---\nCase cible : ");
		targetOut.println("\n--- Tour adverse ---\nL'adversaire est en train de tirer...");
		String targetedCell;
		try {
			do {
				targetedCell = shooterIn.readLine();
				while(!Utils.isValidCoord(targetedCell)) {
					shooterOut.println("Cette case n'existe pas ou a déjà été ciblée.... Entrer une nouvelle cible : ");
					targetedCell = shooterIn.readLine();
				}
			}while(!isValidShot(target, shooter, targetedCell));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Ecrit sur le PrintWriter du joueur en paremètre.
	 * @param player
	 * @param message
	 */
	private void display(int player, String message) {
		if(player == 1) out1.println(message);
		else out2.println(message);
	}
	
	/**
	 * Affiche les textes de fin de partie et envoie l'information de fin de partie au ListeningThread du client..
	 */
	private void endMessage() {
		if(game.isFirstPlayerActive()) { // Joueur 2 gagnant
			out1.println("Vous avez perdu ...");
			out2.println("Vous avez gagné !");
		}else { // Joueur 1 gagnant
			out1.println("Vous avez gagné !");
			out2.println("Vous avez perdu ...");
		}
		
		//Fermeture de la partie
		out1.println("end");
		out2.println("end");
	}
}
