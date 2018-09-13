package project.katacka.dominion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int totalOpponentCards = 5;
        //generateCards((TableRow) findViewById(R.id.Opponent_Cards), totalOpponentCards, R.drawable.dominion);

        /*int totalOpponentCards = 5;
        TableRow trOpponentCards =(TableRow) findViewById(R.id.Opponent_Cards);
        for (int c=1; c<=totalOpponentCards; c++){
            ImageView ivOpponentCard = new ImageView (this);
            ivOpponentCard.setImageDrawable(getResources().getDrawable(R.drawable.dominion));
            ivOpponentCard.setPadding(0, 0, 0, 0); //padding in each image if needed
            //add here on click event etc for each image...
            //...
            trOpponentCards.addView(ivOpponentCard, 100,100);
        }*/
    }

    protected void generateCards(TableRow targetRow, int totalCards, int imageID) {
        for (int c=1; c<=totalCards; c++){
            ImageView ivOpponentCard = new ImageView (this);
            //ivOpponentCard.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            ivOpponentCard.setImageDrawable(getResources().getDrawable(imageID));
            ivOpponentCard.setPadding(0, 0, 0, 0); //padding in each image if needed
            //add here on click event etc for each image...
            //...
            targetRow.addView(ivOpponentCard, 300, 300);
        }
    }
}
