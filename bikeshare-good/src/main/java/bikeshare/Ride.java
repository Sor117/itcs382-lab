package bikeshare;

/** A finished ride and the fee for it. */
public record Ride(String memberId, MemberType memberType, Bike bike, int minutes) {
    public Ride {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Ride time must be at least 1 minute.");
        }
    }

    public long fee() {
        return memberType.feeFor(minutes);
    }
}
