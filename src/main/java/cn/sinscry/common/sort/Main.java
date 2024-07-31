package cn.sinscry.common.sort;

import cn.sinscry.common.sort.Service.HeapSort;
import cn.sinscry.common.sort.Service.QuickSort;
import cn.sinscry.common.utils.ArrayUtils;

import java.util.Arrays;

import static cn.sinscry.common.utils.ArrayUtils.printArr;

public class Main {
    public static void main(String[] args) {
        int size = 100;
        int[] arr = ArrayUtils.genArr(size);
        printArr(arr);

        int[] trueRes = arr.clone();
        Arrays.sort(trueRes);

        int[] quickSortRes = arr.clone();
        QuickSort.Sort(quickSortRes);
        assert Arrays.equals(trueRes, quickSortRes) : "quick sort is wrong";

        int[] heapSortRes = arr.clone();
        HeapSort.Sort(heapSortRes);
        assert Arrays.equals(trueRes, heapSortRes) : "heap sort is wrong";

        printArr(trueRes);
        System.out.println("All sorts are running correctly");
    }
}
