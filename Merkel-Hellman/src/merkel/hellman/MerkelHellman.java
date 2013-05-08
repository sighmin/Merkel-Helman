package merkel.hellman;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author simon
 */
public class MerkelHellman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Parse commandline args & dispatch function
        if (args.length < 1) {
            help();
        } else {
            // Join args in a string
            String arguments = "";
            for (String s : args) {
                arguments += s + " ";
            }

            // Dispatch function
            if (arguments.contains("encrypt")) {
                encrypt();
            } else if (arguments.contains("decrypt")) {
                decrypt();
            } else if (arguments.contains("help")) {
                help();
            } else if (arguments.contains("test")) {
                test();
            }
        }
    }

    public static void test() {
        // init
        Crypto crypto = new Crypto();
        // test
        boolean pass = crypto.test();

        U.p("\n\n\n======================");
        if (pass) {
            U.p(":) P A S S E D!");
        } else {
            U.p(":( F A I L E D!");
        }
    }

    public static void help() {
        System.out.println("Merkel-Hellman Usage:\n"
                + "help\n"
                + "  java -jar Merkel-Hellman.jar help\n"
                + "encrypt\n"
                + "  java -jar Merkel-Hellman.jar encrypt < plaintext\n"
                + "decrypt\n"
                + "  java -jar Merkel-Hellman.jar decrypt < ciphertext");
    }

    // java -jar Merkel-Hellman.jar encrypt < plaintext > ciphertext
    public static void encrypt() {
        Crypto crypto = new Crypto();
        byte[] block = new byte[2];
        int arrLength = 1;
        int[] encryptedNum = new int[arrLength];
        int bytesRead = 0;

        // Do encryption
        try {
            //Consideration: could not use scanner, as scanner uses spaces as default delimiter, which will then mess up the actual input stream
            bytesRead = System.in.read(block); //read 16 bytes in from Standard.in into the block array
            while (bytesRead == 2) {
                encryptedNum[arrLength - 1] = crypto.encryptBlock(block);

                // initialise temp array and copy current array to temp
                int[] temp = new int[++arrLength];
                System.arraycopy(encryptedNum, 0, temp, 0, encryptedNum.length);
                encryptedNum = temp;

                // Read next block
                bytesRead = System.in.read(block);
            }
            
            // this prints out to std.out the int[] of encrypted numbers
            for (int i = 0; i < encryptedNum.length - 1; ++i) {
                if (i == encryptedNum.length - 2) { //last element in array
                    System.out.print(encryptedNum[i]);
                } else {
                    System.out.print(encryptedNum[i] + "+");
                }
            }
        } catch (IOException e) {
            //System.out.println("I/O Exception caught.");
        }
    }

    // java -jar Merkel-Hellman.jar decrypt < ciphertext > decipheredText
    public static void decrypt() {
        Crypto crypto = new Crypto();
        Scanner scan = null;
        try {
            scan = new Scanner(System.in);
            String input = "";
            while (scan.hasNext()) {
                input += scan.nextLine();
            }
            
            String[] encryptedNum = input.split(",");
            int[] decryptTheseNumbers = new int[encryptedNum.length];
            
            // parse string representation of encrypted numbers to int for decryption
            for(int i = 0; i < encryptedNum.length; ++i) {
                decryptTheseNumbers[i] = Integer.parseInt(encryptedNum[i]);
            }
            
            byte[] decryptedData = crypto.decrypt(decryptTheseNumbers);
            String decryptedStr = new String(decryptedData);
            
            System.out.println(decryptedStr);
        } finally {
            if(scan != null)
                scan.close();
        }
    }
}
