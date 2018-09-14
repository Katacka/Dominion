package project.katacka.dominion;

import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int totalOpponentCards = 5;
        displayCards((TableRow) findViewById(R.id.Opponent_Cards), totalOpponentCards, new int[]{R.drawable.opponent_card});

        int totalPlayerCards = 5;
        displayCards((TableRow) findViewById(R.id.User_Cards), totalPlayerCards, new Cards(totalPlayerCards));

        //generateStack((ConstraintLayout) findViewById(R.id.OpponentDiscard_Constraint), 1, R.drawable.dominion);
        //generateStack((ConstraintLayout) findViewById(R.id.OpponentDraw_Constraint), 1, R.drawable.dominion);
    }

    protected void displayCards(TableRow targetLayout, int totalCards, int[] imageID) {
        for (int i = 0; i < totalCards; i++){
            ImageView ivOpponentCard = new ImageView (this);
            //ivOpponentCard.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
            ivOpponentCard.setImageDrawable(getResources().getDrawable(imageID[0]));
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;
            trParams.gravity = Gravity.TOP;
            //ivOpponentCard.layout
            ivOpponentCard.setLayoutParams(trParams);
            targetLayout.addView(ivOpponentCard);
        }
    }

    protected void displayCards(TableRow targetLayout, int totalCards, Cards cards) {
        for (int i = 0; i < totalCards; i++){
            final RelativeLayout rlCard = new RelativeLayout(this);
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;
            trParams.setMargins(10, 10, 10, 10);
            rlCard.setLayoutParams(trParams);

            RelativeLayout.LayoutParams rlParam0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam0.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

            //Card Title
            final TextView tvCardName = new TextView(this);
            tvCardName.setText(cards.cardStack.get(i).cName);
            tvCardName.setTypeface(null, Typeface.BOLD);
            //tvCardName.setTextSize(14);
            tvCardName.setId(1);
            rlCard.addView(tvCardName, rlParam0);

            RelativeLayout.LayoutParams rlParam1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam1.addRule(RelativeLayout.BELOW, tvCardName.getId());

            //Card Image
            final ImageView ivCard = new ImageView (this);
            ivCard.setImageDrawable(getResources().getDrawable(cards.cardStack.get(i).cPhotoId));
            ivCard.setId(4);
            ivCard.setAdjustViewBounds(true);
            //ivCard.setScaleType(ImageView.ScaleType.FIT_XY);
            rlCard.addView(ivCard, rlParam1);

            RelativeLayout.LayoutParams rlParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam2.addRule(RelativeLayout.BELOW, ivCard.getId());

            //Card Text
            final TextView tvCardText = new TextView(this);
            tvCardText.setText(cards.cardStack.get(i).cText);
            tvCardText.setId(2);
            rlCard.addView(tvCardText, rlParam2);

            RelativeLayout.LayoutParams rlParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam3.addRule(rlCard.ALIGN_PARENT_BOTTOM, tvCardText.getId());

            //Card Type
            final TextView tvCardType = new TextView(this);
            tvCardType.setText(cards.cardStack.get(i).cType);
            tvCardType.setId(3);
            rlCard.addView(tvCardType, rlParam3);

            //rlParams.addRule(RelativeLayout.BELOW, ivCard.getId());

            targetLayout.addView(rlCard);
        }
    }

    protected void generateStack(ConstraintLayout targetLayout, int totalCards, int imageID) {
        ImageView ivOpponentCard = new ImageView (this);
        ivOpponentCard.setImageDrawable(getResources().getDrawable(imageID));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100);
        //lp.setMargins(300, 0, 300, 300);
        ivOpponentCard.setLayoutParams(lp);
        targetLayout.addView(ivOpponentCard);
    }
}
