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

    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel label1 = new JLabel();
    JPanel panel1 = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount = 10;
    Mine[][] board = new Mine[numRows][numCols];
    ArrayList<Mine> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    MineSweeper() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        label1.setFont(new Font("Arial", Font.BOLD, 25));
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setText("Minesweeper: " + Integer.toString(mineCount));
        label1.setOpaque(true);

        panel1.setLayout(new BorderLayout());
        panel1.add(label1);
        frame.add(panel1, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Mine tile = new Mine(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        Mine tile = (Mine) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                }
                                else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                            }
                            else if (tile.getText() == "🚩") {
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
        mineList = new ArrayList<Mine>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            Mine tile = board[r][c]; 
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            Mine tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        label1.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        Mine tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        minesFound += countMine(r-1, c-1); 
        minesFound += countMine(r-1, c); 
        minesFound += countMine(r-1, c+1); 

        minesFound += countMine(r, c-1);    
        minesFound += countMine(r, c+1);    

        minesFound += countMine(r+1, c-1);  
        minesFound += countMine(r+1, c);    
        minesFound += countMine(r+1, c+1); 

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        }
        else {
            tile.setText("");
            
            checkMine(r-1, c-1);
            checkMine(r-1, c);
            checkMine(r-1, c+1);

            checkMine(r, c-1);
            checkMine(r, c+1);


            checkMine(r+1, c-1);
            checkMine(r+1, c);
            checkMine(r+1, c+1); 
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            label1.setText("Mines Cleared!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
}