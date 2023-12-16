import java.util.*;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class DataSet {
    private Double[][] data;

    public DataSet(Double[][] data) {
        this.data = data;
    }

    public Double[] get(int index) {
        return data[index];
    }

    public int size() {
        return data.length;
    }

    public void print() {
    for (Double[] row : data) {
        System.out.println(Arrays.toString(row));
    }
}
}

class DataSetReader {
    public DataSet readDataSet(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    ArrayList<Double[]> data = new ArrayList<>();

    // Ignora la primera línea
    reader.readLine();

    String line;
    while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\t");
        Double[] row = Arrays.stream(parts)
                             .map(Double::valueOf)
                             .toArray(Double[]::new);
        data.add(row);
    }

    reader.close();

    Double[][] dataArray = data.toArray(new Double[data.size()][]);
    return new DataSet(dataArray);
    }
}

class CorrelationTask implements Callable<Double> {
    private Double[] vector1;
    private Double[] vector2;

    public CorrelationTask(Double[] vector1, Double[] vector2) {
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    @Override
    public Double call() {
        return CorrelationLIExtractor.calculatePearsonCorrelation(vector1, vector2);
    }
}

class Task implements Callable<Integer> {
    private DataSet dataSet;
    private int start;
    private int end;
    private Double selectedValue;

    public Task(DataSet dataSet, int start, int end, Double selectedValue) {
        this.dataSet = dataSet;
        this.start = start;
        this.end = end;
        this.selectedValue = selectedValue;
    }

    @Override
    public Integer call() {
        int count = 0;
        for (int i = start; i < end; i++) {
            Double[] values = dataSet.get(i);
            for (Double value : values) {
                if (value.equals(selectedValue)) {
                    count++;
                }
            }
        }
        return count;
    }
}

public class EXFinal {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // -----LECTOR DE ARCHIVO-----
        // Leer el archivo DataSet.DAT
        DataSetReader reader = new DataSetReader();
        DataSet dataSet = null;
        try {
            dataSet = reader.readDataSet("DataSet.DAT");
            //dataSet.print();  // Imprime el contenido del DataSet
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // -----LECTOR DE ARCHIVO-----

        // Crea un ExecutorService con un número fijo de hilos
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Selecciona el valor con el que quieres calcular la correlación
        Double selectedValue = dataSet.get(0)[0];

        // Divide tu DataSet en partes y crea una tarea para cada parte
        List<Future<Integer>> futures = new ArrayList<>();
        int partSize = dataSet.size() / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int start = i * partSize;
            int end = (i + 1) * partSize;
            if (i == numThreads - 1) {
                end = dataSet.size(); // Asegúrate de que la última parte incluya los elementos restantes
            }
            futures.add(executor.submit(new Task(dataSet, start, end, selectedValue)));
        }

        // Recoge los resultados de las tareas
        int total = 0;
        for (Future<Integer> future : futures) {
            total += future.get();
        }

        // Imprime los resultados
        System.out.println("Count of " + selectedValue + ": " + total);

        // Crea una lista para almacenar los futuros
        List<Future<Double>> futures2 = new ArrayList<>();

        // Para cada fila en el DataSet que tenga el mismo valor que el seleccionado, calcula la correlación de Pearson en paralelo
        for (int i = 0; i < dataSet.size(); i++) {
            Double[] row = dataSet.get(i);
            if (Arrays.asList(row).contains(selectedValue)) {
                futures2.add(executor.submit(new CorrelationTask(row, dataSet.get(i))));
            }
        }

        // Recoge los resultados de las tareas
        List<Double> correlations = new ArrayList<>();
        for (Future<Double> future : futures2) {
            correlations.add(future.get());
        }

        // Imprime los resultados
        System.out.println("Correlations: " + correlations);

        // No olvides cerrar tu ExecutorService!
        executor.shutdown();
    }
}