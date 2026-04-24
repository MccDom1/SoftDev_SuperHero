# 🏚️ Escape the Mansion

### Team SuperHero Squad – Client Deliverable for Team Justice League

---

## 📌 Project Overview

**Escape the Mansion** is a data-driven, text-based adventure game where players explore a mysterious mansion, solve puzzles, interact with items, and survive encounters with hostile entities.

This system was designed and developed by **Team SuperHero Squad** as part of a software engineering deliverable for **Team Justice League**, following object-oriented design principles and real-world development practices.

The game emphasizes:

* Logical exploration
* Player-driven decision making
* Modular and scalable system architecture
* Data-driven world design (no hard-coded environments)

---

## 🎯 Objectives

The primary goal of this system is to deliver a **fully interactive adventure experience** that demonstrates:

* Object-Oriented Programming (OOP)
* Separation of concerns (MVC-inspired structure)
* Data-driven design using external files
* Scalable and maintainable code architecture

---

## 🧠 System Design

The application follows a structured design approach:

### 🔹 Core Components

* **Player** – Manages player state (health, inventory, position)
* **Room** – Represents game locations and connections
* **Item** – Defines usable, equippable, and collectible objects
* **Puzzle** – Handles logic-based challenges and progression
* **Monster** – Represents enemies and combat interactions
* **GameWorld** – Loads and constructs the game from external data
* **GameModel** – Core logic and rule processing
* **GameController** – Handles user input and command routing
* **GameView** – Displays output to the user

---

## 🗺️ Game World (Data-Driven)

The game world is **not hard-coded**. All content is loaded from external files:

* `Rooms.txt`
* `Items.txt`
* `PuzzleTextFile.txt`
* `Monsters.txt`

This allows:

* Easy updates to the game world
* No code changes required for content modifications
* Improved scalability and reuse

---

## 🎮 Gameplay Features

### 🧭 Navigation

* Move between rooms using directional commands
* View available exits and routes
* Explore environments dynamically

### 🧩 Puzzles

* Solve logic-based challenges
* Limited attempts system
* Rewards and consequences

### 🎒 Inventory System

* Pick up, drop, and manage items
* Equip weapons and armor
* Use and consume items with effects

### ⚔️ Combat System

* Attack or flee from monsters
* Damage calculation based on equipment
* Enemy counterattacks and defeat states

### 🧠 Player State Management

* Health tracking
* Equipped gear
* Progression and interaction tracking

---

## 🧾 Supported Commands

| Command            | Description                |
| ------------------ | -------------------------- |
| `Start`            | Begin the game             |
| `Explore`          | View room details          |
| `Exit [direction]` | Move to another room       |
| `Route`            | View available connections |
| `Inventory`        | View items                 |
| `Pick up <item>`   | Collect item               |
| `Drop <item>`      | Remove item                |
| `Equip <item>`     | Equip gear                 |
| `Use <item>`       | Apply item effect          |
| `Consume <item>`   | Use and remove item        |
| `Solve <answer>`   | Attempt puzzle             |
| `Attack`           | Engage monster             |
| `Flee`             | Attempt escape             |
| `Map`              | View discovered areas      |
| `Help`             | Show commands              |
| `Save Game`        | Save progress              |
| `Quit`             | Exit game                  |

---

## 🛠️ Technologies Used

* Java (Core Language)
* Object-Oriented Programming (OOP)
* File I/O (Data-driven design)
* Git & GitHub (Version control)
* Eclipse / VS Code (Development environments)

---

## ▶️ How to Run

### 🔹 Compile

```bash
javac src/*.java
```

### 🔹 Run

```bash
java -cp src Main
```

> Ensure all `.txt` data files are located in the project root directory.

---

## 📁 Project Structure

```
Escape_Mansion/
├── src/
│   ├── Main.java
│   ├── GameController.java
│   ├── GameModel.java
│   ├── GameView.java
│   ├── GameResult.java
│   ├── GameWorld.java
│   ├── Player.java
│   ├── Room.java
│   ├── Item.java
│   ├── Puzzle.java
│   └── Monster.java
│
├── Rooms.txt
├── Items.txt
├── PuzzleTextFile.txt
├── Monsters.txt
├── README.md
└── .gitignore
```

---

## 👥 Team Contributions

### 🟦 Dominique McClaney

* Player system design and state management
* Game flow logic integration
* System coordination and architecture alignment

### 🟩 Marquella

* Room structure and environment modeling
* Spatial design and navigation mapping

### 🟨 Jake

* Item system and inventory mechanics
* Equipment and consumable logic

### 🟥 Macaiyla

* Puzzle system design and interaction logic
* Reward and progression mechanics

---

## 🔍 Key Design Decisions

* No hard-coded game content
* Separation of logic, data, and presentation
* Modular class design for scalability
* Externalized game configuration for flexibility

---

## 🚀 Future Enhancements

* Visual map rendering (ASCII or GUI)
* Enhanced combat mechanics
* Save/load persistence system
* GUI-based interface
* Expanded storyline and branching paths

---

## 📌 Final Notes

This project demonstrates a complete, modular, and scalable approach to building a text-based game system using professional software development practices.

The architecture allows future teams or developers to extend the game without modifying core logic, ensuring long-term maintainability.

---

**Submitted by Team SuperHero Squad**
For **Team Justice League**
