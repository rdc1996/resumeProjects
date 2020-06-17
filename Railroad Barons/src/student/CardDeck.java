package student;

import model.Card;
import model.Deck;

import java.util.*;

public class CardDeck implements Deck {

    private Queue<Card> cards;

    public CardDeck() {
        cards = new LinkedList<>();
        this.createDeck();
        this.shuffle();
    }

    @Override
    public void reset() {
        cards = new LinkedList<>();
        this.createDeck();
        this.shuffle();
    }

    @Override
    public Card drawACard() {
        if (cards.size() != 0) {
            return cards.poll();
        }
        return Card.NONE;
    }

    @Override
    public int numberOfCardsRemaining() {
        return cards.size();
    }

    public void shuffle() {
        List list = new ArrayList(cards);
        Collections.shuffle(list);
        cards = new LinkedList<>(list);
    }

    public void createDeck() {
        for (int i = 0; i < 20; i++) {
            for (Card c: Card.values()) {
                if (!(c.equals(Card.NONE)) && !(c.equals(Card.BACK))) {
                    cards.add(c);
                }
            }
        }
    }
}
