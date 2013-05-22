package merkle.hellman.exceptions;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 * @class  Key Data Violation Exception
 */
public class KeyDataViolationException extends ParseException {

    public KeyDataViolationException() { super(); }

    public KeyDataViolationException(String msg) {
        super(msg);
    }
}
