
package merkel.hellman;

/**
 *
 * @author simon
 */
public class MerkelHellman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // init
        Crypto crypto = new Crypto();
        
        // Encrypt
        int[] encryptedData = crypto.encrypt("a".getBytes());
        
        // Decrypt
        byte[] decryptedData = crypto.decrypt(encryptedData);
        
        String decryptedStr = new String(decryptedData);
        U.p(decryptedStr);
    }
}
