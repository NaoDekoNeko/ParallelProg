import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Operation {
    private final BlockingQueue<MatrixData> IN;
    private final BlockingQueue<Result> OUT;

    public Operation(BlockingQueue<MatrixData> REQUEST, BlockingQueue<Result> RESPONSE) {
        this.IN = REQUEST;
        this.OUT = RESPONSE;
    }

    public Result Process(MatrixData data) {
        computeUpper(data.matrix, data.upper, data.lower, data.index);
        computeLower(data.matrix, data.lower, data.upper, data.index);
        return new Result(data.lower, data.upper);
    }

    private void computeUpper(int[][] matrix, double[][] upper, double[][] lower, int i) {
        int n = matrix.length;

        for (int k = i; k < n; k++) {
            int sum = 0;
            for (int j = 0; j < i; j++)
                sum += (lower[i][j] * upper[j][k]);
            upper[i][k] = matrix[i][k] - sum;
        }
    }

    private void computeLower(int[][] matrix, double[][] lower, double[][] upper, int i) {
        int n = matrix.length;

        for (int k = i; k < n; k++) {
            if (i == k)
                lower[i][i] = 1;
            else {
                int sum = 0;
                for (int j = 0; j < i; j++)
                    sum += (lower[k][j] * upper[j][i]);
                lower[k][i] = (matrix[k][i] - sum) / upper[i][i];
            }
        }
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    MatrixData data = IN.take();
                    Result result = Process(data);
                    OUT.put(result);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class MatrixData {
    int[][] matrix;
    double[][] lower;
    double[][] upper;
    int index;

    public MatrixData(int[][] matrix, double[][] lower, double[][] upper, int index) {
        this.matrix = matrix;
        this.lower = lower;
        this.upper = upper;
        this.index = index;
    }
}

class Result {
    double[][] lower;
    double[][] upper;

    public Result(double[][] lower, double[][] upper) {
        this.lower = lower;
        this.upper = upper;
    }
}

public class Prueba{
    public static void main(String[] args) {
        BlockingQueue<MatrixData> REQUEST = new LinkedBlockingQueue<>();
        BlockingQueue<Result> RESPONSE = new LinkedBlockingQueue<>();
        Operation OBJ = new Operation(REQUEST, RESPONSE);
        OBJ.start();
        try {
            int[][] matrix = { { 2, 1, -1 }, { -3, -1, 2 }, { -2, 1, 2 } };
            double[][] lower = new double[matrix.length][matrix.length];
            double[][] upper = new double[matrix.length][matrix.length];
            REQUEST.put(new MatrixData(matrix, lower, upper, 0));
            System.out.println(RESPONSE.take());
        } catch (InterruptedException ERROR) {
            ERROR.printStackTrace();
            System.out.println(ERROR);
        }
    }
}