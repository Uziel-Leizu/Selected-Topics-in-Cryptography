package practica6cripto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ECDSAPractica {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int opcion = 0;
        boolean salir = false;
        System.out.println("---------------------------------------------------------");
        System.out.println("Programa para firmar archivos...");
        System.out.println("Grupo: 7CM2");
        System.out.println("---------------------------------------------------------");
        System.out.println("Bienvenido " + System.getProperty("user.name"));
        while (!salir) {
            System.out.println("---------------------------------------------------------");
            System.out.println("Selecciona una de las siguientes opciones...");
            System.out.println("\n1) Generar Llaves.");
            System.out.println("2) Firmar archivo.");
            System.out.println("3) Verificar firma.");
            System.out.println("4) Salir");
            System.out.print("\nR: ");
            opcion = scan.nextInt();

            if (opcion == 1) {
                GenerarLlaves();
            } else if (opcion == 2) {
                firmar();
            } else if (opcion == 3) {
                Verificar();
            } else {
                salir = true;
            }
        }
    }

    public static void GenerarLlaves() {
        try {
            Scanner scan = new Scanner(System.in);

            System.out.println("-----------------------------------------------------");
            System.out.println("Generando las llaves asimetricas...");
            System.out.println("-----------------------------------------------------");
            //Instanciamos el generador de llaves asimetricas para ECDSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");

            //Especifica la curva eliptica a utilizar
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");

            //Inicializamos el generador de claves con la especificacion de la curva
            keyGen.initialize(ecSpec);

            //Genera el par de claves
            KeyPair keyPair = keyGen.generateKeyPair();

            //Obtener la clave privada y publica
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            System.out.println("Escriba el nombre para guardar la llave... ");
            System.out.print("R: ");
            String nombre = scan.nextLine();
            System.out.println("-----------------------------------------------------");
            System.out.println("Elija la carpeta donde guardar su par de llaves... ");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int r = fileChooser.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                System.out.println("Usted selecciono la carpeta: " + selectedFolder.getAbsolutePath());

                //Guardamos la clave privada en un archivo
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
                File llavePrivada = new File(selectedFolder.getAbsolutePath() + "\\" + nombre + "PrKey.pem");
                FileOutputStream fos = new FileOutputStream(llavePrivada);
                fos.write(privateKeySpec.getEncoded());
                fos.flush();
                fos.close();
                //Guardamos la clave publica en un archivo
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
                File llavePublica = new File(selectedFolder.getAbsolutePath() + "\\" + nombre + "PuKey.pem");
                fos = new FileOutputStream(llavePublica);
                fos.write(publicKeySpec.getEncoded());
                fos.flush();
                fos.close();

            } else {
                System.out.println("No se selecciono la ubicacion ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void firmar() {
        try {
            System.out.println("-----------------------------------------------------");
            System.out.println("Seleccione la llave privada para cifrar... ");
            JFileChooser SelectKey = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de llave", "pem");
            SelectKey.setFileFilter(filter);
            int r = SelectKey.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                System.out.println("Ahora seleccione el archivo a firmar... ");
                JFileChooser SelectFile = new JFileChooser();
                int res = SelectFile.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    //Cargamos la llave privada desde el archivo
                    File FPrivKey = SelectKey.getSelectedFile();
                    FileInputStream fis = new FileInputStream(FPrivKey);
                    byte[] privateKeyBytes = fis.readAllBytes();
                    fis.close();
                    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("EC");
                    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                    //Cargamos el archivo a firmar
                    File Archivo = SelectFile.getSelectedFile();
                    fis = new FileInputStream(Archivo);
                    byte[] fileBytes = fis.readAllBytes();
                    fis.close();

                    //Calculamos la firma digital del archivo
                    Signature signature = Signature.getInstance("SHA256withECDSA");
                    signature.initSign(privateKey);

                    //Actualizamos la firma con el contenido del archivo
                    signature.update(fileBytes);

                    //Generar la firma digital
                    byte[] digitalSignature = signature.sign();

                    //Guardamos la firma digital en un archivo
                    FileOutputStream signatureOut = new FileOutputStream(Archivo.getParent() + "\\signature.bin");
                    signatureOut.write(digitalSignature);
                    signatureOut.close();
                    System.out.println("Se ha generado la firma del archivo " + Archivo.getName());
                } else {
                    System.out.println("No se selecciono ningun archivo para firmar ");
                }
            } else {
                System.out.println("No se selecciono ninguna llave");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Verificar() {
        try {
            System.out.println("-----------------------------------------------------");
            System.out.println("Seleccione la llave publica para verificar... ");
            JFileChooser SelectKey = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de llave", "pem");
            SelectKey.setFileFilter(filter);
            int r = SelectKey.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                System.out.println("Ahora seleccione el archivo firmado... ");
                JFileChooser SelectFile = new JFileChooser();
                int res = SelectFile.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Ahora seleccione la firma del archivo");
                    JFileChooser SelectFirma = new JFileChooser();
                    FileNameExtensionFilter SigFilter = new FileNameExtensionFilter("Archivo de firma digital", "bin");
                    SelectFirma.setFileFilter(SigFilter);
                    int resu = SelectFirma.showOpenDialog(null);
                    if (resu == JFileChooser.APPROVE_OPTION) {
                        //Cargamos la llave publica desde el archivo
                        File FPubKey = SelectKey.getSelectedFile();
                        FileInputStream fis = new FileInputStream(FPubKey);
                        byte[] publicKeyBytes = fis.readAllBytes();
                        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                        KeyFactory keyFactory = KeyFactory.getInstance("EC");
                        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
                        fis.close();
                        //Cargamos el archivo a verificar
                        File Archivo = SelectFile.getSelectedFile();
                        fis = new FileInputStream(Archivo);
                        byte[] fileBytes = fis.readAllBytes();
                        fis.close();
                        
                        //Cargamos la firma digital
                        File firma = SelectFirma.getSelectedFile();
                        fis = new FileInputStream(firma);
                        byte[] signatureBytes = fis.readAllBytes();
                        fis.close();
                        
                        //Inicializar Signature con la clave publica
                        Signature signature = Signature.getInstance("SHA256withECDSA");
                        signature.initVerify(publicKey);

                        //Actualizamos la firma con el contenido del archivo
                        signature.update(fileBytes);

                        //Verificar la firma
                        boolean isSignatureValid = signature.verify(signatureBytes);
                        if(isSignatureValid){
                            System.out.println("La firma es valida.");
                        }else{
                            System.out.println("La firma no es valida");
                        }
                    } else {
                        System.out.println("No se selecciono ninguna firma digital");
                    }
                } else {
                    System.out.println("No se selecciono ningun archivo para firmar ");
                }
            } else {
                System.out.println("No se selecciono ninguna llave");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
