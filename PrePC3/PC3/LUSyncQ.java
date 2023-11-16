import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LUSyncQ {
    // Método para leer datos desde un archivo de texto y convertirlos en una matriz
    public static double[][] readtxtToMatrix(String txtFile, String txtSplitBy) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Utilizar coma como separador
                String[] columns = line.split(txtSplitBy);
                rows.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = rows.get(0).length;
        double[][] matrix = new double[rows.size()][n];

        // Extraer datos de la matriz desde el archivo de texto
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(rows.get(i)[j]);
            }
        }

        return matrix;
    }

    // Método para mostrar una matriz en la consola
    public static void shwMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Método para escribir una matriz en un archivo de texto
    public static void MatrixTotxt(String fileName, double[][] matrix) {
        StringBuilder txtContent = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == matrix[i].length - 1) {
                    txtContent.append(String.format("%.4f", matrix[i][j])).append("\n");
                } else {
                    txtContent.append(String.format("%.4f", matrix[i][j])).append(",");
                }
            }
        }
        totxt(txtContent.toString(), fileName);
    }

    // Método para escribir contenido de texto en un archivo
    public static void totxt(String txtContent, String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(txtContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para realizar la descomposición LU de una matriz
    public static Pair luDecomposition(double[][] matrix) {
        int n = matrix.length;

        double[][] lower = new double[n][n];
        double[][] upper = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int k = i; k < n; k++) {
                double sum = 0;
                for (int j = 0; j < i; j++)
                    sum += (lower[i][j] * upper[j][k]);
                upper[i][k] = matrix[i][k] - sum;
            }

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
    }

    public static void main(String[] args) {
        // Generar matrices y leer desde archivos
        CreacionMatrices.Matrix(100, 100, "X1.txt");
        CreacionMatrices.Matrix(100, 100, "X2.txt");
        double[][] x1 = readtxtToMatrix("X1.txt", ",");
        double[][] x2 = readtxtToMatrix("X2.txt", ",");
    
        // Medir el tiempo de inicio para la descomposición LU secuencial
        long startTimeLUSequential = System.currentTimeMillis();
    
        // Realizar descomposición LU secuencial
        Pair resultSequential1 = luDecomposition(x1);
        double[][] lowerSequential1 = resultSequential1.getLower();
        double[][] upperSequential1 = resultSequential1.getUpper();
    
        Pair resultSequential2 = luDecomposition(x2);
        double[][] lowerSequential2 = resultSequential2.getLower();
        double[][] upperSequential2 = resultSequential2.getUpper();
    
        // Escribir las matrices resultantes en archivos de texto
        MatrixTotxt("X_L_Sequential1.txt", lowerSequential1);
        MatrixTotxt("X_U_Sequential1.txt", upperSequential1);
        MatrixTotxt("X_L_Sequential2.txt", lowerSequential2);
        MatrixTotxt("X_U_Sequential2.txt", upperSequential2);
    
        // Medir el tiempo de finalización para la descomposición LU secuencial
        long endTimeLUSequential = System.currentTimeMillis();
    
        // Calcular el tiempo total de ejecución para la descomposición LU secuencial
        long executionTimeLUSequential = endTimeLUSequential - startTimeLUSequential;
        System.out.println("Tiempo de ejecución de LU secuencial: " + executionTimeLUSequential + " milisegundos");
    
        // Colas bloqueantes para la comunicación entre hilos
        BlockingQueue<double[][]> queue = new LinkedBlockingQueue<>();
        BlockingQueue<Result> resultQueue1 = new LinkedBlockingQueue<>();
    
        // Crear y comenzar el hilo de operación
        Operation operation1 = new Operation(queue, resultQueue1);
        operation1.start();
    
        // Medir el tiempo de inicio para la descomposición LU concurrente
        long startTimeLUConcurrent = System.currentTimeMillis();
    
        // Agregar matrices a la cola para su procesamiento
        queue.add(x1);
        queue.add(x2);
    
        try {
            int i = 0;
            while (i < 2) {  // Suponiendo que deseas procesar dos matrices
                Result result1 = resultQueue1.take();
                double[][] lower1 = result1.getLower();
                double[][] upper1 = result1.getUpper();
    
                // Escribir las matrices resultantes en archivos de texto
                MatrixTotxt("X_L_Concurrent" + i + ".txt", lower1);
                MatrixTotxt("X_U_Concurrent" + i + ".txt", upper1);
                i++;
            }
    
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        // Medir el tiempo de finalización para la descomposición LU concurrente
        long endTimeLUConcurrent = System.currentTimeMillis();
    
        // Calcular el tiempo total de ejecución para la descomposición LU concurrente
        long executionTimeLUConcurrent = endTimeLUConcurrent - startTimeLUConcurrent;
        System.out.println("Tiempo de ejecución de LU concurrente: " + executionTimeLUConcurrent + " milisegundos");
    }
}

// Clase que representa un par de matrices (lower y upper)
class Pair {
    double[][] lower;
    double[][] upper;

    public Pair(double[][] lower, double[][] upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double[][] getLower() {
        return lower;
    }

    public double[][] getUpper() {
        return upper;
    }
}

// Clase que representa el resultado de la descomposición LU
class Result {
    private final double[][] lower;
    private final double[][] upper;

    public Result(double[][] lower, double[][] upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double[][] getLower() {
        return lower;
    }

    public double[][] getUpper() {
        return upper;
    }
}

// Clase que realiza la operación de descomposición LU en un hilo separado
class Operation {
    private final BlockingQueue<double[][]> IN;
    private final BlockingQueue<Result> OUT;

    public Operation(BlockingQueue<double[][]> REQUEST, BlockingQueue<Result> RESPONSE) {
        this.IN = REQUEST;
        this.OUT = RESPONSE;
    }

    public Pair luDecomposition(double[][] matrix) {
        int n = matrix.length;
        double[][] lower = new double[n][n];
        double[][] upper = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int k = i; k < n; k++) {
                double sum = 0;
                for (int j = 0; j < i; j++)
                    sum += (lower[i][j] * upper[j][k]);
                upper[i][k] = matrix[i][k] - sum;
            }

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
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    double[][] matrix = IN.take();
                    Pair result = luDecomposition(matrix);
                    OUT.put(new Result(result.getLower(), result.getUpper()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
