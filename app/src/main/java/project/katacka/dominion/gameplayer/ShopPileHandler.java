package project.katacka.dominion.gameplayer;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import project.katacka.dominion.R;
import project.katacka.dominion.gamestate.DominionGameState;

public class ShopPileHandler implements GestureDetector.OnGestureListener {
    //private GestureDetector detector = new GestureDetector(this);
    protected View view;
    protected DominionGameState state;

    ShopPileHandler(DominionGameState state){
        //view=v;
        this.state = state;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d("TAG","onDown: ");

        // don't return false here or else none of the other
        // gestures will work
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("TAG", "onSingleTapConfirmed: ");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        /*
        Log.i("DominionHumanPlayer: onLongClick", "shopcard longpressed");
        int id = layout.getId();
        int index = 0;
        int playerID = state.getCurrentTurn();
        TextView title = layout.findViewById(R.id.textViewTitle);
        String titleString = title.getText().toString();
        for(int j=0; j<state.getShopCards().size(); j++){
            if(state.getShopCards().get(j).getCard().getTitle().equals(titleString)){
                index = j;
            }
        }
        //array list of shop piles, if arraylist contains constraint layout
        boolean isBaseCard = basePiles.contains(layout);

        state.buyCard(state.getCurrentTurn(), index, basePiles.contains(layout));
        return false;
        }
        */
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        Log.i("TAG", "onScroll: ");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d("TAG", "onFling: ");
        return true;
    }

    @Override
    public void	onShowPress(MotionEvent e){
        return;
    }


}
