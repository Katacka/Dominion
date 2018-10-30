package project.katacka.dominion.localgame;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.LocalGame;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionLocalGame extends LocalGame {

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

        return;
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
        return true;
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
        return null;
    }


    /**
     * Makes a move on behalf of a player.
     *
     * @param action
     * 			The move that the player has sent to the game
     * @return
     * 			Tells whether the move was a legal one.
     */
    protected boolean makeMove(GameAction action){

        return true;
    }

    protected boolean getInfo(project.katacka.dominion.gamestate.DominionGameState gameState) {
        return true;
    }



}
