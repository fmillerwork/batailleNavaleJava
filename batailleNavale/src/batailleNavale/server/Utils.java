package batailleNavale.server;

public abstract class Utils {

	
	/**
	 * Renvoi la coordonnée String passée en paramètre sous forme int
	 * 
	 * @param strCoord
	 * @return intCoord
	 */
	public static int[] strCoordToIntCoord(String strCoord) {
		int[] intCoord = new int[2];
		intCoord[0] = strCoord.toCharArray()[0] - 65; // code ASCII de A => 65, B => 66
		intCoord[1] = Integer.parseInt(strCoord.substring(1)) - 1;
		
		// Exemple : A7 => [0,6], J10 => [9,9]
		return intCoord; 
	}
	
	/**
	 * Renvoie true si la coordonnées est présente dans une grille de jeu. Retourne false sinon.
	 * @param coord
	 * @return isCoordValid
	 */
	public static boolean isValidCoord(String coord) {
		int[] originCoord = strCoordToIntCoord(coord);
		return originCoord[0] >= 0 && originCoord[0] <= 9 && originCoord[1] >= 0 && originCoord[1] <= 9;
	}
}
