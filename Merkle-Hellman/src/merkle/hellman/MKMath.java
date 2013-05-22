package merkle.hellman;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author Simon van Dyk & Deon Taljaard
 * @date 2013-05-21
 * @class MKMath class for mathematical cryptographic functions for the program
 */
public class MKMath {

    public long[] createSuperincreasing() {
        final int length = 16;
        long[] seq = new long[length];
        final int randomResolution = 3;
        final SecureRandom sr = new SecureRandom();

        for (int i = 0; i < length; ++i) {
            long temp = 1;
            for (int j = 0; j < i; ++j) {
                temp += seq[j];
            }
            seq[i] = temp + sr.nextInt(randomResolution);
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
        int sum = 0;
        for (int i = 0; i < key.length; ++i) {
            sum += key[i];
        }
        return sum;
    }

    // use final because methods is called from Crypto constructor
    public final int getMultiplicativeModularInverse(int multiplier, int modulo) {
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
        final SecureRandom sr = new SecureRandom();
        final int random = sr.nextInt(modulo / 2);
        final int coprime = random + 1;
        return coprime;
    }

    public boolean isCoPrime(int muliplier, int modulo) {
        if(gcd(muliplier, modulo) == 1) {
            return true;
        }
        return false;
    }

    public static int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }
} // end class MKMath
