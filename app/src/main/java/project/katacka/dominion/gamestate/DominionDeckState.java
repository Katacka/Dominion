package project.katacka.dominion.gamestate;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Holds state information for all cards a player posseses: draw, hand, and discard.
 * Provides helper methods to automatically access these cards.
 *
 * @author Ryan Regier, Julian Donovan, Hayden Liao, Ashika Mulagada
 */
public class DominionDeckState {

    private final ArrayList<project.katacka.dominion.gamestate.DominionCardState> draw;
    private final ArrayList<project.katacka.dominion.gamestate.DominionCardState> discard;
    private final ArrayList<project.katacka.dominion.gamestate.DominionCardState> hand;

    public DominionDeckState(int startSize){
        draw = new ArrayList<>(startSize);
        discard = new ArrayList<>(startSize);
        hand = new ArrayList<>(10);
    }

    public DominionDeckState(DominionDeckState deckState, boolean isRealDeck){

        if(isRealDeck){
            this.draw = new ArrayList<>(deckState.draw);
            this.discard = new ArrayList<>(deckState.discard);
            this.hand = new ArrayList<>(deckState.hand);

            /*
            this.draw.addAll(deckState.draw);
            this.discard.addAll(deckState.discard);
            this.hand.addAll(deckState.hand);
            */

        } else {
            this.draw = new ArrayList<project.katacka.dominion.gamestate.DominionCardState>();
            this.discard = new ArrayList<project.katacka.dominion.gamestate.DominionCardState>();
            this.hand = new ArrayList<project.katacka.dominion.gamestate.DominionCardState>();

            for(project.katacka.dominion.gamestate.DominionCardState blankCard: deckState.draw){
                this.draw.add(new project.katacka.dominion.gamestate.DominionCardState());
            }
            for(project.katacka.dominion.gamestate.DominionCardState blankCard: deckState.discard){
                this.discard.add(blankCard);
                this.draw.add(new project.katacka.dominion.gamestate.DominionCardState());
            }
            for(project.katacka.dominion.gamestate.DominionCardState blankCard: deckState.hand){
                this.hand.add(blankCard);
                this.draw.add(new project.katacka.dominion.gamestate.DominionCardState());
            }
        }
    }

    public int getHandSize(){ return  hand.size(); }

    /**
     * Returns the top card from the draw pile, shuffling if necessary.
     * Revealed card remains at top of deck.
     *
     * @return The revealed card, or null if deck is empty
     */
    public project.katacka.dominion.gamestate.DominionCardState reveal(){
        if (draw.isEmpty()){
            if (discard.isEmpty()) {
                //Empty deck, cannot reveal card
                return null;
            }
            reshuffle();
        }

        int index = draw.size() - 1;
        project.katacka.dominion.gamestate.DominionCardState card = draw.get(index);
        return card;
    }

    /**
     * Removes the top card from the draw pile and returns it, shuffling if necessary.
     * Adds drawn card to hand.
     *
     * @return The drawn card, or null if deck is empty
     */
    public project.katacka.dominion.gamestate.DominionCardState draw(){
        project.katacka.dominion.gamestate.DominionCardState card = reveal();
        if (card == null){
            return null;
        }
        draw.remove(draw.size()-1);
        hand.add(card);
        return card;
    }

    public project.katacka.dominion.gamestate.DominionCardState[] drawMultiple(int drawNum){
        project.katacka.dominion.gamestate.DominionCardState[] drawnCards = new project.katacka.dominion.gamestate.DominionCardState[drawNum];
        for(int i = 0; i < drawNum; i++) drawnCards[i] = draw();
        return drawnCards;
    }

    /**
     * Gets the number of cards left in the draw pile
     * @return Number of cards in draw pile
     */
    public int getDrawSize(){
        return draw.size();
    }

    /**
     * Puts card in the discard pile.
     * This card will be removed from the hand, if it exists.
     * @param card The card to put in discard
     */
    public void discard(project.katacka.dominion.gamestate.DominionCardState card){
        discard.add(card);
        hand.remove(card);
    }

    public boolean discard(String cardName){
        for(project.katacka.dominion.gamestate.DominionCardState card : hand){
           if(card.getTitle().equals(cardName)) {
               discard.add(card);
               hand.remove(card);
               return true;
           }
        }
        return false;
    }

    /**
     * Adds a card to the discard pile.
     *
     * @param card The new card
     */
    public void discardNew(project.katacka.dominion.gamestate.DominionCardState card){
        discard.add(card);
    }

    /**
     * Add many copies of a card to the discard pile.
     * Used to create starter deck.
     * @param card CardView to add
     * @param count Number of cards to add
     */
    public void addManyToDiscard(project.katacka.dominion.gamestate.DominionCardState card, int count){
        for (int i = 0; i < count; i++){
            discard.add(card);
        }
    }

    /**
     * Discards all cards in hand.
     */
    public void discardAll(){
        discard.addAll(hand);

        hand.clear();
    }

    /**
     * Gets the card most recently added to the discard pile.
     * @return The last card, or null if discard is empty
     */
    public project.katacka.dominion.gamestate.DominionCardState getLastDiscard(){
        int size = discard.size();
        if (size == 0){
            return null;
        }

        return discard.get(size - 1);
    }

    /**
     * Gets number of cards in discard pile
     * @return Size of discard pile
     */
    public int getDiscardSize(){
        return discard.size();
    }

    /**
     * Puts all discard cards into the draw pile and randomizes order of cards.
     */
    public void reshuffle(){
        draw.addAll(discard);
        discard.clear();
        Collections.shuffle(draw);
    }

    public int getTotalCards(){
        return getDiscardSize() + getHandSize() + getDrawSize();
    }

    /**
     * Counts deck for number of victory points
     * @return Number of victory points
     */
    public int countVictory(){
        int totalCards = getTotalCards();
        return Stream.of(discard.parallelStream(), hand.parallelStream(), draw.parallelStream())
                .flatMap(s -> s)
                .mapToInt(s -> s.getVictoryPoints(totalCards))
                .sum();
    }

    /**
     * Creates an array of Strings from an ArrayList of cards.
     * Used for toString method to list cards in deck.
     *
     * @param array The array to store card titles in. Must be at least as big as {@code cards}
     * @param cards The array of cards to read from.
     */
    private void createCardArray(String[] array, ArrayList<project.katacka.dominion.gamestate.DominionCardState> cards){
        for (int i = 0; i < array.length; i++){
            array[i] = cards.get(i).getTitle();
        }
    }

    @Override
    public String toString(){

        String[] drawStr = new String[draw.size()];
        String[] discardStr = new String[discard.size()];
        String[] handStr = new String[hand.size()];

        createCardArray(drawStr, draw);
        createCardArray(discardStr, discard);
        createCardArray(handStr, hand);

        return String.format(Locale.US, "Deck\n\tDraw: %s\n\tDiscard: %s\n\tHand: %s",
                TextUtils.join(", ", drawStr), TextUtils.join(", ", discardStr),
                TextUtils.join(", ", handStr));
    }

    public ArrayList<project.katacka.dominion.gamestate.DominionCardState> getDraw() {
        return draw;
    }

    public ArrayList<project.katacka.dominion.gamestate.DominionCardState> getDiscard() {
        return discard;
    }

    public ArrayList<project.katacka.dominion.gamestate.DominionCardState> getHand() {
        return hand;
    }
}
