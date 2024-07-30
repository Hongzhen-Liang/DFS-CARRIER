package cn.sinscry.common.sort.Service;

public class HeapSort {
    private static void  heapInsert(int[] heap, int index){
        while(heap[index]>heap[(index-1)/2]){
            swap(heap, index, (index-1)/2);
            index = (index-1)/2;
        }
    }

    private static void swap(int[] heap, int i, int j){
        int tmp = heap[i];
        heap[i]=heap[j];
        heap[j]=tmp;
    }
    public static int heapPop(int[] heap, int heapSize){
        int ans = heap[0];
        swap(heap,0,--heapSize);
        heapify(heap, heapSize);
        return ans;
    }

    public static void heapify(int[] heap, int heapSize){
        for(int i=0;i<heapSize;i++){
            heapInsert(heap, i);
        }
    }

    public static void heapSort(int[] arr, int heapSize){
        HeapSort.heapify(arr, heapSize);
        for(int i=heapSize;i>0;i--){
            HeapSort.heapPop(arr,i);
        }
    }
}
