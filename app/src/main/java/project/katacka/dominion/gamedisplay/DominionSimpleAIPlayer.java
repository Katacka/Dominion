package project.katacka.dominion.gamedisplay;

import android.view.View;

import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionSimpleAIPlayer extends GameComputerPlayer {
    protected Cards mDeckPile;
    protected Cards mDiscardPile;
    protected Cards mHand;
    protected int mActions;
    protected int mBuys;
    protected int mGold;
    protected int mVP;

    protected DominionSimpleAIPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    protected DominionSimpleAIPlayer(String name, int numCards) {
        super(name);

        this.mDeckPile = new Cards(numCards);
        this.mDiscardPile = new Cards(0);
        this.mHand = new Cards(0);

        this.mActions = 0;
        this.mBuys = 0;
        this.mGold = 0;
        this.mVP = 0;
    }

    public boolean playSimpleTurn() {
        updateDeckInfo();
        playSimpleActionPhase();
        playAllTreasures();
        playSimpleBuyPhase();
        return true;
    }

    public boolean updateDeckInfo() {
        return true;
    }

    public boolean playSimpleActionPhase() {
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean playSimpleBuyPhase() {
        return true;
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }

    @Override
    public void setAsGui(GameMainActivity activity) {

    }

    @Override
    public void receiveInfo(GameInfo info) {

    }
}

