/**
 * this is a wordle game, here a random word will be selected and u have to guess the word in 6 attempts
 * @author Gauresh Rekdo
 * @version 1.0
 * @since 14/08/2024
 * @lastmodified 09/09/2024
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class WordleGameGui {
    private static final int MAX_ATTEMPTS = 8;
    private static final int WORD_LENGTH = 5;
    private static String SECRET_WORD;
    private static int attemptsLeft;
    private static List<String> guesses;
    private static JPanel hintPanel;
    private static JTextField guessField;
    private static JLabel attemptLabel;
    private static JTextArea hintPara;
    public static void main(String[] args) {
        // Load words from CSV file and select a random word
        List<String> words = loadWordsFromCSV("words.csv");
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No words found in the CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SECRET_WORD = selectRandomWord(words);
        attemptsLeft = MAX_ATTEMPTS;
        guesses = new ArrayList<>();

        // Create and set up the window
        JFrame frame = new JFrame("Wordle Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());

        // Create and set up components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 1));
        
        attemptLabel = new JLabel("Attempts left: " + attemptsLeft);
        inputPanel.add(attemptLabel);

        guessField = new JTextField();
        inputPanel.add(guessField);

        JButton guessButton = new JButton("Submit Guess");
        inputPanel.add(guessButton);

        JTextArea hintPara = new JTextArea(
        "Hints: If backgroundcolor of letter is red then that word is not there in \nIf color is yellow then that letter is there in word but not in correct place \nIf color is green then that letter is there in that word and it is on correct place..!" 
        );
        inputPanel.add(hintPara);

        hintPanel = new JPanel();
        hintPanel.setLayout(new GridLayout(MAX_ATTEMPTS, WORD_LENGTH));

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(hintPanel), BorderLayout.CENTER);

        // Add action listener to the button
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processGuess();
            }
        });

        // Add key listener to the guess field
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processGuess();
                }
            }
        });

        // Display the window
        frame.setVisible(true);
    }

    private static List<String> loadWordsFromCSV(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] wordArray = line.split(",");
                for (String word : wordArray) {
                    words.add(word.trim().toUpperCase());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading the CSV file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return words;
    }

    private static String selectRandomWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private static void processGuess() {
        String guess = guessField.getText().toUpperCase().trim();

        if (guess.length() != WORD_LENGTH || !guess.matches("[A-Z]*")) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a " + WORD_LENGTH + "-letter word.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        guesses.add(guess);
        attemptsLeft--;
        attemptLabel.setText("Attempts left: " + attemptsLeft);

        if (guess.equals(SECRET_WORD)) {
            displayHint(guess, true); // Show all green when guessed correctly
            JOptionPane.showMessageDialog(null, "Congratulations! You've guessed the word correctly!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (attemptsLeft <= 0) {
            JOptionPane.showMessageDialog(null, "Sorry, you've used all attempts. The word was: " + SECRET_WORD, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else {
            displayHint(guess, false); // Display normal hints
        }

        guessField.setText("");
    }

    private static void displayHint(String guess, boolean isCorrectGuess) {
        JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new GridLayout(1, WORD_LENGTH));

        for (int i = 0; i < WORD_LENGTH; i++) {
            JLabel letterLabel = new JLabel(String.valueOf(guess.charAt(i)), SwingConstants.CENTER);
            letterLabel.setOpaque(true);

            if (isCorrectGuess) {
                letterLabel.setBackground(Color.GREEN);
            } else if (guess.charAt(i) == SECRET_WORD.charAt(i)) {
                letterLabel.setBackground(Color.GREEN);
            } else if (SECRET_WORD.contains(String.valueOf(guess.charAt(i)))) {
                letterLabel.setBackground(Color.YELLOW);
            } else {
                letterLabel.setBackground(Color.RED);
            }

            letterLabel.setForeground(Color.WHITE);
            letterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guessPanel.add(letterLabel);
        }

        hintPanel.add(guessPanel);
        hintPanel.revalidate(); // Refresh the hint panel
        hintPanel.repaint(); // Ensure the new components are rendered
    }
}
