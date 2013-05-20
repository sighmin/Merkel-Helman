package merkle.hellman;

import java.math.BigInteger;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date 2013-05-21
 * @class Utility class for the program
 */
public class U {

    private static boolean debug = false;

    public static void p(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void p(int s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void p(long s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void p(char[] s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void p(int[] s) {
        if (debug) {
            for (int i : s) {
                System.out.print(i + ",");
            }
            System.out.println("");
        }
    }

    public static void p(long[] s) {
        if (debug) {
            for (long i : s) {
                System.out.print(i + ",");
            }
            System.out.println("");
        }
    }

    public static void p(BigInteger[] s) {
        if (debug) {
            for (BigInteger i : s) {
                System.out.println(i.toString());
            }
            System.out.println("");
        }
    }

    public static void p(byte[] data) {
        if (debug) {
            for (byte b : data) {
                System.out.print(Integer.toBinaryString(b));
            }
        }
    }

    public static char[] toCharArr(byte[] data) {
        char[] chars;
        String temp = "";
        String binaryValue = "";

        for (byte b : data) {
            // byte value is signed so AND with 0xff to get unsigned
            int uns = b & 0xFF;
            binaryValue = Integer.toBinaryString(uns);

            // if binaryValue is not 8 bits, pad MSB with 0's
            int difference = 8 - binaryValue.length();
            for (int i = 0; i < difference; ++i) {
                binaryValue = "0" + binaryValue;
            }
            temp = temp + binaryValue;
        }

        chars = temp.toCharArray();
        return chars;
    }

    public static byte toByte(String str) {
        String temp = str;
        U.p("Before: " + str);

        // if binaryValue is not 8 bits, pad MSB with 0's
        int difference = 8 - temp.length();
        for (int i = 0; i < difference; ++i) {
            temp = "0" + temp;
        }
        
        byte b = (byte) Integer.parseInt(temp, 2);
        
        U.p("After: " + b);
        U.p(Integer.toBinaryString(b));
        
        return b;
    }
}
