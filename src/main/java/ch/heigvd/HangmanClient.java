package ch.heigvd;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HangmanClient {

    private static final String SERVER_IP = "127.0.0.1"; // or your server IP
    private static final int SERVER_PORT = 12345;
    private static final String[] HANGMAN_STAGES = {
            "+---+\n O  |\n/|\\ |\n/ \\ |\n   ===",  // right leg
            "+---+\n O  |\n/|\\ |\n/   |\n   ===", // left leg
            "+---+\n O  |\n/|\\ |\n    |\n   ===", // right arm
            "+---+\n O  |\n/|  |\n    |\n   ===", // left arm
            "+---+\n O  |\n |  |\n    |\n   ===", // body
            "+---+\n O  |\n    |\n    |\n   ===", // head
            "+---+\n    |\n    |\n    |\n   ===" // initial state
    };

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {

            System.out.println("Connected to Hangman Server");


            // Reading messages from server
            String fromServer = in.readLine();
            System.out.println("Server: " + fromServer);
            int availableGuess = fromServer.split(" ")[1].length();

            // Check if the connection is initialized
            if (fromServer.startsWith("INIT")) {
                while (true) {
                    System.out.print("Enter your guess (single letter or whole word): ");
                    String userInput = scanner.nextLine().trim().toLowerCase();

                    if (userInput.equals("quit")) {
                        break;
                    } else if (!userInput.isEmpty() && userInput.matches("[a-zA-Z]+")) {
                        // Send the guess to the server
                        out.println("GUESS " + userInput);
                    } else {
                        System.out.println("Invalid input. Please enter a letter or a word.");
                        continue;
                    }

                    // Read the server's response
                    fromServer = in.readLine();
                    System.out.println("Server: " + fromServer);

                    // Update hangman figure if the guess was wrong
                    if (fromServer.startsWith("MISS") && availableGuess > 0) {
                        availableGuess--;
                    }

                    // Display the current state of the hangman
                    System.out.println(HANGMAN_STAGES[Math.min(availableGuess, HANGMAN_STAGES.length - 1)]);

                    if(availableGuess == 0){
                        fromServer = in.readLine();
                    }

                    // Check for game over conditions
                    if (fromServer.startsWith("GAME OVER")) {
                        System.out.println("Server: " + fromServer);
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + SERVER_IP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    SERVER_IP);
            System.exit(1);
        }
    }
}
