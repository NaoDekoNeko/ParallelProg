import java.util.ArrayList;
import java.util.List;

public class CorrelationLIExtractor {

    public static void main(String[] args) {
        int n = 100; // Ajusta según sea necesario
        Double[][] dataset = MatrixHandling.generateMatrix(n, 4);

        MatrixHandling.matrixToTxt(dataset, "DataSet");

        List<Double[]> liVectors = extractLIvectors(dataset);

        // Convierte la lista de vectores a una matriz
        Double[][] liMatrix = new Double[liVectors.size()][];
        liMatrix = liVectors.toArray(liMatrix);

        MatrixHandling.matrixToTxt(liMatrix, "LIvectors");
    }

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
            Double correlationThreshold = 0.7; // Ajusta según sea necesario

            if (Math.abs(correlation) > correlationThreshold)
                return false; // Vectores linealmente dependientes
        }
        return true;
    }

    private static Double calculatePearsonCorrelation(Double[] vector1, Double[] vector2) {
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
}