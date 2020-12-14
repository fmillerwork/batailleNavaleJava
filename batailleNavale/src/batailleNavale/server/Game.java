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
	 * @param result
	 */
	public void shotResult(int shooter, String coord, int result) {
		if(shooter == 1) p1.shotResult(coord, result);
		else p2.shotResult(coord, result);
	}

	/**
	 * Renvoie true si la partie est terminée.
	 * @return isGameEnded
	 */
	public boolean isGameEnded() {
		return p1.isDefeated() || p2.isDefeated();
	}
	
	/**
	 * Retourne true et actualise la grille du joueur target si ce dernier est touché suivant la coordonnée en paramètre.
	 * @param target
	 * @param coord
	 * @return isShot
	 */
	public boolean isShot(int target, String coord) {
		if(target == 1) {
			return isTargetShot(p1, p2, coord);
		}
		else { // joueur 2 ciblé
			return isTargetShot(p2, p1, coord);
		}
	}
	
	/**
	 * Retourne le nom du navire si ce dernier est coulé et actualise la grille du joueur target. Retourne null sinon.
	 * @param target
	 * @param coord
	 * @return isSunk
	 */
	public String isSunk(int target, String coord) {
		if(target == 1) {
			return isTargetSunk(p1, p2, coord);
		}
		else { // joueur 2 ciblé
			return isTargetSunk(p2, p1, coord);
		}
	}
	
	/**
	 * Retourne true si la joueur cible à touché le joueur target. Retourne false sinon. 
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isShot
	 */
	private boolean isTargetShot(Player target, Player shooter, String coord) {
		if(target.isShot(coord)) { // touché
			shooter.shotResult(coord, 1);
			return true;
		}else { // loupé
			shooter.shotResult(coord, -1);
			return false;
		}
	}

	/**
	 * Retourne le nom du navire coulé et actualise la grille si le joueur cible à coulé le navire du joueur target. Retourne null sinon. 
	 * @param target
	 * @param shooter
	 * @param coord
	 * @return isSunk
	 */
	private String isTargetSunk(Player target, Player shooter, String coord) {
		String sunkBoatName = target.isSunk(coord);
		if(sunkBoatName != null) { // coulé
			return sunkBoatName;
		}else { // non coulé
			return null;
		}
	}
	/**
	 * Retourne true si la coordonnée est valide (case vide) pour joueur tireur en paramètre
	 * @param player
	 * @param coord
	 * @return canShoot
	 */
	public boolean checkShoot(int shooter, String coord) {
		if(shooter == 1) return p1.canShoot(coord);
		else return p2.canShoot(coord);
	}
	
	/**
	 * Retourne le tableau de jeu global de chaque joueur.
	 * @param isFirstDisplay
	 * @return boards[]
	 */
	public String[] getPlayersBoards(boolean isFirstDisplay) {
		return new String[] {p1.getBoards(isFirstDisplay),p2.getBoards(isFirstDisplay)};
	}
	
	/**
	 * Retourne le tableau personnel de chaque joueur.
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
	 * Retourne une ArrayList des id et noms des bateaux non placés pour le joueur en paramètre.
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
