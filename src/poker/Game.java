package poker;

/*
 * TODO - Sidepots
 * Take the smallest stack - i.e. smallest total contribution and match this with all other players
 * Subtract this from the total contribution of all the other players who haven't folded 
 * Put all the subtracted cash and this smallest stack into a pot, plus dead chips (chips from folded hands) plus all the antes
 * Put the leftover cash into another pot (a side pot)
 * repeat process if needs be
 * If there is leftover cash i.e. a player contesting a pot only with himself, return cash
 * Create no. of chips earned for each player and add this all at the end
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Game extends Thread {

	//Used to store the number of active players as opposed to the total number of players in DealerServer
	public volatile int noPlayers;
	
	public volatile int bustedPlayerCount = 0;
	
	public volatile int playersReady = 0;
	
	public boolean bustedReset = false;

	public HashMap<Integer, String> nameMap;
	
	public ArrayList<Integer> bustedPlayers = new ArrayList<>();
		
	private volatile int smallestKey = 100;
	private volatile int largestKey;
	
	public Set<Integer> tiedPlayers;
	
	private Boolean enteredEnd = false;
	
	public volatile int playersSwapped = 0;
	
	private volatile int playersAllIn = 0;
	private ReadWriteLock allInLock;
	private Lock readAllInLock;
	private Lock writeAllInLock;
	
	int ante = 50;
	private ReadWriteLock anteLock;
	private Lock readAnteLock;
	private Lock writeAnteLock;
	
	private int startingChips = 10000;
	private ReadWriteLock startChipsLock;
	private Lock readStartChipsLock;
	private Lock writeStartChipsLock;
	
	private volatile boolean chipsAdded = false;
	private ReadWriteLock chipsAddedLock;
	private Lock readChipsAdded;
	private Lock writeChipsAdded;
	
	private volatile int winner = -1;
	private ReadWriteLock winnerLock;
	private Lock readWinnerLock;
	private Lock writeWinnerLock;

	public volatile int handsSent = 0;
	private ReadWriteLock handSentLock;
	private Lock readHandsSentLock;
	private Lock writeHandsSentLock;
	
	volatile int counter = 0;
	
	private volatile int playerTurn = 0;
	private ReadWriteLock turnLock;
	private Lock readTurnLock;
	private Lock writeTurnLock;

	private volatile int pot = 0;
	private ReadWriteLock potLock;
	private Lock readPotLock;
	private Lock writePotLock;

	private volatile String stage;
	private ReadWriteLock stageLock;
	private Lock readStageLock;
	private Lock writeStageLock;

	private boolean continueBetting = true;
	private ReadWriteLock betLock;
	private Lock readBetLock;
	private Lock writeBetLock;

	private volatile int playersChecked = 0;
	private ReadWriteLock checkLock;
	private Lock readCheckLock;
	private Lock writeCheckLock;

	private volatile int playersFolded = 0;
	private ReadWriteLock foldLock;
	private Lock readFoldLock;
	private Lock writeFoldLock;

	private volatile int highestContribution = -1;
	private ReadWriteLock contributionLock;
	private Lock readContributionLock;
	private Lock writeContributionLock;

	private volatile int playersCalled = 0;
	private ReadWriteLock callLock;
	private Lock readCallLock;
	private Lock writeCallLock;

	private volatile int highestBet = -1;
	private ReadWriteLock highbetLock;
	private Lock readHighbetLock;
	private Lock writeHighbetLock;
	
	private ArrayList<ArrayList<String>> messageQueue;
	public ReadWriteLock messageLock;
	public Lock readMessageLock;
	public Lock writeMessageLock;
	
	private ArrayList<ArrayList<String>> chatQueue;
	public ReadWriteLock chatLock;
	public Lock readChatLock;
	public Lock writeChatLock;
	
	boolean insideShowDown = false;
	
	boolean betTwo = false;
	
	public volatile int playersReset = 0;
	
	private LinkedHashMap<Integer, String> finalHands;
	private ReadWriteLock finalHandLock;
	private Lock writeFinalHandLock;
	
	public HashMap<Integer, Integer> totalContributions;
	private ReadWriteLock contributionMapLock;
	private Lock writeContributionMapLock;
	
	public ArrayList<Integer> foldedPlayers = new ArrayList<>(); 
	public HashMap<Integer, String> eligiblePlayers = new HashMap<>();
	public ArrayList<Integer> playerWinnings = new ArrayList<>();
	private int currentPot;

	public Game(int playerCount) {

		finalHands = new LinkedHashMap<>();
		
		this.turnLock = new ReentrantReadWriteLock();
		this.readTurnLock = turnLock.readLock();
		this.writeTurnLock = turnLock.writeLock();

		this.potLock = new ReentrantReadWriteLock();
		this.readPotLock = potLock.readLock();
		this.writePotLock = potLock.writeLock();

		this.stageLock = new ReentrantReadWriteLock();
		this.readStageLock = stageLock.readLock();
		this.writeStageLock = stageLock.writeLock();

		this.betLock = new ReentrantReadWriteLock();
		this.readBetLock = betLock.readLock();
		this.writeBetLock = betLock.writeLock();

		this.checkLock = new ReentrantReadWriteLock();
		this.readCheckLock = checkLock.readLock();
		this.writeCheckLock = checkLock.writeLock();

		this.foldLock = new ReentrantReadWriteLock();
		this.readFoldLock = foldLock.readLock();
		this.writeFoldLock = foldLock.writeLock();

		this.contributionLock = new ReentrantReadWriteLock();
		this.readContributionLock = contributionLock.readLock();
		this.writeContributionLock = contributionLock.writeLock();

		this.callLock = new ReentrantReadWriteLock();
		this.readCallLock = callLock.readLock();
		this.writeCallLock = callLock.writeLock();

		this.highbetLock = new ReentrantReadWriteLock();
		this.readHighbetLock = highbetLock.readLock();
		this.writeHighbetLock = highbetLock.writeLock();
		
		this.messageLock = new ReentrantReadWriteLock();
		this.readMessageLock = messageLock.readLock();
		this.writeMessageLock = messageLock.writeLock();
		
		this.winnerLock = new ReentrantReadWriteLock();
		this.readWinnerLock = winnerLock.readLock();
		this.writeWinnerLock = winnerLock.writeLock();
		
		this.chatLock = new ReentrantReadWriteLock();
		this.readChatLock = chatLock.readLock();
		this.writeChatLock = chatLock.readLock();
		
		this.chipsAddedLock = new ReentrantReadWriteLock();
		this.readChipsAdded = chipsAddedLock.readLock();
		this.writeChipsAdded = chipsAddedLock.writeLock();
		
		this.startChipsLock = new ReentrantReadWriteLock();
		this.readStartChipsLock = startChipsLock.readLock();
		this.writeStartChipsLock = startChipsLock.writeLock();
		
		this.anteLock = new ReentrantReadWriteLock();
		this.readAnteLock = anteLock.readLock();
		this.writeAnteLock = anteLock.writeLock();
		
		this.handSentLock = new ReentrantReadWriteLock();
		this.readHandsSentLock = handSentLock.readLock();
		this.writeHandsSentLock = handSentLock.writeLock();
		
		this.allInLock = new ReentrantReadWriteLock();
		this.readAllInLock = allInLock.readLock();
		this.writeAllInLock = allInLock.writeLock();
		
		this.finalHandLock = new ReentrantReadWriteLock();
		this.writeFinalHandLock = finalHandLock.writeLock();
		
		this.contributionMapLock = new ReentrantReadWriteLock();
		this.writeContributionMapLock = this.contributionMapLock.writeLock();
		
		this.noPlayers = playerCount;
		
		//A separate queue of information for each of the player clients to read from
		messageQueue = new ArrayList<ArrayList<String>>();
		for(int x = 0;x < noPlayers;x++) {
			messageQueue.add(new ArrayList<String>());
		}
		
		chatQueue = new ArrayList<ArrayList<String>>();
		for(int x = 0;x < noPlayers;x++) {
			chatQueue.add(new ArrayList<String>());
		}
		
		totalContributions = new HashMap<>();
		foldedPlayers = new ArrayList<>();
		
		for(int x = 0;x < DealerServer.noPlayers;x++) {
			this.playerWinnings.add(0);
			this.eligiblePlayers.put(x, "LIVE " + x);
		}
		
		nameMap = new HashMap<>();
		
	}

	@Override
	public void run() {

		this.stage = "BET";

		DealerServer.serverStart = true;

		this.addMessage("Ante is: " + this.getAnte());
		
		while (true) {
			
			
			//System.out.println(this.getStage());
			//System.out.println("Turn: " + this.getPlayerTurn());
			
			if(DealerServer.killApp) {
				DealerServer.playersKilled++;
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//System.out.println("Players reset: " + playersReset + " no players " + DealerServer.noPlayers);
			if(playersReset >= DealerServer.noPlayers) {
				this.setStage("BET");
				playersReset = 0;
			}
			
			if((DealerServer.noPlayers - this.getPlayersFolded()) == 1 && enteredEnd == false) {
								
				System.out.println("Resetting");
				
				DealerServer.deck = new Deck();
				DealerServer.deck.newDeck();
				DealerServer.deck.shuffle();
				
				smallestKey = 0;
				largestKey = 0;
				this.setPlayerTurn(0);
				this.playersAllIn = 0;
				this.playersSwapped = 0;
				this.setContinueBetting(true);
				this.setPlayersCalled(0);
				this.setPlayersChecked(0);
				this.setPlayersFolded(0);
				this.setHighestBet(-1);
				this.setHighestContribution(-1);
				this.handsSent = 0;
				insideShowDown = false;
				this.foldedPlayers.clear();
				this.totalContributions.clear();
				this.eligiblePlayers.clear();
				this.playerWinnings.clear();
				this.playersReady = 0;
				
				for(int x = 0;x < DealerServer.game.noPlayers;x++) {
					this.playerWinnings.add(0);
					this.eligiblePlayers.put(x, "LIVE " + x);
				}
								
				this.currentPot = 0;
				
				winner = -1;
				finalHands.clear();
				
				this.betTwo = false;	
				
				this.setStage("ALLFOLDED");
							
				while(!this.isChipsAdded()){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Holding");
				}
				
				this.setPot(0);
				chipsAdded = false;
				enteredEnd = true;
				
				counter = 5;
				
				Timer t = new Timer();
				t.schedule(new TimerTask() {
				    @Override
				    public void run() {
				       DealerServer.game.addMessage("Restarting in: " + counter);
				       counter--;
				       if(counter == 0) {
				    	   this.cancel();
				       }
				    }
				}, 0, 1000);
				
				while(counter > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//System.out.println(counter);
				}
				
				this.addMessage("RESTARTING");
				
				this.addMessage("Ante is: " + this.getAnte());
				
			}
			else if (this.getStage().equals("BET")) {
				//System.out.println("Player Turn: " + this.getPlayerTurn() + " Checked players: " + this.getPlayersChecked() + " Live Players: " + (DealerServer.game.noPlayers - (this.getPlayersFolded() + this.getPlayersAllIn())));
				//Check to see whether the game stage be advanced
			    if (this.getPlayersCalled() >= (DealerServer.game.noPlayers - (this.getPlayersFolded() + this.getPlayersAllIn()))
						|| this.getPlayersChecked() >= (DealerServer.game.noPlayers - (this.getPlayersFolded() + this.getPlayersAllIn()))) {
			    	
					
					if(betTwo == false){
						this.setStage("SWAP");
					}
					else if(betTwo == true){
						this.setStage("SHOWDOWN");
					}
					this.setPlayerTurn(0);
					this.setContinueBetting(false);
					this.setPlayersCalled(0);
					this.setPlayersChecked(0);

					this.setHighestBet(-1);
					this.setHighestContribution(-1);
				}

			}
			else if(this.getStage().equals("SWAP")){
				
				betTwo = true;
				this.setContinueBetting(true);
		
			}
			else if(this.getStage().equals("SHOWDOWN") && insideShowDown == false){
				
				System.out.println("Starting showdown");
				
				bustedReset = false;
				
				boolean handsCopied = false;
				boolean mainPot = true;
				boolean anteAdded = false;
				int smallestContribution = (this.startingChips * DealerServer.noPlayers) + 1;
				
				insideShowDown = true;
				this.setPlayerTurn(0);
				LinkedHashMap<Integer, String> handCopy = new LinkedHashMap<>();
				
				while(true) {
					
					if(handsCopied) {
						finalHands.putAll(handCopy);
					}
					
					if(this.getHandsSent() == (DealerServer.game.noPlayers - this.getPlayersFolded())) {
						
						this.currentPot = 0;
												
						if(!handsCopied) {
							handsCopied = true;
							handCopy.putAll(finalHands);
						}
						
						//Find the smallest stack
						for(int x = 0;x < noPlayers;x++) {
							//System.out.println("Contribution is: " + this.totalContributions.get(x));
							if(this.totalContributions.get(x) < smallestContribution && this.eligiblePlayers.get(x).startsWith("LIVE")) {
								smallestContribution = this.totalContributions.get(x);
							}
						}
							
						//If folded add all that players stake into the main pot
						//Otherwise subtract the smallest stack from the stake of all other players
						for(int x = 0;x < noPlayers;x++) {
							if(this.foldedPlayers.contains(x)) {
								this.pot -= this.totalContributions.get(x);
								this.currentPot += this.totalContributions.get(x);
								this.totalContributions.put(x, 0);
							}
							else if(this.totalContributions.get(x) > 0) {
								this.totalContributions.put(x, (this.totalContributions.get(x) - smallestContribution));
								this.pot -= smallestContribution;
								this.currentPot += smallestContribution;
							}
						}
						
						if(!anteAdded) {
							anteAdded = true;
							this.currentPot += (ante * this.noPlayers);
							this.pot -= (ante * this.noPlayers);
						}
																			
						ArrayList<Integer> keysToDelete = new ArrayList<>();
												
					    Iterator it2 = eligiblePlayers.entrySet().iterator();
					    while (it2.hasNext()) {
					        Map.Entry pair = (Map.Entry)it2.next();

					        if(!((String)pair.getValue()).startsWith("LIVE")) {
					        	//System.out.println(pair.getValue());
					        	keysToDelete.add((int)pair.getKey());
					        }
					    }
						//System.out.println("Smallest key: " + smallestKey + " Largest key: " + largestKey);
						//System.out.println("Hands sent " + this.finalHands.size());
						//Compare all the hands aganst each other
						//If a hand loses even one comparison it loses overall
						for(int x = smallestKey;x <= largestKey;x++) {
							for(int y = largestKey;y >= smallestKey;y--) {
								if(x != y && finalHands.get(x) != null && finalHands.get(y) != null && this.eligiblePlayers.get(x).startsWith("LIVE") && this.eligiblePlayers.get(y).startsWith("LIVE")) {
									
									//System.out.println("Hand one " + finalHands.get(x));
									//System.out.println("Hand two " + finalHands.get(y));
									
									String[] tokens = finalHands.get(x).split(" ");
								    String[] tokens2 = finalHands.get(y).split(" ");
								    
								    int index = Integer.parseInt(tokens[0]);
								    
								    String hand1 = "";
								    String hand2 = "";
								    
								    for(int z = 1;z < tokens.length;z++) {
								    	hand1 += tokens[z] + " ";
								    	hand2 += tokens2[z] + " ";
								    }
									
								    hand1.trim();
								    hand2.trim();
								    
								    PokerHand pokerHand1 = new PokerHand(hand1);
								    PokerHand pokerHand2 = new PokerHand(hand2);
								    								    
									if(pokerHand1.compareWith(pokerHand2).equals("LOSS")) {
										keysToDelete.add(index);
									}
									
								}
							}
						}
						
						for(int x = 0;x < keysToDelete.size();x++) {
							finalHands.remove(keysToDelete.get(x));
						}
								
						//System.out.println("Final hand size: " + finalHands.size());
						if(finalHands.size() != 1) {
							this.tiedPlayers = finalHands.keySet();
							String potType = "";
							if(mainPot) {
								potType = "for the main pot";
							}
							else {
								potType = "for a side pot";
							}
							for(int index: tiedPlayers) {
								this.addMessage("Player " + nameMap.get(index) + " was part of a tie " + potType + " and won " + (currentPot / tiedPlayers.size()) + " chips");
								this.playerWinnings.set(index, this.playerWinnings.get(index) + (currentPot / tiedPlayers.size()));

							}
						}
						else {
						    Iterator it = finalHands.entrySet().iterator();
						    while (it.hasNext()) {
						        Map.Entry pair = (Map.Entry)it.next();
						        if(mainPot) {
						        	this.addMessage("THE WINNER OF THE MAIN POT IS PLAYER " + nameMap.get(pair.getKey()) + " WITH A STACK OF " + currentPot);
						        	this.playerWinnings.set((int)pair.getKey(), this.playerWinnings.get((int)pair.getKey()) + currentPot);
						        }
						        else {
						        	this.addMessage("THE WINNER OF THE SIDE POT IS PLAYER " + nameMap.get(pair.getKey()) + " WITH A STACK OF " + currentPot);
						        	this.playerWinnings.set((int)pair.getKey(),this.playerWinnings.get((int)pair.getKey()) + currentPot);
						        }
						    }
						}
						
					    Iterator it = totalContributions.entrySet().iterator();
					    ArrayList<Integer> playersToDelete = new ArrayList<>();
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        if((int)pair.getValue() == 0) {
					        	playersToDelete.add((int)pair.getKey());
					        }
					    }
					    
					    for(int x = 0;x < playersToDelete.size();x++) {
					    	this.eligiblePlayers.put(playersToDelete.get(x), "INELIGIBLE");
					    }
						
					    //Return winnings to a player who has overbet
					    it = totalContributions.entrySet().iterator();
					    int counter = 0;
					    int playerIndex = 0;
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        if((int)pair.getValue() > 0) {
					        	counter++;
					        	playerIndex = (int)pair.getKey();
					        }
					    }
					    
						if(counter == 1 && pot > 0) {
							this.playerWinnings.set(playerIndex, this.playerWinnings.get(playerIndex) + this.pot);
							this.totalContributions.put(playerIndex, this.totalContributions.get(playerIndex) - this.pot);
							this.addMessage(this.pot + " chips returned to player " + nameMap.get(playerIndex));
							this.pot -= this.pot;
						}
						
						//Once all winnings have been distributed
						if(pot == 0) {
							this.setWinner(1);
							break;
						}
						mainPot = false;
						
					}		
					
				}
				
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//System.out.println("Expected Num: " + DealerServer.noPlayers + " Actual: " + this.getPlayersFolded());
					if(DealerServer.game.noPlayers == this.getPlayersFolded()) {
						break;
					}
				}
				
				this.bustedPlayers.clear();
					
				int tempFold = this.getPlayersFolded() + this.bustedPlayerCount;
				//System.out.println("Expected Num: " + 1 + " Actual: " + (DealerServer.noPlayers - (tempFold - 1)));
				this.setPlayersFolded(tempFold - 1);
				enteredEnd = false;
			}

		}

	}

	public int getPlayerTurn() {

		try {
			this.readTurnLock.lock();
			return playerTurn;
		} finally {
			this.readTurnLock.unlock();
		}

	}

	public void setPlayerTurn(int playerTurn) {

		try {
			this.writeTurnLock.lock();
			this.playerTurn = playerTurn;
		} finally {
			this.writeTurnLock.unlock();
		}

	}

	public void incrementPlayerTurn() {

		try {
			this.writeTurnLock.lock();
			this.playerTurn++;
		} finally {
			this.writeTurnLock.unlock();
		}

	}

	public int getPot() {

		try {
			this.readPotLock.lock();
			return pot;

		} finally {
			this.readPotLock.unlock();
		}

	}

	public void setPot(int pot) {

		try {
			this.writePotLock.lock();
			this.pot = pot;
		} finally {
			this.writePotLock.unlock();
		}

	}

	public void increasePot(int pot) {

		try {
			this.writePotLock.lock();
			this.pot += pot;
		} finally {
			this.writePotLock.unlock();
		}

	}

	public String getStage() {

		try {
			this.readStageLock.lock();
			return stage;

		} finally {
			this.readStageLock.unlock();
		}

	}

	public void setStage(String stage) {

		try {
			this.writeStageLock.lock();
			this.stage = stage;

		} finally {
			this.writeStageLock.unlock();
		}

	}

	public boolean isContinueBetting() {

		try {
			this.readBetLock.lock();
			return continueBetting;
		} finally {
			this.readBetLock.unlock();
		}

	}

	public void setContinueBetting(boolean continueBetting) {

		try {
			this.writeBetLock.lock();
			this.continueBetting = continueBetting;
		} finally {
			this.writeBetLock.unlock();
		}

	}

	public int getPlayersChecked() {

		try {
			this.readCheckLock.lock();
			return playersChecked;

		} finally {
			this.readCheckLock.unlock();
		}

	}

	public void setPlayersChecked(int playersChecked) {

		try {
			this.writeCheckLock.lock();
			this.playersChecked = playersChecked;

		} finally {
			this.writeCheckLock.unlock();
		}

	}
	
	public void incrementPlayersChecked() {

		try {
			this.writeCheckLock.lock();
			this.playersChecked++;

		} finally {
			this.writeCheckLock.unlock();
		}

	}

	public int getPlayersFolded() {

		try {
			this.readFoldLock.lock();
			return playersFolded;

		} finally {
			this.readFoldLock.unlock();
		}

	}

	public void setPlayersFolded(int playersFolded) {

		try {
			this.writeFoldLock.lock();
			this.playersFolded = playersFolded;

		} finally {
			this.writeFoldLock.unlock();
		}

	}
	
	public void incrementPlayersFolded() {

		try {
			this.writeFoldLock.lock();
			this.playersFolded++;

		} finally {
			this.writeFoldLock.unlock();
		}

	}

	public int getHighestContribution() {

		try {
			this.readContributionLock.lock();
			return highestContribution;
		} finally {
			this.readContributionLock.unlock();
		}

	}

	public void setHighestContribution(int highestContribution) {

		try {
			this.writeContributionLock.lock();
			this.highestContribution = highestContribution;

		} finally {
			this.writeContributionLock.unlock();
		}

	}

	public int getPlayersCalled() {

		try {
			this.readCallLock.lock();
			return playersCalled;

		} finally {
			this.readCallLock.unlock();
		}

	}

	public void setPlayersCalled(int playersCalled) {

		try {
			this.writeCallLock.lock();
			this.playersCalled = playersCalled;

		} finally {
			this.writeCallLock.unlock();
		}

	}

	public void incrementPlayersCalled() {

		try {
			this.writeCallLock.lock();
			this.playersCalled++;

		} finally {
			this.writeCallLock.unlock();
		}

	}

	public int getHighestBet() {

		try {
			this.readHighbetLock.lock();
			return highestBet;

		} finally {
			this.readHighbetLock.unlock();
		}

	}

	public void setHighestBet(int highestBet) {

		try {
			this.writeHighbetLock.lock();
			this.highestBet = highestBet;

		} finally {
			this.writeHighbetLock.unlock();
		}

	}
	
	public void addMessage(String message) {
		
		try {
			this.writeMessageLock.lock();
			for(int x = 0;x < messageQueue.size();x++) {
				messageQueue.get(x).add(message);
			}
		}
		finally {
			this.writeMessageLock.unlock();
		}
		
	}
	
	public String getMessage(int playerNum) {
		try {
			this.writeMessageLock.lock();
			String msg = messageQueue.get(playerNum).get(0);
			messageQueue.get(playerNum).remove(0);
			return msg;
		}
		finally {
			this.writeMessageLock.unlock();
		}
	}
	
	public boolean isMessageEmpty(int playerNum) {
		
		try {
			this.readMessageLock.lock();
			if(messageQueue.get(playerNum).size() == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		finally {
			this.readMessageLock.unlock();
		}
		
	}

	public int getWinner() {
		try{
			this.readWinnerLock.lock();
			return winner;
		}
		finally{
			this.readWinnerLock.unlock();
		}
	}

	public void setWinner(int winner) {
		try{
			this.writeWinnerLock.lock();
			this.winner = winner;
		}
		finally{
			this.writeWinnerLock.unlock();
		}
	}
	
	public void addChat(String message) {
		
		try {
			this.writeChatLock.lock();
			for(int x = 0;x < chatQueue.size();x++) {
				chatQueue.get(x).add(message);
			}
		}
		finally {
			this.writeChatLock.unlock();
		}
		
	}
	
	public String getChat(int playerNum) {
		try {
			this.writeChatLock.lock();
			String msg = chatQueue.get(playerNum).get(0);
			chatQueue.get(playerNum).remove(0);
			return msg;
		}
		finally {
			this.writeChatLock.unlock();
		}
	}
	
	public boolean isChatEmpty(int playerNum) {
		
		try {
			this.readChatLock.lock();
			if(chatQueue.get(playerNum).size() == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		finally {
			this.readChatLock.unlock();
		}
		
	}

	public boolean isChipsAdded() {
		try{
			this.readChipsAdded.lock();
			return chipsAdded;
		}
		finally{
			this.readChipsAdded.unlock();
		}
	}

	public void setChipsAdded(boolean chipsAdded) {
		try{
			this.writeChipsAdded.lock();
			this.chipsAdded = chipsAdded;
		}
		finally{
			this.writeChipsAdded.unlock();
		}
	}

	public int getStartingChips() {
		try {
			this.readStartChipsLock.lock();
			return startingChips;
		}
		finally {
			this.readStartChipsLock.unlock();
		}
	}

	public void setStartingChips(int startingChips) {
		try {
			this.writeStartChipsLock.lock();
			this.startingChips = startingChips;
		}
		finally {
			this.writeStartChipsLock.unlock();
		}
	}
	
	public void decrementStartingChips(int ante) {
		try {
			this.writeStartChipsLock.lock();
			this.startingChips -= ante;
		}
		finally {
			this.writeStartChipsLock.unlock();
		}
	}

	public int getAnte() {
		try {
			this.readAnteLock.lock();
			return ante;
		}
			
		finally {
			this.readAnteLock.unlock();
		}
	}
		

	public void addFinalHand(int id, String hand) {
		try {
			this.writeFinalHandLock.lock();
			if(id < smallestKey) {
				smallestKey = id;
			}
			if(id > largestKey) {
				largestKey = id;
			}
			this.finalHands.put(id, hand);
		}
		finally {
			this.writeFinalHandLock.unlock();
		}
		
	}
	
	public int getHandsSent() {
		try {
			this.readHandsSentLock.lock();
			return handsSent;
		}
		finally {
			this.readHandsSentLock.unlock();
		}
	}

	public void incrementHandsSent() {
		try {
			this.writeHandsSentLock.lock();
			this.handsSent++;
		}
		finally {
			this.writeHandsSentLock.unlock();
		}
	}

	public int getPlayersAllIn() {
		try{
			this.readAllInLock.lock();
			return playersAllIn;
		}
		finally{
			this.readAllInLock.unlock();
		}
	}

	public void incrementPlayersAllIn() {
		try{
			this.writeAllInLock.lock();
			this.playersAllIn++;
		}
		finally{
			this.writeAllInLock.unlock();
		}
	}
	
	public void setTotalContribution(int index,int value) {
		try {
			this.writeContributionMapLock.lock();
			this.totalContributions.put(index, value);
		}
		finally {
			this.writeContributionMapLock.unlock();
		}
	}
	
	

}
