import java.io.*;
import java.util.ArrayList;

public class Transaction {
    static ArrayList<Flight> flights;
    static Controller controller;
    static int sleep=1000;
    int id;

    Transaction(int id) {
        this.id=id;
    }

    public void reserve(int flight_id, int pass_id) {
        while (true) {
            if(controller.lock_x(flights.get(flight_id), id))
                break;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!flights.get(flight_id).passengers.contains(pass_id)) {
            if(flights.get(flight_id).addPassenger(pass_id))
                System.out.printf("Passenger %d added to Flight %d\n", pass_id, flight_id);
            else
                System.out.printf("Passenger %d could not be added to Flight %d\n", pass_id, flight_id);
        }
        else
            System.out.printf("Passenger %d already present on Flight %d\n", pass_id, flight_id);
        controller.release_lock(flights.get(flight_id));
    }

    public void cancel(int flight_id, int pass_id) {
        while (true) {
            if(controller.lock_x(flights.get(flight_id), id))
                break;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(flights.get(flight_id).passengers.contains(pass_id)) {
            flights.get(flight_id).removePassenger(pass_id);
            System.out.printf("Passenger %d removed from Flight %d\n", pass_id, flight_id);
        }
        else
            System.out.printf("Passenger %d not present on Flight %d\n", pass_id, flight_id);
        controller.release_lock(flights.get(flight_id));
    }

    public void my_flights(int pass_id) {
//        System.out.printf("My Flights: ");
        for (Flight flight:flights) {
            while (true) {
                if(controller.lock_s(flight))
                    break;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (flight.passengers.contains(pass_id))
                System.out.printf("%d ",flight.id);
            if (Controller.mode==1)
                controller.release_lock(flight);
        }
        if (Controller.mode==2) {
            for (Flight flight:flights) {
                controller.release_lock(flight);
            }
        }
    }

    public void total_reservations() {
        int sum=0;
        for (Flight flight:flights) {
            while (true) {
                if(controller.lock_s(flight))
                    break;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sum+=flight.num_passengers;
            if (Controller.mode==1)
                controller.release_lock(flight);
        }
        if (Controller.mode==2) {
            for (Flight flight:flights) {
                controller.release_lock(flight);
            }
        }
        System.out.println("Total Reservations " + sum);
    }

    public void transfer(int f1, int f2, int pass_id) {
        Flight flight1=flights.get(f1);
        Flight flight2=flights.get(f2);

//        for (Flight flight:flights)
//            System.out.println(flight.s_locks + " " + flight.lock + " " + flight.x_lock);
        while(true) {
            if(controller.lock_s(flight2))
                break;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (flight2.num_passengers==flight2.total_passengers) {
            controller.release_lock(flight2);
            System.out.printf("There is no space on flight %d\n", flight2.id);
            return;
        }
        while (true) {
            if(controller.lock_s(flight1))
                break;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!flight1.passengers.contains(pass_id)) {
            controller.release_lock(flight1);
            controller.release_lock(flight2);
            System.out.printf("There is no passenger %d on flight %d\n", pass_id, flight1.id);
            return;
        }
        while (true) {
            if (controller.upgrade(flight1, id))
                break;
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flight1.removePassenger(pass_id);
        if (Controller.mode==1)
            controller.release_lock(flight1);
        while (true) {
            if (controller.upgrade(flight2, id))
                break;
        }
        flight2.addPassenger(pass_id);
        System.out.printf("Passenger %d transferred to Flight %d\n", pass_id, flight2.id);
        controller.release_lock(flight2);
        if (Controller.mode==2)
            controller.release_lock(flight1);
    }

    public static void deserialize() throws IOException, ClassNotFoundException {
        controller=new Controller();
        flights=new ArrayList<>();
        InputStream inputStream=new FileInputStream("flights.dat");
        ObjectInputStream in=null;
        Flight.deserialize(flights);
    }

    public static void serialize() {
        try {
            Flight.serialize(flights);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
