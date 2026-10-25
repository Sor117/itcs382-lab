package bikeshare;

/** A bike that a member has out right now. Only BikeShare uses it. */
record Rental(String memberId, MemberType memberType, Bike bike) {
    Ride finish(int minutes) {
        return new Ride(memberId, memberType, bike, minutes);
    }
}
