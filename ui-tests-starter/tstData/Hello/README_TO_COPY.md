# Java Minesweeper Game: Classic Mine-Finding Strategy Game Implementation

This project implements the classic Minesweeper game in Java using Swing for the graphical user interface. The game challenges players to clear a board containing hidden mines without detonating any of them, using number clues to guide their progress.

The implementation features a fully functional Minesweeper game with a 16x16 grid containing 40 mines. Players can left-click to reveal cells and right-click to mark suspected mine locations. The game includes all classic Minesweeper mechanics including recursive revealing of empty cells, mine counting, and win/lose condition detection. The status bar displays the number of remaining mines and game state messages.

## Repository Structure
```
ui-tests-starter/tstData/Hello/
├── catalog-info.yaml          # Backstage component definition file
└── src/com/tancode/          # Main source code directory
    ├── Board.java            # Core game logic and board rendering
    └── Minesweeper.java      # Main application entry point and UI setup
```

## Usage Instructions
### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Java Runtime Environment (JRE)
- Java Swing library (included in JDK)

### Installation
1. Clone the repository:
```bash
git clone <repository-url>
cd ui-tests-starter/tstData/Hello
```

2. Compile the Java files:
```bash
javac src/com/tancode/*.java
```

### Quick Start
1. Run the compiled game:
```bash
java -cp src com.tancode.Minesweeper
```

2. Play the game:
- Left-click to reveal a cell
- Right-click to mark/unmark a suspected mine
- The status bar shows remaining mines or game status

### More Detailed Examples
#### Game Controls
```java
// Left-click to reveal a cell
mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
        // Reveals the cell
    }
}

// Right-click to mark a mine
mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3) {
        // Marks/unmarks the cell as a mine
    }
}
```

### Troubleshooting
#### Common Issues
1. Images Not Loading
- Problem: Game board shows blank cells
- Solution: Ensure the resource files (*.png) are present in the `src/resources/` directory
- Debug: Check file permissions and path structure

2. Game Window Not Appearing
- Problem: No visible window after launching
- Solution: Verify Swing EDT initialization
- Debug: Add logging to `EventQueue.invokeLater()` callback

## Data Flow
The game operates on a cell-based grid system where each cell maintains its state and interacts with neighboring cells.

```ascii
[User Input] -> [Mouse Handler] -> [Board State Update] -> [UI Refresh]
     ^                                     |                    |
     |                                     v                    v
     +--------------------[Game State Check]<-[Cell Revelation]
```

Component Interactions:
1. Mouse events trigger cell interactions through the MinesAdapter class
2. Board class manages the game state and cell values
3. Cell revelation propagates through recursive empty cell checking
4. UI updates reflect game state changes through paintComponent
5. Status bar provides real-time feedback on game progress
6. Game state (won/lost) is checked after each move
7. Mine counting and marking system tracks remaining mines