package project.katacka.dominion;

public class DominionPlayerState {
    protected String mName;
    protected Cards mDeckPile;
    protected Cards mDiscardPile;
    protected Cards mHand;
    protected int mActions;
    protected int mBuys;
    protected int mGold;
    protected int mVP;

    protected DominionPlayerState(String name) {
        this(name, 5);
    }

    protected DominionPlayerState(String name, int numCards) {
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
