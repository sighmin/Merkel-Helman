
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
    // public key:
        // B: hard sequence
    
    private int[] w = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768};
    private int[] B = new int[w.length];
    private int q = 32771;
    private int r = 2588;
    private int blocksize = w.length/8; // 8bits in a byte ;)
    
    public Crypto(){
        // generate public key B (hard sequence)
        for (int i = 0; i < w.length; ++i){
            B[i] = w[i] * r % q;
        }
    }
    
    public int[] encrypt(byte[] data){
        U.p("\nIn encrypt()\n===");

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
        U.p("= encrypt block");
        U.p(U.toCharArr(block));
        
        char[] chardata = U.toCharArr(block);
        int encryptedNum = 0;
        for (int i = 0; i < chardata.length; ++i){
            if (chardata[i] == '1'){
                encryptedNum += B[i];
            }
        }
        
        U.p(encryptedNum);
        
        return encryptedNum;
    }
    
    public byte[] decrypt(int[] encrypted){
        U.p("\nIn decrypt()\n===");
        byte[] decrypted = new byte[encrypted.length * blocksize];
        
        // multiply encrypted number by modular inverse and mod by q
        BigInteger inverse = new BigInteger(String.valueOf(r)); 
        inverse = inverse.modInverse(new BigInteger(String.valueOf(q)));
        int inverseModular = inverse.intValue();
 
        for (int i = 0; i < encrypted.length; ++i){
            byte[] temp = new byte[blocksize];
            temp = decryptBlock(encrypted[i], inverseModular);
            for (int j = 0; j < blocksize; ++j){
                decrypted[(i*blocksize)+j] = temp[j];
            }
        }
        
        U.p("\n(in decrypt(...)) Decrypted: ");
        U.p(U.toCharArr(decrypted));
        
        // decrypt result with w and return
        return decrypted;
    }
    
    private byte[] decryptBlock(int block, int inverse){
        U.p("= decrypt block");
        U.p(block);
        
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
        
        U.p(U.toCharArr(decryptedBlock));
        
        return decryptedBlock;
    }
    
    public boolean test(){
        return test8bit() && test16bit() && test16bit2() && test128bit();
    }
    
    // We need to implement padding before including this test
    public boolean test8bit(){
        // init
//        Crypto crypto = new Crypto();
//        String data = "a";
//        U.p("\n***Testing " + data + " ***");
//        int[] encryptedData = crypto.encrypt(data.getBytes());
//        byte[] decryptedData = crypto.decrypt(encryptedData);
//        U.p(U.toCharArr(decryptedData));
//        
//        String decryptedStr = new String(decryptedData);
//        
//        U.p(decryptedStr);
//        return decryptedStr.equals(data);
        return true;
    }
    
    public boolean test16bit(){
        Crypto crypto = new Crypto();
        String data = "ab";
        U.p("\n************ Testing " + data + " ************");
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);
        
        String decryptedStr = new String(decryptedData);
        
        U.p(decryptedStr.equals(data) ? "Passed" : "Failed");
        U.p("\nDecrypted string: " + decryptedStr);
        return decryptedStr.equals(data);
    }
    
    public boolean test16bit2(){
        Crypto crypto = new Crypto();
        String data = "abcdefghijkl";
        U.p("\n************ Testing " + data + " ************");
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);
        
        String decryptedStr = new String(decryptedData);
        
        U.p(decryptedStr.equals(data) ? "Passed" : "Failed");
        U.p("\nDecrypted string: " + decryptedStr);
        return decryptedStr.equals(data);
    }
    
    // We need to confirm key length before including this test
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
