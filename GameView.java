import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

// GameView.java
// This class extends JPanel and is responsible for rendering the game.
// It will draw the player, aliens, shots, and UI elements based on the GameModel.
public class GameView extends JPanel {
    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        // TODO: Configure panel properties and input listeners if needed.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: Draw game objects from the model here.
    }
}
