public class MulplicacionMatrices {
	private static int N; //dimension de la matriz
	private static int H; //numero de hilos
	private static int T; //tamaño de particion
	private static double A[][];
	private static double X[][];
	private static double B[][];
	//--------------------------------------------
	//Mulplicacion de matriz A por columna X[k]
	public static void MultiplyAXParalelo(int k) {
	for(int j = 0;j<N;j++) {
		    B[j][k] = 0;
			for(int i=0;i<N;i++) {
			    B[j][k] += A[j][i]*X[i][k];
		    }
	    }
	}
	//--------------------------------------------
	//Visualización de matriz
	public static void ViewData(int[][] C) {
	for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				System.out.print(C[i][j] + " ");
			}
			System.out.println("\n");
		}
	}
	
	//--------------------------------------------
	//Carga de datos a las matrices A y X
	public static void LoadData() {
	int a=-10, b=10;
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				A[i][j] = (int)(a + Math.random()*(b-a+1));
				X[i][j] = (int)(a + Math.random()*(b-a+1));
			}
		}
	}
	//--------------------------------------------
	public static double[][] MatMul(){
		H = Runtime.getRuntime().availableProcessors();
		T = N/H;
		for(int k=0;k<H;k++){
			int cont  = k;
			new Thread(new Runnable() {
				public void run() {
					//se manda a llamar a la funcion que multiplica la matriz A por la columna X[j]
					//donde j es la fila que le toca, y están particionados según el hilo en el que está
					for(int j = cont*T; j<(cont+1)*T;j++) {
						MultiplyAXParalelo(j);
					}
				}
			}).start();
		}
		return B;
	}
	//--------------------------------------------
	public MulplicacionMatrices(double[][] A,double[][] X){
		MulplicacionMatrices.A = A;
		MulplicacionMatrices.X = X;
		MulplicacionMatrices.N = A.length;
		MulplicacionMatrices.B = new double[N][N];
	}
}