import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// GameController.java
// This class connects the model and view, and starts the application.
public class GameController {
    private final GameModel model;
    private final GameView view;
    private final JFrame frame;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController());
    }
}
