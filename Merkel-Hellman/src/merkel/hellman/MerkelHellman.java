
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
        
        boolean pass = crypto.test();
        
        U.p("======================");
        if (pass) {
            U.p(":) PASSED!");
        } else {
            U.p(":( FAILED!");
        }
    }
}
