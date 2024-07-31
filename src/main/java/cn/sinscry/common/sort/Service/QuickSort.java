package cn.sinscry.common.sort.Service;

import cn.sinscry.common.utils.ArrayUtils;

import java.util.Random;

public class QuickSort {
    public static void Sort(int[] arr){
        quickSort(arr, 0, arr.length-1);
    }

    private static void quickSort(int[] arr, int left, int right){
        if(left<right){
            int pivot = partition(arr, left, right);
            quickSort(arr, left, pivot-1);
            quickSort(arr, pivot+1, right);
        }
    }

    private static int partition(int[] arr, int left, int right){
        int pivotValue = arr[left];
        while(left<right){
            while(left<right && arr[right]>=pivotValue){
                right--;
            }
            arr[left] = arr[right];
            while(left<right && arr[left]<=pivotValue){
                left++;
            }
            arr[right] = arr[left];
        }
        arr[left]=pivotValue;
        return left;
    }
}
