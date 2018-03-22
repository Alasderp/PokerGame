package poker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DealerServer extends Thread {

	public static volatile boolean killApp = false;
	public static volatile int playersKilled = 0;
	
	public static Deck deck;
	
	//Used to hold some of the logic/rules
	public static Game game;
	
	public static boolean serverStart = false;
	
	public static ArrayList<ClientThread> players = new ArrayList<>();
	
	public static volatile int noPlayers = 2;

	public static void main(String[] args) throws Exception {
		
		int port = 8181;
		ServerSocket socket = new ServerSocket(port);
		System.out.println("Server Socket is operating on port: " + socket.getLocalPort());
		System.out.println("Type 'exit' to close application after players have joined");
		deck = new Deck();
		deck.newDeck();
		deck.shuffle();

		int counter = 0;
		
		ArrayList<InetAddress> clientIPs = new ArrayList<>();
		
		boolean gameMade = false;

		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String command = "";
			
			while (true) {
				
				//Loop till the desired amount of players connect
				if(counter < noPlayers) {
					Socket clientSocket = socket.accept();
					ClientThread player = new ClientThread(clientSocket, counter);
					//player.start();
					players.add(player);
					System.out.println(clientSocket.getInetAddress() + " connected");
					clientIPs.add(clientSocket.getInetAddress());
					counter++;
				}
				
				//Once the required amount of players have joined create a new game and start the threads
				if(counter == noPlayers && gameMade == false) {
					gameMade = true;
					game = new Game(noPlayers);
					game.start();
					counter++;
					for(int x = 0;x < players.size();x++) {
						players.get(x).start();
					}
					System.out.println("Game and players started");
				}
				
				if(br.ready()) {
					command = br.readLine();
					System.out.println("cmd received: " + command);
				}
				
				if(command.equals("exit")) {
					
					killApp = true;
					
					while(true) {
						if((noPlayers + 1) == playersKilled) {
							
							Thread.sleep(1000);
							
							socket.close();
							System.exit(0);
						}
					}
				}
				
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}

	}

}
