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
        project.katacka.dominion.gamedisplay.Cards a;
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

    @Override
    public void tick(GameTimer timer) {

    }

    public boolean updateDeckInfo() {
        project.katacka.dominion.gamedisplay.DominionAIGetInfoAction a;
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean endTurn() {
        project.katacka.dominion.gamedisplay.DominionEndTurnAction a;
        return true;
    }
}
