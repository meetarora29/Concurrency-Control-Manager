import java.util.ArrayList;

public class Passenger {
    int id;
    ArrayList<Flight> myFlights;

    Passenger(int id) {
        this.id=id;
        myFlights=new ArrayList<>();
    }

    ArrayList<Flight> getMyFlights() {
        return myFlights;
    }
}
