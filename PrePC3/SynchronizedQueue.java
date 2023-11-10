import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//====================================================================
public class SynchronizedQueue {
    //----------------------------------------------------------------
    public static void main(String[] args) {
        BlockingQueue<Integer> REQUEST  = new LinkedBlockingQueue<>();
        BlockingQueue<Result>  RESPONSE = new LinkedBlockingQueue<>();
        Operation OBJ = new Operation(REQUEST,RESPONSE);
        OBJ.start();
        try {
            REQUEST.put(123458);
            REQUEST.put(876542);
            REQUEST.put(324381);
            System.out.println(RESPONSE.take());
            System.out.println(RESPONSE.take());
            System.out.println(RESPONSE.take());
        }
        catch (InterruptedException ERROR) {
            ERROR.printStackTrace();
            System.out.println(ERROR);
        }
    }
}
//====================================================================
class Operation {
    private final BlockingQueue<Integer> IN;
    private final BlockingQueue<Result>  OUT;
    //----------------------------------------------------------------
    public Operation(BlockingQueue<Integer> REQUEST, BlockingQueue<Result> RESPONSE) {
        this.IN  = REQUEST;
        this.OUT = RESPONSE;
    }
    //----------------------------------------------------------------
    public int Proceso(int A) {
        System.out.println("Aqui se debe ejecutar el Proceso(s)");
        System.out.println(A);
        System.out.println("Aqui se debe ejecutar el Proceso(s)");
        return A*A;
    }
    //----------------------------------------------------------------
    public void start() {
    new Thread(new Runnable() {
        public void run() {
            int A,B;
            int counter = 0; // Añade un contador
            while (counter < 3) { // Cambia la condición de salida
                try {
                    A  = IN.take();
                    B = Proceso (A);
                    OUT.put(new Result(A,B));
                    counter++; // Incrementa el contador
                }
                catch (InterruptedException ERROR) {
                    ERROR.printStackTrace();
                }
            }
        }
        }).start();
    }
}

//====================================================================
class Result {
    private final int IN;
    private final int OUT;
    //----------------------------------------------------------------
    public Result(int A, int B) {
        this.IN  = A;
        this.OUT = B;
    }
    //----------------------------------------------------------------
    @Override public String toString() {
        return "\nProceso asociado a " + IN + ": " + OUT;
    }
}
//====================================================================