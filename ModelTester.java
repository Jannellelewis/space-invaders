import java.lang.reflect.Field;

public class ModelTester {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        int passed = 0;
        int total = 5;

        if (testPlayerCannotMovePastLeftEdge(model)) passed++;
        if (testPlayerCannotMovePastRightEdge(model)) passed++;
        if (testFireWhileBulletActiveDoesNothing(model)) passed++;
        if (testBulletReachesTopIsRemoved(model)) passed++;
        if (testDestroyAlienIncreasesScore(model)) passed++;
        if (testLosingAllLivesTriggersGameOver(model)) passed++;

        System.out.printf("%d/%d tests passed.%n", passed, total);
    }

    private static boolean testPlayerCannotMovePastLeftEdge(GameModel model) {
        model.reset();
        for (int i = 0; i < 100; i++) {
            model.movePlayerLeft();
        }
        boolean passed = model.getPlayerX() == 0;
        printResult("Player cannot move past left edge", passed);
        return passed;
    }

    private static boolean testPlayerCannotMovePastRightEdge(GameModel model) {
        model.reset();
        for (int i = 0; i < 100; i++) {
            model.movePlayerRight();
        }
        boolean passed = model.getPlayerX() + 40 == GameModel.FIELD_WIDTH;
        printResult("Player cannot move past right edge", passed);
        return passed;
    }

    private static boolean testFireWhileBulletActiveDoesNothing(GameModel model) {
        model.reset();
        model.firePlayerBullet();
        int firstX = model.getPlayerBulletX();
        int firstY = model.getPlayerBulletY();
        model.firePlayerBullet();
        boolean passed = model.isPlayerBulletActive()
                && model.getPlayerBulletX() == firstX
                && model.getPlayerBulletY() == firstY;
        printResult("Firing while bullet already in flight does nothing", passed);
        return passed;
    }

    private static boolean testBulletReachesTopIsRemoved(GameModel model) {
        model.reset();
        model.firePlayerBullet();
        for (int i = 0; i < 100; i++) {
            model.tick();
            if (!model.isPlayerBulletActive()) {
                break;
            }
        }
        boolean passed = !model.isPlayerBulletActive();
        printResult("Bullet that reaches the top is removed", passed);
        return passed;
    }

    private static boolean testDestroyAlienIncreasesScore(GameModel model) {
        model.reset();
        int targetX = model.getAlienFormationX();
        while (model.getPlayerX() > targetX) {
            model.movePlayerLeft();
        }
        while (model.getPlayerX() < targetX) {
            model.movePlayerRight();
        }
        int initialScore = model.getScore();
        model.firePlayerBullet();
        for (int i = 0; i < 100; i++) {
            model.tick();
            if (model.getScore() > initialScore) {
                break;
            }
        }
        boolean passed = model.getScore() > initialScore;
        printResult("Destroying an alien increases the score", passed);
        return passed;
    }

    private static boolean testLosingAllLivesTriggersGameOver(GameModel model) {
        model.reset();
        try {
            Field livesField = GameModel.class.getDeclaredField("lives");
            livesField.setAccessible(true);
            livesField.setInt(model, 0);
        } catch (Exception e) {
            printResult("Losing all lives triggers game-over state", false);
            return false;
        }
        boolean passed = model.isGameOver();
        printResult("Losing all lives triggers game-over state", passed);
        return passed;
    }

    private static void printResult(String testName, boolean passed) {
        System.out.printf("%s: %s%n", testName, passed ? "PASS" : "FAIL");
    }
}
