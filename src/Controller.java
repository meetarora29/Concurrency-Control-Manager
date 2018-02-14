import java.util.HashMap;

public class Controller {
    static int mode=-1;

    static void setMode(int mode) {
        Controller.mode=mode;
    }

    synchronized boolean lock_s(Flight f) {
        if(f.lock!=1) {
            f.lock=0;
            f.s_locks++;
            return true;
        }
        return false;
    }

    synchronized boolean lock_x(Flight f, int id) {
        if (f.lock==-1 || (f.x_lock==id && f.lock==1)) {
            f.lock=1;
            f.x_lock=id;
            return true;
        }
        return false;
    }

    synchronized boolean upgrade(Flight f, int id) {
        if(f.s_locks == 1) {
            f.s_locks--;
            f.lock=-1;
            lock_x(f, id);
            return true;
        }
        return false;
    }

    synchronized void release_lock(Flight f) {
        if(f.lock==0) {
            f.s_locks--;
            if (f.s_locks<=0) {
                f.s_locks=0;
                f.lock = -1;
            }
        }
        else if(f.lock==1) {
            f.x_lock=-1;
            f.lock=-1;
        }
//        System.out.println(f.s_locks + " " + f.x_lock + " " + f.lock + " " + f.id);
    }
}
