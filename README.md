# Space Shooter Deluxe - ULTIMATE EDITION 🚀

A fully-featured 2D arcade spaceship shooter game built with **Java Swing and AWT**. Now 10x better with progressive levels, power-ups, multiple enemy types, particle effects, and more!

---

## ✨ NEW FEATURES (Ultimate Upgrade!)

### 🎮 Game Progression
- **Level System**: Progressive difficulty with increasing enemy spawn rates
- **Difficulty Selection**: Choose Easy, Normal, or Hard before each game
- **High Score Persistence**: Your best score is automatically saved and loaded
- **Combo System**: Build multipliers by continuously defeating enemies for bigger scores
- **Floating Scores**: Visual feedback for points earned during combat

### 💥 Advanced Combat
- **4 Weapon Types**:
  - 🟡 **Normal**: Standard single shots
  - 🔴 **Rapid Fire**: Quick shots for 5 seconds
  - 🟣 **Spread Shot**: 3-way bullet spread
  - 🟨 **Laser**: Piercing laser beam (100 damage)
- **Bomb System**: Press [B] to clear the screen of standard enemies (Limited supply!)

### 🛡️ Enemy Intelligence
- **Armed Enemies**: Enemies now fight back! Watch out for incoming fire.
- **4 Enemy Types** with unique behaviors:
  - 🔴 **Basic**: Standard enemies that occasionally shoot
  - 🟣 **Fast**: Quick, elusive enemies
  - ⬛ **Heavy**: Armored ships with 3 HP
  - 🌟 **Boss**: Massive warships with 10 HP and high fire rate!

### 🎁 Power-Up System
- **Shield** (Blue S): Absorb one hit
- **Rapid Fire** (Red R): 5 seconds of quick shots
- **Spread Shot** (Purple W): 3-directional fire
- **Laser** (Yellow L): Piercing high-damage weapon
- **Health/Lives**: Recovery items to keep you in the fight

### 🎨 Visual & Feedback
- **Particle Effects**: Dynamic explosions for every destruction
- **Screen Shake**: Intense feedback on impacts and bomb usage
- **Floating Text**: Damage and score indicators pop up in real-time
- **Smooth Rendering**: Anti-aliased graphics and twinkling starfield

---

## Controls

| Key          | Action                      |
| ------------ | --------------------------- |
| 1, 2, 3      | Select difficulty (Menu)    |
| Spacebar     | Start game (Menu) / Shoot   |
| **B**        | **Use Mega Bomb**           |
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
- **Mega Bomb Kill**: 10 points (Combo does not apply to bombs)

---

## Future Improvements

- Add sound effects and ambient background music
- Implement a weapon upgrade/shop system
- Add more diverse boss patterns and phases
- Support for game controllers

---

## License
This project is open source and free to use.

---
## Author
Created by [GhanshyamJha05](https://github.com/GhanshyamJha05)
