
package merkel.hellman;

/**
 *
 * @author simon
 */
public class Crypto {
    
    // private key:
        // q: prime larger than sum of superincreasing sequence
        // r: multiplier which is in [1,q)
        // w: superincreasing sequence
    
    int[] w = {2, 7, 11, 21, 42, 89, 180, 354};
    int[] B = new int[w.length];
    int q = 881;
    int r = 588;
    
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
        int inverse;
        
        
        // decrypt result with w and return
        return decrypted;
    }
}
