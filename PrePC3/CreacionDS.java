import java.io.FileWriter;
import java.util.Random;

public class CreacionDS {
    public static void main(String[] args) {
        hamming();
        vectors(1000, 3);
    }

    public static void hamming(){
        Random Random = new Random();
        int i = Random.nextInt(1000);
        int j = Random.nextInt(1000);
        String csvContent = "";
        for (int k = 0; k < 1000; k++) {
            csvContent = csvContent  + i%2 + "," + j%2 + "\n";
            i = Random.nextInt(1000);
            j = Random.nextInt(1000);
        }
        toCsv(csvContent, "HammingDS.csv");
    }

    public static void toCsv(String csvContent, String fileName){
        try(FileWriter fw = new FileWriter(fileName)){
            fw.write(csvContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void vectors(int n, int dim){
        double[][] x = new double[n][dim];
        double[][] y = new double[n][dim];
        Random Random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dim ; j++) {
                x[i][j] = Random.nextDouble();
                y[i][j] = Random.nextDouble();
            }
        }

        String csvContent = "";

        for(int i = 0; i < n; i++){
            for(int j = 0; j < dim; j++){
                if(j == dim - 1)
                    csvContent = csvContent + x[i][j] + "\n";
                else
                    csvContent = csvContent + x[i][j] + ",";
            }
        }

        toCsv(csvContent, "vectorsDS.csv");
    }
}