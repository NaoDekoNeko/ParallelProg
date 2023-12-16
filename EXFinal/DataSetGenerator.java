import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataSetGenerator {

    public static void main(String[] args) {
        // Definir parámetros del dataset
        int totalObservations = 10000; // Número total de observaciones
        int numPredictors = 2;       // Número de variables predictoras (n)

        // Generar el dataset y guardarlo en un archivo DataSet.DAT
        generateDataSet(totalObservations, numPredictors, "DataSet.DAT");
    }

    private static void generateDataSet(int totalObservations, int numPredictors, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Random random = new Random();

            // Escribir encabezado
            for (int i = 1; i <= numPredictors; i++) {
                writer.write("X" + i + "\t");
            }
            
            writer.newLine();

            // Generar observaciones
            for (int observation = 1; observation <= totalObservations; observation++) {
                // Generar variables predictoras (Xi)
                for (int predictor = 1; predictor <= numPredictors; predictor++) {
                    double value = random.nextDouble() * 10; // Valor aleatorio entre 0 y 10
                    writer.write(String.format("%.2f\t", value));
                }

                

                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
