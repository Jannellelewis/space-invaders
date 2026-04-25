import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// GameController.java
// This class connects the model and view, handles input, and runs the game loop.
public class GameController {
    private final GameModel model;
    private final GameView view;
    private final JFrame frame;
    private final Timer gameTimer;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        frame.setVisible(true);

        gameTimer = new Timer(50, e -> gameLoop());
        gameTimer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                model.movePlayerLeft();
                break;
            case KeyEvent.VK_RIGHT:
                model.movePlayerRight();
                break;
            case KeyEvent.VK_SPACE:
                model.firePlayerBullet();
                break;
        }
    }

    private void gameLoop() {
        model.tick();
        view.repaint();
        if (model.isGameOver()) {
            gameTimer.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController());
    }
}

