package project.katacka.dominion;

import java.util.ArrayList;

import project.katacka.dominion.gameframework.GameConfig;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.LocalGame;
import project.katacka.dominion.gameframework.GamePlayerType;
import project.katacka.dominion.gameplayer.DominionHumanPlayer;
import project.katacka.dominion.gameplayer.DominionSimpleAIPlayer;
import project.katacka.dominion.gameplayer.DominionSmartAIPlayer;
import project.katacka.dominion.localgame.DominionLocalGame;

/**
 * The Main Activity for the game.
 * @author Ryan Regier, Julian Donovan
 */
public class MainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    //NOTE: Number used in Pig
    private static final int PORT_NUMBER = 2278;

    @Override
    public GameConfig createDefaultConfig() {
        ArrayList<GamePlayerType> playerTypes = new ArrayList<>(3);
        playerTypes.add(new GamePlayerType("Human") {
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionHumanPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Simple AI") {
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionSimpleAIPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Smart AI") {
            @Override
            public GamePlayer createPlayer(String name) {
                return new DominionSmartAIPlayer(name);
            }
        });
        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 4, "Dominion", PORT_NUMBER);
        defaultConfig.addPlayer("You", 0);
        defaultConfig.addPlayer("Computer 1", 1);
        defaultConfig.addPlayer("Computer 2", 1);
        defaultConfig.addPlayer("Smart Computer", 2);
        defaultConfig.setRemoteData("Remote player", "", 0);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new DominionLocalGame(this);
    }
}