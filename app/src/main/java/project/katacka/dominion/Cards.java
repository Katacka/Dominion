package project.katacka.dominion;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Cards {
    protected ArrayList<Card> cardStack;
    protected int totalCards;
    protected final static int uniqueCards = 10; //This can't be zero, else generateStack will throw a div zero error

    public Cards(int totalCards) {
        this.cardStack = new ArrayList<>(totalCards);
        this.totalCards = totalCards;
        generateStack(); //Populates cardStack
    }

    //Credit: https://stackoverflow.com/questions/4307273/how-can-i-create-and-display-an-arraylist-of-random-numbers-in-java
    private void generateStack(){
        //int[] randomCardInit = new int[stackSize];
        Random randomObj = new Random();
        randomObj.setSeed(System.currentTimeMillis());

        //I'd prefer this, but we'd have to agree to Java/Android version restrictions
        ThreadLocalRandom.current().ints(0, uniqueCards).distinct().limit(totalCards).forEach(randomInt -> initializeCards(randomInt));
    }

    private void initializeCards(int randomCard) {
        switch(randomCard) {
            case(0): //Festival
                cardStack.add(new Card("Festival", R.drawable.dominion_festival, "+2 Actions\n+1 Buy\n+2 Gold", 5, "ACTION"));
                break;
            case(1): //Merchant
                cardStack.add(new Card("Merchant", R.drawable.dominion_merchant, "+1 Card\n+1 Action\nThe first time you play a Silver this turn, +1 Gold", 3, "ACTION"));
                break;
            case(2): //Remodel
                cardStack.add(new Card("Remodel", R.drawable.dominion_remodel, "Trash a card from your hand. Gain a card costing up to 2 Gold more than it", 4, "ACTION"));
                break;
            case(3): //Throne Room
                cardStack.add(new Card("Throne Room", R.drawable.dominion_throne_room, "You may play an Action card from your hand twice", 4, "ACTION"));
                break;
            case(4): //Artisan
                cardStack.add(new Card("Artisan", R.drawable.dominion_artisan, "Gain a card to your hand costing up to 5 Gold. Put a card from your hand onto your deck", 6, "ACTION"));
                break;
            case(5): //Witch
                cardStack.add(new Card("Witch", R.drawable.dominion_witch, "+2 Cards\nEach other player gains a curse", 5, "ATTACK"));
                break;
            case(6): //Library
                cardStack.add(new Card("Library", R.drawable.dominion_library, "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards", 5, "ACTION"));
                break;
            case(7): //Laboratory
                cardStack.add(new Card("Laboratory", R.drawable.dominion_laboratory, "+2 Cards\n+1 Action", 5, "ACTION"));
                break;
            case(8): //Militia
                cardStack.add(new Card("Militia", R.drawable.dominion_militia, "+2 Gold\nEach other player discards down to 3 cards in hand", 4, "ATTACK"));
                break;
            case(9): //Harbinger
                cardStack.add(new Card("Harbinger", R.drawable.dominion_harbinger, "+1 Card\n+1 Action\nLook through your discard pile. You may put a card from it onto your deck", 3, "ACTION"));
                break;
            default:
                Log.e("initalizeCards","An exception has occured. No card has been assigned to this value");
        }
    }
}
