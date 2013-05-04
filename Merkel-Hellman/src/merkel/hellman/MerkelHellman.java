
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
        
        //U.p(U.createSuperincreasing());
        boolean pass = crypto.test();
        
        U.p("\n\n\n======================");
        if (pass) {
            U.p(":) P A S S E D!");
        } else {
            U.p(":( F A I L E D!");
        }
    }
}
