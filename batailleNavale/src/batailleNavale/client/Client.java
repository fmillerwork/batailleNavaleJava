package batailleNavale.client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	
	private static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		tryConnection();
	}
	
	private static void tryConnection(){
		try {
			System.out.println("Tentative de connexion...");
			
			Socket serv = new Socket("localhost", 1500);
			PrintWriter out = new PrintWriter(serv.getOutputStream(), true);
			ListeningThread lt = new ListeningThread(serv);
			lt.start();
			System.out.println("Connexion réussie ! En attente d'un adversaire...");
			
			while (lt.isAlive()) { // le thread est en vie (donc le serveur n'a pas indiqué la fin de la partie)
				out.println(sc.nextLine());
			}
			
			
		}catch (Exception e) {
			System.out.println("Serveur non disponible...\nPour quitter l'application, tapper \"Quitter\". Pour tenter une reconnexion, entrer autre chose.");
			String choice = sc.nextLine();
			if(!choice.equals("Quitter")) 
				tryConnection(); // Appel récursif
			else	
				System.out.println("Fermeture de l'application");
		}
	}
}
