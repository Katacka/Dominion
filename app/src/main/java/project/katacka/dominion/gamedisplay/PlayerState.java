package project.katacka.dominion.gamedisplay;

public class PlayerState {
    protected String mName;
    protected Cards mDeckPile;
    protected Cards mDiscardPile;
    protected Cards mHand;
    protected int mActions;
    protected int mBuys;
    protected int mGold;
    protected int mVP;

    protected PlayerState(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    protected PlayerState(String name, int numCards) {
        this.mName = name;
        this.mDeckPile = new Cards(numCards);
        this.mDiscardPile = new Cards(0);
        this.mHand = new Cards(0);

        this.mActions = 0;
        this.mBuys = 0;
        this.mGold = 0;
        this.mVP = 0;
    }

}
