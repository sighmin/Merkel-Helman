package merkle.hellman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import merkle.hellman.exceptions.KeyDataViolationException;
import merkle.hellman.exceptions.ParseException;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date 2013-05-21
 * @class Program entry point
 */
public class MerkleHellman {

    public static void main(String[] args) {
        // Parse commandline args & dispatch function
        // Join args in a white space delimited string
        String arguments = "";
        for (String s : args) {
            arguments += s + " ";
        }

        // Dispatch function
        try {
            if (arguments.contains("encrypt") || arguments.contains("--encrypt") || arguments.contains("-e")) {
                if (!arguments.contains("public.key")) {
                    throw new ParseException("You must encrypt with a public key file called \"public.key\"");
                }
                encrypt(args);
            } else if (arguments.contains("decrypt") || arguments.contains("--decrypt") || arguments.contains("-d")) {
                if (!arguments.contains("private.key")) {
                    throw new ParseException("You must decrypt with a private key file called \"private.key\"");
                }
                decrypt(args);
            } else if (arguments.contains("keygen") || arguments.contains("--keygen") || arguments.contains("-k")) {
                keygen();
            } else {
                usage();
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            U.p(e.getMessage());
        }
    }

    private static void usage() {
        System.out.println(
                "Merkel-Hellman Usage:\n"
                + "help\n"
                + "  java -jar Merkle-Hellman.jar [help|--help|-h]\n"
                + "keygen\n"
                + "  java -jar Merkle-Hellman.jar [keygen|--keygen|-k]\n"
                + "encrypt\n"
                + "  java -jar Merkle-Hellman.jar [encrypt|--encrypt|-e] public.key < plain > encrypted\n"
                + "decrypt\n"
                + "  java -jar Merkle-Hellman.jar [decrypt|--decrypt|-d] private.key < encrypted > decrypted");
    }

    private static void encrypt(String[] args) throws Exception {
        // Read key file
        String filename = "";
        String keyString = "";
        for (String s : args) {
            if (s.equals("public.key")) {
                filename = s;
            }
        }

        // read in the public key
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(filename));
            keyString = bf.readLine();
        } catch (IOException e) {
            System.exit(2);
        } finally {
            if (bf != null) {
                bf.close();
            }
        }

        // Init crypto object with key
        final String[] key = keyString.split(",");

        validatePublicKeyInput(key);

        final Crypto crypto = new Crypto(key);
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
            U.p(e.getMessage());
        }
    }

    private static void decrypt(String[] args) throws Exception {
        // Read key file
        String filename = "";
        String keyString = "";
        for (String s : args) {
            if (s.equals("private.key")) {
                filename = s;
            }
        }

        // read in the private key
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(filename));
            keyString = bf.readLine();
        } catch (IOException e) {
            System.exit(2);
        } finally {
            if (bf != null) {
                bf.close();
            }
        }

        // Parse cmd line input
        final String[] keydata = keyString.split(";");
        final String[] key = keydata[2].split(",");
        validatePrivateKeyInput(keydata);

        // Init crypto class with private key (modulo, multiplier, super increasing sequence)
        final Crypto crypto = new Crypto(keydata[0], keydata[1], key);

        // read in input from standard in containing the cipher text
        Scanner scan = null;
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

    private static void keygen() throws Exception {
        Crypto crypto = new Crypto();
        crypto.keygen();
    }

    private static void validatePrivateKeyInput(String[] keyData) throws Exception {
        final MKMath math = new MKMath();

        if (keyData.length != 3) {
            throw new KeyDataViolationException("Error: Key data of private key does not contain three elements.");
        }
        
        if(Integer.parseInt(keyData[1]) > Integer.parseInt(keyData[0])) {
            throw new KeyDataViolationException("Error: The multiplier is bigger than the modulo.");
        }

        if (!math.isPrime(Long.parseLong(keyData[0]))) {
            throw new KeyDataViolationException("Error: Modulo is not prime.");
        }

        if (!math.isCoPrime(Integer.parseInt(keyData[1]), Integer.parseInt(keyData[0]))) {
            throw new KeyDataViolationException("Error: The multiplier and modulo is not coprime.");
        }

        String[] keystring = keyData[2].split(",");
        if (keystring.length != 16) {
            throw new KeyDataViolationException("Error: Private key does not contain 16 elements.");
        }

        long[] privateKey = new long[keystring.length];
        for (int i = 0; i < privateKey.length; ++i) {
            privateKey[i] = Long.parseLong(keystring[i]);
        }

        if (!math.isSuperIncreasing(privateKey)) {
            throw new Exception("Error: Private key is not superincreasing.");
        }

        for (int i = 0; i < privateKey.length; ++i) {
            if (privateKey[i] > Long.MAX_VALUE) {
                throw new KeyDataViolationException("Error: An element in the private key is larger than Long.MAX_VALUE.");
            }
        }
    }

    private static void validatePublicKeyInput(String[] key) throws KeyDataViolationException {
        // check if key is length 16, values smaller than max value of long data type
        if (key.length != 16) {
            throw new KeyDataViolationException("Error: Public key does not contain 16 elements.");
        }

        for (int i = 0; i < key.length; ++i) {
            if (Long.parseLong(key[i]) > Long.MAX_VALUE) {
                throw new KeyDataViolationException("Error: An element in the public key is larger than Long.MAX_VALUE.");
            }
        }
    }
}
