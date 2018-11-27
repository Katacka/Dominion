package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static project.katacka.dominion.GameStateGenerator.getNewState;

import project.katacka.dominion.gameplayer.DominionHumanPlayer;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class CardStateTest {

    private DominionGameState state;

    private final int COPPER = 0;
    private final int ESTATE = 1;
    private final int MOAT = 0;
    private final int COUNCIL = 8;
    private final int MONEY_LENDER = 9;
    private final int MERCHANT = 3;
    private final int SILVER = 2;

    private int NUM_PLAYERS = 4;
    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;
    private int currPlayer;


    @BeforeClass
    public static void setup(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void makeState(){
        state = getNewState(NUM_PLAYERS);
        baseCards = state.getBaseCards();
        shopCards = state.getShopCards();
        currPlayer = state.getCurrentTurn();
    }

    /**
     * @author Hayden
     */
    @Test
    public void cardStateConstructor(){
        DominionCardState copper = new DominionCardState(1234, "copper", "dominion_copper",
                "copper description:\n +1 Treasure", 0, "TREASURE", "baseAction",
                1, 0, 0, 0, 0);

        assertEquals("title","copper", copper.getTitle());
        assertEquals("photoid", "dominion_copper", copper.getPhotoId());
        assertEquals("text", "copper description:\n +1 Treasure", copper.getFormattedText());
        assertEquals("cost", 0, copper.getCost());

        assertEquals("type", DominionCardType.TREASURE, copper.getType());

        assertEquals("actionName", "baseAction", copper.getAction());
        assertEquals("addedTreasure", 1, copper.getAddedTreasure());
        assertEquals("addedActions", 0, copper.getAddedActions());
        assertEquals("addedDraws", 0, copper.getAddedDraw());
        assertEquals("addedBuys", 0, copper.getAddedBuys());
        assertEquals("added Victory points, dn account for gardens", 0, copper.getSimpleVictoryPoints());
    }

    //SEPARATE CLASS, test copy constructor

    /**
     * @author Ryan
     */
    @Test
    public void testCardAction(){
        /*
         * External citation
         * Date: 11/25/18
         * Problem: Wanted to use streams, didn't know function names
         * Resource:
         *  https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
         * Solution: Found function names
         */

        assertEquals("All normal cards can be played.", 0, Stream.concat(state.getBaseCards().stream(), state.getShopCards().stream())
                                                    .map(DominionShopPileState::getCard) //Converts piles to cards
                                                    .filter(card -> !card.cardAction(state)) //Gets cards whose actions fail
                                                    .map(DominionCardState::getTitle) //Get failing cards names (for debugging)
                                                    .count());

        assertTrue("Blank card can be played.", DominionCardState.BLANK_CARD.cardAction(state));
    }

    /**
     * @author Hayden Liao
     * moat +2 cards to hand
     */
    @Test
    public void testMoatAction(){
        DominionPlayerState p = state.getDominionPlayer(currPlayer);
        DominionCardState moatCard = state.getShopCards().get(MOAT).getCard();

        ArrayList<DominionCardState> hand = p.getDeck().getHand();
        hand.set(2, shopCards.get(MOAT).getCard()); //ensure the hand has a moat

        assertEquals("hand size before", 5, hand.size());

        moatCard.moatAction(state);

        assertEquals("hand size after", 7, hand.size()); // +2 other cards, discard not built into moatAction
    }

    /**
     * @author Ryan
     */
    @Test
    public void testCouncilRoom(){
        currPlayer = state.getCurrentTurn();
        int otherPlayer = (currPlayer + 1) % 4;

        DominionDeckState currDeck = state.getDominionPlayer(currPlayer).getDeck();
        DominionDeckState otherDeck = state.getDominionPlayer(otherPlayer).getDeck();

        //Test initial state
        assertEquals("Initial hand", 5, currDeck.getHandSize());
        assertEquals("Initial other hand", 5, otherDeck.getHandSize());
        assertEquals("1 buy", 1, state.getBuys());

        //Perform action
        DominionCardState councilRoom = state.getShopCards().get(COUNCIL).getCard();
        councilRoom.councilRoomAction(state);

        //Test new state
        assertEquals("Drawn 4", 9, currDeck.getHandSize());
        assertEquals("Drawn 1", 6, otherDeck.getHandSize());
        assertEquals("2 buys", 2, state.getBuys());
    }

    /**
     * @author Ryan and Hayden
     */
    @Test
    //can play moneylender with no copper, but nothing happens
    public void testMoneyLenderNoCopper(){
        getNewState(4);
        DominionDeckState deck = state.getDominionPlayers()[currPlayer].getDeck();
        setupSpecialHand(deck);

        DominionCardState moneyLender = shopCards.get(MONEY_LENDER).getCard();

        state.playCard(currPlayer, 0); //Plays a copper, you have no copper now

        assertEquals(moneyLender, deck.getHand().get(3));//money lender is 3rd card

        boolean playedCard = state.playCard(currPlayer, 3); //try to player Money Lender

        assertTrue(playedCard); //make sure card is played

        assertEquals(0, state.getActions()); //action used

        assertEquals(1, state.getTreasure()); //No treasure bonus (1 from copper).
    }

    /**
     * @author Ryan and Hayden
     */
    @Test
    public void testMoneyLenderWithOneCopper(){
        getNewState(4);
        DominionDeckState deck = state.getDominionPlayers()[currPlayer].getDeck();
        setupSpecialHand(deck);

        assertEquals(shopCards.get(MONEY_LENDER).getCard(), deck.getHand().get(4));//money lender is 4th card
        assertEquals(baseCards.get(COPPER).getCard(), deck.getHand().get(0));//copper is 1st card
        assertEquals(0, state.getTreasure()); //have 0 treasure
        assertEquals(1, state.getActions()); //have 1 action

        boolean playedCard = state.playCard(currPlayer, 4); //try to player Money Lender
        assertTrue(playedCard); //make sure you played the card

        //make sure money lender and copper are NOT in hand
        assertFalse(deck.getHand().contains(baseCards.get(COPPER).getCard()));
        assertFalse(deck.getHand().contains(shopCards.get(MONEY_LENDER).getCard()));

        assertEquals(0, state.getActions()); //used an action
        assertEquals(3, state.getTreasure()); //have 3 treasure now
    }

    /**
     * @author Ryan, Hayden
     */
    @Test
    public void testMoneyLenderWithManyCopper(){
        getNewState(4);
        DominionDeckState deck = state.getDominionPlayers()[currPlayer].getDeck();
        setupSpecialHand(deck);
        ArrayList<DominionCardState> hand = deck.getHand();

        hand.add(baseCards.get(COPPER).getCard()); //Add a copper as 8th card
        hand.add(baseCards.get(COPPER).getCard()); //and 9th card

        assertEquals(baseCards.get(COPPER).getCard(), hand.get(7));//8th card is a copper
        assertEquals(shopCards.get(MONEY_LENDER).getCard(), hand.get(4));//money lender is 4th card
        assertEquals(baseCards.get(COPPER).getCard(), hand.get(0));//copper is 1st card

        assertEquals(0, state.getTreasure()); //have 0 treasure
        assertEquals(1, state.getActions()); //have 1 action

        boolean playedCard = state.playCard(currPlayer, 4); //try to player Money Lender
        assertTrue(playedCard); //make sure you played the card

        //make sure money lender is NOT in hand
        assertFalse(hand.contains(shopCards.get(MONEY_LENDER).getCard()));

        //make sure there is still 2 coppers
        assertTrue(hand.contains(baseCards.get(COPPER).getCard()));
        hand.remove(baseCards.get(COPPER).getCard());
        //still 1 copper
        assertTrue(hand.contains(baseCards.get(COPPER).getCard()));

        //remove another one
        hand.remove(baseCards.get(COPPER).getCard());
        assertFalse(hand.contains(baseCards.get(COPPER).getCard()));

        assertEquals(0, state.getActions()); //still have an action
        assertEquals(3, state.getTreasure()); //have 3 treasure now
    }

    /**
     * @author Ryan, Hayden
     */
    @Test
    public void testSilverAndMerchantAction(){
        DominionCardState silver, merchant, copper;
        silver = state.getBaseCards().get(SILVER).getCard();
        merchant = state.getShopCards().get(MERCHANT).getCard();
        copper = state.getBaseCards().get(COPPER).getCard();

        //Test we got the right cards
        assertEquals("Got silver.", "Silver", silver.getTitle());
        assertEquals("Got merchant.", "Merchant", merchant.getTitle());

        //Test initial state
        assertEquals("No starting treasure", 0, state.getTreasure());

        //Test silver without a merchant play
        silver.silverAction(state);
        assertEquals("Got 2 treasure", 2, state.getTreasure());

        //RESET TURN, so that next silver is first on turn
        state.endTurn(state.getCurrentTurn());

        //Test silver adds treasure for every merchant played.
        merchant.merchantAction(state);

        //merchant bonus not added yet
        assertEquals("merchant bonus before silver is played", 0, state.getTreasure());

        merchant.merchantAction(state);
        silver.silverAction(state);
        assertEquals("Merchant bonus", 4, state.getTreasure());

        //Test additional merchants do not grant extra treasure
        merchant.merchantAction(state);
        assertEquals("Merchant doesn't grant bonus", 4, state.getTreasure());

        //Test additional silver do not grant extra treasure
        silver.silverAction(state);
        assertEquals("Silver doesn't grant bonus", 6, state.getTreasure());

        //RESET TURN, so that next silver is first on turn
        state.endTurn(state.getCurrentTurn());

        //play a silver
        assertEquals("no treasure", 0, state.getTreasure());
        silver.silverAction(state);
        assertEquals("+2 silver", 2, state.getTreasure());

        //play a merchant, no bonus because a silver has already been played
        merchant.merchantAction(state);
        assertEquals("+2 silver", 2, state.getTreasure());

        //play copper, does not use bonus
        copper.cardAction(state);
        assertEquals("+2 silver +1 copper", 3, state.getTreasure());

        //
        silver.silverAction(state);
        assertEquals("+2 silver +1 copper +2 silver", 5, state.getTreasure());
    }

    /**
     * @author Hayden Liao
     */
    @Test
    public void testBaseAction(){
        DominionPlayerState player = state.getDominionPlayer(currPlayer);
        DominionCardState copper = player.getDeck().getHand().get(0); //a copper card

        int initHandSize = player.getDeck().getHandSize();
        int initDrawSize = player.getDeck().getDrawSize();
        int initDiscardSize = player.getDeck().getDiscardSize();
        int initInPlaySize = player.getDeck().getInPlaySize();

        assertEquals(5, initHandSize);
        assertEquals(5, initDrawSize);
        assertEquals(0, initDiscardSize);
        assertEquals(0, initInPlaySize);

        assertEquals("hand size", initHandSize + copper.getAddedDraw(), player.getDeck().getHandSize());
        assertEquals("draw size", initDrawSize - copper.getAddedDraw(), player.getDeck().getDrawSize());
        assertEquals("discard size", initDiscardSize, initDiscardSize);
        assertEquals("inplay size", initInPlaySize, initInPlaySize);
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////BELOW ARE SOME TESTING HELPER METHODS///////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Modifies hand to have set list of cards, used to test
     * @param deck The deck to modify the hand of
     */
    private void setupSpecialHand(DominionDeckState deck) {
        ArrayList<DominionCardState> hand = deck.getHand();
        hand.set(0, baseCards.get(COPPER).getCard()); //First card copper
        hand.set(1, baseCards.get(ESTATE).getCard()); //Second card Estate
        hand.set(2, shopCards.get(MOAT).getCard()); //Third card Moat
        hand.set(3, shopCards.get(COUNCIL).getCard()); //Forth card Council room
        hand.set(4, shopCards.get(MONEY_LENDER).getCard()); //Fifth card Money Lender
        hand.add(shopCards.get(MERCHANT).getCard()); //Sixth card merchant
        hand.add(baseCards.get(SILVER).getCard()); //Seventh card Silver
    }
}