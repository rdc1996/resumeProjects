package student;

import model.Card;
import model.Pair;

public class CardPair implements Pair {
    Card cardOne;
    Card cardTwo;

    public CardPair(Card cardOne, Card cardTwo){
        this.cardOne=cardOne;
        this.cardTwo=cardTwo;
    }
    @Override
    public Card getFirstCard() {
        return cardOne;
    }

    @Override
    public Card getSecondCard() {
        return cardTwo;
    }
}
