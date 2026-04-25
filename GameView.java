import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

// GameView.java
// This class extends JPanel and is responsible for rendering the game.
// It draws the player, alien formation, bullets, score, lives, and game-over message.
public class GameView extends JPanel {
    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 20;
    private static final int ALIEN_WIDTH = 36;
    private static final int ALIEN_HEIGHT = 24;
    private static final int ALIEN_HORIZONTAL_SPACING = 10;
    private static final int ALIEN_VERTICAL_SPACING = 10;
    private static final int PLAYER_BULLET_WIDTH = 6;
    private static final int PLAYER_BULLET_HEIGHT = 12;
    private static final int ALIEN_BULLET_WIDTH = 6;
    private static final int ALIEN_BULLET_HEIGHT = 12;

    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.FIELD_WIDTH, GameModel.FIELD_HEIGHT));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawScoreAndLives(g);
        drawPlayer(g);
        drawAliens(g);
        drawShields(g);
        drawBullets(g);
        if (model.isGameOver()) {
            drawGameOver(g);
        }
    }

    private void drawBackground(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawScoreAndLives(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.drawString("Score: " + model.getScore(), 16, 24);
        g.drawString("Lives: " + model.getLives(), 16, 48);
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    private void drawAliens(Graphics g) {
        boolean[][] aliens = model.getAliensAlive();
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (!aliens[row][col]) {
                    continue;
                }
                int x = model.getAlienFormationX() + col * (ALIEN_WIDTH + ALIEN_HORIZONTAL_SPACING);
                int y = model.getAlienFormationY() + row * (ALIEN_HEIGHT + ALIEN_VERTICAL_SPACING);
                g.setColor(Color.GREEN);
                g.fillRect(x, y, ALIEN_WIDTH, ALIEN_HEIGHT);
                g.setColor(Color.WHITE);
                g.drawRect(x, y, ALIEN_WIDTH, ALIEN_HEIGHT);
            }
        }
    }

    private void drawShields(Graphics g) {
        List<GameModel.Shield> shields = model.getShields();
        for (GameModel.Shield shield : shields) {
            if (shield.health <= 0) {
                continue;
            }
            if (shield.health >= 3) {
                g.setColor(Color.GREEN);
            } else if (shield.health == 2) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED.darker());
            }
            g.fillRect(shield.x, shield.y, shield.width, shield.height);
            g.setColor(Color.WHITE);
            g.drawRect(shield.x, shield.y, shield.width, shield.height);
        }
    }

    private void drawBullets(Graphics g) {
        if (model.isPlayerBulletActive()) {
            g.setColor(Color.YELLOW);
            g.fillRect(model.getPlayerBulletX(), model.getPlayerBulletY(), PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT);
        }
        List<GameModel.Bullet> alienBullets = model.getAlienBullets();
        g.setColor(Color.RED);
        for (GameModel.Bullet bullet : alienBullets) {
            g.fillRect(bullet.x, bullet.y, ALIEN_BULLET_WIDTH, ALIEN_BULLET_HEIGHT);
        }
    }

    private void drawGameOver(Graphics g) {
        String message = "GAME OVER";
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(message);
        int textHeight = metrics.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();
        g.drawString(message, x, y);
    }
}
