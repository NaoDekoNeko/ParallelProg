import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class KendallParallel {
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

        double kendallCorrelation = calculateParallelKendallCorrelation(x, y);
        // Para el calculo del tiempo de ejecucion
        long endTime = System.currentTimeMillis();

        // Resultados
        System.out.println("Coeficiente de correlación de Kendall: " + kendallCorrelation);
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " milisegundos");
    }

    public static double calculateParallelKendallCorrelation(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Los conjuntos de datos deben tener la misma longitud");
        }

        int n = x.length;

        // Utilizando ForkJoinPool para paralelizar el cálculo
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        KendallCorrelationTask task = new KendallCorrelationTask(x, y, 0, n - 1);
        int[] results = forkJoinPool.invoke(task);

        // Calcular el coeficiente de correlación de Kendall
        double concordantCount = results[0];
        double discordantCount = results[1];

        return (concordantCount - discordantCount) / Math.sqrt((concordantCount + discordantCount) * (n * (n - 1) / 2));
    }

    // Clase que representa una tarea recursiva para calcular las concordancias y discordancias
    static class KendallCorrelationTask extends RecursiveTask<int[]> {
        private final double[] x;
        private final double[] y;
        private final int start;
        private final int end;

        public KendallCorrelationTask(double[] x, double[] y, int start, int end) {
            this.x = x;
            this.y = y;
            this.start = start;
            this.end = end;
        }

        @Override
        protected int[] compute() {
            if (start == end) {
                return new int[]{0, 0}; // Tarea base: no hay pares para comparar
            }

            int mid = (start + end) / 2;

            // Dividir la tarea en sub-tareas
            KendallCorrelationTask leftTask = new KendallCorrelationTask(x, y, start, mid);
            KendallCorrelationTask rightTask = new KendallCorrelationTask(x, y, mid + 1, end);

            // Ejecutar sub-tareas de manera paralela
            invokeAll(leftTask, rightTask);

            // Combinar resultados de sub-tareas
            int[] leftResults = leftTask.join();
            int[] rightResults = rightTask.join();

            int concordantCount = leftResults[0] + rightResults[0];
            int discordantCount = leftResults[1] + rightResults[1];

            // Calcular concordancias y discordancias para la tarea actual
            for (int i = start; i <= mid; i++) {
                for (int j = mid + 1; j <= end; j++) {
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

            // Detalle de los hilos usados
            System.out.println("Hilo " + Thread.currentThread().getId() + " - Tarea completada en rango " + start + " a " + end);

            return new int[]{concordantCount, discordantCount};
        }
    }
}