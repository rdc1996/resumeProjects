package student;

import model.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RailroadBaronLonely implements RailroadBarons {

    private List<RailroadBaronsObserver> observers = new ArrayList<>();
    private RailroadMap map;
    private List<Player> players = new ArrayList<>();
    private int currentPlayer = 0;
    private Deck deck;

    /**
     * Railroad Baron Lonely Constructor
     */
    public RailroadBaronLonely() {

        players.add(new PlayerClass(Baron.BLUE, this));
        //players.add(new PlayerClass(Baron.RED, this));
        players.add(new ComputerPlayer(this, (RailMap)this.getRailroadMap(), Baron.RED));
        players.add(new ComputerPlayer(this, (RailMap)this.getRailroadMap(), Baron.GREEN));
        players.add(new ComputerPlayer(this, (RailMap)this.getRailroadMap(), Baron.YELLOW));



        Collections.shuffle(players);

    }


    /**
     * adds observers
     * @param observer The {@link RailroadBaronsObserver} to add to the
     */
    @Override
    public void addRailroadBaronsObserver(RailroadBaronsObserver observer) {
        observers.add(observer);
    }

    /**
     * removes observers
     * @param observer The {@link RailroadBaronsObserver} to remove.
     */
    @Override
    public void removeRailroadBaronsObserver(RailroadBaronsObserver observer) {
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    /**
     * starts a game with a map
     * @param map The {@link RailroadMap} on which the game will be played.
     */
    @Override
    public void startAGameWith(RailroadMap map) {
        this.map = map;
        deck = new CardDeck();
        for (RailroadBaronsObserver r : observers) {
            r.turnStarted(this, this.getCurrentPlayer());
        }
        this.getCurrentPlayer().startTurn(new CardPair(deck.drawACard(), deck.drawACard()));
    }

    /**
     * starts a game with a map and a deck
     * @param map The {@link RailroadMap} on which the game will be played.
     * @param deck The {@link Deck} of cards used to play the game. This may
     *             be ANY implementation of the {@link Deck} interface,
     *             meaning that a valid implementation of the
     *             {@link RailroadBarons} interface should use only the
     */
    @Override
    public void startAGameWith(RailroadMap map, Deck deck) {
        this.map = map;
        this.deck = deck;
        for (RailroadBaronsObserver r : observers) {
            r.turnStarted(this, this.getCurrentPlayer());
        }
        this.getCurrentPlayer().startTurn(new CardPair(deck.drawACard(), deck.drawACard()));
    }

    /**
     * returns railroadMap
     * @return
     */
    @Override
    public RailroadMap getRailroadMap() {
        return this.map;
    }

    /**
     * gets the number of cards remaining
     * @return
     */
    @Override
    public int numberOfCardsRemaining() {
        return this.deck.numberOfCardsRemaining();
    }

    /**
     * returns whether or not a player can claim the given route
     * @param row The row of a {@link Track} in the {@link Route} to check.
     * @param col The column of a {@link Track} in the {@link Route} to check.
     * @return
     */
    @Override
    public boolean canCurrentPlayerClaimRoute(int row, int col) {
        Route route = this.map.getRoute(row, col);
        //return (route.getBaron().equals(Baron.UNCLAIMED));
        return players.get(currentPlayer).canClaimRoute(route);
        /*
        for (Route r : routes) {
            if (r.getOrigin().getRow() == row && r.getOrigin().getCol() == col) {
                if (r.getBaron().equals(Baron.UNCLAIMED)) {
                    return true;
                }
            }
            if (r.getDestination().getRow() == row && r.getDestination().getCol() == col) {
                if (r.getBaron().equals(Baron.UNCLAIMED)) {
                    return true;
                }
            }
            List<Track> tracks = r.getTracks();
            for (Track t : tracks) {
                if (t.getRow() == row && t.getCol() == col) {
                    if (r.getBaron().equals(Baron.UNCLAIMED)) {
                        return true;
                    }
                }
            }
        }
        return false; */
    }

    /**
     * claims a route
     * @param row The row of a {@link Track} in the {@link Route} to claim.
     * @param col The column of a {@link Track} in the {@link Route} to claim.
     * @throws RailroadBaronsException
     */
    @Override
    public void claimRoute(int row, int col) throws RailroadBaronsException {
        Route route = this.map.getRoute(row, col);
        Player current = this.getCurrentPlayer();
        current.claimRoute(route);
        route.claim(current.getBaron());
        this.endTurn();
        map.routeClaimed(route);
        RailMap.graph.connect(route.getOrigin(), route.getDestination());

    }

    /**
     * ends the turn
     */
    @Override
    public void endTurn() {
        System.out.println(this.getCurrentPlayer().getBaron() + " ends their turn");
        for (RailroadBaronsObserver r : observers) {
            r.turnEnded(this, this.getCurrentPlayer());
        }
        currentPlayer = currentPlayer + 1;
        if (currentPlayer == 4) {
            currentPlayer = 0;
        }
        for (RailroadBaronsObserver r : observers) {
            r.turnStarted(this, this.getCurrentPlayer());
        }
        this.getCurrentPlayer().startTurn(new CardPair(deck.drawACard(), deck.drawACard()));
        if (this.gameIsOver()) {
            Player winner = this.getCurrentPlayer();
            for (Player p : players) {
                if (p.getScore() >= winner.getScore()) {
                    winner = p;
                }
            }
            for (RailroadBaronsObserver r : observers) {
                r.gameOver(this, winner);
            }
        }
    }

    /**
     * returns the current player
     * @return
     */
    @Override
    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    /**
     * returns the collection of players
     * @return
     */
    @Override
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * returns if game is over
     * @return
     */
    @Override
    public boolean gameIsOver() {
        int shortest = map.getLengthOfShortestUnclaimedRoute();
        if (shortest == 99) {
//            Player winner = players.get(0);
//            for (Player p : players) {
//                if (p.getScore() > winner.getScore()) {
//                    winner = p;
//                }
//            }
//            for (RailroadBaronsObserver r : observers) {
//                r.gameOver(this, winner);
//            }
            return true;
        }
        int pieceCount = 0;
        for (Player p : players) {
            if (p.getNumberOfPieces() < shortest) {
                pieceCount += 1;
            }
        }
        if (pieceCount == 4) {
            return true;
        }
        int count = 0;
        if (deck.numberOfCardsRemaining() <= 1) {
            for (Player p : players) {
                int pieces = p.getNumberOfPieces();

                int wild = p.countCardsInHand(Card.WILD);
                if (wild > 1) {
                    wild = 1;
                }
                int black = p.countCardsInHand(Card.BLACK);
                int blue = p.countCardsInHand(Card.BLUE);
                int green = p.countCardsInHand(Card.GREEN);
                int orange = p.countCardsInHand(Card.ORANGE);
                int pink = p.countCardsInHand(Card.PINK);
                int red = p.countCardsInHand(Card.RED);
                int white = p.countCardsInHand(Card.WHITE);
                int yellow = p.countCardsInHand(Card.YELLOW);

                if (pieces >= shortest) {
                    if ((black + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((blue + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((green + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((orange + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((pink + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((red + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((white + wild) > shortest) {
                        count = count + 1;
                    }
                    if ((yellow + wild) > shortest) {
                        count = count + 1;
                    }
                }
            }
        }
        else {
            count = 1;
        }
//        if (count == 0) {
//            Player winner = players.get(0);
//            for (Player p : players) {
//                if (p.getScore() > winner.getScore()) {
//                    winner = p;
//                }
//            }
//            for (RailroadBaronsObserver r : observers) {
//                r.gameOver(this, winner);
//            }
//        }
        /*
        for (Route r : routes) {
            if (r.getBaron().equals(Baron.UNCLAIMED)) {
                count = count + 1;
            }
            for (Player p : players) {
                if (p.getNumberOfPieces() >= r.getLength()) {

                }
            }
        } */
        return (count == 0);
    }
}
