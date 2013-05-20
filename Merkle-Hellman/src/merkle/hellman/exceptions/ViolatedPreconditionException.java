
package merkle.hellman.exceptions;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 * @class  Program entry point
 */
public class ViolatedPreconditionException extends Exception {

    public ViolatedPreconditionException() { }

    public ViolatedPreconditionException(String msg) {
        super(msg);
    }
}
