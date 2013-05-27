
package merkle.hellman.exceptions;

/**
 * @author Simon van Dyk & Deon Taljaard
 * @date   2013-05-21
 * @class  Violation of data format during parsing.
 */
public class ParseException extends Exception {

    public ParseException() { }

    public ParseException(String msg) {
        super(msg);
    }
}
