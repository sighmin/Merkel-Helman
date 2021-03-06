package merkle.hellman;

import java.io.BufferedWriter;
import java.io.FileWriter;
import merkle.hellman.exceptions.MathViolationException;
import merkle.hellman.exceptions.ViolatedPreconditionException;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date 2013-05-21
 * @class Cryptographic implementation
 */
public class Crypto {

    private MKMath math = new MKMath();
    private final int BYTE_SIZE = 8;
    private final int keylength = 16;
    private final int blocksize = keylength / BYTE_SIZE;
    private long[] privateKey = new long[keylength];
    private long[] publicKey = new long[keylength];
    private int modulo;
    private int multiplier;
    private int modularInverse;

    // keygen
    public Crypto() {
    }

    // encrypt
    public Crypto(String[] key) {
        for (int i = 0; i < keylength; ++i) {
            this.publicKey[i] = Long.parseLong(key[i]);
        }
        U.p(this.publicKey);
    }

    // decrypt
    public Crypto(String modulostring, String multiplierString, String[] keystring) {
        this.modulo = Integer.parseInt(modulostring);
        this.multiplier = Integer.parseInt(multiplierString);
        for (int i = 0; i < keylength; ++i) {
            this.privateKey[i] = Long.parseLong(keystring[i]);
        }
        this.modularInverse = this.math.getMultiplicativeModularInverse(this.multiplier, this.modulo);
        U.p(this.modulo);
        U.p(this.multiplier);
        U.p(this.privateKey);
        U.p(this.math.calculateSumOf(privateKey));
    }

    public long encryptBlock(byte[] block) throws Exception {
        if (block.length == 0) {
            throw new ViolatedPreconditionException("Error: received empty block to encrypt.");
        }

        U.p("= encrypt block");
        U.p(U.toCharArr(block));

        char[] chardata = U.toCharArr(block);
        long encryptedNum = 0;

        // loop through chardata and multiply each bit by its corresponding number in publicKey[]
        for (int i = 0; i < chardata.length; ++i) {
            if (chardata[i] == '1') {
                encryptedNum += publicKey[i];
            }
        }
        U.p(encryptedNum);

        return encryptedNum;
    }

    public byte[] decrypt(long[] encrypted) throws Exception {
        if (encrypted.length == 0) {
            throw new ViolatedPreconditionException("Error: received empty array to decrypt.");
        }

        U.p("\nIn decrypt()\n===");
        byte[] decrypted = new byte[encrypted.length * blocksize];

        // multiply encrypted number by modular inverse and mod by modulo
        for (int i = 0; i < encrypted.length; ++i) {
            final byte[] temp = decryptBlock(encrypted[i]);
            for (int j = 0; j < blocksize; ++j) {
                decrypted[(i * blocksize) + j] = temp[j];
            }
        }

        U.p("\n(in decrypt(...)) Decrypted: ");
        U.p(U.toCharArr(decrypted));

        // decrypt result with w and return
        return decrypted;
    }

    private byte[] decryptBlock(long block) throws Exception {
        U.p("= decrypt block");
        U.p(block);

        String res = "";
        final long decryptedNum = block * modularInverse % modulo;
        byte[] decryptedBlock = new byte[blocksize];

        // build binary string representation of decrypted block
        long tempDecryptedNum = decryptedNum;
        for (int i = privateKey.length - 1; i >= 0; --i) {
            if (tempDecryptedNum - privateKey[i] >= 0) {
                tempDecryptedNum = tempDecryptedNum - privateKey[i];
                res = "1" + res;
            } else {
                res = "0" + res;
            }
        }

        // for each block in the binary String, convert into a byte
        for (int i = 0, j = 0; i < res.length(); i += 8, ++j) {
            final String tempString = res.substring(i, i + 8);
            decryptedBlock[j] = U.toByte(tempString);
        }

        U.p(U.toCharArr(decryptedBlock));

        return decryptedBlock;
    }

    public void keygen() throws Exception {
        // gen superincreasing sequence of private key
        if (!this.math.isSuperIncreasing(this.privateKey = this.math.createSuperincreasing())) {
            throw new MathViolationException("Error: Key generated is not superincreasing.");
        }
        U.p("Is superincreasing: " + this.math.isSuperIncreasing(this.privateKey));
        U.p(privateKey);

        // find modulo and multiplier
        final long sum = this.math.calculateSumOf(this.privateKey);
        if (!this.math.isPrime(this.modulo = this.math.findNextPrimeFrom(sum))) {
            throw new MathViolationException("Error: modulo is not prime.");
        }
        this.multiplier = this.math.getRandomCoPrime(this.modulo);
        U.p("mod: " + modulo + " mul: " + multiplier + "\n" + Integer.MAX_VALUE);

        // generate public key
        for (int i = 0; i < keylength; ++i) {
            publicKey[i] = privateKey[i] * multiplier % modulo;
        }

        U.p(publicKey);

        // write keys to files
        writeKeysToFile();
    }

    private void writeKeysToFile() throws Exception {
        // write private key to file        
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("private.key"));
            writer.write(Integer.toString(this.modulo) + ";");
            writer.write(Integer.toString(this.multiplier) + ";");
            for (int i = 0; i < keylength; ++i) {
                writer.write(Long.toString(privateKey[i]));
                if (i != keylength - 1) {
                    writer.write(",");
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        // write public key to file
        try {
            writer = new BufferedWriter(new FileWriter("public.key"));
            for (int i = 0; i < keylength; ++i) {
                writer.write(Long.toString(publicKey[i]));
                if (i != keylength - 1) {
                    writer.write(",");
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }   
}
