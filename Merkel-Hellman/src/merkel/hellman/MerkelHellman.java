
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
        
        // Parse commandline args & dispatch function
        if (args.length < 1){
            help();
        } else {
            // Join args in a string
            String arguments = "";
            for (String s : args){
                arguments += s + " ";
            }
            
            // Dispatch function
            if (arguments.contains("encrypt")){
                encrypt();
            } else if (arguments.contains("decrypt")){
                decrypt();
            } else if (arguments.contains("help")){
                help();
            } else if (arguments.contains("test")){
                test();
            }
        }
    }
    
    public static void test(){
        // init
        Crypto crypto = new Crypto();
        
        // test
        boolean pass = crypto.test();

        U.p("\n\n\n======================");
        if (pass) {
            U.p(":) P A S S E D!");
        } else {
            U.p(":( F A I L E D!");
        }
    }
    
    public static void help(){
        System.out.println("Merkel-Hellman Usage:\n"
                + "help\n"
                + "  java -jar Merkel-Hellman.jar help\n"
                + "encrypt\n"
                + "  java -jar Merkel-Hellman.jar encrypt < plaintext\n"
                + "decrypt\n"
                + "  java -jar Merkel-Hellman.jar decrypt < ciphertext");
    }
    
    // java -jar Merkel-Hellman.jar encrypt < plaintext
    public static void encrypt(){
        
    }
    
    // java -jar Merkel-Hellman.jar decrypt < ciphertext
    public static void decrypt(){
        
    }
}
