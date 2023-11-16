import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LU {
    private static final int n = (int)Math.pow(2, 11);
    private static Double[][] A = new Double[n][n];
    private static Double[][] L = new Double[n][n];
    private static Double[][] U = new Double[n][n];
    private static Double[][] Ls = new Double[n][n];
    private static Double[][] Us = new Double[n][n];
    private static int numThreads;


    public static void LUDecomposition() {
        for (int i = 0; i < n; i++) {
            for (int k = i; k < n; k++) {
                Double sum = 0.0;
                for (int j = 0; j < i; j++)
                    sum += (Ls[i][j] * Us[j][k]);
                Us[i][k] = A[i][k] - sum;
            }

            for (int k = i; k < n; k++) {
                if (i == k)
                    Ls[i][i] = 1.0;
                else {
                    Double sum = 0.0;
                    for (int j = 0; j < i; j++)
                        sum += (Ls[k][j] * Us[j][i]);
                    Ls[k][i] = (A[k][i] - sum) / Us[i][i];
                }
            }
        }
    }


    public static void main(String[] args) {
        numThreads = Runtime.getRuntime().availableProcessors();
        MatrixHandling.generateMatrixToTxt(n, n, "A");
        A = MatrixHandling.readTxtToMatrix("A.txt", ",");

        // Secuencial
        long startTime = System.currentTimeMillis();
        LUDecomposition();
        long endTime = System.currentTimeMillis();
        // Guardar matrices L y U en archivos de texto
        MatrixHandling.matrixToTxt(Ls, "Ls");
        MatrixHandling.matrixToTxt(Us, "Us");

        System.out.println("Secuencial: " + (endTime - startTime)/1000 + " s");

        // Paralelo
        BlockingQueue<Request> REQUEST = new LinkedBlockingQueue<>();
        BlockingQueue<ResponseLU> RESPONSE = new LinkedBlockingQueue<>();
        OperationLU OBJ = new OperationLU(REQUEST, RESPONSE);
        OBJ.start();
        long startTime2 = 0, endTime2 = 0;
        try {
            startTime2 = System.currentTimeMillis();
            for(int i = 0; i < numThreads; i++) {
                int start = i * (n / numThreads);
                int end = (i + 1) * (n / numThreads);
                if (i == numThreads - 1) {
                    end = n;
                }
                REQUEST.put(new Request(A, L, U, start, end));
            }

            for (int i = 0; i < numThreads; i++) {
                ResponseLU response = RESPONSE.take();
                for (int j = response.start; j < response.end; j++) {
                    for (int k = 0; k < n; k++) {
                        L[j][k] = response.L[j][k];
                        U[j][k] = response.U[j][k];
                    }
                }
            }

            endTime2 = System.currentTimeMillis();
        } catch (Exception ERROR) {
            ERROR.printStackTrace();
            System.out.println(ERROR);
        }

        // Asegúrate de que el hilo de operación haya terminado antes de continuar
        OBJ.stopThread();
        
        // Guardar matrices L y U en archivos de texto
        MatrixHandling.matrixToTxt(L, "L");
        MatrixHandling.matrixToTxt(U, "U");

        System.out.println("Paralelo: " + (endTime2 - startTime2)/1000 + " s");
    }
}

class Request {
    public Double[][] A;
    public Double[][] L;
    public Double[][] U;
    public int start;
    public int end;

    public Request(Double[][] A, Double[][] L, Double[][] U, int start, int end) {
        this.A = A;
        this.L = L;
        this.U = U;
        this.start = start;
        this.end = end;
    }
}

class ResponseLU {
    public Double[][] L;
    public Double[][] U;
    public int start;
    public int end;

    public ResponseLU(Double[][] L, Double[][] U, int start, int end) {
        this.L = L;
        this.U = U;
        this.start = start;
        this.end = end;
    }
}

class OperationLU {
    private final BlockingQueue<Request> IN;
    private final BlockingQueue<ResponseLU> OUT;
    private volatile boolean stopThread = false;

    public OperationLU(BlockingQueue<Request> REQUEST, BlockingQueue<ResponseLU> RESPONSE) {
        this.IN = REQUEST;
        this.OUT = RESPONSE;
    }

    public ResponseLU LUDecomposition(Double[][] A, Double[][] L, Double[][] U, int start, int end) {
        for (int i = start; i < end; i++) {
            for (int k = i; k < A.length; k++) {
                double sumU = 0.0;
                for (int j = 0; j < i; j++) {
                    sumU += L[i][j] * U[j][k];
                }
                U[i][k] = A[i][k] - sumU;

                if (i != k) {
                    double sumL = 0.0;
                    for (int j = 0; j < i; j++) {
                        sumL += L[k][j] * U[j][i];
                    }
                    L[k][i] = (A[k][i] - sumL) / U[i][i];
                } else {
                    L[i][i] = 1.0;
                }
            }
        }

        return new ResponseLU(L, U, start, end);
    }

    public void start() {
        new Thread(() -> {
            while (!stopThread) {
                try {
                    Request request = IN.take();
                    ResponseLU response = LUDecomposition(request.A, request.L, request.U, request.start, request.end);
                    OUT.put(response);
                } catch (InterruptedException ERROR) {
                    ERROR.printStackTrace();
                }
            }
        }).start();
    }

    public void stopThread() {
        stopThread = true;
    }
}
