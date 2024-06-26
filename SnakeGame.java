import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class SnakeGame extends JFrame implements KeyListener, ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int CELL_SIZE = 20;

    private LinkedList<Point> snake;
    private Point fruit;
    private int direction;
    private boolean isRunning;

    private JButton playButton;

    private BufferedImage offscreenImage;
    private Graphics offscreenGraphics;

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);

        snake = new LinkedList<>();
        snake.add(new Point(10, 10));

        direction = KeyEvent.VK_RIGHT;
        isRunning = false;

        // Create a "Play Game" button
        playButton = new JButton("Play Game");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        playButton.setBounds(160, 180, 100, 30);
        add(playButton);

        // Set up the offscreen image for double buffering
        offscreenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        offscreenGraphics = offscreenImage.getGraphics();
    }

    private void startGame() {
        remove(playButton);
        isRunning = true;
        requestFocus();
        spawnFruit(); // Ensure a fruit is available at the beginning
        Timer timer = new Timer(100, this);
        timer.start();
    }

    public void spawnFruit() {
        int x, y;
        do {
            x = (int) (Math.random() * (WIDTH / CELL_SIZE));
            y = (int) (Math.random() * (HEIGHT / CELL_SIZE));
        } while (snake.contains(new Point(x, y)));
        fruit = new Point(x, y);
    }

    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
            checkCollision();
            repaint();
        }
    }

    public void move() {
        Point head = snake.getFirst();
        int x = head.x;
        int y = head.y;

        if (direction == KeyEvent.VK_DOWN) y++;
        if (direction == KeyEvent.VK_UP) y--;
        if (direction == KeyEvent.VK_LEFT) x--;
        if (direction == KeyEvent.VK_RIGHT) x++;

        Point newHead = new Point(x, y);

        if (fruit.equals(newHead)) {
            snake.addFirst(fruit);
            spawnFruit();
        } else {
            snake.addFirst(newHead);
            snake.removeLast();
        }
    }

    public void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH / CELL_SIZE || head.y < 0 || head.y >= HEIGHT / CELL_SIZE) {
            isRunning = false;
            int choice = JOptionPane.showConfirmDialog(this, "Game Over! Restart?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                isRunning = false;
                int choice = JOptionPane.showConfirmDialog(this, "Game Over! Restart?", "Game Over", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    System.exit(0);
                }
                break;
            }
        }
    }

    public void restartGame() {
        snake.clear();
        snake.add(new Point(10, 10));
        direction = KeyEvent.VK_RIGHT;
        isRunning = false;
        add(playButton);
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!isRunning) {
            return;
        }

        if ((key == KeyEvent.VK_LEFT) && (direction != KeyEvent.VK_RIGHT)) direction = KeyEvent.VK_LEFT;
        if ((key == KeyEvent.VK_RIGHT) && (direction != KeyEvent.VK_LEFT)) direction = KeyEvent.VK_RIGHT;
        if ((key == KeyEvent.VK_UP) && (direction != KeyEvent.VK_DOWN)) direction = KeyEvent.VK_UP;
        if ((key == KeyEvent.VK_DOWN) && (direction != KeyEvent.VK_UP)) direction = KeyEvent.VK_DOWN;
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void paint(Graphics g) {
        super.paint(g); // Call the superclass's paint method to clear the background

        // Draw the grid on the offscreen image
        offscreenGraphics.setColor(Color.DARK_GRAY);
        offscreenGraphics.fillRect(0, 0, WIDTH, HEIGHT);
        offscreenGraphics.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= WIDTH / CELL_SIZE; i++) {
            offscreenGraphics.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT);
        }
        for (int i = 0; i <= HEIGHT / CELL_SIZE; i++) {
            offscreenGraphics.drawLine(0, i * CELL_SIZE, WIDTH, i * CELL_SIZE);
        }

        if (isRunning) {
            offscreenGraphics.setColor(Color.RED);
            offscreenGraphics.fillRect(fruit.x * CELL_SIZE, fruit.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            offscreenGraphics.setColor(Color.GREEN.darker());
            for (Point point : snake) {
                offscreenGraphics.fillOval(point.x * CELL_SIZE, point.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw the offscreen image to the screen
        g.drawImage(offscreenImage, 0, 0, this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setVisible(true);
        });
    }
}
