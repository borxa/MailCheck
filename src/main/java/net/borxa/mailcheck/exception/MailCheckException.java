/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.exception;

/**
 *
 * @author borxa.varela
 */
public class MailCheckException extends Exception {
    
    public MailCheckException(Exception e) {
        super(e);
    } 
    
    public MailCheckException(String message) {
        super(message);
    }
}
