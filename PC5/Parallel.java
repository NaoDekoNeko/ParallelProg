import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ParallelDataProcessing {
    private final BlockingQueue<DataSet> dataQueue;
    private final ExecutorService executor;

    public ParallelDataProcessing(BlockingQueue<DataSet> dataQueue, int numThreads) {
        this.dataQueue = dataQueue;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    public void start() {
        List<Future<ProcessedData>> futures = new ArrayList<>();
        double sum = 0.0;
        String txtContent = "";
        while (!dataQueue.isEmpty()) {
            try {
                DataSet dataSet = dataQueue.take();
                Future<ProcessedData> future = executor.submit(() -> processDataSet(dataSet));
                futures.add(future);
                sum += Math.pow(future.get().getDeviation(), 2);
                txtContent = txtContent + Arrays.toString(dataSet.getVector()) + "," + future.get().getDeviation() + "\n";
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown(); // Apaga el ExecutorService

        try (FileWriter fw = new FileWriter("DesviacionDSResult.csv")) {
            fw.write(txtContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double standardDeviation = Math.sqrt(sum / futures.size());
        System.out.println("La desviación estándar total es: " + standardDeviation);
    }

    private ProcessedData processDataSet(DataSet dataSet) {
        double deviation = calculateDeviation(dataSet.getVector());
        return new ProcessedData(deviation);
    }

    private double calculateDeviation(double[] vector) {
        double mean = Arrays.stream(vector).average().orElse(0.0);
        double sumSquaredDeviation = Arrays.stream(vector).map(x -> Math.pow(x - mean, 2)).sum();
        return Math.sqrt(sumSquaredDeviation / vector.length);
    }

    public static void main(String[] args) {
        // Crea una cola para los conjuntos de datos
        BlockingQueue<DataSet> dataQueue = new LinkedBlockingQueue<>();
    
        // Crea una instancia de ParallelDataProcessing con 4 hilos
        ParallelDataProcessing pdp = new ParallelDataProcessing(dataQueue, 4);
    
        // Lee los datos del archivo CSV
        String csvFile = "HammingDS.csv";
        String line;
        String csvSplitBy = ","; // El delimitador del CSV
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // Usa la coma como separador
                String[] columns = line.split(csvSplitBy);
    
                // Crea un nuevo DataSet con las columnas leídas y lo agrega a la cola
                dataQueue.add(new DataSet(columns[0], columns[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Inicia el procesamiento de los datos
        pdp.start();
    }
}

class DataSet {
    private final double[] vector;

    public DataSet(double[] vector) {
        this.vector = vector;
    }

    public double[] getVector() {
        return vector;
    }
}

class ProcessedData {
    private final double deviation;

    public ProcessedData(double deviation) {
        this.deviation = deviation;
    }

    public double getDeviation() {
        return deviation;
    }
}
