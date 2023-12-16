import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CorrelationLIExtractor {
    // Método para extraer los vectores fila LI de una matriz utilizando la correlación de Pearson
    public static List<Double[]> extractLIvectors(Double[][] matrix) {
        int numRows = matrix.length;
        List<Double[]> liVectors = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            Double[] currentVector = matrix[i];

            if (isLinearlyIndependentWithCorrelation(liVectors, currentVector))
                liVectors.add(currentVector);
        }

        return liVectors;
    }

    // Método para verificar si un vector es linealmente independiente respecto a un conjunto dado utilizando la correlación de Pearson
    private static boolean isLinearlyIndependentWithCorrelation(List<Double[]> vectors, Double[] vector) {
        for (Double[] v : vectors) {
            Double correlation = calculatePearsonCorrelation(v, vector);

            // Establece un umbral para determinar la dependencia lineal
            Double correlationThreshold = 0.05; // Ajusta según sea necesario

            if (Math.abs(correlation) > correlationThreshold)
                return false; // Vectores linealmente dependientes
        }
        return true;
    }

    public static Double calculatePearsonCorrelation(Double[] vector1, Double[] vector2) {
        if (vector1.length != vector2.length) 
            throw new IllegalArgumentException("Los vectores deben tener la misma longitud");
    
        // Calcular la media de cada vector
        Double mean1 = calculateMean(vector1);
        Double mean2 = calculateMean(vector2);

        // Calcular la correlación de Pearson
        Double numerator = 0.0;
        Double denominator1 = 0.0;
        Double denominator2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            Double diff1 = vector1[i] - mean1;
            Double diff2 = vector2[i] - mean2;

            numerator += diff1 * diff2;
            denominator1 += Math.pow(diff1, 2);
            denominator2 += Math.pow(diff2, 2);
        }

        // Evitar la división por cero
        if (denominator1 == 0 || denominator2 == 0)
            return 0.0;

        return numerator / Math.sqrt(denominator1 * denominator2);
    }

    // Método para calcular la media de un vector
    private static Double calculateMean(Double[] vector) {
        Double sum = 0.0;
        for (Double value : vector) {
            sum += value;
        }
        return sum / vector.length;
    }

    // Método para extraer los vectores fila LI de una matriz utilizando la correlación de Pearson de manera paralela
    public static List<Double[]> extractLIvectorsParallel(Double[][] matrix, int numThreads) {
        int numRows = matrix.length;
        List<Double[]> liVectors = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Double[]>>> futures = new ArrayList<>();

        // Divide el trabajo en partes para cada hilo
        int tasksPerThread = numRows / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * tasksPerThread;
            int endIndex = (i == numThreads - 1) ? numRows : (i + 1) * tasksPerThread;

            Callable<List<Double[]>> task = new LIExtractionTask(matrix, startIndex, endIndex);
            Future<List<Double[]>> future = executor.submit(task);
            futures.add(future);
        }

        // Espera a que todos los hilos completen
        for (Future<List<Double[]>> future : futures) {
            try {
                liVectors.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return liVectors;
    }

    // Clase Callable para realizar la extracción de LI en un rango de filas de la matriz
    static class LIExtractionTask implements Callable<List<Double[]>> {
        private final Double[][] matrix;
        private final int startIndex;
        private final int endIndex;

        public LIExtractionTask(Double[][] matrix, int startIndex, int endIndex) {
            this.matrix = matrix;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public List<Double[]> call() {
            List<Double[]> localLIVectors = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                Double[] currentVector = matrix[i];

                if (isLinearlyIndependentWithCorrelation(localLIVectors, currentVector))
                    localLIVectors.add(currentVector);
            }

            return localLIVectors;
        }
    }
}