Prompt 1:
USED: I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.
RESULTS: a blank window opens and 3 java classes were made
FIXES: none
OBSERVATIONS: AI did this pretty fast

Prompt 2: 
USED: Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.
RESULTS: the player can move to the left and the right, the alien throws bullets at you randomly and the alien moves around and gets closer to you. if a bullet hits you, you lose a life (but nothing can actually be seen yet)
FIXES: none
OBSERVATIONS: i wasnt sure if the player was supposed to send a bullet or just get hit by one

Prompt 3: 
USED: Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.
RESULTS: draws the player, alien and the bullets. Now the score changes when you get hit. there is a game over message screen
FIXES: none
OBSERVATIONS: now i can actually see what the things look like

Prompt 4: 
USED: Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.
RESULTS: connects so now i can move around and i can fire bullets to the alien
FIXES: none
OBSERVATIONS:its starting to look like the real game

Prompt 5: 
USED:Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.
RESULTS: it terminal it prints the differant types of tests, all 5 tests passed 
FIXES: none
OBSERVATIONS: i was glad all the test passed the first time so didnt have to debug

Prompt 6: 
USED: In GameModel.java, add a list of shield rectangles positioned between the player and the alien formation. Reduce a shield's health when hit by a bullet from either side. Remove the shield when health reaches zero. No Swing imports
RESULTS: 4 rectangle shields are made but you cant see them yet
FIXES: none
OBSERVATIONS: I though i would be able to see the shields after this prompt but the next one draws it

Prompt 7: 
USED: In GameView.java's paintComponent method only, draw the shields from the model's shield list. Use the shield's health value to choose a color from full green to dim red. Do not call any model mutating methods.
RESULTS: now i can see the shields and they change color when hit with a bullet because theyre decreasing in health
FIXES: none
OBSERVATIONS: I thought it would be like a shield around the player but i didnt read the prompt close enough

Prompt 8: 
USED: In GameModel.java, increase the alien movement speed each time an alien is destroyed. Expose a method the Controller can call to get the current recommended timer interval. Do not touch the View.
RESULTS: the alien speed increase when they get hit with a bullet from the player
FIXES: none
OBSERVATIONS: the alien gets really fast pretty

Prompt 9:
USED: do a test exact;y like the following but so there are no problems and it runs the same: static void testInitialState() {
    GameModel model = new GameModel();
    check("player starts with 3 lives",   model.getLives() == 3);
    check("score starts at zero",         model.getScore() == 0);
    check("no bullet at start",           model.getPlayerBullet() == null);
    check("game is not over at start",    !model.isGameOver());
}
static void testPlayerMovement() {
    GameModel model = new GameModel();
    int startX = model.getPlayerX();
    model.movePlayerRight();
    check("moving right increases x",     model.getPlayerX() > startX);

    // Drive the player as far left as possible
    for (int i = 0; i < 200; i++) model.movePlayerLeft();
    check("player x never goes below 0",  model.getPlayerX() >= 0);
}
static void testBulletFiring() {
    GameModel model = new GameModel();
    model.firePlayerBullet();
    check("firing creates a bullet",      model.getPlayerBullet() != null);
    model.firePlayerBullet();             // fire again while one is in flight
    check("cannot fire a second bullet",  model.getPlayerBullet() != null);
    // (this is a weak check — we want exactly one bullet, not two)
}
RESULTS: all those test passed
FIXES: at first it replaced the previous test but i told it that it should be in addition to
OBSERVATIONS: im glad they passed so i didnt have to try and fix stuff

Prompt 10:
USED: add this test into the modeltester file so that it will run with my program: static void testAlienDestruction() {
GameModel model = new GameModel();
int before = model.getAlienCount();
model.destroyAlien(0, 0);
check("alien count decreases on hit",
model.getAlienCount() == before - 1);
check("score increases on hit",
model.getScore() > 0);
}
static void testGameOver() {
GameModel model = new GameModel();
// Destroy all aliens
for (int row = 0; row < 5; row++)
for (int col = 0; col < 11; col++)
model.destroyAlien(row, col);
model.checkCollisions();
check("game over when all aliens gone",
model.isGameOver());
}
RESULTS: ther was an error at first but when i fixed it, all the tests passed
FIXES: i said this to the AI 'theres an error: Exception in thread "main" java.lang.Error: Unresolved compilation problem: 
        The method allAliensDestroyed() is undefined for the type GameModel

        at GameModel.isGameOver(GameModel.java:307)
        at ModelTester.testLosingAllLivesTriggersGameOver(ModelTester.java:103)
        at ModelTester.main(ModelTester.java:14)
PS C:\Users\Jannelle\Downloads\space-invaders> '
OBSERVATIONS: had some errors but thats inevitable