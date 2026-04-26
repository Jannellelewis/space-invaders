import java.lang.reflect.Field;

public class ModelTester {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        int passed = 0;
        int total = 11;

        if (testPlayerCannotMovePastLeftEdge(model)) passed++;
        if (testPlayerCannotMovePastRightEdge(model)) passed++;
        if (testFireWhileBulletActiveDoesNothing(model)) passed++;
        if (testBulletReachesTopIsRemoved(model)) passed++;
        if (testDestroyAlienIncreasesScore(model)) passed++;
        if (testLosingAllLivesTriggersGameOver(model)) passed++;
        if (testInitialState()) passed++;
        if (testPlayerMovement()) passed++;
        if (testPlayerBoundary()) passed++;
        if (testBulletFiring()) passed++;
        if (testAlienDestruction()) passed++;
        if (testGameOver()) passed++;

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

    private static boolean testInitialState() {
        GameModel model = new GameModel();
        boolean allPass = true;
        allPass &= printResult("player starts with 3 lives", model.getLives() == 3);
        allPass &= printResult("score starts at zero", model.getScore() == 0);
        allPass &= printResult("no bullet at start", !model.isPlayerBulletActive());
        allPass &= printResult("game is not over at start", !model.isGameOver());
        return allPass;
    }

    private static boolean testPlayerMovement() {
        GameModel model = new GameModel();
        int startX = model.getPlayerX();
        model.movePlayerRight();
        boolean allPass = true;
        allPass &= printResult("moving right increases x", model.getPlayerX() > startX);

        // Drive the player as far left as possible
        for (int i = 0; i < 200; i++) model.movePlayerLeft();
        allPass &= printResult("player x never goes below 0", model.getPlayerX() >= 0);
        return allPass;
    }

    private static boolean testPlayerBoundary() {
        GameModel model = new GameModel();
        for (int i = 0; i < 200; i++) {
            model.movePlayerLeft();
        }
        return printResult("player boundary never drops below zero", model.getPlayerX() >= 0);
    }

    private static boolean testBulletFiring() {
        GameModel model = new GameModel();
        model.firePlayerBullet();
        boolean allPass = true;
        allPass &= printResult("firing creates a bullet", model.isPlayerBulletActive());
        model.firePlayerBullet();             // fire again while one is in flight
        allPass &= printResult("cannot fire a second bullet", model.isPlayerBulletActive());
        // (this is a weak check — we want exactly one bullet, not two)
        return allPass;
    }

    private static boolean testAlienDestruction() {
        GameModel model = new GameModel();
        int before = model.getAlienCount();
        model.destroyAlien(0, 0);
        boolean allPass = true;
        allPass &= printResult("alien count decreases on hit", model.getAlienCount() == before - 1);
        allPass &= printResult("score increases on hit", model.getScore() > 0);
        return allPass;
    }

    private static boolean testGameOver() {
        GameModel model = new GameModel();
        // Destroy all aliens
        for (int row = 0; row < 5; row++)
            for (int col = 0; col < 11; col++)
                model.destroyAlien(row, col);
        model.checkCollisions();
        boolean allPass = printResult("game over when all aliens gone", model.isGameOver());
        return allPass;
    }

    private static boolean printResult(String testName, boolean passed) {
        System.out.printf("%s: %s%n", testName, passed ? "PASS" : "FAIL");
        return passed;
    }
}
