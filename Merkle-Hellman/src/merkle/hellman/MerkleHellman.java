package merkle.hellman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 */
public class MerkleHellman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Parse commandline args & dispatch function
        // Join args in a white space delimited string
        String arguments = "";
        for (String s : args) {
            arguments += s + " ";
        }

        // Dispatch function
        if (arguments.contains("encrypt") || arguments.contains("--encrypt") || arguments.contains("-e")) {
            if (!arguments.contains("public.key")) {
                System.out.println("You must encrypt with a public key file called \"public.key\"");
            } else {
                encrypt(args);
            }
        } else if (arguments.contains("decrypt") || arguments.contains("--decrypt") || arguments.contains("-d")) {
            if (!arguments.contains("private.key")) {
                System.out.println("You must decrypt with a private key file called \"private.key\"");
            } else {
                decrypt(args);
            }
        } else if (arguments.contains("keygen") || arguments.contains("--keygen") || arguments.contains("-k")){
            keygen();
        } else if (arguments.contains("help") || arguments.contains("--help") || arguments.contains("-h")) {
            usage();
        } else {
            usage();
        }
    }

    public static void usage() {
        System.out.println(
                "Merkel-Hellman Usage:\n"
                + "help\n"
                + "  java -jar Merkle-Hellman.jar help/--help/-h\n"
                + "keygen\n"
                + "  java -jar Merkle-Hellman.jar keygen/--keygen/-k\n"
                + "encrypt\n"
                + "  java -jar Merkle-Hellman.jar encrypt/--encrypt/-e public.key < plain > encrypted\n"
                + "decrypt\n"
                + "  java -jar Merkle-Hellman.jar decrypt/--decrypt/-d private.key < encrypted > decrypted");
    }

    public static void encrypt(String[] args) {
        // Read key file
        String filename = "";
        String keyString = "";
        for (String s : args) {
            if (s.equals("public.key")){ filename = s; }
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            keyString = bf.readLine();
        } catch (IOException e) {
            System.exit(2);
        }
        
        // Init crypto object with key
        String[] key = keyString.split(",");
        Crypto crypto = new Crypto(key);
        int blocksize = 2;
        int bytesRead = 0;
        byte[] block = new byte[blocksize];

        try {
            U.p(block);
            do {
                bytesRead = System.in.read(block, 0, block.length);
                if (bytesRead == 2) {
                    System.out.println(crypto.encryptBlock(block));
                } else if (bytesRead > 0) { // Pad input block if not large enough for block
                    for (int i = bytesRead; i < blocksize; ++i) {
                        block[i] = 0;
                    }
                    System.out.println(crypto.encryptBlock(block));
                }
            } while (bytesRead > 0);
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
        } catch (IOException e) {
            System.exit(2);
        }
        String[] keydata = keyString.split(";");
        String[] key = keydata[2].split(",");
        
        // Init crypto class with private key (modulo, multiplier, super increasing sequence)
        Scanner scan = null;
        Crypto crypto = new Crypto(keydata[0], keydata[1], key);
        
        // read in input from standard in containing the cipher text
        try {            
            scan = new Scanner(System.in);
            String input;
            while (scan.hasNext()) {
                input = scan.nextLine();
                long[] encryptedint = new long[1];
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
    }
}
