package batailleNavale.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BoatPlacingThread extends Thread{

	private BufferedReader in;
	private PrintWriter out;
	private Game game;
	private int player;
	private int direction;
	private String origin;
	
	public BoatPlacingThread(BufferedReader in, PrintWriter out, Game game, int player) {
		try {
			this.in = in;
			this.out = out;
			this.game = game;
			this.player = player;
			
		}catch (Exception e) {
			
		}
	}
	
	public void run() {
		try {
			do { // Placement des bateaux
				boatToPlaceDisplay();
				
				//Choix du bateau
				out.println("\nNuméro du bateau : ");
				String boatIndexStr = in.readLine();
				
				while(!Utils.isNumeric(boatIndexStr) || !game.unPlacedBoats(player).containsKey(Integer.parseInt(boatIndexStr))) {	// n'est pas un entier ou le numéro entrée ne correspond pas à un bateau non placé
					out.println("Entrer un bon numéro : ");
					boatIndexStr = in.readLine();
				}
				int boatIndex = Integer.parseInt(boatIndexStr);
				out.println(game.unPlacedBoats(player).get(boatIndex).getName() + " selectionné.");
				
				//Case et direction
				boatPlacing();
				while(!setBoat(player, boatIndex, direction, origin)) {
					out.println("Mauvais placement, veuillez recommencer...");
					boatPlacing();
				}
				game.unPlacedBoats(player).get(boatIndex).setPlaced(true);
				
				
			}while(game.unPlacedBoats(player).size() != 0);
			out.println("Tous les bateaux sont placés. En attente de l'adversaire...");
		}catch (Exception e) {
			// todo
		}
	}
	
	private void boatPlacing() {
		try {
			// Case d'origine
			out.println("Case d'origine : ");
			origin = in.readLine();
			while(!Utils.isValidCoord(origin)) {
				out.println("Cette case n'existe pas.... Entrer une nouvelle case d'origine : ");
				origin = in.readLine();
			}
		
			//Direction
			out.println("Direction (H => horizontal | V => vertical) : ");
			String directionStr = in.readLine();
			while(!Utils.isNumeric(directionStr)) {
				out.println("Entrer H (horizontal) ou V (vertical) : ");
				directionStr = in.readLine();
			}
			while(!directionStr.equals("H") && !directionStr.equals("V")) {
				out.println("Entrer H (horizontal) ou V (vertical) : ");
				directionStr = in.readLine();
			}
			if(directionStr.equals("H")) direction  = 1;
			else direction = -1; // vertical
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Retourne true si le bateau est correctement placé et le place. Retourne false sinon.
	 * @param player
	 * @param boatIndex
	 * @param direction
	 * @param origin
	 * @return isSet
	 */
	private boolean setBoat(int player, int boatIndex, int direction, String origin) {
		if(game.setPlayerBoat(player, boatIndex, direction, origin)) {
			out.println("Votre " + game.getP1().getBoats()[boatIndex].getName() + " est placé.") ;
			return true;
		}else {
			out.println("Veuillez vérifier les coordonnées entrées...") ;
			return false;
		}
	}
	
	/**
	 * Affiche la grille personnel du joueur et les bateaux restant à placer.
	 */
	private void boatToPlaceDisplay() {
		out.println(game.getPersonalBoards()[player - 1]);
		
		StringBuffer sb = new StringBuffer();
		sb.append("Bateaux restants à placer :\n");
		for(int i : game.unPlacedBoats(player).keySet()) {
			sb.append("\t" + i + " => " + game.unPlacedBoats(player).get(i).getName() + " (" + game.unPlacedBoats(player).get(i).getSize() + " cases)\n");
		}
		out.println(sb.toString());
	}
	
	
	
}


