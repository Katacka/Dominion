package project.katacka.dominion.gamestate;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Holds state information for all cards a player posseses: draw, hand, and discard.
 * Provides helper methods to automatically access these cards.
 *
 * @author Ryan Regier, Julian Donovan, Hayden Liao, Ashika Mulagada
 */
public class DominionDeckState implements Serializable{

    private final ArrayList<DominionCardState> draw;
    private final ArrayList<DominionCardState> discard;
    private final ArrayList<DominionCardState> hand;
    private final ArrayList<DominionCardState> inPlay;

    /**
     * Constructor. Creates empty deck.
     */
    public DominionDeckState(){
        draw = new ArrayList<>(10);
        discard = new ArrayList<>(10);
        hand = new ArrayList<>(10);
        inPlay = new ArrayList<>(10);
    }

    /**
     * Copy constructor that obfuscates hidden cards
     * @param deckState The deck to copy
     * @param isRealDeck Whether to obfuscate hand
     */
    public DominionDeckState(DominionDeckState deckState, boolean isRealDeck){

        this.draw = new ArrayList<>(deckState.draw.size());
        this.discard = new ArrayList<>(deckState.discard.size());
        this.hand = new ArrayList<>(deckState.hand.size());
        this.inPlay = new ArrayList<>(deckState.inPlay.size());

        //Creates empty deck and discard, since they are not known to player

        for(int i = 0; i < deckState.draw.size(); i++){
            this.draw.add(DominionCardState.BLANK_CARD);
        }

        for(int i = 0; i < deckState.discard.size() - 1; i++){
            this.discard.add(DominionCardState.BLANK_CARD);
        }
        //Reveal the top of the discard pile, since it is face up
        if (deckState.discard.size() >= 1){
            this.discard.add(new DominionCardState(deckState.discard.get(deckState.discard.size()-1)));
        }

        //Show the player's hand if they are holding it, or blank cards otherwise
        if(isRealDeck){
            //Show hand
            for(DominionCardState card : deckState.hand){
                this.hand.add(new DominionCardState(card));
            }
        }
        else {
            //Make hand blank
            for(int i = 0; i < deckState.hand.size(); i++){
                this.hand.add(DominionCardState.BLANK_CARD);
            }
        }

        //Populate the in play cards
        for(DominionCardState card : deckState.inPlay){
            this.inPlay.add(new DominionCardState(card));
        }
    }

    /**
     * Get the number of cards in hand
     * @return The hand size
     */
    public int getHandSize(){ return  hand.size(); }

    /**
     * Returns the top card from the draw pile, shuffling if necessary.
     * Revealed card remains at top of deck.
     *
     * @return The revealed card, or null if deck is empty
     */
    public DominionCardState reveal(){
        if (draw.isEmpty()){
            if (discard.isEmpty()) {
                //Empty deck, cannot reveal card
                return null;
            }
            else {
                reshuffle();

            }
        }
        int index = draw.size() - 1;
        return draw.get(index);
    }

    /**
     * Removes the top card from the draw pile and returns it, shuffling if necessary.
     * Adds drawn card to hand.
     *
     * @return The drawn card, or null if deck is empty
     */
    public DominionCardState draw(){
        DominionCardState card = reveal();
        if (card == null){
            return null;
        }
        draw.remove(draw.size()-1);
        hand.add(card);
        return card;
    }

    /**
     * Draws multiple cards at once and adds them to hand.
     * @param drawNum The number of cards to draw. Will draw fewer if deck has fewer cards.
     */
    public void drawMultiple(int drawNum){
        for(int i = 0; i < drawNum; i++){
            DominionCardState card = draw();
            if (card == null) return; //Occurs if draw and discard empty. No reason to continue.
        }
    }

    /**
     * Gets the number of cards left in the draw pile
     * @return Number of cards in draw pile
     */
    public int getDrawSize(){
        return draw.size();
    }

    public boolean removeCard(String cardName){
        for(DominionCardState card : hand){
            if(card.getTitle().equals(cardName)) {
                hand.remove(card);
                return true;
            }
        }
        return false;
    }

    public boolean putInPlay(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.size()){
            return false;
        }
        else {
            DominionCardState card = hand.get(handIndex);
            inPlay.add(card);
            hand.remove(handIndex);
            return true;
        }
    }

    public DominionCardState getLastPlayed(){
        int size = inPlay.size();
        if (size == 0) return null;
        else return inPlay.get(size-1);
    }

    /**
     * Puts card in the discard pile.
     * This card will be removed from the hand, if it exists.
     * @param handIndex The index of the card in hand to put in discard
     * @return Whether the discard succeeds. Fails if index is out of bounds.
     */
    public boolean discard(int handIndex){
        if (handIndex < 0 || handIndex >= hand.size()){
            return false;
        }
        else {
            DominionCardState card = hand.get(handIndex);
            discard.add(card);
            hand.remove(handIndex);
            return true;
        }
    }

    /**
     * Tries to put a card in the discard pile.
     * Looks for first card in hand that matches the name.
     * @param cardName The name of the card to discard
     * @return true if card matching name was in the hand and has been discarded, false if it is not
     */
    public boolean discard(String cardName){
        for(DominionCardState card : hand){
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
    public void discardNew(DominionCardState card){
        discard.add(card);
    }

    /**
     * Add many copies of a card to the discard pile.
     * Used to create starter deck.
     * @param card Card to add
     * @param count Number of cards to add
     */
    public void addManyToDiscard(DominionCardState card, int count){
        for (int i = 0; i < count; i++){
            discard.add(card);
        }
    }

    /**
     * Discards all cards in hand.
     */
    public void discardAll(){
        discard.addAll(inPlay);
        discard.addAll(hand);

        inPlay.clear();
        hand.clear();
    }

    /**
     * Gets the card most recently added to the discard pile.
     * @return The last card, or null if discard is empty
     */
    public DominionCardState getLastDiscard(){
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

    /**
     * Get the number of cards in hand, draw, and discard.
     * @return The number of cards in the deck
     */
    public int getTotalCards(){
        return getDiscardSize() + getHandSize() + getDrawSize();
    }

    /**
     * Counts deck for number of victory points
     * @return Number of victory points
     */
    public int countVictory(){
        int totalCards = getTotalCards();
        return Stream.of(discard.stream(), hand.stream(), draw.stream())
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
    private void createCardArray(String[] array, ArrayList<DominionCardState> cards){
        for (int i = 0; i < array.length; i++){
            array[i] = cards.get(i).getTitle();
        }
    }

    /**
     * Displays deck as string
     * @return The string in question
     */
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

    public ArrayList<DominionCardState> getDraw() {
        return draw;
    }

    public ArrayList<DominionCardState> getDiscard() {
        return discard;
    }

    public ArrayList<DominionCardState> getHand() {
        return hand;
    }

    public ArrayList<DominionCardState> getInPlay() {
        return inPlay;
    }

    //Autogenerated methods


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DominionDeckState that = (DominionDeckState) o;
        return Objects.equals(draw, that.draw) &&
                Objects.equals(discard, that.discard) &&
                Objects.equals(hand, that.hand) &&
                Objects.equals(inPlay, that.inPlay);
    }

    @Override
    public int hashCode() {

        return Objects.hash(draw, discard, hand, inPlay);
    }
}
