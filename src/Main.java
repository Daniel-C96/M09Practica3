import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static final Scanner input = new Scanner(System.in);
    static final int KEY_LENGTH = 192; // Tamaño de la clave en bits
    static String rutaArchivo = "";

    public static void main(String[] args) {
        System.out.println("Escribe la ruta del fichero:");
        rutaArchivo = input.nextLine();

        FileInputStream archivo = buscarArchivoExistente(rutaArchivo);

        try {
            String password = obtenerPassword();
            byte[] key = generarClaveDesdePassword(password);
            SecretKey clave = new SecretKeySpec(key, "AES");

            byte[] bytesOriginal = leerArchivo(archivo);
            System.out.println("Contenido del archivo es: " + new String(bytesOriginal, "UTF-8"));

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            if (rutaArchivo.endsWith(".aes")) {
                System.out.println("Entrando en modo descifrado...");
                descifrarYGuardarArchivo(cipher, clave, rutaArchivo, bytesOriginal);
                sobreescribirYBorrarArchivo(bytesOriginal, rutaArchivo);
            } else {
                System.out.println("Entrando en modo cifrado...");
                try {
                    cifrarYGuardarArchivo(cipher, clave, rutaArchivo, bytesOriginal);
                    sobreescribirYBorrarArchivo(bytesOriginal, rutaArchivo);
                } catch (Exception e) {
                    System.out.print("La contraseña que has introducido no es correcta...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static FileInputStream buscarArchivoExistente(String ruta) {
        FileInputStream archivo = null;
        while (archivo == null) {
            try {
                archivo = new FileInputStream(ruta);
                rutaArchivo = ruta;
            } catch (FileNotFoundException ex) {
                System.out.println("Archivo no encontrado... Escribe uno valido");
                ruta = input.nextLine();
            }
        }
        return archivo;
    }

    public static String obtenerPassword() {
        System.out.println("Introduce la contraseña:");
        return input.nextLine();
    }

    public static byte[] generarClaveDesdePassword(String password) throws Exception {
        byte[] data = password.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data);
        return Arrays.copyOf(hash, KEY_LENGTH / 8);
    }

    public static byte[] leerArchivo(FileInputStream archivo) throws IOException {
        byte[] bytesOriginal = new byte[archivo.available()];
        archivo.read(bytesOriginal);
        return bytesOriginal;
    }

    public static void cifrarYGuardarArchivo(Cipher cipher, SecretKey clave, String rutaArchivo, byte[] bytesOriginal) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] bytesCifrados = cipher.doFinal(bytesOriginal);

        String archivoCifrado = rutaArchivo + ".aes";
        try (FileOutputStream archivoSalida = new FileOutputStream(archivoCifrado)) {
            archivoSalida.write(bytesCifrados);
        }

        System.out.println("Archivo cifrado con éxito en: " + archivoCifrado);
        System.out.println("El contenido cifrado es: " + new String(bytesCifrados, "UTF-8"));
    }

    public static void descifrarYGuardarArchivo(Cipher cipher, SecretKey clave, String rutaArchivo, byte[] bytesOriginal) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, clave);
        byte[] bytesDescifrados = cipher.doFinal(bytesOriginal);

        String archivoDescifrado = rutaArchivo.substring(0, rutaArchivo.length() - 4);
        try (FileOutputStream archivoSalida = new FileOutputStream(archivoDescifrado)) {
            archivoSalida.write(bytesDescifrados);
        }

        System.out.println("Archivo descifrado con éxito en: " + archivoDescifrado);
        System.out.println("El contenido descifrado es: " + new String(bytesDescifrados, "UTF-8"));
    }

    public static void sobreescribirYBorrarArchivo(byte[] bytesOriginal, String rutaArchivo) throws IOException {
        Arrays.fill(bytesOriginal, (byte) 0);
        try (FileOutputStream archivoOriginalSalida = new FileOutputStream(rutaArchivo)) {
            archivoOriginalSalida.write(bytesOriginal);
        }

        File archivoOriginal = new File(rutaArchivo);
        if (archivoOriginal.delete()) {
            System.out.println("El archivo original se ha sobreescrito con 0s y borrado del sistema...");
        } else {
            System.out.println("No se pudo eliminar el archivo original.");
        }
    }
}
