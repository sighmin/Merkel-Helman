package merkel.hellman;

import java.io.*;
import java.nio.charset.Charset;
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
                encrypt(args);
            } else if (arguments.contains("decrypt")) {
                decrypt(args);
            } else if (arguments.contains("keygen")){
                keygen();
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
        byte signed = -1;
        int uns = signed & 0xFF;
        byte newS = (byte)uns;
        System.out.println("uns: " + uns + " b: " + signed + " newS: " + newS);
        System.out.println("binary of uns: " + Integer.toBinaryString(uns) + "\nbinary of signed: " + Integer.toBinaryString(signed) + "\nbinary of newS: " + Integer.toBinaryString(newS));
       
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
                + "  cat plaintext | java -jar Merkel-Hellman.jar > encrypted\n"
                + "decrypt\n"
                + "  cat encrypted | java -jar Merkel-Hellman.jar > decrypted");
    }

    public static void encrypt(String[] args) {
        // Read key file
        String filename = "";
        String keyString = "";
        for (String s : args) {
            if (s.equals("public.key")){
                filename = s;
            }
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            keyString = bf.readLine();
        } catch (Exception e) {
            System.exit(2);
        }
        
        Crypto crypto = new Crypto(keyString);
        int blocksize = 2;
        // Normal 2 byte encrypt
        byte[] block = new byte[blocksize];
        //int bytesRead = System.in.read(block, 0, block.length);
        int bytesRead = 0;

        try {
            //U.p(block);
            //System.out.println();
            //System.out.println(System.getProperty("file.encoding"));
            
            bytesRead = System.in.read(block, 0, block.length);;
            while (bytesRead == 2) {
                System.out.println(crypto.encryptBlock(block));
                //bytesRead = System.in.read(block);
                bytesRead = System.in.read(block, 0, block.length);
            }
            
            // Pad input block if not large enough for block
            if (bytesRead > 0) {
                for (int i = bytesRead; i < blocksize; ++i) {
                    block[i] = 0;
                }
                System.out.println(crypto.encryptBlock(block));
            }
        } catch (IOException e) {
            System.out.println("I/O Exception caught.");
        }
    }

    public static void decrypt(String[] args) {
        // Read key file
        String filename = "";
        String keyString = "";
        for (String s : args) {
            if (s.equals("private.key")){
                filename = s;
            }
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            keyString = bf.readLine();
        } catch (Exception e) {
            System.exit(2);
        }
        String[] keydata = keyString.split(";");
        String[] key = keydata[2].split(",");
        
        Scanner scan = null;
        try {
            Crypto crypto = new Crypto(keydata[0], keydata[1], key);
            scan = new Scanner(System.in);
            String input;
            while (scan.hasNext()) {
                input = scan.nextLine();
                int[] encryptedint = new int[1];
                encryptedint[0] = Integer.parseInt(input);
                byte[] decryptedData = crypto.decrypt(encryptedint);
                System.out.write(decryptedData, 0, decryptedData.length);
            }
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }
    
    public static void keygen(){
        Crypto crypto = new Crypto();
        crypto.keygen();
        
        //int[] publickey = crypto.getPublicKey();
        //int[] privatekey = crypto.getPrivateKey();
        
        // Write keys to files.
    }
}
