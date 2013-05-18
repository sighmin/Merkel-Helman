package merkle.hellman;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 */
public class Crypto {
    private final int keylength = 16;
    private long[] privateKey = new long[keylength];;
    private long[] publicKey = new long[keylength];
    private int modulo;
    private int multiplier;
    private int modularInverse;
    private int blocksize = keylength / 8;

    public Crypto(){ /* Exists for testing methods */ }
    
    // encrypt
    public Crypto(String keyString) {
        String[] key = keyString.split(",");
        for (int i = 0; i < keylength; ++i) {
            this.publicKey[i] = Integer.parseInt(key[i]);
        }
        U.p(this.publicKey);
    }
    
    // decrypt
    public Crypto(String modulostring, String multiplierString, String[] keystring) {
       this.modulo = Integer.parseInt(modulostring);
       this.multiplier = Integer.parseInt(multiplierString);
       for (int i = 0; i < keylength; ++i) {
           this.privateKey[i] = Integer.parseInt(keystring[i]);
       }
       this.modularInverse = U.getModInverse(this.multiplier, this.modulo);       
       U.p(this.modulo);
       U.p(this.multiplier);
       U.p(this.privateKey);
       U.p(U.calculateSumOf(privateKey));
    }

    public long encryptBlock(byte[] block) {
        U.p("= encrypt block");
        U.p(U.toCharArr(block));

        char[] chardata = U.toCharArr(block);
        long encryptedNum = 0;
        
        //loop through chardata and multiply each bit by its corresponding number in B[]
        for (int i = 0; i < chardata.length; ++i) {
            if (chardata[i] == '1') {
                encryptedNum += publicKey[i];
            }
        }
        U.p(encryptedNum);

        return encryptedNum;
    }

    public byte[] decrypt(long[] encrypted) {
        U.p("\nIn decrypt()\n===");
        byte[] decrypted = new byte[encrypted.length * blocksize];

        // multiply encrypted number by modular inverse and mod by q      
        for (int i = 0; i < encrypted.length; ++i) {
            byte[] temp = new byte[blocksize];
            temp = decryptBlock(encrypted[i]);
            for (int j = 0; j < blocksize; ++j) {
                long v = (i * blocksize) + j;
                decrypted[(i * blocksize) + j] = temp[j];
            }
        }

        U.p("\n(in decrypt(...)) Decrypted: ");
        U.p(U.toCharArr(decrypted));

        // decrypt result with w and return
        return decrypted;
    }

    private byte[] decryptBlock(long block) {
        U.p("= decrypt block");
        U.p(block);

        String res = "";
        long decryptedNum = block * modularInverse % modulo;
        byte[] decryptedBlock = new byte[blocksize];

        // build string representation of decrypted block
        long temp = decryptedNum;
        for (int i = privateKey.length - 1; i >= 0; --i) {
            if (temp - privateKey[i] >= 0) {
                temp = temp - privateKey[i];
                res = "1" + res;
            } else {
                res = "0" + res;
            }
        }

        // for each block in the binary String, convert into a byte
        for (int i = 0, j = 0; i < res.length(); i += 8, ++j) {
            String tempString = res.substring(i, i + 8);
            decryptedBlock[j] = U.toByte(tempString);
        }

        U.p(U.toCharArr(decryptedBlock));

        return decryptedBlock;
    }
    
    public void keygen() {
        // gen private key
        this.privateKey = U.createSuperincreasing();
        U.p("Is superincreasing: " + U.isSuperIncreasing(this.privateKey));
        U.p(privateKey);
        
        // find modulo
        long sum = 0;
        for (long i : privateKey) { sum += i; }
        this.modulo = U.findNextPrimeFrom(sum);
        this.multiplier = U.getRandomCoPrime(this.modulo);
        U.p("mod: "+modulo+" mul: "+multiplier+"\n"+Integer.MAX_VALUE);
        
        for (int i = 0; i < keylength; ++i) {
            long temp = privateKey[i] * multiplier;
            if (temp >= Integer.MAX_VALUE)
                U.p("Problem: " + temp);
            publicKey[i] = temp % modulo;
        }
        
        U.p(publicKey);
        
        // write private key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("private.key"));
            pf.write(Integer.toString(this.modulo) + ";");
            pf.write(Integer.toString(this.multiplier) + ";");
            for (int i = 0; i < keylength; ++i){
                pf.write(Long.toString(privateKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (IOException e){
            System.exit(2);
        }
        // write public key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("public.key"));
            for (int i = 0; i < keylength; ++i){
                pf.write(Long.toString(publicKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (IOException e){
            System.exit(2);
        }
    }
}
