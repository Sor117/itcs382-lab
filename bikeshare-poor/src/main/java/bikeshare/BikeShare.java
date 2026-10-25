package bikeshare;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class BikeShare {
    public static final int STUDENT = 1;
    public static final int STAFF = 2;
    public static final String RIDES_FILE = "rides.csv";
    public static final String[] STATION_NAMES = {"Library", "Canteen", "Dorm"};
    public static final String[] BIKE_IDS = {"B1", "B2", "B3", "B4", "B5", "B6"};

    public static String[] bikeStations = {"Library", "Library", "Canteen", "Canteen", "Dorm", "Dorm"};
    public static String[] riders = new String[BIKE_IDS.length];
    public static int[] riderTypes = new int[BIKE_IDS.length];
    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            System.out.println("BikeShare commands: rent, return, quit");
            System.out.print("> ");
            while (input.hasNextLine()) {
                switch (input.nextLine().trim().toLowerCase()) {
                    case "quit" -> {
                        return;
                    }
                    case "rent" -> useBike(true);
                    case "return" -> useBike(false);
                    default -> System.out.println("Unknown command. Use rent, return or quit.");
                }
                System.out.print("> ");
            }
        } finally {
            input.close();
        }
    }

    public static void useBike(boolean renting) {
        if (renting) {
            String member = ask("Member ID: ");
            String typeText = ask("Member type (student/staff): ");
            String station = findStation(ask("Station (Library/Canteen/Dorm): "));
            int type = typeText.equalsIgnoreCase("student") ? STUDENT
                    : typeText.equalsIgnoreCase("staff") ? STAFF : 0;
            if (member.isEmpty() || member.contains(",") || type == 0 || station == null) {
                System.out.println("Rent cancelled: enter a member ID without commas, student or staff, and a known station.");
                return;
            }
            for (int i = 0; i < BIKE_IDS.length; i++) {
                if (riders[i] == null && bikeStations[i].equals(station)) {
                    riders[i] = member;
                    riderTypes[i] = type;
                    System.out.println("Member " + member + " rented " + BIKE_IDS[i] + " at " + station + ".");
                    return;
                }
            }
            System.out.println("No bike is available at " + station + ".");
        } else {
            int bike = Arrays.asList(BIKE_IDS).indexOf(ask("Bike ID: ").toUpperCase());
            if (bike < 0 || riders[bike] == null) {
                System.out.println("Return cancelled: that bike is unknown or not rented.");
                return;
            }
            String station = findStation(ask("Return station (Library/Canteen/Dorm): "));
            int minutes;
            try {
                minutes = Integer.parseInt(ask("Ride time in minutes: "));
            } catch (NumberFormatException e) {
                minutes = 0;
            }
            if (station == null || minutes <= 0) {
                System.out.println("Return cancelled: enter a known station and a whole number of minutes above 0.");
                return;
            }
            long fee = fee(riderTypes[bike], minutes);
            System.out.println("----- Receipt -----");
            System.out.println("Bike:    " + BIKE_IDS[bike]);
            System.out.println("Minutes: " + minutes);
            System.out.println("Fee:     " + fee + " baht");
            saveRide(String.join(",", BIKE_IDS[bike], riders[bike],
                    riderTypes[bike] == STUDENT ? "student" : "staff",
                    bikeStations[bike], station, String.valueOf(minutes), String.valueOf(fee)));
            bikeStations[bike] = station;
            riders[bike] = null;
        }
    }

    public static long fee(int memberType, int minutes) {
        return memberType == STUDENT ? minutes * 2L : minutes * 3L;
    }

    public static String findStation(String name) {
        for (String station : STATION_NAMES) {
            if (station.equalsIgnoreCase(name)) {
                return station;
            }
        }
        return null;
    }

    public static String ask(String prompt) {
        System.out.print(prompt);
        return input.hasNextLine() ? input.nextLine().trim() : "";
    }

    public static void saveRide(String line) {
        boolean newFile = !new File(RIDES_FILE).exists();
        try (FileWriter out = new FileWriter(RIDES_FILE, StandardCharsets.UTF_8, true)) {
            if (newFile) {
                out.write("bike,member,type,from,to,minutes,fee" + System.lineSeparator());
            }
            out.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not save the ride to " + RIDES_FILE + ": " + e.getMessage());
        }
    }
}
