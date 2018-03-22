package poker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

	private Socket socket;
	BufferedReader input;
	PrintWriter output;
	int playerNum;
	int ogPlayerNum;
	int contribution = 0;
	int totalContribution = 0;
	boolean betRequired = false;
	boolean excluded = false;
	boolean busted = false;
	String playerName = "";

	public ClientThread(Socket s, int id) {

		this.socket = s;
		this.playerNum = id;
		this.ogPlayerNum = id;

		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
			
		} catch (IOException e) {
			System.out.println("Player died: " + e);
		}

	}

	@Override
	public void run() {
		
		try {

		} catch (Exception e) {
			System.out.println(e);
		}

		// Initial deal
		String cards = "";
		for (int x = 0; x < 5; x++) {
			cards += DealerServer.deck.pop() + " ";
		}
		cards.trim();

		//Holding loop till all players join
		while (true) {
			//Once all players have joined start the GUI
			if (DealerServer.serverStart) {
				
				
				
				output.println(cards);
				DealerServer.game.increasePot(DealerServer.game.getAnte());

				output.println("ADDCHIPS " + (DealerServer.game.getStartingChips() - DealerServer.game.getAnte()));
					
				break;
			}
		}

		boolean hideBtnSent = false;

		// Read/send commands from/to the client GUI
		try {

			boolean swapMade = false;
			boolean folded = false;
			boolean allIn = false;
			String command = "";
			boolean showdown = false;

			boolean commandOutput = false;
			boolean commandOutput2 = false;
			boolean commandOutput3 = false;
			boolean reset = false;
			boolean handSent = false;
			int oldNum = playerNum;
			playerName = "" + playerNum;
			DealerServer.game.nameMap.put(playerNum, playerName);
			
			while (true) {
				
				//System.out.println("Name: " + playerName + " Number: " + playerNum);
				
				Thread.sleep(100);
				
				/*
				if(busted & DealerServer.game.getPlayerTurn() == this.playerNum) {
					DealerServer.game.incrementPlayerTurn();
					
					if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn() && busted == false){
						DealerServer.game.setPlayerTurn(0);
					}
				}
				*/
				
				if(DealerServer.killApp) {
					output.println("EXIT");
					DealerServer.playersKilled++;
					break;
				}

				if(commandOutput2 == false && (DealerServer.game.noPlayers - 1) == playerNum && (DealerServer.game.getStage().equals("SWAP") || DealerServer.game.getStage().equals("SHOWDOWN"))){
					commandOutput2 = true;
					output.println("HIDEBUTTONS");
				}			
								
				//Loop till all informational messages are consumed
				while(playerNum != -1 && !DealerServer.game.isMessageEmpty(playerNum)){
					output.println("INFO " + DealerServer.game.getMessage(playerNum));
				}
				
				while(playerNum != -1 && !DealerServer.game.isChatEmpty(playerNum)){
					output.println("MSG " + DealerServer.game.getChat(playerNum));
				}
				
				if (input.ready()) {
					command = input.readLine();
				}
				
				if(command.startsWith("MSG")){
					DealerServer.game.addChat(playerName + " says: " + command);
					command = "";
				}
				else if(command.startsWith("SETNAME")){
					String[] values = command.split(" ");
					this.playerName = values[1];
					command = "";
					DealerServer.game.nameMap.put(playerNum, playerName);
				}

				//Decide whether the player is allowed to check or must match a bet
				if (DealerServer.game.getHighestContribution() > contribution) {
					betRequired = true;
				} else {
					betRequired = false;
				}
				
				if(!DealerServer.game.bustedReset){
					reset = false;
				}
				
				if(!reset && DealerServer.game.getStage().equals("ALLFOLDED")) {
					 					
					if(!folded && !showdown) {
						DealerServer.game.addMessage("Player " + playerName + " has won as all other players have folded");
						output.println("ADDCHIPS " + DealerServer.game.getPot());
						DealerServer.game.setChipsAdded(true);
					}
					
					reset = true;
					DealerServer.game.playersReset++;			
					contribution = 0;
					totalContribution = 0;			
					swapMade = false;
					folded = false;
					allIn = false;
					commandOutput = false;
					commandOutput2 = false;
					commandOutput3 = false;
					hideBtnSent = false;
					handSent = false;
					showdown = false;
					
					int chips = 0;
					output.println("GETCHIPS");
					while(true) {
						if(input.ready()) {
							chips = Integer.parseInt(input.readLine());
							break;
						}
					}
					
					if(chips - DealerServer.game.getAnte() <= 0) {
						folded = true;
						if(excluded == false && busted == false) {
							synchronized(DealerServer.game) {
								excluded = true;
								busted = true;
								DealerServer.game.bustedPlayers.add(playerNum);
								DealerServer.game.bustedPlayerCount++;
								DealerServer.game.addMessage("Player " + playerName + " has busted out");
								DealerServer.game.noPlayers--;
								//playerNum = (DealerServer.game.noPlayers + DealerServer.game.bustedPlayerCount) - 1;
								playerNum = DealerServer.noPlayers - DealerServer.game.bustedPlayerCount;
								System.out.println("Busted player "  + oldNum + " now has a number of " + playerNum);
								output.println("BUSTED");
								//playerNum = -1;
							}
						}
						DealerServer.game.playersReady++;
					}
					else{
						DealerServer.game.playersReady++;
						
						while(DealerServer.game.playersReady < DealerServer.noPlayers) {
							//System.out.println("ready: " + DealerServer.game.playersReady + " total " + DealerServer.noPlayers);
						}
						
						DealerServer.game.bustedReset = true;
						
						if(!DealerServer.game.bustedPlayers.isEmpty()) {
							for(int x = 0;x < DealerServer.game.bustedPlayers.size();x++) {
								if(playerNum >= DealerServer.game.bustedPlayers.get(x)) {
									//System.out.println("Player " + playerNum + " is now player " + (playerNum - 1));
									playerNum--;
								}
							}
						}
						
						cards = "";
						for (int x = 0; x < 5; x++) {
							cards += DealerServer.deck.pop() + " ";
						}
						cards.trim();

						output.println("NEWDEAL " + cards);

						output.println("ANTE " + DealerServer.game.getAnte());
						DealerServer.game.increasePot(DealerServer.game.getAnte());
					}
					
					DealerServer.game.nameMap.put(playerNum, playerName);
					
				}

				else if (DealerServer.game.getPlayerTurn() == playerNum && DealerServer.game.getStage().equals("BET") && busted == false) {
					
					reset = false;
					showdown = false;
					
					//System.out.println("Player Num: " + playerNum + " Player Turn: " + DealerServer.game.getPlayerTurn());
					
					//Skip turn if folded
					if (folded) {
						DealerServer.game.incrementPlayerTurn();
						
						//If last player reset player turn
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn()){
							DealerServer.game.setPlayerTurn(0);
						}
						
					}
					else if(allIn) {
						DealerServer.game.incrementPlayerTurn();
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn()){
							DealerServer.game.setPlayerTurn(0);
						}
					}
					//Process the bet a player has sent
					else if (command.startsWith("BETDONE")) {
						String[] betRequest = command.split(" ");
						int betAmount = Integer.parseInt(betRequest[1]);
						DealerServer.game.increasePot(betAmount);
						contribution += betAmount;
						totalContribution += betAmount;
						if (contribution > DealerServer.game.getHighestContribution()) {
							DealerServer.game.setHighestContribution(contribution);
							DealerServer.game.setPlayersCalled(0);
							DealerServer.game.setPlayersChecked(0);
						}
						if (betAmount > DealerServer.game.getHighestBet()) {
							DealerServer.game.setHighestBet(betAmount);
						}
						//System.out.println("Pot is now: " + DealerServer.game.getPot());
						DealerServer.game.addMessage(("Player " + playerName + " has bet " + betAmount));
						DealerServer.game.addMessage(("Pot is now: " + DealerServer.game.getPot()));
						
						DealerServer.game.incrementPlayerTurn();
						DealerServer.game.incrementPlayersCalled();
						output.println("HIDEBUTTONS");
						
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn() && busted == false){
							DealerServer.game.setPlayerTurn(0);
						}
						
						commandOutput = false;
						command = "";
					} else if (command.startsWith("FOLDED") && folded == false) {
						DealerServer.game.foldedPlayers.add(playerNum);
						System.out.println("Player " + playerNum + " has excluded themselves from play");
						DealerServer.game.eligiblePlayers.put(playerNum, "FOLDED");
						DealerServer.game.incrementPlayerTurn();
						DealerServer.game.incrementPlayersFolded();
						DealerServer.game.addMessage("Player " + playerName + " has folded");
						
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn() && busted == false){
							DealerServer.game.setPlayerTurn(0);
						}
						
						folded = true;
						command = "";
					} else if (command.startsWith("CHECK")) {
						DealerServer.game.incrementPlayerTurn();
						DealerServer.game.incrementPlayersChecked();
						DealerServer.game.addMessage("Player " + playerName + " has checked");

						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn() && busted == false){
							DealerServer.game.setPlayerTurn(0);
						}
						
						commandOutput = false;
						command = "";
					}
					else if(command.startsWith("ALLIN")) {
						String[] tokens = command.split(" ");
						totalContribution += Integer.parseInt(tokens[1]);
						contribution += Integer.parseInt(tokens[1]);
						allIn = true;
						DealerServer.game.increasePot(Integer.parseInt(tokens[1]));
						System.out.println("Pot is now: " + DealerServer.game.getPot());
						DealerServer.game.addMessage(("Player " + playerName + " has gone all in with " + Integer.parseInt(tokens[1])));
						DealerServer.game.addMessage(("Pot is now: " + DealerServer.game.getPot()));
						
						DealerServer.game.setPlayersChecked(0);
						
						if (contribution > DealerServer.game.getHighestContribution()) {
							DealerServer.game.setHighestContribution(contribution);
							DealerServer.game.setPlayersCalled(0);
						}
						if (Integer.parseInt(tokens[1]) > DealerServer.game.getHighestBet()) {
							DealerServer.game.setHighestBet(Integer.parseInt(tokens[1]));
						}
						
						DealerServer.game.incrementPlayerTurn();
						DealerServer.game.incrementPlayersAllIn();
						output.println("HIDEBUTTONS");
						
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn() && busted == false){
							DealerServer.game.setPlayerTurn(0);
						}
						
						commandOutput = false;
						command = "";
					}

					if (DealerServer.game.isContinueBetting() == true && folded == false && commandOutput == false
							&& DealerServer.game.getPlayerTurn() == playerNum
							&& DealerServer.game.getStage().equals("BET") && allIn == false) {
						commandOutput = true;
						output.println(
								"BET " + betRequired + " " + (DealerServer.game.getHighestContribution() - contribution)
										+ " " + DealerServer.game.getHighestBet());
					}

				} else if (DealerServer.game.getPlayerTurn() == playerNum
						&& DealerServer.game.getStage().equals("SWAP") && !busted) {

					
					
					contribution = 0;
					betRequired = false;
					commandOutput = false;
					
					if (!hideBtnSent) {
						hideBtnSent = true;
						output.println("HIDEBUTTONS");
					}

					if (swapMade == false) {
						hideBtnSent = false;
						output.println("SWAP");
						swapMade = true;
					}

					if (folded || busted) {
						DealerServer.game.incrementPlayerTurn();
						
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn()){
							DealerServer.game.setPlayerTurn(0);
						}
						
					} 
					else if (command.startsWith("SWAPPING")) {
						String[] cmnds = command.split(" ");
						int loops = Integer.parseInt(cmnds[1]);
						DealerServer.game.addMessage("Player " + playerName + " has swapped " + loops + " cards");
						if(loops> 0){
							String newCards = "";
							for (int i = 0; i < loops; i++) {
								newCards += DealerServer.deck.pop() + " ";
							}
							output.println(newCards.trim());
						}
						
						DealerServer.game.incrementPlayerTurn();
						DealerServer.game.playersSwapped++;
						
						//System.out.println("Actual Value: " + DealerServer.game.playersSwapped + " Expected Value: " + (DealerServer.noPlayers - (DealerServer.game.getPlayersFolded())));
						
						if(DealerServer.game.noPlayers == DealerServer.game.getPlayerTurn()){
							DealerServer.game.setPlayerTurn(0);
						}

						if ((DealerServer.game.playersSwapped == DealerServer.game.noPlayers - (DealerServer.game.getPlayersFolded())) && swapMade == true) {
							DealerServer.game.setStage("BET");
							DealerServer.game.setPlayerTurn(0);
						}

						command = "";
					}

				}
				else if (DealerServer.game.getStage().equals("SHOWDOWN") && busted == false){
					
					showdown = true;
					
					if (!commandOutput3) {
						commandOutput3 = true;
						output.println("HIDEBUTTONS");
					}
					
					DealerServer.game.setTotalContribution(playerNum, this.totalContribution);
					//System.out.println("Client side  " + DealerServer.game.totalContributions.get(playerNum));
					
					if(!folded && !handSent) {
						output.println("HAND");
						handSent = true;
						String submission = "";
						while(true) {
							if (input.ready()) {
								submission = input.readLine();
								DealerServer.game.addMessage("Player " + playerName + "'s hand: " + submission);
								DealerServer.game.addFinalHand(playerNum, playerNum + " " + submission);
								break;
							}
						}
						
						DealerServer.game.handsSent++;						
						
						while(DealerServer.game.getWinner() == -1) {
							Thread.sleep(500);
							//System.out.println("Game winner " + DealerServer.game.getWinner());
						}
										
						if(DealerServer.game.getWinner() == 1) {
							output.println("ADDCHIPS " + DealerServer.game.playerWinnings.get(playerNum));
							DealerServer.game.setChipsAdded(true);
							DealerServer.game.incrementPlayerTurn();
						}
								
						DealerServer.game.incrementPlayersFolded();
						
						
					}
					
				}

			}
		} catch (Exception e) {
			System.out.println("Problem in ClientThread " + playerNum);
			e.printStackTrace();
		}

	}

}
