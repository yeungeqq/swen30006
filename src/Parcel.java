public class Parcel extends MailItem{

    private final float weight;

    Parcel(int floor, int room, int arrival, float weight) {
        super(floor, room, arrival);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return super.toString() + ", Weight: " + weight + "kg";
    }

    float myWeight() {return weight;}
    
}
