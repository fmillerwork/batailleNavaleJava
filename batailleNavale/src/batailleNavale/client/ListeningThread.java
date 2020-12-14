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
				System.out.println(serverMessage);
				if(serverMessage.equals("Fin de la partie... (Entrer pour quitter l'application)"))
					break; // fermeture du thread
			}
		}catch (IOException e) {
			System.out.println("Connexion au serveur interrompue...");
		}
	}
}
