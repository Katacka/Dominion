package project.katacka.dominion.gamedisplay;

import android.util.Log;
import java.util.ArrayList;

import project.katacka.dominion.gameplayer.DominionHumanPlayer;

import static android.content.ContentValues.TAG;

public class displayState {
    //TODO: may need to delete this class

    protected ArrayList<DominionHumanPlayer> mDominionPlayers; //Sorted by order of turn
    protected int mCurrentTurn; //-1 when game ended

    protected displayState(int numPlayers) {
        mDominionPlayers = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            mDominionPlayers.add(new DominionHumanPlayer("Player"+i));
        }

        mCurrentTurn = 0;
    }

    @Override
    protected project.katacka.dominion.gamedisplay.displayState clone() {
        project.katacka.dominion.gamedisplay.displayState clone = null;

        try{
            clone = (project.katacka.dominion.gamedisplay.displayState) super.clone();
            clone.mDominionPlayers = new ArrayList<>(mDominionPlayers);
        }
        catch(CloneNotSupportedException cnse) {
            Log.e(TAG, "Error while cloning displayState: ", cnse);
        }

        return clone;
    }

    //ToDo: Implement method
    @Override
    public String toString() {
        return super.toString();
    }
}
