import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class SearchDataset1 {
private static final int N = (int)Math.pow(10, 4); //Cantidad de Datos
private static final int T = N/4; //Tamaño de Particion
private static List<Integer> regMatches = new ArrayList<Integer>();


public static void GenerarDataset(){
       FileWriter writer = null;
       int num;
       try {
           writer = new FileWriter("dataset.txt");
           for(int i = 0; i < N; i++){
               for (int j = 0; j < 10; j++) {
                   num = 1 + (int) (Math.random() * 6);
                   writer.write(num + "");
               }
               writer.write("\n");
           }
           writer.close();
       } catch (IOException e) {
           System.out.println("Error encontrado");
           e.printStackTrace();
       }
   }
   public static void printMatches(int index){
       RandomAccessFile raf = null;
       String registro = "dataset.txt";
       try {
           raf = new RandomAccessFile(registro,"r");
           raf.seek(index);
           System.out.println((index/11 + 1) + " --> (" + raf.readLine() + ")");


       } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
       } catch (IOException e){
           System.out.println("Error");
       }
   }
   public static void BusqParalela(int posInicio, int posFinal, char[] arrSearch){
       RandomAccessFile raf = null;
       String registro = "dataset.txt";
       try{
           raf = new RandomAccessFile(registro, "r");
           raf.seek(posInicio);
           while(posInicio < posFinal){
               arrSearch = raf.readLine().toCharArray();
               if(arrSearch[0] == arrSearch[1]){
                   regMatches.add(posInicio);
               }
               posInicio += 11;
           }
       }
       catch(FileNotFoundException e){
           System.out.println("Archivo no encontrado");
       }
       catch(IOException e){
           System.out.println("Error");
       }finally {
           try {
               if(raf != null)
                   raf.close();
           }
           catch (Exception e) {
               System.out.println("Error al cerrar el fichero");
               System.out.println(e.getMessage());
           }
       }
   }


   public static void main(String args[]) throws InterruptedException {
       int numPart = N/T; int sizeReg = 11;
       GenerarDataset();
       char[][] arrSearch = new char[numPart][];
       double t1 = System.currentTimeMillis();
       for(int i = 0; i < numPart; i++){
           int finalI = i;
           new Thread(new Runnable() {
               @Override
               public void run() {
                   BusqParalela(finalI * T * sizeReg,(finalI +1)*T*sizeReg,arrSearch[finalI]);
               }
           }).start();
       }
       double t2 = System.currentTimeMillis();
       System.out.println("Tiempo de ejecución paralela: " + (t2-t1)/1000 + " segundos");    

       double t3 = System.currentTimeMillis();
       for(int i = 0; i < numPart; i++){
          BusqParalela(i * T * sizeReg,(i +1)*T*sizeReg,arrSearch[i]);
       }
       double t4 = System.currentTimeMillis();
       System.out.println("Tiempo de ejecución secuencial: " + (t4-t3)/1000 + " segundos");
   }
}