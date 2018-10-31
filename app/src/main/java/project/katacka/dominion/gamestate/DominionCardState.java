package project.katacka.dominion.gamestate;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * A data class intended to represent the state of a card object.
 * Only one instance should be created per unique card.
 *
 * @author Julian Donovan, Hayden Liao, Ashika Mulagada, Ryan Regier
 */
public class DominionCardState {

    //A card with no data, used for obfuscation
    public static final DominionCardState BLANK_CARD = new DominionCardState();

    //Card attributes
    //Final because only one instance is made per card. Changing an attribute would change all copies
    private final String title;
    private final String photoID;
    private final String text;
    private final int cost;
    private final DominionCardType type;
    private final Method action;
    private final int addedTreasure;
    private final int addedActions;
    private final int addedDraw;
    private final int addedBuys;
    private final int victoryPoints;

    /**
     * Constructor
     *
     * @param name The card name
     * @param photoStringID The name of the drawable for the card art
     * @param text The card description
     * @param cost The cost of the card
     * @param type The type of the card. Converted into DominionCardType - must match name exactly
     * @param action The name of the function called when card is played.
     * @param addedTreasure The treasure the card gives when played.
     * @param addedActions The actions the card gives when played.
     * @param addedDraw The cards drawn when played.
     * @param addedBuys The buys the card gives when plays.
     * @param victoryPoints The worth of the card in victory points.
     */
    public DominionCardState (String name, String photoStringID, String text, int cost, String type, String action,
                              int addedTreasure, int addedActions, int addedDraw, int addedBuys, int victoryPoints){
        this.title = name;

        this.photoID = photoStringID;
        this.text = text;
        this.cost = cost;
        this.type = DominionCardType.getTypeFromString(type);
        if (this.type == null){
            Log.e(TAG, "Illegal type for card " + this.title);
            throw new IllegalArgumentException("Card type does not exist.");
        }

        //Dynamically assigned by method reflection, allowing for a String method reference to be held in JSON
        this.action = getMethod(action);

        this.addedTreasure = addedTreasure;
        this.addedActions = addedActions;
        this.addedDraw = addedDraw;
        this.addedBuys = addedBuys;
        this.victoryPoints = victoryPoints;
    }

    /**
     * Copy constructor
     * @param other The instance to copy
     */
     public DominionCardState(DominionCardState other){
        this.title = other.title;
        this.photoID = other.photoID;
        this.text = other.text;
        this.cost = other.cost;
        this.type = other.type;
        this.action = other.action;
        this.addedTreasure = other.addedTreasure;
        this.addedActions = other.addedActions;
        this.addedBuys = other.addedBuys;
        this.addedDraw = other.addedDraw;
        this.victoryPoints = other.victoryPoints;
     }

    /**Blank constructor
     * Creates empty card
     * Used to obfuscate cards players cannot "see"
     */
    private DominionCardState(){
        this.title = "Blank";
        this.photoID = null;
        this.text = "Blank text";
        this.cost = 0;
        this.type = DominionCardType.BLANK;

        //Dynamically assigned by method reflection, allowing for a String method reference to be held in JSON
        this.action = getMethod("basicAction");

        this.addedTreasure = 0;
        this.addedActions = 0;
        this.addedDraw = 0;
        this.addedBuys = 0;
        this.victoryPoints = 0;
    }

    /**
     * External Citation
     * Date: 10/4
     * Source: https://stackoverflow.com/questions/13604111/final-variable-assignment-with-try-catch
     * Problem: wouldn't let action be assigned if final
     * Solution: used method to get Method
     */

    /**
     * Gets function of this class corresponding to {@code action}.
     * Used to load functions for card actions from JSON.

     * @param action A String name referencing the relevant DominionCardState function
     * @return A Method reference to a DominionCardState function
     */
    private Method getMethod(String action){
        try {
            return DominionCardState.class.getDeclaredMethod(action, DominionGameState.class);
        }
        catch (NoSuchMethodException e) {
            Log.e(TAG, "Error encountered reflecting action method: " + e + " with card " + this.title);
            throw new IllegalArgumentException("Card function " + action + " does not exist", e);
        }
    }

    /**
     * A method wrapped allowing for any DominionCardState object to invoke the function stored in
     * their action instance variable. Handles common Method errors.
     * @return A boolean regarding the success of the action invocation
     */
    public boolean cardAction(DominionGameState game) {
        try {
            Boolean result = (Boolean) action.invoke(this, game); //return automatically boxed to Boolean
            return result; //Note: automatically unboxed
        }
        catch (IllegalArgumentException e) {
            Log.e(TAG, "Illegal argument encountered when running reflected action method: " + e);
        }
        catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal access encountered when running reflected action method: " + e);
        }
        catch (InvocationTargetException e) {
            Log.e(TAG, "Invalid target encountered when running reflected action method: " + e);
        }
        return false;
    }

    /**
     * Overrides the default inherited toString() behavior, properly displaying object data.
     * Here for debug purposes, currently unused.
     *
     * @return A String containing object type, title, photoId, text, cost, type, amount and action info
     */
    @Override
    public String toString() {
        return "\nDominionCardState: {\n" +
                "\ttitle: " + getTitle() + ",\n" +
                "\tphotoId: " + getPhotoId() + ",\n" +
                "\ttext: " + getPlainText() + ",\n" +
                "\tcost: " + getCost() + ",\n" +
                "\ttype: " + getType() + ",\n" +
                "\taction: " + getAction() + ",\n" +
                "\taddedTreasures: " + getAddedTreasure() + ",\n" +
                "\taddedActions: " + getAddedActions() + ",\n" +
                "\taddedDraw: " + getAddedDraw() + ",\n" +
                "\taddedBuys: " + getAddedBuys() + ",\n" +
                "\tvictoryPoints: " + getVictoryPoints(0) + ",\n" +
                "},";
    }

    public String getTitle() {
        return title;
    }

    public String getPhotoId() {
        return photoID;
    }

    public String getFormattedText() {
        return text;
    }

    public String getPlainText() {
        return text.replaceAll("[\\s]", " ");
    }

    public int getCost() {
        return cost;
    }

    public DominionCardType getType() {
        return type;
    }

    public Method getAction() {
        return action;
    }

    public int getAddedTreasure() { return addedTreasure; }

    public int getAddedActions() { return addedActions; }

    public int getAddedDraw() { return addedDraw; }

    public int getAddedBuys() { return addedBuys; }

    /**
     * Calculates the number of victory points card is worth
     * @param totalCards Total number of cards in deck. Effects VP calculation for gardens
     * @return The VP worth of this card
     */
    public int getVictoryPoints(int totalCards) {
        if(title.equals("Gardens")) return totalCards/10;
        else return victoryPoints;
    }

    private boolean moatAction(DominionGameState game) {
        //Will have other behavior upon adding ATTACK cards
        return basicAction(game);
    }

    private boolean merchantAction(DominionGameState game) {
        game.silverBoon = true; //The first Silver played is worth one more
        return basicAction(game);
    }

    /////////////////////////////////////////////////////////////////////////
    /*
    Functions below this point are card actions. They are called when the card is played.
    Note that we cannot know what functions will be called until runtime, since cards are linked to
        methods by reading the XML.
    It is assumed the card is legal to play - these methods will not check for sufficient actions,
        current player's turn, ect.

    */
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Council room action:
     * +4 Cards, 1 Buy, Each other player draws a card
     * @param game The game state the card is played in
     * @return Action completed successfuly
     */
    private boolean councilRoomAction(DominionGameState game) {
        //Card text: "Each other player draws a card"
        for (int i = 0; i < game.dominionPlayers.length; i++) {
            if (i != game.currentTurn) game.dominionPlayers[i].getDeck().draw();
        }
        return basicAction(game);
    }

    /**
     * Money lender action:
     * You may trash a Copper from your hand for +3 Gold
     *
     * Implemented assuming player will only play card if they will trash copper
     * (there is no reason to play it otherwise)
     * @param game The game state the card is played in
     * @return Action completed successfully, meaning Copper in hand is trashed
     */
    private boolean moneylenderAction(DominionGameState game) {
        if(game.dominionPlayers[game.currentTurn].getDeck().discard("Copper")) {
            game.treasure += 3;
            return true;
        }
        return false;
    }

    /**
     * Silver action.
     * Needed to deal with market effect.
     *
     * @param game The game state the card is played in
     * @return Action completed successfully.
     */
    private boolean silverAction(DominionGameState game) {
        if(game.silverBoon) {
            game.treasure += 1; //Handles merchant silver boon
            game.silverBoon = false;
        }

        return basicAction(game);
    }

    /**
     * Basic action.
     * Used by any card whose action contain the following:
     * <ul>
     *     <li>Draw</li>
     *     <li>Actions</li>
     *     <li>Buys</li>
     *     <li>Treasure</li>
     * </ul>
     *
     * @param game The game the card is played in
     * @return Action success
     */
    private boolean basicAction(DominionGameState game) {
        DominionPlayerState currentPlayer = game.dominionPlayers[game.currentTurn];
        currentPlayer.getDeck().drawMultiple(this.addedDraw);
        game.actions += this.addedActions;
        game.buys += this.addedBuys;
        game.treasure += this.addedTreasure;
        return true;
    }
}
