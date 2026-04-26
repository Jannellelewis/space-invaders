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
    private boolean resetScreenVisible;

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
                if (!resetScreenVisible) {
                    model.movePlayerLeft();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!resetScreenVisible) {
                    model.movePlayerRight();
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!resetScreenVisible) {
                    model.firePlayerBullet();
                }
                break;
            case KeyEvent.VK_R:
                handleResetRequest();
                break;
            case KeyEvent.VK_ESCAPE:
                if (resetScreenVisible) {
                    cancelResetScreen();
                }
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

    private void handleResetRequest() {
        if (resetScreenVisible) {
            model.reset();
            resetScreenVisible = false;
            view.setResetScreenVisible(false);
            if (!gameTimer.isRunning()) {
                gameTimer.start();
            }
        } else {
            resetScreenVisible = true;
            view.setResetScreenVisible(true);
            gameTimer.stop();
        }
        view.repaint();
    }

    private void cancelResetScreen() {
        resetScreenVisible = false;
        view.setResetScreenVisible(false);
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        view.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController());
    }
}

