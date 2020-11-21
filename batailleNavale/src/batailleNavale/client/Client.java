package batailleNavale.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	public static void main(String[] args) {
		try {
			Socket serv = new Socket("localhost", 1500);
			PrintWriter out = new PrintWriter(serv.getOutputStream(), true);
			Scanner sc = new Scanner(System.in);
			new ListeningThread(serv).start();
			System.out.println("Connexion réussie ! En attente d'un adversaire...");
			
			while (true) {
				out.println(sc.nextLine());
			}
			
			
		}catch (Exception e) {
			System.out.println("Erreur client....");
		}
	}
	
}
