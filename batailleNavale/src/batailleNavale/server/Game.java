package batailleNavale.server;

import java.util.HashMap;
import java.util.Hashtable;

public class Game {
	private Player p1 = new Player();
	private Player p2 = new Player();
	private boolean firstPlayerActive = true; // true => j1 actif | false => j2 actif
	
	public Game() {
	}


	/**
	 * Ajuste le tableau de l'adversaire du shooter en paramètre, en fonction du résultat du tir (touché ou loupé)
	 * @param shooter
	 * @param coord
	 * @param isShot
	 */
	public void shotResult(int shooter, String coord, boolean isShot) {
		if(shooter == 1) p1.shotResult(coord, isShot);
		else p2.shotResult(coord, isShot);
	}

	/**
	 * Renvoie true si la partie est terminée. False sinon.
	 * @return isGameEnded
	 */
	public boolean isGameEnded() {
		return p1.isDefeated() || p2.isDefeated();
	}
	
	/**
	 * Retourne true et actualise la grille de placedment du joueur target si ce dernier est touché suivant la coordonnée en paramètre.
	 * @param target
	 * @param coord
	 * @return isShot
	 */
	public boolean isShot(int target, String coord) {
		if(target == 1) return isTargetShot(p1, p2, coord);
		else return isTargetShot(p2, p1, coord); // joueur 2 ciblé
	}
	
	/**
	 * Retourne le nom du navire si ce dernier est coulé et actualise la grille de placement du joueur target. Retourne null sinon.
	 * @param target
	 * @param coord
	 * @return isSunk
	 */
	public String isSunk(int target, String coord) {
		if(target == 1) return isTargetSunk(p1, p2, coord);
		else return isTargetSunk(p2, p1, coord); // joueur 2 ciblé
	}
	
	/**
	 * Sous méthode de isShot() de la classe Game.
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isShot
	 */
	private boolean isTargetShot(Player target, Player shooter, String coord) {
		if(target.isShot(coord)) { // touché
			shooter.shotResult(coord, true);
			return true;
		}else { // loupé
			shooter.shotResult(coord, false);
			return false;
		}
	}

	/**
	 * Sous méthode de isSunk() de la classe Game.
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isSunk
	 */
	private String isTargetSunk(Player target, Player shooter, String coord) {
		String sunkBoatName = target.isSunk(coord);
		if(sunkBoatName != null) return sunkBoatName; // coulé
		else return null; // non coulé
	}
	/**
	 * Retourne true si la coordonnée est valide (case pas déjà ciblée) pour joueur tireur en paramètre
	 * @param player
	 * @param coord
	 * @return canShoot
	 */
	public boolean isAlreadyShot(int shooter, String coord) {
		if(shooter == 1) return p1.canShoot(coord);
		else return p2.canShoot(coord);
	}
	
	/**
	 * Retourne les 2 grilles de chaque joueur.
	 * @param isFirstDisplay
	 * @return boards[]
	 */
	public String[] getPlayersBoards(boolean isFirstDisplay) {
		return new String[] {p1.getBoards(isFirstDisplay),p2.getBoards(isFirstDisplay)};
	}
	
	/**
	 * Retourne la grille de placement de chaque joueur.
	 * @return personalBoards
	 */
	public String[] getPersonalBoards() {
		return new String[] {"\nVotre Board\n" + p1.boardToString(true),"\nVotre Board\n" + p2.boardToString(true)};
	}

	/**
	 * Renvoie true si la bateau est correctement placé dans le board du joueur correspondant. Sinon, renvoie false.
	 * @param player
	 * @param boatIndex
	 * @param direction
	 * @param origin
	 * @return isSet
	 */
	public boolean setPlayerBoat(int player, int boatIndex, boolean direction, String origin) {
		if(player == 1) return p1.setBoat(boatIndex, direction, origin);
		else return p2.setBoat(boatIndex, direction, origin);
	}
	
	/**
	 * Retourne une HashMap<Integer, Boat> des id et noms des bateaux non placés pour le joueur en paramètre.
	 * @return unPlacedBoats
	 */
	public HashMap<Integer, Boat> unPlacedBoats(int player) {
		if(player == 1) return p1.unplacedBoats();
		else return p2.unplacedBoats();
	}
	
	
	
	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}


	public boolean isFirstPlayerActive() {
		return firstPlayerActive;
	}


	public void setFirstPlayerActive(boolean firstPlayerActive) {
		this.firstPlayerActive = firstPlayerActive;
	}







	
	

}
