package diffie.hellman;

public class AEE {
    //creditos a https://www.makingcode.dev/2015/06/implementacion-de-euclides-extendido-en.html#:~:text=El%20algoritmo%20de%20Euclides%20extendido,de%20la%20computación%20entre%20otras.
    //Algoritmo creado por Andrés Esquivel 
    public static long[] euclidesExtendido(int a, int b) {
        long[] resp = new long[3];
        long x = 0, y = 0, d = 0;
        if (b == 0) {
            resp[0] = a;
            resp[1] = 1;
            resp[2] = 0;
        } else {
            long x2 = 1, x1 = 0, y2 = 0, y1 = 1;
            long q = 0, r = 0;
            while (b > 0) {
                q = (a / b);
                r = a - q * b;
                x = x2 - q * x1;
                y = y2 - q * y1;
                a = b;
                b = (int) r;
                x2 = x1;
                x1 = x;
                y2 = y1;
                y1 = y;
            }
            resp[0] = a;
            resp[1] = x2;
            resp[2] = y2;
        }
        return resp;
    }
}