package project.katacka.dominion.gamedisplay;

import android.util.Log;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

public class GameState {
    //TODO: may need to delete this class

    protected ArrayList<DominionHumanPlayer> mDominionPlayers; //Sorted by order of turn
    protected int mCurrentTurn; //-1 when game ended

    protected GameState(int numPlayers) {
        mDominionPlayers = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            mDominionPlayers.add(new DominionHumanPlayer("Player"+i));
        }

        mCurrentTurn = 0;
    }

    @Override
    protected GameState clone() {
        GameState clone = null;

        try{
            clone = (GameState) super.clone();
            clone.mDominionPlayers = new ArrayList<>(mDominionPlayers);
        }
        catch(CloneNotSupportedException cnse) {
            Log.e(TAG, "Error while cloning GameState: ", cnse);
        }

        return clone;
    }

    //ToDo: Implement method
    @Override
    public String toString() {
        return super.toString();
    }
}
