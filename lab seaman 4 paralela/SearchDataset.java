public class SearchDataset {
private static final int N = 5000000; //Cantidad de Datos
private static final int T = 1250000; //Tamaño de Particion
private static int V01[] = new int[N+1];
private static int V02[] = new int[N+1];
private static int V03[] = new int[N+1];
private static int V04[] = new int[N+1];
private static int V05[] = new int[N+1];
private static int V06[] = new int[N+1];
private static int V07[] = new int[N+1];
private static int V08[] = new int[N+1];
private static int V09[] = new int[N+1];
private static int V10[] = new int[N+1];

    public static void ProcesoSerial() {
        for(int i=1;i<=N;i++) {
            if((V01[i]==V02[i])&&(V03[i]==V04[i])&&(V05[i]==V06[i])&&(V07[i]==V08[i])&&(V09[i]==V10[i])&&(V01[i]==V03[i])&&(V03[i]==V05[i])&&(V05[i]==V07[i])&&(V07[i]==V09[i])) {
                System.out.println(i + "\t---> [ " + 
                                   V01[i] + " " + 
                                   V02[i] + " " + 
                                   V03[i] + " " + 
                                   V04[i] + " " + 
                                   V05[i] + " " + 
                                   V06[i] + " " + 
                                   V07[i] + " " + 
                                   V08[i] + " " + 
                                   V09[i] + " " + 
                                   V10[i] + " ]");
            }
        }
    }

    public static void Recorrido(int k, int A, int B) {
    	System.out.println("Particion " + k + ":");
    	System.out.println("-------------------");
        for(int i=A;i<=B;i++) {
            if((V01[i]==V02[i])&&(V03[i]==V04[i])&&(V05[i]==V06[i])&&(V07[i]==V08[i])&&(V09[i]==V10[i])&&(V01[i]==V03[i])&&(V03[i]==V05[i])&&(V05[i]==V07[i])&&(V07[i]==V09[i])) {
                System.out.println(i + "\t---> [ " + 
                                   V01[i] + " " + 
                                   V02[i] + " " + 
                                   V03[i] + " " + 
                                   V04[i] + " " + 
                                   V05[i] + " " + 
                                   V06[i] + " " + 
                                   V07[i] + " " + 
                                   V08[i] + " " + 
                                   V09[i] + " " + 
                                   V10[i] + " ]");
            }
        }
    }

    public static void ProcesoParalelo() {
    int TP;
        TP = N/T;
        for(int k=1;k<=TP;k++) {
        	Recorrido(k,(k-1)*T+1,k*T);
        }
    }



    public static void VisualizarDataset() {
    int num;
        for(int i=1;i<=N;i++) {
            System.out.println(i + "\t---> [ " + 
                               V01[i] + "   " + 
                               V02[i] + "   " + 
                               V03[i] + "   " + 
                               V04[i] + "   " + 
                               V05[i] + "   " + 
                               V06[i] + "   " + 
                               V07[i] + "   " + 
                               V08[i] + "   " + 
                               V09[i] + "   " + 
                               V10[i] + " ]");
        }
    }
    public static void GenerarDataset() {
    int num;
        for(int i=1;i<=N;i++) {
            num = 1 + (int)(Math.random()*6);   V01[i] = num;
            num = 1 + (int)(Math.random()*6);   V02[i] = num;
            num = 1 + (int)(Math.random()*6);   V03[i] = num;
            num = 1 + (int)(Math.random()*6);   V04[i] = num;
            num = 1 + (int)(Math.random()*6);   V05[i] = num;
            num = 1 + (int)(Math.random()*6);   V06[i] = num;
            num = 1 + (int)(Math.random()*6);   V07[i] = num;
            num = 1 + (int)(Math.random()*6);   V08[i] = num;
            num = 1 + (int)(Math.random()*6);   V09[i] = num;
            num = 1 + (int)(Math.random()*6);   V10[i] = num;
        }
    }
    public static void main(String args[]) {
        GenerarDataset();
        //VisualizarDataset();
        //ProcesoSerial();
        //ProcesoParalelo();

        for (int i = 1; i<= (N/T);i++){
            int cont = i;
            new Thread(new Runnable (){
                public void run(){
                    Recorrido(cont,(cont-1)*T+1,cont*T);
                }
            }).start();
        }
    }
}