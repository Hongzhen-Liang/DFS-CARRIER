package cn.sinscry.common.sort.Service;

import cn.sinscry.common.utils.ArrayUtils;

public class HeapSort {
    private static void  heapInsert(int[] heap, int index){
        while(heap[index]>heap[(index-1)/2]){
            ArrayUtils.swap(heap, index, (index-1)/2);
            index = (index-1)/2;
        }
    }


    public static int heapPop(int[] heap, int heapSize){
        int ans = heap[0];
        ArrayUtils.swap(heap,0,--heapSize);
        heapify(heap, heapSize);
        return ans;
    }

    public static void heapify(int[] heap, int heapSize){
        for(int i=0;i<heapSize;i++){
            heapInsert(heap, i);
        }
    }

    public static void Sort(int[] arr){
        HeapSort.heapify(arr, arr.length);
        for(int i=arr.length;i>0;i--){
            HeapSort.heapPop(arr,i);
        }
    }
}
