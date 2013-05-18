package merkle.hellman;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 */
public class U {

    static boolean silent = true;

    static public void p(String s) {
        if (!silent) {
            System.out.println(s);
        }
    }

    static public void p(int s) {
        if (!silent) {
            System.out.println(s);
        }
    }
    
    static public void p(long s) {
        if (!silent) {
            System.out.println(s);
        }
    }

    static public void p(char[] s) {
        if (!silent) {
            System.out.println(s);
        }
    }

    static public void p(int[] s) {
        if (!silent) {
            for (int i : s) {
                System.out.print(i + ",");
            }
            System.out.println("");
        }
    }

    static public void p(long[] s) {
        if (!silent) {
            for (long i : s) {
                System.out.print(i + ",");
            }
            System.out.println("");
        }
    }

    static public void p(BigInteger[] s) {
        if (!silent) {
            for (BigInteger i : s) {
                System.out.println(i.toString());
            }
            System.out.println("");
        }
    }

    static public void p(byte[] data) {
        if (!silent) {
            for (byte b : data) {
                System.out.print(Integer.toBinaryString(b));
            }
        }
    }

    static public char[] toCharArr(byte[] data) {
        char[] chars;
        String temp = "";
        String binaryValue = "";

        for (byte b : data) {
            //byte value is signed so AND with 0xff to get unsigned
            int uns = b & 0xFF;
            binaryValue = Integer.toBinaryString(uns);

            int difference = 8 - binaryValue.length();

            for (int i = 0; i < difference; ++i) {
                binaryValue = "0" + binaryValue;
            }
            temp = temp + binaryValue;
        }

        chars = temp.toCharArray();
        return chars;
    }

    public static int getModInverse(int multiplier, int prime) {
        BigInteger inverse = new BigInteger(String.valueOf(multiplier));
        inverse = inverse.modInverse(new BigInteger(String.valueOf(prime)));
        return inverse.intValue();
    }

    public static byte toByte(String str) {
        String temp = str;
        U.p("Before: "+str);

        int difference = 8 - temp.length();

        for (int i = 0; i < difference; ++i) {
            temp = "0" + temp;
        }
        byte b = (byte) Integer.parseInt(temp, 2);
        U.p("After: "+b);
        U.p(Integer.toBinaryString(b));
        return b;
    }

    public static long[] createSuperincreasing() {
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
    
    public static boolean isSuperIncreasing(long[] seq) {
        int sum = 0;
        boolean test = true;
        for (int i = 0; i < seq.length; ++i) {
            if (seq[i] <= sum){
                test = false;
                break;
            }
            sum += seq[i];
        }
        return test;
    }
    
    public static int findNextPrimeFrom(long sum) {
        long prime = 0;
        for (long i = sum; i < sum*3; ++i){
            if (isPrime(i)){
                prime = i;
                break;
            }
        }
        return (int)prime;
    }
    
    public static boolean isPrime(long num){
        if (num == 2) {
          return true;
        }
        for (int i = 2; i <= (int)Math.sqrt(num) + 1; ++i){
          if (num % i == 0){
            return false;
          }
        }
        return true;
    }
    
    public static int getRandomCoPrime(int modulo) {
        SecureRandom sr = new SecureRandom();
        int random = sr.nextInt(modulo/2);
        int coprime = random + 1;
        return coprime;
    }

    public static long calculateSumOf(long[] key) {
        int val = 0;
        for (int i = 0; i < key.length; ++i) {
            val += key[i];
        }
        return val;
    }
    
    public static BigInteger[] createBigIntegerSuperincreasing() {
        int length = 128;
        BigInteger[] seq = new BigInteger[length];
        
        for (int i = 0; i < length; ++i){
            BigInteger temp = new BigInteger("1");
            for (int j = 0; j < i; ++j){
                //temp += seq[j];
                temp = temp.add(seq[j]);
            }
            seq[i] = temp;
        }
        return seq;
    }
}
