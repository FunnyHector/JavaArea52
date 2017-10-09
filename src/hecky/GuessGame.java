/*

This was the code challenge that a friend of mine did when Powershop/Flux interviewed him.

I got the challenge from him afterwards and felt fun, so I did some experiment.
 */

package hecky;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A number guessing game. Each round the game asks you to enter a 4 digits number, and tells you how many correct
 * digits and how many misplaced digits. You win if you have 4 correct digits and 0 misplaced digits.
 */
public class GuessGame {

    /**
     * Number of digits
     */
    private static final int NUM_OF_DIGITS = 4;

    /**
     * A random int generator
     */
    private static final Random RANDOM = new Random();

    /**
     * A scanner for retrieving user input
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * The magic number
     */
    private int[] magicNumber;

    /**
     * How many rounds has attempted
     */
    private int rounds;

    /**
     * Boolean flag to show whether the player has won.
     */
    private boolean hasWon;

    /**
     * Constructor
     */
    public GuessGame() {
        this.magicNumber = generateMagicNumber();
        this.rounds = 0;
        this.hasWon = false;
    }

    /**
     * Generates the magic number.
     */
    public int[] generateMagicNumber() {
        int[] magicNumber = new int[NUM_OF_DIGITS];

        for (int i = 0; i < NUM_OF_DIGITS; i++) {
            magicNumber[i] = RANDOM.nextInt(10);
        }

        return magicNumber;
    }

    /**
     * Run the game
     */
    private void run() {
        welcomePrompt();

        while (!hasWon) {
            runRound();
            rounds++;
        }

        endingPrompt();
    }

    /**
     * Prompt some fancy words to welcome the player
     */
    private void welcomePrompt() {
        System.out.println("Welcome!");
        System.out.println("You are about to guess a 4-digit number.");
    }

    /**
     * Run one round of the game.
     */
    private void runRound() {
        int[] userGuess = getUserGuess();
        int[] result = compareUserGuessWithMagicNum(userGuess);

        int numCorrectDigits = result[0];
        int numMisplacedDigits = result[1];

        if (numCorrectDigits == NUM_OF_DIGITS && numMisplacedDigits == 0) {
            this.hasWon = true;
        } else {
            System.out.println("The number of correct digits: " + numCorrectDigits);
            System.out.println("The number of misplaced digits: " + numMisplacedDigits);
        }
    }

    /**
     * Compare the user guess and the magic number, and analyse for how many correct digits and how many misplaced
     * digits.
     * <p>
     * E.g. <br> Magic number: 3456 <br> User guess:   1736 <br> Number of correct digits: 1 (digit 6) <br> Number of
     * misplaced digits: 1 (digit 3)
     *
     * @param userGuess user-guessed digits
     * @return a two elements array of int, where the first int is the number of correct digits, and the second int is
     * the number of misplaced digits.
     */
    private int[] compareUserGuessWithMagicNum(int[] userGuess) {
        int numCorrectDigits = 0;
        int numMisplacedDigits = 0;

        // first sweep: find out the number of correct digits
        for (int i = 0; i < NUM_OF_DIGITS; i++) {
            if (userGuess[i] == this.magicNumber[i]) {
                numCorrectDigits++;
            }
        }

        // second sweep: find out the number of misplaced digits
        for (int i = 0; i < NUM_OF_DIGITS; i++) {
            int digitFromGuess = userGuess[i];

            for (int j = 0; j < NUM_OF_DIGITS; j++) {
                if (i == j) {
                    continue;  // this is the correct digit case, we need to avoid double-counting it.
                }

                if (this.magicNumber[j] == digitFromGuess) {
                    numMisplacedDigits++;
                    break;  // we also need to avoid re-counting it if there are repeated digits.
                }
            }
        }

        return new int[]{ numCorrectDigits, numMisplacedDigits };
    }

    /**
     * Get the user-guessed digits.
     *
     * @return the user-guessed digits
     */
    public int[] getUserGuess() {
        System.out.println("Please make a guess containing only " + NUM_OF_DIGITS + " digits:");

        String line = SCANNER.nextLine();

        while (line.length() != NUM_OF_DIGITS || line.chars().anyMatch(c -> !Character.isDigit(c))) {
            System.out.println("Incorrect input. Please make a guess containing only " + NUM_OF_DIGITS + " digits:");
            line = SCANNER.nextLine();
        }

        int[] userGuess = new int[NUM_OF_DIGITS];

        for (int i = 0; i < NUM_OF_DIGITS; i++) {
            userGuess[i] = Integer.valueOf(line.substring(i, i + 1));
        }

        return userGuess;
    }

    /**
     * Prompt some fancy words to show the summary
     */
    private void endingPrompt() {
        String magicNum = Arrays.stream(magicNumber).mapToObj(String::valueOf).collect(Collectors.joining());

        System.out.println("You won! The magic number is: " + magicNum);
        System.out.println("You used " + rounds + " attempts.");
    }

    /**
     * Main function
     *
     * @param args
     */
    public static void main(String[] args) {
        new GuessGame().run();
    }
}
