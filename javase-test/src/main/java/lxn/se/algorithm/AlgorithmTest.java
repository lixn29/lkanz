package lxn.se.algorithm;

/**
 * 算法
 */
public class AlgorithmTest {
    public static void main(String[] args) {
        bubbleSort();
    }

    /**
     * 冒泡排序
     */
    public static void bubbleSort() {
        int[] arr = {5, 3, 4, 1, 2};
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j + 1] > arr[j]) {
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println("a[" + i + "]=" + arr[i]);
        }
    }

}
