package com.studio.sumapuntos;

public class MaximoComunDivisor {

    public static int MCD(int a, int b) {
        int temporal;//Para no perder b
        while (b != 0) {
            temporal = b;
            b = a % b;
            a = temporal;
        }
        return a;
    }
}
