package batailleNavale.server;

import java.util.HashMap;

public class Player {
	private int[][] placementBoard = new int[10][10]; // grille de placement en 10 x 10. ligne x colonne | 0 => case vide | 1 => bateau non touché | -1 => bateau touché | -2 bateau coulé
	private int[][] shootingBoard = new int[10][10]; // grille de tir en 10 x 10. ligne x colonne | 0 => case vide | 1 => bateau touché | -1 => tir loupé
	private Boat[] boats = new Boat[5];
	
	public Player() {
		boats[0] = new Boat("Porte-Avion", 5);
		boats[1] = new Boat("Cuirasser", 4);
		boats[2] = new Boat("Sous-marin", 3);
		boats[3] = new Boat("Croiseur", 3);
		boats[4] = new Boat("Destroyer", 2);
	}
	
	/**
	 * Retourne le HUD des bateaux en vie du joueur (nom + HP)
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
	 * Retourne une HashMap<Integer, Boat> des ids et noms des bateaux non placés.
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
			if(boats[i].getHP() != 0) return false; // s'il reste des HP.
		}
		return true;
	}
	
	
	/**
	 * Ajuste la grille de tir en fonction du résultat du tir (touché ou loupé)
	 * @param coord
	 * @param isShot
	 */
	public void shotResult(String coord, boolean isShot) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		if(isShot) shootingBoard[shootCoord[0]][shootCoord[1]] = 1; // touché
		else  shootingBoard[shootCoord[0]][shootCoord[1]] = -1; //tir loupé
	}
	
	/**
	 * Retourne true si la coordonnée est valide (case pas déjà ciblée) pour un tir.
	 * @param coord
	 * @return canShoot
	 */
	public boolean canShoot(String coord) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		return shootingBoard[shootCoord[0]][shootCoord[1]] == 0; // case vide
	}
	
	
	/**
	 * Retourne true si touché, ajuste la grille de placement et décrémente les HP du bateau touché. Retoune false si loupé. 
	 * @param coord
	 * @return isShot
	 */
	public boolean isShot(String coord) {
		int[] shootCoord = Utils.strCoordToIntCoord(coord);
		if(placementBoard[shootCoord[0]][shootCoord[1]] == 1) { // présence d'un bateau
			placementBoard[shootCoord[0]][shootCoord[1]] = -1; // bateau devient touché
			
			//Gestion des HP
			for(int i = 0; i < 5; i++) {
				if(boats[i].isPresent(Utils.strCoordToIntCoord(coord)))	boats[i].setHP(boats[i].getHP() - 1);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Retourne le nom du bateau coulé suite au tir et ajuste la grille de placement si le navire est coulé. Retourne null sinon. 
	 * @param coord
	 * @return isSunk
	 */
	public String isSunk(String coord) {
		
		//Recherche du bateau touché.
		for(int i = 0; i < 5; i++) {
			if(boats[i].isPresent(Utils.strCoordToIntCoord(coord)) && boats[i].getHP() == 0){ // bateau vient d'être coulé
				for(int j = 0; j < boats[i].getCoord().length;j++) {
					placementBoard[boats[i].getCoord()[j][0]][boats[i].getCoord()[j][1]] = -2; // bateau devient coulé
				}
				return boats[i].getName();
			}
		}
		return null;
	}
	/**
	 * Retourne le tableau de jeu global (les 2 grilles) en string pour le joueur actuel.
	 * @param isFirstDisplay
	 * @return boards
	 */
	public String getBoards(boolean isFirstDisplay) {
		if(isFirstDisplay) return "\nVotre Board\n" + boardToString(true) + "\n\nBoard Adverse\n" + boardToString(false);
		else return "\nVotre Board\n" + boardToString(true) + getBoatsHUD() + "\n\nBoard Adverse\n" + boardToString(false);
	}
	
	
	/**
	 * Renvoi une grille choisi d'un joueur int[][] sous la forme d'un string.
	 * 
	 * @param isPlayerBoard
	 * @return board
	 */
	public String boardToString(boolean isPlayerBoard) {
		int[][] board;
		if(isPlayerBoard) board = placementBoard;
		else board = shootingBoard;
		StringBuffer sb = new StringBuffer();
		
		sb.append("  1 2 3 4 5 6 7 8 9 10\n");
		for (int i = 0; i < 10; i++) {
			sb.append((char)(i + 65) + " "); // code ASCII pour A à J
			for (int j = 0; j < 10; j++) {
				if (board[i][j] == 0) // case vide (idem dans les 2 boards)
					sb.append(". ");
				else if (board[i][j] == 1) { // case bateau (player) ou case bateau touché (opponent)
					if(isPlayerBoard) sb.append("b ");
					else sb.append("x ");
				}else if (board[i][j] == -1) { // case bateau touché (player) ou case tir loupé (opponent)
					if(isPlayerBoard) sb.append("x "); 
					else sb.append("m ");
				}
				else { // (= -2) case bateau coulé (idem dans les 2 boards)
					sb.append("✟ "); 
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	
	/**
	 * Retourne true et place le bateau dans la grille de placement si les arguments sont
	 * valides. Retourne false sinon.
	 * 
	 * @param boatIndex (0 => PA, 1 => Cuirasser, 2 => Sous-Marin, 3 => Croiseur, 4 => Destoyer)
	 * @param direction (1 => droite, -1 => bas)
	 * @return IsSet
	 */
	public boolean setBoat(int boatIndex, boolean direction, String originCoord) {

		// vérif des données
		if (!isLocationAvailable(boatIndex, direction, originCoord)) return false;
		else { // placement des bateaux
			setBoatForPlayer(boatIndex, originCoord, direction);
			boats[boatIndex].setCoord(Utils.strCoordToIntCoord(originCoord), direction, boats[boatIndex].getSize());
		}
		return true;
	}


	/**
	 * Retourne true si les coordonnées du bateau correspondent à des cases vides et existantes
	 * dans la grille de placement.
	 * 
	 * @param boatIndex
	 * @param direction
	 * @param originCoord
	 * @return isLocationAvailable
	 */
	private boolean isLocationAvailable(int boatIndex, boolean direction, String originCoord) {
		int[] originCoordInt = Utils.strCoordToIntCoord(originCoord);
		if (direction) { // droite/horizontale
			if(boats[boatIndex].getSize() + originCoordInt[1] > 10) return false; 	// si le bateau dépasse la grille
			for (int i = originCoordInt[1]; i < boats[boatIndex].getSize() + originCoordInt[1]; i++) {
				if (!isNotAdjacent2Boat(new int[] {originCoordInt[0],i})) return false; // la case est non adjacente à un bateau (pas besoin de vérifier la présence d'un bateau sur la base courante)
			}
		} else { // (false) vers le bas / verticale
			if(boats[boatIndex].getSize() + originCoordInt[0] > 10) return false;	// si le bateau dépasse la grille
			for (int i = originCoordInt[0]; i < boats[boatIndex].getSize() + originCoordInt[0]; i++) {
				if (!isNotAdjacent2Boat(new int[] {i,originCoordInt[1]})) return false; // la case est non adjacente à un bateau (pas besoin de vérifier la présence d'un bateau sur la base courante)
			}
		}
		return true;
	}
	
	/**
	 * Retourne true si la celulle en paramètre n'est pas adjacente à un autre bateau (en diagonal aussi).
	 * @param coord
	 * @return isAdjacent2Boat
	 */
	private boolean isNotAdjacent2Boat(int[] coord) {
		if(coord[0]-1 < 0) {
			return placementBoard[coord[0]+1][coord[1]] ==0 &&
					placementBoard[coord[0]][coord[1]-1] ==0 && 
					placementBoard[coord[0]][coord[1]+1] ==0 &&
					placementBoard[coord[0]+1][coord[1]-1] ==0 &&
					placementBoard[coord[0]+1][coord[1]+1] ==0;
		}if(coord[0]+1 > 9) {
			return placementBoard[coord[0]-1][coord[1]] ==0 &&
					placementBoard[coord[0]][coord[1]-1] ==0 && 
					placementBoard[coord[0]][coord[1]+1] ==0 &&
					placementBoard[coord[0]-1][coord[1]-1] ==0 &&
					placementBoard[coord[0]-1][coord[1]+1] ==0;
		}if(coord[1]-1 < 0) {
			return placementBoard[coord[0]-1][coord[1]] ==0 && 
					placementBoard[coord[0]+1][coord[1]] ==0 &&
					placementBoard[coord[0]][coord[1]+1] ==0 &&
					placementBoard[coord[0]-1][coord[1]+1] ==0 &&
					placementBoard[coord[0]+1][coord[1]+1] ==0;
		}if(coord[1]+1 > 9) {
			return placementBoard[coord[0]-1][coord[1]] ==0 && 
					placementBoard[coord[0]+1][coord[1]] ==0 &&
					placementBoard[coord[0]][coord[1]-1] ==0 && 
					placementBoard[coord[0]-1][coord[1]-1] ==0 &&
					placementBoard[coord[0]+1][coord[1]-1] ==0;
		}
		return placementBoard[coord[0]-1][coord[1]] ==0 && 
				placementBoard[coord[0]+1][coord[1]] ==0 &&
				placementBoard[coord[0]][coord[1]-1] ==0 && 
				placementBoard[coord[0]][coord[1]+1] ==0 &&
				placementBoard[coord[0]-1][coord[1]-1] ==0 &&
				placementBoard[coord[0]-1][coord[1]+1] ==0 &&
				placementBoard[coord[0]+1][coord[1]-1] ==0 &&
				placementBoard[coord[0]+1][coord[1]+1] ==0;
		
	}

	/**
	 * Place le bateau correspondant dans la grille.
	 * @param boatIndex
	 * @param playerBoard
	 * @param originCoord
	 * @param direction
	 */
	private void setBoatForPlayer(int boatIndex, String originCoord, boolean direction) {
		int[] originCoordInt = Utils.strCoordToIntCoord(originCoord);
		if (direction) { // droite/horizontale
			for (int i = originCoordInt[1]; i < boats[boatIndex].getSize() + originCoordInt[1]; i++) placementBoard[originCoordInt[0]][i] = 1;
			
		} else { // (false) vers le bas / verticale
			for (int i = originCoordInt[0]; i < boats[boatIndex].getSize() + originCoordInt[0]; i++) placementBoard[i][originCoordInt[1]] = 1;		
		}
	}


	public Boat[] getBoats() {
		return boats;
	}

	public void setBoats(Boat[] boats) {
		this.boats = boats;
	}

	
	
	
}
