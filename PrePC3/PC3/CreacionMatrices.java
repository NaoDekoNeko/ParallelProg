import java.io.FileWriter;
import java.util.Random;

public class CreacionMatrices {
    public static void Matrix(int n, int m, String fileName){
        int[][] x = new int[n][m];
        Random Random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m ; j++) {
                x[i][j] = Random.nextInt(100);
            }
        }

        String xContent = "";

        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(j == m - 1){
                    xContent = xContent + x[i][j] + "\n";
                }
                else{
                    xContent = xContent + x[i][j] + ",";
                }
            }
        }

        totxt(xContent, fileName);
    }

    public static void totxt(String txtContent, String fileName){
        try(FileWriter fw = new FileWriter(fileName)){
            fw.write(txtContent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
