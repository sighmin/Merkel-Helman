package merkle.hellman;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date 2013-05-21
 */
public class Crypto {

    private MKMath math;
    private final int keylength = 16;
    private long[] privateKey = new long[keylength];
    private long[] publicKey = new long[keylength];
    private int modulo;
    private int multiplier;
    private int modularInverse;
    private int blocksize = keylength / 8;

    public Crypto() {
        this.math = new MKMath();
    }

    // encrypt
    public Crypto(String[] key) {
        this.math = new MKMath();
        for (int i = 0; i < keylength; ++i) {
            this.publicKey[i] = Integer.parseInt(key[i]);
        }
        U.p(this.publicKey);
    }

    // decrypt
    public Crypto(String modulostring, String multiplierString, String[] keystring) {
        this.math = new MKMath();
        this.modulo = Integer.parseInt(modulostring);
        this.multiplier = Integer.parseInt(multiplierString);
        for (int i = 0; i < keylength; ++i) {
            this.privateKey[i] = Integer.parseInt(keystring[i]);
        }
        this.modularInverse = this.math.getModInverse(this.multiplier, this.modulo);
        U.p(this.modulo);
        U.p(this.multiplier);
        U.p(this.privateKey);
        U.p(this.math.calculateSumOf(privateKey));
    }

    public long encryptBlock(byte[] block) {
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

    public byte[] decrypt(long[] encrypted) {
        U.p("\nIn decrypt()\n===");
        byte[] decrypted = new byte[encrypted.length * blocksize];

        // multiply encrypted number by modular inverse and mod by q      
        for (int i = 0; i < encrypted.length; ++i) {
            byte[] temp = new byte[blocksize];
            temp = decryptBlock(encrypted[i]);
            for (int j = 0; j < blocksize; ++j) {
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

        // build binary string representation of decrypted block
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
        this.privateKey = this.math.createSuperincreasing();
        U.p("Is superincreasing: " + this.math.isSuperIncreasing(this.privateKey));
        U.p(privateKey);

        // find modulo
        long sum = math.calculateSumOf(this.privateKey);
        this.modulo = this.math.findNextPrimeFrom(sum);
        this.multiplier = this.math.getRandomCoPrime(this.modulo);
        U.p("mod: " + modulo + " mul: " + multiplier + "\n" + Integer.MAX_VALUE);

        for (int i = 0; i < keylength; ++i) {
            long temp = privateKey[i] * multiplier;
//            if (temp >= Integer.MAX_VALUE) { --> not necessary because we're using long
//                U.p("Problem: " + temp);
//            }
            publicKey[i] = temp % modulo;
        }

        U.p(publicKey);

        // write private key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("private.key"));
            pf.write(Integer.toString(this.modulo) + ";");
            pf.write(Integer.toString(this.multiplier) + ";");
            for (int i = 0; i < keylength; ++i) {
                pf.write(Long.toString(privateKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (IOException e) {
            System.exit(2);
        }

        // write public key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("public.key"));
            for (int i = 0; i < keylength; ++i) {
                pf.write(Long.toString(publicKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (IOException e) {
            System.exit(2);
        }
    }

    /**
     * @clas MKMath private class for mathematical cryptographic functions for the program
     */
    private class MKMath {

        public long[] createSuperincreasing() {
            int length = 16;
            long[] seq = new long[length];
            SecureRandom sr = new SecureRandom();

            for (int i = 0; i < length; ++i) {
                long temp = 1;
                for (int j = 0; j < i; ++j) {
                    temp += seq[j];
                }
                seq[i] = temp + sr.nextInt(3);
            }
            return seq;
        }

        public boolean isSuperIncreasing(long[] seq) {
            int sum = 0;
            boolean test = true;
            for (int i = 0; i < seq.length; ++i) {
                if (seq[i] <= sum) {
                    test = false;
                    break;
                }
                sum += seq[i];
            }
            return test;
        }

        public long calculateSumOf(long[] key) {
            int val = 0;
            for (int i = 0; i < key.length; ++i) {
                val += key[i];
            }
            return val;
        }

        public int getModInverse(int multiplier, int modulo) {
            BigInteger inverse = new BigInteger(String.valueOf(multiplier));
            inverse = inverse.modInverse(new BigInteger(String.valueOf(modulo)));
            return inverse.intValue();
        }

        public int findNextPrimeFrom(long sum) {
            long prime = 0;
            for (long i = sum; i < sum * 3; ++i) {
                if (isPrime(i)) {
                    prime = i;
                    break;
                }
            }
            return (int) prime;
        }
        
        public boolean isPrime(long num) {
            if (num == 2) {
                return true;
            }
            for (int i = 2; i <= (int) Math.sqrt(num) + 1; ++i) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }

        public int getRandomCoPrime(int modulo) {
            SecureRandom sr = new SecureRandom();
            int random = sr.nextInt(modulo / 2);
            int coprime = random + 1;
            return coprime;
        }

        public BigInteger[] createBigIntegerSuperincreasing() {
            int length = 128;
            BigInteger[] seq = new BigInteger[length];

            for (int i = 0; i < length; ++i) {
                BigInteger temp = new BigInteger("1");
                for (int j = 0; j < i; ++j) {
                    temp = temp.add(seq[j]);
                }
                seq[i] = temp;
            }
            return seq;
        }
    } // end class MKMath
}
