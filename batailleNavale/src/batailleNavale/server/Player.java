package batailleNavale.server;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
	private int[][] playerBoard = new int[10][10]; // tableau de jeu en 10 x 10. ligne x colonne | 0 => case vide | 1 => bateau | -1 => bateau touché
	private int[][] opponentBoard = new int[10][10]; //ligne x colonne | 0 => case vide | 1 => bateau touché | -1 => tir loupé
	private Boat[] boats = new Boat[5];
	
	public Player() {

		boats[0] = new Boat("Porte-Avion", 5);
		boats[1] = new Boat("Cuirasser", 4);
		boats[2] = new Boat("Sous-marin", 3);
		boats[3] = new Boat("Croiseur", 3);
		boats[4] = new Boat("Destroyer", 2);
	}
	
	/**
	 * Retourne le HUD des bateaux (nom + PV)
	 * @return boatsHUD
	 */
	private String getBoatsHUD() {
		StringBuffer stringDisplay = new StringBuffer();
		stringDisplay.append("Bateaux restants :\n");
		for(int i = 0; i < 5; i ++) { // pour chaque bateau
			if(boats[i].isAlive()) { // si vivant
				stringDisplay.append("\t" + boats[i].getName() + " ");
				for(int j = 0; j < boats[i].getHP(); j++) { // pour chaque PV
					stringDisplay.append("♡ ");
				}
				stringDisplay.append("\n");
			}
		}
		return stringDisplay.toString();
	}
	
	/**
	 * Retourne une ArrayList des id et références des bateaux non placés.
	 * @return unplacedBoats
	 */
	public HashMap<Integer, Boat> unplacedBoats() {
		HashMap<Integer, Boat> boats = new HashMap<Integer, Boat>();
		for(int i = 0; i < 5; i ++) {
			if(!this.boats[i].isPlaced()) boats.put(i, this.boats[i]);
		}
		return boats;
	}
	
	/**
	 * Renvoie true si le joueur est vaincu.
	 * @return isDefeated
	 */
	public boolean isDefeated() {
		for(int i = 0; i < 5; i++) {
			if(boats[i].getHP() != 0) // s'il reste des PV.
				return false;
		}
		return true;
	}
	
	
	/**
	 * Ajuste le tableau de l'adversaire en fonction du résultat du tir (touché ou loupé)
	 * @param coord
	 * @param result
	 */
	public void shotResult(String coord, boolean result) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		if(result) opponentBoard[shootCoord[0]][shootCoord[1]] = 1; // touché
		else opponentBoard[shootCoord[0]][shootCoord[1]] = -1; //tir loupé
	}
	
	/**
	 * Retourne true si la coordonnée est valide (case vide) pour un tir.
	 * @param coord
	 * @return canShoot
	 */
	public boolean canShoot(String coord) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		return opponentBoard[shootCoord[0]][shootCoord[1]] == 0; // case vide
	}
	
	
	/**
	 * Retourne true si touché et false si loupé. Ajuste le tableau de tir du joueur si touché et décrémente les HP du bateau touché.
	 * @param coord
	 * @return isShot
	 */
	public boolean isShot(String coord) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		if(playerBoard[shootCoord[0]][shootCoord[1]] == 1) { // présence d'un bateau
			playerBoard[shootCoord[0]][shootCoord[1]] = -1; // bateau devient touché
			
			//Gestion des HP
			for(int i = 0; i < 5; i++) {
				if(boats[i].isPresent(Utils.strCoordToIntCoord(coord)))	boats[i].setHP(boats[i].getHP() - 1);
			}
			return true;
		}
		return false;
	}

	/**
	 * Retourne le tableau de jeu global en string pour le joueur actuel.
	 * @param isFirstDisplay
	 * @return boards
	 */
	public String getBoards(boolean isFirstDisplay) {
		if(isFirstDisplay) return "\nVotre Board\n" + boardToString(true) + "\n\nBoard Adverse\n" + boardToString(false);
		else return "\nVotre Board\n" + boardToString(true) + getBoatsHUD() + "\n\nBoard Adverse\n" + boardToString(false);
	}
	
	
	/**
	 * Renvoi le tableau de jeu d'un joueur int[][] sous la forme d'un string.
	 * 
	 * @param isPlayerBoard
	 * @return board
	 */
	public String boardToString(boolean isPlayerBoard) {
		int[][] board;
		if(isPlayerBoard) board = playerBoard;
		else board = opponentBoard;
		StringBuffer sb = new StringBuffer();
		
		sb.append("  1 2 3 4 5 6 7 8 9 10\n");
		for (int i = 0; i < 10; i++) {
			sb.append((char)(i + 65) + " ");
			for (int j = 0; j < 10; j++) {
				if (board[i][j] == 0) // case vide (idem dans les 2 boards)
					sb.append(". ");
				else if (board[i][j] == 1) { // case bateau (player) ou case bateau touché (opponent)
					if(isPlayerBoard) sb.append("B ");
					else sb.append("x ");
				}
				else { // (= -1) case bateau coulé (player) ou case tir loupé (opponent)
					if(isPlayerBoard) sb.append("x "); 
					else sb.append("m ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	
	/**
	 * Retourne true et place le bateau dans la grille si les arguments sont
	 * valides. Retourne false sinon.
	 * 
	 * @param boatIndex (0 => PA, 1 => Cuirasser, 2 => Sous-Marin, 3 => Croiseur, 4 => Destoyer)
	 * @param direction (1 => droite, -1 => bas)
	 * @return IsSet
	 */
	public boolean setBoat(int boatIndex, int direction, String originCoord) {

		// vérif des données
		if (!isLocationAvailable(boatIndex, direction, originCoord)) {
			return false;

		} else { // placement des bateaux
			setBoatForPlayer(boatIndex, originCoord, direction);
			boats[boatIndex].setCoord(Utils.strCoordToIntCoord(originCoord), direction, boats[boatIndex].getSize());
		}
		return true;
	}


	/**
	 * Retourne true si les coordonnsss du bateau correspondent à des cases vides et existantes
	 * dans la grille (pas de dépacement).
	 * 
	 * @param boatIndex
	 * @param direction
	 * @param originCoord
	 * @return isLocationAvailable
	 */
	private boolean isLocationAvailable(int boatIndex, int direction, String originCoord) {
		int[] originCoordInt = Utils.strCoordToIntCoord(originCoord);
		if (direction == 1) { // droite
			if(boats[boatIndex].getSize() + originCoordInt[1] > 10) { // si le bateau dépasse la grille
				return false;}
			for (int i = originCoordInt[1]; i < boats[boatIndex].getSize() + originCoordInt[1]; i++) {
				if (playerBoard[originCoordInt[0]][i] != 0) { // la case est vide
					return false;
				}
			}
		} else { // vers le bas
			if(boats[boatIndex].getSize() + originCoordInt[0] > 10)
				return false;
			for (int i = originCoordInt[0]; i < boats[boatIndex].getSize() + originCoordInt[0]; i++) {
				if (playerBoard[i][originCoordInt[1]] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Place le beateau correspondant dans la grille.
	 * @param boatIndex
	 * @param playerBoard
	 * @param originCoord
	 * @param direction
	 */
	private void setBoatForPlayer(int boatIndex, String originCoord, int direction) {
		int[] originCoordInt = Utils.strCoordToIntCoord(originCoord);
		if (direction == 1) { // droite
			for (int i = originCoordInt[1]; i < boats[boatIndex].getSize() + originCoordInt[1]; i++)
				playerBoard[originCoordInt[0]][i] = 1;
		} else { // vers le bas
			for (int i = originCoordInt[0]; i < boats[boatIndex].getSize() + originCoordInt[0]; i++)
				playerBoard[i][originCoordInt[1]] = 1;
		}
	}


	public Boat[] getBoats() {
		return boats;
	}

	public void setBoats(Boat[] boats) {
		this.boats = boats;
	}

	
	
	
}
