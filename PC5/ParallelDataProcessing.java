import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ParallelDataProcessing {
    private final BlockingQueue<DataSet> dataQueue;
    private final ExecutorService executor;

    public ParallelDataProcessing(BlockingQueue<DataSet> dataQueue, int numThreads) {
        this.dataQueue = dataQueue;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    public void start() {
        List<Future<ProcessedData>> futures = new ArrayList<>();
        int hammingDistance = 0;
        String txtContent = "";
        while (!dataQueue.isEmpty()) {
            try {
                DataSet dataSet = dataQueue.take();
                Future<ProcessedData> future = executor.submit(() -> processDataSet(dataSet));
                futures.add(future);
                txtContent = txtContent + dataSet.getColumn1() + "," + dataSet.getColumn2() + "," + future.get().getHammingDistance() + "\n";
            } catch (InterruptedException| ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown(); // Apaga el ExecutorService

        try(FileWriter fw = new FileWriter("HammingDSResult.csv")){
            fw.write(txtContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        for (Future<ProcessedData> future : futures) {
            try{
                ProcessedData processedData = future.get();
                hammingDistance += processedData.getHammingDistance();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("La distancia de Hamming total es: " + hammingDistance);
    }

    private ProcessedData processDataSet(DataSet dataSet) {
        // Aquí es donde procesarías tu conjunto de datos para encontrar patrones y tendencias.
        // Por ejemplo, podrías calcular la distancia de Hamming entre dos columnas binarias:
        int hammingDistance = calculateHammingDistance(dataSet.getColumn1(), dataSet.getColumn2());
        return new ProcessedData(hammingDistance);
    }

    private int calculateHammingDistance(String binary1, String binary2) {
        int distance = 0;
        for (int i = 0; i < binary1.length(); i++) {
            if (binary1.charAt(i) != binary2.charAt(i)) {
                distance++;
            }
        }
        return distance;
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

class DataSet{
    private final String column1;
    private final String column2;

    public DataSet(String column1, String column2) {
        this.column1 = column1;
        this.column2 = column2;
    }

    public String getColumn1() {
        return column1;
    }

    public String getColumn2() {
        return column2;
    }
}

class ProcessedData{
    private final int hammingDistance;

    public ProcessedData(int hammingDistance) {
        this.hammingDistance = hammingDistance;
    }

    public int getHammingDistance() {
        return hammingDistance;
    }
}