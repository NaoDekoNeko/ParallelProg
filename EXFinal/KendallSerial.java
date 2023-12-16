import java.util.Random;

public class KendallSerial {
    public static void main(String[] args) {
        // Generacion de vectore
        int totalObservations = 1000; // Número total de observaciones
        double[] x = new double[totalObservations];
        double[] y = new double[totalObservations];
        Random random = new Random();
        for (int i = 0; i < totalObservations; i++) {
            x[i] = random.nextInt(10);
            y[i] = random.nextInt(10);
        }

        long startTime = System.currentTimeMillis();

        double kendallCorrelation = calculateKendallCorrelation(x, y);
        // Para el calculo del tiempo de ejecucion
        long endTime = System.currentTimeMillis();

        System.out.println("Coeficiente de correlación de Kendall: " + kendallCorrelation);
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " milisegundos");
    }
    
    public static double calculateKendallCorrelation(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Los conjuntos de datos deben tener la misma longitud");
        }

        int n = x.length;
        int concordantCount = 0;
        int discordantCount = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double x1 = x[i];
                double x2 = x[j];
                double y1 = y[i];
                double y2 = y[j];

                // Verificar si los pares son concordantes o discordantes
                if ((x1 < x2 && y1 < y2) || (x1 > x2 && y1 > y2)) {
                    concordantCount++;
                } else if ((x1 < x2 && y1 > y2) || (x1 > x2 && y1 < y2)) {
                    discordantCount++;
                }
            }
        }

        // Calcular el coeficiente de correlación de Kendall
        double kendallCorrelation = (concordantCount - discordantCount) / Math.sqrt((concordantCount + discordantCount) * (n * (n - 1) / 2));

        return kendallCorrelation;
    }
}