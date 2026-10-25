package parcelpoint;

public class Locker {
    private int number;
    private Size size;
    private Parcel parcel;

    public Locker(int number, Size size) {
        this.number = number;
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public Size getSize() {
        return size;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public boolean isFree() {
        return parcel == null;
    }
}
