
package merkel.hellman;

import java.math.BigInteger;

/**
 *
 * @author simon
 */
public class U {
    static boolean silent = false;
    
    static public void p(String s){
        if (!silent) System.out.println(s);
    }
    
    static public void p(int s){
        if (!silent) System.out.println(s);
    }
    
    static public void p(char[] s){
        if (!silent) System.out.println(s);
    }
    
    static public void p(int[] s){
        if (!silent){
            for (int i : s)
                System.out.print(i + ",");
            System.out.println("");
        }
    }
    
    static public void p(long[] s){
        if (!silent){
            for (long i : s)
                System.out.print(i + ",");
            System.out.println("");
        }
    }
    
    static public void p(BigInteger[] s){
        if (!silent){
            for (BigInteger i : s)
                System.out.println(i.toString());
            System.out.println("");
        }
    }
    
    static public void p(byte[] data){
        if (!silent){
            for (byte b : data){
                System.out.print(Integer.toBinaryString(b));
            }
        }
    }
    
    static public char[] toCharArr(byte[] data){
        char[] chars;
        
        String temp = "";
        String bytelength = "";
        for (byte b : data){
            bytelength = Integer.toBinaryString(b);
            int difference = 8 - bytelength.length();
        
            for (int i = 0; i < difference; ++i){
                bytelength = "0" + bytelength;
            }
            
            temp = temp + bytelength;
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
        String temp = new String(str);
        //U.p("Before: "+str);
        
        int difference = 8 - temp.length();
        
        for (int i = 0; i < difference; ++i){
            temp = "0" + temp;
        }
        byte b = (byte) Integer.parseInt(temp, 2);
        //U.p("After: "+b);
        return b;
    }
  
    public static int[] createSuperincreasing() {
        int length = 16;
        int[] seq = new int[length];
        
        for (int i = 0; i < length; ++i){
            int temp = 1;
            for (int j = 0; j < i; ++j){
                temp += seq[j];
            }
            seq[i] = temp;
        }
        
        return seq;
    }
    
    public static int calculate(int[] key) {
        int val = 0;
        for(int i = 0; i < key.length; ++i) {
            val += key[i];
        }
        return val;
    }
        
//    public static BigInteger[] createSuperincreasing() {
//        int length = 128;
//        BigInteger[] seq = new BigInteger[length];
//        
//        for (int i = 0; i < length; ++i){
//            BigInteger temp = new BigInteger("1");
//            for (int j = 0; j < i; ++j){
//                //temp += seq[j];
//                temp = temp.add(seq[j]);
//            }
//            seq[i] = temp;
//        }
//        
//        return seq;
//    }
}
