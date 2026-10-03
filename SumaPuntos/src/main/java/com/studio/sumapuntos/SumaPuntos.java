package com.studio.sumapuntos;

import java.util.Scanner;
import static java.lang.Math.pow;

/**
 *
 * @author Leizu
 */
public class SumaPuntos {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int a, b, p;
        System.out.println("Bienvenido al programa para calcular suma de puntos en Curvas Elipticas");
        System.out.println("Programa hecho por Leizu");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Primero ingrese los datos de la curva eliptica a trabajar");
        System.out.print("Ingrese el valor de a: ");
        a = scan.nextInt();
        System.out.print("Ingrese el valor de b: ");
        b = scan.nextInt();
        System.out.print("Ingrese el valor de p: ");
        p = scan.nextInt();
        CurvaEliptica CE = new CurvaEliptica(a, b, p);
        CE.imprimir();
//        System.out.println("----------------------------------------------------------------------------");
//        System.out.println("Vamos a hacer suma de puntos P+Q = R");
//        System.out.println("----------------------------------------------------------------------------");
//        System.out.println("Valores de P");
//        System.out.print("Ingresa el valor de x1: ");
//        int x1 = scan.nextInt();
//        System.out.print("Ingresa el valor de y1: ");
//        int y1 = scan.nextInt();
//        System.out.println("----------------------------------------------------------------------------");
//        System.out.println("Valores de P");
//        System.out.print("Ingresa el valor de x2: ");
//        int x2 = scan.nextInt();
//        System.out.print("Ingresa el valor de y2: ");
//        int y2 = scan.nextInt();
//        System.out.println("----------------------------------------------------------------------------");
//        if (x1 == x2 && y1 == y2) {
//            DobPun(x2, y2, a, p);
//        } else {
//            SumPun(x1, y1, x2, y2, p);
//        }
        System.out.println("Ingresa un punto para determinar si es generador ");
        System.out.print("Ingresa x: ");
        int gX = scan.nextInt();
        System.out.print("Ingresa y: ");
        int gY = scan.nextInt();
        boolean esG = esGenerador(gX, gY, p, a, b, CE);
        System.out.println("es punto generador: "+esG);
    }

    public static int[] DobPun(int x, int y, int a, int p) {
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
        System.out.println("x3= " + x3 + " y3= " + y3);
        if (y3 < 0) {
            y3 = p + y3;
        }
        if (x3 < 0) {
            x3 = p + x3;
        }
        int par[] = new int[2];
        par[0] = x3;
        par[1] = y3;
        return par;
    }

    public static int[] SumPun(int x1, int y1, int x2, int y2, int p) {
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
        y3 = y3%p;
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

    public static boolean esGenerador(int x, int y, int p, int a, int b, CurvaEliptica ce) {
        int xAux = x;
        int yAux = y;
        int par[] = new int[2];
        int cont = 0;
        for (int i = 0; i < ce.x.length; i++) {
            if (x == xAux && y == yAux) {
                par=DobPun(xAux, yAux, a, p);
            } else {
                par=SumPun(x, y, xAux, yAux, p);
            }
            xAux = par[0];
            yAux = par[1];
            for(int j=0;j<ce.x.length;j++){
                if(xAux==ce.x[j]){
                    if(yAux==(int)ce.y1[j]){
//                        System.out.println("Punto encontrado");
                        cont++;
                    }else if(yAux==(int)ce.y2[j]){
//                        System.out.println("Punto encontrado");
                        cont++;
                    }
                }
            }
        }
        System.out.println("Puntos generados "+cont);
        if (cont == ce.x.length){
            return true;
        }else
        return false;
    }

}
