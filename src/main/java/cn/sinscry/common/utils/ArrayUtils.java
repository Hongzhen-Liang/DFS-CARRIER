package cn.sinscry.common.utils;

import java.util.Random;

public class ArrayUtils {
    public static void swap(int[] heap, int i, int j){
        int tmp = heap[i];
        heap[i]=heap[j];
        heap[j]=tmp;
    }

    public static int[] genArr( int size){
        Random random = new Random();
        int[] arr = new int[size];
        for(int i=0;i<size;i++){
            arr[i] = random.nextInt(0,size);
        }
        return arr;
    }

    public static void printArr(int[] arr){
        for(int i=0;i<arr.length-1;i++){
            System.out.printf("%d,",arr[i]);
        }
        System.out.println(arr[arr.length-1]);
    }
}
