package com.studio.diffiehellmancurvaeliptica;

import static java.lang.Math.pow;

public class SumaPuntos {

    public int[] sumarPuntos(int x1, int y1, int x2, int y2, int a, int p) {
        int par[] = new int[2];
        if (x1 == x2 && y1 == y2) {
            par = DobPun(x1, y1, a, p);

        } else {
            par = sumPun(x1, y1, x2, y2, p);
        }
        return par;
    }

    public int[] sumPun(int x1, int y1, int x2, int y2, int p) {
        System.out.println("Vamos a sumar los puntos");
        int m;
        int part1 = y2 - y1;
        int part2 = x2 - x1;
        long inversoMul[];
        part2 = part2 % p;
        part1 = part1 % p;
        if (part2 < 0) {
            part2 = p + part2;
        }
        if (part1 < 0) {
            part1 = p + part1;
        }
        //inverso multiplicativo de p2
        inversoMul = AEE.euclidesExtendido(part2, p);
        part2 = (int) inversoMul[1];

        //System.out.println("parte 2= " + part2);
        m = (part1 * part2) % p;
        int x3 = ((int) pow(m, 2)) - x1 - x2;
        x3 = x3 % p;
        int y3 = m * (x1 - x3) - y1;
        y3 = y3 % p;
        if (y3 < 0) {
            y3 = p + y3;
        }
        if (x3 < 0) {
            x3 = p + x3;
        }
        System.out.println("x3= " + x3 + " y3= " + y3);
        int par[] = new int[2];
        par[0] = x3;
        par[1] = y3;
        return par;
    }

    public int[] DobPun(int x, int y, int a, int p) {
        System.out.println("Vamos a doblar el punto");
        int m;
        int part1 = (3 * ((int) pow(x, 2)) + a);
        int part2 = 2 * y;
        long inversoMul[];
        //inverso multiplicativo de p2
        //System.out.println(part2);
        part2 = part2 % p;
        part1 = part1 % p;
        if (part2 < 0) {
            part2 = p + part2;
        }
        if (part1 < 0) {
            part1 = p + part1;
        }
        inversoMul = AEE.euclidesExtendido(part2, p);
        part2 = (int) inversoMul[1];

        //System.out.println(part2);
        m = (part1 * part2) % p;
        //System.out.println(m);
        int x3 = ((int) pow(m, 2)) - x - x;
        x3 = x3 % p;
        int y3 = m * (x - x3) - y;
        y3 = y3 % p;

        if (y3 < 0) {
            y3 = p + y3;
        }
        if (x3 < 0) {
            x3 = p + x3;
        }
        System.out.println("x3= " + x3 + " y3= " + y3);
        int par[] = new int[2];
        par[0] = x3;
        par[1] = y3;
        return par;
    }
}
