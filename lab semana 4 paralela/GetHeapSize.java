public class GetHeapSize {
    public static void main(String[] args) {
        long heapsize = Runtime.getRuntime().totalMemory();
        System.out.println("heapsize is :: " + heapsize);
    }
} 
