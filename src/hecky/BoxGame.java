/*

This was the code challenge I did in Sep 2016 when Powershop/Flux interviewed me.

I recently re-wrote it down in IDE, and polished a little bit.

 */

package hecky;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A number guessing game. If player guess two boxes in a row that contain same character, these two boxes are revealed.
 * It's a famous game, you know the rule.
 *
 * @author Hector
 */
public class BoxGame {

    /**
     * Number of boxes
     */
    private static final int NUM_BOXES = 20;

    /**
     * Number of lives
     */
    private static final int NUM_LIVES = 40;

    /**
     * The alphabet
     */
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * A random number generator
     */
    private static final Random RANDOM = new Random();

    /**
     * A scanner for retrieving user input
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Boxes. We use the index as id for each box.
     */
    private List<Box> boxes;

    /**
     * How many turns/lives left for player to guess
     */
    private int turnsLeft;

    /**
     * The id of the last guessed box
     */
    private int lastGuessedId;

    /**
     * A flag to indicate whether we meet the win condition
     */
    private boolean won;

    /**
     * A flag to indicate whether we meet the lose condition
     */
    private boolean lost;

    /**
     * Constructor
     */
    public BoxGame() {
        initialiseBox();

        this.turnsLeft = NUM_LIVES;
        this.lastGuessedId = -1;
        this.won = false;
        this.lost = false;
    }

    /**
     * Initialise and randomise boxes with characters.
     */
    private void initialiseBox() {
        if (NUM_BOXES % 2 != 0) {
            throw new RuntimeException("The number of boxes has to be even!");
        }

        // 1. make half of characters
        char[] halfOfChars = new char[NUM_BOXES / 2];
        List<Character> charList = new ArrayList<>();

        for (int i = 0; i < NUM_BOXES / 2; i++) {
            halfOfChars[i] = generateRandomAlphabet();
        }

        // 2. duplicate the half we just made
        for (char c : halfOfChars) {
            charList.add(c);
            charList.add(c);  // make that double
        }

        // 3. shuffle characters
        Collections.shuffle(charList);

        // 4. make our boxes
        this.boxes = new ArrayList<>(NUM_BOXES);
        for (int i = 0; i < NUM_BOXES; i++) {
            this.boxes.add(new Box(charList.get(i), false));
        }
    }

    /**
     * Run the game
     */
    private void run() {
        startingMessage();

        while (!won && !lost) {
            runRound();

            if (this.boxes.stream().allMatch(Box::isRevealed)) {
                won = true;
            }

            if (turnsLeft <= 0) {
                lost = true;
            }
        }

        endingMessage();
    }

    /**
     * Display some fancy welcome message
     */
    private void startingMessage() {
        System.out.println("Welcome to this crappy game.");
        System.out.println("Let's pretend that the game just showed you all the instructions");
    }

    /**
     * Run each round
     */
    private void runRound() {
        promptRound();

        int guessedId = getUserInput(1, NUM_BOXES) - 1;  // minus 1 is for 0-indexed array list.
        Box guessedBox = this.boxes.get(guessedId);

        while (guessedBox.isRevealed()) {
            System.out.println("You have guessed it. Try another one:");
            guessedId = getUserInput(1, NUM_BOXES) - 1;
            guessedBox = this.boxes.get(guessedId);
        }

        if (lastGuessedId < 0) {
            // this is the odd turn, i.e. player makes the first guess of two in a row
            guessedBox.setRevealed(true);
            this.lastGuessedId = guessedId;
        } else {
            // this is the even turn, i.e. player makes the second guess of two in a row
            Box lastGuessedBox = this.boxes.get(this.lastGuessedId);

            if (guessedBox.cha == lastGuessedBox.cha) {
                guessedBox.setRevealed(true);
            } else {
                lastGuessedBox.setRevealed(false);
            }

            this.lastGuessedId = -1;
        }

        turnsLeft--;
    }

    /**
     * Display current game status for each turn
     */
    private void promptRound() {
        System.out.println("You have " + turnsLeft + " guesses left. The boxes:");
        displayBoxes();
        System.out.println("Please take a guess (from 1 to 20):");
    }

    /**
     * Display all boxes. Revealed boxes are displayed as the character inside, and unrevealed boxes are displayed as a
     * '#'
     */
    private void displayBoxes() {
        int numLines = NUM_BOXES / 10;

        for (int i = 0; i < numLines; i++) {
            System.out.println("123456789X");  // a line of indices. Fantastic UI design isn't it?

            List<Box> lineOfBoxes = this.boxes.subList(i * 10, i * 10 + 10);
            String boxesStr = lineOfBoxes.stream().map(Box::charToDisplay).collect(Collectors.joining());

            System.out.println(boxesStr);
        }
    }

    /**
     * Display some fancy ending message for summary
     */
    private void endingMessage() {
        if (won) {
            System.out.println("Congratulations! You win!");
        } else if (lost) {
            System.out.println("You have no more guesses!");
            System.out.println("Here are all the boxes:");

            String boxesStr = this.boxes.stream()
                    .map(box -> String.valueOf(box.cha))
                    .collect(Collectors.joining());

            System.out.println(boxesStr);
        }
    }

    /**
     * Get user input as a int. The int has to be between <i>floor</i> and <i>ceiling</i>.
     *
     * @param floor   the left boundary of wanted input
     * @param ceiling the right boundary of wanted input
     * @return
     */
    private int getUserInput(int floor, int ceiling) {
        if (floor >= ceiling) {
            throw new IllegalArgumentException("floor has to be less or equal to ceiling");
        }

        String input = SCANNER.nextLine();
        int index;

        while (true) {
            try {
                index = Integer.valueOf(input);

                if (index >= floor && index <= ceiling) {
                    break;  // exit point
                } else {
                    System.out.println("please enter an integer between 1 and 20:");
                    input = SCANNER.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.println("please enter an integer:");
                input = SCANNER.nextLine();
            }
        }

        return index;
    }

    /**
     * @return a random alphabetic char
     */
    private char generateRandomAlphabet() {
        return ALPHABET[RANDOM.nextInt(26)];
    }

    /**
     * Main function
     *
     * @param args
     */
    public static void main(String[] args) {
        new BoxGame().run();
    }

    /**
     * A data structure to represent a box.
     */
    class Box {

        /**
         * The character held by the box
         */
        final char cha;

        /**
         * Is the box revealed?
         */
        private boolean isRevealed;

        /**
         * Constructor
         *
         * @param cha
         * @param isRevealed
         */
        Box(char cha, boolean isRevealed) {
            this.cha = cha;
            this.isRevealed = isRevealed;
        }

        /**
         * @return true if the box is revealed, or false if it's not
         */
        boolean isRevealed() {
            return this.isRevealed;
        }

        /**
         * Setter
         *
         * @param revealed
         */
        void setRevealed(boolean revealed) {
            this.isRevealed = revealed;
        }

        /**
         * @return the character if it's revealed, or '#' if it's not
         */
        String charToDisplay() {
            return isRevealed ? String.valueOf(cha) : "#";
        }
    }
}
