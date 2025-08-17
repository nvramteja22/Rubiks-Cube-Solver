package com.ramteja.rubik;
import org.kociemba.twophase.Search;
import java.util.Random;

/**
 * Represents a Rubik's Cube with methods to manipulate and display its state.
 */
public class RubikCube {
    // The ANSI color codes for the cube faces 
    // U: White, D: Yellow, L: Orange, R: Red, F: Green, B: Blue
    private static final String[] ANSI_COLOR = {
        "\u001b[0;37m", // White
        "\u001b[0;33m", // Yellow
        "\u001b[38;5;208m", // Orange
        "\u001b[0;31m", // Red
        "\u001b[0;32m", // Green
        "\u001b[0;34m"  // Blue
    };
    // ANSI reset code to revert to default terminal color
    private static final String ANSI_RESET = "\u001b[0m";

    private static final boolean DEBUG = false; // Debug mode flag

    // The cube faces represented as 2D arrays
    // U: Up, D: Down, L: Left, R: Right, F: Front, B: Back
    char[][] U = new char[3][3];
    char[][] D = new char[3][3];
    char[][] L = new char[3][3];
    char[][] R = new char[3][3];
    char[][] F = new char[3][3];
    char[][] B = new char[3][3];

    /**
     * Constructor to initialize the cube with default colors.
     * Each face is filled with its respective color.
     */
    public RubikCube() {
        resetCube();
    }

    /**
     * Applies a sequence of rotations to the cube.
     * Each character in the string represents a face to rotate.
     * Uppercase letters perform clockwise rotations, lowercase letters perform counter-clockwise rotations.
     * Spaces are ignored.
     * @param rotation A string representing the sequence of rotations to apply.
     */
    public void applyRotation(String rotation) {
        if(DEBUG) {
            System.out.println("Applying the rotation: " + rotation);
        }
        String[] moves = rotation.trim().split("\\s+");
        for (String move : moves) {
            if (!move.isEmpty())
                rotateFace(move);
        }
    }
    
    /**
     * Rotates the specified face of the cube.
     * Suports standard notations: U, U', U2, etc.
     * @param face A character representing the face to rotate ('U', "U'", "U2", etc.)
     */
    public void rotateFace(String move) {
        char face = move.charAt(0);
        int times = 1; // Default to 1 for clockwise rotation
        if (move.length() > 1 && move.charAt(1) == '\'') {
            times = 3;
        } else if (move.length() > 1 && move.charAt(1) == '2') {
            times = 2;
        }
        for(int i = 0; i < times; i++) {
            switch (face) {
                case 'U': rotateUpper(); break;
                case 'F': rotateFront(); break;
                case 'R': rotateRight(); break;
                case 'L': rotateLeft(); break;
                case 'B': rotateBack(); break;
                case 'D': rotateDown(); break;
                default: 
                    System.out.println("Invalid rotation: " + face);
            }
        }
    }

    public void resetCube() {
        fillFace(U, 'W');
        fillFace(D, 'Y');
        fillFace(L, 'O');
        fillFace(R, 'R');
        fillFace(F, 'G');
        fillFace(B, 'B');
    }

    /**
     * Displays the current state of the Rubik's Cube in the console.
     * Each cell is printed with its respective ANSI color code.
     * The cube is displayed in a 2D format:
     * - Up face at the top
     * - Left, Front, Right, and Back faces in the middle
     * - Down face at the bottom
    */
    private void displayCube() {
        for(char[] row : U) {
            System.out.print("      ");
            for(char cell : row) {
                System.out.print(getColorCode(cell) + cell + " ");
            }
            System.out.println();
        }
        for(int i = 0; i < 3; i++){
            for(char cell : L[i]){
                System.out.print(getColorCode(cell) + cell + " ");
            }
            for(char cell : F[i]){
                System.out.print(getColorCode(cell) + cell + " ");
            }
            for(char cell : R[i]){
                System.out.print(getColorCode(cell) + cell + " ");
            }
            for(char cell : B[i]){
                System.out.print(getColorCode(cell) + cell + " ");
            }
            System.out.println();
        }
        for(char[] row : D) {
            System.out.print("      ");
            for(char cell : row) {
                System.out.print(getColorCode(cell) + cell + " ");
            }
            System.out.println();
        }
        System.out.println(ANSI_RESET); // Reset to default color
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        for (char[] row : U) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        for (char[] row : R) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        for (char[] row : F) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        for (char[] row : D) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        for (char[] row : L) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        for (char[] row : B) {
            for (char cell : row) {
                sb.append(getFaceName(cell));
            }
        }
        return sb.toString();
    }

    public boolean isSolved() {
        char[][][] faces = {U, D, L, R, F, B};
        char[] colors = {'W', 'Y', 'O', 'R', 'G', 'B'};
        for (int i = 0; i < faces.length; i++) {
            char[][] face = faces[i];
            char color = colors[i];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (face[j][k] != color) {
                        return false; // Found a cell that does not match the expected color
                    }
                }
            }
        }
        return true; // All faces are correctly colored
    }

    public String solveCube() {
        if(!isSolved()) {
        String solution = Search.solution(toString(), 21, 1000, false);
        return solution;
        } else {
            return "Cube is already solved!";
        }
    }

    /**
     * Generates a random scramble sequence for the cube.
     * @return A string containing a random sequence of moves to scramble the cube.
     */
    public String scrambleCube() {
        Random random = new Random();
        int moveCount = random.nextInt(5) + 10; // Between 10 - 15 moves
        StringBuilder scramble = new StringBuilder();
        char lastFace = ' ';
        
        // Move types: normal, prime ('), and double (2)
        String[] moveTypes = {"", "'", "2"};
        
        // Possible faces to rotate
        char[] faces = {'U', 'D', 'L', 'R', 'F', 'B'};
        
        for (int i = 0; i < moveCount; i++) {
            char face;
            // Ensure we don't repeat the same face twice in a row
            do {
                face = faces[random.nextInt(faces.length)];
            } while (face == lastFace);
            
            lastFace = face;
            String moveType = moveTypes[random.nextInt(moveTypes.length)];
            
            scramble.append(face).append(moveType).append(" ");
        }
        
        String scrambleStr = scramble.toString().trim();
        
        return scrambleStr;
    }

    // --- Private Helper Methods ---
    
    /**
     * Fills a given face with a specific color character.
     * @param face The 3x3 face array to fill.
     * @param color The character representing the color to fill.
     */
    private void fillFace(char[][] face, char color) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                face[i][j] = color;
            }
        }
    }

    /**
     * Returns the ANSI color code for a given cell character.
     * @param cell The character representing the color of the cell.
     * @return The ANSI color code string for the cell.
     */
    private String getColorCode(char cell) {
        switch(cell){
            case 'W': return ANSI_COLOR[0];
            case 'Y': return ANSI_COLOR[1];
            case 'O': return ANSI_COLOR[2];
            case 'R': return ANSI_COLOR[3];
            case 'G': return ANSI_COLOR[4];
            case 'B': return ANSI_COLOR[5];
            default: return ANSI_RESET;
        }
    }

    // U: White, D: Yellow, L: Orange, R: Red, F: Green, B: Blue
    private static char getFaceName(char color){
        switch (color) {
            case 'W':
                return 'U';
            case 'Y':
                return 'D';
            case 'O':
                return 'L';
            case 'R':
                return 'R';
            case 'G':
                return 'F';
            case 'B':
                return 'B';
            default:
                return ' ';
        }
    }

    /**
     * Rotates a given face 90 degrees clockwise.
     * @param face The 3x3 face array to rotate.
     */
    private void rotateClockwise(char[][] face) {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < i; j++){
                char temp = face[i][j];
                face[i][j] = face[j][i];
                face[j][i] = temp;
            }
        }
        for(int i = 0; i < 3; i++){
            char temp = face[i][0];
            face[i][0] = face[i][2];
            face[i][2] = temp;
        }
    }

    /**
     * Rotates the upper (U) face clockwise and updates the adjacent faces accordingly.
     */
    private void rotateUpper() {
        rotateClockwise(U);
        char[] temp = F[0];
        F[0] = R[0].clone();
        R[0] = B[0].clone();
        B[0] = L[0].clone();
        L[0] = temp;
    }

    /**
     * Rotates the Front (F) face clockwise and updates the adjacent faces accordingly.
     */
    private void rotateFront() {
        rotateClockwise(F);
        char[] temp = U[2].clone();
        for (int i = 0; i < 3; i++) {
            U[2][i] = L[2-i][2];
        }
        for (int i = 0; i < 3; i++) {
            L[i][2] = D[0][i];
        }
        for (int i = 0; i < 3; i++) {
            D[0][i] = R[2-i][0];
        }
        for (int i = 0; i < 3; i++) {
            R[i][0] = temp[i];
        }
    }

    private void rotateRight() {
        rotateClockwise(R);
        char[] temp = {U[0][2], U[1][2], U[2][2]};
        for (int i = 0; i < 3; i++) {
            U[i][2] = F[i][2];
        }
        for (int i = 0; i < 3; i++) {
            F[i][2] = D[i][2];
        }
        for (int i = 0; i < 3; i++) {
            D[i][2] = B[2-i][0];
        }
        for (int i = 0; i < 3; i++) {
            B[2-i][0] = temp[i];
        }
    }

    private void rotateLeft() {
        rotateClockwise(L);
        char[] temp = {U[0][0], U[1][0], U[2][0]};
        for (int i = 0; i < 3; i++) {
            U[i][0] = B[2-i][2];
        }
        for (int i = 0; i < 3; i++) {
            B[i][2] = D[2-i][0];
        }
        for (int i = 0; i < 3; i++) {
            D[i][0] = F[i][0];
        }
        for (int i = 0; i < 3; i++) {
            F[i][0] = temp[i];
        }
    }

    private void rotateBack() {
        rotateClockwise(B);
        char[] temp = U[0].clone();
        for (int i = 0; i < 3; i++) {
            U[0][i] = R[i][2];
        }
        for (int i = 0; i < 3; i++) {
            R[i][2] = D[2][2-i];
        }
        for (int i = 0; i < 3; i++) {
            D[2][i] = L[i][0];
        }
        for (int i = 0; i < 3; i++) {
            L[i][0] = temp[2-i];
        }
    }

    private void rotateDown() {
        rotateClockwise(D);
        char[] temp = F[2].clone();
        F[2] = L[2].clone();
        L[2] = B[2].clone();
        B[2] = R[2].clone();
        R[2] = temp;
    }
    

    public static void main(String[] args) {
        RubikCube cube = new RubikCube();
        cube.displayCube();
        cube.applyRotation("B' U' L F B2 R' F L' F' U' R2 L2 U R2 L2 U' F2 U D2");
        cube.displayCube();
        System.out.println("Cube state: " + cube.toString().length());
        String solution = Search.solution(cube.toString(), 21, 1000, false);
        System.out.println("Solution found: " + solution);
        cube.applyRotation(solution);
        cube.displayCube();
        System.out.println("Cube state after solution: " + cube.toString().length());
    }
}