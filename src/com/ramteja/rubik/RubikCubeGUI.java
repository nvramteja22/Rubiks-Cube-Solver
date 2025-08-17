package com.ramteja.rubik;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

// Main GUI class for the Rubik's Cube Simulator
public class RubikCubeGUI extends JFrame {
    private RubikCube cube;
    private JPanel cubePanel;
    private JTextField moveInput;
    private JButton applyButton;
    private JTextPane movesDisplay;
    private StyledDocument movesDocument;
    private int speedIndex = 1;
    private final int defaultDelayTime = 300;
    private int delayTime = 300;
    private int currentMoveIndex = -1;
    private List<String> currentMoves = new ArrayList<>();

    public RubikCubeGUI() {
        cube = new RubikCube();
        setTitle("Rubik's Cube Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Center panel holds cube display and moves panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        cubePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCube(g);
            }
        };
        cubePanel.setPreferredSize(new Dimension(400, 300));
        cubePanel.setBorder(BorderFactory.createTitledBorder("Cube Display"));
        
        JPanel movesPanel = createMovesPanel();
        
        centerPanel.add(cubePanel, BorderLayout.CENTER);
        centerPanel.add(movesPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom input panel
        JPanel controlPanel = new JPanel();
        moveInput = new JTextField(20);
        applyButton = new JButton("Apply Move");
        controlPanel.add(new JLabel("Moves:"));
        controlPanel.add(moveInput);
        controlPanel.add(applyButton);
        add(controlPanel, BorderLayout.SOUTH);

        applyButton.addActionListener(e -> {
            String moves = moveInput.getText().trim();
            if (!moves.isEmpty()) {
                animateMoves(moves);
                moveInput.setText("");
            }
        });

        // Create all the move buttons - F R U B L D and variations
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 6, 10, 8));
        String[] buttonLabels = {
                "F", "R", "U", "B", "L", "D",
                "F'", "R'", "U'", "B'", "L'", "D'",
                "F2", "R2", "U2", "B2", "L2", "D2",
        };
        for (String label : buttonLabels) {
            JButton btn = new JButton(label);
            btn.addActionListener(e -> {
                addMoveToHistory(label);
                cube.applyRotation(label);
                cubePanel.repaint();
            });
            buttonsPanel.add(btn);
        }
        add(buttonsPanel, BorderLayout.NORTH);

        // Left side panel with solve controls
        JPanel solvePanel = new JPanel();
        JButton resetButton = new JButton("Reset Cube");
        resetButton.addActionListener(e -> {
            cube.resetCube();
            clearMoves();
            cubePanel.repaint();
        });
        resetButton.setToolTipText("Resets the cube to its original state");
        JButton solveButton = new JButton("Solve Cube");
        solveButton.addActionListener(e -> {
            String solution = cube.solveCube();
            animateMoves(solution);
        });
        solveButton.setToolTipText("Solves the cube and shows the moves");

        JButton scrambleButton = new JButton("Scramble Cube");
        scrambleButton.addActionListener(e -> {
            cube.resetCube();
            clearMoves();
            cubePanel.repaint();
            String scrambleMoves = cube.scrambleCube();
            animateMoves(scrambleMoves);
        });
        scrambleButton.setToolTipText("Randomly scrambles the cube and shows the moves");
        // Slider for animation speed
        JSlider speedSlider = new JSlider(0, 2, 1); // default at index 1 (1x)
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPreferredSize(new Dimension(100,40));

        // Map positions to speed labels
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0.5x"));
        labelTable.put(1, new JLabel("1x"));
        labelTable.put(2, new JLabel("2x"));
        speedSlider.setLabelTable(labelTable);
        speedSlider.setPaintLabels(true);

        // Map index → actual speed values
        double[] speeds = {0.5, 1.0, 2.0};

        // Listener to react to slider changes
        speedSlider.addChangeListener(e -> {
            int value = speedSlider.getValue();
            double speed = speeds[value];
            System.out.println("Selected speed: " + speed + "x");
            delayTime = (int) (defaultDelayTime / speed); // adjust delay based on speed
        });
        solvePanel.add(resetButton);
        solvePanel.add(solveButton);
        solvePanel.add(scrambleButton);
        solvePanel.add(new Label("Animation Speed:"));
        solvePanel.add(speedSlider);

        solvePanel.setLayout(new FlowLayout());
        solvePanel.setPreferredSize(new Dimension(100, 300));
        add(solvePanel, BorderLayout.WEST);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setVisible(true);
    }

    private JPanel createMovesPanel() {
        JPanel movesPanel = new JPanel(new BorderLayout());
        movesPanel.setPreferredSize(new Dimension(250, 300));
        movesPanel.setBorder(BorderFactory.createTitledBorder("Moves Sequence"));

        // Text area for showing moves with different colors
        movesDisplay = new JTextPane();
        movesDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        movesDisplay.setEditable(false);
        movesDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        movesDocument = movesDisplay.getStyledDocument();

        // Setup text styles for highlighting moves
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style normalStyle = movesDocument.addStyle("normal", defaultStyle);
        StyleConstants.setForeground(normalStyle, Color.BLACK);
        
        Style highlightStyle = movesDocument.addStyle("highlight", normalStyle);
        StyleConstants.setBackground(highlightStyle, Color.YELLOW);
        StyleConstants.setBold(highlightStyle, true);
        
        Style completedStyle = movesDocument.addStyle("completed", normalStyle);
        StyleConstants.setForeground(completedStyle, Color.GREEN);
        StyleConstants.setBold(completedStyle, true);

        JScrollPane scrollPane = new JScrollPane(movesDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        movesPanel.add(scrollPane, BorderLayout.CENTER);

        // Stats at the bottom
        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        JLabel movesCountLabel = new JLabel("Total Moves: 0");
        JLabel currentMoveLabel = new JLabel("Current: -");
        statsPanel.add(movesCountLabel);
        statsPanel.add(currentMoveLabel);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        movesPanel.add(statsPanel, BorderLayout.SOUTH);

        return movesPanel;
    }

    private void addMoveToHistory(String move) {
        currentMoves.add(move);
        updateMovesDisplayStatic();
    }

    private void clearMoves() {
        try {
            movesDocument.remove(0, movesDocument.getLength());
            currentMoves.clear();
            currentMoveIndex = -1;
            updateMovesDisplay();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Animate through a sequence of moves with delays
    private void animateMoves(String moves) {
        String[] moveArray = moves.trim().split("\\s+");
        currentMoves = new ArrayList<>();
        for (String move : moveArray) {
            if (!move.isEmpty()) {
                currentMoves.add(move);
            }
        }
        
        updateMovesDisplay();

        // Run animation in separate thread so GUI doesn't freeze
        new Thread(() -> {
            for (int i = 0; i < currentMoves.size(); i++) {
                String move = currentMoves.get(i);
                currentMoveIndex = i;
                
                SwingUtilities.invokeLater(this::updateMovesDisplay);
                
                cube.applyRotation(move);
                SwingUtilities.invokeLater(() -> cubePanel.repaint());
                
                try {
                    Thread.sleep(delayTime); // 200ms delay between moves
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            currentMoveIndex = -1;
            SwingUtilities.invokeLater(this::updateMovesDisplay);
        }).start();
    }

    private void updateMovesDisplay() {
        try {
            movesDocument.remove(0, movesDocument.getLength());
            
            // Color each move based on its state
            for (int i = 0; i < currentMoves.size(); i++) {
                String move = currentMoves.get(i);
                String styleType;
                
                if (i == currentMoveIndex) {
                    styleType = "highlight"; // current move is yellow
                } else if (i < currentMoveIndex) {
                    styleType = "completed"; // completed moves are green
                } else {
                    styleType = "normal"; // future moves are black
                }
                
                movesDocument.insertString(movesDocument.getLength(), move, movesDocument.getStyle(styleType));
                
                if (i < currentMoves.size() - 1) {
                    movesDocument.insertString(movesDocument.getLength(), " ", movesDocument.getStyle("normal"));
                }
            }
            
            updateStats();
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Same as updateMovesDisplay but without the highlighting
    private void updateMovesDisplayStatic() {
        try {
            movesDocument.remove(0, movesDocument.getLength());
            
            for (int i = 0; i < currentMoves.size(); i++) {
                String move = currentMoves.get(i);
                movesDocument.insertString(movesDocument.getLength(), move, movesDocument.getStyle("normal"));
                
                if (i < currentMoves.size() - 1) {
                    movesDocument.insertString(movesDocument.getLength(), " ", movesDocument.getStyle("normal"));
                }
            }
            
            updateStats();
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Update the stats labels - not cleanest way but works
    private void updateStats() {
        Component[] components = ((JPanel) movesDisplay.getParent().getParent().getParent()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getBorder() instanceof TitledBorder) {
                TitledBorder border = (TitledBorder) ((JPanel) comp).getBorder();
                if ("Statistics".equals(border.getTitle())) {
                    JPanel statsPanel = (JPanel) comp;
                    JLabel movesCount = (JLabel) statsPanel.getComponent(0);
                    JLabel currentMove = (JLabel) statsPanel.getComponent(1);
                    
                    movesCount.setText("Total Moves: " + currentMoves.size());
                    if (currentMoveIndex >= 0 && currentMoveIndex < currentMoves.size()) {
                        currentMove.setText("Current: " + currentMoves.get(currentMoveIndex) + 
                                          " (" + (currentMoveIndex + 1) + "/" + currentMoves.size() + ")");
                    } else {
                        currentMove.setText("Current: -");
                    }
                    break;
                }
            }
        }
    }

    // Draw the cube in 2D - shows all 6 faces laid out flat
    private void drawCube(Graphics g) {
        int size = 30;
        int offsetX = 100, offsetY = 40;
        char[][][] faces = { cube.U, cube.R, cube.F, cube.D, cube.L, cube.B };
        Color[] colors = { Color.WHITE, Color.RED, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.BLUE };

        // Top face (U)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                drawCell(g, offsetX + j * size, offsetY + i * size, faces[0][i][j], colors[faceIndex(faces[0][i][j])]);

        // Middle row: Left, Front, Right, Back
        int[] faceOrder = { 4, 2, 1, 5 }; // L F R B
        for (int f = 0; f < 4; f++)
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    drawCell(g, 10 + (j + 3 * f) * size, offsetY + 3 * size + i * size, faces[faceOrder[f]][i][j],
                            colors[faceIndex(faces[faceOrder[f]][i][j])]);

        // Bottom face (D)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                drawCell(g, offsetX + j * size, offsetY + 6 * size + i * size, faces[3][i][j],
                        colors[faceIndex(faces[3][i][j])]);
    }

    private void drawCell(Graphics g, int x, int y, char label, Color color) {
        g.setColor(color);
        g.fillRect(x, y, 30, 30);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 30, 30);
        g.drawString(String.valueOf(label), x + 12, y + 18);
    }

    private int faceIndex(char c) {
        switch (c) {
            case 'W': return 0; // white
            case 'R': return 1; // red
            case 'G': return 2; // green  
            case 'Y': return 3; // yellow
            case 'O': return 4; // orange
            case 'B': return 5; // blue
            default: return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RubikCubeGUI::new);
    }
}
