package bikeshare;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/** Holds the bikes parked at one station. */
public final class Station {
    private final String name;
    private final Deque<Bike> bikes;

    public Station(String name, List<Bike> bikes) {
        this.name = name;
        this.bikes = new ArrayDeque<>(bikes);
    }

    public String name() {
        return name;
    }

    public Optional<Bike> takeBike() {
        return Optional.ofNullable(bikes.poll());
    }

    public void dock(Bike bike) {
        bikes.add(bike);
    }
}
