package ch.heigvd;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HangmanServer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Server <max thread> <port>");
            System.exit(1);
        }

        int maxThread = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        if(!isValidPort(port)){
            System.out.println("Please provide a valid server port.");
            System.exit(1);
        }

        ExecutorService executor = null;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Hangman Server is listening on port " + port + " with a pool thread of " + maxThread);

            executor = Executors.newFixedThreadPool(maxThread);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(new ClientHandler(clientSocket));


                } catch (IOException e) {
                    System.err.println("Exception caught when trying to listen on port "
                            + port + " or listening for a connection");
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.err.println(e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    private static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private String wordToGuess;
        private final Set<Character> guessedLetters = new HashSet<>();
        private int attemptsLeft;
        private String visibleWord;

        private static int initID = 0;
        private int clientID = initID;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.wordToGuess = fetchRandomWord().toLowerCase();
            this.attemptsLeft = 7; //wordToGuess.length(); // Set attempts based on word length or a fixed value
            this.visibleWord = "_".repeat(wordToGuess.length());
        }

        private String fetchRandomWord() {
            // This method would actually fetch a random word from an API
            // Placeholder for demonstration
            List<String> words = Arrays.asList("example", "hangman", "protocol", "server", "client");
            return words.get(new Random().nextInt(words.size()));
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                initID++;
                System.out.println("New client " + clientID + " connected");

                out.println("INIT " + visibleWord + " " + attemptsLeft);

                String inputLine;
                while ((inputLine = in.readLine()) != null && attemptsLeft > 0 && !visibleWord.equalsIgnoreCase(wordToGuess)) {
                    if (inputLine.toUpperCase().startsWith("GUESS")) {
                        String guess = inputLine.substring(6).trim().toLowerCase();
                        processGuess(guess, out);
                    } else {
                        out.println("ERR 1 Invalid command");
                    }

                    if (attemptsLeft <= 0) {
                        out.println("GAME OVER LOSS " + wordToGuess);
                    }
                }

                System.out.println("Client " + clientID + " disconnected");

            } catch (IOException e) {
                System.err.println("Exception caught when trying to interact with client " + clientID);
                e.printStackTrace();
            } finally {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Exception caught when trying to close client " + clientID + " socket.");
                    e.printStackTrace();
                }
            }
        }

        private void processGuess(String guess, PrintWriter out) {
            if (guess.length() == 1) {
                char letter = guess.charAt(0);
                if (guessedLetters.contains(letter)) {
                    out.println("ERR 2 Already guessed");
                } else {
                    guessedLetters.add(letter);
                    if (wordToGuess.contains(guess)) {
                        updateVisibleWord(letter);
                        if(visibleWord.equalsIgnoreCase(wordToGuess)) {
                            out.println("GAME OVER WIN " + wordToGuess);
                        } else {
                            out.println("HIT " + visibleWord);
                        }
                    } else {
                        attemptsLeft--;
                        out.println("MISS " + attemptsLeft + " " + visibleWord);
                    }
                }
            } else if(guess.length() == 0 || guess.length() < visibleWord.length() || guess.length() > visibleWord.length()){
                out.println("ERR 3 Invalid guess length");
            } else if (guess.equalsIgnoreCase(wordToGuess)) {
                visibleWord = wordToGuess;
                out.println("GAME OVER WIN " + wordToGuess);
            } else {
                attemptsLeft--;
                out.println("MISS " + attemptsLeft + " " + visibleWord);
                if (attemptsLeft <= 0) {
                    out.println("GAME OVER LOSS " + wordToGuess);
                }
            }
        }

        private void updateVisibleWord(char letter) {
            StringBuilder newVisibleWord = new StringBuilder(visibleWord);
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (wordToGuess.charAt(i) == letter) {
                    newVisibleWord.setCharAt(i, letter);
                }
            }
            visibleWord = newVisibleWord.toString();
        }
    }
}
