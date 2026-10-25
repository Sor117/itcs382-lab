package bikeshare;

/** Somewhere to keep finished rides. The rules depend only on this interface. */
public interface RideLog {
    void save(Ride ride);
}
