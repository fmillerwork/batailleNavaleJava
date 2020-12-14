package batailleNavale.server;

public class Boat {

	String name;
	int size;
	int HP;
	boolean placed = false;
	int[][] coord; // [i][j] => dimension i => nombre de coords et dimension j => coordonnées.
				   // coord[0][0] => ligne coord 1 | coord[0][1] => col coord 1
	 			   // coord[1][0] => ligne coord 2 | coord[1][1] => col coord 2
	
	
	public Boat(String nom, int size) {
		super();
		this.name = nom;
		this.size = size;
		this.HP = size;
	}
	
	
	
	/**
	 * Renvoie true si le bateau est présent à la coordonnée en paramètre.
	 * @param coord
	 * @return isPresent
	 */
	public boolean isPresent(int[] cellCoord) {
		for(int i = 0; i < size; i++) {
			if(cellCoord[0] == coord[i][0] && cellCoord[1] == coord[i][1])	return true;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isAlive() {
		return HP != 0;
	}
	public boolean isPlaced() {
		return placed;
	}
	public void setPlaced(boolean set) {
		this.placed = set;
	}
	public int getHP() {
		return HP;
	}
	public void setHP(int hP) {
		HP = hP;
	}
	public int[][] getCoord() {
		return coord;
	}
	
	public void setCoord(int[] origin, boolean direction, int size) {
		this.coord = new int[size][2];	// [ligneCoord,colonneCoord] | [ligneCoord,colonneCoord] | [ligneCoord,colonneCoord], ....
		if(direction) { // droite/horizontale
			for(int i = 0; i < size; i++) {	// pour chaque case
				this.coord[i][0] = origin[0]; // ligne de la case
				this.coord[i][1] = origin[1] + i; // colonne de la case
			}
		}
		else { // (false) vers le bas / verticale
			for(int i = 0; i < size; i++) {	// pour chaque case
				this.coord[i][0] = origin[0] + i; // ligne de la case
				this.coord[i][1] = origin[1]; // colonne de la case
			}
		}
	}
}
