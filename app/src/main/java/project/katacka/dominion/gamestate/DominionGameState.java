package project.katacka.dominion.gamestate;


import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.Random;

import project.katacka.dominion.gameframework.infoMsg.GameState;

/**
 * A data class intended to represent the state of a game object
 * @author Ryan Regier, Julian Donovan, Ashika Mulagada, Hayden Liao
 */
public class DominionGameState extends GameState implements Serializable{

    /**
     * The six base cards, in the following order:
     *  0: Copper
     *  1: Estate
     *  2: Silver
     *  3: Duchy
     *  4: Gold
     *  5: Province
     */
    protected final ArrayList<DominionShopPileState> baseCards;
    protected final ArrayList<DominionShopPileState> shopCards;

    //Location of cards in base cards
    //Only need the locations of cards used in starter deck
    //  as well as providence to detect game over
    private final int PILE_COPPER = 0;
    private final int PILE_ESTATE = 1;
    private final int PILE_PROVIDENCE = 5;

    protected DominionPlayerState dominionPlayers[]; //Sorted by order of turn

    protected int currentTurn;
    protected int attackTurn; //Player ID of responder
    protected boolean isAttackTurn;
    protected boolean isGameOver;
    protected int playerQuit; //Used to identify which player exited. -1 describes no player having quit

    protected int numPlayers;

    protected int actions;
    protected int buys;
    protected int treasure;
    protected boolean silverBoon; //Merchant: First silver is worth 1 more

    private int emptyPiles;
    private boolean providenceEmpty = false;

    //RULE: With 2 players, 8 of each victory card should exist
    //      With 3-4 players, default to 12 copies of each victory card
    //The XML file uses the 3-4 player count, so this variable holds the pile size when only
    //  two players play
    private final int VICTORY_CARDS_2_PLAYER = 8;

    /**
     * Constructs a game state fully representing all objects, logical actions and players within a
     * Dominion game. A game state created in this way should be used as a master copy from which state
     * changes may be copied and obfuscated
     * @param paramNumPlayers The number of players playing
     * @param baseCardArray Describes the base cards available in the shop
     * @param shopCardArray Describes the unique cards available for purchase in the shop
     */
    public DominionGameState(int paramNumPlayers, ArrayList<DominionShopPileState> baseCardArray,
                             ArrayList<DominionShopPileState> shopCardArray) {

        //Updates shop amounts for 2 player game
        numPlayers = paramNumPlayers;
        if (numPlayers == 2) {
            //Base cards
            for (DominionShopPileState pile : baseCardArray) {
                if (pile.getCard().getType() == DominionCardType.VICTORY) {
                    pile.setAmount(VICTORY_CARDS_2_PLAYER);
                }
            }

            //Shop cards (needed for Gardens)
            for (DominionShopPileState pile : shopCardArray) {
                if (pile.getCard().getType() == DominionCardType.VICTORY) {
                    pile.setAmount(VICTORY_CARDS_2_PLAYER);
                }
            }
        }

        this.baseCards = baseCardArray;
        this.shopCards = shopCardArray;

        //Create the players
        this.dominionPlayers = new DominionPlayerState[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            this.dominionPlayers[i] = new DominionPlayerState("Player " + i,
                    baseCards.get(PILE_COPPER), //The copper pile
                    baseCards.get(PILE_ESTATE).getCard()); //The estate card

        }

        //Sets up turn with a random first player
        this.currentTurn = (new Random()).nextInt(numPlayers);
        this.treasure = 0;
        this.buys = 1;
        this.actions = 1;
        this.silverBoon = false;

        dominionPlayers[currentTurn].startTurn();

        this.isGameOver = false; //The game is not over
        this.playerQuit = -1; //No player has quit

        this.attackTurn = this.currentTurn;
        this.isAttackTurn = false;

        this.emptyPiles = 0;
    }

    /**
     * Constructs an obfuscated copy of a game's state, as purposed to send to players to inform of
     * update game state changes
     * @param gameState Relevant DominionGameState from which data will be gathered
     */
    public DominionGameState(DominionGameState gameState, int player){
        //Copies all base cards and shop cards
        this.baseCards= new ArrayList<>(gameState.baseCards.size());
        this.shopCards= new ArrayList<>(gameState.shopCards.size());

        for(DominionShopPileState basePileState: gameState.baseCards){
            this.baseCards.add(new DominionShopPileState(basePileState));
        }

        for(DominionShopPileState shopPileState: gameState.shopCards){
            this.shopCards.add(new DominionShopPileState(shopPileState));
        }

        this.numPlayers = gameState.numPlayers;
        this.dominionPlayers = new DominionPlayerState[this.numPlayers];

        //copy each player including the deckState
        for (int i = 0; i < this.numPlayers; i++) {
            this.dominionPlayers[i] = new DominionPlayerState(gameState.dominionPlayers[i],
                    player == i);
        }

        //Copy all other instance variables
        this.currentTurn = gameState.currentTurn;
        this.attackTurn = gameState.attackTurn;
        this.isAttackTurn = gameState.isAttackTurn;
        this.isGameOver = gameState.isGameOver;
        this.playerQuit = gameState.playerQuit;

        this.emptyPiles = gameState.emptyPiles;
        this.providenceEmpty = gameState.providenceEmpty;
        this.silverBoon = gameState.silverBoon;

        this.actions = gameState.actions;
        this.buys = gameState.buys;
        this.treasure = gameState.treasure;
    }

    @Override
    /**
     * Converts the game state to a String representation.
     * For debugging purposes.
     */
    public String toString() {

        //Strings that will be joined to form final String.
        String turnStr, batStr, boonStr, baseStr, shopStr, playerStr, emptyPilesStr, providenceEmptyStr, quitStr, gameOverStr;

        String attackString = "";
        if (isAttackTurn){
            attackString = String.format(Locale.US,
                    "An attack has been played. Player #%d is responding to the attack", attackTurn);
        }
        turnStr = String.format(Locale.US, "It is player #%d's turn. %s", currentTurn, attackString);

        batStr = String.format(Locale.US, "There are %d buys, %d actions, and %d treasure remaining.",
                buys, actions, treasure);

        boonStr = silverBoon ? "A silver boon is in effect.\n" : "";

        String[] baseStrs = new String[baseCards.size()];
        for (int i = 0; i < baseCards.size(); i++){
            baseStrs[i] = baseCards.get(i).toString();
        }

        /**
         * External Citation
         * Date: 10/7
         * Problem: Needed to turn array of strings into single array
         * Resource:
         *  https://stackoverflow.com/questions/1978933/a-quick-and-easy-way-to-join-array-elements-with-a-separator-the-opposite-of-sp
         * Solution: Used built-in Android helper function
         */
        baseStr = String.format(Locale.US, "\nThe base cards in the shop:\n%s",
                TextUtils.join("\n", baseStrs));

        String[] shopStrs = new String[shopCards.size()];
        for (int i = 0; i < shopCards.size(); i++){
            shopStrs[i] = shopCards.get(i).toString();
        }
        shopStr = String.format(Locale.US, "\nThe kingdom cards in the shop:\n%s",
                TextUtils.join("\n", shopStrs));

        String[] playerStrs = new String[dominionPlayers.length];
        for (int i = 0; i < dominionPlayers.length; i++){
            playerStrs[i] = dominionPlayers[i].toString();
        }
        playerStr = String.format(Locale.US, "There are %d players in the game:\n%s",
                dominionPlayers.length, TextUtils.join("\n", playerStrs));

        emptyPilesStr = String.format(Locale.US, "There are %d empty piles.", emptyPiles);

        providenceEmptyStr = providenceEmpty ? "The providence pile is empty.\n" : "";

        quitStr = playerQuit >= 0 ? "Player #" + playerQuit + " has quit the game." : "No player has quit the game.";

        if (isGameOver){
            gameOverStr = "The game is over.";
        } else {
            gameOverStr = "The game is not over.";
        }

        return String.format(Locale.US, "%s\n%s\n%s%s\n%s\n%s\n%s\n%s%s\n%s", turnStr, batStr,
                boonStr, baseStr, shopStr, playerStr, emptyPilesStr, providenceEmptyStr, quitStr, gameOverStr);
    }


    //Start of actions that can be performed by a player
    /**
     * Moves a card from the shop to a player's hand
     * @param playerID PlayerID in question, for which data will be found
     * @param cardIndex Relative location of the card one wishes to buy
     * @param isBaseCard Determines exists in the shop card group or base card group
     *
     * @return A boolean describing whether the card was successfully bought
     */
    public boolean buyCard(int playerID, int cardIndex, boolean isBaseCard){

        if (isLegalBuy(playerID, cardIndex, isBaseCard)) {
            DominionShopPileState cardPile;
            if (isBaseCard)
                cardPile = baseCards.get(cardIndex);
            else
                cardPile = shopCards.get(cardIndex);

            dominionPlayers[playerID].getDeck().discardNew(cardPile.getCard());
            cardPile.removeCard();
            buys--;
            treasure -= cardPile.getCard().getCost();
            if (cardPile.isEmpty()){
                emptyPiles++;
                if (isBaseCard && cardIndex == PILE_PROVIDENCE){
                    providenceEmpty = true;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Ends the player's turn, returning a boolean regarding the success
     * @param playerID PlayerID in question, for which data will be found
     *
     * @return A boolean describing whether the turn was successfully ended
     */
    public boolean endTurn(int playerID){
        if(!this.isGameOver && this.currentTurn == playerID) {
            if (emptyPiles >= 3 || providenceEmpty){
                isGameOver = true;
            } else {
                treasure = 0;
                buys = 1;
                actions = 1;
                DominionPlayerState currPlayer = dominionPlayers[currentTurn];
                currPlayer.getDeck().discardAll();
                currPlayer.getDeck().drawMultiple(5);
                currentTurn = (currentTurn + 1) % numPlayers;
                attackTurn = currentTurn;
                silverBoon = false;
                dominionPlayers[currentTurn].startTurn();
            }

            return true;
        }
        return false;
    }

    /**
     * Allows the user to quit the game so long as the game has not already been quit.
     * Returns a truth value describing the success of this action
     *
     * @param playerQuit The player who is choosing to quit the game
     * @return A boolean describing whether the game was successfully quit
     */
    public boolean quitGame(int playerQuit){
        if(!isGameOver) {
            this.isGameOver = true;
            this.playerQuit = playerQuit;
            return true;
        }
        return false;
    }

    /**
     * Plays a card, if legal, calling its action method and moving it to the discard pile.
     * Returns a boolean as to the success of this operation
     *
     * @return A boolean describing whether the selected card may legally be played
     */
    public boolean playCard(int playerID, int cardIndex){
        if(isLegalPlay(playerID, cardIndex)) {
            DominionDeckState deck = this.dominionPlayers[playerID].getDeck();
            DominionCardState card = deck.getHand().get(cardIndex);
            deck.discard(cardIndex); //Discarding before playing the card fixes cases like moneylender
            if(!card.cardAction(this)){
                return false;
            }
            else if(card.getType() == DominionCardType.ACTION){
                actions--;
            }

            return true;
        }
        return false;
    }

    /**
     * Action which will play every treasure card in player's hand.
     *
     * @param playerID The player performing the action. Must be their turn
     * @return Whether action completes successfully.
     */
    public boolean playAllTreasures(int playerID){
        if (canMove(playerID)){
            ArrayList<DominionCardState> hand = dominionPlayers[currentTurn].getDeck().getHand();

            //Loop through every card. Using custom loop, because hand changes as we iterate through it,
            //      breaking a regular for loop.
            int i = 0;
            while (i < hand.size()){
                DominionCardState card = hand.get(i);
                if (card.getType() == DominionCardType.TREASURE){
                    playCard(playerID, i);
                }
                else {
                    i++;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines whether a card may be legally played, considering the current player, the card's existence,
     * and if of type ACTION, the player's actions available. Returns a truth value.
     *
     * @return A boolean describing whether the selected card may legally be played
     */
    public boolean isLegalPlay(int playerID, int cardIndex) {
        if(canMove(playerID)) {
            DominionDeckState deck = dominionPlayers[playerID].getDeck();
            if (cardIndex >= 0 && cardIndex < deck.getHandSize()){
                DominionCardState card = deck.getHand().get(cardIndex);
                return card.getType() != DominionCardType.ACTION || actions > 0;
            }
        }
        return false;
    }

    /**
     * Determines whether a card may be legally bought, considering the current player, the card's existence,
     * and the player's buys available. Returns a truth value.
     *
     * @return A boolean describing whether the selected card may legally be bought
     */
    public boolean isLegalBuy(int playerID, int cardIndex, boolean baseCard) {
        if(canMove(playerID)){
            if (buys >= 1){ //Allowed to buy
                if (!baseCard && cardIndex >= 0 && cardIndex < shopCards.size()) { //Card pile exists
                    DominionShopPileState shopPile = shopCards.get(cardIndex);
                    if (!shopPile.isEmpty()){
                        return treasure >= shopPile.getCard().getCost();
                    }
                }
                else if (baseCard && cardIndex >= 0 && cardIndex < baseCards.size()){
                    DominionShopPileState basePile = baseCards.get(cardIndex);
                    if (!basePile.isEmpty()){
                        return treasure >= basePile.getCard().getCost();
                    }
                }
            }
        }
        return false;
    }

    public DominionPlayerState[] getDominionPlayers() {
        return dominionPlayers;
    }

    public int getActions() {
        return actions;
    }

    public int getBuys() {
        return buys;
    }

    public int getTreasure() {
        return treasure;
    }

    public ArrayList<DominionShopPileState> getBaseCards() {
        return baseCards;
    }

    public ArrayList<DominionShopPileState> getShopCards() {
        return shopCards;
    }

    /**
     * Checks if given player can perform actions
     *
     * @param player The index of the player trying to perform an action
     * @return Whether the player in question can perform an action
     */
    public boolean canMove(int player){
        if(isGameOver){
            return false;
        }
        if (isAttackTurn){
            return player == attackTurn;
        } else {
            return player == currentTurn;
        }
    }

    public boolean getGameOver(){
        return isGameOver;
    }

    /**
     * Gets the scores of every player
     * @return The list of scores
     */
    public int[] getPlayerScores(){
        int[] scores = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++){
            scores[i] = dominionPlayers[i].getDeck().countVictory();
        }

        return scores;
    }

    //In the case of a tie, this is populated with the indexes of all tied players.
    private int[] tiedPlayers;

    /**
     * Determines game winner. In case of tie, determines all winners.
     * Will only return accurate value on game over.
     * @return The id of the player who won. If the game is not over, returns -1. In case of
     *              a tie, returns -1 and sets {@code tiedPlayers} to the list of tied players.
     */
    public int getWinner(){
        if (!isGameOver) return -1;
        else{

            //Check if there is a winner
            int[] scores = getPlayerScores(); //The scores of every player
            int maxScore = 0; //The highest score seen
            int winner = 0; //The index of the winner
            int tie = 0;
            for (int i = 0; i < numPlayers; i++){
                int score = scores[i];
                if(score > maxScore){
                    maxScore = score;
                    winner = i;
                    tie = 1;
                }
                else if(score == maxScore){
                    tie++;
                }
            }

            if(tie == 1){
                return winner;
            }

            //RULE: To break a tie, winner is player with fewer turns
            //      Otherwise, unbreakable tie

            int minTurnsPlayed = dominionPlayers[winner].getTurnsPlayed();
            int tieWinner = winner;
            int unbreakableTie = 1;
            for (int i = winner + 1; i < numPlayers; i++){
                int turns = dominionPlayers[i].getTurnsPlayed();
                int score = scores[i];
                if (score == maxScore && turns < minTurnsPlayed){
                    minTurnsPlayed = turns;
                    tieWinner = i;
                    unbreakableTie = 1;
                }
                else if (score == maxScore && turns == minTurnsPlayed){
                    unbreakableTie++;
                }
            }

            if(unbreakableTie == 1){
                return tieWinner;
            }

            tiedPlayers = new int[unbreakableTie];
            int players = 0;
            for (int i = tieWinner; i < numPlayers; i++){
                if(dominionPlayers[i].getTurnsPlayed() == minTurnsPlayed && scores[i] == maxScore){
                    tiedPlayers[players++] = i;
                }
            }

            return -1;
        }
    }

    /**
     * Gets a list of all players tied for highest score and turns played.
     * Only accurate if getWinner was called and returned -1 while the game was over.
     * @return List of indexes of tied players. Returns null if not valid.
     */
    public int[] getTiedPlayers(){
        return tiedPlayers;
    }

    public int getCurrentTurn(){
        return currentTurn;
    }

    public int getAttackTurn(){
        return attackTurn;
    }

    public DominionPlayerState getDominionPlayer(int player) {
        return dominionPlayers[player];
    }

    public boolean getIsAttackTurn(){
        return isAttackTurn;
    }
}
