package ch.heigvd;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.HelpCommand;

import java.util.concurrent.Callable;

@Command(name = "hangman game", mixinStandardHelpOptions = true, version = "1.0",
        description = "Play the hangman game",
        subcommands = {HelpCommand.class})
public class Hangman implements Callable<Void> {

    @Option(names = {"-s", "--server"}, description = "Run as TCP server")
    private boolean serverMode;

    @Option(names = {"-c", "--client"}, description = "Run as TCP client")
    private boolean clientMode;

    @Option(names = {"-p", "--port"}, description = "Port number (default : 9795)")
    private int port = 9795;

    @Option(names = {"-i", "--ip"}, description = "Server IP address (default : localhost)")
    private String serverIp = "localhost";


    public static void main(String[] args) {
        int exitCode = new CommandLine(new Hangman()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Void call() throws Exception {
        if (serverMode) {
            startServer();
        } else if (clientMode) {
            startClient();
        } else {
            System.out.println("Please specify either --server or --client option.");
        }
        return null;
    }

    private void startServer() {
        System.out.println("Starting TCP server on port " + port);
        // Create an instance of Server and invoke its main method
        HangmanServer.main(new String[]{String.valueOf(port)});
    }

    private void startClient() {
        System.out.println("Starting TCP client and connecting to " + serverIp + ":" + port);
        // Create an instance of Client and invoke its main method
        HangmanClient.main(new String[]{serverIp, String.valueOf(port)});
    }
}

