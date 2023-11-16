import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LUSyncQFun {
    public static void main(String[] args) {
        CreacionMatrices.Matrix(100, 100, "X.txt");
        int[][] x = readtxtToMatrix("X.txt", ",");
    
        int numParts = 4;
        int partSize = x.length / numParts;
    
        Operation operation = new Operation(numParts);
    
        long startTime = System.nanoTime();
    
        List<Future<Pair>> futures = new ArrayList<>();
        for (int i = 0; i < numParts; i++) {
            int start = i * partSize;
            int end = (i + 1) * partSize;
            int[][] subMatrix = Arrays.copyOfRange(x, start, end);
            futures.add(operation.luDecomposition(subMatrix));
        }
    
        double[][] X_L_P = new double[x.length][x.length];
        double[][] X_U_P = new double[x.length][x.length];
    
        for (int i = 0; i < numParts; i++) {
            try {
                Pair result = futures.get(i).get();
                int start = i * partSize;
                int end = (i + 1) * partSize;
                for (int j = start; j < end; j++) {
                    for (int k = 0; k < x.length; k++) {
                        X_L_P[j][k] = result.getLower()[j - start][k];
                        X_U_P[j][k] = result.getUpper()[j - start][k];
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    
        long endTime = System.nanoTime();
        double seconds = (double) (endTime - startTime) / 1_000_000_000.0;
    
        System.out.println("Execution time in seconds: " + String.format("%.8f", seconds));
    
        MatrixTotxt("X_L_P.txt", X_L_P);
        MatrixTotxt("X_U_P.txt", X_U_P);
    
        // Shutdown the executor service
        operation.shutdown();
    }    

    public static int[][] readtxtToMatrix(String txtFile, String txtSplitBy){
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                String[] columns = line.split(txtSplitBy);
                rows.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = rows.get(0).length;
        int[][] matrix = new int[rows.size()][n];

        // Extract matrix data from txt
        for(int i = 0; i < rows.size(); i++){
            for(int j = 0; j < n; j++){
                matrix[i][j] = Integer.parseInt(rows.get(i)[j]);
            }
        }

        return matrix;
    }

    public static void MatrixTotxt(String fileName, double [][] matrix){
        StringBuilder txtContent = new StringBuilder();
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){ 
                if(j == matrix[i].length - 1){
                    txtContent.append(String.format("%.4f", matrix[i][j])).append("\n");
                }
                else{
                    txtContent.append(String.format("%.4f", matrix[i][j])).append(",");
                }
            }
        }
        totxt(txtContent.toString(), fileName);
    }

    public static void totxt(String txtContent, String fileName){
        try(FileWriter fw = new FileWriter(fileName)){
            fw.write(txtContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

class Pair{
    double[][] lower;
    double[][] upper;

    public Pair(double[][] lower, double[][] upper){
        this.lower = lower;
        this.upper = upper;
    }

    public double[][] getLower(){return lower;}

    public double[][] getUpper(){return upper;}
}

class Operation {
    private final ExecutorService executor;

    public Operation(int numThreads) {
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    public Future<Pair> luDecomposition(int[][] matrix) {
        return executor.submit(() -> {
            int n = matrix.length;
            double[][] lower = new double[n][n];
            double[][] upper = new double[n][n];

            for (int i = 0; i < n; i++) {
                // Upper Triangular
                for (int k = i; k < n; k++) {
                    double sum = 0;
                    for (int j = 0; j < i; j++)
                        sum += (lower[i][j] * upper[j][k]);
                    upper[i][k] = matrix[i][k] - sum;
                }

                // Lower Triangular
                for (int k = i; k < n; k++) {
                    if (i == k)
                        lower[i][i] = 1;
                    else {
                        double sum = 0;
                        for (int j = 0; j < i; j++)
                            sum += (lower[k][j] * upper[j][i]);
                        lower[k][i] = (matrix[k][i] - sum) / upper[i][i];
                    }
                }
            }

            return new Pair(lower, upper);
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
