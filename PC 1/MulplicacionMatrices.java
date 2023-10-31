public class MulplicacionMatrices {
private static final int N = (int)Math.pow(6,5); //dimension de la matriz
private static final int T = N/4; //tamaño de particion
private static int A[][] = new int[N+1][N+1];
private static int X[][] = new int[N+1][N+1];
private static int B[][] = new int[N+1][N+1];
	//--------------------------------------------
	//Mulplicacion de matriz A por columna X[k]
	public static void MultiplyAXParalelo(int k) {
	for(int j = 1;j<=N;j++) {
		    B[j][k] = 0;
			for(int i=1;i<=N;i++) {
			    B[j][k] += A[j][i]*X[i][k];
		    }
	    }
	}
	//--------------------------------------------
	public static void MultiplyAXParaleloReduction(int k) {
		for (int i = 1; i <= n; i++) {
			int[] temp = new int[m];
			for (int j = 1; j <= m; j++) {
				temp[j] = A[i][j] * x[j][k];
			}
			for (int l = (int)(Math.log(n) / Math.log(2)); l >= 0; l--) {
				for (int j = 1; j <= Math.pow(2, l); j++) {
					temp[j] = temp[j] + temp[j + (int)Math.pow(2, l)];
				}
			}
			B[i][k] = temp[1];
		}
	}
	//--------------------------------------------
	//Visualización de matriz
	public static void ViewData(int[][] C) {
	for(int i=1;i<=N;i++) {
			for(int j=1;j<=N;j++) {
				System.out.print(C[i][j] + " ");
			}
			System.out.println("\n");
		}
	}
	
	//--------------------------------------------
	//Carga de datos a las matrices A y X
	public static void LoadData() {
	int a=-10, b=10;
		for(int i=1;i<=N;i++) {
			for(int j=1;j<=N;j++) {
				A[i][j] = (int)(a + Math.random()*(b-a+1));
				X[i][j] = (int)(a + Math.random()*(b-a+1));
			}
		}
	}
	//--------------------------------------------
	public static void main(String args[]) {
		int H = N/T; //Número de hilos -> dimension de la matriz/numero de particiones
		LoadData();
		double t1 = System.currentTimeMillis();
		//Se crean los hilos
		for(int k=1;k<=H;k++){
			int cont  = k;
			new Thread(new Runnable() {
				public void run() {
					//se manda a llamar a la funcion que multiplica la matriz A por la columna X[j]
					//donde j es la fila que le toca, y están particionados según el hilo en el que está
					for(int j = (cont-1)*T+1; j<=cont*T;j++) {
						MultiplyAXParaleloReduction(j);
					}
				}
			}).start();
		}

		double t2 = System.currentTimeMillis();

		System.out.println("Paralelo: "+ (t2-t1)/1000 + "segundos.\n");

		//Borrar comentarios para visualizar las matrices
		/*
		System.out.println("Matriz A:\n");
		ViewData(A);
		System.out.println("Matriz X:\n");
		ViewData(X);
		System.out.println("Matriz B:\n");
		ViewData(B);
		*/
		
		double t3 = System.currentTimeMillis();
		MultiplyAX();
		double t4 = System.currentTimeMillis();

		System.out.println("Secuencial: "+ (t4-t3)/1000 + "segundos.\n");

		//Borrar comentarios para visualizar las matrices
		/*
		System.out.println("Matriz A:\n");
		ViewData(A);
		System.out.println("Matriz X:\n");
		ViewData(X);
		System.out.println("Matriz B:\n");
		ViewData(B);
		 */
	}
}