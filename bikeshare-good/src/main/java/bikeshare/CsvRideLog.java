package bikeshare;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Adds each finished ride as one line at the end of a CSV file. */
public final class CsvRideLog implements RideLog {
    private static final String HEADER = "member_id,member_type,bike,minutes,fee_baht";

    private final Path file;

    public CsvRideLog(Path file) {
        this.file = file;
    }

    @Override
    public void save(Ride ride) {
        String header = Files.exists(file) ? "" : HEADER + System.lineSeparator();
        String row = String.join(",", ride.memberId(), ride.memberType().name(), ride.bike().id(),
                String.valueOf(ride.minutes()), String.valueOf(ride.fee()));
        try {
            Files.writeString(file, header + row + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save the ride to " + file + ".", e);
        }
    }
}
