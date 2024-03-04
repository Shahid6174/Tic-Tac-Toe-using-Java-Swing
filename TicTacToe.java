/*
 * Author - Mohammed Shahid
 * Project - TIc-Tac-Toe
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[] buttons = new JButton[9];
    private int[] board = new int[9];
    private boolean xTurn = true;
    private int gameMode; // 1 for Single-Player, 2 for Multi-Player
    private boolean gameWon = false; // Flag to track if the game has been won

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        getContentPane().setBackground(new Color(0, 255, 0, 100)); 

         playIntroSound();

        // Ask for game mode choice
        String[] options = {"Single-Player", "Multi-Player"};
        int choice = JOptionPane.showOptionDialog(this, "Choose game mode", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            gameMode = 1; // Single-Player
        } else {
            gameMode = 2; // Multi-Player
        }

        // Initialize buttons
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 40));
            buttons[i].setBackground(Color.CYAN);
            buttons[i].setOpaque(true);
            buttons[i].addActionListener(this);
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
            add(buttons[i]);
        }

        setVisible(true);
    }

    private void playIntroSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("intro.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing intro sound: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int pos = -1;
        for (int i = 0; i < 9; i++) {
            if (buttons[i] == clickedButton) {
                pos = i;
                break;
            }
        }

        if (gameWon || board[pos] != 0) { // Check if the game is won or the position is already occupied
            JOptionPane.showMessageDialog(this, "Wrong move!!");
            return;
        }

        if (xTurn) {
            clickedButton.setText("X");
            board[pos] = -1;
        } else {
            clickedButton.setText("O");
            board[pos] = 1;
        }
        xTurn = !xTurn;

        playClickSound(); // Play sound after each click

        checkForWin();

        if (gameMode == 1 && !xTurn && !gameWon) {
            // If single-player mode and it's computer's turn and game is not won
            computerTurn();
        }

        // Check for a tie after X's move
        if (isBoardFull(board)) {
            JOptionPane.showMessageDialog(this, "It's a tie!");
            resetGame();
        }
    }

    private void computerTurn() {
        int[] move = minimax(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        buttons[move[1]].setText("O");
        board[move[1]] = 1;
        xTurn = true;

        checkForWin(); // Check for win after computer's move
    }

    private int[] minimax(int[] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int result = checkForWinner(board);
        if (result != 0) {
            return new int[]{result * depth, -1};
        }
        if (isBoardFull(board)) {
            return new int[]{0, -1};
        }

        if (maximizingPlayer) {
            int[] bestMove = {-1, -1};
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 0) {
                    board[i] = 1;
                    int[] eval = minimax(board, depth + 1, alpha, beta, false);
                    board[i] = 0;
                    if (eval[0] > maxEval) {
                        maxEval = eval[0];
                        bestMove[0] = maxEval;
                        bestMove[1] = i;
                    }
                    alpha = Math.max(alpha, maxEval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return bestMove;
        } else {
            int[] bestMove = {-1, -1};
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == 0) {
                    board[i] = -1;
                    int[] eval = minimax(board, depth + 1, alpha, beta, true);
                    board[i] = 0;
                    if (eval[0] < minEval) {
                        minEval = eval[0];
                        bestMove[0] = minEval;
                        bestMove[1] = i;
                    }
                    beta = Math.min(beta, minEval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return bestMove;
        }
    }

    private int checkForWinner(int[] board) {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] condition : winConditions) {
            int a = condition[0];
            int b = condition[1];
            int c = condition[2];

            if (board[a] != 0 && board[a] == board[b] && board[a] == board[c]) {
                return board[a];
            }
        }

        return 0;
    }

    private boolean isBoardFull(int[] board) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                return false;
            }
        }
        return true;
    }

    private void checkForWin() {
        int winner = checkForWinner(board);
        if (winner != 0) {
            if (winner == -1) 
            {
                playerXsound();
                JOptionPane.showMessageDialog(this, "Player X wins");
            } 
            else {
                playerOsound();
                JOptionPane.showMessageDialog(this, "Player O wins");
                
            }
            resetGame(); // Reset the game after either player wins
            return;
        }
        
        if (isBoardFull(board)) {
            playTieSound();
            JOptionPane.showMessageDialog(this, "It's a tie!");
            resetGame();
            return;
        }
    }
    
    private void playerXsound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("PlayerX.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
    }
    
    private void playerOsound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("PlayerO.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
    }
    
    private void playTieSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Tie.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
    }
    

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            board[i] = 0;
        }
        xTurn = true;
        gameWon = false; // Reset the gameWon flag
    }

    private void playClickSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("mixkit-arcade-game-jump-coin-216.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}
