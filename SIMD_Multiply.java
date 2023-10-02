public class SIMD_Multiply {
    private static final int N = 3;
    private static int A[][] = new int[N+1][N+1];
    private static int   X[] = new int[N+1];
    private static int   B[] = new int[N+1];
    private static int C[][] = new int[N+1][N+1];
    
        //--------------------------------------------
      public static void ViewData2() {
            int a=-10, b=10;
            for (int k=1; k<=N;k++){
                for(int i=1;i<=N;i++) {
                    for(int j=1;j<=N;j++) {
                        System.out.print(A[i][j] + "\t");
                    }
                    System.out.print("# X["+i+"][0] = " + X[i] + "\t");
                    System.out.print("# " + C[i][k] + "\n");
                }
                System.out.println("");
                System.out.println("");
            }
        }    
        public static void MultiplyAX2() {
            int a=-10, b=10, S;
            for (int j = 0; j <= N; j++) {
                for(int i=1;i<=N;i++) {
                    S = 0;
                    for(int k=1;k<=N;k++) {
                        if(k==j){
                            C[i][j] = A[i][k] * X[j];
                        }
                    }
                }
            }
           
        }    
        //--------------------------------------------
        public static void MultiplyAX() {
        int a=-10, b=10, S;
            for(int i=1;i<=N;i++) {
                S = 0;
                for(int k=1;k<=N;k++) {
                    S = S + A[i][k]*X[k];
                }
                B[i] = S;
            }
        }
        //--------------------------------------------
        public static void ViewData() {
        int a=-10, b=10;
            for(int i=1;i<=N;i++) {
                for(int j=1;j<=N;j++) {
                    System.out.print(A[i][j] + "\t");
                }
                System.out.print("│\t" + X[i] + "\t");
                System.out.print("│\t" + B[i] + "\n");
            }
            System.out.print("\n\n\n");
        }
        //--------------------------------------------
        public static void LoadData() {
        int a=-10, b=10;
            for(int i=1;i<=N;i++) {
                for(int j=1;j<=N;j++) {
                    A[i][j] = (int)(a + Math.random()*(b-a+1));
                }
                X[i] = (int)(a + Math.random()*(b-a+1));
            }
        }
        //--------------------------------------------
        public static void main(String args[]) {
    
      LoadData();
            MultiplyAX2();
            ViewData2();        
        }
    
    }