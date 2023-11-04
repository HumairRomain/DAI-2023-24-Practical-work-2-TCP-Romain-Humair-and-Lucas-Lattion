package ch.heigvd;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        long start = System.nanoTime();
        int exitCode = new CommandLine(new Hangman()).execute(args);
        long end = System.nanoTime();
        if(exitCode == 0){
            System.out.println("Success Execution ! Elapsed time: " + (end - start) / 1000000 + "ms / " + (end - start) + "ns");
        }
        System.exit(exitCode);
    }
}