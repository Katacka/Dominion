package project.katacka.dominion;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import project.katacka.dominion.R;
import project.katacka.dominion.gameframework.GameConfig;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.LocalGame;
import project.katacka.dominion.gameframework.GamePlayerType;
import project.katacka.dominion.gameplayer.DominionHumanPlayer;
import project.katacka.dominion.gameplayer.DominionSimpleAIPlayer;
import project.katacka.dominion.localgame.DominionLocalGame;


//import project.katacka.dominion.game.config.GamePlayerType;

//extends AppCompatActivity
public class MainActivity extends GameMainActivity{

    // the port number that this game will use when playing over the network
    //NOTE: Number used in Pig
    private static final int PORT_NUMBER = 2278;

    @Override
    public GameConfig createDefaultConfig() {
        //TODO: undummy createDefaultConfig()
        //TODO: necessary for compilation, cannot get portnumber in GameMainActivity without creating a defaultConfig
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>(3);
        playerTypes.add(new GamePlayerType("Human"){
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionHumanPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Simple AI"){
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionSimpleAIPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Smart AI"){
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionHumanPlayer(name);
            }
        });
        GameConfig defaultConfig = new GameConfig(playerTypes, 1, 4, "Dominion", PORT_NUMBER);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new DominionLocalGame(this);
    }

    /*
        TODO: probably move this to to DominionHumanPlayer in the method setAsGui()
        not sure which of these pieces needs to go where, or we would have done it already
    */

    //START COMMENT HERE
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removes the title and notification bars respectively
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        //Dominion GUI code
        setContentView(R.layout.activity_main);

        //PlayerState tab code
        //TODO: Consider writing this into a multi-purpose tab function
        String[] names = {"Smart AI", "Dumb AI", "PlayerState 1", "PlayerState 2"};
        setNames(names);

        //Populates and displays the opponent cards
        displayImages(findViewById(R.id.Opponent_Cards), 5, R.drawable.dominion_opponent_card_back);

        //Populates the base cards (Treasure and Victory points)
        displayCards(findViewById(R.id.Base_Cards), R.layout.shop_card, 2, new Cards());

        //Populates and displays the shop cards
        displayCards(findViewById(R.id.Shop_Cards), R.layout.shop_card, 5, new Cards(10));

        //Populates and displays the player cards
        displayCards(findViewById(R.id.User_Cards), R.layout.player_card, new Cards(4));
    }

    //Generates and displays a row of images within a TableRow
    protected void displayImages(TableRow targetLayout, int totalCards, int imageID) {
        for (int i = 0; i < totalCards; i++){
            //Create, define and size image
            ImageView ivOpponentCard = new ImageView (this);
            ivOpponentCard.setScaleType(ImageView.ScaleType.FIT_XY);
            ivOpponentCard.setImageResource(imageID);

            //Modify image parameters relating to sizing within the parent layout
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;

            //Effectively renders the image
            targetLayout.addView(ivOpponentCard, trParams);
        }
    }

    //Generates and adds a gray filter on top of some pre-existing layout
    protected void displayEmptyStack(View cardLayout) {
        //Defines constraints and coloring for the gray overlay
        LinearLayout grayOverlay = new LinearLayout(this);
        grayOverlay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        grayOverlay.setBackgroundColor(Color.parseColor("#c8000000"));

        ((ConstraintLayout) cardLayout).addView(grayOverlay);
    }

    //
    protected View populateCardLayout(View cardLayout, CardView card){
        //Sets the card title
        ((TextView) cardLayout.findViewById(R.id.textViewTitle))
                .setText(card.mTitle);

        //Sets the card image
        ((ImageView) cardLayout.findViewById(R.id.imageViewArt))
                .setImageResource(card.mPhotoId);

        //Sets the card text
        ((TextView) cardLayout.findViewById(R.id.textViewText))
                .setText(card.mText);

        //Sets the card cost text
        ((TextView) cardLayout.findViewById(R.id.textViewCost))
                .setText(String.format(Locale.US, "%d", card.mCost));

        //Sets the card amount text
        //TODO: Remove randomization used for example GUI
        int randomAmount = (int) (Math.random() * 11);
        ((TextView) cardLayout.findViewById(R.id.textViewAmount))
                .setText(String.format(Locale.US, "%d", randomAmount));

        //Sets the card type text
        ((TextView) cardLayout.findViewById(R.id.textViewType))
                .setText(card.mType);

        //Grays out the card layout if empty
        if (randomAmount == 0) {
            displayEmptyStack(cardLayout);
        }

        return cardLayout;
    }

    protected View populatePlayerCardLayout(View cardLayout, CardView card) {
        //Reveals text for player cards, as room allows
        cardLayout.findViewById(R.id.textViewText)
                .setVisibility(View.VISIBLE);

        //Hides the card amount, as not relevant
        cardLayout.findViewById(R.id.frameLayout2)
                .setVisibility(View.INVISIBLE);

        return populateCardLayout(cardLayout, card);
    }

    //Used to generate player cards
    protected void displayCards(ViewGroup cardsLayout, int layoutID, Cards cards) {
        //Declares and defines parameters used to define parent-child relationship attributes
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT);
        trParams.weight = 1.0f;
        trParams.setMargins(5,5,5,5);

        int totalCards = cards.totalCards;

        //Iterates over the TableLayout's TableRows populating each one
        for (int i = 0; i < totalCards; i++) {
            ViewGroup cardLayout = (ViewGroup) getLayoutInflater().inflate(layoutID, null);
            cardsLayout.addView(populatePlayerCardLayout(cardLayout, cards.cardViewStack.get(i)), trParams);
        }
    }

    //Used to generate shop cards
    protected void displayCards(ViewGroup cardsLayout, int layoutID, int cardsPerRow, Cards cards) {
        int numRows = cardsLayout.getChildCount();
        int indexOffset = 0;

        //Iterates over the TableLayout's TableRows populating each one
        for (int i = 0; i < numRows; i++, indexOffset+=cardsPerRow) {
            TableRow cardRow = (TableRow) cardsLayout.getChildAt(i);

            for (int j = 0; j < cardsPerRow; j++) {
                //cardRow.getChildAt(j).setLayoutParams(trParams);
                populateCardLayout(cardRow.getChildAt(j), cards.cardViewStack.get(j + indexOffset));
            }
        }
    }

    //Sets tab names
    protected void setNames(String[] names) {
        ConstraintLayout tabLayout = findViewById(R.id.Player_Tabs);
        for(int i = 0; i < names.length; i++) {
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(names[i]);
        }
    }

    //END COMMENT HERE
    */
}
