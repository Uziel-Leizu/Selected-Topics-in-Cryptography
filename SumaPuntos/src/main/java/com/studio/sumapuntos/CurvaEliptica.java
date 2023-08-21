package com.studio.sumapuntos;

import static java.lang.Math.pow;
import java.util.ArrayList;

public class CurvaEliptica {

    int a, b, p;
    boolean primo = false;
    int[] x;
    double[] y1;
    double[] y2;
    double[] evaluado;
    double[] resCuad;

    public CurvaEliptica(int a, int b, int p) {
        evaluado = evaluar(a, b, p);
        resCuad = residuosCuadraticos(p);
        x = puntosX(evaluado, resCuad);
        y1 = new double[x.length];
        y2 = new double[x.length];
        y1 = new double[x.length];
        y2 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            int c = 0;
            for (int j = 0; j < p; j++) {
                double operacion = (pow(j, 2) % p);
                if (operacion == evaluado[x[i]]) {
                    if (c == 0) {
                        y1[i]=j;
                        c=1;
                    } else {
                        y2[i]=j;
                        c=0;
                    }
                }
            }
        }

    }

    public double[] evaluar(int a, int b, int p) {
        double Res[];
        Res = new double[p];
        System.out.println("--------------------------------------------------");
//        System.out.println("AQUI EVALUAMOS LA FUNCION DE LA CURVA ELIPTICA");
        for (int x = 0; x <= p - 1; x++) {
//            System.out.println("--------------------------------------");
//            System.out.println("para x = " + x + "\nx^3+ax+b mod p :");
//            System.out.println(x + "^3+" + a + "*" + x + "+" + b + " mod " + p);
            double potencia = pow(x, 3);
            //System.out.println("Potencia: " + potencia);
            double res = (potencia + (a * x) + b) % p;
//            System.out.println("Resultado: " + res);
            Res[x] = res;
//            System.out.println("---------------------------------------");
        }
        return Res;
    }

    public double[] burbuja(double[] array) {
        double aux = 0;
        int i, j;
        for (i = 0; i < array.length; i++) {
            for (j = 0; j < array.length - i - 1; j++) {
                if (array[j + 1] < array[j]) {
                    aux = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = aux;
                }
            }
        }
        return array;
    }

    public double[] residuosCuadraticos(int p) {
        double residuos[];
        double residuosFiltrados[];
        double potencia;
        residuos = new double[p];

        for (int i = 1; i < p; i++) {
            potencia = pow(i, 2);
            residuos[i] = potencia % p;
            //    System.out.println("residuo cuadratico [" + i + "]: " + residuos[i]);
        }
        residuosFiltrados = new double[(residuos.length / 2) + 1];
        //System.out.println("--------------------------------------------------");
        for (int i = 1; i <= (residuos.length / 2); i++) {
            residuosFiltrados[i] = residuos[i];
            //    System.out.println("residuo cuadratico [" + i + "]: " + residuosFiltrados[i]);
        }
        residuosFiltrados = burbuja(residuosFiltrados);
        //System.out.println("------------------------------------------------------");
        for (int i = 1; i < residuosFiltrados.length; i++) {
            //    System.out.println("residuo cuadratico [" + i + "]: " + residuosFiltrados[i]);
        }
        return residuosFiltrados;
    }

    public static boolean esPrimo(int numero) {
        // El 0, 1 y 4 no son primos
        if (numero == 0 || numero == 1 || numero == 4) {
            return false;
        }
        for (int x = 2; x < numero / 2; x++) {
            // Si es divisible por cualquiera de estos números, no
            // es primo
            if (numero % x == 0) {
                return false;
            }
        }
        // Si no se pudo dividir por ninguno de los de arriba, sí es primo
        return true;
    }

    public static int[] puntosX(double Res[], double ResCuad[]) {
        ArrayList<Integer> X = new ArrayList<Integer>();
        for (int i = 0; i < Res.length; i++) {
            for (int j = 1; j < ResCuad.length; j++) {
                if (Res[i] == ResCuad[j]) {
                    X.add(i);
                }
            }
        }
        //System.out.println(X);
        int x[];
        x = new int[X.size()];
        for (int i = 0; i < X.size(); i++) {
            x[i] = X.get(i);
        }
        return x;
    }

    public void puntosY1() {

        for (int i = 0; i < x.length; i++) {
            int c = 0;
            for (int j = 0; j < p; j++) {
                double operacion = (pow(j, 2) % p);
                if (operacion == evaluado[x[i]]) {
                    if (c == 0) {
                        y1[i] = j;
                        c = 1;
                    } else {
                        y2[i] = j;
                        c = 0;
                    }
                }
            }
        }
    }

    public void imprimir() {
//        System.out.println("-------------------------------------------------");
//        System.out.println("TABLA CON LOS VALORES DE X & Y");
//        for(int i=0;i<y1.length;i++){
//            System.out.println("X: "+x[i]+" Y1: "+y1[i]+" Y2: "+y2[i]);
//        }
        System.out.println("--------------------------------------------------");
        System.out.println("Puntos de la curva eliptica:");
        for (int i = 0; i < y1.length; i++) {
            System.out.println("(" + x[i] + "," + (int) y1[i] + ") (" + x[i] + "," + (int) y2[i] + ")");
        }
        System.out.println("(Punto al Infinito)");
    }
}
