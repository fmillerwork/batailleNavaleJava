package batailleNavale.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ListeningThread extends Thread{
	BufferedReader in;
	
	public ListeningThread(Socket s) throws IOException {
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	
	public void run(){
		try {
			while (true) {
				String serverMessage = in.readLine();
				if(serverMessage.equals("end"))
					break; // fermeture du thread si réception du message de fin de partie
				System.out.println(serverMessage);
			}
			System.out.println("Fin de la partie... (Appuyer sur Entrer pour quitter l'application)");
			
		}catch (IOException e) {
			System.out.println("Connexion au serveur interrompue...");
		}
	}
}
