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

        //Loop para que pregunte hasta que se introduzca un fichero que existe
        FileInputStream archivo = null;
        while (archivo == null) {
            try {
                archivo = new FileInputStream(rutaArchivo);
            } catch (Exception FileNotFoundException) {
                System.out.println("Archivo no encontrado... Escribe uno valido");
                rutaArchivo = input.nextLine();

            }
        }
        try {
            //Contraseña y clave simetrica por teclado
            System.out.println("Introduce la contraseña:");
            String password = input.nextLine();
            byte[] data = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            byte[] key = Arrays.copyOf(hash, 192 / 8);
            SecretKey clau = new SecretKeySpec(key, "AES");


            // Crea un array de bytes del tamaño del archivo y almacena ahi el archivo leyendolo
            byte[] bytesOriginal = new byte[archivo.available()];
            archivo.read(bytesOriginal);

            String contenido = new String(bytesOriginal, "UTF-8");
            System.out.println("Contenido del archivo es: " + contenido);

            //Generar Cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            boolean cifrar = cifrarDescifrar(rutaArchivo);
            if (!cifrar) {
                System.out.println("Entrando en modo cifrado...");
                cifrar(cipher, clau, rutaArchivo, bytesOriginal);

            } else {
                System.out.println("Entrando en modo descifrado...");

                try {
                    descifrar(cipher, clau, rutaArchivo, bytesOriginal);

                } catch (Exception e) {
                    System.out.print("La contraseña que has introducido no es correcta...");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cifrarDescifrar(String archivoNombre) {

        return archivoNombre.endsWith(".aes");
    }

    public static void cifrar(Cipher cipher, SecretKey clau, String rutaArchivo, byte[] bytesOriginal) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, clau);

        // Cifrar el archivo
        byte[] bytesCifrados = cipher.doFinal(bytesOriginal);

        //Guardar el archivo cifrado
        String archivoCifrado = rutaArchivo + ".aes";
        FileOutputStream archivoSalida = new FileOutputStream(archivoCifrado);
        archivoSalida.write(bytesCifrados);
        archivoSalida.close();

        System.out.println("Archivo cifrado con éxito en: " + archivoCifrado);
        System.out.println("El contenido cifrado es: " + new String(bytesCifrados, "UTF-8"));

        sobreescribirBorrar(bytesOriginal, rutaArchivo);
    }

    public static void descifrar(Cipher cipher, SecretKey clau, String rutaArchivo, byte[] bytesOriginal) throws Exception {
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

        sobreescribirBorrar(bytesOriginal, rutaArchivo);
    }

    public static void sobreescribirBorrar(byte[] bytesOriginal, String rutaArchivo) throws Exception {
        // Sobreescribir con 0s el archivo original
        Arrays.fill(bytesOriginal, (byte) 0);
        FileOutputStream archivoOriginalSalida = new FileOutputStream(rutaArchivo);
        archivoOriginalSalida.write(bytesOriginal);
        archivoOriginalSalida.close();

        //Eliminar archivo original
        File archivoOriginal = new File(rutaArchivo);
        archivoOriginal.delete();

        System.out.println("El archivo original se ha sobreescrito con 0s y borrado del sistema...");

    }
}