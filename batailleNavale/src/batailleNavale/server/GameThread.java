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
			
			// Phase de placement (thread)
			BoatPlacingThread bst1 = new BoatPlacingThread(in1, out1, game, 1);
			BoatPlacingThread bst2 = new BoatPlacingThread(in2, out2, game, 2);
			bst1.start();
			bst2.start();
			while(bst1.isAlive() | bst2.isAlive()) {} // execution bloquée tant que les 2 joueurs n'ont pas placé tous leurs bateaux
			
			// Phase de tir
			do {	
				printBoardPlayer(game.getActivePlayer(), false);
				
				if(game.getActivePlayer() == 1) {
					shootingPhase(in1, out1, 1, 2, out2);
					game.setActivePlayer(2);
				}else {
					shootingPhase(in2, out2, 2, 1, out1);
					game.setActivePlayer(1);
				}
			}while(!game.isGameEnded());
			
			//Fin de partie
			endMessage();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Affiche le tableau de jeu global pour le joueur passé en argument.
	 * @param player
	 */
	private void printBoardPlayer(int player, boolean isFirstDisplay) {
		if(player == 1) {
			out1.println(game.getPlayersBoards(isFirstDisplay)[0]);
		}
		else {
			out2.println(game.getPlayersBoards(isFirstDisplay)[1]);
		}
	}
	
	/**
	 * Retourne true si le tir est validé. Effectue le tir entre le joueur shooter et le joueur target. Retourne false sinon.
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isValidShoot
	 */
	private boolean shoot(int target, int shooter, String coord) { 
		if(!Utils.isValidCoord(coord)) { // coordonnée en dehors de la grille de jeu
			display(shooter, "Veuillez choisir une case existante...");
			return false;
		}
		else if(!game.checkShoot(shooter, coord)) { // tir invalide (case bateau touché ou case bateau touché)
			display(shooter, "Veuillez choisir une autre case, vous avez déjà tiré sur cette dernière...");
			return false;
		}else { // tir valide
			if(game.isShot(target, coord)) { // touché
				display(shooter, "Adversaire touché : " + coord);
				display(target, "Vous êtes touché : " + coord);
				game.shotResult(shooter, coord, true);
				return true;
			}else{ // loupé
				display(shooter, "Tir loupé : " + coord);
				display(target, "L'adversaire à loupé son tir : " + coord);
				game.shotResult(shooter, coord, true);
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
		shooterOut.println("Case cible : ");
		targetOut.println("L'adversaire est en train de tirer...");
		String targetedCell;
		try {
			do {
				targetedCell = shooterIn.readLine();
				while(!Utils.isValidCoord(targetedCell)) {
					shooterOut.println("Cette case n'existe pas ou a déjà été ciblée.... Entrer une nouvelle cible : ");
					targetedCell = shooterIn.readLine();
				}
			}while(!shoot(target, shooter, targetedCell));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 * Affiche les textes de fin de partie.
	 */
	private void endMessage() {
		if(game.getActivePlayer() == 1) { // Joueur 2 gagnant
			out1.println("Vous avez perdu ...");
			out2.println("Vous avez gagné !");
		}else { // Joueur 1 gagnant
			out1.println("Vous avez gagné !");
			out2.println("Vous avez perdu ...");
		}
	}
}
