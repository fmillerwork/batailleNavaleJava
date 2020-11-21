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
				System.out.println(in.readLine());
			}
		}catch (IOException e) {};
	}
}
