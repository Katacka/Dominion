package project.katacka.dominion.gamestate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import project.katacka.dominion.R;

import java.util.ArrayList;

/**
 * @author Julian Donovan, Hayden Liao, Ashika Mulagada, Ryan Regier
 */

public class MainActivity extends AppCompatActivity {
    /**
     * External citation
     * Date: 10/3
     * Problem: Wanted tag for logger
     * Resource:
     *  https://stackoverflow.com/questions/8355632/how-do-you-usually-tag-log-entries-android
     * Solution: Using class name as a tag
     **/
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fetch button reference and attach listener
        //Button runTestButton = findViewById(R.id.runButton);
        //runTestButton.setOnClickListener(buttonOnClickListener);
    }

   private final Button.OnClickListener buttonOnClickListener = (View v) -> {
       //Define a CardReader to deserialize shop_card.json data and base_card.json data to their respective object forms
       CardReader reader = new CardReader("base");
       ArrayList<DominionShopPileState> shopCards1 = reader.generateCards(getApplicationContext(), 10, R.raw.shop_cards);

       ArrayList<DominionShopPileState> baseCards1 = reader.generateCards(getApplicationContext(), R.raw.base_cards);

       //TextView editText = findViewById(R.id.editText);
       //editText.setText("");

       //Instantiate a DominionGameState object to store all relevant game information
       DominionGameState firstInstance = new DominionGameState(4, baseCards1, shopCards1);
       DominionGameState secondInstance = new DominionGameState(firstInstance);
        //COMMENT FOR THE GRADER: dominionPlayers[0] is "player 1"

       //Recording "functionality" of game actions

       //Have players draw cards
       firstInstance.start();

       //Set player 1's hand so that we can test
       //Index 4 is gold, index 0 is moat
       //Note: Indices may change in the future. Only here (and hard-coded) for testing purposes.
       firstInstance.dominionPlayers[0].testMoat(baseCards1.get(4).getCard(), shopCards1.get(0).getCard());

       String gameStateTest = "\n";

       gameStateTest = gameStateTest.concat("The player tries to play a Moat, which should let them draw two cards. " +
               "playCard() evaluates as " + firstInstance.playCard(0, 0) + ".\n");

       gameStateTest = gameStateTest.concat("The player now plays all the treasure in their hand. " +
               "playAllTreasures() evaluates as " + firstInstance.playAllTreasures(0) + ".\n");

       gameStateTest = gameStateTest.concat("Opting to spend their treasure, Player 1 buys 1 Gold card for 6 treasure. " +
               "buyCard() evaluates as " + firstInstance.buyCard(0, 4, true) + ".\n");

       gameStateTest = gameStateTest.concat("Having done all they can, Player 1 decides to end their turn which " +
               "yields " + firstInstance.endTurn(0) + "\n");

       gameStateTest = gameStateTest.concat("Growing impatient as Player 2's turn drags on, Player 1 decides to " +
               "quitGame. This runs " + firstInstance.quitGame(0) + "\n");

       ArrayList<DominionShopPileState> shopCards2 = reader.generateCards(getApplicationContext(), 10, R.raw.shop_cards);

       ArrayList<DominionShopPileState> baseCards2 = reader.generateCards(getApplicationContext(), R.raw.base_cards);

       //Instantiate another DominionGameState object for comparison, as directed by the assignment
       DominionGameState thirdInstance = new DominionGameState(4, baseCards2, shopCards2);
       Log.i(TAG, firstInstance.toString());

       DominionGameState fourthInstance = new DominionGameState(thirdInstance);

       String str2 = secondInstance.toString();
       String str4 = fourthInstance.toString();
       if(str2.equals(str4)){ gameStateTest = gameStateTest.concat("\nsecondInstance and fourthInstance are identical.\n"); }
       else { gameStateTest = gameStateTest.concat("\nsecondInstance and fourthInstance are not identical.\n"); }

       gameStateTest = gameStateTest.concat("SECOND INSTANCE\n" + secondInstance.toString());
       gameStateTest = gameStateTest.concat("\nFOURTH INSTANCE\n" + fourthInstance.toString());

       //editText.setText(gameStateTest);
   };

}
