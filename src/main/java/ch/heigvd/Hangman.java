package ch.heigvd;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "hangman game",
        description = "play the hangman game")
public class Hangman implements Runnable {

    @Parameters(index = "0", description = "type server or client")
    private String type = "client";

    @Option(names = {"-l", "--language"}, description = "Select game words language (default : EN)")
    private String language = "EN";

    @Option(names = {"-p", "--players"}, description = "Number of players (default : 1 player)")
    private Integer players = 1;

    @Option(names = {"-w", "--word-size"}, description = "define a minimum word size")
    private Integer word_size = 0;


    public static void main(String[] args) {
        CommandLine.run(new Hangman(), args);
    }

    @Override
    public void run() {
        type = type.toLowerCase();
        if(type.equals("server") || type.equals("client")){
            System.out.println("Hello " + type);
        } else {
            System.out.println("arg not valid. Use server or client");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        String[] words = {"java", "programming", "computer", "hangman", "developer"};
        String wordToGuess = words[(int)(Math.random() * words.length)];
        int incorrectGuesses = 0;

        char[] wordGuessed = new char[wordToGuess.length()];
        Set<Character> lettersGuessed = new TreeSet<>();

        boolean gameFinished = false;

        while (incorrectGuesses < 7 && !gameFinished) {
            System.out.println("Word to guess: " + displayWord(wordToGuess, wordGuessed));
            System.out.println("Lettres already guessed : " + lettersGuessed.toString());
            System.out.println("Incorrect guesses: " + incorrectGuesses);

            char letter = 0;
            boolean isValidInput = false;
            while (!isValidInput) {
                System.out.print("Guess a letter: ");
                String input = scanner.next();
                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    letter = input.charAt(0);
                    isValidInput = true;
                } else {
                    System.out.println("input not valid");
                }
            }

            if (lettersGuessed.contains(letter)) {
                System.out.println("letter already guessed!");
                continue;
            }

            lettersGuessed.add(letter);

            if (wordToGuess.contains(String.valueOf(letter))) {
                System.out.println("Good guess!");
                addLetter(letter, wordToGuess, wordGuessed);
            } else {
                System.out.println("Incorrect letter!");
                System.out.println(displayHangman(incorrectGuesses));
                incorrectGuesses++;
            }

            gameFinished = isWordGuessed(wordToGuess, wordGuessed);
        }

        if (gameFinished) {
            System.out.println("Congratulations, you guessed the word: " + wordToGuess);
        } else {
            System.out.println("Too bad, the word was: " + wordToGuess);
        }

        scanner.close();
    }


    public static String displayWord(String word, char[] wordGuessed) {
        StringBuilder display = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (contains(wordGuessed, letter)) {
                display.append(letter);
            } else {
                display.append("_");
            }
            display.append(" ");
        }
        return display.toString();
    }

    public static void addLetter(char letter, String word, char[] wordGuessed) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                wordGuessed[i] = letter;
            }
        }
    }

    public static boolean contains(char[] array, char value) {
        for (char element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWordGuessed(String word, char[] wordGuessed) {
        for (char letter : word.toCharArray()) {
            if (!contains(wordGuessed, letter)) {
                return false;
            }
        }
        return true;
    }

    public static String displayHangman(int incorrectGuesses) {
        String[] hangmanStages = {
                "  +---+\n" +
                "  |   |\n" +
                "      |\n" +
                "      |\n" +
                "      |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                "      |\n" +
                "      |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                "  |   |\n" +
                "      |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                " /|   |\n" +
                "      |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                " /|\\  |\n" +
                "      |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                " /|\\  |\n" +
                " /    |\n" +
                "      |\n" +
                "=========",

                "  +---+\n" +
                "  |   |\n" +
                "  O   |\n" +
                " /|\\  |\n" +
                " / \\  |\n" +
                "      |\n" +
                "========="
        };

        if (incorrectGuesses < 7) {
            return hangmanStages[incorrectGuesses];
        } else {
            return hangmanStages[6];
        }
    }
}

