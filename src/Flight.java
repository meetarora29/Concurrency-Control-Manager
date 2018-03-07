import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Flight implements Serializable{
    String dest, source;
    int num_passengers, id, total_passengers;
    volatile int lock, s_locks, x_lock;
    ArrayList<Integer> passengers;

    static final long serialVersionUID = 1L;

    Flight(String dest, String source, int id, int total) {
        this.dest=dest;
        this.source=source;
        this.id=id;
        this.num_passengers=0;
        this.total_passengers=total;
        passengers=new ArrayList<>(total);
        lock=-1;
        s_locks=0;
        x_lock=-1;
    }

    @Override
    public String toString() {
        return dest + source + num_passengers + "/" + total_passengers;
    }

    boolean addPassenger(int pass_id) {
        if(num_passengers>=total_passengers)
            return false;
        passengers.add(pass_id);
        num_passengers++;
        return true;
    }

    void removePassenger(int pass_id) {
        int i=passengers.indexOf(pass_id);
        passengers.remove(i);
    }

    public static void serialize(ArrayList<Flight> flights) throws IOException {
        OutputStream outputStream=new FileOutputStream("flights.dat");
        ObjectOutputStream out=null;

        try {
            out=new ObjectOutputStream(outputStream);
            for(int i=0;i<5;i++) {
                out.writeObject(flights.get(i));
            }
        }
        finally {
            if(out!=null)
                out.close();
        }
    }

    public static void deserialize(ArrayList<Flight> flights) throws IOException, ClassNotFoundException {
        InputStream inputStream=new FileInputStream("flights.dat");
        ObjectInputStream in=null;
        try {
            in=new ObjectInputStream(inputStream);
            try {
                while (true) {
                    Flight temp=(Flight)in.readObject();
                    flights.add(temp);
                }
            }
            catch (EOFException e) {

            }
        }
        finally {
            if(in!=null)
                in.close();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner Reader=new Scanner(System.in);
        ArrayList<Flight> flights=new ArrayList<>(5);

//        for(int i=0;i<5;i++) {
//            flights.add(new Flight(Reader.next(), Reader.next(), i, Reader.nextInt()));
//        }
//        serialize(flights);

        deserialize(flights);
        for (Flight flight:flights)
            System.out.println(flight);
    }
}
