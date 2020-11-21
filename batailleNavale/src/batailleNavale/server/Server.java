package batailleNavale.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) {
		try {
			ServerSocket listen = new ServerSocket(1500);
			System.out.println("Serveur lancé !");
				while(true) {
					Socket client1 = listen.accept();
					Socket client2 = listen.accept();
					System.out.println("Partie crée !");
					new GameThread(client1, client2).start();
				}
			}
		catch (Exception e) {
			System.out.println("Erreur server....");
		}
	}	
}
