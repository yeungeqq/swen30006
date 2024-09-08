public class MailItem implements Comparable<MailItem> {

    // Fields for storing the floor, room, and arrival time of the mail item
    private final int floor;
    private final int room;
    private final int arrival;

    MailItem(int floor, int room, int arrival) {
        this.floor = floor;
        this.room = room;
        this.arrival = arrival;
    }

    // Method to compare two MailItem objects, primarily by floor, then by room if floors are the same
    @Override
    public int compareTo(MailItem i) {
        int floorDiff = this.floor - i.floor;  // Compare floors
        return (floorDiff == 0) ? this.room - i.room : floorDiff;  // If floors are the same, compare rooms
    }

    // Method to return a string representation of the MailItem, including floor, room, and arrival time
    public String toString() {
        return "Floor: " + floor + ", Room: " + room + ", Arrival: " + arrival;
    }

    // Getter method to return the floor of the mail item
    int myFloor() { return floor; }

    // Getter method to return the room of the mail item
    int myRoom() { return room; }

    // Getter method to return the arrival time of the mail item
    int myArrival() { return arrival; }

}
