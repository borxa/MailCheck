/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.exception;

/**
 * <p>MailCheckException class.</p>
 *
 * @author borxa.varela
 * @version $Id: $Id
 */
public class MailCheckException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * <p>Constructor for MailCheckException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public MailCheckException(Exception e) {
        super(e);
    } 
    
    /**
     * <p>Constructor for MailCheckException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public MailCheckException(String message) {
        super(message);
    }
}
