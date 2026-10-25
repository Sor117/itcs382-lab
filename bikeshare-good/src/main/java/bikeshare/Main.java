package bikeshare;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Creates the objects, connects them and starts the menu. */
public final class Main {
    public static void main(String[] args) {
        List<Station> stations = List.of(
                new Station("Library", List.of(new Bike("B1"), new Bike("B2"))),
                new Station("Canteen", List.of(new Bike("B3"), new Bike("B4"))),
                new Station("Dorm", List.of(new Bike("B5"), new Bike("B6"))));
        BikeShare bikeShare = new BikeShare(stations, new CsvRideLog(Path.of("rides.csv")));
        new ConsoleMenu(bikeShare, new Scanner(System.in), System.out).run();
    }
}
