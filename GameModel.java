import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// GameModel.java
// This class contains the game state for Space Invaders.
// It has no Swing imports and manages player position, aliens, bullets,
// score, lives, and collision/update rules.
public class GameModel {
    public static final int FIELD_WIDTH = 800;
    public static final int FIELD_HEIGHT = 600;

    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 20;
    private static final int PLAYER_Y = FIELD_HEIGHT - 60;
    private static final int PLAYER_SPEED = 12;

    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLUMNS = 11;
    private static final int ALIEN_WIDTH = 36;
    private static final int ALIEN_HEIGHT = 24;
    private static final int ALIEN_HORIZONTAL_SPACING = 10;
    private static final int ALIEN_VERTICAL_SPACING = 10;
    private static final int ALIEN_INITIAL_X = 60;
    private static final int ALIEN_INITIAL_Y = 60;
    private static final int ALIEN_MOVE_STEP = 8;
    private static final int ALIEN_DESCENT = 20;
    private static final int ALIEN_SPEED_INCREMENT = 1;
    private static final int ANIMATION_TOGGLE_TICKS = 10;
    private static final int BASE_TIMER_INTERVAL_MS = 50;
    private static final int TIMER_DECREMENT_PER_SPEED = 2;
    private static final int MIN_TIMER_INTERVAL_MS = 20;

    private static final int PLAYER_BULLET_WIDTH = 6;
    private static final int PLAYER_BULLET_HEIGHT = 12;
    private static final int PLAYER_BULLET_SPEED = -14;

    private static final int ALIEN_BULLET_WIDTH = 6;
    private static final int ALIEN_BULLET_HEIGHT = 12;
    private static final int ALIEN_BULLET_SPEED = 8;
    private static final int MAX_ALIEN_BULLETS = 6;

    private static final int SHIELD_COUNT = 4;
    private static final int SHIELD_WIDTH = 80;
    private static final int SHIELD_HEIGHT = 32;
    private static final int SHIELD_HEALTH = 3;
    private static final int SHIELD_Y = FIELD_HEIGHT - 200;

    private final Random random = new Random();

    private int playerX;
    private final int playerY = PLAYER_Y;
    private boolean playerBulletActive;
    private int playerBulletX;
    private int playerBulletY;

    private final List<Bullet> alienBullets = new ArrayList<>();
    private final List<Shield> shields = new ArrayList<>();
    private int alienMoveStep = ALIEN_MOVE_STEP;
    private boolean animFrame;
    private int animationTick;

    private final boolean[][] aliensAlive = new boolean[ALIEN_ROWS][ALIEN_COLUMNS];
    private int alienFormationX;
    private int alienFormationY;
    private int alienDirection = 1;

    private int score;
    private int lives;

    public GameModel() {
        reset();
    }

    public void reset() {
        playerX = FIELD_WIDTH / 2 - PLAYER_WIDTH / 2;
        playerBulletActive = false;
        playerBulletX = 0;
        playerBulletY = 0;
        alienBullets.clear();
        shields.clear();
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                aliensAlive[row][col] = true;
            }
        }
        int spacing = (FIELD_WIDTH - SHIELD_COUNT * SHIELD_WIDTH) / (SHIELD_COUNT + 1);
        for (int i = 0; i < SHIELD_COUNT; i++) {
            int x = spacing + i * (SHIELD_WIDTH + spacing);
            shields.add(new Shield(x, SHIELD_Y, SHIELD_WIDTH, SHIELD_HEIGHT, SHIELD_HEALTH));
        }
        alienFormationX = ALIEN_INITIAL_X;
        alienFormationY = ALIEN_INITIAL_Y;
        alienDirection = 1;
        alienMoveStep = ALIEN_MOVE_STEP;
        animFrame = false;
        animationTick = 0;
        score = 0;
        lives = 3;
    }

    public void movePlayerLeft() {
        playerX -= PLAYER_SPEED;
        if (playerX < 0) {
            playerX = 0;
        }
    }

    public void movePlayerRight() {
        playerX += PLAYER_SPEED;
        if (playerX + PLAYER_WIDTH > FIELD_WIDTH) {
            playerX = FIELD_WIDTH - PLAYER_WIDTH;
        }
    }

    public void firePlayerBullet() {
        if (!playerBulletActive) {
            playerBulletActive = true;
            playerBulletX = playerX + PLAYER_WIDTH / 2 - PLAYER_BULLET_WIDTH / 2;
            playerBulletY = playerY - PLAYER_BULLET_HEIGHT;
        }
    }

    public void tick() {
        updateAnimation();
        advancePlayerBullet();
        moveAliens();
        maybeFireAlienBullet();
        advanceAlienBullets();
        detectCollisions();
    }

    private void updateAnimation() {
        animationTick++;
        if (animationTick >= ANIMATION_TOGGLE_TICKS) {
            animationTick = 0;
            animFrame = !animFrame;
        }
    }

    private void advancePlayerBullet() {
        if (!playerBulletActive) {
            return;
        }
        playerBulletY += PLAYER_BULLET_SPEED;
        if (playerBulletY + PLAYER_BULLET_HEIGHT < 0) {
            playerBulletActive = false;
        }
    }

    private void moveAliens() {
        int formationWidth = ALIEN_COLUMNS * (ALIEN_WIDTH + ALIEN_HORIZONTAL_SPACING) - ALIEN_HORIZONTAL_SPACING;
        int nextX = alienFormationX + alienDirection * alienMoveStep;
        boolean hitRightEdge = nextX + formationWidth > FIELD_WIDTH - 20;
        boolean hitLeftEdge = nextX < 20;

        if (hitRightEdge || hitLeftEdge) {
            alienDirection *= -1;
            alienFormationY += ALIEN_DESCENT;
        } else {
            alienFormationX = nextX;
        }
    }

    private void maybeFireAlienBullet() {
        if (alienBullets.size() >= MAX_ALIEN_BULLETS) {
            return;
        }
        if (random.nextInt(100) >= 5) {
            return;
        }

        int row = random.nextInt(ALIEN_ROWS);
        int col = random.nextInt(ALIEN_COLUMNS);

        for (int attempt = 0; attempt < ALIEN_ROWS * ALIEN_COLUMNS; attempt++) {
            int r = (row + attempt / ALIEN_COLUMNS) % ALIEN_ROWS;
            int c = (col + attempt) % ALIEN_COLUMNS;
            if (aliensAlive[r][c]) {
                int x = alienFormationX + c * (ALIEN_WIDTH + ALIEN_HORIZONTAL_SPACING) + ALIEN_WIDTH / 2 - ALIEN_BULLET_WIDTH / 2;
                int y = alienFormationY + r * (ALIEN_HEIGHT + ALIEN_VERTICAL_SPACING) + ALIEN_HEIGHT;
                alienBullets.add(new Bullet(x, y, ALIEN_BULLET_SPEED));
                break;
            }
        }
    }

    private void advanceAlienBullets() {
        alienBullets.removeIf(bullet -> {
            bullet.y += bullet.dy;
            return bullet.y > FIELD_HEIGHT;
        });
    }

    private void detectCollisions() {
        if (playerBulletActive) {
            boolean bulletConsumed = false;
            for (int i = shields.size() - 1; i >= 0 && !bulletConsumed; i--) {
                Shield shield = shields.get(i);
                if (rectanglesIntersect(playerBulletX, playerBulletY, PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT,
                        shield.x, shield.y, shield.width, shield.height)) {
                    shield.health -= 1;
                    playerBulletActive = false;
                    bulletConsumed = true;
                    if (shield.health <= 0) {
                        shields.remove(i);
                    }
                }
            }

            if (!bulletConsumed) {
                for (int row = 0; row < ALIEN_ROWS; row++) {
                    for (int col = 0; col < ALIEN_COLUMNS; col++) {
                        if (!aliensAlive[row][col]) {
                            continue;
                        }
                        int alienX = alienFormationX + col * (ALIEN_WIDTH + ALIEN_HORIZONTAL_SPACING);
                        int alienY = alienFormationY + row * (ALIEN_HEIGHT + ALIEN_VERTICAL_SPACING);
                        if (rectanglesIntersect(playerBulletX, playerBulletY, PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT,
                                alienX, alienY, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                            aliensAlive[row][col] = false;
                            playerBulletActive = false;
                            score += 10;
                            alienMoveStep += ALIEN_SPEED_INCREMENT;
                            bulletConsumed = true;
                            break;
                        }
                    }
                    if (bulletConsumed) {
                        break;
                    }
                }
            }
        }

        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = alienBullets.get(i);
            boolean bulletRemoved = false;
            for (int j = shields.size() - 1; j >= 0 && !bulletRemoved; j--) {
                Shield shield = shields.get(j);
                if (rectanglesIntersect(bullet.x, bullet.y, ALIEN_BULLET_WIDTH, ALIEN_BULLET_HEIGHT,
                        shield.x, shield.y, shield.width, shield.height)) {
                    shield.health -= 1;
                    alienBullets.remove(i);
                    bulletRemoved = true;
                    if (shield.health <= 0) {
                        shields.remove(j);
                    }
                }
            }
            if (bulletRemoved) {
                continue;
            }
            if (rectanglesIntersect(bullet.x, bullet.y, ALIEN_BULLET_WIDTH, ALIEN_BULLET_HEIGHT,
                    playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                alienBullets.remove(i);
                lives = Math.max(0, lives - 1);
            }
        }
    }

    private boolean rectanglesIntersect(int x1, int y1, int w1, int h1,
                                        int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public boolean isPlayerBulletActive() {
        return playerBulletActive;
    }

    public int getPlayerBulletX() {
        return playerBulletX;
    }

    public int getPlayerBulletY() {
        return playerBulletY;
    }

    public List<Bullet> getAlienBullets() {
        return new ArrayList<>(alienBullets);
    }

    public boolean[][] getAliensAlive() {
        boolean[][] copy = new boolean[ALIEN_ROWS][ALIEN_COLUMNS];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            System.arraycopy(aliensAlive[row], 0, copy[row], 0, ALIEN_COLUMNS);
        }
        return copy;
    }

    public int getAlienFormationX() {
        return alienFormationX;
    }

    public int getAlienFormationY() {
        return alienFormationY;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getRecommendedTimerInterval() {
        int interval = BASE_TIMER_INTERVAL_MS - (alienMoveStep - ALIEN_MOVE_STEP) * TIMER_DECREMENT_PER_SPEED;
        return Math.max(MIN_TIMER_INTERVAL_MS, interval);
    }

    public boolean isAnimFrame() {
        return animFrame;
    }

    public boolean isGameOver() {
        return lives <= 0 || allAliensDestroyed();
    }

    public boolean allAliensDestroyed() {
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                if (aliensAlive[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getAlienCount() {
        int count = 0;
        for (boolean[] row : aliensAlive) {
            for (boolean alive : row) {
                if (alive) count++;
            }
        }
        return count;
    }

    public void destroyAlien(int row, int col) {
        if (aliensAlive[row][col]) {
            aliensAlive[row][col] = false;
            score += 10;
            alienMoveStep += ALIEN_SPEED_INCREMENT;
        }
    }

    public void checkCollisions() {
        detectCollisions();
    }

    public List<Shield> getShields() {
        return new ArrayList<>(shields);
    }

    public static class Bullet {
        public int x;
        public int y;
        public final int dy;

        public Bullet(int x, int y, int dy) {
            this.x = x;
            this.y = y;
            this.dy = dy;
        }
    }

    public static class Shield {
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public int health;

        public Shield(int x, int y, int width, int height, int health) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.health = health;
        }
    }
}
