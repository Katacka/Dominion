package project.katacka.dominion.gameplayer;

import android.view.View;

import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionHumanPlayer extends GameHumanPlayer{
    public DominionHumanPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    public DominionHumanPlayer(String name, int numCards) {
        super(name);

        Cards a;
    }

    public boolean actionPlayed() {
        project.katacka.dominion.gamedisplay.DominionActionPlayed;
        return true;
    }

    public boolean playSimpleActionPhase() {
        project.katacka.dominion.gamedisplay.DominionPlayCardAction a;
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean playSimpleBuyPhase() {
        project.katacka.dominion.gamedisplay.DominionBuyCardAction a;
        return true;
    }

    public boolean quitGame() {
        project.katacka.dominion.gamedisplay.DominionQuitGameAction a;
        return true;
    }

    public boolean endTurn() {
        project.katacka.dominion.gamedisplay.DominionEndTurnAction a;
        return true;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }

    @Override
    public void gameSetAsGui(GameMainActivity activity) {

    }

    @Override
    public void receiveInfo(GameInfo info) {

    }

    @Override
    public View getTopView() {
        return null;
    }
}
