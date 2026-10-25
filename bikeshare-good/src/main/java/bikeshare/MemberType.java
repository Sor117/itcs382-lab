package bikeshare;

/** The member types and the fee each one pays per minute. */
public enum MemberType {
    STUDENT(2),
    STAFF(3);

    private final int bahtPerMinute;

    MemberType(int bahtPerMinute) {
        this.bahtPerMinute = bahtPerMinute;
    }

    public long feeFor(int minutes) {
        return (long) bahtPerMinute * minutes;
    }

    public static MemberType parse(String text) {
        for (MemberType type : values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Member type must be student or staff.");
    }
}
