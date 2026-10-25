package bikeshare;

import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Scanner;

/** Reads commands from the user and prints the results. Holds no business rules. */
public final class ConsoleMenu {
    private static final String HELP = """
            Commands:
              rent <member-id> <student|staff> <station>
              return <member-id> <station> <minutes>
              quit""";

    private final BikeShare bikeShare;
    private final Scanner input;
    private final PrintStream output;

    public ConsoleMenu(BikeShare bikeShare, Scanner input, PrintStream output) {
        this.bikeShare = bikeShare;
        this.input = input;
        this.output = output;
    }

    public void run() {
        output.println(HELP);
        while (true) {
            output.print("> ");
            if (!input.hasNextLine()) {
                return;
            }
            String line = input.nextLine().trim();
            if (line.equalsIgnoreCase("quit")) {
                return;
            }
            if (!line.isEmpty()) {
                execute(line.split("\\s+"));
            }
        }
    }

    private void execute(String[] words) {
        try {
            if (words.length == 4 && words[0].equalsIgnoreCase("rent")) {
                Bike bike = bikeShare.rent(words[1], MemberType.parse(words[2]), words[3]);
                output.println("Member " + words[1] + " rented bike " + bike.id() + ".");
            } else if (words.length == 4 && words[0].equalsIgnoreCase("return")) {
                printReceipt(bikeShare.returnBike(words[1], words[2], parseMinutes(words[3])));
            } else {
                output.println(HELP);
            }
        } catch (IllegalArgumentException | IllegalStateException | UncheckedIOException e) {
            output.println("Error: " + e.getMessage());
        }
    }

    private void printReceipt(Ride ride) {
        output.println("----- Receipt -----");
        output.println("Bike:    " + ride.bike().id());
        output.println("Minutes: " + ride.minutes());
        output.println("Fee:     " + ride.fee() + " baht");
        output.println("-------------------");
    }

    private static int parseMinutes(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Minutes must be a whole number.");
        }
    }
}
