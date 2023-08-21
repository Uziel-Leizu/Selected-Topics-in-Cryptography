/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diffie.hellman;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Alice {

    public static void main(String[] args) {
        final int port = 45000;
        ServerSocket Serv = null;
        Socket socket = null;
        int a,b,p;
        int Generador[] = new int[2];
        int kprva;
        SumaPuntos sumador = new SumaPuntos();
        Scanner scan = new Scanner(System.in);
        System.out.println("Bienvenido al programa Alice");
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
        System.out.println("Elige un punto de la curva");
        System.out.print("Valor de x: ");
        Generador[0] = scan.nextInt();
        System.out.print("Valor de y: ");
        Generador[1] = scan.nextInt();
        CE.setGenerador(Generador);
        
        try {
            Serv = new ServerSocket(port);
            System.out.println("Servidor iniciado esperando conexion del cliente");
            socket = Serv.accept();
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            ObjectOutputStream oos = new ObjectOutputStream(os);
            ObjectInputStream ois = new ObjectInputStream(is);
                

            oos.writeObject(CE);
            oos.flush();
            
            System.out.println("Elige tu llave privada...");
            System.out.print("Key Priv: ");
            kprva = scan.nextInt();
            
            int aux[] = Generador;
            
            for(int i=0;i<kprva-1;i++){
                aux = sumador.sumarPuntos(Generador[0], Generador[1], aux[0], aux[1], a, p);
            }
            System.out.println(aux[0]+","+aux[1]);
            int A[] = aux;
            int B[] = new int[2];
            String ResB;
            bw.flush();
            bw.write(A[0]+","+A[1]);
            bw.newLine();
            bw.flush();
            ResB = br.readLine();
            String ResBSplit[] = ResB.split(",");
            B[0] = Integer.parseInt(ResBSplit[0]);
            B[1] = Integer.parseInt(ResBSplit[1]);
            System.out.println(B[0]+","+B[1]);
            aux = B;
            for(int i=0;i<kprva-1;i++){
                aux = sumador.sumarPuntos(B[0], B[1], aux[0], aux[1], a, p);
            }
            System.out.println(aux[0]+","+aux[1]);
            br.close();
            bw.close();
            socket.close();
            ois.close();
            oos.close();
            Serv.close();
        } catch (IOException ex) {
            Logger.getLogger(Alice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}