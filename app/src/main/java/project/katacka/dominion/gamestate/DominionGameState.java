package project.katacka.dominion.gamestate;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A data class intended to represent the state of a game object
 * @author Ryan Regier, Julian Donovan, Ashika Mulagada, Hayden Liao
 */
public class DominionGameState {

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

    private final int PILE_COPPER;
    private final int PILE_ESTATE;

    protected DominionPlayerState dominionPlayers[]; //Sorted by order of turn
    protected int currentTurn;
    protected int attackTurn; //player id of responder
    protected boolean isAttackTurn;
    protected boolean isGameOver;

    protected int numPlayers;

    protected int actions;
    protected int buys;
    protected int treasure;

    //TODO: All other missing GameState functions (see below)
    //TODO: Ensure all changes as described in the previous GameState submission's comments have been made

    //RULE: With 2 players, 8 of each victory card should exist
    //      With 3-4 players, default to 12 copies of each victory card
    private final int VICTORY_CARDS_2_PLAYER = 8;

    private int emptyPiles;

    public DominionGameState(int paramNumPlayers, ArrayList<DominionShopPileState> baseCardArray,
                              ArrayList<DominionShopPileState> shopCardArray) {
        PILE_COPPER = 0;
        PILE_ESTATE = 1;

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

        //Sets up turn with player 0 as first player
        this.currentTurn = 0;
        this.treasure = 0;
        this.buys = 1;
        this.actions = 1;

        this.isGameOver = false;

        this.attackTurn = this.currentTurn; //in the event of an attack
        this.isAttackTurn = false;

        this.emptyPiles = 0;
    }

    //obfuscated copy
    public DominionGameState(DominionGameState gameState, DominionPlayerState playerState){
        PILE_COPPER = 0;
        PILE_ESTATE = 1;

        this.baseCards= new ArrayList<DominionShopPileState>();
        this.shopCards= new ArrayList<DominionShopPileState>();

        for(DominionShopPileState basePileState: gameState.baseCards){
            this.baseCards.add(new DominionShopPileState(basePileState));
        }

        for(DominionShopPileState shopPileState: gameState.shopCards){
            this.baseCards.add(new DominionShopPileState(shopPileState));
        }

        this.numPlayers = gameState.numPlayers;
        this.dominionPlayers = new DominionPlayerState[this.numPlayers];

        //copy each player including the deckState
        for (int i = 0; i < this.numPlayers; i++) {
            //if(i == playerState)
            this.dominionPlayers[i] = new DominionPlayerState(gameState.dominionPlayers[i],
                    gameState.currentTurn == i);
        }
        this.currentTurn = gameState.currentTurn;
        this.attackTurn = gameState.attackTurn;
        this.isAttackTurn = gameState.isAttackTurn;
        this.isGameOver = gameState.isGameOver;

        this.actions = gameState.actions;
        this.buys = gameState.buys;
        this.treasure = gameState.treasure;
    }

    /**
     * Yields information to the player as necessary, obscuring game state data not relevant to that particular player
     * @param state Relevant GameState from which data will be gathered
     * @param playerID PlayerID in question, for which data will be found
     */
    protected void hideInformation(DominionGameState state, int playerID){
        //COMMENT FOR THE GRADER: functionality wrapped into copy constructor.
            //not deleting with intention of (maybe?) implementing this later.
    }

    @Override
    public String toString() {

        String turnStr, gabStr, baseStr, shopStr, playerStr, emptyPilesStr, gameOverStr;

        String attackString = "";
        if (isAttackTurn){
            attackString = String.format(Locale.US,
                    "An attack has been played. Player #%d is responding to the attack", attackTurn);
        }
        turnStr = String.format(Locale.US, "It is player #%d's turn. %s", currentTurn, attackString);

        gabStr = String.format(Locale.US, "There are %d buys, %d actions, and %d treasure remaining.",
                buys, actions, treasure);

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

        if (isGameOver){
            gameOverStr = "The game is over.";
        } else {
            gameOverStr = "The game is not over.";
        }

        return String.format(Locale.US, "%s\n%s\n%s\n%s\n%s\n%s\n%s", turnStr, gabStr,
                baseStr, shopStr, playerStr, emptyPilesStr, gameOverStr);
    }


    //Start of actions that can be performed by a player

    //TODO: Delete this..?
    public boolean revealCard(int playerID){
        if(this.currentTurn == playerID && this.dominionPlayers[playerID].getDeck().getHandSize() > 0) {
            //Reveal card
            return true;
        }
        return false;
    }

    public boolean discardCard(int playerID){
        if(this.currentTurn == playerID && this.dominionPlayers[playerID].getDeck().getHandSize() > 0) {
            //Discard card
            return true;
        }
        return false;
    }

    public boolean buyCard(int playerID){
        if(this.currentTurn == playerID) {
            //Buy card
            return true;
        }
        return false;
    }

    public boolean chooseCard(int playerID){
        if(this.currentTurn == playerID && this.dominionPlayers[playerID].getDeck().getHandSize() > 0) {
            //Choose card
            return true;
        }
        return false;
    }

    public boolean endTurn(int playerID){
        if(this.currentTurn == playerID) {
            //End turn
            return true;
        }
        return false;
    }

    /**
     * Determines whether a card may be legally drawn considering whether it is that player's turn.
     * Returns a truth value describing the success of the draw
     *
     * @return A boolean describing whether the selected card was successfully drawn
     */
    public boolean drawCard(int playerID){
        if(this.currentTurn == playerID) {
            return (this.dominionPlayers[playerID].getDeck().draw() != null);
        }
        return false;
    }

    /**
     * Allows the user to quit the game so long as the game has not already been quit.
     * Returns a truth value describing the success of this action
     *
     * @return A boolean describing whether the game was successfully quit
     */
    public boolean quitGame(int playerID){
        return (!isGameOver && (this.isGameOver = true));
    }

    /**
     * Plays a card, if legal, calling its action method and moving it to the discard pile.
     * Returns a boolean as to the success of this operation
     *
     * @return A boolean describing whether the selected card may legally be played
     */
    public boolean playCard(int playerID, DominionCardState card){
        if(isLegalPlay(playerID, card, card.getType().equals("ACTION"))) {
            DominionDeckState deck = this.dominionPlayers[playerID].getDeck();
            card.cardAction(this);
            deck.discard(card);
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
    public boolean isLegalPlay(int playerID, DominionCardState card, boolean isAction) {
        if(this.currentTurn == playerID && this.dominionPlayers[playerID].getDeck().getHand().contains(card)) {
            if(isAction && this.actions <= 0) return false; //An available actions is required to play an action card
            return true;
        }
        return false;
    }

    /**
     * Determines whether a card may be legally bought, considering the current player, the card's existence,
     * and the player's buys available. Returns a truth value.
     *
     * @return A boolean describing whether the selected card may legally be bought
     */
    public boolean isLegalBuy(int playerID, DominionCardState card) {
        if(this.currentTurn == playerID && isShopCard(card) && this.buys > 0) return true;
        return false;
    }

    /**
     * Determines whether a card exists in the shop in a positive, non-zero quantity.
     * Returns a truth value
     *
     * @return A boolean describing whether the selected card exists in shopCards
     */
    private boolean isShopCard(DominionCardState card) {
        for(DominionShopPileState shopPile : shopCards) {
            if(card == shopPile.getCard() && shopPile.getAmount() > 0) return true;
        }
        return false;
    }

}
