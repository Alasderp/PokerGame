package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Deck {

	private ArrayList<String> deck;

	private String[] suits = { "H", "S", "D", "C" };
	private static String[] ranks = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

	
	private ReadWriteLock deckLock;
	private Lock writeDeckLock;
	
	public Deck() {
		this.deck = new ArrayList<String>();
	}

	public ArrayList<String> getDeck() {
		return this.deck;
	}

	public ArrayList<String> newDeck() {
		
		deckLock = new ReentrantReadWriteLock();
		writeDeckLock = deckLock.writeLock();

		this.deck = new ArrayList<String>();

		for (int x = 0; x < suits.length; x++) {
			for (int y = 0; y < ranks.length; y++) {
				deck.add(ranks[y] + suits[x]);
			}
		}

		return deck;

	}

	public ArrayList<String> shuffle() {
		Collections.shuffle(deck);
		return deck;
	}

	public String pop() {
		try {
			writeDeckLock.lock();
			String topCard = deck.get(deck.size() - 1);
			deck.remove(deck.size() - 1);
			return topCard;
		}
		finally {
			writeDeckLock.unlock();
		}
	}

}
