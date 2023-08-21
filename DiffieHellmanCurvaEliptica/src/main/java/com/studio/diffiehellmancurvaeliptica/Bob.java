package com.studio.diffiehellmancurvaeliptica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bob {

    public static void main(String[] args) throws ClassNotFoundException {
        final String IP = "127.0.0.1";
        Scanner scan = new Scanner(System.in);
        final int port = 45000;
        SumaPuntos sumador = new SumaPuntos();
        int kprvb;
        try {
            Socket socket = new Socket(IP, port);
            System.out.println("Esperando a que el servidor conteste");

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(os);
            
            CurvaEliptica CE = (CurvaEliptica) ois.readObject();
            CE.imprimir();
        
            int Generador[]=CE.Generador;
            int a = CE.a;
            int p = CE.p;
            System.out.println("Elige tu llave privada...");
            System.out.print("Key Priv: ");
            kprvb = scan.nextInt();
            
            int aux[] = Generador;
            
            System.out.println(Generador[0]+"");
            for(int i=0;i<kprvb-1;i++){
                aux = sumador.sumarPuntos(Generador[0], Generador[1], aux[0], aux[1], a, p);
            }
            System.out.println(aux[0]+","+aux[1]);
            int B[] = aux;
            int A[] = new int[2];
            
            System.out.println("Esperando a leer entero");
            String ResA = br.readLine();
            bw.flush();
            bw.write(B[0]+","+B[1]);
            bw.newLine();
            bw.flush();
            //System.out.println(ResA);
            String ResASplit[] = ResA.split(",");
            A[0] = Integer.parseInt(ResASplit[0]);
            A[1] = Integer.parseInt(ResASplit[1]);
            System.out.println(A[0]+","+A[1]);
            aux = A;
            for(int i=0;i<kprvb-1;i++){
                aux = sumador.sumarPuntos(A[0], A[1], aux[0], aux[1], a, p);
            }
            System.out.println(aux[0]+","+aux[1]);
            br.close();
            bw.close();
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
