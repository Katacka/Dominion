package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import project.katacka.dominion.GameStateGenerator;
import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

public class CardReaderTest { //Note for the grader: GSONDeserializer does not have its own test suite
                              //    as it its functionality is entirely expressed by its wrapper class, CardReader

    CardReader cr = new CardReader("base"); //The base expansion set is the only set supported as of current

    @Test //Julian Donovan
    public void testGenerateCards() {
        try (InputStream shopStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("shop_cards.json");
             InputStream baseStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("base_cards.json")) {
            ArrayList<DominionShopPileState> genCards = cr.generateCards(shopStream, 10);
            assertEquals(10, genCards.size()); //All 10 base shopCards have been deserialized

            DominionCardState moneylender = genCards.get(9).getCard(); //Spot check for the moneylender card
            assertEquals("Moneylender", moneylender.getTitle());
            assertEquals(4, moneylender.getCost());

            DominionCardState laboratory = genCards.get(2).getCard(); //Spot check for the laboratory card
            assertEquals("Laboratory", laboratory.getTitle());
            assertEquals(5, laboratory.getCost());
            assertEquals(2, laboratory.getAddedDraw());
            assertEquals(1, laboratory.getAddedActions());

            genCards = cr.generateCards(baseStream, 6);
            assertEquals(7, genCards.size()); //All 7 baseCards have been deserialized

            DominionCardState province = genCards.get(5).getCard(); //Spot check for the province card
            assertEquals("Province", province.getTitle());
            assertEquals(8, province.getCost());
            assertEquals(6, province.getSimpleVictoryPoints());

            DominionCardState copper = genCards.get(0).getCard(); //Spot check for the copper card
            assertEquals("Copper", copper.getTitle());
            assertEquals(0, copper.getCost());
            assertEquals(1, copper.getAddedTreasure());

            //If the uniqueCardPile argument exceeds the max number of cardPiles in the JSON, it defaults to the max card piles
            genCards = cr.generateCards(shopStream, Integer.MAX_VALUE);
            assertEquals(10, genCards.size());

            //If the uniqueCardPile argument exceeds the max number of cardPiles in the JSON, it defaults to the max card piles
            genCards = cr.generateCards(baseStream, Integer.MAX_VALUE);
            assertEquals(7, genCards.size());

        }
        catch (IOException e) {
            e.printStackTrace(); //Log failure in the event an input stream error occurs
        }
    }
}
