
package merkel.hellman;

import java.math.BigInteger;
/**
 *
 * @author simon
 */
public class Crypto {
    
    // private key:
        // q: prime larger than sum of superincreasing sequence
        // r: multiplier which is in [1,q)
        // w: superincreasing sequence
    
    private int[] w = {2, 7, 11, 21, 42, 89, 180, 354};
    private int[] B = new int[w.length];
    private int q = 881;
    private int r = 588;
    
    public Crypto(){
    }
    
    public int[] encrypt(byte[] data){
        U.p("In encrypt()");
        // generate public key B (hard sequence)
        for (int i = 0; i < w.length; ++i){
            B[i] = w[i] * r % q;
        }
        U.p(B);
        
        // encrypt for every 128 bits and get a number
        U.p(U.toCharArr(data)); //test
        
        char[] chardata = U.toCharArr(data);
        int encryptedNum = 0;
        for (int i = 0; i < chardata.length; ++i){
            if (chardata[i] == '1'){
                encryptedNum += B[i];
            }
        }
        
        U.p(encryptedNum);
        
        // return the encrypted bytes
        int[] result = new int[1];
        result[0] = encryptedNum;
        return result;
    }
    
    public byte[] decrypt(int[] encrypted){
        U.p("In decrypt()");
        byte[] decrypted = new byte[1];
        
        // multiply encrypted number by modular inverse and mod by q
        BigInteger inverse = new BigInteger(String.valueOf(r)); 
        inverse = inverse.modInverse(new BigInteger(String.valueOf(q)));
        System.out.println(inverse);
 
        String res = "";
        
        int decryptedNum = encrypted[0] * inverse.intValue() % q;
        
        int temp = decryptedNum;
        for (int i = w.length - 1; i >= 0; --i) {  
            if(temp - w[i] >= 0) {
                temp = temp - w[i];
                res = "1" + res; 
            } 
            else {
                res = "0" + res;
            }
        }
        //System.out.println("Res: " + res);
        decrypted[0] = U.toByte(res);
        // decrypt result with w and return
        return decrypted;
    }
}
