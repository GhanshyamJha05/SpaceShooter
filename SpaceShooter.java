import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.io.*;

public class SpaceShooter extends JPanel implements ActionListener, KeyListener {

    // Game States
    private enum GameState { MENU, PLAYING, PAUSED, GAME_OVER }
    private GameState gameState = GameState.MENU;
    private int selectedDifficulty = 1; // 1=Easy, 2=Normal, 3=Hard

    private Timer timer;
    private int spaceshipX = 275;
    private final int spaceshipY = 430;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<PowerUp> powerUps = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();
    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<FloatingText> floatingTexts = new ArrayList<>();
    private int score = 0;
    private int highScore = 0;
    private int level = 1;
    private int lives = 3;
    private int health = 100;
    private int bombs = 3;
    private final int MAX_HEALTH = 100;
    private int weaponType = 0; // 0=Normal, 1=Rapid, 2=Spread, 3=Laser
    private int rapidFireCounter = 0;
    private int shieldTimer = 0;
    private int invincibleTimer = 0;
    private int combo = 0;
    private long lastKillTime = 0;
    private int screenShake = 0;

    private Random rand = new Random();

    public SpaceShooter() {
        setPreferredSize(new Dimension(600, 500));
        setFocusable(true);
        addKeyListener(this);
        loadHighScore();

        // Create starfield
        for (int i = 0; i < 100; i++) {
            stars.add(new Star(rand.nextInt(600), rand.nextInt(500), rand.nextInt(3) + 1));
        }

        timer = new Timer(15, this);
        timer.start();
    }

    private void startGame() {
        score = 0;
        level = 1;
        lives = 3;
        health = MAX_HEALTH;
        bombs = 3;
        gameState = GameState.PLAYING;
        enemies.clear();
        bullets.clear();
        enemyBullets.clear();
        powerUps.clear();
        particles.clear();
        floatingTexts.clear();
        weaponType = 0;
        combo = 0;
        invincibleTimer = 0;
        
        spawnEnemyWave();
    }

    private void spawnEnemyWave() {
        new Timer(getEnemySpawnRate(), e -> {
            if (gameState == GameState.PLAYING) {
                spawnRandomEnemy();
            }
        }).start();
    }

    private void spawnRandomEnemy() {
        if (gameState != GameState.PLAYING) return;
        int type = rand.nextInt(100);
        Enemy enemy;
        
        if (type < 60) {
            enemy = new Enemy(rand.nextInt(550), -40, EnemyType.BASIC);
        } else if (type < 85) {
            enemy = new Enemy(rand.nextInt(550), -40, EnemyType.FAST);
        } else if (type < 95) {
            enemy = new Enemy(rand.nextInt(550), -40, EnemyType.HEAVY);
        } else {
            enemy = new Enemy(rand.nextInt(550), -40, EnemyType.BOSS);
        }
        enemies.add(enemy);
    }

    private int getEnemySpawnRate() {
        return Math.max(400, 1800 - (level * 100) - (selectedDifficulty * 150));
    }

    private void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = br.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("highscore.txt"))) {
            pw.println(highScore);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw gradient background
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

        // Apply screen shake
        if (screenShake > 0) {
            int shakeX = rand.nextInt(screenShake) - screenShake / 2;
            int shakeY = rand.nextInt(screenShake) - screenShake / 2;
            g2d.translate(shakeX, shakeY);
            screenShake--;
        }

        if (gameState == GameState.MENU) {
            drawMenu(g2d);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOverScreen(g2d);
        } else if (gameState == GameState.PAUSED) {
            drawPlayingElements(g2d);
            drawPauseScreen(g2d);
        } else {
            drawPlayingElements(g2d);
        }
    }

    private void drawPlayingElements(Graphics2D g2d) {
        // Draw spaceship with shield
        if (invincibleTimer == 0 || (invincibleTimer / 5) % 2 == 0) {
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
        }

        // Shield
        if (shieldTimer > 0) {
            int alpha = (shieldTimer * 255) / 200;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(alpha, 255) / 255f));
            g2d.setColor(new Color(100, 200, 255));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(spaceshipX + 5, spaceshipY, 40, 50);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Draw particles
        for (Particle p : particles) {
            g2d.setColor(p.color);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, p.life / 255f));
            g2d.fillOval(p.x, p.y, p.size, p.size);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Draw bullets with glow effect
        for (Bullet b : bullets) {
            g2d.setColor(new Color(255, 255, 100));
            g2d.fillOval(b.x, b.y, 6, 12);
            g2d.setColor(new Color(255, 255, 150, 150));
            g2d.fillOval(b.x - 1, b.y - 2, 10, 16);
        }

        // Draw enemy bullets
        for (Bullet b : enemyBullets) {
            g2d.setColor(new Color(255, 50, 50));
            g2d.fillOval(b.x, b.y, 8, 8);
            g2d.setColor(new Color(255, 100, 100, 150));
            g2d.drawOval(b.x - 2, b.y - 2, 12, 12);
        }

        // Draw enemies
        for (Enemy en : enemies) {
            drawEnemy(g2d, en);
        }

        // Draw power-ups
        for (PowerUp p : powerUps) {
            drawPowerUp(g2d, p);
        }

        // Draw HUD
        drawHUD(g2d);
    }

    private void drawEnemy(Graphics2D g2d, Enemy en) {
        switch (en.type) {
            case BASIC:
                g2d.setColor(new Color(255, 0, 50));
                g2d.fillOval(en.x, en.y, 40, 30);
                break;
            case FAST:
                g2d.setColor(new Color(255, 100, 200));
                int[] xp = {en.x + 20, en.x, en.x + 40};
                int[] yp = {en.y, en.y + 30, en.y + 30};
                g2d.fillPolygon(xp, yp, 3);
                break;
            case HEAVY:
                g2d.setColor(new Color(200, 50, 50));
                g2d.fillOval(en.x - 5, en.y - 5, 50, 40);
                g2d.setColor(Color.darkGray);
                g2d.fillRect(en.x + 5, en.y + 10, 10, 10);
                g2d.fillRect(en.x + 25, en.y + 10, 10, 10);
                break;
            case BOSS:
                float healthPercent = (float)en.health / en.maxHealth;
                if (healthPercent > 0.7) {
                    g2d.setColor(new Color(200, 20, 20)); // Red
                } else if (healthPercent > 0.4) {
                    g2d.setColor(new Color(200, 100, 0)); // Orange
                } else {
                    g2d.setColor(new Color(150, 0, 150)); // Purple (Enraged)
                }
                g2d.fillRect(en.x, en.y, 60, 50);
                g2d.setColor(new Color(255, 100, 100));
                g2d.fillRect(en.x + 5, en.y + 5, 50, 40);
                g2d.setColor(Color.yellow);
                g2d.fillOval(en.x + 10, en.y + 10, 10, 10);
                g2d.fillOval(en.x + 40, en.y + 10, 10, 10);
                break;
        }
        
        // Health bar for heavy/boss
        if (en.type == EnemyType.HEAVY || en.type == EnemyType.BOSS) {
            int barWidth = (en.type == EnemyType.BOSS) ? 60 : 40;
            g2d.setColor(Color.red);
            g2d.fillRect(en.x, en.y - 10, barWidth, 5);
            g2d.setColor(Color.green);
            g2d.fillRect(en.x, en.y - 10, (en.health * barWidth) / en.maxHealth, 5);
        }

        // Eyes
        g2d.setColor(Color.white);
        g2d.fillOval(en.x + 8, en.y + 8, 8, 8);
        g2d.fillOval(en.x + (en.type == EnemyType.BOSS ? 44 : 24), en.y + 8, 8, 8);
        g2d.setColor(Color.black);
        g2d.fillOval(en.x + 10, en.y + 10, 4, 4);
        g2d.fillOval(en.x + (en.type == EnemyType.BOSS ? 46 : 26), en.y + 10, 4, 4);
    }

    private void drawPowerUp(Graphics2D g2d, PowerUp p) {
        g2d.setColor(p.color);
        g2d.fillRect(p.x, p.y, 20, 20);
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(p.icon, p.x + 5, p.y + 15);
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        
        // Health Bar
        g2d.setColor(Color.darkGray);
        g2d.fillRect(10, 20, 150, 15);
        Color healthColor = health > 50 ? Color.green : (health > 20 ? Color.orange : Color.red);
        g2d.setColor(healthColor);
        g2d.fillRect(10, 20, (Math.max(0, health) * 150) / MAX_HEALTH, 15);
        g2d.setColor(Color.white);
        g2d.drawRect(10, 20, 150, 15);
        g2d.setFont(new Font("Verdana", Font.BOLD, 10));
        g2d.drawString("HEALTH", 10, 15);

        // Lives
        for (int i = 0; i < lives; i++) {
            int x = 10 + i * 25;
            int y = 45;
            g2d.setColor(Color.cyan);
            int[] lx = {x, x + 10, x + 20};
            int[] ly = {y + 15, y, y + 15};
            g2d.fillPolygon(lx, ly, 3);
        }
        
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        // Level
        String levelText = "Level: " + level;
        g2d.setColor(Color.yellow);
        g2d.drawString(levelText, 250, 30);
        
        // Score
        String scoreText = "Score: " + score;
        g2d.setColor(Color.cyan);
        g2d.drawString(scoreText, 450, 30);
        
        // Combo
        if (combo > 1) {
            String comboText = "COMBO x" + combo;
            g2d.setColor(new Color(255, 100, 255));
            g2d.setFont(new Font("Verdana", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(comboText)) / 2;
            g2d.drawString(comboText, x, 70);
        }

        // Bombs
        g2d.setFont(new Font("Verdana", Font.BOLD, 12));
        g2d.setColor(Color.orange);
        g2d.drawString("BOMBS: " + bombs + " [B]", 10, 75);

        // Floating texts
        for (FloatingText ft : floatingTexts) {
            g2d.setColor(new Color(ft.color.getRed(), ft.color.getGreen(), ft.color.getBlue(), Math.min(255, ft.life * 5)));
            g2d.setFont(new Font("Verdana", Font.BOLD, 14));
            g2d.drawString(ft.text, ft.x, ft.y);
        }

        // Weapon indicator
        g2d.setFont(new Font("Verdana", Font.PLAIN, 12));
        String weaponName = "";
        switch (weaponType) {
            case 0: weaponName = "Normal"; break;
            case 1: weaponName = "RAPID FIRE!"; break;
            case 2: weaponName = "SPREAD SHOT!"; break;
            case 3: weaponName = "LASER!"; break;
        }
        if (!weaponName.isEmpty()) {
            g2d.setColor(Color.cyan);
            g2d.drawString("Weapon: " + weaponName, 10, 480);
        }
    }

    private void drawMenu(Graphics2D g2d) {
        g2d.setColor(new Color(10, 10, 30, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.cyan);
        g2d.setFont(new Font("Verdana", Font.BOLD, 50));
        drawCenteredString(g2d, "SPACE SHOOTER", getWidth(), 80);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Verdana", Font.PLAIN, 20));
        drawCenteredString(g2d, "Select Difficulty and Press SPACE", getWidth(), 150);

        String[] difficulties = {"EASY (1)", "NORMAL (2)", "HARD (3)"};
        for (int i = 0; i < 3; i++) {
            if (selectedDifficulty == i + 1) {
                g2d.setColor(Color.yellow);
                g2d.setFont(new Font("Verdana", Font.BOLD, 24));
            } else {
                g2d.setColor(Color.white);
                g2d.setFont(new Font("Verdana", Font.PLAIN, 24));
            }
            drawCenteredString(g2d, difficulties[i], getWidth(), 250 + i * 50);
        }

        g2d.setColor(Color.yellow);
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        drawCenteredString(g2d, "High Score: " + highScore, getWidth(), 420);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(255, 50, 50, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.yellow);
        g2d.setFont(new Font("Verdana", Font.BOLD, 60));
        drawCenteredString(g2d, "GAME OVER", getWidth(), 100);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Verdana", Font.BOLD, 30));
        drawCenteredString(g2d, "Score: " + score, getWidth(), 180);
        drawCenteredString(g2d, "Level: " + level, getWidth(), 220);

        if (score > highScore) {
            g2d.setColor(Color.yellow);
            drawCenteredString(g2d, "NEW HIGH SCORE!", getWidth(), 260);
        } else {
            g2d.setColor(Color.cyan);
            drawCenteredString(g2d, "High Score: " + highScore, getWidth(), 260);
        }

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Verdana", Font.PLAIN, 20));
        drawCenteredString(g2d, "Press ENTER to Return to Menu", getWidth(), 350);
    }

    private void drawPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.cyan);
        g2d.setFont(new Font("Verdana", Font.BOLD, 40));
        drawCenteredString(g2d, "PAUSED", getWidth(), 150);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Verdana", Font.PLAIN, 20));
        drawCenteredString(g2d, "Press P to Resume", getWidth(), 250);
    }

    private void drawCenteredString(Graphics2D g, String text, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState != GameState.PLAYING) return;

        // Move spaceship
        if (leftPressed && spaceshipX > 0) spaceshipX -= 7;
        if (rightPressed && spaceshipX < getWidth() - 50) spaceshipX += 7;

        // Update shield and invincibility
        if (shieldTimer > 0) shieldTimer--;
        if (invincibleTimer > 0) invincibleTimer--;

        // Move and update bullets
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet b = bulletIter.next();
            b.y -= b.speed;
            if (b.y < -20 || b.y > getHeight() + 20) {
                bulletIter.remove();
            }
        }

        // Move and update enemy bullets
        Iterator<Bullet> enemyBulletIter = enemyBullets.iterator();
        while (enemyBulletIter.hasNext()) {
            Bullet b = enemyBulletIter.next();
            b.y -= b.speed; // b.speed is negative for enemy bullets
            
            Rectangle ebRect = new Rectangle(b.x, b.y, 8, 8);
            Rectangle playerRect = new Rectangle(spaceshipX, spaceshipY, 50, 40);
            
            if (ebRect.intersects(playerRect)) {
                if (invincibleTimer == 0) {
                    if (shieldTimer > 0) {
                        shieldTimer = 0;
                    } else {
                        health -= 10;
                        if (health <= 0) handlePlayerDeath();
                    }
                    screenShake = 5;
                    createExplosion(b.x, b.y);
                    enemyBulletIter.remove();
                }
            } else if (b.y > getHeight() + 20) {
                enemyBulletIter.remove();
            }
        }

        // Move enemies and handle spawning
        Iterator<Enemy> enemyIter = enemies.iterator();
        int enemiesAlive = 0;
        while (enemyIter.hasNext()) {
            Enemy en = enemyIter.next();
            en.y += en.speed;
            
            if (en.type == EnemyType.BOSS) {
                float healthPercent = (float)en.health / en.maxHealth;

                if (healthPercent > 0.7) {
                    // Phase 1: Sine movement + Single Shot
                    en.x += (int)(3 * Math.sin(System.currentTimeMillis() / 500.0));
                    
                    if (rand.nextInt(100) < 3) {
                       Bullet eb = new Bullet(en.x + 20, en.y + 30, 0);
                       eb.speed = -4 - (level / 2);
                       enemyBullets.add(eb);
                    }
                } else if (healthPercent > 0.4) {
                    // Phase 2: Faster movement + Spread Shot
                    en.x += (int)(6 * Math.sin(System.currentTimeMillis() / 300.0));
                    
                    if (rand.nextInt(100) < 4) {
                       Bullet eb1 = new Bullet(en.x + 20, en.y + 30, 0);
                       Bullet eb2 = new Bullet(en.x + 20, en.y + 30, -3); // Left diagonal
                       Bullet eb3 = new Bullet(en.x + 20, en.y + 30, 3);  // Right diagonal
                       
                       eb1.speed = -5 - (level / 2);
                       eb2.speed = -5 - (level / 2);
                       eb3.speed = -5 - (level / 2);
                       
                       enemyBullets.add(eb1);
                       enemyBullets.add(eb2);
                       enemyBullets.add(eb3);
                    }
                } else {
                    // Phase 3: Center hover + Spiral Bullet Hell
                    int targetX = getWidth() / 2 - 30;
                    if (en.x < targetX) en.x += 2;
                    if (en.x > targetX) en.x -= 2;

                    en.angle += 0.2; // Rotate firing angle
                    if (rand.nextInt(100) < 15) { // Rapid fire
                        int bulletSpeed = 4;
                        // Calculate velocity based on angle
                        int vx = (int)(bulletSpeed * Math.cos(en.angle));
                        int vy = (int)(bulletSpeed * Math.sin(en.angle));
                        
                        // We need to modify Bullet to handle arbitrary velocity vectors better, 
                        // but for now we can hack it or update Bullet class.
                        // The current Bullet class only supports velocityX and fixed vertical speed.
                        // Let's modify Bullet class to support vx and vy fully or just use what we have.
                        
                        // Wait, Bullet only has velocityX and 'speed' which is subtracted from y.
                        // For spiral, we need X and Y velocity control.
                        // The current update() is: x += velocityX. 
                        // The main loop does: b.y -= b.speed.
                        
                        // So for a bullet traveling DOWN (enemy bullet), we set speed negative.
                        // To travel at an angle, we need to set independent X and Y velocities.
                        // I will update the Bullet class to have vx and vy and use those.
                        
                        Bullet eb = new Bullet(en.x + 20, en.y + 30, vx);
                        eb.speed = -vy; // b.y -= b.speed => b.y -= (-vy) => b.y += vy
                        enemyBullets.add(eb);
                        
                        // Add a second stream for double spiral
                        vx = (int)(bulletSpeed * Math.cos(en.angle + Math.PI));
                        vy = (int)(bulletSpeed * Math.sin(en.angle + Math.PI));
                        Bullet eb2 = new Bullet(en.x + 20, en.y + 30, vx);
                        eb2.speed = -vy;
                        enemyBullets.add(eb2);
                    }
                }
                
                en.x = Math.max(0, Math.min(en.x, getWidth() - 60));
            } else {
                // Non-Boss movement
                // Existing logic? No, wait. 
                // All enemies move vertically by en.speed in line 490: en.y += en.speed;
                // Boss horizontal movement is handled above.
            }

            // Standard shooting for non-boss enemies
            if (en.type != EnemyType.BOSS && rand.nextInt(100) < 2 + (level / 2)) {
                 Bullet eb = new Bullet(en.x + 20, en.y + 30, 0);
                 eb.speed = -4 - (level / 2); // Traveling down
                 enemyBullets.add(eb);
            }
            
            // Check if enemy hit player
            Rectangle enemyRect = new Rectangle(en.x, en.y, 
                en.type == EnemyType.BOSS ? 60 : (en.type == EnemyType.HEAVY ? 50 : 40), 
                en.type == EnemyType.BOSS ? 50 : (en.type == EnemyType.HEAVY ? 40 : 30));
            Rectangle playerRect = new Rectangle(spaceshipX, spaceshipY, 50, 40);
            
            if (enemyRect.intersects(playerRect)) {
                if (shieldTimer > 0) {
                    enemyIter.remove();
                    shieldTimer = 0;
                    screenShake = 5;
                    createExplosion(en.x + 20, en.y + 15);
                } else if (invincibleTimer == 0) {
                    int damage = 20;
                    switch (en.type) {
                        case FAST: damage = 15; break;
                        case HEAVY: damage = 40; break;
                        case BOSS: damage = 60; break;
                    }
                    health -= damage;
                    screenShake = 8;
                    createExplosion(spaceshipX + 25, spaceshipY + 20);
                    
                    if (health <= 0) {
                        handlePlayerDeath();
                        if (gameState == GameState.GAME_OVER) return;
                    }
                    enemyIter.remove();
                }
            } else if (en.y > getHeight()) {
                enemyIter.remove();
            } else {
                enemiesAlive++;
            }
        }

        // Check for level up
        if (enemiesAlive == 0 && enemies.size() == 0) {
            level++;
            spawnEnemyWave();
        }

        // Move and update power-ups
        Iterator<PowerUp> puIter = powerUps.iterator();
        while (puIter.hasNext()) {
            PowerUp pu = puIter.next();
            pu.y += 2;
            
            Rectangle puRect = new Rectangle(pu.x, pu.y, 20, 20);
            if (puRect.intersects(new Rectangle(spaceshipX, spaceshipY, 50, 40))) {
                applyPowerUp(pu);
                puIter.remove();
            } else if (pu.y > getHeight()) {
                puIter.remove();
            }
        }

        // Update particles
        Iterator<Particle> partIter = particles.iterator();
        while (partIter.hasNext()) {
            Particle p = partIter.next();
            p.update();
            if (p.life <= 0) {
                partIter.remove();
            }
        }

        // Update floating texts
        Iterator<FloatingText> ftIter = floatingTexts.iterator();
        while (ftIter.hasNext()) {
            FloatingText ft = ftIter.next();
            ft.y -= 1;
            ft.life--;
            if (ft.life <= 0) {
                ftIter.remove();
            }
        }

        // Handle rapid fire
        if (weaponType == 1) {
            rapidFireCounter++;
            if (rapidFireCounter % 5 == 0) {
                bullets.add(new Bullet(spaceshipX + 22, spaceshipY, 0));
            }
        }

        // Check bullet-enemy collisions
        bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet b = bulletIter.next();
            enemyIter = enemies.iterator();
            boolean bulletHit = false;
            
            while (enemyIter.hasNext()) {
                Enemy en = enemyIter.next();
                Rectangle bulletRect = new Rectangle(b.x, b.y, 6, 12);
                Rectangle enemyRect = new Rectangle(en.x, en.y, 
                    en.type == EnemyType.BOSS ? 60 : (en.type == EnemyType.HEAVY ? 50 : 40),
                    en.type == EnemyType.BOSS ? 50 : (en.type == EnemyType.HEAVY ? 40 : 30));
                
                if (bulletRect.intersects(enemyRect)) {
                    en.health -= b.damage;
                    createExplosion(en.x + 20, en.y + 15);
                    
                    if (en.health <= 0) {
                        int points = 15;
                        switch (en.type) {
                            case FAST: points = 25; break;
                            case HEAVY: points = 50; break;
                            case BOSS: points = 200; break;
                        }
                        score += points * combo;
                        floatingTexts.add(new FloatingText("+" + (points * combo), en.x, en.y, 50, Color.yellow));
                        combo++;
                        lastKillTime = System.currentTimeMillis();
                        
                        // 30% chance to drop power-up
                        if (rand.nextInt(100) < 30) {
                            powerUps.add(new PowerUp(en.x + 15, en.y + 10));
                        }
                        
                        enemyIter.remove();
                    }
                    bulletHit = true;
                    break;
                }
            }
            
            if (bulletHit || b.y < -20) {
                bulletIter.remove();
            }
        }

        // Reset combo if time has passed
        if (System.currentTimeMillis() - lastKillTime > 3000) {
            combo = 1;
        }

        repaint();
    }

    private void handlePlayerDeath() {
        lives--;
        if (lives <= 0) {
            health = 0;
            gameState = GameState.GAME_OVER;
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        } else {
            health = MAX_HEALTH;
            invincibleTimer = 100;
            screenShake = 20;
            enemyBullets.clear(); // Clear bullets to give player a chance
        }
    }

    private void useBomb() {
        if (bombs > 0 && gameState == GameState.PLAYING) {
            bombs--;
            screenShake = 30;
            Iterator<Enemy> it = enemies.iterator();
            while (it.hasNext()) {
                Enemy en = it.next();
                if (en.type != EnemyType.BOSS) {
                    createExplosion(en.x + 20, en.y + 15);
                    score += 10;
                    it.remove();
                } else {
                    en.health -= 5;
                }
            }
            enemyBullets.clear();
        }
    }

    private void createExplosion(int x, int y) {
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2;
            int vx = (int)(5 * Math.cos(angle));
            int vy = (int)(5 * Math.sin(angle));
            particles.add(new Particle(x, y, vx, vy, new Color(255, 150, 0)));
        }
    }

    private void applyPowerUp(PowerUp pu) {
        switch (pu.type) {
            case SHIELD:
                shieldTimer = 200;
                break;
            case RAPID_FIRE:
                weaponType = 1;
                new Timer(5000, e -> {
                    if (weaponType == 1) weaponType = 0;
                }).start();
                break;
            case SPREAD_SHOT:
                weaponType = 2;
                new Timer(5000, e -> {
                    if (weaponType == 2) weaponType = 0;
                }).start();
                break;
            case LASER:
                weaponType = 3;
                new Timer(5000, e -> {
                    if (weaponType == 3) weaponType = 0;
                }).start();
                break;
            case HEALTH:
                health = Math.min(MAX_HEALTH, health + 30);
                break;
            case LIFE:
                lives++;
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameState == GameState.MENU) {
            if (key == KeyEvent.VK_1) {
                selectedDifficulty = 1;
            } else if (key == KeyEvent.VK_2) {
                selectedDifficulty = 2;
            } else if (key == KeyEvent.VK_3) {
                selectedDifficulty = 3;
            } else if (key == KeyEvent.VK_SPACE) {
                startGame();
            }
            return;
        }

        if (gameState == GameState.GAME_OVER) {
            if (key == KeyEvent.VK_ENTER) {
                gameState = GameState.MENU;
            }
            return;
        }

        if (key == KeyEvent.VK_P) {
            gameState = (gameState == GameState.PLAYING) ? GameState.PAUSED : GameState.PLAYING;
            return;
        }

        if (gameState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } else if (key == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } else if (key == KeyEvent.VK_SPACE) {
                fireWeapon();
            } else if (key == KeyEvent.VK_B) {
                useBomb();
            }
        }
    }

    private void fireWeapon() {
        switch (weaponType) {
            case 0: // Normal
                bullets.add(new Bullet(spaceshipX + 22, spaceshipY, 0));
                break;
            case 1: // Rapid fire (handled in actionPerformed)
                break;
            case 2: // Spread shot
                bullets.add(new Bullet(spaceshipX + 22, spaceshipY, 0));
                bullets.add(new Bullet(spaceshipX + 10, spaceshipY, -2));
                bullets.add(new Bullet(spaceshipX + 34, spaceshipY, 2));
                break;
            case 3: // Laser
                // Laser pierces all enemies
                Bullet laser = new Bullet(spaceshipX + 22, spaceshipY, 0);
                laser.damage = 100;
                laser.speed = 12;
                bullets.add(laser);
                break;
        }
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

    enum EnemyType { BASIC, FAST, HEAVY, BOSS }

    private static class Bullet {
        int x, y;
        int damage = 1;
        float speed = 10;
        float velocityX = 0;
        // Using float for smoother angles if needed, but int is in class. Let's keep int but allow setting vy via speed.

        Bullet(int x, int y, float velocityX) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
        }
        
        void update() {
            this.x += velocityX;
        }
    }

    enum PowerUpType { SHIELD, RAPID_FIRE, SPREAD_SHOT, LASER, HEALTH, LIFE }

    private static class PowerUp {
        int x, y;
        PowerUpType type;
        Color color;
        String icon;

        PowerUp(int x, int y) {
            this.x = x;
            this.y = y;
            int rand = new Random().nextInt(100);
            if (rand < 20) {
                this.type = PowerUpType.SHIELD;
                this.color = Color.blue;
                this.icon = "S";
            } else if (rand < 40) {
                this.type = PowerUpType.RAPID_FIRE;
                this.color = Color.red;
                this.icon = "R";
            } else if (rand < 60) {
                this.type = PowerUpType.SPREAD_SHOT;
                this.color = Color.magenta;
                this.icon = "W";
            } else if (rand < 75) {
                this.type = PowerUpType.LASER;
                this.color = Color.yellow;
                this.icon = "L";
            } else if (rand < 90) {
                this.type = PowerUpType.HEALTH;
                this.color = Color.green;
                this.icon = "H";
            } else {
                this.type = PowerUpType.LIFE;
                this.color = new Color(255, 100, 100);
                this.icon = "♥";
            }
        }
    }

    private static class Particle {
        int x, y, vx, vy;
        int life = 200;
        int size = 4;
        Color color;

        Particle(int x, int y, int vx, int vy, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
        }

        void update() {
            x += vx;
            y += vy;
            life -= 10;
            vy += 1; // gravity
        }
    }

    private static class Enemy {
        int x, y;
        EnemyType type;
        int health;
        int maxHealth;
        int speed;
        float angle = 0; // For boss patterns

        Enemy(int x, int y, EnemyType type) {
            this.x = x;
            this.y = y;
            this.type = type;
            
            switch (type) {
                case BASIC:
                    this.health = 1;
                    this.speed = 2;
                    break;
                case FAST:
                    this.health = 1;
                    this.speed = 4;
                    break;
                case HEAVY:
                    this.health = 3;
                    this.speed = 1;
                    break;
                case BOSS:
                    this.health = 50;
                    this.speed = 1;
                    break;
            }
            this.maxHealth = this.health;
        }
    }

    private static class FloatingText {
        String text;
        int x, y, life;
        Color color;

        FloatingText(String text, int x, int y, int life, Color color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.life = life;
            this.color = color;
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
