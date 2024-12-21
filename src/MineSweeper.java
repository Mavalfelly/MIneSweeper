import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MineSweeper {
    private class Mine extends JButton {
        int r;
        int c;

        public Mine(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 50;
    int numRows;
    int numCols;
    int boardWidth;
    int boardHeight;
    int mineCount;

    JFrame frame = new JFrame("Minesweeper");
    JLabel label1 = new JLabel();
    JPanel panel1 = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton resetButton = new JButton("Reset");

    Mine[][] board;
    ArrayList<Mine> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    public MineSweeper() {
        showStartScreen();
    }

    private void showStartScreen() {
        JFrame startFrame = new JFrame("Minesweeper Setup");
        startFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel welcome = new JLabel("<html><center>Welcome to Matts Minesweeper.<br>Please select the difficulty and board size below</center></html>");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setVerticalAlignment(JLabel.CENTER);
        welcome.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 30, 20);
        startFrame.add(welcome, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.anchor = GridBagConstraints.EAST;

        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        String[] difficulties = {"Easy (10 mines)", "Medium (20 mines)", "Hard (30 mines)", "Custom"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);

        JLabel sizeLabel = new JLabel("Board Size:");
        String[] sizes = {"8x8", "10x10", "12x12", "Custom"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);

        JTextField customMinesField = new JTextField(5);
        JTextField customSizeField = new JTextField(5);
        customMinesField.setEnabled(false);
        customSizeField.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        startFrame.add(difficultyLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        startFrame.add(difficultyCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        startFrame.add(new JLabel("Custom mines:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        startFrame.add(customMinesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        startFrame.add(sizeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        startFrame.add(sizeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        startFrame.add(new JLabel("Custom size:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        startFrame.add(customSizeField, gbc);

        JButton startButton = new JButton("Start Game");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);
        startFrame.add(startButton, gbc);

        difficultyCombo.addActionListener(e -> {
            customMinesField.setEnabled(difficultyCombo.getSelectedItem().equals("Custom"));
        });

        sizeCombo.addActionListener(e -> {
            customSizeField.setEnabled(sizeCombo.getSelectedItem().equals("Custom"));
        });

        startButton.addActionListener(e -> {
            int size;
            int mines;
            
            String selectedSize = (String) sizeCombo.getSelectedItem();
            if (selectedSize.equals("Custom")) {
                try {
                    size = Integer.parseInt(customSizeField.getText());
                    if (size < 5 || size > 20) {
                        JOptionPane.showMessageDialog(startFrame, "Please enter a size between 5 and 20");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(startFrame, "Please enter a valid number for size");
                    return;
                }
            } else {
                size = Integer.parseInt(selectedSize.split("x")[0]);
            }
            
            String selectedDifficulty = (String) difficultyCombo.getSelectedItem();
            if (selectedDifficulty.equals("Custom")) {
                try {
                    mines = Integer.parseInt(customMinesField.getText());
                    if (mines < 1 || mines > (size * size) - 1) {
                        JOptionPane.showMessageDialog(startFrame, 
                            "Please enter a valid number of mines (1 to " + ((size * size) - 1) + ")");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(startFrame, "Please enter a valid number for mines");
                    return;
                }
            } else {
                mines = Integer.parseInt(selectedDifficulty.split(" ")[1].replace("(", "").replace(")", ""));
            }
            
            startFrame.dispose();
            initializeGame(size, mines);
        });

        startFrame.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        startFrame.pack();
        startFrame.setMinimumSize(new Dimension(500, 400));
        startFrame.setLocationRelativeTo(null);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setVisible(true);
    }

    private void initializeGame(int size, int mines) {
        numRows = size;
        numCols = size;
        mineCount = mines;
        board = new Mine[numRows][numCols];

        frame.setSize(numCols * tileSize + 50, numRows * tileSize + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        label1.setFont(new Font("Arial", Font.BOLD, 20));
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setText("Minesweeper: " + mineCount + " mines");

        panel1.setLayout(new BorderLayout());
        panel1.add(label1, BorderLayout.CENTER);

        resetButton.setEnabled(false);
        resetButton.addActionListener(e -> {
            frame.dispose();
            new MineSweeper();
        });
        panel1.add(resetButton, BorderLayout.EAST);

        frame.add(panel1, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Mine tile = new Mine(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.BOLD, 20));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;

                        Mine tile = (Mine) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().isEmpty()) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().isEmpty() && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        setMines();
    }

    void setMines() {
        mineList = new ArrayList<>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            Mine tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    void revealMines() {
        for (Mine tile : mineList) {
            tile.setText("💣");
            tile.setEnabled(false);
        }

        gameOver = true;
        label1.setText("Game Over!");
        resetButton.setEnabled(true);
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        Mine tile = board[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;

        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);

        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);

        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
            switch (minesFound) {
                case 1 -> tile.setForeground(Color.RED);
                case 2 -> tile.setForeground(Color.BLUE);
                case 3 -> tile.setForeground(Color.GREEN);
                default -> tile.setForeground(Color.BLACK);
            }
        } else {
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);

            checkMine(r, c - 1);
            checkMine(r, c + 1);

            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            label1.setText("Mines Cleared!");
            resetButton.setEnabled(true);
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;
        return mineList.contains(board[r][c]) ? 1 : 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MineSweeper::new);
    }
}