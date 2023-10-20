import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        //Especifica la ruta del archivo que deseas abrir
        System.out.println("Escribe la ruta del fichero:");
        String rutaArchivo = input.nextLine();


        FileInputStream archivo = leerArchivo(rutaArchivo);

        if (archivo != null) {

            try {
                //Contraseña y clave simetrica por teclado
                System.out.println("Introduce una contraseña:");
                String password = input.nextLine();
                byte[] data = password.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(data);
                byte[] key = Arrays.copyOf(hash, 192 / 8);
                SecretKey clau = new SecretKeySpec(key, "AES");


                // Lee el contenido del archivo
                byte[] bytesOriginal = new byte[archivo.available()];
                archivo.read(bytesOriginal);

                String contenido = new String(bytesOriginal, "UTF-8");
                System.out.println("Contenido del archivo es: " + contenido);

                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

                boolean cifrar = cifrarDescifrar(rutaArchivo);
                if (!cifrar) {
                    System.out.println("Entrando en modo cifrado...");

                    cipher.init(Cipher.ENCRYPT_MODE, clau);

                    byte[] bytesCifrados = cipher.doFinal(bytesOriginal);
                    String archivoCifrado = rutaArchivo + ".aes";
                    FileOutputStream archivoSalida = new FileOutputStream(archivoCifrado);
                    archivoSalida.write(bytesCifrados);
                    archivoSalida.close();

                    System.out.println("Archivo cifrado con éxito en: " + archivoCifrado);
                    System.out.println("El contenido cifrado es: " + bytesCifrados);

                } else {
                    System.out.println("Entrando en modo descifrado...");

                    try {
                        cipher.init(Cipher.DECRYPT_MODE, clau);

                        // Descifrar el archivo
                        byte[] bytesDescifrados = cipher.doFinal(bytesOriginal);

                        // Guardar el archivo descifrado
                        String archivoDescifrado = rutaArchivo.substring(0, rutaArchivo.length() - 4);
                        FileOutputStream archivoSalida = new FileOutputStream(archivoDescifrado);
                        archivoSalida.write(bytesDescifrados);
                        archivoSalida.close();

                        System.out.println("Archivo descifrado con éxito en: " + archivoDescifrado);
                        System.out.println("El contenido descifrado es: " + new String(bytesDescifrados, "UTF-8"));
                    } catch (Exception e) {
                        System.out.print("La contraseña que has introducido no es correcta...");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static FileInputStream leerArchivo(String rutaArchivo) {

        while (true) {
            try {
                // Crea un objeto File con la ruta del archivo
                FileInputStream in = new FileInputStream(rutaArchivo);
                return in;
            } catch (Exception FileNotFoundException) {
                System.out.println("Archivo no encontrado... Introduce uno válido:");
                rutaArchivo = input.nextLine(); // Solicitar una nueva ruta
            }
        }
    }

    public static boolean cifrarDescifrar(String archivoNombre) {

        return archivoNombre.endsWith(".aes");

    }
}