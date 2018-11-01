package project.katacka.dominion.localgame;

import java.util.Locale;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.LocalGame;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.DominionGameState;

/**
 * Implementation of Local Game for Dominion
 *
 * @author Ryan Regier
 */
public class DominionLocalGame extends LocalGame {

    //The offical copy of the game state
    private DominionGameState state;

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p
     * 			the player to notify
     */
    protected void sendUpdatedStateTo(GamePlayer p){
        DominionGameState copy = new DominionGameState(state, getPlayerIdx(p));
        p.sendInfo(copy);
    }

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     * 		true iff the player is allowed to move
     */
    protected boolean canMove(int playerIdx){
        return state.canMove(playerIdx);
    }


    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return
     * 			a message that tells who has won the game, or null if the
     * 			game is not over
     */
    protected String checkIfGameOver(){
        if (!state.getGameOver()){
            return null;
        }

        int[] scores = state.getPlayerScores();
        int winner = state.getWinner();
        String result;
        if (winner != -1){ //No tie
            result = String.format(Locale.US, "%s has won.\nScores:\n", playerNames[winner]);
        } else {
            int[] tiedPlayers = state.getTiedPlayers();
            int numTied = tiedPlayers.length;
            result = String.format(Locale.US, "There was a %d-way tie between ", numTied);
            for (int i = 0; i < numTied; i++){
                if (i == numTied-1){
                    result += String.format(Locale.US, "%s.\nScores:\n", playerNames[i]);
                } else if ( i == numTied - 2){
                    result += String.format(Locale.US, "%s and ", playerNames[i]);
                } else {
                    result += String.format(Locale.US, "%s, ", playerNames[i]);
                }
            }
        }

        for(int i = 0; i < playerNames.length; i++){
            result += String.format(Locale.US, "%s: %d\n", playerNames[i], scores[i]);
        }

        result += "Thanks for playing!";
        return result;

    }


    /**
     * Makes a move on behalf of a player.
     *
     * @param gameAction
     * 			The move that the player has sent to the game
     * @return
     * 			Tells whether the move was a legal one.
     */
    protected boolean makeMove(GameAction gameAction){
        if (gameAction instanceof DominionPlayCardAction){
            DominionPlayCardAction action = (DominionPlayCardAction) gameAction;
            GamePlayer player = action.getPlayer();
            int playerID = getPlayerIdx(player);
            int cardIndex = action.getCardIndex();
            return state.playCard(playerID, cardIndex);
        } else if (gameAction instanceof DominionBuyCardAction){
            DominionBuyCardAction action = (DominionBuyCardAction) gameAction;
            GamePlayer player = action.getPlayer();
            int playerIndex = getPlayerIdx(player);
            int cardIndex = action.getCardIndex();
            boolean isBaseCard = action.getIsBaseCard();
            return state.buyCard(playerIndex, cardIndex, isBaseCard);
        } else if (gameAction instanceof DominionEndTurnAction){
            DominionEndTurnAction action = (DominionEndTurnAction) gameAction;
            int playerIndex = getPlayerIdx(action.getPlayer());
            return state.endTurn(playerIndex);
        } else return false;
    }

    @Override
    public void start(GamePlayer[] players) {
        super.start(players);
        //TODO: Julian read cards here
        //state = new DominionGameState();
    }
}
