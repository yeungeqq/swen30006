public class MailItem implements Comparable<MailItem> {
    private final int floor;
    private final int room;
    private final int arrival;


    @Override
    public int compareTo(MailItem i) {
        int floorDiff = this.floor - i.floor;
        return (floorDiff == 0) ? this.room - i.room : floorDiff;
    }

    MailItem(int floor, int room, int arrival) {
        this.floor = floor;
        this.room = room;
        this.arrival = arrival;
    }

    public String toString() {
        return "Floor: " + floor + ", Room: " + room + ", Arrival: " + arrival;
    }

    int myFloor() { return floor; }
    int myRoom() { return room; }
    int myArrival() { return arrival; }

}