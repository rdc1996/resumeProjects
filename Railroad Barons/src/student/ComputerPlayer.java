package student;

import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to represent a computer player.
 */
public class ComputerPlayer implements Player {
    RailroadBarons game;
    RailMap map;
    private List<PlayerObserver> observers = new ArrayList<PlayerObserver>();
    private List<Route> claimedRoutes = new ArrayList<Route>();
    private List<Card> hand = new ArrayList<Card>();
    private List<Card> types = new ArrayList<Card>();
    private int score;
    private Card lastFirst;
    private Card lastSecond;
    private Baron baron;
    private int pieces;
    private List<Station> stationsLeft = new ArrayList<>();
    private List<Station> stationsBottom = new ArrayList<>();

    /**
     * Creates a computer player.
     *
     * @param game the instance of the game.
     * @param map the map that is being played on.
     * @param baron the baron of the player.
     */
    public ComputerPlayer(RailroadBarons game, RailMap map, Baron baron){
        this.game = game;
        this.map = map;
        this.baron = baron;
        pieces = 45;
        types.add(Card.BLACK);
        types.add(Card.BLUE);
        types.add(Card.GREEN);
        types.add(Card.ORANGE);
        types.add(Card.PINK);
        types.add(Card.RED);
        types.add(Card.WHITE);

        types.add(Card.YELLOW);
        types.add(Card.WILD);

//        Station first = Map.stations.get(0);
//        int greatestRow = first.getRow();
//
//        for (Station stat: Map.stations){
//            Station stats = (Station)stat;
//            if (stats.getRow()>greatestRow){
//                greatestRow = stats.getRow();
//            }
//        }
//
//        for (Station s : Map.stations) {
//            if (s.getCol() == 0) {
//                stationsLeft.add(s);
//            }
//            if (s.getRow() == (greatestRow + 1)) {
//                stationsBottom.add(s);
//            }
//        }
    }

    /**
     * Resets the players hand.
     *
     * @param dealt The hand of {@link Card cards} dealt to the player at the
     */
    @Override
    public void reset(Card... dealt) {
        hand.clear();
        pieces = 45;
        score = 0;
        claimedRoutes.clear();
        lastFirst = Card.NONE;
        lastSecond = Card.NONE;
        for (PlayerObserver o : observers){
            o.playerChanged(this);
        }


    }

    /**
     * Adds an observer to the list.
     *
     * @param observer The new {@link PlayerObserver}.
     */
    @Override
    public void addPlayerObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the list.
     *
     * @param observer The {@link PlayerObserver} to remove.
     */
    @Override
    public void removePlayerObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    /**
     * Gets the baron of the current player.
     *
     * @return
     */
    @Override
    public Baron getBaron() {
        return baron;
    }

    /**
     * Starts the turn of the current player.
     *
     * @param dealt a {@linkplain Pair pair of cards} to the player. Note that
     */
    @Override
    public void startTurn(Pair dealt) {
        System.out.println(this.getBaron() + " starts their turn");
        hand.add(dealt.getFirstCard());
        hand.add(dealt.getSecondCard());
        lastFirst = dealt.getFirstCard();
        lastSecond = dealt.getSecondCard();

        for (PlayerObserver o : observers){
            o.playerChanged(this);
        }

        System.out.println("Entering Loop the current baron is "+ game.getCurrentPlayer().getBaron());
        Baron mostRecentBaron = game.getCurrentPlayer().getBaron();
        if (game.getCurrentPlayer().getBaron() != Baron.BLUE) {
            for (Route r : game.getRailroadMap().getRoutes()) {
                if (this.canClaimRoute(r)) {
                    //System.out.println(this.baron + " can claim a route");
                    try {
                        if (r.getOrientation() == Orientation.HORIZONTAL) {
                            game.claimRoute(r.getOrigin().getRow(), r.getOrigin().getCol() + 1);
                            //game.endTurn();
                            break;

                        } else
                            game.claimRoute(r.getOrigin().getRow() + 1, r.getOrigin().getCol());
                            //game.endTurn();
                            break;

                    } catch (Exception e) {

                    }
                }
            }
            System.out.println(game.getCurrentPlayer().getBaron());
            if (game.getCurrentPlayer().getBaron()==mostRecentBaron)
                game.endTurn();


        }



    }

    /**
     * Gets the last two cards.
     *
     * @return the last two cards.
     */
    @Override
    public Pair getLastTwoCards() {
        return new CardPair(lastFirst,lastSecond);
    }

    /**
     * Counts a certain type of card in the players hand.
     *
     * @param card The {@link Card} of interest.
     * @return number of cards.
     */
    @Override
    public int countCardsInHand(Card card) {
        int count = 0;
        for (Card c : hand){
            if (c == card){
                count = count+1;
            }
        }
        return count;
    }

    /**
     * Gets the number of pieces.
     *
     * @return number of pieces.
     */
    @Override
    public int getNumberOfPieces() {
        return pieces;
    }

    @Override
    public boolean canClaimRoute(Route route) {
        if (route.getBaron() == Baron.UNCLAIMED){
            if (pieces >= route.getLength()) {
                int wild = this.countCardsInHand(Card.WILD);
                if (wild>1)
                    wild=1;
                for (Card c : types){
                    if (!(c.equals(Card.WILD))) {
                        int cardOne = this.countCardsInHand(c);
                        int length = route.getLength();
                        if (cardOne + wild >= length - 1){
                            return true;
                        }
                    }
                }
                return false;
            }
            else return false;
        }
        else return false;
    }

    @Override
    public void claimRoute(Route route) throws RailroadBaronsException {
        route.claim(this.getBaron());
        claimedRoutes.add(route);
        score = score + route.getPointValue();
        pieces = pieces - route.getLength() + 1;

        int wild =countCardsInHand(Card.WILD);
        if (wild>1)
            wild=1;
        cards:
        for (Card c : types) {
            if (countCardsInHand(c) + wild >= route.getLength()-1) {
                if(countCardsInHand(c)<route.getLength()-1)
                    hand.remove(Card.WILD);
                for(int i=0; i < route.getLength(); i++){
                    hand.remove(c);
                }
                break cards;
            }
        }
        for (PlayerObserver o : observers){
            o.playerChanged(this);
        }

    }

    @Override
    public Collection<Route> getClaimedRoutes() {
        return claimedRoutes;
    }

    @Override
    public int getScore() {
        int bonus=0;
        int greatestRow = game.getRailroadMap().getRows() - 1;
        for (Station s : Map.stations) {
            if (s.getCol() == 0) {
                if (!stationsLeft.contains(s)) {
                    stationsLeft.add(s);
                }
            }
            if (s.getRow() == greatestRow) {
                if (!stationsBottom.contains(s)) {
                    stationsBottom.add(s);
                }
            }
        }
        for (Station s : stationsBottom){
            if (RailMap.graph.pathExistsNS(s,0,this.baron, game)){
                bonus = game.getRailroadMap().getRows()*5;
                bonus = bonus-5;
            }
        }
        for (Station s : stationsLeft){
            if (RailMap.graph.pathExistsEW(s,game.getRailroadMap().getCols(),this.baron, game)){
                bonus = game.getRailroadMap().getCols()*5;
            }
        }
        return score+bonus;
    }

    @Override
    public boolean canContinuePlaying(int shortestUnclaimedRoute) {
        int wild = this.countCardsInHand(Card.WILD);
        if (wild != 0) {
            wild = 1;
        }
        for (Card c : types) {
            if (this.countCardsInHand(c) + wild >= shortestUnclaimedRoute && pieces >= shortestUnclaimedRoute) {
                return true;
            }
        }
        return false;
    }

}
