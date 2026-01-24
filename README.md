# Space Shooter Deluxe - ULTIMATE EDITION 🚀

A fully-featured 2D arcade spaceship shooter game built with **Java Swing and AWT**. Now 10x better with progressive levels, power-ups, multiple enemy types, particle effects, and more!

---

## ✨ NEW FEATURES (10x Upgrade!)

### 🎮 Game Progression
- **Level System**: Progressive difficulty with increasing enemy spawn rates
- **Difficulty Selection**: Choose Easy, Normal, or Hard before each game
- **High Score Persistence**: Your best score is automatically saved and loaded
- **Combo System**: Build multipliers by continuously defeating enemies for bigger scores

### 💥 Advanced Combat
- **4 Weapon Types**:
  - 🟡 **Normal**: Standard single shots
  - 🔴 **Rapid Fire**: Quick shots for 5 seconds
  - 🟣 **Spread Shot**: 3-way bullet spread
  - 🟨 **Laser**: Piercing laser beam (100 damage)

### 🛡️ Enemy Variety
- **4 Enemy Types** with unique behaviors:
  - 🔴 **Basic**: Standard enemies
  - 🟣 **Fast**: Quick enemies for 25 points
  - ⬛ **Heavy**: Armored enemies with 3 HP, worth 50 points
  - 🌟 **Boss**: Large boss fights with 10 HP, worth 200 points!

### 🎁 Power-Up System
- **Shield** (Blue S): Absorb one hit
- **Rapid Fire** (Red R): 5 seconds of quick shots
- **Spread Shot** (Purple W): 3-directional fire
- **Laser** (Yellow L): Piercing high-damage weapon
- 30% chance to drop after each kill

### 🎨 Visual Enhancements
- **Particle Effects**: Explosion effects when enemies are destroyed
- **Screen Shake**: Impact feedback on collisions
- **Shield Visualization**: See your active shield around the spaceship
- **Health Bars**: Heavy enemies and bosses show remaining health
- **Anti-aliasing**: Smooth graphics rendering
- **Dynamic UI**: Weapon indicator and combo display

### 🎯 Gameplay Mechanics
- **3 Lives System**: Take 3 hits before game over (shields extend your life)
- **Twinkling Starfield**: Atmospheric background with twinkling effects
- **Boss Encounters**: Face challenging boss enemies at higher levels
- **Smooth Controls**: Responsive keyboard input

### 🎮 Game States
- **Menu Screen**: Difficulty selection before each game
- **Pause System**: Press P to pause/resume
- **Game Over Screen**: Shows final score, level, and high score comparison
- **Score Tracking**: Real-time display of score, health, level, and weapon type

---

## Controls

| Key          | Action                      |
| ------------ | --------------------------- |
| 1, 2, 3      | Select difficulty (Menu)    |
| Spacebar     | Start game (Menu) / Shoot   |
| Left Arrow   | Move spaceship left         |
| Right Arrow  | Move spaceship right        |
| P            | Pause / Resume              |
| Enter        | Return to menu (Game Over)  |

---

## How to Run

1. Ensure you have **Java 8 or higher** installed
2. Navigate to the project directory
3. Compile:
   ```bash
   javac SpaceShooter.java
   ```
4. Run:
   ```bash
   java SpaceShooter
   ```

---

## Scoring System

- **Basic Enemy**: 15 points × combo multiplier
- **Fast Enemy**: 25 points × combo multiplier  
- **Heavy Enemy**: 50 points × combo multiplier
- **Boss Enemy**: 200 points × combo multiplier
- **Combo Multiplier**: Resets after 3 seconds without a kill

---

## Tips & Tricks

🎯 **Master Combo Chains**: Continuously kill enemies within 3 seconds to build your combo multiplier
🛡️ **Use Shields Wisely**: Shields protect you from one hit and give you breathing room
🔴 **Watch for Bosses**: Bosses are large targets, destroy them for massive points!
🟣 **Avoid Heavy Enemies**: Heavy enemies are slow but durable, use spread shot or laser
⚡ **Laser Pierces**: The laser weapon passes through multiple enemies in a line

---

## File Structure

- **SpaceShooter.java** - Main game code with all classes:
  - Game loop and rendering logic
  - Spaceship, Bullet, Enemy (with types), PowerUp, and Particle classes
  - Complete game state management (Menu, Playing, Paused, GameOver)
  - High score persistence system
- Keyboard input handling
- Game state management

---
## Future Improvements

- Add sound effects and background music
- Introduce power-ups and multiple enemy types
- Implement player lives and health system
- Add explosion animations and particle effects
- Create multiple levels with increasing difficulty

---

## License
This project is open source and free to use.
---
## Author
Created by [GhanshyamJha05](https://github.com/GhanshyamJha05)
