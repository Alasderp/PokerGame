package poker;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PokerHand {
	
    public enum Result { TIE, WIN, LOSS } 
    
    public static int counter = 0;
    
    public static boolean aceHi = false;

    private String player;

    PokerHand(String hand)
    {
      this.player = hand;
    }
    
    public String getHand() {
    	return this.player;
    }
    
    //Used to order a hand by value of card
    class CardComparator implements Comparator<String>{
    
    	public int compare(String cardOne, String cardTwo) {
    		
    		//Take the first character of a card (it's value)
    		String cardOneVal = cardOne.charAt(0) + "";
    		String cardTwoVal = cardTwo.charAt(0) + "";
    		
    		//Convert any face cards to a number
    		int oneNum = Integer.parseInt(checkFaceCard(cardOneVal, aceHi));
    		int twoNum = Integer.parseInt(checkFaceCard(cardTwoVal, aceHi));
    	
    		if(oneNum > twoNum) {
    			return 1;
    		}
    		else if(oneNum < twoNum) {
    			return -1;
    		}
    		else {
    			return 0;
    		}
    	
    	
    	}
    	
    	//Return the numeric value of any face cards
    	public String checkFaceCard(String card, Boolean aceHi) {
    		
	   		if(card.equals("A")) {
	    		if(aceHi) {
	    			return "14";
	    		}
	    		else {
	    			return "0";
	    		}
	    	}
	   		else if(card.equals("J")) {
	   			return "11";
	   		}
	   		else if(card.equals("Q")) {
	   			return "12";
	   		}
	   		else if(card.equals("K")) {
	   			return "13";
	   		}
	   		else if(card.equals("T")) {
	   			return "10";
	   		}
	   		
	   		return card;
	   		
    	}
    	
    }
    
    //Put aside in the case of ties
    public boolean playerHasEdge = false;
    public boolean trueTie = false;
    int playerHighest = 0;
    int opponentHighest = 0;

    public String compareWith(PokerHand hand) {
      
    	counter++;
    	
        playerHasEdge = false;
        trueTie = false;
        playerHighest = 0;
        opponentHighest = 0;
    	
    	//Stores the rank of the players best hand found so far
        int playerRank = 0;
        int opponentRank = 0;
        //Stores each car in the players hand and the number of occurrences
        HashMap<String,Integer> found = new HashMap<String,Integer>();
        HashMap<String,Integer> foundOpponent = new HashMap<String,Integer>();
      
        //Split the hand string into an array of cards
        String[] playerCards = this.player.split(" ");
        String[] opponentCards = hand.getHand().split(" ");
        
        //Finding pairs
        int playerPairs = 0;
        int opponentPairs = 0;
        
        //Order the hands by value
        List<String> playerList = Arrays.asList(playerCards);
        Collections.sort(playerList, new CardComparator());
     
        List<String> opponentList = Arrays.asList(opponentCards);
        Collections.sort(opponentList, new CardComparator());
        
        playerHighest = findHighest(playerList, opponentList);
        
        int playerHighestPair = 0;
        int opponentHighestPair = 0;
        
        String sortedPlayer = "";
        String sortedOpponent = "";
        for(int x = 0;x < playerList.size();x++) {
        	sortedPlayer += playerList.get(x) + " ";
        	sortedOpponent += opponentList.get(x) + " ";
        }
        
        //System.out.println("Players sorted hand: " + sortedPlayer);
        //System.out.println("Opponents sorted hand: " + sortedOpponent);
        
        //Finding pairs
        playerPairs = findPairs(playerCards, found);
        opponentPairs = findPairs(opponentCards, foundOpponent);
        
        if(playerPairs == 1){
          playerRank = 1;
        }
        if(playerPairs == 2){
          playerRank = 2;
        }
        
        if(opponentPairs == 1){
          opponentRank = 1;
        }
        if(opponentPairs == 2){
          opponentRank = 2;
        }
        
              
        //System.out.println("Player pairs: " + playerPairs + " Player Rank: " + playerRank);
        //System.out.println("Opponent pairs: " + opponentPairs + " Opponent Rank: " + opponentRank);
        
        //If tie highest pair wins
        if(playerRank == opponentRank) {

        	playerHighestPair = pairTiebreaker(found, 2);
        	opponentHighestPair = pairTiebreaker(foundOpponent, 2);
        	
        	if(playerHighestPair> opponentHighestPair) {
        		playerHasEdge = true;
        		trueTie = false;
        	}
        	else if(playerHighestPair < opponentHighestPair) {
        		playerHasEdge = false;
        		trueTie = false;
        	}
        	else {
        		trueTie = true;
        	}
        	
        	//System.out.println("Tiebreaker: \nPlayers highest pair: " + playerHighest + "\nOpponents highest pair: " + opponentHighest);
        	//System.out.println("Player has edge? " + playerHasEdge);
        	
            finalTie(playerHighest);

        }
                
        //Check for 3 and 4 of a kind
        int player3and4 = checkThreeAndFourKind(found);
        int opponent3and4 = checkThreeAndFourKind(foundOpponent);
        
        if(player3and4 == 3 || player3and4 == 4) {
        	playerRank = 3;
        	//System.out.println("Three of a kind? Yes Player rank: " + playerRank);
        }
        else {
        	//System.out.println("Three of a kind? No Player rank: " + playerRank);
        }
        if(opponent3and4 == 3 || opponent3and4 == 4) {
        	opponentRank = 3;
            //System.out.println("Three of a kind? Yes Opponent rank: " + opponentRank);
        }
        else {
        	//System.out.println("Three of a kind? No Opponent rank: " + opponentRank);
        }
        
        if(playerRank == 3 && opponentRank == 3) {
        	playerHighestPair = pairTiebreaker(found, 3);
        	opponentHighestPair = pairTiebreaker(foundOpponent, 3);
        	
        	if(playerHighestPair > opponentHighestPair) {
        		playerHasEdge = true;
        		trueTie = false;
        	}
        	else if(playerHighestPair < opponentHighestPair) {
        		playerHasEdge = false;
        		trueTie = false;
        	}
        	else {
        		trueTie = true;
        	}
        	
        	//System.out.println("Tiebreaker: \nPlayers highest three of a kind: " + playerHighest + "\nOpponents highest three of a kind: " + opponentHighest);
        	//System.out.println("Player has edge? " + playerHasEdge);
        	
        	finalTie(playerHighest);

        }
         
        //Find straights
        this.aceHi = true;
        
        playerList = Arrays.asList(playerCards);
        Collections.sort(playerList, new CardComparator());
        opponentList = Arrays.asList(opponentCards);
        Collections.sort(opponentList, new CardComparator());

        boolean playerStraight = checkStraight(playerList, true);
        boolean opponentStraight = checkStraight(opponentList, true);
        
        
        
        //Order the hands by value
        this.aceHi = false;  
        
        playerList = Arrays.asList(playerCards);
        Collections.sort(playerList, new CardComparator());
        opponentList = Arrays.asList(opponentCards);
        Collections.sort(opponentList, new CardComparator());
        
        if(!playerStraight) {
        	playerStraight = checkStraight(playerList, false);
        }
        if(!opponentStraight) {
        	opponentStraight = checkStraight(opponentList, false);
        }
        
        if(playerStraight) {
        	playerRank = 4;
        	//System.out.println("Player has a straight. Ranks is: " + playerRank);
        }
        else {
        	//System.out.println("Player has no straight. Ranks is: " + playerRank);
        }
        
        if(opponentStraight) {
        	opponentRank = 4;
        	//System.out.println("Opponent has a straight. Ranks is: " + opponentRank);
        }
        else {
        	//System.out.println("Opponent has no straight. Ranks is: " + opponentRank);
        }
        
        if(playerRank == 4 && opponentRank == 4) {
        	
        	finalTie(playerHighest);
        	
        	//System.out.println("Tiebreaker: \nPlayers highest card in straight: " + playerHighest + "\nOpponents highest card in straight: " + opponentHighest);
        	//System.out.println("Player has edge? " + playerHasEdge);
        }
        
        //Check for flush
        boolean playerFlush = findFlush(playerCards);
        boolean opponentFlush = findFlush(opponentCards);
        
        if(playerFlush) {
        	playerRank = 5;
        	//System.out.println("Player has a flush. Ranks is: " + playerRank);
        }
        else {
        	//System.out.println("Player has no flush. Ranks is: " + playerRank);
        }
        
        if(opponentFlush) {
        	opponentRank = 5;
        	//System.out.println("Opponent has a flush. Ranks is: " + opponentRank);
        }
        else {
        	//System.out.println("Opponent has no flush. Ranks is: " + opponentRank);
        }
        
        if(playerRank == 5 && opponentRank == 5) {
        	
        	finalTie(playerHighest);
        	
        	//System.out.println("Tiebreaker: \nPlayers highest card in flush: " + playerHighest + "\nOpponents highest card in flush: " + opponentHighest);
        	//System.out.println("Player has edge? " + playerHasEdge);

        }
        
        //Check for fullhouse
        boolean playerFullHouse = false;
        boolean opponentFullHouse = false;
        if(player3and4 == 3 && playerPairs == 2) {
        	playerFullHouse = true;
        	playerRank = 6;
        	//System.out.println("Player has full-house");
        }
        else {
        	//System.out.println("Player has no full-house");
        }
        if(opponent3and4 == 3 && opponentPairs == 2) {
        	opponentFullHouse = true;
        	opponentRank = 6;
        	//System.out.println("Opponent has full-house");
        }
        else {
        	//System.out.println("Opponent has no full-house");
        }
        
        if(playerRank == 6 && opponentRank == 6) {        	
        	finalTie(playerHighest);
        }
        
        //Check for four of a kind
        if(player3and4 == 4) {
        	playerRank = 7;
        	//System.out.println("Four of a kind? Yes Player rank: " + playerRank);
        }
        else {
        	//System.out.println("Four of a kind? No Player rank: " + playerRank);
        }
        if(opponent3and4 == 4) {
        	opponentRank = 7;
            //System.out.println("Four of a kind? Yes Opponent rank: " + opponentRank);
        }
        else {
        	//System.out.println("Four of a kind? No Opponent rank: " + opponentRank);
        }
        
        if(playerRank == 7 && opponentRank == 7) {
        	playerHighestPair = pairTiebreaker(found, 4);
        	opponentHighestPair = pairTiebreaker(foundOpponent, 4);
        	
        	if(playerHighestPair > opponentHighestPair) {
        		playerHasEdge = true;
        		trueTie = false;
        	}
        	else if(playerHighestPair < opponentHighestPair) {
        		playerHasEdge = false;
        		trueTie = false;
        	}
        	else {
        		trueTie = true;
        	}
        	
           	//System.out.println("Tiebreaker: \nPlayers highest four of a kind: " + playerHighest + "\nOpponents highest four of a kind: " + opponentHighest);
        	//System.out.println("Player has edge? " + playerHasEdge);
        	        	
        	finalTie(playerHighest);

        }
        
        //Check for straight flush
        boolean playerStraightFlush = false;
        if(playerFlush && playerStraight) {
        	playerRank = 8;
        	playerStraightFlush = false;
        	//System.out.println("Player has straight flush");
        }
        
        boolean opponentStraightFlush = false;
        if(opponentFlush && opponentStraight) {
        	opponentRank = 8;
        	opponentStraightFlush = false;
        	//System.out.println("Opponent has straight flush");
        }
        
        if(playerRank == 8 && opponentRank == 8) {        	
        	finalTie(playerHighest);
        }
        
        //Royal flush?
        
        
        //High card wins   
        this.aceHi = true;
        Collections.sort(opponentList, new CardComparator());
        Collections.sort(playerList, new CardComparator());
        playerHighest = this.findHighest(playerList, opponentList);
        if(playerRank == 0 && opponentRank == 0) {
        	
        	if(playerHighest == 1) {
        		playerHasEdge = true;
        		trueTie = false;
        		//System.out.println("High card wins. Player: " + playerHighest + " Opponent: " + opponentHighest);
        	}
        	else if(playerHighest == -1) {
        		playerHasEdge = false;
        		trueTie = false;
        		//System.out.println("High card wins. Player: " + playerHighest + " Opponent: " + opponentHighest);
        	}
        	else {
        		trueTie = true;
        		playerHasEdge = false;
        	}
        	
        	//System.out.println("True tie? " + trueTie);
        }   
        
        //System.out.println("!!COUNTER!!: " + counter);
        
        if(playerRank > opponentRank){
          return "WIN";
        }
        else if(opponentRank > playerRank){
          return "LOSS";
        }
        else if(trueTie) {
        	return "TIE";
        }
        else if(playerHasEdge) {
        	return "WIN";
        }
        else {
        	return "LOSS";
        }
        
    }
    
    public int findPairs(String[] cards, HashMap<String, Integer> found){
  
       int pairs = 0;
        
       for(int x = 0;x < cards.length;x++){
        
          String cardValue = "" + cards[x].charAt(0);
        
          if(found.get(cardValue) != null){
            	found.put(cardValue, found.get(cardValue) + 1); 
              if(found.get(cardValue) % 2 == 0){
                pairs++;
              }
          }
          else if(found.get(cardValue) == null){
            found.put(cardValue,1);
          }
        }
                       
          return pairs;
      
      } 
    
    public int pairTiebreaker(HashMap<String, Integer> found, int noDuplicates) {
      	 Iterator it = found.entrySet().iterator();
      	 int currentCard = 0;
      	 int highest = 0;
      	 
      	CardComparator converter = new CardComparator();
      	 
      	 while (it.hasNext()) {
  	         Map.Entry pair = (Map.Entry)it.next();
  	         //currentCard = Integer.parseInt((converter.checkFaceCard((String)pair.getKey(), true).charAt(0) + ""));
  	         currentCard = Integer.parseInt(converter.checkFaceCard(((String)pair.getKey()).charAt(0) + "", true));
  	         
  	         if((Integer)pair.getValue() >= noDuplicates && currentCard > highest) {
  	        	 highest = currentCard;
  	         }
  	         
      	 }
      	 
      	 return highest;
    }
    
     public int checkThreeAndFourKind(HashMap<String, Integer> found) {
    	 
    	 int value = 0;
    	 Iterator it = found.entrySet().iterator();
    	 while (it.hasNext()) {
	         Map.Entry pair = (Map.Entry)it.next();
	         if((Integer)pair.getValue() == 3 && value == 0) {
	        	 value = 3;
	         }
	         if((Integer)pair.getValue() == 4) {
	        	value = 4; 
	         }
	         
    	 }
    	 
    	 
    	 return value;
     }
     
     public boolean checkStraight(List<String> cards, Boolean aces) {
    	 
    	 boolean straight = true;
    	 int card = 0;
    	 int previousCard = 0;
    	 CardComparator converter = new CardComparator();
    	 
    	 for(int x = 1;x < cards.size();x++) {
    		 
    		 card = Integer.parseInt(converter.checkFaceCard(cards.get(x).charAt(0) + "", aces));
    		 previousCard = Integer.parseInt(converter.checkFaceCard(cards.get(x - 1).charAt(0) + "", aces));
    		 
    		 if(card != (previousCard + 1)) {
    			 straight = false;
    			 break;
    		 }
    		 
    	 }
    	 
    	 return straight;
    	 
     }
     
     public boolean findFlush(String[] cards) {
    	 
    	 String card = "";
    	 String previousCard = "";
    	 
    	 for(int x = 1;x < cards.length;x++) {
    		 card = cards[x].charAt(1) + "";
    		 previousCard = cards[x - 1].charAt(1) + "";
    		 if(!(card.equals(previousCard))) {
    			 return false;
    		 }
    	 }
    	 
    	 return true;
    	 
     }
     
     
     public int findHighest(List<String> playerCards, List<String> opponentCards) {
    	 int playerBetter = 0;
    	 int playerCard = 0;
    	 int opponentCard = 0;
    	 CardComparator converter = new CardComparator();
    	 
    	 for(int x = 0;x < playerCards.size();x++) {
    		 playerCard = Integer.parseInt(converter.checkFaceCard(playerCards.get(x).charAt(0) + "", true));
    		 opponentCard = Integer.parseInt(converter.checkFaceCard(opponentCards.get(x).charAt(0) + "", true));
    		     		 
    		 if(playerCard > opponentCard) {
    			 playerBetter = 1;
    		 }
    		 else if(playerCard < opponentCard) {
    			 playerBetter = -1;
    		 }

    	 }
    	 
    	 return playerBetter;
    	 
     }
     
     public void finalTie(int playerHighest) {
    	 
     	if(playerHighest == 1 && trueTie == true) {
    		playerHasEdge = true;
    		trueTie = false;
    	}
    	else if(playerHighest == -1 && trueTie == true) {
    		playerHasEdge = false;
    		trueTie = false;
    	}
    	else if(trueTie){
    		trueTie = true;
    		playerHasEdge = false;
    	}
    	
    	//System.out.println("True tie? " + trueTie);
     }    

}
