package proyecto1cripto;

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
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Bob {

    public static void main(String[] args) {
        final String IP = "127.0.0.1";
        Scanner scan = new Scanner(System.in);
        final int port = 45000;
        ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
        String mensaje = "Hola Mundo";
        try {
            //Añadimos el proveedor BoutyCastle para utilizar el algritmo ECDH
            //Dejo el link a la pagina de BouncyCastle para su descarga https://www.bouncycastle.org/latest_releases.html
            /*Para instalar las librerias necesarias son:
            bc-noncert-1.0.2.4.javadoc.jar
            bcjmail-jdk18on-173.jar
            bcmail-jdk18on-173.jar
            bcpg-jdk18on-173.jar
            bcpkix-jdk18on-173.jar
            bcprov-jdk18on-173.jar
            bctest-jdk18on-173.jar
            bctls-jdk18on-173.jar
            */
            Security.addProvider(new BouncyCastleProvider());
            Socket socket = new Socket(IP, port);
            System.out.println("Esperando a que el servidor conteste");
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDH", "BC");
            g.initialize(spec, new SecureRandom());
            KeyPair keyPair = g.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            //Flujos de datos
            InputStream is = socket.getInputStream();

            OutputStream os = socket.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(os);
            ObjectInputStream ois = new ObjectInputStream(is);

            //Recibimos la llave publica de Alice
            PublicKey alice = (PublicKey) ois.readObject();
            System.out.println(alice.toString());
            //Enviamos la llave publica
            oos.writeObject(publicKey);
            oos.flush();

            //Genera un IV (Vector de inicializacion) aleatorio para el cifrado AES
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            //Generamos la llave de secreto compartido con la llave que recibimos en el socket
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
            keyAgreement.init(privateKey); //Usamos la llave privada de bob
            keyAgreement.doPhase(alice, true); //Usamos la llave publica de alice
            byte[] sharedSecretBytes = keyAgreement.generateSecret(); //Generamos la llave en bytes 
            SecretKey secretkey = new SecretKeySpec(sharedSecretBytes, "AES"); //Obtenemos la llave de sesion para AES
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); //Creamos el cifrador con el algoritmo y modo de operacion a usar

            //Recibimos el texto cifrado
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("Esperando mensaje...");
            byte[] buffer = new byte[1024]; // Tamaño del buffer para la lectura
            int bytesRead;
            bytesRead = is.read(buffer);
            System.out.println("------------------------------------------------------------------------------------");
            byte[] mensajeRBytes = new byte[bytesRead];
            System.arraycopy(buffer, 0, mensajeRBytes, 0, bytesRead);
            byte[] ivRecibido = new byte[12];
            System.out.println("Tamanio de bytes recibidos: " + mensajeRBytes.length);
            System.arraycopy(mensajeRBytes, 0, ivRecibido, 0, ivRecibido.length);
            byte[] cipherText = new byte[mensajeRBytes.length - ivRecibido.length];
            System.arraycopy(mensajeRBytes, 12, cipherText, 0, mensajeRBytes.length - ivRecibido.length);

            cipher.init(Cipher.DECRYPT_MODE, secretkey, new GCMParameterSpec(128, ivRecibido));
            byte[] decryptedData = cipher.doFinal(cipherText);
            String plaintext = new String(decryptedData, StandardCharsets.UTF_8);
            System.out.println("Mensaje recibido: "+plaintext);
            
            //-----------------------------------------------------------------------------------------------
            System.out.println("-----------------------------------------------------------------------------");
            System.out.print("Mensaje a enviar: ");
            mensaje=scan.nextLine();
            System.out.println("-----------------------------------------------------------------------------");
            //-----------------------------------------------------------------------------------------------
            
            cipher = Cipher.getInstance("AES/GCM/NoPadding"); //Creamos el cifrador con el algoritmo y modo de operacion a usar
            //Iniciamos el cifrador en modo Cifrar con la llave de sesion
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, new GCMParameterSpec(128,iv));
            //Cifrar el Texto Plano
            byte[] cipherTextE = cipher.doFinal(mensaje.getBytes());
            //concatena el iv al texto cifrado
            byte[] encryptedText = new byte[iv.length + cipherTextE.length];
            System.arraycopy(iv, 0, encryptedText, 0, iv.length);
            System.arraycopy(cipherTextE, 0, encryptedText,iv.length, cipherTextE.length);
            
            System.out.println("Enviando mensaje");
            
            os.write(encryptedText);
            os.flush();
            
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Alice.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Alice.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
