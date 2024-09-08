public class Parcel extends MailItem {

    // The weight of the parcel, which is a final value
    private final int weight;

    // Constructor to initialize a Parcel object
    Parcel(int floor, int room, int arrival, int weight) {
        // Call the constructor of the parent class (MailItem)
        super(floor, room, arrival);
        // Set the weight of the parcel
        this.weight = weight;
    }

    // Override the toString method to include the weight of the parcel in the output
    @Override
    public String toString() {
        // Call the parent class's toString method and append the parcel's weight
        return super.toString() + ", Weight: " + weight;
    }

    // Method to get the weight of the parcel
    int myWeight() {
        return weight;
    }
}
