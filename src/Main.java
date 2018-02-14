import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TransactionThread implements Runnable {
    static Random random;
    int index;

    TransactionThread(int i) {
        index=i;
    }

    @Override
    public void run() {
        int flight_id=random.nextInt(5);
        Flight f=Transaction.flights.get(flight_id);
        int pass_id=random.nextInt(f.total_passengers);
        Transaction transaction=new Transaction(index);
        int t=random.nextInt(5);
//        if (index%1==0)
//        t=4;
        if (t==0)
            transaction.reserve(flight_id, pass_id);
        else if(t==1)
            transaction.cancel(flight_id, pass_id);
        else if (t==2) {
            pass_id=random.nextInt(100);
            transaction.my_flights(pass_id);
        } else if(t==3)
            transaction.total_reservations();
        else if(t==4) {
            int flight_id2=random.nextInt(5);
            while (flight_id2==flight_id)
                flight_id2=random.nextInt(5);
            Flight f2=Transaction.flights.get(flight_id2);
            pass_id=random.nextInt(Math.max(f.total_passengers, f2.total_passengers));
            transaction.transfer(flight_id, flight_id2, pass_id);
        }
//        System.out.println(index);
    }
}

public class Main {

    public static void main(String[] args) {
        int mode=2;
        Controller.setMode(mode);
        try {
            Transaction.deserialize();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        TransactionThread.random=new Random();

        int num_transactions=50;
        int num_threads=20;
        if (mode==1)
            num_threads=1;

        ExecutorService pool= Executors.newFixedThreadPool(num_threads);

        long start=System.currentTimeMillis();
        for(int i=0;i<num_transactions;i++) {
            TransactionThread tt=new TransactionThread(i);
            pool.execute(tt);
        }
        pool.shutdown();
        long end=System.currentTimeMillis();
        while (!pool.isTerminated())
            end=System.currentTimeMillis();
        System.out.println("Time: "+ (end-start));
//        Transaction.serialize();
    }
}
