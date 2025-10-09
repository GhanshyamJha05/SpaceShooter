import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SpaceShooter extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private int spaceshipX = 275;
    private final int spaceshipY = 430;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Star> stars = new ArrayList<>();
    private int score = 0;
    private boolean gameOver = false;

    private Random rand = new Random();

    public SpaceShooter() {
        setPreferredSize(new Dimension(600, 500));
        setFocusable(true);
        addKeyListener(this);

        // Create starfield
        for (int i = 0; i < 100; i++) {
            stars.add(new Star(rand.nextInt(600), rand.nextInt(500), rand.nextInt(3) + 1));
        }

        timer = new Timer(15, this);
        timer.start();

        // Spawn enemies every 1.8 seconds
        new Timer(1800, e -> {
            if (!gameOver) {
                enemies.add(new Enemy(rand.nextInt(550), -40));
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw gradient background (spacey)
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(10, 10, 30), 0, getHeight(), Color.black);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw stars with twinkle effect
        g2d.setColor(Color.white);
        for (Star s : stars) {
            int alpha = 100 + (int)(Math.sin(System.currentTimeMillis() / 300.0 + s.twinkleOffset) * 100);
            alpha = Math.min(Math.max(alpha, 50), 255);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
            g2d.fillOval(s.x, s.y, s.size, s.size);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        if (gameOver) {
            // Draw Game Over Screen
            g2d.setColor(new Color(255, 50, 50));
            g2d.setFont(new Font("Verdana", Font.BOLD, 60));
            drawCenteredString(g2d, "GAME OVER", getWidth(), getHeight() / 2 - 30);
            g2d.setFont(new Font("Verdana", Font.BOLD, 30));
            drawCenteredString(g2d, "Score: " + score, getWidth(), getHeight() / 2 + 20);
            g2d.setFont(new Font("Verdana", Font.PLAIN, 20));
            drawCenteredString(g2d, "Press ENTER to Restart", getWidth(), getHeight() / 2 + 60);
            return;
        }

        // Draw spaceship (a cool triangle with fins)
        g2d.setColor(Color.cyan);
        int[] xPoints = {spaceshipX, spaceshipX + 25, spaceshipX + 50};
        int[] yPoints = {spaceshipY + 40, spaceshipY, spaceshipY + 40};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Fins
        g2d.setColor(Color.blue.darker());
        g2d.fillRect(spaceshipX + 5, spaceshipY + 40, 10, 5);
        g2d.fillRect(spaceshipX + 35, spaceshipY + 40, 10, 5);

        // Cockpit window
        g2d.setColor(new Color(0, 200, 255, 180));
        g2d.fillOval(spaceshipX + 15, spaceshipY + 10, 20, 20);

        // Draw bullets with glow effect
        for (Bullet b : bullets) {
            g2d.setColor(new Color(255, 255, 100));
            g2d.fillOval(b.x, b.y, 6, 12);
            g2d.setColor(new Color(255, 255, 150, 150));
            g2d.fillOval(b.x - 1, b.y - 2, 10, 16);
        }

        // Draw enemies as alien ships (with eyes)
        for (Enemy en : enemies) {
            g2d.setColor(new Color(255, 0, 50));
            g2d.fillOval(en.x, en.y, 40, 30);
            g2d.setColor(Color.white);
            g2d.fillOval(en.x + 8, en.y + 8, 8, 8);
            g2d.fillOval(en.x + 24, en.y + 8, 8, 8);
            g2d.setColor(Color.black);
            g2d.fillOval(en.x + 10, en.y + 10, 4, 4);
            g2d.fillOval(en.x + 26, en.y + 10, 4, 4);
        }

        // Draw Score with shadow
        String scoreText = "Score: " + score;
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(Color.black);
        g2d.drawString(scoreText, 12, 32);
        g2d.setColor(Color.yellow);
        g2d.drawString(scoreText, 10, 30);
    }

    private void drawCenteredString(Graphics2D g, String text, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        // Move spaceship
        if (leftPressed && spaceshipX > 0) spaceshipX -= 7;
        if (rightPressed && spaceshipX < getWidth() - 50) spaceshipX += 7;

        // Move bullets
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet b = bulletIter.next();
            b.y -= 10;
            if (b.y < -20) {
                bulletIter.remove();
            }
        }

        // Move enemies
        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy en = enemyIter.next();
            en.y += 4;
            if (en.y > getHeight()) {
                gameOver = true;
            }
        }

        // Check collisions
        bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet b = bulletIter.next();
            enemyIter = enemies.iterator();
            while (enemyIter.hasNext()) {
                Enemy en = enemyIter.next();
                Rectangle bulletRect = new Rectangle(b.x, b.y, 6, 12);
                Rectangle enemyRect = new Rectangle(en.x, en.y, 40, 30);
                if (bulletRect.intersects(enemyRect)) {
                    bulletIter.remove();
                    enemyIter.remove();
                    score += 15;
                    break;
                }
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
            return;
        }
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (key == KeyEvent.VK_SPACE && !gameOver) {
            bullets.add(new Bullet(spaceshipX + 22, spaceshipY));
        }
    }

    private void restartGame() {
        score = 0;
        enemies.clear();
        bullets.clear();
        gameOver = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Inner classes

    private static class Bullet {
        int x, y;

        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Enemy {
        int x, y;

        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Star {
        int x, y, size;
        double twinkleOffset;

        Star(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.twinkleOffset = Math.random() * 10;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter Deluxe");
        SpaceShooter gamePanel = new SpaceShooter();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
