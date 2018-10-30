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

    public String toString(){
        String string = "CardView Name: " + mName;
        return string;
    }

    /*
    [5%] Add a toString() method to the game state class the describes the state of the game as a
    string. This method should print the values of all the variables in your game state. If the
    variable is an array (or similar aggregate type) all its values must be printed. Your string
    should have sufficient formatting so its reasonable for you to tell values go with which variables.
    Be sure to put the @Override tag on your toString() method to verify it has the proper signature.
     */

}
