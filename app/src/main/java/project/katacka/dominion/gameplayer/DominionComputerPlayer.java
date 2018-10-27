package project.katacka.dominion.gameplayer;

import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionComputerPlayer extends GameComputerPlayer {

    /**
     * constructor
     *
     * @param name
     * 			the player's name (e.g., "John")
     */
    public DominionComputerPlayer(String name) {
        super(name);
    }


    // sets this player as the GUI player (overrideable)
    public void setAsGui(GameMainActivity activity){

    }

    /**
     * Callback-method implemented in the subclass whenever updated
     * state is received.
     *
     * @param info
     * 			the object representing the information from the game
     */
    protected void receiveInfo(GameInfo info){


    }




}
