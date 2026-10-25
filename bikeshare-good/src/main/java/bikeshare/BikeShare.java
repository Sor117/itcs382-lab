package bikeshare;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Applies the rental rules: who has which bike, renting, and returning. */
public final class BikeShare {
    private final Map<String, Station> stations = new HashMap<>();
    private final Map<String, Rental> rentals = new HashMap<>();
    private final RideLog rideLog;

    public BikeShare(List<Station> stations, RideLog rideLog) {
        for (Station station : stations) {
            this.stations.put(key(station.name()), station);
        }
        this.rideLog = rideLog;
    }

    public Bike rent(String memberId, MemberType memberType, String stationName) {
        if (rentals.containsKey(memberId)) {
            throw new IllegalStateException("Member " + memberId + " already has a bike.");
        }
        Station station = find(stationName);
        Bike bike = station.takeBike()
                .orElseThrow(() -> new IllegalStateException("No bike is available at " + station.name() + "."));
        rentals.put(memberId, new Rental(memberId, memberType, bike));
        return bike;
    }

    public Ride returnBike(String memberId, String stationName, int minutes) {
        Station station = find(stationName);
        Rental rental = rentals.get(memberId);
        if (rental == null) {
            throw new IllegalStateException("Member " + memberId + " has no bike to return.");
        }
        Ride ride = rental.finish(minutes);
        rideLog.save(ride);
        rentals.remove(memberId);
        station.dock(ride.bike());
        return ride;
    }

    private Station find(String stationName) {
        Station station = stations.get(key(stationName));
        if (station == null) {
            throw new IllegalArgumentException("There is no station called " + stationName + ".");
        }
        return station;
    }

    private static String key(String stationName) {
        return stationName.toLowerCase(Locale.ROOT);
    }
}
