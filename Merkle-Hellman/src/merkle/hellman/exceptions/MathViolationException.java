
package merkle.hellman.exceptions;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 * @class  Mathematical properties have been lost.
 */
public class MathViolationException extends Exception {

    public MathViolationException() { }

    public MathViolationException(String msg) {
        super(msg);
    }
}
