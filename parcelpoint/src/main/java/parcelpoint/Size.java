package parcelpoint;

public enum Size {
    SMALL(10.0),
    MEDIUM(15.0),
    LARGE(20.0);

    private final double dailyFee;

    Size(double dailyFee) {
        this.dailyFee = dailyFee;
    }

    public double getDailyFee() {
        return dailyFee;
    }

    public boolean fitsIn(Size lockerSize) {
        return compareTo(lockerSize) <= 0;
    }

    // The user types the size as 1, 2 or 3.
    public static Size fromNumber(int number) {
        if (number < 1 || number > values().length) {
            throw new IllegalArgumentException("Size must be 1 to 3, not " + number);
        }
        return values()[number - 1];
    }
}
