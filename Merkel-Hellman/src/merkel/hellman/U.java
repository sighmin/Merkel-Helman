
package merkel.hellman;

import java.math.BigInteger;

/**
 *
 * @author simon
 */
public class U {
    static public void p(String s){
        System.out.println(s);
    }
    
    static public void p(int s){
        System.out.println(s);
    }
    
    static public void p(char[] s){
        System.out.println(s);
    }
    
    static public void p(int[] s){
        for (int i : s)
            System.out.print(i + ",");
        System.out.println("");
    }
    
    static public void p(long[] s){
        for (long i : s)
            System.out.print(i + ",");
        System.out.println("");
    }
    
    static public void p(BigInteger[] s){
        for (BigInteger i : s)
            System.out.println(i.toString());
        System.out.println("");
    }
    
    static public void p(byte[] data){
        for (byte b : data){
            System.out.print(Integer.toBinaryString(b));
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
   
    
    public static byte toByte(String str) {
        byte b;
        
        int difference = 8 - str.length();
        
        for (int i = 0; i < difference; ++i){
            str = "0" + str;
        }
        
        return (byte) Integer.parseInt(str, 2);
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
  
    public static int[] createSuperincreasing() {
        int length = 16;
        int[] seq = new int[length];
        
        for (int i = 0; i < length; ++i){
            int temp = 1;
            for (int j = 0; j < i; ++j){
                temp += seq[j];
                //temp = temp.add(seq[j]);
            }
            seq[i] = temp;
        }
        
        return seq;
    }
}
