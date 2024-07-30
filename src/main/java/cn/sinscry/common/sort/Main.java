package cn.sinscry.common.sort;

import cn.sinscry.common.sort.Service.HeapSort;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int heapSize = 100;
        int[] arr = genArr(heapSize);
        HeapSort.heapSort(arr, heapSize);
        printArr(arr, heapSize);
    }

    private static int[] genArr( int heapSize){
        Random random = new Random();
        heapSize = 100;
        int[] arr = new int[heapSize];
        for(int i=0;i<heapSize;i++){
            arr[i] = random.nextInt(0,heapSize);
        }
        return arr;
    }

    private static void printArr(int[] arr, int heapSize){
        for(int i=0;i<heapSize;i++){
            System.out.printf("%d,",arr[i]);
        }
    }
}
