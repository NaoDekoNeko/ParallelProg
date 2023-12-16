import java.awt.Font;
import java.awt.Color;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

public class DS_Thread {

private static JFrame WDW1 = new JFrame("Program 1");
private static JFrame WDW2 = new JFrame("Program 2");
private static JFrame WDW3 = new JFrame("Program 3");
private static JFrame WDW4 = new JFrame("Program 4");
private static JScrollPane SP1;
private static JScrollPane SP2;
private static JScrollPane SP3;
private static JScrollPane SP4;
private static JTextArea   TA1  = new JTextArea("");  
private static JTextArea   TA2  = new JTextArea("");  
private static JTextArea   TA3  = new JTextArea("");  
private static JTextArea   TA4  = new JTextArea("");  
private static Font fntLABEL = new Font("Arial",Font.BOLD,24);
private static Font fntTEXT  = new Font("Lucida Console",Font.BOLD,18);
private static JLabel LBL1Start  = new javax.swing.JLabel();
private static JLabel LBL1Finish = new javax.swing.JLabel();
private static JLabel LBL2Start  = new javax.swing.JLabel();
private static JLabel LBL2Finish = new javax.swing.JLabel();
private static JLabel LBL3Start  = new javax.swing.JLabel();
private static JLabel LBL3Finish = new javax.swing.JLabel();
private static JLabel LBL4Start  = new javax.swing.JLabel();
private static JLabel LBL4Finish = new javax.swing.JLabel();
//Numero de filas de la matriz
private static final int N = 500000;
private static Double[][] M1;

//==============================================================================
    public static void ConfigurarControles(JFrame WDW, 
                                           int WW,
                                           int HH,
                                           int LEFT,
                                           int TOP,
                                           JScrollPane SP,
                                           JTextArea   TA,
                                           JLabel      LBLStart,
                                           JLabel      LBLFinish
                                           )  {

        WDW.setSize(WW, HH);
        WDW.setLocation(LEFT,TOP);
        WDW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WDW.setVisible(true);

        LBLStart.setBounds(25, 20, 400, 40);
        LBLFinish.setBounds(25, 20, 400, 40);

        LBLStart.setFont(fntLABEL);
        LBLFinish.setFont(fntLABEL);

        TA.setEditable(false);
        TA.setBounds(25,60,300,300);
        TA.setBackground(Color.WHITE);
        TA.setFont(fntTEXT);
        TA.setForeground(Color.GREEN);
        TA.setBackground(Color.BLACK);

        SP = new JScrollPane(TA);
        SP.setBounds(25,50,300,300);

        WDW.add(LBLStart);
        WDW.add(SP);
        WDW.add(LBLFinish);
        WDW.setVisible(true);
    }
    //==============================================================================
    private static void LoadMatrices() {
        M1 = MatrixHandling.generateMatrix(N, 20); //Numero de columnas de la matriz
    }
    //==============================================================================
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger AI1 = new AtomicInteger(1);
        AtomicInteger AI2 = new AtomicInteger(1);
        AtomicInteger AI3 = new AtomicInteger(1); //AI1.get()==1
        AtomicInteger AI4 = new AtomicInteger(1); //AI1.get()==1
        ConfigurarControles(WDW1,400,500,100,40,SP1,TA1,LBL1Start,LBL1Finish);
        ConfigurarControles(WDW2,400,500,500,40,SP2,TA2,LBL2Start,LBL2Finish);
        ConfigurarControles(WDW3,400,500,700,40,SP3,TA3,LBL3Start,LBL3Finish);
        ConfigurarControles(WDW4,400,500,950,40,SP4,TA4,LBL4Start,LBL4Finish);
        LoadMatrices();
        MatrixHandling.matrixToTxt(M1, "DS1");
        List<Double[]> liVectors = new ArrayList<>();
        int tasksPerThread = N / 3;
        //-------------------------------------------------
        new Thread(new Runnable() {  //Thread.sleep(1000);
            public void run() {
                TA1.append(AI1.toString()+"\n");
                long inicio = System.currentTimeMillis();
                LBL1Start.setText("Time Execution: " + 0 + " segundos");
                List<Double[]> resSec = CorrelationLIExtractor.extractLIvectors(M1);
                long fin = System.currentTimeMillis() - inicio;
                LBL1Finish.setText("Time Execution: " + fin / 1000 + " segundos");
                AI1.set(0);
                TA1.append(AI1.toString()+"\n");
                Double[][] iMatrix = new Double[resSec.size()][];
                iMatrix = resSec.toArray(iMatrix);
                MatrixHandling.matrixToTxt(iMatrix, "LIvectorsSec");
            }
        }).start();
        //-------------------------------------------------
        new Thread(new Runnable() {
            public void run() {
                TA2.append(AI2.toString()+"\n");
                long inicio = System.currentTimeMillis();
                LBL2Start.setText("Time Execution: " + 0 + " segundos");
                int startIndex = 0;
                int endIndex = tasksPerThread;
                for (int i = startIndex; i < endIndex; i++) {
                    Double[] currentVector = M1[i];

                    synchronized (liVectors) {
                        if (CorrelationLIExtractor.isLinearlyIndependentWithCorrelation(liVectors, currentVector))
                            liVectors.add(currentVector);
                    }
                }
                long fin = System.currentTimeMillis() - inicio;
                LBL2Finish.setText("Time Execution: " + fin / 1000 + " segundos");
                AI2.set(0);
                TA2.append(AI2.toString()+"\n");
            }
        }).start();
        //-------------------------------------------------
        new Thread(new Runnable() {
            public void run() {
                TA3.append(AI3.toString()+"\n");
                long inicio = System.currentTimeMillis();
                LBL3Start.setText("Time Execution: " + 0 + " segundos");
                int startIndex = tasksPerThread;
                int endIndex = 2 * tasksPerThread;
                for (int i = startIndex; i < endIndex; i++) {
                    Double[] currentVector = M1[i];

                    synchronized (liVectors) {
                        if (CorrelationLIExtractor.isLinearlyIndependentWithCorrelation(liVectors, currentVector))
                            liVectors.add(currentVector);
                    }
                }
                long fin = System.currentTimeMillis() - inicio;
                LBL3Finish.setText("Time Execution: " + fin / 1000 + " segundos");
                AI3.set(0);
                TA3.append(AI3.toString()+"\n");
            }
        }).start();
        //-------------------------------------------------
        new Thread(new Runnable() {
            public void run() {
                TA4.append(AI4.toString()+"\n");
                long inicio = System.currentTimeMillis();
                LBL4Start.setText("Time Execution: " + 0 + " segundos");
                int startIndex = 2 * tasksPerThread;
                int endIndex = N;
                for (int i = startIndex; i < endIndex; i++) {
                    Double[] currentVector = M1[i];

                    synchronized (liVectors) {
                        if (CorrelationLIExtractor.isLinearlyIndependentWithCorrelation(liVectors, currentVector))
                            liVectors.add(currentVector);
                    }
                }
                long fin = System.currentTimeMillis() - inicio;
                LBL4Finish.setText("Time Execution: " + fin / 1000 + " segundos");
                AI4.set(0);
                TA4.append(AI4.toString()+"\n");
            }
        }).start();
        //-------------------------------------------------
        while (AI1.get()==1 || AI2.get()==1 || AI3.get()==1 || AI4.get()==1) {
            Thread.sleep(1000);
            Double[][] iMatrix1 = new Double[liVectors.size()][];
                iMatrix1 = liVectors.toArray(iMatrix1);
                MatrixHandling.matrixToTxt(iMatrix1, "LIvectorsPar");
        }
    }
    //==============================================================================
}

class CorrelationLIExtractor {
    // Método para extraer los vectores fila LI de una matriz utilizando la correlación de Pearson
    public static List<Double[]> extractLIvectors(Double[][] matrix) {
        int numRows = matrix.length;
        List<Double[]> liVectors = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            Double[] currentVector = matrix[i];

            if (isLinearlyIndependentWithCorrelation(liVectors, currentVector))
                liVectors.add(currentVector);
        }

        return liVectors;
    }

    // Método para verificar si un vector es linealmente independiente respecto a un conjunto dado utilizando la correlación de Pearson
    public static boolean isLinearlyIndependentWithCorrelation(List<Double[]> vectors, Double[] vector) {
        for (Double[] v : vectors) {
            Double correlation = calculatePearsonCorrelation(v, vector);

            // Establece un umbral para determinar la dependencia lineal
            Double correlationThreshold = 0.05; // Ajusta según sea necesario

            if (Math.abs(correlation) > correlationThreshold)
                return false; // Vectores linealmente dependientes
        }
        return true;
    }

    private static Double calculatePearsonCorrelation(Double[] vector1, Double[] vector2) {
        if (vector1.length != vector2.length) 
            throw new IllegalArgumentException("Los vectores deben tener la misma longitud");
    
        // Calcular la media de cada vector
        Double mean1 = calculateMean(vector1);
        Double mean2 = calculateMean(vector2);

        // Calcular la correlación de Pearson
        Double numerator = 0.0;
        Double denominator1 = 0.0;
        Double denominator2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            Double diff1 = vector1[i] - mean1;
            Double diff2 = vector2[i] - mean2;

            numerator += diff1 * diff2;
            denominator1 += Math.pow(diff1, 2);
            denominator2 += Math.pow(diff2, 2);
        }

        // Evitar la división por cero
        if (denominator1 == 0 || denominator2 == 0)
            return 0.0;

        return numerator / Math.sqrt(denominator1 * denominator2);
    }

    // Método para calcular la media de un vector
    private static Double calculateMean(Double[] vector) {
        Double sum = 0.0;
        for (Double value : vector) {
            sum += value;
        }
        return sum / vector.length;
    }
}