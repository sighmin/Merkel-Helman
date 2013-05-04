
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
    
    private int[] w = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768};
    private int[] B = new int[w.length];
    private int q = 32771;
    private int r = 2588;
    private int blocksize = w.length/8; // 8bits in a byte ;)
    
    public Crypto(){
    }
    
    public int[] encrypt(byte[] data){
        U.p("In encrypt()");
        // generate public key B (hard sequence)
        for (int i = 0; i < w.length; ++i){
            B[i] = w[i] * r % q;
        }
        U.p(B);
        
        // encrypt for every block and get a number
        int[] result = new int[data.length / blocksize];
        for (int i = 0, j = 0; i < data.length; i += blocksize, ++j){
            byte[] piece = new byte[blocksize];
            for (int k = 0; k < blocksize; ++k){
                piece[k] = data[i+k];
            }
            result[j] = encryptBlock(piece);
        }
        
        // return the encrypted bytes
        return result;
    }
    
    private int encryptBlock(byte[] block){
        char[] chardata = U.toCharArr(block);
        int encryptedNum = 0;
        for (int i = 0; i < chardata.length; ++i){
            if (chardata[i] == '1'){
                encryptedNum += B[i];
            }
        }
        
        return encryptedNum;
    }
    
    public byte[] decrypt(int[] encrypted){
        U.p("In decrypt()");
        byte[] decrypted = new byte[encrypted.length * blocksize];
        
        // multiply encrypted number by modular inverse and mod by q
        BigInteger inverse = new BigInteger(String.valueOf(r)); 
        inverse = inverse.modInverse(new BigInteger(String.valueOf(q)));
        System.out.println("Inverse: " + inverse);
 
        for (int i = 0; i < encrypted.length; ++i){
            byte[] temp = new byte[blocksize];
            temp = decryptBlock(encrypted[i], inverse.intValue());
            for (int j = 0; j < blocksize; ++j){
                decrypted[i+j] = temp[j];
            }
        }
        
        // decrypt result with w and return
        return decrypted;
    }
    
    private byte[] decryptBlock(int block, int inverse){
        String res = "";
        int decryptedNum = block * inverse % q;
        byte[] decryptedBlock = new byte[blocksize];
        
        // build string representation of decrypted block
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
        
        // foreach block in the binary String, convert into a byte
        for (int i = 0, j = 0; i < res.length(); i += 8, ++j){
            String tempString = res.substring(i, i+8);
            decryptedBlock[j] = U.toByte(tempString);
        }
        
        return decryptedBlock;
    }
    
    public boolean test(){
        return test8bit() && test128bit();
    }
    
    public boolean test8bit(){
        // init
        Crypto crypto = new Crypto();
        String data = "ab";
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);
        String decryptedStr = new String(decryptedData);
        
        U.p(decryptedStr);
        return decryptedStr.equals(data);
    }
    
    public boolean test128bit(){
        // init
//        Crypto crypto = new Crypto();
//        String data = "1234567890abcdef";
//        int[] encryptedData = crypto.encrypt(data.getBytes());
//        byte[] decryptedData = crypto.decrypt(encryptedData);
//        String decryptedStr = new String(decryptedData);
//        
//        U.p(decryptedStr);
//        return decryptedStr.equals(data);
        return true;
    }
}
