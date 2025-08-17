# Rubik's Cube Simulator

A Java-based Rubik's Cube simulator and solver with a graphical user interface. Uses the [Kociemba Two-Phase Algorithm](https://github.com/hkociemba/RubiksCube-TwophaseSolver) for solving the cube.

## Features

- Interactive GUI to manipulate and visualize the cube
- Apply moves manually or using buttons
- Scramble, reset, and solve the cube
- Animated move sequences
- Uses [`org.kociemba.twophase.Search`](src/org/kociemba/twophase/Search.java) from [lib/twophase.jar](lib/twophase.jar)

## Project Structure

```
rubik-cube-solver/
├── src/com/ramteja/rubik/
│   ├── RubikCube.java          # Core cube logic and operations
│   └── RubikCubeGui.java       # Graphical user interface
├── lib/
│   └── twophase.jar            # Two-phase algorithm library
├── compile.bat/.sh             # Build scripts
├── run.bat/.sh                 # Run scripts
├── .gitignore
└── README.md
```

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/[your-username]/rubik-cube-solver.git
   cd rubik-cube-solver
   ```

2. **Compile the project:**
   
   **Windows:**
   ```bash
   compile.bat
   ```
   
   **Mac/Linux:**
   ```bash
   chmod +x compile.sh
   ./compile.sh
   ```
   
   **Manual compilation:**
   ```bash
   mkdir -p bin
   javac -cp "lib/*" -d bin src/main/java/com/[username]/rubikcube/*.java
   ```

## Usage

### Running the Application

**Windows:**
```bash
run.bat
```

**Mac/Linux:**
```bash
chmod +x run.sh
./run.sh
```

**Manual execution:**
```bash
# Windows
java -cp "bin;lib/*" com.[username].rubikcube.RubikCubeGui

# Mac/Linux  
java -cp "bin:lib/*" com.[username].rubikcube.RubikCubeGui
```

### How to Use

1. **Launch the application** using one of the methods above
2. **Input cube state** or use the interface to set up your cube configuration
3. **Click solve** to find the solution sequence
4. **Follow the moves** displayed to solve your physical cube

## Dependencies

- **twophase.jar**: Implements the two-phase algorithm for optimal cube solving by Kociemba
  - This library provides efficient solving capabilities
  - Included in the `lib/` directory

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

 - **Herbert Kociemba** for creating the revolutionary [Two-Phase Algorithm](https://github.com/hkociemba/RubiksCube-TwophaseSolver) that makes efficient cube solving possible
 - Kociemba's twophase library for providing the robust implementation used in this project
 - The original research paper: Kociemba, H. (1992). "Close to optimal solutions for the Rubik's cube"
 - Inspired by the mathematical beauty and complexity of the Rubik's Cube
 - Thanks to the Java community for excellent documentation and resources

## Author

Created by Ram Teja - feel free to contact me with questions or suggestions!

---

⭐ If you found this project helpful, please give it a star on GitHub!