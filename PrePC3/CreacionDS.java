import java.io.FileWriter;
import java.util.Random;

public class CreacionDS {
    public static void main(String[] args) {
        Random Random = new Random();
        int i = Random.nextInt(1000);
        int j = Random.nextInt(1000);
        String csvContent = "";
        for (int k = 0; k < 1000; k++) {
            csvContent = csvContent  + i%2 + "," + j%2 + "\n";
            i = Random.nextInt(1000);
            j = Random.nextInt(1000);
        }
        try(FileWriter fw = new FileWriter("HammingDS.csv")){
            fw.write(csvContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
