import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatrixHandling {
    // Método para leer datos desde un archivo de texto y convertirlos en una matriz
    public static Double[][] readTxtToMatrix(String TxtFile, String TxtSplitBy) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TxtFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Utilizar coma como separador
                String[] columns = line.split(TxtSplitBy);
                rows.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n = rows.get(0).length;
        Double[][] matrix = new Double[rows.size()][n];

        // Extraer datos de la matriz desde el archivo de texto
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(rows.get(i)[j]);
            }
        }

        return matrix;
    }

    // Método para mostrar una matriz en la consola
    public static void shwMatrix(Double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Método para escribir una matriz en un archivo de texto
    public static void matrixToTxt(Double[][] matrix, String fileName) {
        StringBuilder TxtContent = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == matrix[i].length - 1) {
                    TxtContent.append(String.format("%.6f", matrix[i][j])).append("\n");
                } else {
                    TxtContent.append(String.format("%.6f", matrix[i][j])).append(",");
                }
            }
        }
        toTxt(TxtContent.toString(), fileName+".txt");
    }

    // Método para escribir contenido de texto en un archivo
    public static void toTxt(String TxtContent, String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(TxtContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para generar una matriz con datos aleatorios
    public static Double[][] generateMatrix(int n, int m) {
        Double[][] matrix = new Double[n][m];
        int a = -10, b = 10;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m ; j++) {
                matrix[i][j] = a + Math.random() * (b - a + 1);
            }
        }
        return matrix;
    }

    // Método para generar una matriz con datos aleatorios y guardarlas en texto
    public static void generateMatrixToTxt(int n, int m, String fileName) {
        Double[][] matrix = generateMatrix(n, m);
        matrixToTxt(matrix, fileName);
    }

    // Método para multiplicar matrices
    public static Double[][] multiplyMatrix(Double[][] A, Double[][] B) {
        int n = A.length;
        int m = B[0].length;
        int o = B.length;
        Double[][] C = new Double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m ; j++) {
                Double sum = 0.0;
                for (int k = 0; k < o; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
}