
package merkle.hellman.exceptions;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 * @class  Thrown when the control flow precondition is unsatisfied.
 */
public class ViolatedPreconditionException extends Exception {

    public ViolatedPreconditionException() { }

    public ViolatedPreconditionException(String msg) {
        super(msg);
    }
}
